package com.certacure.machine.web.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.certacure.core.base.entity.BaseAuditableBranchedEntity;
import com.certacure.core.base.entity.BaseAuditableTenantedEntity;
import com.certacure.core.base.entity.BaseEntity;
import com.certacure.core.common.business.exception.BusinessException;
import com.certacure.core.common.business.exception.BusinessException.ErrorSeverity;
import com.certacure.core.common.util.ReflectionUtil;
import com.certacure.core.common.util.SecurityUtil;
import com.certacure.core.common.util.StringUtil;
import com.certacure.lis.interfaces.service.LkpService;

@Component
@Aspect
public class ServiceInterceptor {

	@PersistenceContext
	public EntityManager entityManager;

	private Logger log = LoggerFactory.getLogger(getClass());
	private Map<String, Class<?>> persistedClasses;

	@PostConstruct
	public void init() {
		Set<Class<?>> clazzes = ReflectionUtil.getAllPersistedClasses(entityManager);
		persistedClasses = new HashMap<>();
		for (Class<?> clazz : clazzes) {
			if (StringUtil.isEmpty(clazz.getSimpleName())) {
				continue;
			}
			if (persistedClasses.containsKey(clazz.getSimpleName())) {
				continue;
			}
			persistedClasses.put(clazz.getSimpleName(), clazz);
		}
	}

	@Pointcut("execution(* org.springframework.data.repository.Repository+.*(..))")
	public void beforeAfterQueryExecution() {
	}

	@Around("beforeAfterQueryExecution()")
	public Object beforeAfterQueryExecution(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		Object result = pjp.proceed();//get the result of the query to return it normally when logging is finished
		long end = System.currentTimeMillis();
		String methodName = pjp.getSignature().getName();
		log.info("Execution of " + methodName + " took " + (end - start) + " ms");
		return result;
	}

	@Before("(execution(* com.certacure.lis.interfaces.service.*.*(..)) || execution(* com.certacure.lis.interfaces.*.service.*.*(..))) && "
			+ "!execution(* com.certacure.core.base.service.BaseService.findById(..)) && "
			+ "!@annotation(com.certacure.lis.interfaces.annotation.InterceptorFree) && !@target(com.certacure.lis.interfaces.annotation.InterceptorFree)")

	public void beforeInterceptor(JoinPoint point) {
		log.info(point + " called...");
		Class<?> entity = getEntity(point);
		if (entity == null) {
			return;
		}
		boolean isBranched = entity.getSuperclass().equals(BaseAuditableBranchedEntity.class);
		boolean isTenanted = isBranched ? true : entity.getSuperclass().equals(BaseAuditableTenantedEntity.class);
		if (isTenanted) {
			Session s = entityManager.unwrap(Session.class);
			Filter filter = s.enableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			filter.setParameter("tenantId", SecurityUtil.getCurrentUser().getTenantId());
			filter.validate();
		}
		if (isBranched) {
			if (SecurityUtil.getCurrentUser().getBranchId() == null) {
				throw new BusinessException("User does not belong to a branch", "userNoBranch", ErrorSeverity.ERROR);
			}
			Filter branchFilter = entityManager.unwrap(Session.class).enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
			branchFilter.validate();
		}
	}

	private Class<?> getEntity(JoinPoint point) {
		Class<?> entity = null;
		if (point.getTarget().getClass().equals(LkpService.class)) {
			for (Object o : point.getArgs()) {
				if (o instanceof Class) {
					entity = (Class<?>) o;
					break;
				} else if (o instanceof BaseEntity) {
					BaseEntity baseEntity = (BaseEntity) o;
					entity = baseEntity.getClass();
					break;
				}
			}
		} else {
			String serviceName = point.getTarget().getClass().getSimpleName();
			serviceName = serviceName.substring(0, serviceName.indexOf("Service"));
			entity = persistedClasses.get(serviceName);
		}
		return entity;
	}

}
