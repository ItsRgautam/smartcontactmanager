package com.smart.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.html.HTMLModElement;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.helper.MyComparator;
import com.smart.requestdto.UserRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import javax.imageio.IIOException;

@Service
@Slf4j
public class NormalUserServicesImpl implements NormalUserServices {

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	JwtServices jwtServices;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	MyComparator myComparator;

	public String getUsername(HttpServletRequest request) {
		
		System.out.println("inside get username method");
		
		String jwttoken=null;
		
		 Cookie[] cookies=   request.getCookies();
	   	 if(cookies!=null) {
	   	 for(Cookie c:cookies) {
	   		 if(c.getName().equalsIgnoreCase("token")) {
	   			 jwttoken=c.getValue();
	   		 }
	   	 }
	   	 }
	   	jwttoken = jwttoken.substring(6);
	   	 String name=jwtServices.getUsernameFromToken(jwttoken);
	   	 System.out.println(name);
	   	 
	   	 return name;
		
	}

	@Override
	public void addCommon(Model model,HttpServletRequest request) {
		
		String username=getUsername(request);

		User user = userRepository.getUserByUserName(username);
		model.addAttribute("user", user);
	
	}

	@Override
	public String favourite(@RequestParam("cid") int cid) {
     
		Optional<Contact> optional=contactRepository.findById(cid);
		Contact contact=optional.get();
		boolean result=contact.getFavourite();
		contact.setFavourite(!result);
		contactRepository.save(contact);
        
		return "redirect:/user/" + contact.getCId() + "/contact";
	}
	
	@Override
	public String favouriteContacts(Model model,HttpServletRequest request ) {
		
		
		
		User user = userRepository.getUserByUserName( getUsername(request));
		List<Contact> contacts=contactRepository.findByFavouriteAndUser(true, user);
		contacts.sort(myComparator);
		System.out.println(contacts);
		model.addAttribute("contacts", contacts);

		return "normal/showcontacts";
	}

	@Override
	public String registerContact(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			 Model model,HttpServletRequest request) {
		
		HttpSession session=request.getSession();

		session.removeAttribute("message");
		try {
			User user = userRepository.getUserByUserName(getUsername(request));
			String mobilegot = contact.getPhone();

			if (mobilegot.length() != 10 && mobilegot.length() != 12)
				throw new Exception("Enter A valid mobile number");
			List<Contact> optionalcontact = contactRepository.findByPhoneAndUser(mobilegot, user);

			if (optionalcontact.size() != 0) {
				throw new Exception("phone number already associated with other contact");
			}

//			processing image
			if (file.isEmpty()) {
				contact.setImage("defaultprofile.png");

			} else {
				Integer integer = user.getId();
				String id = integer.toString();
				String filename = id.concat(file.getOriginalFilename());
				contact.setImage(filename);

				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + filename);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			user.getContactList().add(contact);
			this.userRepository.save(user);
			model.addAttribute("userRequest", new UserRequest());
			session.setAttribute("message", new Message("Saved Successfully ", "alert-success"));

			System.out.println("contact from register contact");

		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something Went Wrong !! " + e.getMessage(), "alert-danger"));

			e.printStackTrace();
		}

		return "normal/add_contact_form";
	}

	@Override
	public String showContacts(Model model,HttpServletRequest request) {
		
	
		
		User user = userRepository.getUserByUserName(getUsername(request));

		List<Contact> contacts = this.contactRepository.findContactByUser(user.getId());
		contacts.sort(myComparator);
		System.out.println(contacts);
		model.addAttribute("contacts", contacts);

		return "normal/showcontacts";
	}

	@Override
	public String ContactDetails(@PathVariable("cId") Integer cId, Model model,HttpServletRequest request) {
		
	
		
		Optional<Contact> optional = contactRepository.findById(cId);
		Contact contact = optional.get();

		User user = userRepository.getUserByUserName(getUsername(request));

		if (user.getId() == (contact.getUser()).getId())
			model.addAttribute("contact", contact);

		System.out.println("contact details printing..............." + contact.getName() + contact.getEmail());
		return "normal/contactdetails";
	}

