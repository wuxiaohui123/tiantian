package org.activiti.engine.identity;

import java.io.Serializable;

public abstract interface User extends Serializable {
	public abstract String getId();

	public abstract void setId(String paramString);

	public abstract String getName();

	public abstract void setName(String paramString);

	public abstract String getSex();

	public abstract void setSex(String paramString);

	public abstract String getDepartment();

	public abstract void setDepartment(String paramString);

	public abstract String getEmail();

	public abstract void setEmail(String paramString);

	public abstract String getPhone();

	public abstract void setPhone(String paramString);
}
