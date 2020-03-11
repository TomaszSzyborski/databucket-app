package pl.databucket.web.database;

public enum SizeLimit {
    DESCRIPTION(100),
    CREATED_BY(50),
    UPDATED_BY(50),
    GROUP_NAME(50),
    CLASS_NAME(50),
    BUCKET_NAME(50),
    TAG_NAME(50),
    FILTER_NAME(50),
    TASK_NAME(50),
    EVENT_NAME(50),
    COLUMNS_NAME(50),
    VIEW_NAME(50);

    private final int number;

    SizeLimit(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
