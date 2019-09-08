package com.yinhai.sysframework.app.domain;

import java.util.Map;

public abstract class AbstractDomainObject extends BaseVO implements DomainObject {

	public static final String KEY_VALUE = "`";
	private String objId;
	private transient Key key;

	public AbstractDomainObject() {
	}

	public AbstractDomainObject(Key key) {
		this.key = key;
	}

	@Override
	public String getObjId() {
		if (objId != null) {
			return objId;
		}
		if (getKey() != null)
			return getKey().getId();
		return null;
	}

	@Override
	public void setObjId(String objId) {
		this.objId = objId;
	}

	public Key getKey() {
		if (key != null)
			return key;
		if (objId != null)
			return new Key("id`" + objId);
		return getPK();
	}

	@Override
	public void setKey(Key key) {
		this.key = key;
	}

	public void setKey(String id) {
		key = new Key(id);
	}

	public boolean equals(Object object) {
		if (null == object)
			return false;
		if (this == object)
			return true;
		if (!(object instanceof AbstractDomainObject)) {
			return false;
		}
		return getKey().equals(((AbstractDomainObject) object).getKey());
	}

	public int hashCode() {
		return getKey().getSortedId().hashCode();
	}

	public String getDomainObjectName() {
		return getClass().getSimpleName();
	}

	public abstract Map toMap();

	public String toJson() {
		return super.toJson();
	}
}
