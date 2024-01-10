package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
	@Autowired
	private JwtAutheticationEntryPoint point;
	
	@Autowired
	private JwtAuthenticationFilter filter;
	
	 @Bean
     SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         http.csrf(csrf -> csrf.disable())
                 .authorizeHttpRequests(auth-> auth.requestMatchers("/home/**").permitAll()
                		 .requestMatchers("/css/**").permitAll()
                		 .requestMatchers("/images/**").permitAll()
                		 .requestMatchers("/js/**").permitAll()
                         .anyRequest()
                         .authenticated())
                 .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
         http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
         return http.build();
	 }
	 
//	 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		 http.authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**")
//		 .hasRole("USER").requestMatchers("/**").permitAll().and().formLogin().loginPage("/signin")
//		 .defaultSuccessUrl("/user/index").and().csrf().disable();
//		 http.formLogin().defaultSuccessUrl("/user/index", true);
//		 return http.build();
//		 }


}
