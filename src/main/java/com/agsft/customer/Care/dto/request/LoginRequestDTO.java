package com.agsft.customer.Care.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequestDTO {
    @NotEmpty(message = "{email.empty}")
    @Email(message = "{email.valid}")
    String email;
    @NotEmpty(message = "{password.not.null}")
    String password;
}
