package com.agsft.customer.Care.dto.response;

import com.agsft.customer.Care.model.Company;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponseDTO {
    String id;
    String username;
    String firstName;
    String lastName;
    String email;
    List<String> roles;
}
