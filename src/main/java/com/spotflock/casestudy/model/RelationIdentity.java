package com.spotflock.casestudy.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class RelationIdentity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID1")
	@NotNull
	private int id1;

	@Column(name = "ID2")
	@NotNull
	private int id2;

	public RelationIdentity() {

	}

	public RelationIdentity(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}

	public int getId1() {
		return id1;
	}

	public void setId1(int id1) {
		this.id1 = id1;
	}

	public int getId2() {
		return id2;
	}

	public void setId2(int id2) {
		this.id2 = id2;
	}

}
