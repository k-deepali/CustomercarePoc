package com.agsft.customer.Care.dto.response;

import com.agsft.customer.Care.model.FileInput;
import com.agsft.customer.Care.model.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadeFileResponseDTO {
    String name;
   // String status;
    String path;
    Integer NumberOfRecords;
    BillFileResponseDto billFileResponseDto;
    //List<FileInputResponseDTO> fileInputs;
}
