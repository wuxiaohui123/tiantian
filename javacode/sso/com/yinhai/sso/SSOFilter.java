package com.yinhai.sso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.AbstractConfigurationFilter;

public class SSOFilter extends AbstractConfigurationFilter {
	private String ssoLogoutUrl;
	private String clientLogoutUrl;

	public SSOFilter() {
		clientLogoutUrl = "/dologout";
	}

	private static SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
	private static AuthenticationFilter authenticationFilter = new AuthenticationFilter();
	private static AbsCas20ProxyReceivingTicketValidationFilter cas20ProxyReceivingTicketValidationFilter = new DefaultTicketValidateFilter();

	private static final List<Filter> filterList = new ArrayList<Filter>();

	private static class VirtualFilterChain implements FilterChain {
		private final FilterChain originalChain;
		private final List<Filter> additionalFilters;
		private int currentPosition = 0;

		private VirtualFilterChain(FilterChain chain, List<Filter> additionalFilters) {
			originalChain = chain;
			this.additionalFilters = additionalFilters;
		}

		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			if (currentPosition == additionalFilters.size()) {
				originalChain.doFilter(request, response);
			} else {
				currentPosition += 1;
				Filter nextFilter = (Filter) additionalFilters.get(currentPosition - 1);
				nextFilter.doFilter(request, response, this);
			}
		}
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filter) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String url = request.getRequestURI();
		url = url.substring(request.getContextPath().length(), url.length());
		if (clientLogoutUrl.equals(url)) {
			cas20ProxyReceivingTicketValidationFilter.doLogoutCurrentSystem(request, response);
			response.reset();
			String ret_url = request.getParameter("ret_url");
			String tmp = new String(ssoLogoutUrl);
			if ((ret_url != null) && (!"".equals(ret_url))) {
				tmp = tmp + "?service=" + ret_url;
			}
			response.sendRedirect(tmp);
			return;
		}
		if (cas20ProxyReceivingTicketValidationFilter.isNeedAuthUrl(request, url)) {
			VirtualFilterChain vf = new VirtualFilterChain(filter, filterList);
			vf.doFilter(req, resp);

		} else {

			filter.doFilter(req, resp);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		singleSignOutFilter.init(filterConfig);
		authenticationFilter.init(filterConfig);

		filterList.add(singleSignOutFilter);
		filterList.add(authenticationFilter);

		String className = filterConfig.getInitParameter("ticketValidationFilter");
		if (className != null) {
			try {
				getClass();
				cas20ProxyReceivingTicketValidationFilter = (AbsCas20ProxyReceivingTicketValidationFilter) Class.forName(className).newInstance();
			} catch (Exception e) {
			}
		}
		cas20ProxyReceivingTicketValidationFilter.init(filterConfig);
		filterList.add(cas20ProxyReceivingTicketValidationFilter);

		ssoLogoutUrl = filterConfig.getInitParameter("ssoLogoutUrl");
		cas20ProxyReceivingTicketValidationFilter.setLocalSystemId(filterConfig.getInitParameter("localSystemId"));
		Boolean isTransSSOLoginIdToLocalLoginId = filterConfig.getInitParameter("isTransSSOLoginIdToLocalLoginId") == null ? Boolean.FALSE : Boolean
				.valueOf(filterConfig.getInitParameter("isTransSSOLoginIdToLocalLoginId"));
		cas20ProxyReceivingTicketValidationFilter.setIsTransSSOLoginIdToLocalLoginId(isTransSSOLoginIdToLocalLoginId);
	}

	public void destroy() {
	}

	public String getSsoLogoutUrl() {
		return ssoLogoutUrl;
	}
}
