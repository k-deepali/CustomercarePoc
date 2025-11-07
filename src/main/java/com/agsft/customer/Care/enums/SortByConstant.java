package com.agsft.customer.Care.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public enum SortByConstant {
    FILE_NAME("name","name"),
    CREATED_AT("createdAt","created_at"),
    STATUS("status", "status");
    String name;
    String value;

}