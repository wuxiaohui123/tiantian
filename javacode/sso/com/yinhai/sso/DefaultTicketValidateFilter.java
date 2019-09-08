package com.yinhai.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.validation.Assertion;

public class DefaultTicketValidateFilter extends AbsCas20ProxyReceivingTicketValidationFilter {
	protected void registLoalUserSession(HttpServletRequest request, HttpServletResponse response, Assertion assertion) {
	}

	protected boolean isNeedAuthUrl(HttpServletRequest request, String url) {
		return false;
	}

	protected void doLogoutCurrentSystem(HttpServletRequest request, HttpServletResponse response) {
	}
}
