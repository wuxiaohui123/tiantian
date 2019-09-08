package com.yinhai.sysframework.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yinhai.sysframework.app.domain.DomainObject;
import com.yinhai.sysframework.app.domain.VO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.SimpleTypeConvert;
import com.yinhai.sysframework.util.ValidateUtil;

@SuppressWarnings("unchecked")
public class BaseDTO<K, V> extends HashMap implements DTO {

    private transient Log log = LogFactory.getLog(getClass());

    private static final String FIELDS_NOTEMPTY_SELECT = "FIELDS_NOTEMPTY_SELECT";

    private static final String FIELDS_NOTEMPTY_UPDATE = "FIELDS_NOTEMPTY_UPDATE";

    private static final String FIELDS_NOTEMPTY_INSERT = "FIELDS_NOTEMPTY_INSERT";

    private static final String FIELDS_NOTEMPTY_DELETE = "fields_notempty_delete";

    private static final String FIELDS_NOTEMPTY_PRC = "fields_notempty_prc";

    public BaseDTO() {
    }

    public BaseDTO(String xml) {
    }

    public BaseDTO(String key, Object object) {
        super.put(key, object);
    }

    public BaseDTO(String key, List list) {
        super.put(key, list);
    }

    public BaseDTO(Map map) {
        super.putAll(map);
    }

    public BaseDTO(String key, DomainObject object) {
        super.put(key, object);
    }

    public BaseDTO(DomainObject object) {
        super.putAll(object.toMap());
    }

    @Override
    public Map getDtoAsMap() {
        return this;
    }

