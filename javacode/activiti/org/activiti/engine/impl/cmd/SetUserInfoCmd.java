package org.activiti.engine.impl.cmd;

import java.io.Serializable;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

public class SetUserInfoCmd implements Command<Object>, Serializable {
	private static final long serialVersionUID = 1L;
	protected String userId;
	protected String type;
	protected String key;
	protected String value;

	public SetUserInfoCmd() {
	}

	public SetUserInfoCmd(String userId, String key, String value) {
		this.userId = userId;
		this.type = "userinfo";
		this.key = key;
		this.value = value;
	}

	public Object execute(CommandContext commandContext) {
		commandContext.getIdentityInfoEntityManager().setUserInfo(this.userId, this.type, this.key, this.value);
		return null;
	}
}
