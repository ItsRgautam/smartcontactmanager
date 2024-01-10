package com.smart.services;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import  com.smart.config.MyConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class JwtServices {
	  
	   @Autowired
	    private  MyConfig myConfig;
	   
	   @Autowired
	   UserRegisterServiceImpl userRegisterServiceImpl;
	   
	   @Autowired
	   AuthenticationManager authenticationManager;

	    private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	    private Key getSigninKey(){
	        byte[] key= Decoders.BASE64.decode(secret);
	        return Keys.hmacShaKeyFor(key);
	    }
	    public String getToken(UserDetails userDetails){
	        Date jwtIssuedAt = new Date(System.currentTimeMillis());
	        Date jwtExpiredAt = new Date(System.currentTimeMillis() + myConfig.getAccessTokenMaxLife());

	        return Jwts.builder().setSubject(userDetails.getUsername())
	                .setIssuedAt(jwtIssuedAt)
	                .setExpiration(jwtExpiredAt)
//	                .signWith(  new SecretKeySpec(Base64.getDecoder().decode(myconfig.getSecretKey()),
//	                        SignatureAlgorithm.HS512.getValue()), SignatureAlgorithm.HS512)
	                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
	                .compact();
	    }

	    public Boolean validateToken(String token, UserDetails userDetails) {
	        final String username = getUsernameFromToken(token);
	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }
	    public String getUsernameFromToken(String token) {
	        return getClaimFromToken(token, Claims::getSubject);
	    }

	    private Boolean isTokenExpired(String token) {
	        final Date expiration = getExpirationDateFromToken(token);
	        return expiration.before(new Date());
	    }


	    //retrieve expiration date from jwt token
	    public Date getExpirationDateFromToken(String token) {
	        return getClaimFromToken(token, Claims::getExpiration);
	    }

	    private Claims getAllClaimsFromToken(String token) {
	        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	    }

	    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = getAllClaimsFromToken(token);
	        return claimsResolver.apply(claims);
	    }
	    
		public String signin(@RequestParam("username") String username, @RequestParam("password") String password,
				HttpServletRequest request, HttpServletResponse response, HttpSession session)
		{
			
			
			this.doAuthenticate(username, password);

			try {
				UserDetails userDetails =userRegisterServiceImpl.loadUserByUsername(username);
				String token = this.getToken(userDetails);
				token = "Bearer" + token;

				Cookie cookies = new Cookie("token", token);
				cookies.setMaxAge(60 * 60 * 2);
				cookies.setPath("/");
				response.addCookie(cookies);

				session.removeAttribute("email");
				return "redirect:/user/index";
			} catch (Exception e) {
				e.printStackTrace();

				return "redirect:/home/signin";
			}
			
		}
		
		
		private void doAuthenticate(String email, String password) {

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
			try {
				authenticationManager.authenticate(authentication);

			} catch (BadCredentialsException e) {
				throw new BadCredentialsException(" Invalid Username or Password  !!");
			}

		}


}
