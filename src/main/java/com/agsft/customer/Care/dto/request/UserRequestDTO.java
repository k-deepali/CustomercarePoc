package com.agsft.customer.Care.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class UserRequestDTO {
    @NotEmpty(message = "{first.name}")
    String firstName;
    @NotNull(message = "{last.name}")
    String lastName;
    @NotEmpty(message = "{email.empty}")
    @Email(message = "{email.valid}")
    String email;
    @NotEmpty(message = "{phone.empty}")
    String phoneNumber;
    @NotNull(message = "{username.empty}")
    String username;
    @NotNull(message = "{password.empty}")
    String password;
    @NotNull(message = "{companyId.empty}")
    String companyId;
}
