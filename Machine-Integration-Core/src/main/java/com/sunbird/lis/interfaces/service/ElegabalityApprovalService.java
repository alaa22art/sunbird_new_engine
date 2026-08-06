package com.sunbird.lis.interfaces.service;

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

import com.sunbird.core.base.service.GenericService;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.sunbird.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.sunbird.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.sunbird.lis.interfaces.repo.ElegabalityApprovalRepo;

@Service("ElegabalityApprovalService")
public class ElegabalityApprovalService extends GenericService<ElegabalityApprovalEntity, ElegabalityApprovalRepo> {

    @Autowired
    private ElegabalityApprovalRepo repo;
    
    @Autowired
    private PostDetailFinancialTransactionService pdfts;

    @Override
    protected ElegabalityApprovalRepo getRepository() {
        return repo;
    }

    @InterceptorFree
    public List<ElegabalityApprovalEntity> getAllPendingElegablityApproval() {
        return getRepository().getNotSentElegabalityApprovalRecords();
    }
    
    public ElegabalityApprovalEntity updateElegablityApproval(ElegabalityApprovalEntity elegabalityApprovalEntity) {
        return getRepository().save(elegabalityApprovalEntity);
    }
    
    
    public ElegabalityApprovalEntity setElegabalityApprovalAsSent(Long elegabilityApprovalRid) throws Exception {
    	
    	
    	
    	
    	
    	ElegabalityApprovalEntity elegabalityApprovalEntity = getRepository().findById(elegabilityApprovalRid).orElse(null);
    	
    	elegabalityApprovalEntity.setIsSent(true);
    	elegabalityApprovalEntity.setIsSuccess(true);
    	elegabalityApprovalEntity.setIsFailed(false);
    	
    
    
    	
        return getRepository().save(elegabalityApprovalEntity);
    }



//  @InterceptorFree
//  public List<ElegabalityApprovalEntity> getAllPendingElegablityApproval() {
//       String url = "jdbc:postgresql://localhost:5432/certacure_middleware2";
//          String user = "postgres";
//          String password = "root";
//          
//          String FETCH_ELEGABLITYRECORDS_SQL = "SELECT * FROM mw_elegabality_approval_inbound_message";
//
//          List<ElegabalityApprovalEntity> elegabalityApprovalEntities = new ArrayList<>();
//
//          try (Connection connection = DriverManager.getConnection(url, user, password);
//               PreparedStatement preparedStatement = connection.prepareStatement(FETCH_ELEGABLITYRECORDS_SQL)) {
//
//              System.out.println(preparedStatement);
//
//              // Execute the query
//              try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                  // Process the results
//                  while (resultSet.next()) {
//                      // Assuming you have a constructor for ElegabalityApprovalEntity that accepts ResultSet
//                      ElegabalityApprovalEntity elegabalityApprovalEntity = new ElegabalityApprovalEntity(resultSet);
//                      elegabalityApprovalEntities.add(elegabalityApprovalEntity);
//                  }
//              }
//
//          } catch (SQLException e) {
//              // print SQL exception information
//              printSQLException(e);
//          }
//
//          return elegabalityApprovalEntities;
//  }
    
    
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
    
    
    
public ElegabalityApprovalEntity createElegabalityAppointmentRecord(ElegabalityApprovalEntity elegabalityApprovalEntity) {
    
    	
    	elegabalityApprovalEntity.setIsSuccess(false);
    	elegabalityApprovalEntity.setIsSent(false);
    	elegabalityApprovalEntity.setIsFailed(false);
    	
    	elegabalityApprovalEntity.setJsonBody(elegabalityApprovalEntity.toString());
    	
        
    	return getRepository().save(elegabalityApprovalEntity);
    }


	public void updateIsSent(String orderID , boolean isSent) {
		getRepository().updateIsSentFlag(orderID,isSent);
		
	}

	public List<ElegabalityApprovalEntity> getAllPendingElegablityAppointmentApproval() {
		  return getRepository().getAllAppointmentApproval();
	}
    
}
