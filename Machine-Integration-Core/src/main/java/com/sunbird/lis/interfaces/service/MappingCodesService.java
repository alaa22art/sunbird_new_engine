package com.sunbird.lis.interfaces.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.CheckInEntity;
import com.sunbird.lis.interfaces.entities.MappingCodes;
import com.sunbird.lis.interfaces.entities.TestCatalog;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;
import com.sunbird.lis.interfaces.repo.CheckInRepo;
import com.sunbird.lis.interfaces.repo.MappingCodesRepo;

@Service("MappingCodesService")
public class MappingCodesService extends GenericService<MappingCodes, MappingCodesRepo> {
	
	
	@Autowired
	private MappingCodesRepo repo;

	@Override
	protected MappingCodesRepo getRepository() {
		// TODO Auto-generated method stub
		return repo;
	}
	
//	public MappingCodes getMappingCodesById(Long rId) {
//
//		List<MappingCodes> mappingCodesList = getRepository().find(Arrays.asList(new SearchCriterion("rid", rId, FilterOperator.eq)),
//				MappingCodes.class);
//
//		return mappingCodesList.get(0);
//	}
	
//	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TEST_CATALOG + "')")
	public Page<MappingCodes> getMappingCodesPage(FilterablePageRequest filterablePageRequest) {

		Page<MappingCodes> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				MappingCodes.class);

		return page;
	}

	public MappingCodes addMappingCode(MappingCodes mappingCodes) {
		return getRepository().save(mappingCodes);
		
	}
	
//	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_TEST_CATALOG + "')")
	public MappingCodes getMappingCodesById(Long rId) {

		List<MappingCodes> tempMappingCodesList = getRepository().find(Arrays.asList(new SearchCriterion("rid", rId, FilterOperator.eq)),
				MappingCodes.class);

		return tempMappingCodesList.get(0);
	}
	
//	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_TEST_CATALOG + "')")
	public MappingCodes updateMappingCodes(MappingCodes mappingCodes) {

		if (mappingCodes.getCertaItemCode().trim() == "" || mappingCodes.getVistaItemCode().trim() == "") {
			throw new BusinessException("insert all data", "insertTestWithEmptyData", ErrorSeverity.ERROR);
		} else

		{
			return getRepository().save(mappingCodes);
		}

	}
	
//	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEL_TEST_CATALOG + "')")
	public MappingCodes deleteMappingCodes(MappingCodes mappingCodes) {
		getRepository().delete(mappingCodes);

		return mappingCodes;

	}

	   
	@InterceptorFree
	public String getCertaItemCodeByVistaItemCode(String fT1_7_TransactionCode) {
		List<MappingCodes> mappingCodes = getRepository().getCertaItemCodeByVistaItemCode(fT1_7_TransactionCode);
		if (mappingCodes.size() == 0) {
			return null;
		}		
		return mappingCodes.get(0).getCertaItemCode();
	}
	
	
	@InterceptorFree
	public String getCertaItemCodeByVistaItemCodeAndServSection(String fT1_7_TransactionCode , String obr24_DiagnosticServSectID) {
		List<MappingCodes> mappingCodes = getRepository().getCertaItemCodeByVistaItemCodeAndServSection(fT1_7_TransactionCode , obr24_DiagnosticServSectID);
		if (mappingCodes.size() == 0) {
			return null;
		}		
		return mappingCodes.get(0).getCertaItemCode();
	}
	   
	   
	   

}
