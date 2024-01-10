package com.smart.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Service
public class VerificationServicesImpl implements VerificationServices {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	Random random = new Random(1000);

	@Override
	public String sendingEmail(@RequestParam(name = "mailid") String mailid, Model model, HttpSession session) {
		try {


			User user = userRepository.getUserByUserName(mailid);
			if (user == null) {
				throw new Exception("No user registered with this Email");
			}

			model.addAttribute("user", user);
			model.addAttribute("email", mailid);
		
			model.addAttribute("title", "verification");

			String from = "smartcontactmanagingwebsite@gmail.com";
			String to = mailid;

			SimpleMailMessage message = new SimpleMailMessage();

			message.setFrom(from);
			message.setTo(to);
			message.setSubject("OTP");
			int otporiginal = random.nextInt(999999);
			user.setOtp(otporiginal);
			userRepository.save(user);
			System.out.println(otporiginal + "-*-*--*-*-*--*-*-***-*-*-*-**-*-**-*-*-*-*-*-*-*-*-*-*************");

			message.setText("Your otp is " + otporiginal);

			mailSender.send(message);

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));

			System.out.println(e.getMessage());

			return "/forgetpass";
		}

		return "changepassword";
	}


	@Override
	public String changePassword(@RequestParam("otp") Integer otp,@RequestParam("email") String email, @RequestParam("newpassword") String newpassword,
			@RequestParam("confirmnewpassword") String confirmnewpassword, HttpSession session,Model model) {
		
		
		try {
			int otpgot = otp;
			System.out.println("OOOOOOOOOTTTTTTTTTTTTTPPPPPPPPPPP" + otp);

			System.out.println(email);
			User user = userRepository.getUserByUserName(email);
			if (user.getOtp() != otpgot)
				throw new Exception("OTP Incorrect");

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			System.out.println("EXCEPTION************************************" + e.getMessage());
			return "changepassword";
		}

		System.out.println(email);
		System.out.println(newpassword);
		System.out.println(confirmnewpassword);
		User user = userRepository.getUserByUserName(email);
		model.addAttribute("email", email);
		try {
			if (newpassword.length() < 6)
				throw new Exception("Password should be of at least 6 characters");

			if (!newpassword.equals(confirmnewpassword))
				throw new Exception("Passwords do not matched");

			if (this.passwordEncoder.matches(newpassword, user.getPassword()))
				throw new Exception("Enter a new Password");

		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "changepassword";
		}

		user.setPassword(passwordEncoder.encode(newpassword));
		user.setOtp(11102001);
		userRepository.save(user);

		return "redirect:/home/signin?change=password changed successfully";
	}
}
