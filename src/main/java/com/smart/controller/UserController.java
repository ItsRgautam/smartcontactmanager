
package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.smart.requestdto.UserRequest;
import com.smart.services.JwtServices;
import com.smart.services.UserRegisterServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@SessionAttributes
@RequestMapping("/home")
public class UserController {

	@Autowired
	UserRegisterServiceImpl userRegisterServiceImpl;
	
	@Autowired
	JwtServices jwtServices;

	@GetMapping("/")
	public String home(Model M) {
		M.addAttribute("title", "Home-Smart Contact Manager");

		return "home";
	}

	@GetMapping("/about")
	public String about(Model M) {
		M.addAttribute("title", "About-Smart Contact Manager");

		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model M, HttpSession session) {
		M.addAttribute("title", "Signup-Smart Contact Manager");
		M.addAttribute("userRequest", new UserRequest());
		session.removeAttribute("message");

		return "signup";
	}

	@RequestMapping("/signin")
	public String index(HttpSession session, HttpServletRequest request) {

		session.removeAttribute("email");
		return "login";
	}

	@PostMapping("/signingin")
	public String signin(@RequestParam("username") String username, @RequestParam("password") String password,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		System.out.println("inside signinapi--------------------------------------------------------------");
		
		return jwtServices.signin(username, password, request, response, session);

	}

	@PostMapping("/register")
	public String register(@Valid UserRequest userRequest, BindingResult result, boolean agreement, Model model,
			HttpSession session) {

		return userRegisterServiceImpl.register(userRequest, result, agreement, model, session);

	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {

		Cookie cookies = new Cookie("token", null);

		cookies.setMaxAge(0);
		cookies.setPath("/");

		response.addCookie(cookies);

		return "redirect:/home/signin?logout=logout successfully";
	}

}
