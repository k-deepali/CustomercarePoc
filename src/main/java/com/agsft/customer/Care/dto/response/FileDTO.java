package com.agsft.customer.Care.dto.response;

import lombok.Data;

import java.io.InputStream;

/**
 * @author Pranjal
 */
@Data
public class FileDTO {

    InputStream inputStream;

    String fileName;

}
