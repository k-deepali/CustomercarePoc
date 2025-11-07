package com.agsft.customer.Care.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillFileResponseDto {
    Long id;
    String fileName;
    Integer totalElement;
    Integer dndCount;
    Integer notDndCount;
    Integer NACount;
    Float chargePerRecord;
    Float  totalCharges;
    String createdAt;
}