	@Override
	public String DeleteContact(@PathVariable("cId") Integer cId, Model model,HttpServletRequest request) {


		Optional<Contact> optionalContact = contactRepository.findById(cId);
		Contact contact = optionalContact.get();

		System.out.println("contact details printing..............." + contact.getName() + contact.getEmail());

		User user = userRepository.getUserByUserName(getUsername(request));

		if (user.getId() == (contact.getUser()).getId()) {

			if (!(contact.getImage().equals("defaultprofile.png"))) {

				try {
					File deleteFile = new ClassPathResource("static/images").getFile();
					File file1 = new File(deleteFile, contact.getImage());
					file1.delete();

				} catch (Exception e) {

				}

			}
			user.getContactList().remove(contact);
			this.userRepository.save(user);
		}
		return "redirect:/user/showcontacts";
	}

	@Override
	public String UpdateContactForm(@PathVariable("cId") Integer cId, Model model, HttpServletRequest request) {

		Optional<Contact> optionalContact = contactRepository.findById(cId);
		Contact contact = optionalContact.get();
		model.addAttribute("contact", contact);

		return "normal/updatecontactform";

	}

	@Override
	public String UpdateContactProcess(@ModelAttribute Contact contact,
			@RequestParam("profileimage") MultipartFile file, Model model,HttpServletRequest request) {
		
		HttpSession session=request.getSession();
		try {

			System.out.println(
					"Updating contact-------------------****************************************************************");
			System.out.println("name-------" + contact.getName());

			User user = userRepository.getUserByUserName(getUsername(request));
			String mobilegot = contact.getPhone();

			List<Contact> optionalcontact = contactRepository.findByPhoneAndUser(mobilegot, user);

			if (optionalcontact.size() > 0) {
				int contactid = optionalcontact.get(0).getCId();

				if (contact.getCId() != contactid) {

					throw new Exception("phone number already associated with other contact");
				}
			}

//	  		processing image
			if (file.isEmpty()) {
				// contact.setImage("defaultprofile.png");

			} else {

				if (!(contact.getImage().equals("defaultprofile.png"))) {
					File deleteFile = new ClassPathResource("static/images").getFile();
					File file1 = new File(deleteFile, contact.getImage());
					file1.delete();
				}

				Integer integer = user.getId();
				String id = integer.toString();
				String filename = id.concat(file.getOriginalFilename());
				contact.setImage(filename);

				File saveFile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + filename);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			contact.setUser(user);
			contactRepository.save(contact);

		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something Went Wrong !! " + e.getMessage(), "alert-danger"));

			e.printStackTrace();
			return "normal/updatecontactform";

		}

		return "redirect:/user/" + contact.getCId() + "/contact";
	}

	@Override
	public ResponseEntity<?> search(@PathVariable("query") String query, HttpServletRequest request) {

		
		System.out.println(query);
		User user = userRepository.getUserByUserName(getUsername(request));
		List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}

	@Override
	public String changePass(@RequestParam("oldpassword") String oldpassword,
			@RequestParam("newpassword") String newpassword, HttpServletRequest request) {
		
		       HttpSession session=request.getSession();
		  
		try {
			if (newpassword.length() < 6)
				throw new Exception("New password length should be more than 5");

			if (newpassword.equals(oldpassword))
				throw new Exception("Enter a different New Password");
			User user = userRepository.getUserByUserName(getUsername(request));
			String original = user.getPassword();

			if (this.passwordEncoder.matches(oldpassword, original)) {
				user.setPassword(passwordEncoder.encode(newpassword));
				userRepository.save(user);
				session.setAttribute("message", new Message("Password changed Successfully", "alert-success"));
			} else {
				throw new Exception(" Old Password is Incorrect");
			}

		} catch (Exception e) {
			System.out.println("Exception -------------------------------------" + e.getMessage());
			session.setAttribute("message", new Message("Password Not changed " + e.getMessage(), "alert-danger"));
		}

		return "normal/settings";
	}

	@Override
	public String deleteUser(Model model, HttpServletRequest request, HttpServletResponse response)   {
		try{

			  User user = userRepository.getUserByUserName(getUsername(request));
			  List<Contact> contactlist=user.getContactList();
			  if(contactlist!=null)
			   {  for (Contact c: contactlist)
			      {
					   if (! c.getName().equals("defaultprofile.png"))
					   {
						File deleteFile = new ClassPathResource("static/images").getFile();
						File file1 = new File(deleteFile, c.getImage());
						file1.delete();

					   }
				  }
			   }
			Cookie cookies = new Cookie("token", null);

			cookies.setMaxAge(0);
			cookies.setPath("/");

			response.addCookie(cookies);
			userRepository.delete(user);

		}
		catch (Exception e){

		}

	return "redirect:/home/";}
}
