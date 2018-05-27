package com.spotflock.casestudy.dto;

public class FriendDTO {
	private String name;
	private String status;
	private String email;
	private boolean isRegisteredUser;

	public FriendDTO(String name, String status, String email, boolean isRegisteredUser) {
		super();
		this.name = name;
		this.status = status;
		this.email = email;
		this.isRegisteredUser = isRegisteredUser;
	}

	public FriendDTO() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isRegisteredUser() {
		return isRegisteredUser;
	}

	public void setRegisteredUser(boolean isRegisteredUser) {
		this.isRegisteredUser = isRegisteredUser;
	}

	@Override
	public String toString() {
		return "FriendDTO [name=" + name + ", status=" + status + ", email=" + email + ", isRegisteredUser="
				+ isRegisteredUser + "]";
	}

}
