package com.agsft.customer.Care.dto.request;

import com.agsft.customer.Care.enums.FileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class PaginationRequestDTO {

    Integer pageNo;
    Integer pageSize;
    String sortBy;
    Sort.Direction sortDirection;
    SearchCriteriaDTO searchCriteriaDTO;

}
