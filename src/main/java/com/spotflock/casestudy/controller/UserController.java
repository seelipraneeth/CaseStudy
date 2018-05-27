package com.spotflock.casestudy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotflock.casestudy.dto.UserDTO;
import com.spotflock.casestudy.response.UserResponse;
import com.spotflock.casestudy.service.UserService;
import com.spotflock.casestudy.util.SecurityConstants;

@RestController
@RequestMapping("/v1/users")
public class UserController {

	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(UserController.class);
		
	@Autowired
	private UserService userServie;

	@PostMapping(value = "/register", produces = { "application/json" })
	public ResponseEntity<UserResponse> register(@RequestBody UserDTO userDTO) {
		log.debug("UserController: Registering user: "+userDTO.getEmail());
		
		UserResponse response = userServie.register(userDTO);
		ResponseEntity<UserResponse> responseEntity = null;

		if (response.isSuccess()) {
			responseEntity = new ResponseEntity<UserResponse>(response, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<UserResponse>(response, HttpStatus.BAD_REQUEST);
		}

		return responseEntity;
	}

	@PostMapping(value = "/login")
	public ResponseEntity<UserResponse> login(@RequestBody UserDTO userDTO) {

		log.debug("UserController: Login user: "+userDTO.getName());
		
		UserResponse response = userServie.login(userDTO);
		ResponseEntity<UserResponse> responseEntity = null;

		if (response.isSuccess())
			responseEntity = new ResponseEntity<UserResponse>(response, HttpStatus.OK);
		else
			responseEntity = new ResponseEntity<UserResponse>(response, HttpStatus.UNAUTHORIZED);

		return responseEntity;
	}

	@GetMapping(value = "/logout/{username}")
	public ResponseEntity<UserResponse> logout(@RequestHeader(SecurityConstants.AUTHORIZATION) String token, @PathVariable("username") String username) {

		log.debug("UserController: Logout user: "+username);
		UserResponse response = userServie.logout(token);
		ResponseEntity<UserResponse> responseEntity = null;
		if (response.isSuccess())
			responseEntity = new ResponseEntity<UserResponse>(response, HttpStatus.OK);
		else
			responseEntity = new ResponseEntity<UserResponse>(response, HttpStatus.UNAUTHORIZED);
		return responseEntity;
	}
}
