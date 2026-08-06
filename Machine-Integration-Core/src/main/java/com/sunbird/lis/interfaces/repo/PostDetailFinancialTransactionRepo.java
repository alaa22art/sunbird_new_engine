package com.sunbird.lis.interfaces.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.lis.interfaces.entities.PostDetailFinancialTransaction;
@Repository("PostDetailFinancialTransactionRepo")
public interface PostDetailFinancialTransactionRepo
    extends GenericRepository<PostDetailFinancialTransaction> {

  
    
    @Query("select pdft from PostDetailFinancialTransaction pdft " +
        "WHERE pdft.orderByID = :certaOrderID AND pdft.RCMOrderActionID = :certaActionID ORDER BY pdft.rid DESC")
    List<PostDetailFinancialTransaction> getOrdersByCertaOrderAndAction(@Param("certaOrderID") String certaOrderID , 
    		@Param("certaActionID") String certaActionID);

    @Query("select pdft from PostDetailFinancialTransaction pdft " +
            "WHERE pdft.vistaOrderID = :vistaOrderID ")
	List<PostDetailFinancialTransaction> getOrdersByVistaOrderId(@Param("vistaOrderID") String vistaOrderID);

    
    @Query("select pdft from PostDetailFinancialTransaction pdft "
    		+ "WHERE pdft.orderByID = :certaOrderID AND pdft.RCMOrderActionID = :certaActionID AND pdft.RCMItemCode = :itemCode")
	List<PostDetailFinancialTransaction> getOrdersByCertaOrderIdAndActionAndItem(
			@Param("certaOrderID") String certaOrderID, 
			@Param("certaActionID") String certaActionID,
			@Param("itemCode") String itemCode);
    
    @Query("select pdft from PostDetailFinancialTransaction pdft " +
            "WHERE pdft.appointmentID = :appointmentId ")
	List<PostDetailFinancialTransaction> getOrdersByAppointmentId(@Param("appointmentId") String appointmentId);
    

    @Query("select pdft from PostDetailFinancialTransaction pdft " +
            "WHERE pdft.appointmentID = :appointmentId AND pdft.vistaOrderID = :vistaOrderID")
	List<PostDetailFinancialTransaction> getOrdersByAppointmentId(@Param("appointmentId") String appointmentId
			, @Param("vistaOrderID") String vistaOrderID);
	
}
