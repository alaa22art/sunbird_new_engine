package com.certacure.lis.interfaces.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
*

* 
*/
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.certacure.lis.interfaces.entities.OutboundErrorEmailEntity;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.repo.ElegabalityApprovalOrderRepo;
import com.certacure.lis.interfaces.repo.ElegabalityApprovalRepo;
import com.certacure.lis.interfaces.repo.OutboundErrorEmailRepo;

@Service("OutboundErrorEmailService")
public class OutboundErrorEmailService extends GenericService<OutboundErrorEmailEntity, OutboundErrorEmailRepo> {

    @Autowired
    private OutboundErrorEmailRepo repo;

    @Override
    protected OutboundErrorEmailRepo getRepository() {
        return repo;
    }

    @InterceptorFree
    public List<OutboundErrorEmailEntity> getAllErrorEmail() {
        return getRepository().getAllOutboundErrorEmails();
    }
    
    public OutboundErrorEmailEntity updateEmailError(OutboundErrorEmailEntity outboundErrorEmailEntity) {
        return getRepository().save(outboundErrorEmailEntity);
    }
    
    
    public OutboundErrorEmailEntity setElegabalityApprovalOrderAsSent(Long emailErrorRid) throws Exception {
    	
    	OutboundErrorEmailEntity outboundErrorEmailEntity = getRepository().findById(emailErrorRid).orElse(null);
    	
    	
    	if (outboundErrorEmailEntity ==null)
    	{
//			throw new Exception("Elegability Approval Record Not Found");
    		System.out.println("Email Error Record Not Found");
    		return null;///add negative message transaction no ACK message trans existed
		}else 
		{
			outboundErrorEmailEntity.setIsSent(true);
			outboundErrorEmailEntity.setIsSuccess(true);
			outboundErrorEmailEntity.setIsFailed(false);
			outboundErrorEmailEntity.setNotes("SUCCESS");

	    
		}
    	
    	
    	
        return getRepository().save(outboundErrorEmailEntity);
    }


    
    
    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t= ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t= t.getCause();
                }
            }
        }
    }
    
    public OutboundErrorEmailEntity createEmailError(OutboundErrorEmailEntity outboundErrorEmailEntity) {
    try {
    	
    	outboundErrorEmailEntity.setIsSuccess(false);
    	outboundErrorEmailEntity.setIsSent(false);
    	outboundErrorEmailEntity.setIsFailed(false);
    	        
    	getRepository().save(outboundErrorEmailEntity);
    	
    	return outboundErrorEmailEntity;
    	
    }catch (Exception e) 
    {
    	return null;
	}
    	
    	
    
    }
    
    
public OutboundErrorEmailEntity createNewErrorEmailRecord(OutboundErrorEmailEntity outboundErrorEmailEntity) {
    
    	
	outboundErrorEmailEntity.setIsSuccess(false);
	outboundErrorEmailEntity.setIsFailed(false);
	outboundErrorEmailEntity.setIsSent(false);
    	
        
    	return getRepository().save(outboundErrorEmailEntity);
    }


	public void updateIsSent(String orderID , boolean isSent) {
		getRepository().updateIsSentFlag(orderID,isSent);
		
	}

	 @InterceptorFree
	    public List<OutboundErrorEmailEntity> getAllOutboundErrorEmail() {
	        return getRepository().getAllOutboundErrorEmails();
	    }

	public OutboundErrorEmailEntity save(OutboundErrorEmailEntity outboundErrorEmailEntity) {
		
		return getRepository().save(outboundErrorEmailEntity);
		
	}


}
