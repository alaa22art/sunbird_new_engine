package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.certacure.lis.interfaces.entities.MachineOrder;

@Repository("ElegabalityApprovalRepo")
public interface ElegabalityApprovalRepo extends GenericRepository<ElegabalityApprovalEntity> {

   
    @Query("select e from ElegabalityApprovalEntity e where e.isSent = false and e.isFailed = false")
    public List<ElegabalityApprovalEntity> getNotSentElegabalityApprovalRecords();

    
    @Modifying
    @Query("update ElegabalityApprovalEntity e set e.isSent =:isSent where e.OrderId =:OrderID")
	public void updateIsSentFlag(@Param("OrderID") String OrderID, @Param("isSent") boolean isSent);
    
   
    @Query(value = "SELECT nextval('ack_message_control_id_seq')", nativeQuery =
            true)
    Long getNextAckMessageControlIdSeriesl();


    @Query("select e from ElegabalityApprovalEntity e where e.isSent = false  and e.isAppointment = true")
	public List<ElegabalityApprovalEntity> getAllAppointmentApproval();
    
    

}
