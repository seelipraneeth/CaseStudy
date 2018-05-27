package com.spotflock.casestudy.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.spotflock.casestudy.dto.FriendDTO;
import com.spotflock.casestudy.model.Friends;
import com.spotflock.casestudy.model.RelationIdentity;
import com.spotflock.casestudy.model.User;
import com.spotflock.casestudy.repository.FriendRepository;
import com.spotflock.casestudy.repository.UserRepository;
import com.spotflock.casestudy.response.FriendListResponse;
import com.spotflock.casestudy.util.ServiceConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FriendServiceTest {

	User loginUser = null;

	@Configuration
	static class FriendServiceTestContextConfiguration {
		@Bean
		public FriendService friendService() {
			return new FriendService();
		}

		@Bean
		public FriendRepository friendRepository() {
			return Mockito.mock(FriendRepository.class);
		}

		@Bean
		public UserRepository userRepository() {
			return Mockito.mock(UserRepository.class);
		}
	}

	// Autowired the FriendService bean so that it is injected from the
	// configuration
	@Autowired
	private FriendService friendService;

	@Autowired
	private FriendRepository friendRepository;

	@Autowired
	private UserRepository userRepository;

	List<FriendDTO> dtolist = new ArrayList<FriendDTO>();

	@Before
	public void setup() {
		loginUser = new User("praneeth", "reachpraneeth@gmail.com", "1234232");
		loginUser.setId(1);
		Mockito.when(userRepository.findByUsername(loginUser.getName())).thenReturn(loginUser);
	}

	@After
	public void teardown() {
		Mockito.reset(friendRepository);
		Mockito.reset(userRepository);
	}

	@Test
	public void testInviteFriendsSuccess() {
		mockForInviteFriends1();
		FriendListResponse friendsResponse = friendService.inviteFriends("praneeth", dtolist);
		assertTrue(friendsResponse.isSuccess());
	}

	@Test
	public void testInviteFriendsFailure() {
		mockForInviteFriends2();
		FriendListResponse friendsResponse = friendService.inviteFriends("praneeth", dtolist);
		assertFalse(friendsResponse.isSuccess());
	}

	@Test
	public void testDisplayFriendsSuccess() {
		callTestData();
		FriendListResponse friendsResponse = friendService.displayFriends("praneeth");
		assertTrue(friendsResponse.isSuccess());
	}
	@Test
	public void testDisplayFriendsSuccessNODATA() {
		callTestData2();
		FriendListResponse friendsResponse = friendService.displayFriends("praneeth");
		assertTrue(friendsResponse.isSuccess());
	}

	@Test
	public void testSaveRequestFailure_WhenRequestPendngIsWithOther() {
		FriendDTO friendDTO = new FriendDTO("user2", "REQUEST ACCEPTED", "user2@gmail.com", true);
		Mockito.when(userRepository.findByUsername(loginUser.getName())).thenReturn(loginUser);
		User user2 = new User("username2", "test2", "user2", "user2@gmail.com", "1234567890");
		user2.setId(2);
		loginUser.setId(3);
		Mockito.when(userRepository.findByEmail(friendDTO.getEmail())).thenReturn(user2);
		RelationIdentity relationIdentity = new RelationIdentity(2, 3);
		Friends friend = new Friends(relationIdentity, "REQUEST PENDING", 3);
		Mockito.when(friendRepository.findByRelationIdentity(Matchers.any(RelationIdentity.class))).thenReturn(friend);
		FriendListResponse friendsResponse = friendService.saveRequest(loginUser.getName(), friendDTO);
		assertFalse(friendsResponse.isSuccess()); 
	}

	@Test
	public void testSaveRequestFailure_WhenNoRequestUpdate() {
		FriendDTO friendDTO = new FriendDTO("user2", "REQUEST ACCEPTED", "user2@gmail.com", true);
		Mockito.when(userRepository.findByUsername(loginUser.getName())).thenReturn(loginUser);
		User user2 = new User("username2", "test2", "user2", "user2@gmail.com", "1234567890");
		user2.setId(2);
		loginUser.setId(1);
		Mockito.when(userRepository.findByEmail(friendDTO.getEmail())).thenReturn(user2);
		RelationIdentity relationIdentity = new RelationIdentity(2, 3);
		Friends friend = new Friends(relationIdentity, "REQUEST PENDING", 1);
		Mockito.when(friendRepository.findByRelationIdentity(Matchers.any(RelationIdentity.class))).thenReturn(friend);
		FriendListResponse friendsResponse = friendService.saveRequest(loginUser.getName(), friendDTO);
		assertFalse(friendsResponse.isSuccess()); 
	}
	
	@Test
	public void testSaveRequestSuccess_WhenRequestPendignWithSameUser() {
		FriendDTO friendDTO = new FriendDTO("user2", "REQUEST ACCEPTED", "user2@gmail.com", true);
		Mockito.when(userRepository.findByUsername(loginUser.getName())).thenReturn(loginUser);
		User user2 = new User("username2", "test2", "user2", "user2@gmail.com", "1234567890");
		user2.setId(2);
		loginUser.setId(1);
		Mockito.when(userRepository.findByEmail(friendDTO.getEmail())).thenReturn(user2);
		RelationIdentity relationIdentity = new RelationIdentity(2, 3);
		Friends friend = new Friends(relationIdentity, "REQUEST ACCEPTED", 1);
		Mockito.when(friendRepository.findByRelationIdentity(Matchers.any(RelationIdentity.class))).thenReturn(friend);
		FriendListResponse friendsResponse = friendService.saveRequest(loginUser.getName(), friendDTO);
		assertTrue(friendsResponse.isSuccess()); 
	}

	@Test
	public void testSaveRequestFailure_SameUserNoRequestPending() {
		FriendDTO friendDTO = new FriendDTO("user2", "REQUEST ACCEPTED", "user2@gmail.com", true);
		Mockito.when(userRepository.findByUsername(loginUser.getName())).thenReturn(loginUser);
		User user2 = new User("username2", "test2", "user2", "user2@gmail.com", "1234567890");
		user2.setId(2);
		loginUser.setId(1);
		Mockito.when(userRepository.findByEmail(friendDTO.getEmail())).thenReturn(user2);
		RelationIdentity relationIdentity = new RelationIdentity(2, 3);
		Friends friend = new Friends(relationIdentity, "REQUEST ACCEPTED", 2);
		Mockito.when(friendRepository.findByRelationIdentity(Matchers.any(RelationIdentity.class))).thenReturn(friend);
		FriendListResponse friendsResponse = friendService.saveRequest(loginUser.getName(), friendDTO);
		assertFalse(friendsResponse.isSuccess()); 
	}


	private void callTestData() {
		loginUser.setId(2);
		List<Friends> friends1 = new ArrayList<Friends>();
		List<Friends> friends2 = new ArrayList<Friends>();

		Friends friend1 = new Friends();
		friend1.setActionBy(2);
		friend1.setStatus("REQUEST ACCEPTED");
		RelationIdentity relationIdentity1 = new RelationIdentity(2, 3);
		friend1.setRelationIdentity(relationIdentity1);
		friends1.add(friend1);

		Friends friend3 = new Friends();
		friend3.setActionBy(4);
		friend3.setStatus("REQUEST PENDING");
		RelationIdentity relationIdentity3 = new RelationIdentity(2, 4);
		friend3.setRelationIdentity(relationIdentity3);
		friends1.add(friend3);

		Friends friend4 = new Friends();
		friend4.setActionBy(2);
		friend4.setStatus("REQUEST PENDING");
		RelationIdentity relationIdentity4 = new RelationIdentity(2, 5);
		friend4.setRelationIdentity(relationIdentity4);
		friends1.add(friend4);

		Friends friend5 = new Friends();
		friend5.setActionBy(2);
		friend5.setStatus("REQUEST PENDING");
		RelationIdentity relationIdentity5 = new RelationIdentity(2, 6);
		friend5.setRelationIdentity(relationIdentity5);
		friends1.add(friend5);

		Friends friend2 = new Friends();
		friend2.setActionBy(2);
		friend2.setStatus("REQUEST PENDING");
		RelationIdentity relationIdentity2 = new RelationIdentity(1, 2);
		friend2.setRelationIdentity(relationIdentity2);

		friends2.add(friend2);
		Mockito.when(friendRepository.findById1(2)).thenReturn(friends1);
		Mockito.when(friendRepository.findById2(2)).thenReturn(friends2);

		User user1 = new User("username2", "test2", "user2", "user2@gmail.com", "1234567890");
		user1.setId(1);
		User user2 = new User("username3", "test3", "user3", "user3@gmail.com", "2345678901");
		user2.setId(3);
		User user3 = new User("username4", "test4", "user4", "user4@gmail.com", "3456789012");
		user3.setId(4);
		User user4 = new User("username5", "test5", "user5", "user5@gmail.com", "4567890123");
		user4.setId(5);
		User user5 = new User(null, null, "user6", "user6@gmail.com", "4567890123");
		user5.setId(6);
		Mockito.when(userRepository.findById(1)).thenReturn(user1);
		Mockito.when(userRepository.findById(4)).thenReturn(user3);
		Mockito.when(userRepository.findById(5)).thenReturn(user4);
		Mockito.when(userRepository.findById(6)).thenReturn(user5);
		Mockito.when(userRepository.findById(3)).thenReturn(user2);
	}
	
	private void callTestData2() {
		loginUser.setId(2);
		List<Friends> friends1 = new ArrayList<Friends>();
		List<Friends> friends2 = new ArrayList<Friends>();
		Mockito.when(friendRepository.findById1(2)).thenReturn(friends1);
		Mockito.when(friendRepository.findById2(2)).thenReturn(friends2);
	}

	private void mockForInviteFriends1() {
		Mockito.when(userRepository.findByUsername(loginUser.getName())).thenReturn(loginUser);
		FriendDTO dto1 = new FriendDTO("sree", null, "sree@gmail.com", false);
		dtolist.add(dto1);
		User friend1 = new User("sree", "sree@gmail.com", "1234232");
		friend1.setId(2);
		Mockito.when(userRepository.findByEmail(friend1.getEmail())).thenReturn(null);
		RelationIdentity identity1 = new RelationIdentity(loginUser.getId(), friend1.getId());
		Mockito.when(friendRepository.findById(identity1)).thenReturn(null);
	}

	private void mockForInviteFriends2() {
		FriendDTO dto1 = new FriendDTO("sree", null, "sree@gmail.com", false);
		dtolist.add(dto1);
		User friend1 = new User("sree", "sree@gmail.com", "1234232");
		friend1.setId(2);
		Mockito.when(userRepository.findByEmail(friend1.getEmail())).thenReturn(friend1);
		RelationIdentity identity2 = new RelationIdentity(loginUser.getId(), friend1.getId());
		Friends friendObj2 = new Friends(identity2, ServiceConstants.REQUEST_PENDING, 1);
		Optional<Friends> friendsObj2 = Optional.of(friendObj2);
		Mockito.when(friendRepository.findById(Matchers.any(RelationIdentity.class))).thenReturn(friendsObj2);
	}
}
