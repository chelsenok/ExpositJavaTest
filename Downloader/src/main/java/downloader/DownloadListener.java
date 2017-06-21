package downloader;

interface DownloadListener {

	void onStart(String fname, int fsize);

	void onUpdate(int bytes, int totalDownloaded);

	void onComplete();

	void onCancel();
}
