package com.agsft.customer.Care.enums;

public enum RoleContants {
   SUPER_ADMIN("Super Admin"),STAFF_ADMIN("Staff Admin");
    String value;

    RoleContants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
