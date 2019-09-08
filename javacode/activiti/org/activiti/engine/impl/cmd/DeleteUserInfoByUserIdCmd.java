package org.activiti.engine.impl.cmd;

import java.io.Serializable;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

public class DeleteUserInfoByUserIdCmd implements Command<Object>, Serializable {
	private static final long serialVersionUID = 1L;
	protected String userId;

	public DeleteUserInfoByUserIdCmd(String userId) {
		this.userId = userId;
	}

	public Object execute(CommandContext commandContext) {
		commandContext.getIdentityInfoEntityManager().deleteUserInfoByUserId(this.userId);
		return null;
	}
}
