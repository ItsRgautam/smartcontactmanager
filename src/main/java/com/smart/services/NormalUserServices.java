package com.smart.services;

import java.security.Principal;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface NormalUserServices {

	public void addCommon(Model model,  HttpServletRequest request);

	public String registerContact(Contact contact, MultipartFile file, Model model,
			HttpServletRequest request);

	public String showContacts(Model model, HttpServletRequest request);

	public String ContactDetails(Integer cId, Model model,  HttpServletRequest request);

	public String DeleteContact(Integer cId, Model model, HttpServletRequest request);

	public String UpdateContactForm(Integer cId, Model model,  HttpServletRequest request);

	public String UpdateContactProcess(Contact contact, MultipartFile file,  Model model,
			HttpServletRequest request);

	public ResponseEntity<?> search(String query, HttpServletRequest request);

	public String changePass(String oldpassword, String newpassword, HttpServletRequest request);

	public String favourite(int cid);

	public String favouriteContacts(Model model, HttpServletRequest request);

	public String deleteUser(Model model, HttpServletRequest request, HttpServletResponse response);

}
