package com.yinhai.ta3.sysapp.configmg.action;

import java.util.List;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.configmg.service.IConfigSysPathMgService;
import com.yinhai.ta3.system.config.domain.ConfigSyspath;
import com.yinhai.webframework.BaseAction;

public class ConfigSysPathAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7609674138488733937L;
	private IConfigSysPathMgService configSysPathMgService = (IConfigSysPathMgService) super.getService("configSysPathMgService");

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		List<IConfigSyspath> list1 = configSysPathMgService.queryConfigSyspaths();
		setList("ssoGrid", list1);
		return super.execute();
	}

	public String querySyspath() throws Exception {
		IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
		List<IConfigSyspath> list1 = configService.getConfigSysPaths();
		setList("ssoGrid", list1);
		return JSON;
	}

	public String toSaveUpdateSyspath() throws Exception {
		String saveUpdate = request.getParameter("saveUpdate");
		if ("update".equals(saveUpdate)) {
			IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
			ConfigSyspath syspath = (ConfigSyspath) configService.getConfigSysPath(request.getParameter("id"));
			setData("id", syspath.getId());
			setData("name", syspath.getName());
			String url = syspath.getUrl();
			String sysipaddress = url.substring(7, url.lastIndexOf(":"));
			String temp = url.substring(url.lastIndexOf(":") + 1, url.length() - 1);
			String sysport = temp.substring(0, temp.lastIndexOf("/"));
			String contextroot = temp.substring(temp.indexOf("/") + 1);
			setData("sysipaddress", sysipaddress);
			setData("sysport", sysport);
			setData("contextroot", contextroot);
			setData("url", syspath.getUrl());
			setData("py", syspath.getPy());
			setData("iscur", syspath.getCursystem());
			setHideObj("btnSave");
			setShowObj("btnUpdate");
		} else {
			setHideObj("btnUpdate");
			setShowObj("btnSave");
			setEnable("id");
			setData("iscur", "1");
			setFocus("id");
		}
		return "saveUpdate";
	}

	public String saveUpdateSyspath() throws Exception {
		configSysPathMgService.saveUpdateSyspath(getDto());
		setSelectInputList("curSyspathId", configSysPathMgService.queryConfigSyspaths());
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		setData("curSyspathId", curSyspathId);
		return JSON;
	}

	public String removeSyspath() throws Exception {
		configSysPathMgService.removeSyspath(getDto().getAsString("id"));

		IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
		List<IConfigSyspath> list = configService.getConfigSysPaths();
		setSelectInputList("curSyspathId", list);
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		setData("curSyspathId", curSyspathId);
		if (ValidateUtil.isEmpty(list)) {
			setDisRequired("curSyspathId");
		}
		return JSON;
	}
}
