package com.spotflock.casestudy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spotflock.casestudy.SpotFlockApplication;
import com.spotflock.casestudy.dto.UserDTO;
import com.spotflock.casestudy.model.User;
import com.spotflock.casestudy.repository.UserRepository;
import com.spotflock.casestudy.response.UserResponse;
import com.spotflock.casestudy.util.ServiceConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomJWTokenService tokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(UserService.class);

	public UserResponse register(UserDTO userDTO) {
		log.info("UserService: rgistering a particular user: "+userDTO.getEmail());
		User user = userRepository.findByEmail(userDTO.getEmail());
		UserResponse response = new UserResponse();

		if (null == user) {
			user = new User(userDTO.getUsername(), userDTO.getPassword(), userDTO.getName(), userDTO.getEmail(),
					userDTO.getMobile());
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
			userRepository.save(user);

			response.setMessage("USER REGISTERED SUCCESSFULLY");
			response.setSuccess(true);

		} else {
			response.setMessage(ServiceConstants.USEREXIST_MSG);
			response.setSuccess(false);
		}
		return response;
	}

	public UserResponse login(UserDTO userDTO) {
		log.info("UserService: login of a user: "+userDTO.getEmail());
		UserResponse response = new UserResponse();
		try {

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = tokenService.generateToken(userDTO.getUsername());
			response.setMessage(ServiceConstants.TOKEN_TYPE + jwt);
			response.setSuccess(true);
			return response;

		} catch (BadCredentialsException e) {
			response.setMessage(ServiceConstants.BAD_CREDENTIALS);
			response.setSuccess(false);
			return response;
		}
	}

	public UserResponse logout(String token) {
		log.info("UserService: logout");
		UserResponse response = new UserResponse();
		SpotFlockApplication.blackListedTokens.add(token.substring(7));
		response.setSuccess(true);
		response.setMessage("Logged out success");
		return response;
	}
}