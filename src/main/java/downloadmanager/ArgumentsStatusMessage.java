package downloadmanager;

public enum ArgumentsStatusMessage {
    UNKNOWN_FILE_FORMAT {
        @Override
        public String getMessage(final String appName) {
            return "Unknown file format.\n" + TRY_HELP.getMessage(appName);
        }
    },
    LACK_OF_DATA {
        @Override
        public String getMessage(final String appName) {
            return "Lack of data for downloading.\n" + TRY_HELP.getMessage(appName);
        }
    },
    SYNTAX_ERROR {
        @Override
        public String getMessage(final String appName) {
            return appName + ": missing operand\n" + TRY_HELP.getMessage(appName);
        }
    },
    TRY_HELP {
        @Override
        public String getMessage(final String appName) {
            return "Try '" + appName + " --help' for more information.";
        }
    },
    HELP {
        @Override
        public String getMessage(final String appName) {
            return "Help information HERE!";
        }
    },
    SUCCESS {
        @Override
        public String getMessage(final String appName) {
            return "Success.";
        }
    },
    NON_EXISTENT_FILE {
        @Override
        public String getMessage(final String appName) {
            return "File does not exist.\n" + TRY_HELP.getMessage(appName);
        }
    },
    WRONG_THREAD_NUMBER {
        @Override
        public String getMessage(final String appName) {
            return "Thread number is wrong.\n" + TRY_HELP.getMessage(appName);
        }
    },
    ACCESS_DENIED {
        @Override
        public String getMessage(final String appName) {
            return "Access to this folder from " + appName + " is denied";
        }
    };

    public abstract String getMessage(final String appName);
}