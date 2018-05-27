package com.spotflock.casestudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.spotflock.casestudy.model.User;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, String> {

	User findById(int userid);

	User findByUsername(String username);

	User findByEmail(String email);
}
