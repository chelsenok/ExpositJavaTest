package downloadmanager.parser;

enum OperationType {

    DOWNLOAD_PATH {
        @Override
        public String getType() {
            return "-p";
        }
    },
    FILE_PATH {
        @Override
        public String getType() {
            return "-f";
        }
    },
    HELP {
        @Override
        public String getType() {
            return "--help";
        }
    },
    THREADS {
        @Override
        public String getType() {
            return "-t";
        }
    },
    REFERENCE {
        @Override
        public String getType() {
            return "-l";
        }
    };

    public abstract String getType();
}