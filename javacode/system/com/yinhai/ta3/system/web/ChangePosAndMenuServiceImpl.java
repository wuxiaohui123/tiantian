package com.yinhai.ta3.system.web;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@WebService
public class ChangePosAndMenuServiceImpl extends WsBaseService implements IChangePosAndMenuService {

	private SimpleDao hibernateDao;

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public Position getPosition(Long positionId) {
		return (Position) hibernateDao.getSession().get(
				SysConfig.getSysConfig(Position.class.getName(), Position.class.getName()), positionId);
	}

	public Menu getMenu(Long menuId) {
		return (Menu) hibernateDao.getSession().get(SysConfig.getSysConfig(Menu.class.getName(), Menu.class.getName()),
				menuId);
	}
}
