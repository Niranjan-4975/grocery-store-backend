// AuthEntryPoint.java (e.g., in com.grocery.config)

package com.grocery.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Set status code based on exception type (401 for bad credentials, 403 for disabled)
        final int status = (authException instanceof DisabledException) ?
                HttpServletResponse.SC_BAD_REQUEST:
                HttpServletResponse.SC_FORBIDDEN;// 403 or 401
        response.setContentType("application/json");
        response.setStatus(status);
        // Build the custom error message map
        final Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status);
        errorDetails.put("message", authException.getMessage()); // ✅ This sends your custom message!
        // Write the JSON response body
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorDetails);
    }
}