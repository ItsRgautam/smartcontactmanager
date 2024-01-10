package com.smart.services;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.smart.requestdto.UserRequest;

import jakarta.servlet.http.HttpSession;

public interface UserRegisterService {


	public String register(UserRequest userRequest, BindingResult result, boolean agreement, Model model,
			HttpSession session);

}
