package com.spotflock.casestudy.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "FRIENDS")
public class Friends {

	@EmbeddedId
	private RelationIdentity relationIdentity;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ACTIONBY")
	private int actionBy;

	
	public Friends() {
		super();
	}

	public Friends(RelationIdentity relationIdentity, String status, int actionBy) {
		super();
		this.relationIdentity = relationIdentity;
		this.status = status;
		this.actionBy = actionBy;
	}

	public RelationIdentity getRelationIdentity() {
		return relationIdentity;
	}

	public void setRelationIdentity(RelationIdentity relationIdentity) {
		this.relationIdentity = relationIdentity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getActionBy() {
		return actionBy;
	}

	public void setActionBy(int actionBy) {
		this.actionBy = actionBy;
	}

}