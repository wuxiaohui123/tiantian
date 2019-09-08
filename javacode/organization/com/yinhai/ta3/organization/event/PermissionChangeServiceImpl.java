package com.yinhai.ta3.organization.event;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;


@WebService
public class PermissionChangeServiceImpl extends WsBaseService implements PermissionChangeService {

	private SimpleDao hibernateDao;
	private IPermissionService permissionServcie;

	@WebMethod(exclude = true)
	public void setPermissionServcie(IPermissionService permissionServcie) {
		this.permissionServcie = permissionServcie;
	}

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void refreshFunctionMemory(Long positionid, Long menuid) {
		StringBuffer hql = new StringBuffer();
		String userClassName = SysConfig.getSysConfig(User.class.getName(), User.class.getName());
		String positionClassName = SysConfig.getSysConfig(Position.class.getName(), Position.class.getName());
		hql.append("select distinct u.userid from ").append(userClassName).append(" u,UserPosition up,")
				.append(positionClassName).append(" p").append(" where p.positionid=up.id.taposition.positionid")
				.append(" and up.id.tauser.userid=u.userid").append(" and p.positionid=?")
				.append(" and (u.destory is null or u.destory=?)");

		List<Long> userids = hibernateDao.createQuery(hql.toString(), new Object[] { positionid, "1" }).list();
		for (Long userid : userids) {
			permissionServcie.clearUserPermissionMenusCache(userid);
			permissionServcie.clearUserPermissionMenusCache(userid, positionid);
			permissionServcie.clearUserPermissionUrlCache(userid);
			permissionServcie.clearUserPermissionUrlCache(userid, positionid);
			permissionServcie.clearPositionsByMenuCache(userid, menuid);
			permissionServcie.clearUserEffectivePositionsCache(userid);
		}
	}

}
