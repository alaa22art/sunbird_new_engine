package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.LkpMessagesType;

/**
 * LkpMessagesTypeRepo.java
 * 
 **/

@Repository("LkpMessagesTypeRepo")
public interface LkpMessagesTypeRepo extends GenericRepository<LkpMessagesType> {

	@Query("select lmt from LkpMessagesType lmt")
	public List<LkpMessagesType> messagesTypeSearch();

}
