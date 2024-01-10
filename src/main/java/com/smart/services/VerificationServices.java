package com.smart.services;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

public interface VerificationServices {

	public String sendingEmail(String mailid, Model model, HttpSession session);

	public String changePassword(Integer otp,String email, String newpassword, String confirmnewpassword, HttpSession session,Model model);

}
