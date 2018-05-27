package com.spotflock.casestudy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.spotflock.casestudy.model.Friends;
import com.spotflock.casestudy.model.RelationIdentity;

@RepositoryRestResource
public interface FriendRepository extends JpaRepository<Friends, RelationIdentity> {

	Friends findByRelationIdentity(RelationIdentity compositeid);

	Friends findByStatus(String status);

	Friends findByActionBy(int id);

	@Query(value = "select * from friends f where f.id1= :id1", nativeQuery = true)
	List<Friends> findById1(@Param("id1") int id1);

	@Query(value = "select * from friends f where f.id2= :id2", nativeQuery = true)
	List<Friends> findById2(@Param("id2") int id2);
}
