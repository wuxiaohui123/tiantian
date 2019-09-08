package org.activiti.engine.impl;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.query.QueryProperty;

public class UserQueryProperty implements QueryProperty {
	private static final long serialVersionUID = 1L;
	private static final Map<String, UserQueryProperty> properties = new HashMap<String, UserQueryProperty>();
	public static final UserQueryProperty USER_ID = new UserQueryProperty("RES.ID_");
	public static final UserQueryProperty NAME = new UserQueryProperty("RES.NAME_");
	public static final UserQueryProperty SEX = new UserQueryProperty("RES.SEX_");
	public static final UserQueryProperty DEPARTMENT = new UserQueryProperty("RES.DEPT_");
	public static final UserQueryProperty EMAIL = new UserQueryProperty("RES.EMAIL_");
	public static final UserQueryProperty PHONE = new UserQueryProperty("RES.PHONE_");
	private String name;

	public UserQueryProperty(String name) {
		this.name = name;
		properties.put(name, this);
	}

	public String getName() {
		return this.name;
	}

	public static UserQueryProperty findByName(String propertyName) {
		return (UserQueryProperty) properties.get(propertyName);
	}
}
