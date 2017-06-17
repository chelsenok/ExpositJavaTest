package downloadmanager;

public enum StatusMessages {
    UnknownFileFormat {
        @Override
        public String getMessage(final String appName) {
            return "Unknown file format.\n" + TryHelp.getMessage(appName);
        }
    },
    LackOfData {
        @Override
        public String getMessage(final String appName) {
            return "Lack of data for downloading.\n" + TryHelp.getMessage(appName);
        }
    },
    SyntaxError {
        @Override
        public String getMessage(final String appName) {
            return appName + ": missing operand\n" + TryHelp.getMessage(appName);
        }
    },
    TryHelp {
        @Override
        public String getMessage(final String appName) {
            return "Try '" + appName + " --help' for more information.";
        }
    },
    Help {
        @Override
        public String getMessage(final String appName) {
            return "Help information HERE!";
        }
    },
    Success {
        @Override
        public String getMessage(final String appName) {
            return "Success.";
        }
    },
    NonExistentFile {
        @Override
        public String getMessage(final String appName) {
            return "File does not exist.\n" + TryHelp.getMessage(appName);
        }
    };

    public abstract String getMessage(final String appName);
}