package downloadmanager.progress;

enum TableField {
    PROGRESS {
        @Override
        String getName() {
            return "progress";
        }
    },
    IN_PROCESS {
        @Override
        String getName() {
            return "in process";
        }
    },
    DOWNLOADED {
        @Override
        String getName() {
            return "downloaded";
        }
    },
    CORRUPTED {
        @Override
        String getName() {
            return "corrupted";
        }
    },
    TOTAL {
        @Override
        String getName() {
            return "total";
        }
    }
    ;
    abstract String getName();
}
