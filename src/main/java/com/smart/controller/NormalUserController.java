package com.smart.controller;

import java.security.Principal;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.services.NormalUserServicesImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class NormalUserController {

	@Autowired
	NormalUserServicesImpl normalUserServicesImpl;
	

	@ModelAttribute
	public void addCommon(Model model,HttpServletRequest request) {

		normalUserServicesImpl.addCommon(model,request );

	}

	@RequestMapping("/index")
	public String index(Model model,HttpServletRequest request) {

		model.addAttribute("title", "Dashboard");
		
		return "normal/user_dashboard";
	}

	@GetMapping("/favourite/{cid}")
	public String favourite(@PathVariable("cid") Integer cid,HttpServletRequest request) {

		return normalUserServicesImpl.favourite(cid);
	}

	@GetMapping("/addcontact")
	public String addContact(Model model, Principal principal,HttpServletRequest request) {

		HttpSession session=request.getSession();
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		session.removeAttribute("message");

		return "normal/add_contact_form";
	}

	@PostMapping("/registercontact")
	public String registerContact(Contact contact, @RequestParam("profileimage") MultipartFile file,
			Principal principal, Model model,HttpServletRequest request) {

		return normalUserServicesImpl.registerContact(contact, file, model, request);
	}

	// show contacts
	@GetMapping("/showcontacts")
	public String showContacts(Model model, Principal principal, HttpServletRequest request) {

		HttpSession session=request.getSession();
		session.removeAttribute("message");
		model.addAttribute("title", "View Contacts");

		return normalUserServicesImpl.showContacts(model,request);
	}
	
	@GetMapping("/showFavouritecontacts")
	public String showFavouriteContacts(Model model,HttpServletRequest request) {

		HttpSession session=request.getSession();
		session.removeAttribute("message");
		model.addAttribute("title", "Favourite Contacts");

		return normalUserServicesImpl.favouriteContacts(model, request);
	}

	@GetMapping("/{cId}/contact")
	public String ContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal,HttpServletRequest request) {
		model.addAttribute("title", "Contact Details");

		return normalUserServicesImpl.ContactDetails(cId, model,request);
	}

	@GetMapping("/delete/{cId}")
	public String DeleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal,HttpServletRequest request) {

		return normalUserServicesImpl.DeleteContact(cId, model,request);
	}

	// updatecontactform

	@GetMapping("/update/{cId}")
	public String UpdateContactForm(@PathVariable("cId") Integer cId, Model model, Principal principal,HttpServletRequest request) {

		return normalUserServicesImpl.UpdateContactForm(cId, model,request);

	}

	@PostMapping("/updatecontactprocess")
	public String UpdateContactProcess(@ModelAttribute Contact contact,
			@RequestParam("profileimage") MultipartFile file, Principal principal, Model model,HttpServletRequest request) {

		return normalUserServicesImpl.UpdateContactProcess(contact, file, model, request);

	}

//   search controller

	@ResponseBody
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal,HttpServletRequest request) {

		return normalUserServicesImpl.search(query, request);
	}

	// Settings
	@GetMapping("/settings")
	public String settings(Model model,HttpServletRequest request) {

		HttpSession session=request.getSession();
		session.removeAttribute("message");
		model.addAttribute("oldpassword", new String());
		model.addAttribute("newpassword", new String());
		

		return "normal/settings";
	}

	@PostMapping("/changepass")
	public String changePass(@RequestParam("oldpassword") String oldpassword,
			@RequestParam("newpassword") String newpassword, Principal principal, HttpServletRequest request) {

		return normalUserServicesImpl.changePass(oldpassword, newpassword, request);
	}

	@GetMapping("/deleteuser")
	public  String deleteUser(Model model, HttpServletRequest request, HttpServletResponse response){
		return normalUserServicesImpl.deleteUser(model, request, response);
	}

}
