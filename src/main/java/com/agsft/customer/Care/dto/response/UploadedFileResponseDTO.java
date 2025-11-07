package com.agsft.customer.Care.dto.response;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadedFileResponseDTO {
    Long id;
    String name;
    Integer failureCount;
    Integer successCount;
    String status;
    String path;
}
