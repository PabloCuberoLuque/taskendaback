package com.pablo.taskendaback.jwt;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@NoArgsConstructor
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws org.springframework.security.core.AuthenticationException, IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"No autorizado");
    }
}
