package com.certacure.lis.interfaces.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.certacure.core.base.entity.BaseAuditableBranchedEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name= "mw_stock")
public class StockEntity extends BaseAuditableBranchedEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	   @Id
	    @Basic(optional= false)
	    @GeneratedValue(strategy= GenerationType.IDENTITY)
	    @Column(name= "rid")
	    private Long rid;
	   
	   
	   
	   	@Column(name = "json_body", nullable = false)
	    @JsonProperty("jsonBody")
	    private String jsonBody;

	    @Column(name = "source", nullable = false)
	    @JsonProperty("source")
	    private String source;

	    @Column(name = "destination", nullable = false)
	    @JsonProperty("destination")
	    private String destination;


	    @Column(name = "operation_type")
	    @JsonProperty("operationType")
	    private String operationType;
	    
	    
	    public StockEntity() {
	    	
	    }
	    
	    
	   	    
	

	public String getJsonBody() {
			return jsonBody;
		}





		public void setJsonBody(String jsonBody) {
			this.jsonBody = jsonBody;
		}





		public String getSource() {
			return source;
		}





		public void setSource(String source) {
			this.source = source;
		}





		public String getDestination() {
			return destination;
		}





		public void setDestination(String destination) {
			this.destination = destination;
		}





		public String getOperationType() {
			return operationType;
		}





		public void setOperationType(String operationType) {
			this.operationType = operationType;
		}





	@Override
	public Long getRid() {
		// TODO Auto-generated method stub
		return rid;
	}

}
