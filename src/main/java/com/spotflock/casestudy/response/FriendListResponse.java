package com.spotflock.casestudy.response;

import java.util.List;

import com.spotflock.casestudy.dto.FriendDTO;

public class FriendListResponse {

	private boolean isSuccess;
	private String message;
	private List<FriendDTO> friendsList;

	public FriendListResponse(boolean isSuccess, String message, List<FriendDTO> friendsList) {
		super();
		this.isSuccess = isSuccess;
		this.message = message;
		this.friendsList = friendsList;
	}

	public FriendListResponse() {
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<FriendDTO> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<FriendDTO> friendsList) {
		this.friendsList = friendsList;
	}

}
