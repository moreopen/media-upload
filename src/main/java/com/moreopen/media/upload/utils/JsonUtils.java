package com.moreopen.media.upload.utils;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
	
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	private static ObjectMapper objectMapper;
	
	//init objectMapper
	static {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	public static <T> T json2Bean(String json, Class<T> clazz) throws Exception {
		return objectMapper.readValue(json, clazz);
	}
	
	public static <T> T json2Bean(String json, TypeReference<T> typeReference) {
		try {
			return objectMapper.readValue(json, typeReference);
		} catch (Exception e) {
			logger.error("convert json to bean failed", e);
			return null;
		}
	}
	
	public static String bean2Json(Object bean) throws Exception {
		try {
			return objectMapper.writeValueAsString(bean);
		} catch (Exception e) {
			logger.error("convert bean to json failed", e);
			throw e;
		}
	}
	
}
