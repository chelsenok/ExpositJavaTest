package parser;

enum OperationType {

    SOURCE_FILE {
        @Override
        public String getType() {
            return "-i";
        }
    },
    SEARCH_STRING {
        @Override
        public String getType() {
            return "-q";
        }
    },
    HELP {
        @Override
        public String getType() {
            return "--help";
        }
    },
    WRITE_FILE {
        @Override
        public String getType() {
            return "-o";
        }
    };

    public abstract String getType();
}