package com.yinhai.webframework;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.util.DESCoderUtil;

public class MySubmitValueIntercptor extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5806633612795530683L;

	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, String[]> m = request.getParameterMap();
		Iterator<String> requestParams = m.keySet().iterator();
		while (requestParams.hasNext()) {
			String s = (String) requestParams.next();
			if ((s.endsWith("_md5list']")) && (s.startsWith("dto['"))) {
				String value = request.getParameter(s);

				if ((value != null) && (!"".equals(value))) {
					String id = s.substring(0, s.indexOf("_md5list']")) + "']";
					String v = request.getParameter(id);
					byte[] datas = DESCoderUtil.decrypt(DESCoderUtil.decryptBASE64(value), "reYj6fIsWGE=");

					if ((v != null) && (!"".equals(v)) && (!isContains(new String(datas), v))) {
						throw new AppException("篡改数据");
					}
				}
			}
		}

		return invocation.invoke();
	}

	public boolean isContains(String parent, String target) {
		String[] datas = parent.split("[,]");
		for (String data : datas) {
			if (target.equals(data)) {
				return true;
			}
		}
		return false;
	}
}
