package com.agsft.customer.Care.util;

import com.agsft.customer.Care.dto.request.PhoneNumberRestRequest;
import com.agsft.customer.Care.dto.response.PhoneNumberRestResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pranjal
 */

@Service
public class PhoneNumberUtility {
        public PhoneNumberRestResponse phoneNumberSearch(PhoneNumberRestRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
                Map<String, Object> body
                        = new HashMap<>();
                body.put("mobileNumber", request.getMobileNumber());
                HttpEntity<Map<String, Object>> requestEntity
                        = new HttpEntity<>(body, headers);

                String serverUrl = "http://192.168.50.120:9092/verify/dnd";

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<PhoneNumberRestResponse> response = restTemplate
                        .postForEntity(serverUrl, requestEntity, PhoneNumberRestResponse.class);

                return response.getBody();
        }

}


