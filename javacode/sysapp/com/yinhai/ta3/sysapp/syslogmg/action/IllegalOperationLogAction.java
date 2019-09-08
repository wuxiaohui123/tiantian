package com.yinhai.ta3.sysapp.syslogmg.action;

import java.util.List;

import com.yinhai.sysframework.log.IIllegalOperationLog;
import com.yinhai.webframework.BaseAction;

public class IllegalOperationLogAction extends BaseAction {

	private IIllegalOperationLog illegalOperationLog = (IIllegalOperationLog) super.getService("illegalOperationLog");

	public String execute() throws Exception {
		List list = illegalOperationLog.queryIllegalOperationLog(null, null);
		setList("illegalOptGrid", list);
		return super.execute();
	}

	public String queryIllegalOperationLog() throws Exception {
		List list = illegalOperationLog.queryIllegalOperationLog(getDto().getAsDate("startDate"), getDto().getAsDate("endDate"));
		setList("illegalOptGrid", list);
		return JSON;
	}
}
