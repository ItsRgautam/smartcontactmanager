package com.smart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.requestdto.UserRequest;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UserRegisterServiceImpl implements UserRegisterService, UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public String register(@Valid @ModelAttribute("userRequest") UserRequest userRequest, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("please mark checkbox");
				throw new Exception("Agree to terms and conditions");
			}

			if (result.hasErrors()) {
				System.out.println(
						"validation error ***************************************************************************");
				System.out.println(result);
				model.addAttribute("userRequest", userRequest);
				session.setAttribute("message", new Message("Invalid input ", "alert-danger"));
				return "signup";
			}

			User user2 = userRequest.toUser();
			User users = userRepository.save(user2);
			System.out.println(users);

			model.addAttribute("userRequest", new UserRequest());

			session.setAttribute("message", new Message("Successfully Register", "alert-success"));

			return "signup";

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			model.addAttribute("userRequest", userRequest);
			session.setAttribute("message",
					new Message("Something Went Wrong !!\n User Already Exists", "alert-danger"));
			return "signup";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("userRequest", userRequest);
			session.setAttribute("message", new Message("Something Went Wrong !! " + e.getMessage(), "alert-danger"));

			return "signup";

		}

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return userRepository.getUserByUserName(username);
	}

}
