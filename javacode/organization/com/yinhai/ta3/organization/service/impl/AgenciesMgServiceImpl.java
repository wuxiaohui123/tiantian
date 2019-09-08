package com.yinhai.ta3.organization.service.impl;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.ta3.organization.service.IAgenciesMgService;
import com.yinhai.ta3.organization.service.IYab003MgService;

public class AgenciesMgServiceImpl extends OrgBaseService implements IAgenciesMgService {

	private IYab003MgService yab003MgService;

	public void setYab003MgService(IYab003MgService yab003MgService) {
		this.yab003MgService = yab003MgService;
	}

	public List queryYab003Tree(Long positionid) {
		return yab003MgService.queryYab003Tree(positionid);
	}

	public List queryCurYab139(Long positionid, String yab003) {
		return yab003MgService.queryCurYab139(positionid, yab003);
	}

	public List<Map<String, String>> queryUnDistrbutedData(Long userid, Long positionid, String yab003) {
		return yab003MgService.queryYab139(userid, positionid, yab003);
	}

	public void addTreeNode(List list) {
	}

	public void removeYab003(List list1) {
		yab003MgService.removeYab003(list1);
	}

	public void editTreeNode(List list) {
	}

	public void saveYab139(String yab003, List<Key> list) {
		yab003MgService.saveYab139(yab003, list);
	}

	public void removeYab139(String yab003, List<Key> list) {
		yab003MgService.removeYab139(yab003, list);
	}

	public void saveYab003(String pyab003, List<Key> list, String firstTree) {
		yab003MgService.saveYab003(pyab003, list, firstTree);
	}

}
