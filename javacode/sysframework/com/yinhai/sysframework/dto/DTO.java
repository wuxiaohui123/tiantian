package com.yinhai.sysframework.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import com.yinhai.sysframework.app.domain.DomainObject;
import com.yinhai.sysframework.app.domain.VO;

public interface DTO<K, V> extends Map, Serializable {

	   String KEY_VALUE = "`";
	   String ENTRY_ENTRY = "^";
	  
	   boolean isEmpty(String key);
	  
	   boolean isNotEmpty(String key);
	  
	   Integer getAsInteger(String key);
	  
	   Integer getAsInteger(String key, int defaultValue);
	  
	   Long getAsLong(String key);
	  
	   Long getAsLong(String key, long defaultValue);
	  
	   String getAsString(String key);
	  
	   String getAsString(String key, String defaultValue);
	  
	   BigDecimal getAsBigDecimal(String key, double defaultValue);
	  
	   BigDecimal getAsBigDecimal(String key);
	  
	   Date getAsDate(String key);
	  
	   Date getAsDate(String key, Date defaultValue);
	  
	   Timestamp getAsTimestamp(String key);
	  
	   Timestamp getAsTimestamp(String key, Timestamp defaultValue);
	  
	   String[] getAsStringArray(String key);
	  
	   Map<K, V> getDtoAsMap();
	  
	   DTO getSubDto(String key);
	  
	   DomainObject toDomainObject(String key);
	  
	   DomainObject toDomainObject(Class<?> key);
	  
	   VO toVO(String voClass);
	  
	   VO toVO(Class<?> voClass);
	  
	   String toXML();
	  
	   DTO append(String key, Object value);
	  
	   DTO checkNotEmptyForSelect(String key);
	  
	   DTO checkNotEmptyForUpdate(String key);
	  
	   DTO checkNotEmptyForInsert(String key);
	  
	   DTO checkNotEmptyForDelete(String key);
	  
	   DTO checkNotEmptyForPrc(String key);
	  
	   String[] getNotEmptyForSelect();
	  
	   String[] getNotEmptyForUpdate();
	  
	   String[] getNotEmptyForInsert();
	  
	   String[] getNotEmptyForDelete();
	  
	   String[] getNotEmptyForPrc();
}
