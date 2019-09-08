package com.yinhai.ta3.sysapp.menumg.service;

import java.util.List;

import javax.jws.WebService;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.service.WsService;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@WebService
public interface ICommonMenuService extends WsService {

	String SERVICEKEY = "commonMenuService";

	List<Menu> getCommonMenusByUserId(Long paramLong);

	void saveCommonMenus(Long paramLong, List<Key> paramList);

	void saveCommonMenu(Long paramLong1, Long paramLong2);

	void deleteCommonMenus(Long paramLong, List<Key> paramList);

	void deleteCommonMenu(Long paramLong1, Long paramLong2);
}
