package com.yinhai.sysframework.security.ta3;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEvent;

public class TaHttpSessionCreateEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3944277794760194702L;

	public TaHttpSessionCreateEvent(HttpSession session) {
		super(session);
	}

	public HttpSession getSession() {
		return (HttpSession) getSource();
	}
}
