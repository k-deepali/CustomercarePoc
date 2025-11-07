package com.agsft.customer.Care.service;

import com.agsft.customer.Care.dto.request.LoginRequestDTO;
import com.agsft.customer.Care.dto.request.PaginationRequestUserDTO;
import com.agsft.customer.Care.dto.request.UserRequestDTO;
import com.agsft.customer.Care.dto.response.*;
import com.agsft.customer.Care.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface UserService {
    public User registerUser(UserRequestDTO userRequestDTO);

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    public User findUserDetail(Long userId);

    ResponseDTO userLogout(HttpServletRequest httpServletRequest);

    public UploadeFileResponseDTO uploadedFile(User request, MultipartFile file) throws IOException, InterruptedException;

    public PaginationResponseUserDTO getAllUserUploadedDetails(User user, PaginationRequestUserDTO paginationRequestUserDTO);
    public void protectedPdf(MultipartFile file) throws IOException;

}
