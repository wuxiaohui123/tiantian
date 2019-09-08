package org.activiti.engine.impl.persistence.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.impl.db.HasRevision;
import org.activiti.engine.impl.db.PersistentObject;

public class IdentityInfoEntity implements PersistentObject, HasRevision, Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_USERINFO = "userinfo";
	protected String id;
	protected int revision;
	protected String type;
	protected String userId;
	protected String key;
	protected String value;
	protected Map<String, String> details;

	public Object getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", this.id);
		persistentState.put("revision", Integer.valueOf(this.revision));
		persistentState.put("type", this.type);
		persistentState.put("userId", this.userId);
		persistentState.put("key", this.key);
		persistentState.put("value", this.value);
		return persistentState;
	}

	public int getRevisionNext() {
		return this.revision + 1;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRevision() {
		return this.revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getDetails() {
		return this.details;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}
}
