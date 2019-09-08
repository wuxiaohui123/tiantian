package com.yinhai.sysframework.security.ta3;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class TaSessionListener implements HttpSessionListener {

	ApplicationContext getContext(ServletContext servletContext) {
		return WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}

	public void sessionCreated(HttpSessionEvent event) {
		TaHttpSessionCreateEvent e = new TaHttpSessionCreateEvent(event.getSession());

		getContext(event.getSession().getServletContext()).publishEvent(e);
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		TaHttpSessionDestoryEvent e = new TaHttpSessionDestoryEvent(event.getSession());
		getContext(event.getSession().getServletContext()).publishEvent(e);
	}
}
