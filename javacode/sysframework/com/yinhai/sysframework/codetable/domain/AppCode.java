package com.yinhai.sysframework.codetable.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.util.PinyinUtil;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName") 
public class AppCode extends BaseDomain {

	protected String codeType;
	protected String codeValue;
	protected String codeTypeDESC;
	protected String codeDESC;

	public AppCode() {
	}

	public AppCode(String codeType, String codeValue, String codeTypeDESC, String codeDESC, String yab003,
			String validFlag, Integer ver) {
		this.codeType = codeType;
		this.codeValue = codeValue;
		this.codeTypeDESC = codeTypeDESC;
		this.codeDESC = codeDESC;
		this.yab003 = yab003;
		this.validFlag = validFlag;
		this.ver = ver;
	}

	protected String yab003;

	protected String validFlag;

	protected String py;

	protected Integer ver;

	public Key getPK() {
		Key key = new Key();
		key.put("codeType", getCodeType());
		key.put("codeValue", getCodeValue());
		return key;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeTypeDESC(String codeTypeDESC) {
		this.codeTypeDESC = codeTypeDESC;
	}

	public String getCodeTypeDESC() {
		return codeTypeDESC;
	}

	public void setCodeDESC(String codeDESC) {
		this.codeDESC = codeDESC;
		setPy(PinyinUtil.converterToFirstSpell(codeDESC, false));
	}

	public String getCodeDESC() {
		return codeDESC;
	}

	public String getYab003() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}

	public String getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}

	public String toString() {
		return new ToStringBuilder(this).append("<br>�������(codeType):", getCodeType() + "<br>")
				.append("����ֵ(codeValue):", getCodeValue() + "<br>")
				.append("�������(codeTypeDESC):", getCodeTypeDESC() + "<br>")
				.append("��������(codeDESC):", getCodeDESC() + "<br>").append("��֯��������(orgId):", getYab003() + "<br>")
				.append("ע����־(validFlag):", getValidFlag() + "<br>").toString();
	}

	public java.util.Map toMap() {
		java.util.Map map = new java.util.HashMap();
		map.put("codeType", getCodeType());
		map.put("codeValue", getCodeValue());
		map.put("codeTypeDESC", getCodeTypeDESC());
		map.put("codeDESC", getCodeDESC());
		map.put("orgId", getYab003());
		map.put("validFlag", getValidFlag());
		return map;
	}

	public String toXMLString() {
		StringBuffer sb = new StringBuffer();
		String className = getClass().getName();
		String nodeName = className.substring(className.lastIndexOf(".") + 1);
		nodeName = nodeName.substring(0, 1).toLowerCase() + nodeName.substring(1);
		sb.append("<" + nodeName + ">\n")
				.append("\t<aaa100>" +getCodeType() + "</aaa100>\n")
				.append("\t<aaa102>" + getCodeValue()+ "</aaa102>\n")
				.append("\t<aaa101>" + getCodeTypeDESC() + "</aaa101>\n")
				.append("\t<aaa103>" + getCodeDESC() + "</aaa103>\n")
				.append("\t<yab003>" + getYab003() + "</yab003>\n")
				.append("\t<aae120>" + getValidFlag() + "</aae120>\n")
				.append("</" + nodeName + ">");

		return sb.toString();
	}

	public Integer getVer() {
		return ver;
	}

	public void setVer(Integer ver) {
		this.ver = ver;
	}

	public String getPy() {
		if (null == py) {
			setPy(PinyinUtil.converterToFirstSpell(codeDESC, false));
		}

		return py;
	}

	public void setPy(String py) {
		this.py = py;
	}
}
