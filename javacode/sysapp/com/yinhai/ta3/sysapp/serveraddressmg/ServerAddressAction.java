package com.yinhai.ta3.sysapp.serveraddressmg;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.cache.ehcache.domain.ServeraddressDomain;
import com.yinhai.sysframework.cache.ehcache.service.ServerAddressService;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.webframework.BaseAction;

public class ServerAddressAction extends BaseAction {

	private ServerAddressService serverAddressService = (ServerAddressService) super.getService("serverAddressService");

	public String execute() throws Exception {
		List list = serverAddressService.query(getDto());
		setList("MainGrid", list);
		return "success";
	}

	public String query() throws Exception {
		List list = serverAddressService.query(reBuildDto("q_", getDto()));
		setList("MainGrid", list);
		return JSON;
	}

	public String toadd() throws Exception {
		return "mainAdd";
	}

	public String save() throws Exception {
		ParamDTO dto = getDto();
		ServeraddressDomain address = (ServeraddressDomain) dto.toDomainObject(ServeraddressDomain.class);
		List<ServeraddressDomain> list = serverAddressService.query(new ParamDTO());
		if (!ValidateUtil.isEmpty(list)) {
			for (ServeraddressDomain add : list) {
				if (add.getAddress().equals(address.getAddress())) {
					setMsg("已经存在该地址");
					return JSON;
				}
			}
		}
		serverAddressService.addServerAddress(address);
		setMsg("保存成功");
		List<ServeraddressDomain> result = serverAddressService.query(new ParamDTO());
		setList("MainGrid", result);
		return JSON;
	}

	public String update() throws Exception {
		ParamDTO dto = getDto();
		serverAddressService.updateServerAddress((ServeraddressDomain) dto.toDomainObject(ServeraddressDomain.class));
		setMsg("编辑成功");
		List list = serverAddressService.query(new ParamDTO());
		setList("MainGrid", list);
		return JSON;
	}

	public String delete() throws Exception {
		List<Key> selected = getSelected("MainGrid");
		if ((selected != null) && (selected.size() > 0)) {
			Key key = null;
			for (int i = 0; i < selected.size(); i++) {
				key = (Key) selected.get(i);
				ParamDTO dto = new ParamDTO();
				dto.putAll(key);
				serverAddressService.removeServerAddress((ServeraddressDomain) dto.toDomainObject(ServeraddressDomain.class));
			}
			writeSuccess("删除成功");
		} else {
			writeFailure("删除失败");
		}
		return null;
	}

	public String tomodify() throws Exception {
		ParamDTO dto = getDto();
		ServeraddressDomain domain = (ServeraddressDomain) serverAddressService.getServerAddress(dto.getAsString("address"));
		setData(domain.toMap(), false);
		return "mainEdit";
	}
}
