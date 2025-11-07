package com.agsft.customer.Care.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ResponseDTO {

	private Integer code;
	private String message;
	private Object Body;

}
