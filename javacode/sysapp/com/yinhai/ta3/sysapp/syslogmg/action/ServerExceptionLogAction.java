package com.yinhai.ta3.sysapp.syslogmg.action;

import java.sql.Timestamp;
import java.util.List;

import com.yinhai.sysframework.log.ServerExceptionService;
import com.yinhai.sysframework.log.Taserverexceptionlog;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.webframework.BaseAction;

public class ServerExceptionLogAction extends BaseAction {

	private ServerExceptionService service = (ServerExceptionService) getService("serverExceptionService");

	public String execute() throws Exception {
		return "success";
	}
	
	public String goDetail() throws Exception {
		String id = getDto().getAsString("id");
		Taserverexceptionlog log = service.getTaserverexceptionlog(id);
		setData("type", log.getType());
		setData("detail", log.getContentStr());
		return "detail";
	}

	public String goTest() throws Exception {
		List<Taserverexceptionlog> list = service.getList(null, null);
		setList("docDetails", list);
		return JSON;
	}

	public String query() throws Exception {
		Timestamp begin = getDto().getAsTimestamp("begin");
		Timestamp end = getDto().getAsTimestamp("end");
		int start = getDto().getStart("MainGrid").intValue();
		int limit = getDto().getLimit("MainGrid").intValue();
		PageBean pg = service.getPage("MainGrid", begin, end, start, limit);
		setList("MainGrid", pg);

		return JSON;
	}

	public String queryLinmit() throws Exception {
		Timestamp begin = getDto().getAsTimestamp("begin");
		Timestamp end = getDto().getAsTimestamp("end");
		int start = getDto().getStart("MainGrid").intValue();
		int limit = getDto().getLimit("MainGrid").intValue();
		PageBean pg = service.getPageByCount("MainGrid", begin, end, start, limit);
		setList("MainGrid", pg);
		return JSON;
	}

	public String delete() throws Exception {
		String id = getDto().getAsString("id");
		service.delete(id);
		return query();
	}
}
