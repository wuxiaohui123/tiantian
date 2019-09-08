package com.yinhai.ta3.sysapp.syslogmg.listener;

import java.util.concurrent.Executor;

import com.yinhai.sysframework.dto.BaseDTO;
import com.yinhai.sysframework.event.TaEvent;
import com.yinhai.sysframework.event.TaEventListener;
import com.yinhai.sysframework.log.UserLineSessionLogService;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.service.ServiceLocator;

public class OnLineLogListener extends BaseService implements TaEventListener {

	public void handleEvent(TaEvent event) {
		BaseDTO dto = event.getSource().getDto();
		if (dto != null) {
			String sessionId = dto.getAsString("login_sessionid");
			Long userId = dto.getAsLong("login_userid");
			String name = dto.getAsString("login_name");
			String clientIp = dto.getAsString("login_clientip");
			String serverIp = dto.getAsString("login_serverip");
			String resource = dto.getAsString("login_resource");
			UserLineSessionLogService lineService = ServiceLocator.getService("userLineSessionLogService",
					UserLineSessionLogService.class);
			lineService.saveLoginSessionLogByParam(sessionId, userId, name, clientIp, serverIp, resource);
		}
	}

	public String getEventType() {
		return "log_online";
	}

	public void setTaskExecutor(Executor taskExecutor) {
	}

	public Executor getTaskExecutor() {
		return null;
	}
}
