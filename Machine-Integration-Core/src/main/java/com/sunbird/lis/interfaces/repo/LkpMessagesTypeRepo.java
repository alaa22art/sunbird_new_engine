package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.LkpMessagesType;

/**
 * LkpMessagesTypeRepo.java
 * 
 **/

@Repository("LkpMessagesTypeRepo")
public interface LkpMessagesTypeRepo extends GenericRepository<LkpMessagesType> {

	@Query("select lmt from LkpMessagesType lmt")
	public List<LkpMessagesType> messagesTypeSearch();

}
