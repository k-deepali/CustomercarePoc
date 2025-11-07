package com.agsft.customer.Care.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequestDTO {
    Boolean isSuperAdmin;
    Boolean isStaffAdmin;
}
