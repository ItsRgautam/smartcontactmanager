package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.services.VerificationServicesImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/home")
public class VerificationController {

	@Autowired
	VerificationServicesImpl verificationServicesImpl;

	@GetMapping("/forgetpass")
	public String forgetPassword(HttpServletRequest request, HttpSession session) {
		session.removeAttribute("message");
		return "forgetpass";
	}

	@GetMapping("/email")
	public String sendingEmail(@RequestParam(name = "mailid") String mailid, Model model, HttpServletRequest request,
			HttpSession session) {
		session.removeAttribute("message");
		return verificationServicesImpl.sendingEmail(mailid, model, session);
	}

	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("otp") Integer otp,@RequestParam("email") String email, @RequestParam("newpassword") String newpassword,
			@RequestParam("confirmnewpassword") String confirmnewpassword, HttpServletRequest request,
			HttpSession session,Model model) {
		session.removeAttribute("message");

		return verificationServicesImpl.changePassword(otp,email, newpassword, confirmnewpassword, session,model);
	}

}
