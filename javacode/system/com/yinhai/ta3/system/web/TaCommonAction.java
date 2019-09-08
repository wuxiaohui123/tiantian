package com.yinhai.ta3.system.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.webframework.BaseAction;

public class TaCommonAction extends BaseAction {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8565583203510229573L;

	public String execute() throws Exception {
		writeSuccess();
		return null;
	}

	public final String taResetCurrentPage() throws Exception {
		IMenu menu = WebUtil.getCurrentMenu(request);

		if ((menu != null) && (StringUtil.isNotEmpty(menu.getUrl()))) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.sendRedirect(menu.getUrl());
			return null;
		}
		return null;
	}
}
