package com.yinhai.ta3.sysapp.syslogmg.listener;

import java.util.concurrent.Executor;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.TaEvent;
import com.yinhai.sysframework.event.TaEventListener;
import com.yinhai.sysframework.log.UserLineSessionLogService;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.service.ServiceLocator;

public class OffLineLogListener extends BaseService implements TaEventListener {

	public void handleEvent(TaEvent event) {
		ParamDTO dto = event.getSource().getDto();
		String sessionId = dto.getAsString("login_sessionid");
		Long userId = dto.getAsLong("login_userid");
		String clientIp = dto.getAsString("login_clientip");
		String serverIp = dto.getAsString("login_serverip");
		UserLineSessionLogService lineService = ServiceLocator.getService("userLineSessionLogService",
				UserLineSessionLogService.class);
		lineService.saveOutLineSessionLogByParam(sessionId, userId, clientIp, serverIp);
	}

	public String getEventType() {
		return "log_offline";
	}

	public void setTaskExecutor(Executor taskExecutor) {
	}

	public Executor getTaskExecutor() {
		return null;
	}
}
