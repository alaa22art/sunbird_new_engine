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
import com.sunbird.lis.interfaces.entities.OutboundErrorEmailEntity;

@Repository("OutboundErrorEmailRepo")
public interface OutboundErrorEmailRepo extends GenericRepository<OutboundErrorEmailEntity> {

   
    @Query("select e from OutboundErrorEmailEntity e where e.isSent = false and e.isFailed = false")
    public List<OutboundErrorEmailEntity> getAllOutboundErrorEmails();

    
    @Modifying
    @Query("update OutboundErrorEmailEntity e set e.isSent =:isSent where e.rid =:rid")
	public void updateIsSentFlag(@Param("rid") String rid, @Param("isSent") boolean isSent);
    

}
