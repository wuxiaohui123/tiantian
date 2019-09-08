package com.yinhai.webframework;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;

public class FirstFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException,
			ServletException {
		Object attribute = ((HttpServletRequest) request).getSession().getServletContext()
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (attribute == null) {
			PrintWriter writer = ((HttpServletResponse) response).getWriter();
			writer.print("xxx");
			writer.write("sss");
		} else {
			filter.doFilter(request, response);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
