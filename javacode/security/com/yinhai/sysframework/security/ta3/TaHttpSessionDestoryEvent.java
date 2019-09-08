package com.yinhai.sysframework.security.ta3;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEvent;

public class TaHttpSessionDestoryEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4107609253254966665L;

	public TaHttpSessionDestoryEvent(HttpSession session) {
		super(session);
	}

	public HttpSession getSession() {
		return (HttpSession) getSource();
	}
}
