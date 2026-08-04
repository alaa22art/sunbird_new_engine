package com.certacure.lis.interfaces.repo;

import java.util.List;
import java.util.Set;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certacure.core.base.repo.GenericRepository;
import com.certacure.lis.interfaces.entities.MachineOrder;

/**

 */
@Repository("MachineOrderRepo")
public interface MachineOrderRepo extends GenericRepository<MachineOrder> {

	public List<MachineOrder> findTop1ByBarcodeOrderByRidDesc(String barcode);

	@Query("select  t from MachineOrder t where t.barcode = :barcode and tenantId = :tenantId and branchId = :branchId and t.resultReceived = false")
	public List<MachineOrder> getBySample(@Param("barcode") String barcode, @Param("tenantId") Long tenantId,
			@Param("branchId") Long branchId);

	//get pcr real time order, for now leave the panel_code constant as COV-19
	@Query("select t from MachineOrder t where t.barcode = :barcode and t.panelCode = :panelCode")
	public List<MachineOrder> getPcrRealTimeOrderBySampleAndPanelCode(@Param("barcode") String barcode,
			@Param("panelCode") String panelCode);

	@Query("select  t from MachineOrder t where t.barcode = :barcode and tenantId = :tenantId and branchId = :branchId and t.resultReceived = false group by t.panelCode , t.rid ORDER BY t.rid DESC")
	public List<MachineOrder> getByPanelCode(@Param("barcode") String barcode, @Param("tenantId") Long tenantId,
			@Param("branchId") Long branchId);

	@Query("select  t.barcode , t.patientFirstName , t.patientLastName , t.patientId , t.dateOfBirth , t.gender  , t.panelCode from MachineOrder t where t.panelCode = :panelCode and tenantId = :tenantId and branchId = :branchId and t.isSentToMachine = false group by  t.panelCode, t.barcode , t.patientFirstName , t.patientLastName , t.patientId , t.dateOfBirth , t.gender , t.panelCode ORDER BY t.barcode DESC")
	public List<Tuple> getAllByPanelCode(@Param("panelCode") String barcode, @Param("tenantId") Long tenantId,
			@Param("branchId") Long branchId);

	@Query("select t from MachineOrder t where t.barcode = :barcode and t.testCode = :testCode and tenantId = :tenantId and branchId = :branchId order by t.rid desc")
	public List<MachineOrder> getBySampleAndTestCode(@Param("barcode") String barcode, @Param("testCode") String testCode,
			@Param("tenantId") Long tenantId, @Param("branchId") Long branchId);

	@Query("select t from MachineOrder t where t.barcode = :barcode and resultReceived = false")
	public List<MachineOrder> getBySampleAndResultNotReceived(@Param("barcode") String barcode);

	@Query("select t from MachineOrder t where t.barcode = :barcode and isSentToMachine = false")
	public List<MachineOrder> getBySampleAndNewTestOnly(@Param("barcode") String barcode);

	@Query("select t from MachineOrder t where t.barcode = :barcode and isSentToMachine = false and resultReceived = false")
	public List<MachineOrder> getBySampleAndNewTestOnlyAndResultNotReceived(@Param("barcode") String barcode);

	//	@Query("SELECT DISTINCT moq FROM MachineOrderQueryResponse moq "
	//			+ " LEFT JOIN FETCH moq.machineOrderId mo "
	//			+ " LEFT JOIN FETCH moq.machineQueryId mq "
	//			+ " LEFT JOIN FETCH mq.machine m "
	//			+ " LEFT JOIN FETCH mq.machineResultList mr "
	//			+ " WHERE "
	//			+ " mo.rid = :rid ")
	//	Set<MachineOrderQueryResponse> findOrderQueryResponseByBarcode(@Param("rid") Long rid);

	@Query("SELECT mo FROM MachineOrder mo"
			+ " LEFT JOIN FETCH mo.machineOrderQueryResponseList moq "
			+ " LEFT JOIN FETCH moq.machineQueryId mq "
			//			+ " LEFT JOIN FETCH mq.machine m "
			+ " LEFT JOIN FETCH mq.machineResultList mr "
			+ " WHERE "
			+ " mo.barcode = :barcode and mo.testCode = mr.testCode")
	Set<MachineOrder> findOrderQueryResponseByBarcode(@Param("barcode") String barcode);

	@Query("SELECT mo FROM MachineOrder mo"
			+ " LEFT JOIN FETCH mo.machineOrderQueryResponseList moq "
			+ " WHERE "
			+ " mo.barcode = :barcode")
	Set<MachineOrder> findOrderQuery(@Param("barcode") String barcode);

	@Query(value = "SELECT DISTINCT mo.barcode FROM MachineOrder mo "
			+ "WHERE "
			+ "LOWER(mo.barcode) LIKE CONCAT('%',LOWER(CAST(:barcode AS java.lang.String)),'%') "
			+ "GROUP BY mo.barcode ", countQuery = "SELECT COUNT(DISTINCT mo.barcode) FROM MachineOrder mo"
					+ " WHERE "
					+ " LOWER(mo.barcode) LIKE CONCAT('%',LOWER(CAST(:barcode AS java.lang.String)),'%') GROUP BY mo.barcode ")
	Page<String> findMachineOrdersByBarcode(@Param("barcode") String barcode, Pageable page);

	@Query("select t from MachineOrder t where t.barcode = :barcode and isSentToMachine = false and panelCode = :panelCode and tenantId = :tenantId and branchId = :branchId order by t.rid desc")
	public List<MachineOrder> getSampleByPanelCode(String barcode, String panelCode, Long tenantId, Long branchId);

	@Modifying
	@Query("UPDATE MachineOrder mo SET mo.isSentToMachine = true  where mo.barcode = :barcode  and mo.panelCode = :panelCode and mo.tenantId = :tenantId and mo.branchId = :branchId")
	public void updateAllOrdersIsSentToMachine(String barcode, String panelCode, boolean b, Long tenantId, Long branchId);

}
