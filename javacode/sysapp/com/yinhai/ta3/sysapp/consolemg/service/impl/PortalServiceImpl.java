package com.yinhai.ta3.sysapp.consolemg.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.consolemg.domain.ConsoleModule;
import com.yinhai.ta3.sysapp.consolemg.domain.ConsoleModuleLocation;
import com.yinhai.ta3.sysapp.consolemg.service.PortalService;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@WebService
public class PortalServiceImpl extends WsBaseService implements PortalService {

	private SimpleDao hibernateDao;

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public List<ConsoleModule> getUserModuleList(Long userId, Set<String> urls) {
		String isPortal = SysConfig.getSysConfig("isPortal", "false");
		List<Menu> list = new ArrayList();
		if ("true".equals(isPortal)) {
			list = hibernateDao.createQuery(
					"select m from " + super.getEntityClassName(Menu.class.getName())
							+ " m where m.consolemodule=? and m.effective=? order by m.menuid", new Object[] { "0", "0" }).list();
		} else {
			String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
			list = hibernateDao.createQuery(
					"select m from " + super.getEntityClassName(Menu.class.getName())
							+ " m where m.consolemodule=? and m.effective=? and m.syspath=? order by m.menuid",
					new Object[] { "0", "0", curSyspathId }).list();
		}
		IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
		List<IConfigSyspath> configSyspaths = configService.getConfigSysPaths();
		if (IUser.ROOT_USERID.equals(userId)) {
			List<ConsoleModule> list1 = new ArrayList();
			for (Menu m : list) {
				ConsoleModule cm = new ConsoleModule();
				cm.setModuleid(m.getMenuid());
				cm.setModulename(m.getMenuname());
				if ("true".equals(isPortal)) {
					for (IConfigSyspath configSyapath : configSyspaths) {
						if (configSyapath.getId().equals(m.getSyspath())) {
							cm.setModuleurl(configSyapath.getUrl() + m.getUrl());
							break;
						}
					}
				} else {
					cm.setModuleurl(m.getUrl());
				}

				list1.add(cm);
			}
			return list1;
		}
		List<ConsoleModule> list1 = new ArrayList();
		for (Menu m : list) {
			if (urls.contains("/" + m.getUrl())) {
				ConsoleModule cm = new ConsoleModule();
				cm.setModuleid(m.getMenuid());
				cm.setModulename(m.getMenuname());
				if ("true".equals(isPortal)) {
					for (IConfigSyspath configSyapath : configSyspaths) {
						if (configSyapath.getId().equals(m.getSyspath())) {
							cm.setModuleurl(configSyapath.getUrl() + m.getUrl());
							break;
						}
					}
				} else {
					cm.setModuleurl(m.getUrl());
				}
				list1.add(cm);
			}
		}
		return list1;
	}

	public void saveLocationInfo(String location, String pageFlag, Long positionId) {
		List<ConsoleModuleLocation> lst = hibernateDao.createQuery("from ConsoleModuleLocation cml where cml.mark=? and cml.positionid=?",
				new Object[] { pageFlag, positionId }).list();

		if ((null == lst) || (lst.size() == 0)) {
			ConsoleModuleLocation cml = new ConsoleModuleLocation();
			cml.setLocation(location);
			cml.setMark(pageFlag);
			cml.setPositionid(positionId);
			hibernateDao.save(cml);
		} else {
			ConsoleModuleLocation cml = (ConsoleModuleLocation) lst.get(0);
			hibernateDao.createQuery("update ConsoleModuleLocation cml set cml.location=? where cml.mark=? and cml.positionid=?",
					new Object[] { location, pageFlag, positionId }).executeUpdate();
		}
	}

	public String getLocationInfo(String pageFlag, Long positionId) {
		String hql = "select distinct cml from ConsoleModuleLocation cml where cml.mark=? and cml.positionid =?";
		List<ConsoleModuleLocation> list = hibernateDao.createQuery(hql, pageFlag, positionId).list();
		if (!ValidateUtil.isEmpty(list)) {
			ConsoleModuleLocation consoleModuleLocation = (ConsoleModuleLocation) list.get(0);
			return consoleModuleLocation.getLocation();
		}
		return null;
	}
}
