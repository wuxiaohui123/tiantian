package com.yinhai.sysframework.util.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yinhai.sysframework.app.domain.BaseVO;
import com.yinhai.sysframework.app.domain.jsonmodel.ResultBean;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.CollectionUtils;

public class JSonFactory {

	private static Logger logger = LogManager.getLogger(JSonFactory.class);

	private static SerializeConfig config = new SerializeConfig();

	private static ObjectMapper objectMapper = new ObjectMapper();

	static {

		/**
		 * <code>fastJson</code>
		 */
		SimpleDateFormatSerializer dateFormat = new SimpleDateFormatSerializer("yyyy-MM-dd");
		SimpleDateFormatSerializer dateTimeFormat = new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss");
		config.put(java.sql.Date.class, dateFormat);
		config.put(java.util.Date.class, dateFormat);
		config.put(java.sql.Timestamp.class, dateTimeFormat);

		/**
		 * <code>jackson</code>
		 */
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		// 设置序列化是默认值和null不进行序列化。
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// objectMapper.setSerializationInclusion(Include.NON_DEFAULT);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(format);
	}

	public static String bean2json(Object src) {
		if (src == null || "".equals(src))
			return "";
		String ret = null;
		if (src instanceof ResultBean) {
			try {
				ret = ((ResultBean) src).toJson();
			} catch (Exception e) {
				ret = JSON.toJSONString(src, config,  SerializerFeature.WriteMapNullValue);
			}
		} else if (src instanceof PageBean) {
			try {
				ret = ((PageBean) src).toJson();
			} catch (Exception e) {
				ret = JSON.toJSONString(src, config, SerializerFeature.WriteMapNullValue);
			}
		} else if (src instanceof Map) {
			try {
				ret = CollectionUtils.mapToJson((Map) src);
			} catch (Exception e) {
				ret = JSON.toJSONString(src, config, SerializerFeature.WriteMapNullValue);
			}
		} else if (src instanceof List) {
			if (((List) src).size() > 8000) {
				ret = JSON.toJSONString(src, config, SerializerFeature.WriteMapNullValue);
			} else {
				try {
					StringBuilder sb = new StringBuilder();
					sb.append("[");
					List list = (List) src;
					Object rowObj = null;
					for (int i = 0; i < list.size(); i++) {
						rowObj = list.get(i);
						if (i > 0)
							sb.append(",");
						if (rowObj instanceof BaseVO) {
							sb.append(((BaseVO) rowObj).toJson());
						} else if (rowObj instanceof Map) {
							sb.append(CollectionUtils.mapToJson((Map) rowObj));
						} else {
							sb.append(bean2json(rowObj));
						}
					}
					sb.append("]");
					ret = sb.toString();
				} catch (Exception e) {
					ret = JSON.toJSONString(src, config, SerializerFeature.WriteMapNullValue);
				}
			}
		} else {
			ret = JSON.toJSONString(src, config, SerializerFeature.WriteMapNullValue);
		}
		return ret;
	}

	public static <T> T json2bean(String json, Class<T> classOfT) {
		if (json == null)
			return null;
		return (T) JSON.parseObject(json, classOfT);
	}

	public static String jsonSerialization(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException("解析对象错误");
		}
	}

	public static <T> Object jsonDeserialization(String json, Class<?> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("反序列化对象错误");
		}
	}

	public static <T> T jsonDeserialization(String json, Class<?> collectionClass, Class<?>... elementClasses) {
		try {
			JavaType javaType = getJavaType(collectionClass, elementClasses);
			return objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			throw new RuntimeException("反序列化对象错误");
		}
	}

	public static <T> T convertObject(Map map, Class<?> convertClass) {
		return (T) objectMapper.convertValue(map, convertClass);
	}

	private static JavaType getJavaType(Class<?> collectionClass, Class<?>... elementClasses) {
		return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	public static <T> T fromJson(String jsonString, TypeReference<T> type) {
		if ("NULL".equals(jsonString) || "null".equals(jsonString)) {
			return null;
		}
		return (T) JSON.parseObject(jsonString, type, new Feature[0]);
	}

	public static String getJson(String ret) {
		String json = JSON.toJSONString(ret);
		json = json.substring(1, json.length() - 1);
		return json;
	}

	public static String toJson(String ret) {
		ret = StringUtils.replace(ret, "\\", "\\\\");
		ret = StringUtils.replace(ret, "\r", "\\\\r");
		ret = StringUtils.replace(ret, "\t", "\\\\t");
		ret = StringUtils.replace(ret, "\b", "\\\\b");
		ret = StringUtils.replace(ret, "\f", "\\\\f");
		ret = StringUtils.replace(ret, "\n", "\\\\n");
		ret = StringUtils.replace(ret, "\"", "\\\"");

		return ret;
	}
}
