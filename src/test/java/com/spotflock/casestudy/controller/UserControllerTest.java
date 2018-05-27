package com.spotflock.casestudy.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
public class UserControllerTest {

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
	private FriendController  friendController;
	
	@Autowired
	private FriendService friendService;

	@Autowired
	private SecurityContextHolder SecurityContextHolder;

	FriendDTO friendDTO = new FriendDTO("USER1", "REQUEST PENDING", "USER@MAIL.COM", true);
	List<FriendDTO> dtolist = new ArrayList<FriendDTO>();

	@Before
	public void setup() {
		friendDTO = new FriendDTO("USER1", "REQUEST PENDING", "USER@MAIL.COM", true);
		dtolist = new ArrayList<FriendDTO>();
		dtolist.add(friendDTO);		
	}

	@After
	public void teardown() {
		Mockito.reset(friendService);
		Mockito.reset(SecurityContextHolder);
	}

	
	
	//@SuppressWarnings("static-access")
	@Test
	@SuppressWarnings("static-access")	
	public void testInviteFriendsSuccess() {
		Authentication authentication = new Authentication() {
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
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
		
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("praneeth");
		FriendListResponse friendResponse = new FriendListResponse();
		friendResponse.setFriendsList(dtolist );
		friendResponse.setMessage("Invited friends");
		friendResponse.setSuccess(true);
		Mockito.when(friendService.inviteFriends("praneeth", dtolist)).thenReturn(friendResponse);
		ResponseEntity<FriendListResponse> friendsResponse = friendController.newFriendRequest(dtolist);
		assertTrue(friendsResponse.getBody().isSuccess());
	}
}
