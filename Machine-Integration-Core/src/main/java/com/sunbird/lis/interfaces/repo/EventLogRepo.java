/**
 * 
 */
package com.certacure.lis.interfaces.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.CoreEventLog;
import com.certacure.lis.interfaces.entities.MachineTest;

/**

 */
@Repository("EventLogRepo")
public interface EventLogRepo extends GenericRepository<CoreEventLog> {

	@Modifying
	@Query(value = "INSERT INTO CoreEventLog ('created_by', 'creation_date', 'details', 'event', 'event_type','sent_date', 'source', 'status_id') VALUES (1,?1,?2,?3,?4,?5,?6,?7)", nativeQuery = true)
	void addCoreEventLog(Date creationDate, String detail, String event, Integer eventType, Date sentDate, String source, Integer statusId);

	@Query("select mt from CoreEventLog mt LEFT JOIN FETCH  mt.machine tc")
	public List<MachineTest> getEventLogList();

}
