package com.spotflock.casestudy.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.spotflock.casestudy.dto.FriendDTO;
import com.spotflock.casestudy.repository.FriendRepository;
import com.spotflock.casestudy.repository.UserRepository;
import com.spotflock.casestudy.response.FriendListResponse;
import com.spotflock.casestudy.service.FriendService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FriendControllerTest {

	SecurityContext context=null; 
	@Configuration
	static class FriendServiceTestContextConfiguration {
		@Bean
		public FriendController friendController() {
			return new FriendController();
		}

		@Bean
		public FriendService friendService() {
			return Mockito.mock(FriendService.class);
		}

		@Bean
		public UserRepository userRepository() {
			return Mockito.mock(UserRepository.class);
		}

		@Bean
		public FriendRepository friendRepository() {
			return Mockito.mock(FriendRepository.class);
		}

		@Bean
		public SecurityContextHolder SecurityContextHolder() {
			return Mockito.mock(SecurityContextHolder.class);
		}
	}

	// Autowired the FriendService bean so that it is injected from the
	// configuration

	@Autowired
	private FriendController friendController;

	@Autowired
	private FriendService friendService;

	@Autowired
	private SecurityContextHolder SecurityContextHolder;

	FriendDTO friendDTO = new FriendDTO("USER1", "REQUEST PENDING", "USER@MAIL.COM", true);
	List<FriendDTO> dtolist = new ArrayList<FriendDTO>();

	@SuppressWarnings("static-access")
	@Before
	public void setup() {
		friendDTO = new FriendDTO("USER1", "REQUEST PENDING", "USER@MAIL.COM", true);
		dtolist = new ArrayList<FriendDTO>();
		dtolist.add(friendDTO);
		init();
		SecurityContextHolder.setContext(context);
	}

	@After
	public void teardown() {
		Mockito.reset(friendService);
		Mockito.reset(SecurityContextHolder);
	}

	// @SuppressWarnings("static-access")
	@Test
	public void testInviteFriendsSuccess() {
		FriendListResponse friendResponse = new FriendListResponse();
		friendResponse.setFriendsList(dtolist);
		friendResponse.setMessage("Invited friends");
		friendResponse.setSuccess(true);
		Mockito.when(friendService.inviteFriends("Praneeth", dtolist)).thenReturn(friendResponse);
		ResponseEntity<FriendListResponse> friendsResponse = friendController.newFriendRequest(dtolist);
		assertTrue(friendsResponse.getBody().isSuccess());
	}
	
	@Test
	public void testInviteFriendsFailure() {
		FriendListResponse friendResponse = new FriendListResponse();
		friendResponse.setFriendsList(dtolist);
		friendResponse.setMessage("Invited friends");
		friendResponse.setSuccess(false);
		Mockito.when(friendService.inviteFriends("Praneeth", dtolist)).thenReturn(friendResponse);
		ResponseEntity<FriendListResponse> friendsResponse = friendController.newFriendRequest(dtolist);
		assertEquals(friendsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	@Test
	public void testDisplayFriendsSuccess() {
		FriendListResponse friendResponse = new FriendListResponse();
		friendResponse.setFriendsList(dtolist);
		friendResponse.setMessage("Invited friends");
		friendResponse.setSuccess(true);
		Mockito.when(friendService.displayFriends("Praneeth")).thenReturn(friendResponse);
		@SuppressWarnings("unchecked")
		ResponseEntity<FriendListResponse> friendsResponse = (ResponseEntity<FriendListResponse>) friendController.displayFriends();
		assertTrue(friendsResponse.getBody().isSuccess());
	}
	
	@Test
	public void testSaveRequestSuccess() {
		FriendListResponse friendResponse = new FriendListResponse();
		friendResponse.setFriendsList(dtolist);
		friendResponse.setMessage("Request updated");
		friendResponse.setSuccess(true);
		Mockito.when(friendService.saveRequest("Praneeth", friendDTO)).thenReturn(friendResponse);
		@SuppressWarnings("unchecked")
		ResponseEntity<FriendListResponse> friendsResponse = (ResponseEntity<FriendListResponse>) friendController.saveRequest(friendDTO);
		assertEquals(friendsResponse.getStatusCode(), HttpStatus.OK);
	}
	@Test
	public void testSaveRequestFailure() {
		FriendListResponse friendResponse = new FriendListResponse();
		friendResponse.setFriendsList(dtolist);
		friendResponse.setMessage("Request updated");
		friendResponse.setSuccess(false);
		Mockito.when(friendService.saveRequest("Praneeth", friendDTO)).thenReturn(friendResponse);
		@SuppressWarnings("unchecked")
		ResponseEntity<FriendListResponse> friendsResponse = (ResponseEntity<FriendListResponse>) friendController.saveRequest(friendDTO);
		assertEquals(friendsResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
	}
	private void init() {
		Authentication authentication = new Authentication() {

			@Override
			public String getName() {
				return "Praneeth";
			}

			@Override
			public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public Object getPrincipal() {
				return null;
			}

			@Override
			public Object getDetails() {
				return null;
			}

			@Override
			public Object getCredentials() {
				return null;
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
		};
		context = new SecurityContext() {

			@Override
			public void setAuthentication(Authentication authentication) {
			}

			@Override
			public Authentication getAuthentication() {
				return authentication;
			}
		};
	}
}
