package com.certacure.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.certacure.lis.interfaces.entities.MachineOrder;
import com.certacure.lis.interfaces.entities.OutboundErrorEmailEntity;

@Repository("OutboundErrorEmailRepo")
public interface OutboundErrorEmailRepo extends GenericRepository<OutboundErrorEmailEntity> {

   
    @Query("select e from OutboundErrorEmailEntity e where e.isSent = false and e.isFailed = false")
    public List<OutboundErrorEmailEntity> getAllOutboundErrorEmails();

    
    @Modifying
    @Query("update OutboundErrorEmailEntity e set e.isSent =:isSent where e.rid =:rid")
	public void updateIsSentFlag(@Param("rid") String rid, @Param("isSent") boolean isSent);
    

}
