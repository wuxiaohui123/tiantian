package com.yinhai.sysframework.event;

import java.util.EventObject;

public class TaEvent extends EventObject {

	public String eventType;

	public TaEvent(EventSource source, String eventType) {
		super(source);
		this.eventType = eventType;
	}

	public EventSource getSource() {
		return (EventSource) source;
	}

	public interface EVENT_TYPE {
		 String user_create = "user_create";
		 String user_update = "user_update";
		 String user_unused = "user_unused";
		 String user_use = "user_use";
		 String user_delete = "user_delete";
		 String org_create = "org_create";
		 String org_update = "org_update";
		 String org_unused = "org_unused";
		 String org_use = "org_use";
		 String org_delete = "org_delete";
		 String org_changeOrg = "org_changeOrg";
		 String position_create = "position_create";
		 String position_update = "position_update";
		 String position_unused = "position_unused";
		 String position_use = "position_use";
		 String position_delete = "position_delete";
		 String grant_permission = "grant_permission";
		 String receive_permission = "receive_permission";
		 String permission_change = "permission_change";
		 String session_create = "session_create";
		 String session_destroy = "session_destroy";
		 String access_log = "access_log";
		 String log_on_off_line = "log_on_off_line";
		 String log_offline = "log_offline";
		 String log_online = "log_online";
		 String position_user = "position_user";
	}
}
