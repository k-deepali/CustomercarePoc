package com.agsft.customer.Care.enums;

/**
 * @author pranjal
 */
public enum FileStatus {

    NEW("new"), PENDING("pending"), PARSED("parsed"),COMPLETED("completed");

    final String value;

    FileStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
