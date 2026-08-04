package com.certacure.lis.interfaces.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certacure.core.base.service.GenericService;
import com.certacure.lis.interfaces.annotation.InterceptorFree;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.repo.PostDetailFinancialTransactionRepo;

@Service("PostDetailFinancialTransactionService")
public class PostDetailFinancialTransactionService
    extends GenericService<PostDetailFinancialTransaction, PostDetailFinancialTransactionRepo> {

    @Override
    protected PostDetailFinancialTransactionRepo getRepository() {
        // TODO Auto-generated method stub
        return null;
    }

    @Autowired
    private PostDetailFinancialTransactionRepo repo;

    @InterceptorFree
    public PostDetailFinancialTransaction add(
        PostDetailFinancialTransaction postDetailFinancialTransaction) {
        postDetailFinancialTransaction= repo.save(postDetailFinancialTransaction);

        return postDetailFinancialTransaction;
    }

    @InterceptorFree
    public List<PostDetailFinancialTransaction> getOrdersByCertaOrderIdAndActionItem(String certaOrderID , String certaActionId) {
        // TODO Auto-generated method stub
        return repo.getOrdersByCertaOrderAndAction(certaOrderID , certaActionId);
    }
    
    
    

    @InterceptorFree
	public List<PostDetailFinancialTransaction> getOrdersByVistaOrderId(String vistaOrderID) {
		// TODO Auto-generated method stub
		 return repo.getOrdersByVistaOrderId(vistaOrderID);
	}

	public  List<PostDetailFinancialTransaction>  getOrdersByCertaOrderIdAndActionAndItem(Long certaOrderId, Long certaActionId,Long itemCode)
	{
			return repo.getOrdersByCertaOrderIdAndActionAndItem(certaOrderId.toString() , certaActionId.toString() , itemCode.toString());
	}
	
	@InterceptorFree
	public List<PostDetailFinancialTransaction>  getOrdersByAppointmentId(String appointmentId) {
		return repo.getOrdersByAppointmentId(appointmentId);
	}

	@InterceptorFree
	public List<PostDetailFinancialTransaction>  getOrdersByAppointmentAndId(String appointmentID, String vistaOrderID) 
	{
				
		return repo.getOrdersByAppointmentId(appointmentID, vistaOrderID);
			
	}
   

}
