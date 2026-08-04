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
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.repo.ElegabalityApprovalOrderRepo;
import com.certacure.lis.interfaces.repo.ElegabalityApprovalRepo;

@Service("ElegabalityApprovalOrderService")
public class ElegabalityApprovalOrderService extends GenericService<ElegabalityApprovalOrderEntity, ElegabalityApprovalOrderRepo> {

    @Autowired
    private ElegabalityApprovalOrderRepo repo;
    
    @Autowired
    private PostDetailFinancialTransactionService pdfts;

    @Override
    protected ElegabalityApprovalOrderRepo getRepository() {
        return repo;
    }

    @InterceptorFree
    public List<ElegabalityApprovalOrderEntity> getAllPendingElegablityApprovalOrder() {
        return getRepository().getNotSentElegabalityApprovalOrderRecords();
    }
    
    public ElegabalityApprovalOrderEntity updateElegablityApprovalOrder(ElegabalityApprovalOrderEntity elegabalityApprovalOrderEntity) {
        return getRepository().save(elegabalityApprovalOrderEntity);
    }
    
    
    public ElegabalityApprovalOrderEntity setElegabalityApprovalOrderAsSent(Long elegabilityApprovalOrderRid) throws Exception {
    	
    	ElegabalityApprovalOrderEntity elegabalityApprovalOrderEntity = getRepository().findById(elegabilityApprovalOrderRid).orElse(null);
    	
    	
    	if (elegabalityApprovalOrderEntity ==null)
    	{
//			throw new Exception("Elegability Approval Record Not Found");
    		System.out.println("Elegability Approval Record Not Found");
    		return null;///add negative message transaction no ACK message trans existed
		}else 
		{
			elegabalityApprovalOrderEntity.setIsSent(true);
			elegabalityApprovalOrderEntity.setIsSuccess(true);
			elegabalityApprovalOrderEntity.setIsFailed(false);
			elegabalityApprovalOrderEntity.setNotes("SUCCESS");

	    
		}
    	
    	
    	
        return getRepository().save(elegabalityApprovalOrderEntity);
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
    
    public ElegabalityApprovalOrderEntity createElegabalityOrderRecord(ElegabalityApprovalOrderEntity elegabalityOrderEntity) {
    try {
    	
    	elegabalityOrderEntity.setIsSuccess(false);
    	elegabalityOrderEntity.setIsSent(false);
    	elegabalityOrderEntity.setIsFailed(false);
    	
        
        
    /* List<PostDetailFinancialTransaction> listPdft = pdfts.getOrdersByCertaOrderIdAndActionItem(elegabalityOrderEntity
    		 .getOrderId().toString(), 
    		 elegabalityOrderEntity.getOrderActionId().toString());
     
     if(listPdft.size() > 0)
     {
    	 elegabalityOrderEntity.setVistaOrderID(listPdft.get(0).getVistaOrderID());
    	 elegabalityOrderEntity.setAttendingDoctorID(listPdft.get(0).getAttendingDoctorID());
    	 elegabalityOrderEntity.setAssignedPatientLocation(listPdft.get(0).getAssignedPatientLocation());
    	 
    	 if(elegabalityOrderEntity.getAppointment_id() == null)
    	 {
    		 elegabalityOrderEntity.setAppointment_id(elegabalityOrderEntity.getVisitInfoId());
    	 }
     }*/
        
    	getRepository().save(elegabalityOrderEntity);
    	
    	return elegabalityOrderEntity;
    	
    }catch (Exception e) 
    {
    	return null;
	}
    	
    	
    
    }
    
    
public ElegabalityApprovalOrderEntity createElegabalityAppointmentRecord(ElegabalityApprovalOrderEntity elegabalityAppOrderEntity) {
    
    	
	elegabalityAppOrderEntity.setIsSuccess(false);
	elegabalityAppOrderEntity.setIsFailed(false);
	elegabalityAppOrderEntity.setIsSent(false);
    	
    	 
    	/* if(elegabalityAppOrderEntity.getAppointment_id() == null)
    	 {
    		 elegabalityAppOrderEntity.setAppointment_id(elegabalityAppOrderEntity.getVisitInfoId());
    	 }*/
        
    	return getRepository().save(elegabalityAppOrderEntity);
    }


	public void updateIsSent(String orderID , boolean isSent) {
		getRepository().updateIsSentFlag(orderID,isSent);
		
	}

	 @InterceptorFree
	    public List<ElegabalityApprovalOrderEntity> getAllPendingElegablityApproval() {
	        return getRepository().getNotSentElegabalityApprovalOrderRecords();
	    }

	public ElegabalityApprovalOrderEntity save(ElegabalityApprovalOrderEntity elegOrderObj) {
		
		return getRepository().save(elegOrderObj);
		
	}


}
