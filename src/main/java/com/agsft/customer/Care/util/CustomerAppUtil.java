package com.agsft.customer.Care.util;

import com.agsft.customer.Care.constant.HttpStatusCodes;
import com.agsft.customer.Care.dto.response.ResponseDTO;
import org.springframework.stereotype.Component;

/**
 * @author Pranjal
 */
@Component
public class CustomerAppUtil {

    public ResponseDTO createResponseEntityDTO(HttpStatusCodes httpStatusCodes, String message, Object body) {
        return new ResponseDTO(httpStatusCodes.getValue(), message, body);
    }
}
