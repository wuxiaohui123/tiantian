package com.yinhai.sysframework.security.ta3;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.security.OnlineSessionInfo;

@SuppressWarnings("rawtypes")
public class TaSessionManager implements ApplicationListener {

	public static final String LOGIN_SESSIONID = "login_sessionid";
	public static final String LOGIN_USERID = "login_userid";
	public static final String LOGIN_CLIENTIP = "login_clientip";
	public static final String LOGIN_SERVERIP = "login_serverip";
	public static final String LOGIN_SESSIONTIME = "login_sessiontime";
	public static final String LOGIN_END_ACCESS_SESSIONTIME = "login_end_access_sessiontime";
	private final Map<String, OnlineSessionInfo> users = new ConcurrentHashMap<String, OnlineSessionInfo>();

	private final Map<String, String> expiredSession = new ConcurrentHashMap<String, String>();

	private final Set<HttpSession> sessions = Collections.synchronizedSet(new HashSet<HttpSession>());

	public void registerNewSession(HttpSession session, OnlineSessionInfo user) {
		user.setSession(session);
		users.put(session.getId(), user);
	}

	public void invalidUsersession(String loginid, String sessionid) {
		for (Iterator<OnlineSessionInfo> it = users.values().iterator(); it.hasNext();) {
			OnlineSessionInfo next = (OnlineSessionInfo) it.next();

			if ((next.getLoginId().equals(loginid)) && (!next.getSession().getId().equals(sessionid))) {

				it.remove();

				next.getSession().removeAttribute("ta3.userinfo");
				expiredSession.put(next.getSession().getId(), "session");
				break;
			}
		}
	}

	public void invalidCurrentUsersession(String loginid, String sessionid) {
		for (Iterator<OnlineSessionInfo> it = users.values().iterator(); it.hasNext();) {
			OnlineSessionInfo next = (OnlineSessionInfo) it.next();

			if ((next.getLoginId().equals(loginid)) && (next.getSession().getId().equals(sessionid))) {

				next.getSession().invalidate();
				break;
			}
		}
	}

	public Map<String, OnlineSessionInfo> getUsers() {
		return users;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if ((event instanceof TaHttpSessionCreateEvent)) {
			sessions.add(((TaHttpSessionCreateEvent) event).getSession());
		}
		if ((event instanceof TaHttpSessionDestoryEvent)) {
			HttpSession session = ((TaHttpSessionDestoryEvent) event).getSession();

			for (Iterator<OnlineSessionInfo> it = users.values().iterator(); it.hasNext();) {
				OnlineSessionInfo onlineSessionInfo = (OnlineSessionInfo) it.next();

				if ((onlineSessionInfo.getSession() != null) && (onlineSessionInfo.getSession().getId().equals(session.getId()))) {
					ParamDTO dto = new ParamDTO();
					IUserAccountInfo userAccount = (IUserAccountInfo) session.getAttribute("ta3.userinfo");
					if (userAccount != null) {
						dto.put("login_userid", userAccount.getUser().getUserid());
						dto.put("login_sessionid", session.getId());
						dto.put("login_clientip", onlineSessionInfo.getClientIp());
						dto.put("login_serverip", onlineSessionInfo.getUseRealServer());
						dto.put("login_end_access_sessiontime", new Timestamp(session.getLastAccessedTime()));
						TaEventPublisher.publishEvent(new EventSource(session, dto), "log_offline");
					}
					it.remove();
				}
			}
			sessions.remove(session);
		}
	}

	public Set<HttpSession> getSessions() {
		return sessions;
	}

	public Map<String, String> getExpiredSession() {
		return expiredSession;
	}
}
