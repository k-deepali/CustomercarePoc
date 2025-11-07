package com.agsft.customer.Care.enums;

public enum FileInputStatus {

    DND("true"), NOTDND("false"), NA("N/A");

    final String value;


    FileInputStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
