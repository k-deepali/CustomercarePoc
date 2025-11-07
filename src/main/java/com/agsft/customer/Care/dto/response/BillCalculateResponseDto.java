package com.agsft.customer.Care.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillCalculateResponseDto {
    String name;
    Integer noOfFiles;
    Float totalBill;
    List<BillFileResponseDto>  FileList;
}

