package com.agsft.customer.Care.security.jwt;

import com.agsft.customer.Care.constant.HttpStatusCodes;
import com.agsft.customer.Care.dto.response.ResponseDTO;
import com.agsft.customer.Care.util.CustomerAppUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Autowired
    CustomerAppUtil customerAppUtil;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException {
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        ResponseDTO response = customerAppUtil.createResponseEntityDTO(HttpStatusCodes.UNAUTHORIZED,
                "Unauthorized Access to API", null);
        OutputStream outStream = httpServletResponse.getOutputStream();
        outStream.write(new ObjectMapper().writeValueAsString(response).getBytes());
        outStream.flush();
    }
}
