package com.yinhai.webframework;

import java.net.InetAddress;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.interceptor.ExceptionHolder;
import com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.exception.IllegalInputAppException;
import com.yinhai.sysframework.exception.PrcException;
import com.yinhai.sysframework.log.ServerExceptionService;
import com.yinhai.sysframework.log.Taserverexceptionlog;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

public class MyExceptionMappingInterceptor extends ExceptionMappingInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6244709537515869580L;
	protected static final Logger LOG = LoggerFactory.getLogger(MyExceptionMappingInterceptor.class);
	private ServerExceptionService serverExceptionService;

	public String intercept(ActionInvocation invocation) throws Exception {
		boolean isAppExp = true;
		String result;
		Object action = invocation.getAction();
		if ((action instanceof BaseAction)) {
			HttpServletRequest request = ServletActionContext.getRequest();
			request.setAttribute("_TA_STACK", ((BaseAction) action).getResultBean());
		}
		try {
			result = invocation.invoke();
			String logFlag = SysConfig.getSysConfig("recordOperationLog");
			if ((logFlag != null) && (logFlag.equals("2"))) {
			}
		} catch (Exception e) {
			if (isLogEnabled()) {
				handleLogging(e);
			}
			if (!(e instanceof AppException)) {
				if (!(e instanceof IllegalInputAppException)) {
					if (!(e instanceof PrcException)) {
						if ((e instanceof NoSuchMethodException)) {
							if (invocation.getAction().toString().toLowerCase().indexOf("action") > -1) {
								HttpServletRequest request = ServletActionContext.getRequest();
								e = new NoSuchMethodException("请求的地址"+ request.getRequestURI()
										+ "不存在，请检查该Action是否被启动或Action是否有请求的相应方法"
										+ "\n注意：Action如果是注解的方式，那么Action类必须在action包名下面,比如com.yinhai.ta3.sysapp.action.XXAction,否则action不会被实例化");
							}
						} else {
							isAppExp = false;
							try {
								serverExceptionService =  ServiceLocator.getService("serverExceptionService", ServerExceptionService.class);
								logException(e);
								insertServerLog(serverExceptionService, e);
							} catch (Exception e4) {
								e4.printStackTrace();
							}
						}
					}
				}
			}
			List<ExceptionMappingConfig> exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings();

			String mappedResult = findResultFromExceptions(exceptionMappings, e);
			String isajax = ServletActionContext.getRequest().getHeader("x-requested-with");
			if ((isAppExp) && (!"XMLHttpRequest".equals(isajax))) {
				//Map<String, ResultConfig> results = invocation.getProxy().getConfig().getResults();
				String method = invocation.getProxy().getMethod();
				if ("execute".equals(method)) {
					method = "success";
					mappedResult = "success";
				}
			}
			if (mappedResult != null) {
				result = mappedResult;
				publishException(invocation, new ExceptionHolder(e));
			} else {
				throw e;
			}
		}
		return result;
	}

	private String findResultFromExceptions(List<ExceptionMappingConfig> exceptionMappings, Exception e) {
		// TODO Auto-generated method stub
		return null;
	}

	private void logException(Exception e) {
	}

	private void insertServerLog(ServerExceptionService service, Exception e) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(e.getClass().getName() + ": " + e.getMessage() + "\r\n");
		StackTraceElement[] trace = e.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			sb.append("\tat " + JSonFactory.getJson(trace[i].toString()));
			sb.append("\r\n");
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		String clientip = WebUtil.getClientIp(request);
		String useragent = WebUtil.getUserAgent(request);
		String url = WebUtil.getRequestUrl(request);
		IMenu menu = WebUtil.getCurrentMenu(request);
		String menuid = "";
		String menuname = "";
		if (menu != null) {
			menuid = String.valueOf(menu.getMenuid());
			menuname = menu.getMenuname();
		}

		Taserverexceptionlog log = new Taserverexceptionlog();

		InetAddress host = InetAddress.getLocalHost();
		log.setIpaddress(host.getHostAddress());
		log.setType(e.getClass().getName());
		byte[] content = sb.toString().getBytes("UTF-8");
		log.setContent(content);
		String syspath = SysConfig.getSysConfig("curSyspathId");
		log.setSyspath(syspath);

		log.setClientip(clientip);
		log.setUrl(url);
		log.setMenuid(menuid);
		log.setMenuname(menuname);
		log.setUseragent(useragent);

		serverExceptionService.addServerException(log);
	}
}
