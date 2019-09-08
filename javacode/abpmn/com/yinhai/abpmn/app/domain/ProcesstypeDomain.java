package com.yinhai.abpmn.app.domain;

import java.util.HashMap;
import java.util.Map;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.app.domain.DomainMeta;
import com.yinhai.sysframework.app.domain.Key;

public class ProcesstypeDomain extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3055671339851342959L;

	private String typecode;
	private String typename;
	private String parenttypecode;
	private Integer orderno;

	public ProcesstypeDomain() {
	}

	public ProcesstypeDomain(String typecode, String typename, String parenttypecode, Integer orderno) {
		this.typecode = typecode;
		this.typename = typename;
		this.parenttypecode = parenttypecode;
		this.orderno = orderno;
	}

	@SuppressWarnings("unchecked")
	public Key getPK() {
		Key key = new Key();
		if (getTypecode() == null) {
			throw new IllegalArgumentException("主键typecode不能为空。");
		}
		key.put("typecode", getTypecode());
		return key;
	}

	public String getTypecode() {
		return typecode;
	}

	public void setTypecode(String typecode) {
		this.typecode = typecode;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getParenttypecode() {
		return parenttypecode;
	}

	public void setParenttypecode(String parenttypecode) {
		this.parenttypecode = parenttypecode;
	}

	public Integer getOrderno() {
		return orderno;
	}

	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("typecode", getTypecode());
		map.put("typename", getTypename());
		map.put("parenttypecode", getParenttypecode());
		map.put("orderno", getOrderno());
		return map;
	}

	public DomainMeta getMetadata() {
		DomainMeta domainMeta = new DomainMeta("ProcesstypeDomain", "abpmn_processtype", "流程分类表Abpmn_processtype", "abpmn_processtype",
				"javacode/abpmn/com/yinhai/abpmn/app/domain/processtype.xml");

		domainMeta.appendField("typecode", "typecode", "分类代码", "String", "VARCHAR2(19)", 19, true, true, false);

		domainMeta.appendField("typename", "typename", "分类名称", "String", "VARCHAR2(50)", 50, false, true, false);

		domainMeta.appendField("parenttypecode", "parenttypecode", "上级分类代码", "String", "VARCHAR2(19)", 19, false, false, false);

		domainMeta.appendField("orderno", "orderno", "同级顺序", "Integer", "NUMBER(3)", 3, false, true, false);

		return domainMeta;
	}
}
