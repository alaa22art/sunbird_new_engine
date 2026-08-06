package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.PCRRealTimeWorkListOrder;

@Repository("PCRRealTimeWorkListOrderRepo")
public interface PCRRealTimeWorkListOrderRepo extends GenericRepository<PCRRealTimeWorkListOrder> {

	@Query("SELECT wo FROM PCRRealTimeWorkListOrder wo Where wo.pcrRealTimeWorkList.rid = :worklistId")
	public List<PCRRealTimeWorkListOrder> getPcrRealTimeWorkListOrders(@Param("worklistId") Long worklistId);

}
