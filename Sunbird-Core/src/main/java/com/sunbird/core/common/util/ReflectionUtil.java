package com.sunbird.core.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.primitives.Primitives;
import com.sunbird.core.base.entity.BaseAuditableBranchedEntity;
import com.sunbird.core.base.entity.BaseAuditableTenantedEntity;
import com.sunbird.core.base.entity.BaseEntity;
import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.repo.GenericRepository;
import com.sunbird.core.common.annotation.MapNotNull;
import com.sunbird.core.common.helper.FieldMetaData;
import com.sunbird.core.common.helper.FilterablePageRequest;

@Component
public class ReflectionUtil {

	public static Validator ENTITY_VALIDATOR;

	@PostConstruct
	public void init() {
		ENTITY_VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * Get all persisted classes in the whole workspace
	 * 
	 * @param entityManager
	 * @return Set of classes
	 */
	public static Set<Class<?>> getAllPersistedClasses(EntityManager entityManager) {
		Metamodel metamodel = entityManager.getMetamodel();
		Set<EntityType<?>> ents = metamodel.getEntities();
		Set<Class<?>> persistedClasses = new HashSet<>();
		for (EntityType<?> entityType : ents) {
			EntityTypeImpl<?> i = (EntityTypeImpl<?>) entityType;
			if (i.getJavaType() != null) {
				persistedClasses.add(i.getJavaType());
			}
		}
		return persistedClasses;
	}

	public static Class<?> findPersistedClassByName(EntityManager entityManager, String className) {
		return getAllPersistedClasses(entityManager)
													.stream().parallel()
													.filter(clazz -> clazz.getSimpleName().equals(className))
													.findAny()
													.get();
	}

	@SuppressWarnings("unchecked")
	public static Class<BaseEntity> getEntityClassByName(String entityName, EntityManager entityManager) {
		if (StringUtil.isEmpty(entityName)) {
			return null;
		}
		Metamodel metamodel = entityManager.getMetamodel();
		Set<EntityType<?>> entities = metamodel.getEntities();
		for (EntityType<?> entityType : entities) {
			EntityTypeImpl<?> entityTypeImpl = (EntityTypeImpl<?>) entityType;
			if (entityTypeImpl.getJavaType() != null) {
				if (entityName.equals(entityTypeImpl.getJavaType().getSimpleName())) {
					return (Class<BaseEntity>) entityTypeImpl.getJavaType();
				}
			}
		}
		return null;
	}

	public static String getFieldNameFromColumn(String entityName, String columnName, EntityManager entityManager) {
		Class<?> entityClass = getEntityClassByName(entityName, entityManager);
		Field[] fields = entityClass.getDeclaredFields();
		Annotation[] annotations;
		for (Field field : fields) {
			annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Column) {
					Column myAnnotation = (Column) annotation;
					if (myAnnotation.name().equals(columnName)) {
						return field.getName();
					}
				} else if (annotation instanceof JoinColumn) {
					JoinColumn myAnnotation = (JoinColumn) annotation;
					if (myAnnotation.name().equals(columnName)) {
						return field.getName();
					}
				}
			}
		}
		return columnName.toLowerCase();
	}

	/**
	 * Get the field type from a given entity using reflection
	 *
	 * @param entityName
	 * @param fieldName
	 * @param entityManager
	 * @return
	 */
	public static Class<?> getFieldType(String entityName, String fieldName, EntityManager entityManager) {
		Class<?> entityClass = getEntityClassByName(entityName, entityManager);
		return getField(entityClass, fieldName).getType();
	}

	/**
	 * Search in joinEntity for a field of type mainEntity and return field name
	 *
	 * @param mainEntity
	 * @param joinEntity
	 * @param entityManager
	 * @return String
	 */
	public static String getJoinFieldName(String mainEntity, String joinEntity, EntityManager entityManager) {
		Class<?> clazz = getEntityClassByName(joinEntity, entityManager);
		Field[] fields = clazz.getDeclaredFields();
		String className;
		for (Field field : fields) {
			className = field.getType().getSimpleName();
			if (className.equals("List")) { // get the generic type
				ParameterizedType pType = ((ParameterizedType) field.getGenericType());
				Class<?> genClass = (Class<?>) pType.getActualTypeArguments()[0];
				className = genClass.getSimpleName();
			}
			if (className.equals(mainEntity)) {
				return field.getName();
			}
		}
		return "";
	}

	public static Class<?> getFieldDataType(Class<?> entityClass, String dbColumnName) {
		if (entityClass != null) {
			Class<?> tempClass = entityClass;
			try {
				Field field = tempClass.getDeclaredField(StringUtil.toLowerCamelCase(dbColumnName));
				Class<?> type = field.getType();
				if (type.isPrimitive()) {
					type = Primitives.wrap(type);
				}
				return type;
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				// String clsName = tempClass.getPackage().getName() + ".extended." + tempClass.getSimpleName() + "EX";
				// try {
				// tempClass = Class.forName(clsName);
				// return getFieldDataType(tempClass, dbColumnName);
				// } catch (ClassNotFoundException ex) {
				// ex.printStackTrace();
				// }
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Class<?> getFieldType(Class<?> clazz, String fieldName) {
		return getField(clazz, fieldName).getType();
	}

	public static Field getField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all the fields of the {@code clazz} even in the super classes
	 * 
	 * @param clazz
	 * @return Map which contains field name and Field object
	 */
	public static Map<String, Field> getAllFieldTypes(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		do {
			Collections.addAll(fields, clazz.getDeclaredFields());
			clazz = clazz.getSuperclass();
		} while (clazz != null);

		return fields	.stream().filter(item -> !item.getName().equals("serialVersionUID"))// remove serialVersionUID since it is duplicated in super classes
						.collect(Collectors.toMap(Field::getName, item -> item));
	}

	/**
	 * 
	 * @param clazz first level class-type
	 * @param deepKey final field key, ex: "level1.level2.fieldName"
	 */
	public static Class<?> getDeepFieldType(Class<?> clazz, String deepKey) {
		Class<?> fieldType = clazz;
		String[] keys = deepKey.split("[.]");
		for (String key : keys) {
			Field field = getAllFieldTypes(fieldType).get(key);
			fieldType = field.getType();
			if (isClassCollection(fieldType)) {
				final ParameterizedType genericSuperclass = (ParameterizedType) field.getGenericType();
				fieldType = (Class<?>) genericSuperclass.getActualTypeArguments()[0];
			}
		}
		return fieldType;
	}

	public static boolean isClassCollection(Class<?> c) {
		return Collection.class.isAssignableFrom(c);
	}

	public static boolean isClassMap(Class<?> c) {
		return Map.class.isAssignableFrom(c);
	}

	public static Map<String, FieldMetaData> getEntityFieldMetaData(Class<?> clazz) {

		Map<String, FieldMetaData> fieldMetaDataList = new HashMap<String, FieldMetaData>();

		FieldMetaData fieldMetaData = null;
		for (Field field : clazz.getDeclaredFields()) {

			fieldMetaData = new FieldMetaData();
			fieldMetaData.setName(field.getName());
			fieldMetaData.setNotNull(field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(MapNotNull.class));
			fieldMetaData.setEmail(field.isAnnotationPresent(Email.class));
			//check if max is not present (0)
			if (field.isAnnotationPresent(Size.class) || field.isAnnotationPresent(Max.class) || field.isAnnotationPresent(Min.class)) {
				fieldMetaData.setSized(true);
			}
			if (field.isAnnotationPresent(Size.class)) {
				Size size = field.getAnnotation(Size.class);
				fieldMetaData.setMin(size.min());
				fieldMetaData.setMax(size.max());

			} else {
				if (field.isAnnotationPresent(Max.class)) {
					Max max = field.getAnnotation(Max.class);
					fieldMetaData.setMax((int) max.value());
				}
				if (field.isAnnotationPresent(Min.class)) {
					Min min = field.getAnnotation(Min.class);
					fieldMetaData.setMin((int) min.value());
				}

			}

			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				fieldMetaData.setUpdatable(column.updatable());
				fieldMetaData.setColumnName(column.name());
			}

			if (field.isAnnotationPresent(Digits.class)) {
				Digits digits = field.getAnnotation(Digits.class);
				fieldMetaData.setInteger(digits.integer());
				fieldMetaData.setFraction(digits.fraction());
			}

			fieldMetaDataList.put(field.getName(), fieldMetaData);
		}

		return fieldMetaDataList;

	}

	/**
	 * Convert from an unknown object type(written as string) to an object.
	 * 
	 * @param className
	 * @param stringObj : the object to deserialize (must be in string format)
	 * @param entityManager
	 * @return The serialized object
	 */
	public static BaseEntity getObjectFromUnknownType(String className, String stringObj, EntityManager entityManager) {
		Class<? extends BaseEntity> clazz = getEntityClassByName(className, entityManager);
		try {
			return JSONUtil.convertJSONToObject(stringObj, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Unproxy the Hibernate lazy object
	 * 
	 * @param proxy
	 * @return unproxied object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unproxy(T proxy) {
		if (proxy instanceof HibernateProxy) {
			HibernateProxy hibernateProxy = (HibernateProxy) proxy;
			LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
			return (T) initializer.getImplementation();
		} else {
			return proxy;
		}
	}

	/**
	 * Get repository of the entityName by reflection
	 * 
	 * @param entityName
	 * @return entity's repository
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BaseEntity> GenericRepository<T> getRepository(String entityName) {
		Object obj = SpringUtil.getApplicationContext().getBean(entityName + "Repo");
		GenericRepository<T> repo = (GenericRepository<T>) obj;
		return repo;
	}

	/**
	 * Get repository of the entityName by reflection
	 * 
	 * @param entityName
	 * @return entity's repository
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BaseEntity> GenericRepository<T> getRepository(Class<T> clazz) {
		Object obj = SpringUtil.getApplicationContext().getBean(clazz.getSimpleName() + "Repo");
		GenericRepository<T> repo = (GenericRepository<T>) obj;
		return repo;
	}

	/**
	 * Save any entity to the database, must auto wire the ReflectionUtil.
	 * Must AutoWire the ReflectionUtil.
	 * 
	 * @param entity
	 * @return saved entity
	 */
	public <T extends BaseEntity> T saveEntity(T entity) {
		return getRepository(entity.getClass().getSimpleName()).save(entity);
	}

	/**
	 * Save any entity to the database, must auto wire the ReflectionUtil.
	 * Must AutoWire the ReflectionUtil.
	 * Use this if the saveEntity is being called in a loop.
	 * 
	 * @param repo
	 * @param entity
	 * @return saved entity
	 */
	public <T extends BaseEntity> T saveEntity(GenericRepository<T> repo, T entity) {
		return repo.save(entity);
	}

	/**
	 * Save any entity to the database in a new transaction.
	 * Must AutoWire the ReflectionUtil.
	 * 
	 * @param entity
	 * @return saved entity
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends BaseEntity> T saveEntitySeparate(T entity) {
		return saveEntity(entity);
	}

	/**
	 * Save any entity to the database in a new transaction.
	 * Must AutoWire the ReflectionUtil.
	 * Use this if the saveEntitySeparate is being called in a loop.
	 * 
	 * @param repo
	 * @param entity
	 * @return saved entity
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T extends BaseEntity> T saveEntitySeparate(GenericRepository<T> repo, T entity) {
		return saveEntity(repo, entity);
	}

	public static <T> Boolean doesEntityHaveField(Class<T> clazz, String fieldName) {
		Field[] properties = clazz.getDeclaredFields();
		for (Field field : properties) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Disable the Intercepter in case of using a function in a parent function that enabled filters
	 * 
	 * @param entityManager
	 * @param disableBranchFilter
	 * @param disableTenantFilter
	 * @param supplier : the function to disable filters
	 * @return supplier data
	 */
	public static <T> T disableIntercepterFilters(EntityManager entityManager, boolean disableBranchFilter, boolean disableTenantFilter,
			Supplier<T> supplier) {
		Session session = entityManager.unwrap(Session.class);
		Filter tenantFilter = null;
		Filter branchFilter = null;
		if (disableTenantFilter) {
			tenantFilter = session.getEnabledFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			if (tenantFilter != null) {
				session.disableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			}
		}
		if (disableBranchFilter) {
			branchFilter = session.getEnabledFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			if (branchFilter != null) {
				session.disableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			}
		}

		T result = supplier.get();
		if (tenantFilter != null) {
			tenantFilter = session.enableFilter(BaseAuditableTenantedEntity.TENANT_FILTER);
			tenantFilter.setParameter("tenantId", SecurityUtil.getCurrentUser().getTenantId());
			tenantFilter.validate();

		}
		if (branchFilter != null) {
			branchFilter = session.enableFilter(BaseAuditableBranchedEntity.BRANCH_FILTER);
			branchFilter.setParameter("branchId", SecurityUtil.getCurrentUser().getBranchId());
			branchFilter.validate();
		}
		return result;
	}

	public static <T extends BaseEntity> Page<T> findPageWithJoins(Class<T> clazz, Consumer<List<T>> consumer,
			FilterablePageRequest filterablePageRequest, String... joins) {
		GenericRepository<T> repo = getRepository(clazz.getSimpleName());
		return findPageWithJoins(repo, consumer, filterablePageRequest, joins);
	}

	/**
	 * To fix pagination with collection joins(joining a collection field).
	 * this function will paginate the data then query again to fetch the joins.
	 * 
	 * @param repo: of T type
	 * @param consumer: nullable
	 * @param filterablePageRequest
	 * @param joins
	 * 
	 * @return Page of T type
	 */
	public static <T extends BaseEntity> Page<T> findPageWithJoins(GenericRepository<T> repo, Consumer<List<T>> consumer,
			FilterablePageRequest filterablePageRequest, String... joins) {
		Page<T> page = repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), null);
		if (page.getNumberOfElements() == 0) {
			return page;
		}
		List<T> data = repo.find(
				Arrays.asList(new SearchCriterion("rid", CollectionUtil.getRidAsList(page.getContent()), FilterOperator.in)),
				null, filterablePageRequest.getSortObject(), Boolean.TRUE, joins).stream().distinct().collect(Collectors.toList());
		if (consumer != null) {
			consumer.accept(data);
		}
		return new PageImpl<T>(data, filterablePageRequest.getPageRequest(), page.getTotalElements());

	}

	/**
	 * Check if the given entity pass all javax constraints validations
	 * 
	 * @param entity
	 * @param ignoreSuperFields: whether to ignore fields in super classes
	 * @return true if entity passes all constraints
	 */
	public static Boolean isEntityValid(BaseEntity entity, boolean ignoreSuperFields) {
		String[] ignoredFields = null;
		if (ignoreSuperFields) {
			ignoredFields = new String[] { "tenantId", "branchId", "version", "creationDate", "createdBy" };
		}

		Set<ConstraintViolation<BaseEntity>> errors = getEntityViolations(entity, ignoredFields);

		return errors.size() == 0;
	}

	public static Set<ConstraintViolation<BaseEntity>> getEntityViolations(BaseEntity entity, String... ignoredFields) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity paramter cannot be null");
		}
		Set<ConstraintViolation<BaseEntity>> errors = ENTITY_VALIDATOR.validate(entity);

		if (ignoredFields != null) {
			List<String> superFields = Arrays.asList(ignoredFields);
			errors.removeIf(e -> superFields.contains(e.getPropertyPath().iterator().next().getName()));
		}

		return errors;
	}

	/**
	 * Remove any entity that does not pass javax constraints validations
	 * 
	 * @param entities
	 * @param ignoreSuperFields: whether to ignore fields in super classes
	 * @return invalid entities
	 */
	public static <T> List<T> removeInvalidEntities(List<T> entities, boolean ignoreSuperFields) {
		if (CollectionUtil.isCollectionEmpty(entities)) {
			return new ArrayList<>();
		}
		List<String> superFields = Arrays.asList("tenantId", "branchId", "version", "creationDate", "createdBy");
		List<T> invalidEntities = new ArrayList<>();
		entities.removeIf(c ->
			{
				Set<ConstraintViolation<T>> errors = ENTITY_VALIDATOR.validate(c);
				if (ignoreSuperFields) {
					errors.removeIf(e -> superFields.contains(e.getPropertyPath().iterator().next().getName()));
				}
				boolean remove = errors.size() != 0;
				if (remove) {
					invalidEntities.add(c);
				}
				return remove;
			});

		return invalidEntities;

	}
}
