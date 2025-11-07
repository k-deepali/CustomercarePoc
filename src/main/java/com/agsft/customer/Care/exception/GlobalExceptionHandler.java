package com.agsft.customer.Care.exception;


import com.agsft.customer.Care.config.MessageConfiguration;
import com.agsft.customer.Care.dto.response.ResponseDTO;
import com.agsft.customer.Care.enums.HttpErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * JSON structures.
 *
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    MessageConfiguration messageSource;
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<String> errorMessage=new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errorMessage.add(error.getDefaultMessage());
        });

        responseDTO.setCode(HttpErrorCode.BAD_REQUEST.getCode());
        responseDTO.setMessage(String.join(", ", errorMessage));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseDTO, header, HttpStatus.OK);

    }
    @ExceptionHandler(value = {IOException.class})
    protected ResponseEntity<?> handleGenericException(IOException e) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseDTO.setMessage(messageSource.messageSource().getMessage(HttpErrorCode.INTERNAL_SERVER_ERROR.getMessage(),null,null));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseDTO, header, HttpStatus.OK);
    }

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<Object> customerException(CustomerException customerException) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(customerException.getCode());
        responseDTO.setMessage(customerException.getMessage());

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseDTO,header, HttpStatus.OK);
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleConflict(Exception e) {
        e.printStackTrace();
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(HttpErrorCode.INTERNAL_SERVER_ERROR.getCode());
        responseDTO.setMessage(messageSource.messageSource().getMessage(HttpErrorCode.INTERNAL_SERVER_ERROR.getMessage(),null,null));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseDTO,header, HttpStatus.OK);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setCode(HttpStatus.FORBIDDEN.value());
        responseDTO.setMessage(messageSource.messageSource().getMessage(HttpErrorCode.ACCESS_DENID.getMessage(),null,null));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseDTO, header, HttpStatus.OK);
    }
    //@ExceptionHandler(MaxUploadSizeExceededException::class)
}
