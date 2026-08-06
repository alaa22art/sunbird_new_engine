package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sunbird.lis.interfaces.entities.TransactionView;

/**
 * LkpMessagesTypeRepo.java
 * 
 * @author Ala'a Himour <ahimour@optimizasolutions.com>
 * @since Mar/06/2018
 **/

@Repository("TransactionViewRepo")
public interface TransactionViewRepo extends CrudRepository<TransactionView, Long> {

	@Query("SELECT t FROM TransactionView t")
	List<TransactionView> getMachineTransaction();

}
