package com.agsft.customer.Care.controller;

import com.agsft.customer.Care.config.MessageConfiguration;
import com.agsft.customer.Care.dto.request.LoginRequestDTO;
import com.agsft.customer.Care.dto.request.PaginationRequestUserDTO;
import com.agsft.customer.Care.dto.request.UserRequestDTO;
import com.agsft.customer.Care.dto.response.LoginResponseDTO;
import com.agsft.customer.Care.dto.response.PaginationResponseUserDTO;
import com.agsft.customer.Care.dto.response.ResponseDTO;
import com.agsft.customer.Care.dto.response.UploadeFileResponseDTO;
import com.agsft.customer.Care.enums.HttpErrorCode;
import com.agsft.customer.Care.enums.HttpSuccessCodes;
import com.agsft.customer.Care.exception.CustomerException;
import com.agsft.customer.Care.model.User;
import com.agsft.customer.Care.service.UserService;
import com.agsft.customer.Care.util.UserUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RestController
@RequestMapping(value = {"/user"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageConfiguration messageSource;
    @Autowired
    UserUtil userUtil;

    @PostMapping(path = {"/registration"}, consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserRequestDTO userDTO) {

        userService.registerUser(userDTO);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.USER_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.USER_SUCCESS.getReasonPhrase(), null, null), null));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        LoginResponseDTO loginResponseDTO = userService.login(loginRequestDTO);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.LOGIN_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.LOGIN_SUCCESS.getReasonPhrase(), null, null), loginResponseDTO));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {
        userService.userLogout(httpServletRequest);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.LOGOUT_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.LOGOUT_SUCCESS.getReasonPhrase(), null, null), null));
    }


    @RequestMapping(method = RequestMethod.POST, path = "/uploadFile")
    public ResponseEntity<?> uploadFileLocally(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException, InterruptedException {
        User loggedInUser = userUtil.getLoginUserFromToken(request).orElseThrow(() -> new CustomerException(HttpErrorCode.USER_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.USER_NOT_FOUND.getMessage(), null, null)));
        UploadeFileResponseDTO uploadFileResponse = userService.uploadedFile(loggedInUser, file);
       // return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.FILE_UPLOADED_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.FILE_UPLOADED_SUCCESS.getReasonPhrase(), null, null), uploadFileResponse));
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.FILE_UPLOADED_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.FILE_UPLOADED_SUCCESS.getReasonPhrase(), null, null), null));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/getUserUploadedFiles", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllUserUploadedFile(@RequestBody PaginationRequestUserDTO requestDTO, HttpServletRequest request) {
        User loggedInUser = userUtil.getLoginUserFromToken(request).orElseThrow(() -> new CustomerException(HttpErrorCode.USER_NOT_FOUND.getCode(), messageSource.messageSource().getMessage(HttpErrorCode.USER_NOT_FOUND.getMessage(), null, null)));
        PaginationResponseUserDTO responseDTO = userService.getAllUserUploadedDetails(loggedInUser, requestDTO);
        ResponseDTO response = new ResponseDTO();
        if (responseDTO.getNumberOfElements().equals(0)) {
            response.setCode(HttpSuccessCodes.OK.getValue());
            response.setMessage(messageSource.messageSource().getMessage(HttpErrorCode.FILE_EMPTY.getMessage(), null, null));
        } else {
            response.setCode(HttpSuccessCodes.OK.getValue());
            response.setMessage(messageSource.messageSource().getMessage(HttpSuccessCodes.LIST_FETCHED.getReasonPhrase(), null, null));
            response.setBody(requestDTO);
        }
        return ResponseEntity.ok(responseDTO);
    }

    //encrypted pdf
    @RequestMapping(method = RequestMethod.POST, path = "/pdfProtected")
    public ResponseEntity<?> encryptedPdf(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException{
        userService.protectedPdf(file);
        return ResponseEntity.ok(new ResponseDTO(HttpSuccessCodes.FILE_UPLOADED_SUCCESS.getValue(), messageSource.messageSource().getMessage(HttpSuccessCodes.FILE_UPLOADED_SUCCESS.getReasonPhrase(), null, null), null));
    }

}
