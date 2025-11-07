package com.agsft.customer.Care.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class PaginationResponseDTO {
    Integer currentPage;
    Integer numberOfElements;
    Integer totalPages;
    Object content;
}
