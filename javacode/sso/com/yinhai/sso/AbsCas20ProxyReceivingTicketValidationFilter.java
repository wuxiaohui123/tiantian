package com.yinhai.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;

import com.yinhai.sysframework.config.SysConfig;

public abstract class AbsCas20ProxyReceivingTicketValidationFilter extends Cas20ProxyReceivingTicketValidationFilter {

	private Boolean isTransSSOLoginIdToLocalLoginId = Boolean.valueOf(false);

	private String localSystemId;

	protected void onSuccessfulValidation(HttpServletRequest request, HttpServletResponse response, Assertion assertion) {
		registLoalUserSession(request, response, assertion);
	}

	protected abstract void registLoalUserSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse,
			Assertion paramAssertion);

	protected abstract boolean isNeedAuthUrl(HttpServletRequest paramHttpServletRequest, String paramString);

	protected abstract void doLogoutCurrentSystem(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);

	protected String transSSOLoginIdToLocalLoginId(AttributePrincipal p) {
		if (!isTransSSOLoginIdToLocalLoginId.booleanValue()) {
			return p.getName();
		}

		String localLoginId = (String) p.getAttributes().get(getLocalSystemId());
		if ((localLoginId == null) || ("".equals(localLoginId))) {
			localLoginId = p.getName();
		}
		return localLoginId;
	}

	public String getLocalSystemId() {
		if (localSystemId != null) {
			return localSystemId;
		}
		return SysConfig.getSysConfig("curSyspathId", getLocalSystemId());
	}

	public Boolean getIsTransSSOLoginIdToLocalLoginId() {
		return isTransSSOLoginIdToLocalLoginId;
	}

	public void setIsTransSSOLoginIdToLocalLoginId(Boolean isTransSSOLoginIdToLocalLoginId) {
		this.isTransSSOLoginIdToLocalLoginId = isTransSSOLoginIdToLocalLoginId;
	}

	public void setLocalSystemId(String localSystemId) {
		this.localSystemId = localSystemId;
	}
}
