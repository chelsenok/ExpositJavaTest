package downloader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

import downloader.utils.Base64;

class DirectDownloader extends HttpConnector {

    private int poolSize = 3;
    private int bufferSize = 2048;

    private Proxy proxy;

    private static final Logger logger = Logger.getLogger(DirectDownloader.class.getName());

    public DirectDownloader() {
    }

    public DirectDownloader(int poolSize) {
        this.poolSize = poolSize;
    }

    public DirectDownloader(Proxy proxy) {
        this.proxy = proxy;
    }

    public DirectDownloader(Proxy proxy, int poolSize) {
        this.poolSize = poolSize;
        this.proxy = proxy;
    }

    protected class DirectDownloadThread extends Thread {

        private static final String CD_FNAME = "fname=";
        private static final String CONTENT_DISPOSITION = "Content-Disposition";

        private boolean cancel = false;
        private boolean stop = false;

        private final DownloadTask task;

        public DirectDownloadThread(DownloadTask task) {
            this.task = task;
        }

        void download(DownloadTask dt) throws IOException, InterruptedException, KeyManagementException,
                NoSuchAlgorithmException {
            HttpURLConnection conn = (HttpURLConnection) getConnection(dt.getUrl(), proxy);

            if (dt.getAuthentication() != null) {
                Authentication auth = dt.getAuthentication();
                String authString = auth.getUsername() + ":" + auth.getPassword();

                conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes(authString.getBytes()));
            }

            conn.setReadTimeout(dt.getTimeout());
            conn.setDoOutput(true);
            conn.connect();

            int fsize = conn.getContentLength();
            String fname;

            String cd = conn.getHeaderField(CONTENT_DISPOSITION);

            if (cd != null) {
                fname = cd.substring(cd.indexOf(CD_FNAME) + 1, cd.length() - 1);
            } else {
                String url = dt.getUrl().toString();
                fname = url.substring(url.lastIndexOf('/') + 1);
            }

            InputStream is = conn.getInputStream();

            OutputStream os = dt.getOutputStream();
            List<DownloadListener> listeners = dt.getListeners();

            byte[] buff = new byte[bufferSize];
            int res;

            for (DownloadListener listener : listeners) {
                listener.onStart(fname, fsize);
            }

            int total = 0;
            while ((res = is.read(buff)) != -1) {
                os.write(buff, 0, res);
                total += res;
                for (DownloadListener listener : listeners) {
                    listener.onUpdate(res, total);
                }

                synchronized (dt) {
                    // cancel download
                    if (cancel || dt.isCancelled()) {
                        close(is, os);
                        for (DownloadListener listener : listeners) {
                            listener.onCancel();
                        }

                        throw new RuntimeException("Cancelled download");
                    }

                    // stop thread
                    if (stop) {
                        close(is, os);
                        for (DownloadListener listener : listeners) {
                            listener.onCancel();
                        }

                        throw new InterruptedException("Shutdown");
                    }

                    // pause thread
                    while (dt.isPaused()) {
                        try {
                            wait();
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            for (DownloadListener listener : listeners) {
                listener.onComplete();
            }

            close(is, os);
        }

        private void close(Closeable is, Closeable os) {
            try {
                is.close();
                os.close();
            } catch (IOException ignored) {
            }
        }

        @Override
        public void run() {
            try {
                download(task);
            } catch (InterruptedException | NoSuchAlgorithmException | IOException | KeyManagementException ignored) {
            }
        }

        public void cancel() {
            cancel = true;
        }

        public void shutdown() {
            stop = true;
        }

    }

    public void download(DownloadTask dt) {
        new DirectDownloadThread(dt).start();
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
