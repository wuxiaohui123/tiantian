package com.yinhai.sso;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;

public class PrincipalHelper {

	protected static AttributePrincipal retrievePrincipalFromSessionOrRequest(ServletRequest servletRequest) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpSession session = request.getSession(false);
		Assertion assertion = (Assertion) (session == null ? request.getAttribute("_const_cas_assertion_") : session
				.getAttribute("_const_cas_assertion_"));

		return assertion == null ? null : assertion.getPrincipal();
	}
}
