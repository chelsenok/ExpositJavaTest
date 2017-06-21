import filemanager.AccessRight;
import filemanager.FileAccessor;
import parser.ArgumentManager;

public final class Main {

    private static final String APP_NAME = "downloadmanager";

    public static void main(String... args) {
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        args = new String[]{
                "-f",
                "file.csv"
        };
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        final ArgumentManager argumentManager = new ArgumentManager(args);
        final ArgumentsStatusMessage message = getArgumentsStatusMessage(argumentManager);
        if (message != (ArgumentsStatusMessage.SUCCESS)) {
            exit(message.getMessage(APP_NAME));
        }
    }

    private static ArgumentsStatusMessage getArgumentsStatusMessage(final ArgumentManager manager) {
        if (manager.isValid && manager.needHelp) {
            return ArgumentsStatusMessage.HELP;
        }
        if (!manager.isValid || (manager.reference == null && manager.filePath == null)) {
            return ArgumentsStatusMessage.SYNTAX_ERROR;
        }
        if (manager.reference != null && manager.filePath != null) {
            return ArgumentsStatusMessage.LACK_OF_DATA;
        }
        if (manager.threads < 1) {
            return ArgumentsStatusMessage.WRONG_THREAD_NUMBER;
        }
        if (manager.downloadPath != null && FileAccessor.getAccessRight(manager.downloadPath) == AccessRight.DENIED) {
            return ArgumentsStatusMessage.ACCESS_DENIED;
        }
        return ArgumentsStatusMessage.SUCCESS;
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
