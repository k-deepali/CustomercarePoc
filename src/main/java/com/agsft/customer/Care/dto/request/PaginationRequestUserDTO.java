package com.agsft.customer.Care.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.bytebuddy.TypeCache;
import org.springframework.data.domain.Sort;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationRequestUserDTO {
    Integer pageNo;
    Integer PageSize;
    String sortBy;
    Sort.Direction sortDirection;
    SearchCriteriaDTO searchCriteriaDTO;
}
