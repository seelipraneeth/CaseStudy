package com.spotflock.casestudy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotflock.casestudy.dto.FriendDTO;
import com.spotflock.casestudy.model.Friends;
import com.spotflock.casestudy.model.RelationIdentity;
import com.spotflock.casestudy.model.User;
import com.spotflock.casestudy.repository.FriendRepository;
import com.spotflock.casestudy.repository.UserRepository;
import com.spotflock.casestudy.response.FriendListResponse;
import com.spotflock.casestudy.util.ServiceConstants;

/**
 * @author Praneeth
 *
 */

@Service
public class FriendService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FriendRepository friendsRepository;
	
	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(FriendService.class);

	public FriendListResponse inviteFriends(String username, List<FriendDTO> friendDTOs) {
		log.debug("FriendService: Inviting friends for username: "+username);
		User user = userRepository.findByUsername(username);
		int actionBy = user.getId();

		List<Friends> friends = new ArrayList<Friends>();
		FriendListResponse response = new FriendListResponse();

		for (FriendDTO eachRequest : friendDTOs) {
			Friends friendRequest = prepareFriendRequestObj(eachRequest, actionBy);
			if (null != friendRequest) {
				friends.add(friendRequest);
			} else {
				response.setMessage(
						response.getMessage() + "\n" + ServiceConstants.REQUEST_EXISTS + eachRequest.getName());
			}
		}

		if (friends.size() > 0) {
			friendsRepository.saveAll(friends);
			response.setSuccess(true);
		}
		return response;
	}

	public FriendListResponse displayFriends(String username) {
		log.debug("FriendService: display friends for username: "+username);
		int userid = userRepository.findByUsername(username).getId();
		return prepareDisplayResponse(userid);
	}

	public FriendListResponse saveRequest(String username, FriendDTO friendDTO) {
		log.debug("FriendService: save friend request for username: "+username);

		int userid = userRepository.findByUsername(username).getId();
		int friendid = userRepository.findByEmail(friendDTO.getEmail()).getId();

		RelationIdentity relationIdentity = new RelationIdentity(userid, friendid);
		if (userid > friendid) {
			relationIdentity.setId1(friendid);
			relationIdentity.setId2(userid);
		}

		Friends friend = friendsRepository.findByRelationIdentity(relationIdentity);
		FriendListResponse response = new FriendListResponse();

		if (friend.getActionBy() == userid && !ServiceConstants.REQUEST_PENDING.equalsIgnoreCase(friend.getStatus())) {

			friend.setStatus(friendDTO.getStatus().toUpperCase());
			friendsRepository.save(friend);
			response.setMessage(ServiceConstants.REQUEST_STATUS_UPDATED);
			response.setSuccess(true);

		} else if (friend.getStatus().equalsIgnoreCase(friendDTO.getStatus())) {

			response.setMessage(ServiceConstants.REQUEST_STATUS_NOTUPDATED);
			response.setSuccess(false);

		}
		return response;
	}

	private FriendListResponse prepareDisplayResponse(int userid) {
		log.debug("FriendService: prepare display response for userid : "+userid);

		List<Friends> friendsList1 = (List<Friends>) friendsRepository.findById1(userid);
		List<Friends> friendsList2 = (List<Friends>) friendsRepository.findById2(userid);

		List<FriendDTO> displayFriendDTOs = retrieveFriendsForUserId(friendsList1);
		displayFriendDTOs = retrieveFriendsForUserId(friendsList2, displayFriendDTOs);
		FriendListResponse friendListResponse = new FriendListResponse(true, null, displayFriendDTOs);
		if(displayFriendDTOs.size()>0) {
			friendListResponse.setMessage(ServiceConstants.DISPLAY);
		} else {
				friendListResponse.setMessage(ServiceConstants.NO_REUSLTS_FOUND);
		}
		return friendListResponse; 
	}

	/**
	 * To retrieve friends from database for UserId1 Add those friends to a list
	 * 
	 * @param friendsList2
	 * @param displayFriendDTOs
	 */
	private List<FriendDTO> retrieveFriendsForUserId(List<Friends> friendsList1) {
		List<FriendDTO> displayFriendDTOs = new ArrayList<FriendDTO>();
		for (Friends eachFriend : friendsList1) {
			User user = userRepository.findById(eachFriend.getRelationIdentity().getId2());
			displayFriendDTOs.add(processFriendRequests(eachFriend, user));
		}
		return displayFriendDTOs;
	}

	/**
	 * To retrieve friends from database for UserId2
	 * 
	 * @param friendsList1
	 */
	private List<FriendDTO> retrieveFriendsForUserId(List<Friends> friendsList2, List<FriendDTO> displayFriendDTOs) {
		for (Friends eachFriend : friendsList2) {
			User user = userRepository.findById(eachFriend.getRelationIdentity().getId1());
			displayFriendDTOs.add(processFriendRequests(eachFriend, user));
		}
		return displayFriendDTOs;
	}

	/**
	 * process friend request for each friend added by a particular user
	 * @param eachFriend
	 * @param user
	 * @return
	 */
	private FriendDTO processFriendRequests(Friends eachFriend, User user) {

		FriendDTO displayFriendDTO = new FriendDTO();
		displayFriendDTO.setName(user.getName());
		displayFriendDTO.setEmail(user.getEmail());

		if (ServiceConstants.REQUEST_PENDING.equals(eachFriend.getStatus())) {
			if (eachFriend.getActionBy() == user.getId()) {
				displayFriendDTO.setStatus(eachFriend.getStatus() + " (BY YOU)");
			} else {
				displayFriendDTO.setStatus(eachFriend.getStatus() + " (BY YOUR FRIEND)");
			}
		} else {
			displayFriendDTO.setStatus(eachFriend.getStatus());
		}

		if (null != user.getPassword())
			displayFriendDTO.setRegisteredUser(true);
		return displayFriendDTO;
	}

	/**
	 * prepare friend request object for given friend request, and action by a particular friend 
	 * @param eachRequest
	 * @param actionBy
	 * @return
	 */
	private Friends prepareFriendRequestObj(FriendDTO eachRequest, int actionBy) {
		log.info("FriendsService: prepare friends request object for each dto object");
		User user = userRepository.findByEmail(eachRequest.getEmail());
		if (null == user) {
			user = new User(eachRequest.getName(), eachRequest.getEmail(), eachRequest.getEmail());
			userRepository.save(user);
		}
		int currentUserId = user.getId();

		int userid1 = currentUserId > actionBy ? actionBy : currentUserId;
		int userid2 = currentUserId < actionBy ? actionBy : currentUserId;

		RelationIdentity relationIdentity = new RelationIdentity(userid1, userid2);

		Optional<Friends> friendsList = friendsRepository.findById(relationIdentity);
		try {
			friendsList.get();
			return null;
		} catch (NoSuchElementException e) {
			return new Friends(relationIdentity, ServiceConstants.REQUEST_PENDING, actionBy);
		}
	}
}
