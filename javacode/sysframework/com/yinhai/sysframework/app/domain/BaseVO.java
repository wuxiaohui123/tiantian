package com.yinhai.sysframework.app.domain;

import com.yinhai.sysframework.dto.BaseDTO;
import com.yinhai.sysframework.dto.DTO;
import com.yinhai.sysframework.exception.IllegalInputAppException;
import com.yinhai.sysframework.exception.SysLevelException;
import com.yinhai.sysframework.util.SimpleTypeConvert;
import com.yinhai.sysframework.util.json.JSonFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseVO implements VO {

    @Override
    public Map toMap() {
        return null;
    }

    @Override
    public String toXMLString(String _className) {
        StringBuffer buffer = new StringBuffer();
        String className = getClass().getName();
        if (_className != null && _className.length() > 0)
            className = _className;
        String nodeName = className.substring(className.lastIndexOf(".") + 1);
        nodeName = nodeName.substring(0, 1).toLowerCase() + nodeName.substring(1);
        buffer.append("<").append(nodeName).append(">\n");
        toMap().forEach((key, value) -> buffer.append("<").append(key).append("><![CDATA[").append(SimpleTypeConvert.convert2String(value, "")).append("]]></").append(key).append(">").append("\n"));
        buffer.append("</").append(nodeName).append(">");
        return buffer.toString();
    }

    @Override
    public Key getKey() {
        return null;
    }

    public Object[] toArray() {
        return new Object[0];
    }

    public void validateData() throws IllegalInputAppException {
    }

    public String toTabString() {
        return null;
    }

    public DTO toDTO() {
        return new BaseDTO(toMap());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        DomainMeta meta = getMetadata();
        if (meta == null) {
            return "";
        }
        toDTO().forEach((key, value) -> {
            buffer.append("值：").append(value).append(meta.getField((String) key) == null ? "" : meta.getField((String) key).toString());
        });
        return buffer.toString();
    }

    @Override
    public DomainMeta getMetadata() {
        return null;
    }

    @Override
    public String toXML() {
        return toXMLString(getClass().getName());
    }

    @Override
    public String toJson() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        toMap().forEach((key, value) -> {
            if (value == null || "".equals(value)) {
                if (atomicBoolean.get()) {
                    buffer.append(",");
                }
                buffer.append("\"").append(key).append("\":null");
            } else {
                if (atomicBoolean.get()) {
                    buffer.append(",");
                }
                buffer.append("\"").append(key).append("\":");
                if (value instanceof List) {
                    buffer.append("[");
                    List list = (List) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object tmpObj = list.get(i);
                        if ((tmpObj instanceof BaseVO)) {
                            if (i > 0)
                                buffer.append(",");
                            buffer.append(((BaseVO) tmpObj).toJson());
                        } else {
                            throw new SysLevelException("toJson生成错误,domain的属性是list类型的，list里面必须是domain");
                        }
                    }
                    buffer.append("]");
                } else if (value instanceof Object[]) {
                    buffer.append("[");
                    Object[] list = (Object[]) value;
                    for (int i = 0; i < list.length; i++) {
                        Object tmpObj = list[i];
                        if (i > 0) {
                            buffer.append(",");
                        }
                        if (tmpObj == null) {
                            buffer.append("null");
                        } else {
                            boolean noy = (tmpObj instanceof Double) || (tmpObj instanceof Long) || (tmpObj instanceof BigDecimal) || (tmpObj instanceof Boolean);
                            if (tmpObj instanceof BaseVO) {
                                buffer.append(((BaseVO) tmpObj).toJson());
                            } else if (noy) {
                                String valueTmp = SimpleTypeConvert.convert2String(tmpObj, "");
                                if (!noy) {
                                    buffer.append("\"");
                                }
                                buffer.append(JSonFactory.toJson(valueTmp));
                                if (!noy)
                                    buffer.append("\"");
                            } else {
                                buffer.append(JSonFactory.bean2json(value));
                            }
                        }
                    }
                    buffer.append("]");
                } else {
                    boolean noy = (value instanceof Double) || (value instanceof Long) || (value instanceof BigDecimal) || (value instanceof Boolean);
                    String valueTmp = SimpleTypeConvert.convert2String(value, "");
                    if (!noy && !"".equals(valueTmp))
                        buffer.append("\"");
                    buffer.append(JSonFactory.toJson(valueTmp));
                    if (!noy && !"".equals(valueTmp)) {
                        buffer.append("\"");
                    }
                }
            }
            atomicBoolean.set(true);
        });
        buffer.append("}");
        return buffer.toString();
    }
}
