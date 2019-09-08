package com.yinhai.ta3.system.security.ta3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IOrganizationService;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.security.ta3.ILoadUserAccountInfo;
import com.yinhai.sysframework.security.ta3.IRoleAuthrity;
import com.yinhai.sysframework.security.ta3.IUserAccountInfo;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;

public class LoadUserAccountService implements ILoadUserAccountInfo {

	private SimpleDao hibernateDao;
	private IOrganizationService organizationService;

	@Override
	public IUserAccountInfo loadUser(String loginId, HttpServletRequest request) {
		User user = getUser(loginId);
		if (user == null) {
			return null;
		}
		DefaultUserAccountInfo userAccount = new DefaultUserAccountInfo(user);

		IPosition mainp = organizationService.getUserMainPosition(user.getUserid());
		userAccount.getUser().setNowPosition(mainp);

		List<IPosition> positions = organizationService.getUserPositions(user.getUserid());

		Set<IRoleAuthrity> set = new HashSet<IRoleAuthrity>();
		for (int i = 0; i < positions.size(); i++) {
			if ((i == 0) && (mainp == null)) {
				userAccount.getUser().setNowPosition((IPosition) positions.get(i));
			}
			set.add(new DefaultRoleAuthrity((IPosition) positions.get(i)));
		}
		if (userAccount.getUser().getNowPosition() != null) {
			Position nowPosition = (Position) userAccount.getUser().getNowPosition();
			IOrg org = organizationService.getOrg(nowPosition.getOrgid());
			userAccount.getUser().setYab003(org.getYab003());
			userAccount.getUser().setYab139(org.getYab139());

			userAccount.getUser().setDepartId(((User) userAccount.getUser()).getDirectorgid().toString());
			nowPosition.setTaorg((Org) org);
		}

		userAccount.setRoleAuthoritys(set);
		return userAccount;
	}

	protected User getUser(String loginId) {
		String userClassName = SysConfig.getSysConfig(User.class.getName(), User.class.getName());
		String queryUserSql = "from " + userClassName + " u where u.loginid=? and (u.destory=? or u.destory is null)";
		return (User) hibernateDao.createQuery(queryUserSql,  loginId, "1" ).uniqueResult();
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setOrganizationService(IOrganizationService organizationService) {
		this.organizationService = organizationService;
	}
}
