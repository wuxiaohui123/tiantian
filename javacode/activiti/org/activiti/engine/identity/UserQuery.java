package org.activiti.engine.identity;

import org.activiti.engine.query.Query;

public abstract interface UserQuery extends Query<UserQuery, User> {
	public abstract UserQuery userId(String paramString);

	public abstract UserQuery userName(String paramString);

	public abstract UserQuery userNameLike(String paramString);

	public abstract UserQuery userSex(String paramString);

	public abstract UserQuery userSexLike(String paramString);

	public abstract UserQuery userDepartment(String paramString);

	public abstract UserQuery userDepartmentLike(String paramString);

	public abstract UserQuery userEmail(String paramString);

	public abstract UserQuery userEmailLike(String paramString);

	public abstract UserQuery userPhone(String paramString);

	public abstract UserQuery userPhoneLike(String paramString);

	public abstract UserQuery memberOfGroup(String paramString);

	public abstract UserQuery potentialStarter(String paramString);

	public abstract UserQuery orderByUserId();

	public abstract UserQuery orderByUserName();

	public abstract UserQuery orderByUserSex();

	public abstract UserQuery orderByUserDepartment();

	public abstract UserQuery orderByUserEmail();

	public abstract UserQuery orderByUserPhone();
}
