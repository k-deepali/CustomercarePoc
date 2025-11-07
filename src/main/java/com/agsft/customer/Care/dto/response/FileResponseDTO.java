package com.agsft.customer.Care.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponseDTO {
    Long id;
    String name;
    String mainstatus;
    String path;
}