	@Override
    public DomainObject toDomainObject(String domainClass) {
        try {
			DomainObject domain = (DomainObject) ReflectUtil.newInstance(domainClass);
            ReflectUtil.copyMapToObject(this, domain, false);
			return domain;
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                ex.printStackTrace();
            }
            throw new AppException("DTO生成DomainObject对象失败，原因是：" + ex.getMessage());
        }
    }

	@Override
    public DomainObject toDomainObject(Class domainClass) {
        try {
			DomainObject domain = (DomainObject) ReflectUtil.newInstance(domainClass);
            ReflectUtil.copyMapToObject(this, domain, false);
			return domain;
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                ex.printStackTrace();
            }
            throw new AppException("DTO生成DomainObject对象失败，原因是：" + ex.getMessage());
        }

    }

	@Override
    public VO toVO(String voClass) {
        try {
            VO vo = (VO) ReflectUtil.newInstance(voClass);
            ReflectUtil.copyMapToObject(this, vo, false);
            return vo;
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                ex.printStackTrace();
            }
            throw new AppException("DTO生成VO对象失败，原因是：" + ex.getMessage());
        }

    }

	@Override
    public VO toVO(Class voClass) {
        try {
			VO vo = (VO) ReflectUtil.newInstance(voClass);
            ReflectUtil.copyMapToObject(this, vo, false);
			return vo;
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                ex.printStackTrace();
            }
            throw new AppException("DTO生成VO对象失败，原因是：" + ex.getMessage());
        }

    }

	@Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        forEach((key, value) -> buffer.append(key).append(":").append(value).append("\n"));
        return buffer.toString();
    }

	@Override
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<data>");
        forEach((key, value) -> buffer.append("<").append(key).append(">").append(value).append("</").append(key).append(">").append("\n"));
        buffer.append("</data>");
        return buffer.toString();
    }

	@Override
    public Integer getAsInteger(String key) {
        Object obj = SimpleTypeConvert.convert(get(key), "Integer", null);
        if (obj != null)
            return (Integer) obj;
        return null;
    }

	@Override
    public Long getAsLong(String key) {
        Object obj = SimpleTypeConvert.convert(get(key), "Long", null);
        if (obj != null)
            return (Long) obj;
        return null;
    }

	@Override
    public String getAsString(String key) {
        Object obj = SimpleTypeConvert.convert(get(key), "String", null);
        if (obj != null)
            return (String) obj;
        return "";
    }

	@Override
    public BigDecimal getAsBigDecimal(String key) {
        Object obj = SimpleTypeConvert.convert(get(key), "BigDecimal", null);
        if (obj != null) {
            return (BigDecimal) obj;
        }
        return null;
    }

	@Override
    public Date getAsDate(String key) {
        Object obj = SimpleTypeConvert.convert(get(key), "Date", "yyyy-MM-dd");
        if (obj != null)
            return (Date) obj;
        return null;
    }

	@Override
    public Timestamp getAsTimestamp(String key) {
        Object obj = SimpleTypeConvert.convert(get(key), "Timestamp", "yyyy-MM-dd HH:mm:ss");
        if (obj != null)
            return (Timestamp) obj;
        return null;
    }

	@Override
    public Integer getAsInteger(String key, int defaultValue) {
        Integer obj = getAsInteger(key);
        return obj != null ? obj : Integer.valueOf(defaultValue);
    }

	@Override
    public Long getAsLong(String key, long defaultValue) {
        Long obj = getAsLong(key);
        return obj != null ? obj : Long.valueOf(defaultValue);
    }

	@Override
    public BigDecimal getAsBigDecimal(String key, double defaultValue) {
        BigDecimal obj = getAsBigDecimal(key);
        return obj != null ? obj : BigDecimal.valueOf(defaultValue);
    }

	@Override
    public Date getAsDate(String key, Date defaultValue) {
        Date obj = getAsDate(key);
        return obj != null ? obj : defaultValue;
    }

	@Override
    public Timestamp getAsTimestamp(String key, Timestamp defaultValue) {
        Timestamp obj = getAsTimestamp(key);
        return obj != null ? obj : defaultValue;
    }

	@Override
    public String getAsString(String key, String defaultValue) {
        String obj = getAsString(key);
        return obj != null ? obj : defaultValue;
    }

	@Override
    public String[] getAsStringArray(String key) {
        Object tmp = get(key);
        if (!ValidateUtil.isEmpty(tmp)) {
            if (tmp instanceof String[]) {
                return (String[]) tmp;
            }
            return new String[]{(String) tmp};
        }

        return new String[0];
    }

	@Override
    public DTO getSubDto(String fieldNames) {
        DTO dto = new BaseDTO();
        String[] fields = fieldNames.split(ValidateUtil.decimalCommaDelimiter);
        for (int i = 0; i < fields.length; i++) {
            dto.put(fields[i].trim(), get(fields[i].trim()));
        }
        return dto;
    }

	@Override
    public DTO append(String key, Object value) {
        put(key, value);
        return this;
    }

	@Override
    public DTO checkNotEmptyForDelete(String keys) {
        put(FIELDS_NOTEMPTY_DELETE, keys);
        return this;
    }

	@Override
    public DTO checkNotEmptyForInsert(String keys) {
        put(FIELDS_NOTEMPTY_INSERT, keys);
        return this;
    }

	@Override
    public DTO checkNotEmptyForSelect(String keys) {
        put(FIELDS_NOTEMPTY_SELECT, keys);
        return this;
    }

	@Override
    public DTO checkNotEmptyForUpdate(String keys) {
        put(FIELDS_NOTEMPTY_UPDATE, keys);
        return this;
    }

	@Override
    public DTO checkNotEmptyForPrc(String keys) {
        put(FIELDS_NOTEMPTY_PRC, keys);
        return this;
    }

	@Override
    public String[] getNotEmptyForDelete() {
        Object keys = get(FIELDS_NOTEMPTY_DELETE);
        if (keys != null && keys.toString().trim().length() != 0) {
            return keys.toString().split(ValidateUtil.decimalCommaDelimiter);
        }
        return null;
    }

	@Override
    public String[] getNotEmptyForInsert() {
        Object keys = get(FIELDS_NOTEMPTY_INSERT);
        if (keys != null && keys.toString().trim().length() != 0) {
            return keys.toString().split(ValidateUtil.decimalCommaDelimiter);
        }
        return null;
    }

	@Override
    public String[] getNotEmptyForSelect() {
        Object keys = get(FIELDS_NOTEMPTY_SELECT);
        if (keys != null && keys.toString().trim().length() != 0) {
            return keys.toString().split(ValidateUtil.decimalCommaDelimiter);
        }
        return null;
    }

	@Override
    public String[] getNotEmptyForUpdate() {
        Object keys = get(FIELDS_NOTEMPTY_UPDATE);
        if (keys != null && keys.toString().trim().length() != 0) {
            return keys.toString().split(ValidateUtil.decimalCommaDelimiter);
        }
        return null;
    }

	@Override
    public String[] getNotEmptyForPrc() {
        Object keys = get(FIELDS_NOTEMPTY_PRC);
        if (keys != null && keys.toString().trim().length() != 0) {
            return keys.toString().split(ValidateUtil.decimalCommaDelimiter);
        }
        return null;
    }

	@Override
    public boolean isEmpty(String key) {
        return ValidateUtil.isEmpty(get(key));
    }

	@Override
    public boolean isNotEmpty(String key) {
        return !isEmpty(key);
    }
}
