package com.spotflock.casestudy.security;

import static java.util.Collections.emptyList;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spotflock.casestudy.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
		
	private UserRepository userRepository;

	public CustomUserDetailsService(UserRepository applicationUserRepository) {
		this.userRepository = applicationUserRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("CustomUserDetailsService: Loading user by username: "+username);
		com.spotflock.casestudy.model.User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(user.getUsername(), user.getPassword(), emptyList());
	}

}
