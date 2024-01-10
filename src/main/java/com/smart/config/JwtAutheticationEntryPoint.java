package com.smart.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAutheticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer = response.getWriter();
		writer.println("Access Denied !! " + authException.getMessage());
		if (authException.getMessage().equalsIgnoreCase("Full authentication is required to access this resource")) {

			response.sendRedirect("/home/signin?relogin=login again");
		} else {
			response.sendRedirect("/home/signin?error=incorrect Credential");
		}
	}

}
