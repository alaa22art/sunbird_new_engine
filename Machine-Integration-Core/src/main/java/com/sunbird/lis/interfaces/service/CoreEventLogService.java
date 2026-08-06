package com.sunbird.lis.interfaces.service;

import java.util.ArrayList;
import java.util.List;

/**
*

*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.core.common.helper.FilterablePageRequest.OrderObject;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.CoreEventLog;
import com.sunbird.lis.interfaces.repo.EventLogRepo;

@Service("CoreEventLogService")
public class CoreEventLogService extends GenericService<CoreEventLog, EventLogRepo> {

	@Autowired
	private EventLogRepo repo;

	@Override
	protected EventLogRepo getRepository() {
		return repo;
	}

	public Page<CoreEventLog> getCoreEventLog(FilterablePageRequest filterablePageRequest) {
		String[] joins = new String[] { "machine" };
		List<OrderObject> sortList = new ArrayList<>();
		sortList.add(new OrderObject(Direction.DESC, "sentDate"));
		Page<CoreEventLog> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				CoreEventLog.class, joins);

		return page;
	}

	@InterceptorFree
	public CoreEventLog addCoreEventLog(CoreEventLog coreLog) {
		//coreLog.setCreatedBy(1L);
		//		repo.addCoreEventLog(new Date(), coreLog.getDetails(), coreLog.getText(), coreLog.getStatusId(), coreLog.getSentDate(),
		//				coreLog.getSource(), coreLog.getStatusId());

		return repo.save(coreLog);
	}

	public CoreEventLog updateDBEventLog(CoreEventLog coreLog) {
		return repo.save(coreLog);
	}

	public void deleteDBEventLog(Long rId) {
		repo.deleteById(rId);
	}
}
