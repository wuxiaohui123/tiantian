package org.activiti.engine.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.NativeGroupQuery;
import org.activiti.engine.identity.NativeUserQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.cmd.CreateGroupCmd;
import org.activiti.engine.impl.cmd.CreateGroupQueryCmd;
import org.activiti.engine.impl.cmd.CreateMembershipCmd;
import org.activiti.engine.impl.cmd.CreateUserCmd;
import org.activiti.engine.impl.cmd.CreateUserQueryCmd;
import org.activiti.engine.impl.cmd.DeleteGroupCmd;
import org.activiti.engine.impl.cmd.DeleteMembershipCmd;
import org.activiti.engine.impl.cmd.DeleteUserCmd;
import org.activiti.engine.impl.cmd.DeleteUserInfoByUserIdCmd;
import org.activiti.engine.impl.cmd.DeleteUserInfoCmd;
import org.activiti.engine.impl.cmd.GetUserInfoCmd;
import org.activiti.engine.impl.cmd.GetUserInfoKeysCmd;
import org.activiti.engine.impl.cmd.SaveGroupCmd;
import org.activiti.engine.impl.cmd.SaveUserCmd;
import org.activiti.engine.impl.cmd.SetUserInfoCmd;
import org.activiti.engine.impl.cmd.SetUserInfoMapCmd;

public class IdentityServiceImpl extends ServiceImpl implements IdentityService {
	public User newUser(String userId) {
		return (User) commandExecutor.execute(new CreateUserCmd(userId));
	}

	public void saveUser(User user) {
		commandExecutor.execute(new SaveUserCmd(user));
	}

	public UserQuery createUserQuery() {
		return (UserQuery) commandExecutor.execute(new CreateUserQueryCmd());
	}

	public NativeUserQuery createNativeUserQuery() {
		return new NativeUserQueryImpl(commandExecutor);
	}

	public void deleteUser(String userId) {
		commandExecutor.execute(new DeleteUserCmd(userId));
	}

	public Group newGroup(String groupId) {
		return (Group) commandExecutor.execute(new CreateGroupCmd(groupId));
	}

	public GroupQuery createGroupQuery() {
		return (GroupQuery) commandExecutor.execute(new CreateGroupQueryCmd());
	}

	public NativeGroupQuery createNativeGroupQuery() {
		return new NativeGroupQueryImpl(commandExecutor);
	}

	public void saveGroup(Group group) {
		commandExecutor.execute(new SaveGroupCmd(group));
	}

	public void deleteGroup(String groupId) {
		commandExecutor.execute(new DeleteGroupCmd(groupId));
	}

	public void createMembership(String userId, String groupId) {
		commandExecutor.execute(new CreateMembershipCmd(userId, groupId));
	}

	public void deleteMembership(String userId, String groupId) {
		commandExecutor.execute(new DeleteMembershipCmd(userId, groupId));
	}

	public void setUserInfo(String userId, String key, String value) {
		commandExecutor.execute(new SetUserInfoCmd(userId, key, value));
	}

	public void setUserInfo(String userId, Map<String, String> userInfos) {
		commandExecutor.execute(new SetUserInfoMapCmd(userId, userInfos));
	}

	public String getUserInfo(String userId, String key) {
		return (String) commandExecutor.execute(new GetUserInfoCmd(userId, key));
	}

	public List<String> getUserInfoKeys(String userId) {
		return (List) commandExecutor.execute(new GetUserInfoKeysCmd(userId, "userinfo"));
	}

	public void deleteUserInfo(String userId, String key) {
		commandExecutor.execute(new DeleteUserInfoCmd(userId, key));
	}

	public void deleteUserInfoByUserId(String userId) {
		commandExecutor.execute(new DeleteUserInfoByUserIdCmd(userId));
	}
}
