package org.activiti.engine;

import java.util.List;
import java.util.Map;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.NativeGroupQuery;
import org.activiti.engine.identity.NativeUserQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

public abstract interface IdentityService {
	public abstract User newUser(String paramString);

	public abstract void saveUser(User paramUser);

	public abstract UserQuery createUserQuery();

	public abstract NativeUserQuery createNativeUserQuery();

	public abstract void deleteUser(String paramString);

	public abstract Group newGroup(String paramString);

	public abstract GroupQuery createGroupQuery();

	public abstract NativeGroupQuery createNativeGroupQuery();

	public abstract void saveGroup(Group paramGroup);

	public abstract void deleteGroup(String paramString);

	public abstract void createMembership(String paramString1, String paramString2);

	public abstract void deleteMembership(String paramString1, String paramString2);

	public abstract void setUserInfo(String paramString1, String paramString2, String paramString3);

	public abstract void setUserInfo(String paramString, Map<String, String> paramMap);

	public abstract String getUserInfo(String paramString1, String paramString2);

	public abstract List<String> getUserInfoKeys(String paramString);

	public abstract void deleteUserInfo(String paramString1, String paramString2);

	public abstract void deleteUserInfoByUserId(String paramString);
}
