package com.yinhai.ta3.sysapp.syslogmg.service.impl;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.BaseDTO;
import com.yinhai.sysframework.log.UserLineSessionLogService;
import com.yinhai.sysframework.service.WsBaseService;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.syslogmg.domain.Taloginhistorylog;
import com.yinhai.ta3.sysapp.syslogmg.domain.Taonlinelog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.sql.Timestamp;

@WebService
public class UserLineSessionLogServiceImpl extends WsBaseService implements UserLineSessionLogService {

	private static final Logger log = LoggerFactory.getLogger(UserLineSessionLogServiceImpl.class);
	private SimpleDao hibernateDao;
	private ITimeService timeService;

	@WebMethod(exclude = true)
	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	public void saveLoginSessionLogByParam(String sessionid, Long userid, String name, String clientip, String serverip,
			String resource) {
		if ((ValidateUtil.isEmpty(sessionid)) || (ValidateUtil.isEmpty(userid))) {
			log.info("错误的登录记");
		} else {
			Taonlinelog online = new Taonlinelog();
			online.setCurresource(resource);
			online.setName(name);
			online.setClientip(clientip);
			online.setServerip(serverip);
			online.setLogintime(timeService.getSysTimestamp());
			online.setSessionid(sessionid);
			online.setUserid(userid);
			online.setSyspath(SysConfig.getSysConfig("curSyspathId", "sysmg"));
			hibernateDao.save(online);
		}
	}

	public void saveLoginSessionLog(BaseDTO dto) {
		String sessionid = dto.getAsString("login_sessionid");
		Long userid = dto.getAsLong("login_userid");
		if ((ValidateUtil.isEmpty(sessionid)) || (ValidateUtil.isEmpty(userid))) {
			log.info("错误的登录记");
		} else {
			Taonlinelog online = new Taonlinelog();
			online.setName(dto.getAsString("login_name"));
			online.setCurresource(dto.getAsString("login_resource"));
			online.setClientip(dto.getAsString("login_clientip"));
			online.setServerip(dto.getAsString("login_serverip"));
			online.setLogintime(dto.getAsTimestamp("login_sessiontime"));
			online.setSessionid(sessionid);
			online.setUserid(userid);
			hibernateDao.save(online);
		}
	}

	public void saveOutLineSessionLogByParam(String sessionid, Long userid, String clientip, String serverip) {
		Timestamp lastTime = timeService.getSysTimestamp();
		if ((ValidateUtil.isEmpty(sessionid)) || (ValidateUtil.isEmpty(lastTime))) {
			log.info("错误的下线记录");
		} else {
			Taonlinelog online = (Taonlinelog) hibernateDao.createQuery("from Taonlinelog t where t.sessionid=? and t.userid=?",
						new Object[] { sessionid, userid }).uniqueResult();
			if (online != null) {
				Taloginhistorylog history = new Taloginhistorylog();
				history.setUserid(userid);
				history.setName(online.getName());
				history.setClientip(clientip);
				history.setServerip(serverip);
				history.setLogintime(online.getLogintime());
				history.setLogouttime(lastTime);
				history.setSessionid(sessionid);
				hibernateDao.save(history);
				hibernateDao.delete(online);
			}
		}
	}

	public void saveOutLineSessionLog(BaseDTO dto) {
		String sessionid = dto.getAsString("login_sessionid");
		Timestamp lastTime = dto.getAsTimestamp("login_end_access_sessiontime");
		Long userid = dto.getAsLong("login_userid");
		if ((ValidateUtil.isEmpty(sessionid)) || (ValidateUtil.isEmpty(lastTime))) {
			log.info("错误的下线记录");
		} else {
			Taonlinelog online = (Taonlinelog) hibernateDao.createQuery("from Taonlinelog t where t.sessionid=? and t.userid=?",
						new Object[] { sessionid, userid }).uniqueResult();
			if (online != null) {
				Taloginhistorylog history = new Taloginhistorylog();
				history.setUserid(userid);
				history.setName(online.getName());
				history.setClientip(dto.getAsString("login_clientip"));
				history.setServerip(dto.getAsString("login_serverip"));
				history.setLogintime(online.getLogintime());
				history.setLogouttime(lastTime);
				history.setSessionid(sessionid);
				hibernateDao.save(history);
				hibernateDao.delete(online);
			}
		}
	}

	@WebMethod(exclude = true)
	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

}
