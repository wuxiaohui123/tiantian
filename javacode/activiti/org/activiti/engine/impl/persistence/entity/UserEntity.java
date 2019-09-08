package org.activiti.engine.impl.persistence.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.identity.User;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.HasRevision;
import org.activiti.engine.impl.db.PersistentObject;

public class UserEntity implements User, Serializable, PersistentObject, HasRevision {
	private static final long serialVersionUID = 1L;
	protected String id;
	protected int revision;
	protected String name;
	protected String sex;
	protected String department;
	protected String email;
	protected String phone;

	public UserEntity() {
	}

	public UserEntity(String id) {
		this.id = id;
	}

	public void delete() {
		Context.getCommandContext().getDbSqlSession().delete(this);
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public int getRevision() {
		return this.revision;
	}

	public int getRevisionNext() {
		return this.revision + 1;
	}

	public Object getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("name", this.name);
		persistentState.put("sex", this.sex);
		persistentState.put("department", this.department);
		persistentState.put("email", this.email);
		persistentState.put("phone", this.phone);
		return persistentState;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
