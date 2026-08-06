package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;
import com.sunbird.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.sunbird.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.sunbird.lis.interfaces.entities.MachineOrder;

@Repository("ElegabalityApprovalOrderRepo")
public interface ElegabalityApprovalOrderRepo extends GenericRepository<ElegabalityApprovalOrderEntity> {

   
    @Query("select e from ElegabalityApprovalOrderEntity e where e.isSent = false and e.isFailed = false")
    public List<ElegabalityApprovalOrderEntity> getNotSentElegabalityApprovalOrderRecords();

    
    @Modifying
    @Query("update ElegabalityApprovalOrderEntity e set e.isSent =:isSent where e.OrderId =:OrderID")
	public void updateIsSentFlag(@Param("OrderID") String OrderID, @Param("isSent") boolean isSent);
    
   
    @Query(value = "SELECT nextval('ack_message_control_id_seq')", nativeQuery =
            true)
    Long getNextAckMessageControlIdSeriesl();



}
