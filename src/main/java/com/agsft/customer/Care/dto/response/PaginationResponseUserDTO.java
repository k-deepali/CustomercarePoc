package com.agsft.customer.Care.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationResponseUserDTO {
    Integer currentPage;
    Integer numberOfElements;
    Integer totalPages;
    Object content;
}
