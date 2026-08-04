package com.certacure.lis.interfaces.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.certacure.core.base.helper.SearchCriterion;
import com.certacure.core.base.helper.SearchCriterion.FilterOperator;
import com.certacure.core.base.service.GenericService;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.lis.interfaces.entities.PCRRealTimeResultTemplate;
import com.certacure.lis.interfaces.entities.PCRRealTimeResultTemplateLine;
import com.certacure.lis.interfaces.repo.PCRRealTimeResultTemplateLineRepo;

@Service("PCRRealTimeTemplateLineService")
public class PCRRealTimeResultTemplateLineService extends GenericService<PCRRealTimeResultTemplateLine, PCRRealTimeResultTemplateLineRepo> {

	@Autowired
	private PCRRealTimeResultTemplateLineRepo repo;

	@Autowired
	private PCRRealTimeResultTemplateService pcrRealTimeResultTemplateService;

	@Override
	protected PCRRealTimeResultTemplateLineRepo getRepository() {
		return repo;
	}

	public List<PCRRealTimeResultTemplateLine> getPcrRealTimeResultTemplateLines() {
		PCRRealTimeResultTemplate pcrRealTimeResultTemplate = pcrRealTimeResultTemplateService.getTemplate();
		if (pcrRealTimeResultTemplate == null) {
			throw new BusinessException("There Is No Result Template", "noTemplate", ErrorSeverity.ERROR);
		}
		return getRepository().find(
				Arrays.asList(new SearchCriterion("pcrRealTimeResultTemplate.rid", pcrRealTimeResultTemplate.getRid(),
						FilterOperator.eq)),
				PCRRealTimeResultTemplateLine.class, Sort.by("code"), "pcrRealTimeResultTemplate");
	}

	public List<PCRRealTimeResultTemplateLine> getPcrRealTimeResultTemplateLines(Long templateId) {
		return getRepository().find(Arrays.asList(new SearchCriterion("pcrRealTimeResultTemplate.rid", templateId, FilterOperator.eq)),
				PCRRealTimeResultTemplateLine.class, Sort.by("code"), "pcrRealTimeResultTemplate");
	}

	public PCRRealTimeResultTemplateLine getPcrRealTimeResultTemplateLine(Long templateLineId) {
		return getRepository().findOne(
				Arrays.asList(new SearchCriterion("rid", templateLineId, FilterOperator.eq)),
				PCRRealTimeResultTemplateLine.class, "pcrRealTimeActualResultValues");
	}

}
