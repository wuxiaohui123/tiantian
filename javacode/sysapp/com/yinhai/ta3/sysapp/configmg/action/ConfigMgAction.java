package com.yinhai.ta3.sysapp.configmg.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.configmg.service.IConfigSysPathMgService;
import com.yinhai.webframework.BaseAction;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConfigMgAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766762701831052118L;
	private IConfigSysPathMgService configMgService = getService("configSysPathMgService",IConfigSysPathMgService.class);

	public String execute() throws Exception {
		List<IConfigSyspath> list1 = configMgService.queryConfigSyspaths();
		setSelectInputList("curSyspathId", list1);
		Map configs = SysConfig.getConfigs();
		Iterator it = configs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> m = (Map.Entry) it.next();
			setData(String.valueOf(m.getKey()), m.getValue());
		}
		if (!ValidateUtil.isEmpty(list1)) {
			setRequired("curSyspathId");
		}
		return super.execute();
	}
}
