package com.yinhai.ta3.system.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.security.ta3.IUserAccountInfo;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.WebUtil;

public class ChangePositionOrMenuInterceptor extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7443807075535726625L;
	static final String POSITION_ID = "__positionId";
	static final String MENU_ID = "___businessId";
	protected static final Logger log = LoggerFactory.getLogger(ChangePositionOrMenuInterceptor.class);

	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String positionId = request.getParameter(POSITION_ID);
		String menuId = request.getParameter(MENU_ID);

		if ((positionId != null) && (!"".equals(positionId))) {
			IChangePosAndMenuService orgService = ServiceLocator.getService("changePosAndMenuService", IChangePosAndMenuService.class);
			IPosition position = orgService.getPosition(Long.valueOf(positionId));
			if (position != null) {
				IUser user = WebUtil.getUserInfo(request);
				if (user != null) {
					WebUtil.getUserInfo(request).setNowPosition(position);
					if (log.isDebugEnabled()) {
						log.debug("切换当前使用岗位" + position.getPositionname() + "[" + position.getPositionid() + "]");
					}
				}
			}
		}
		if ((menuId != null) && (!"".equals(menuId)) && (!"01".equals(menuId))) {
			IChangePosAndMenuService menuService = (IChangePosAndMenuService) ServiceLocator.getService("changePosAndMenuService");
			IMenu menu = menuService.getMenu(Long.valueOf(menuId));
			if (menu != null) {
				ServletActionContext.getRequest().getSession().setAttribute("ta3.currentmenu", menu);
				String __common = request.getParameter("__common");
				if (!"__common".equals(__common)) {
					IUserAccountInfo user = WebUtil.getUserAccountInfo(request);

					ParamDTO dto = new ParamDTO();
					dto.put("menuId", menuId);
					dto.put("user", user.getUser());
					dto.put("url", menu.getUrl());
					dto.put("ispermission", "1");
					TaEventPublisher.publishEvent(new EventSource(ServletActionContext.getRequest(), dto), "access_log");
				}
				if (log.isDebugEnabled()) {
					log.debug("切换菜单:" + menu.getMenuname() + "[" + menu.getMenuid() + "]", new String[0]);
				}
			}
		}
		return invocation.invoke();
	}

}
