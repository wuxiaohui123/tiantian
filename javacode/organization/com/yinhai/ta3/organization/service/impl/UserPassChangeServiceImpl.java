package com.yinhai.ta3.organization.service.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.Md5PasswordEncoder;
import com.yinhai.ta3.organization.service.IUserPassChangeService;
import com.yinhai.ta3.system.org.domain.User;

@WebService
public class UserPassChangeServiceImpl extends WsBaseService implements IUserPassChangeService {

	private SimpleDao hibernateDao;
	private ITimeService timeService;
	private Md5PasswordEncoder md5PasswordEncoder;

	@WebMethod(exclude = true)
	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	@WebMethod(exclude = true)
	public void setMd5PasswordEncoder(Md5PasswordEncoder md5PasswordEncoder) {
		this.md5PasswordEncoder = md5PasswordEncoder;
	}

	public User getUser(String loginId) {
		return (User) hibernateDao.createQuery(
				"from " + super.getEntityClassName(User.class.getName()) + " u where u.loginid=? and (u.destory is null or u.destory=?)",
				new Object[] { loginId, "1" }).uniqueResult();
	}

	public void resetPassword(Long userId, String newPass) {
		User u = (User) hibernateDao.createQuery(
				"from " + super.getEntityClassName(User.class.getName()) + " u where u.userid=? and (u.destory is null or u.destory=?)",
				new Object[] { userId, "1" }).uniqueResult();
		String loginid = u.getLoginid();
		String encodePassword = md5PasswordEncoder.encodePassword(newPass, loginid);
		u.setPassword(encodePassword);
		u.setPwdlastmodifydate(timeService.getSysTimestamp());
		hibernateDao.update(u);
	}

	public String encodePassword(String newPass, String loginId) {
		return md5PasswordEncoder.encodePassword(newPass, loginId);
	}
}
