package downloadmanager.parser;

enum OperationTypes {

    DownloadPath {
        @Override
        public String getType() {
            return "-p";
        }
    },
    FilePath {
        @Override
        public String getType() {
            return "-f";
        }
    },
    Help {
        @Override
        public String getType() {
            return "--help";
        }
    },
    Threads {
        @Override
        public String getType() {
            return "-t";
        }
    },
    Reference {
        @Override
        public String getType() {
            return "-l";
        }
    };

    public abstract String getType();
}