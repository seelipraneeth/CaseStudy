package com.spotflock.casestudy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotflock.casestudy.dto.FriendDTO;
import com.spotflock.casestudy.response.FriendListResponse;
import com.spotflock.casestudy.service.FriendService;

@RestController
@RequestMapping("/v1/friends")
public class FriendController {

	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(FriendController.class);
	
	@Autowired
	private FriendService friendSevice;

	@PostMapping(value = "/new")
	public ResponseEntity<FriendListResponse> newFriendRequest(@RequestBody List<FriendDTO> friendRequest) {

		log.debug("FriendController: Post New Friend Request");
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		FriendListResponse response = friendSevice.inviteFriends(username, friendRequest);

		ResponseEntity<FriendListResponse> responseEntity = null;
		if (response.isSuccess()) {
			responseEntity = new ResponseEntity<FriendListResponse>(response, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<FriendListResponse>(response, HttpStatus.BAD_REQUEST);
		}

		return responseEntity;
	}

	@GetMapping(value = "/request", produces = { "application/json" })
	public ResponseEntity<?> displayFriends() {
		log.debug("FriendController: Get Friends for a given friend");
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		FriendListResponse response = friendSevice.displayFriends(username);

		ResponseEntity<FriendListResponse> responseEntity = new ResponseEntity<FriendListResponse>(response, HttpStatus.OK);
		
		return responseEntity;
	}

	@PostMapping(value = "/request")
	public ResponseEntity<?> saveRequest(@RequestBody FriendDTO friendRequest) {
		log.debug("FriendController: Post request status for a given friend: "+friendRequest.getName());
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		FriendListResponse response = friendSevice.saveRequest(username, friendRequest);

		ResponseEntity<FriendListResponse> responseEntity = null;
		if (response.isSuccess()) {
			responseEntity = new ResponseEntity<FriendListResponse>(HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<FriendListResponse>(HttpStatus.BAD_REQUEST);
		}

		return responseEntity;
	}
}
