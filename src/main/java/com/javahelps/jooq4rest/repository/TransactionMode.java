package com.javahelps.jooq4rest.repository;

public enum TransactionMode {
    NONE(false, false),
    WRITE_ONLY(false, true),
    READ_WRITE(true, true);

    private final boolean transactionalRead;
    private final boolean transactionalWrite;

    TransactionMode(boolean transactionalRead, boolean transactionalWrite) {
        this.transactionalRead = transactionalRead;
        this.transactionalWrite = transactionalWrite;
    }

    public boolean isTransactionalRead() {
        return transactionalRead;
    }

    public boolean isTransactionalWrite() {
        return transactionalWrite;
    }
}
