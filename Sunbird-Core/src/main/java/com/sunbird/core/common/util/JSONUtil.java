package com.sunbird.core.common.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.data.model.TransField;

public class JSONUtil {

	private static ObjectMapper mapper;

	static {
		mapper = JsonMapper	.builder()
							.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)//ex: allows " contrary to forcing \"
							.build();

		Hibernate5Module hibernateModule = new Hibernate5Module();
		hibernateModule.disable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);

		//register hibernate module to prevent auto-proxying lazy fields
		mapper.registerModule(hibernateModule);

		//to prevent errors when an unknown field is sent
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		//to disable serializing fields with null values
		//used instead of Include.NON_NULL due to the internal configuration of HibernateModule and initializing proxies
		//mapper.setSerializationInclusion(Include.NON_EMPTY);

		//disable serialization of map entries with null keys or values
		mapper.configOverride(Map.class).setInclude(Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL));

		//writes dates as <strings-representing-dates> instead of <longs-representing-milliseconds>
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		//specifies the date-format to be used when serializing dates
		mapper.setDateFormat(DateUtil.getISODateFormatter());
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * convert any object or list to JSON
	 *
	 * @param object
	 * @return String
	 */
	public static String convertObjectToJSON(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * convert JSON on jsonString to Object of type targetClass
	 *
	 * @param jsonString
	 * @param targetClass
	 * @return T Object of type targetClass
	 */
	public static <T> T convertJSONToObject(String jsonString, Class<T> targetClass) {
		try {
			return mapper.readValue(jsonString, targetClass);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	public static <T> T convertJSONToObject(String jsonString, TypeReference<T> typeRef) {
		try {
			return mapper.readValue(jsonString, typeRef);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * convert JSON in jsonString to List of type targetClass
	 *
	 * @param jsonString
	 * @param targetClass
	 * @return List<targetClass>
	 */
	public static <T> List<T> convertJSONToList(String jsonString, Class<T> targetClass) {
		try {
			return (mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(List.class, targetClass)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * convert JSON in jsonString to Map
	 *
	 * @param jsonString
	 * @param keyClass
	 * @param valueClass
	 * @return Map<keyClass, valueClass>
	 */
	public static <T1, T2> Map<T1, T2> convertJSONToMap(String jsonString, Class<T1> keyClass, Class<T2> valueClass) {
		try {
			return mapper.readValue(jsonString, mapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	public static <T1, T2> String convertMapToJSON(Map<T1, T2> map) {
		return convertObjectToJSON(map);
	}

	public static String convertTransFieldToJson(TransField transField) {
		return convertObjectToJSON(transField);
	}

	public static TransField convertJsonToTransField(String json) {
		return convertJSONToObject(json, TransField.class);
	}

	public static <T> T convert(Object from, Class<T> clazz) {
		return mapper.convertValue(from, clazz);
	}

	public static <T> T convert(Object from, TypeReference<T> typeRef) {
		return mapper.convertValue(from, typeRef);
	}

}
