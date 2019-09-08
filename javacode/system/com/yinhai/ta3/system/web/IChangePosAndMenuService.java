package com.yinhai.ta3.system.web;

import javax.jws.WebService;

import com.yinhai.sysframework.service.WsService;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@WebService
public interface IChangePosAndMenuService extends WsService {

	public static final String SERVICEKEY = "changePosAndMenuService";

	public abstract Position getPosition(Long paramLong);

	public abstract Menu getMenu(Long paramLong);
}
