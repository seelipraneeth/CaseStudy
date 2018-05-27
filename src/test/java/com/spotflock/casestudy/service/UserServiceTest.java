package com.spotflock.casestudy.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.spotflock.casestudy.dto.UserDTO;
import com.spotflock.casestudy.model.User;
import com.spotflock.casestudy.repository.UserRepository;
import com.spotflock.casestudy.response.UserResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UserServiceTest {

	User loginUser = null;
	UserDTO userDTO = null;

	@Configuration
	static class UserServiceTestContextConfiguration {
		@Bean
		public UserService userService() {
			return new UserService();
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return Mockito.mock(PasswordEncoder.class);
		}

		@Bean
		public CustomJWTokenService tokenService() {
			return Mockito.mock(CustomJWTokenService.class);
		}

		@Bean
		public AuthenticationManager authenticationManager() {
			return Mockito.mock(AuthenticationManager.class);
		}

		@Bean
		public UserRepository userRepository() {
			return Mockito.mock(UserRepository.class);
		}
	}

	// Autowired the FriendService bean so that it is injected from the
	// configuration
	@Autowired
	public UserService userService;

	@Autowired
	public UserRepository userRepository;

	@Autowired
	public CustomJWTokenService tokenService;

	@Autowired
	public AuthenticationManager authenticationManager;

	@Autowired
	public PasswordEncoder passwordEncoder;

	@Before
	public void setup() {
		userDTO = new UserDTO("USERNAME", "PASSWORD", "NAME", "USER@MAIL.COM", "1234567890");
	}

	@After
	public void teardown() {
		Mockito.reset(userRepository);
	}

	@Test
	public void testRegister_SUCEESS() {
		Mockito.when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);
		Mockito.when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("ENCODEDPASSWORD");
		User user=new User(userDTO.getUsername(), "ENCODEDPASSWORD", userDTO.getName(), userDTO.getEmail(), userDTO.getMobile());
		user.setId(1);
		UserResponse userResponse = userService.register(userDTO);
		assertTrue(userResponse.isSuccess());
		
	}
	@Test
	public void testRegister_FAILURE() {
		User user=new User(userDTO.getUsername(), userDTO.getPassword(), userDTO.getName(), userDTO.getEmail(), userDTO.getMobile());
		Mockito.when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(user);

		UserResponse userResponse = userService.register(userDTO);
		assertFalse(userResponse.isSuccess());
	}
	
	@Test
	public void testLogin_SUCEESS() {
		Authentication authentication = null;
		Mockito.when(authenticationManager.authenticate(Matchers.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
		Mockito.when(tokenService.generateToken(userDTO.getName())).thenReturn("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		UserResponse userResponse = userService.login(userDTO);
		assertTrue(userResponse.isSuccess());
	}
	
	@Test
	public void testLogin_FAILURE() {		
		Mockito.when(authenticationManager.authenticate(Matchers.any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(null, null));
		UserResponse userResponse = userService.login(userDTO);
		assertFalse(userResponse.isSuccess());
	}
	
	@Test
	public void testLogout_SUCEESS() {		
		UserResponse userResponse = userService.logout("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmFuZWV0aCIsImlhdCI6MTUyNzM4OTMyOCwiZXhwIjoxNTI3Mzg5NjI4fQ.G91xq0j3DIH_EsI0UjS8xSPIT4LK3mk73Zd061Qpal-3JaJ_X7BqObu50HlhkdXyDsp764uEtwgmz2sFlbr-lw");
		assertTrue(userResponse.isSuccess());
	}
}
