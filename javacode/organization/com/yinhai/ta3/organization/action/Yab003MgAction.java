package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IYab003MgService;
import com.yinhai.ta3.system.org.domain.Yab003LevelMg;

public class Yab003MgAction extends OrgBaseAction {

	private IYab003MgService yab003MgService = (IYab003MgService) super.getService("yab003MgService");

	public String execute() throws Exception {
		List yab003List = yab003MgService.getYab003List(getDto().getUserInfo().getNowPosition().getPositionid());
		setList("yab003Grid", yab003List);
		List list = yab003MgService.queryYab003Tree(getDto().getUserInfo().getNowPosition().getPositionid());
		StringBuffer sb = new StringBuffer();
		if (!ValidateUtil.isEmpty(list)) {
			sb.append("[");
			for (int i = 0; i < list.size(); i++) {
				Yab003LevelMg ylm = (Yab003LevelMg) list.get(i);
				sb.append("{\"id\":\"").append(ylm.getId().getYab003()).append("\",\"name\":\"")
						.append(super.getCodeDesc("yab003", ylm.getId().getYab003(), "9999")).append("\",\"pId\":\"")
						.append(ylm.getId().getPyab003()).append("\"}");

				if (i != list.size() - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
		} else {
			sb.append("[]");
		}
		request.setAttribute("yab003Tree", sb.toString());
		List<AppCode> codeList = super.getCodeList("yab003", "9999");
		if (ValidateUtil.isEmpty(list)) {
			setList("yab003Grid2", codeList);
		} else {
			List<AppCode> list2 = new ArrayList();
			for (AppCode appCode : codeList) {
				for (int i = 0; i < list.size(); i++) {
					Yab003LevelMg ylm = (Yab003LevelMg) list.get(i);
					if (appCode.getCodeValue().equals(ylm.getId().getYab003())) {
						break;
					}
					if (i == list.size() - 1) {
						list2.add(appCode);
					}
				}
			}
			setList("yab003Grid2", list2);
		}
		return super.execute();
	}

	public String queryYab139() throws Exception {
		String yab003 = getDto().getAsString("yab003");
		List<java.util.Map<String, String>> list = yab003MgService.queryYab139(getDto().getUserInfo().getUserid(), getDto().getUserInfo()
				.getNowPosition().getPositionid(), yab003);
		setList("yab139Grid", list);
		setData("yab003", yab003);
		return "toYab139";
	}

	public String queryCurYab139() throws Exception {
		List list = yab003MgService.queryCurYab139(getDto().getUserInfo().getNowPosition().getPositionid(), getDto().getAsString("yab003"));
		setList("yab139Grid", list);
		return "tojson";
	}

	public String saveYab139() throws Exception {
		List<Key> list = getSelected("yab139Grid");
		String yab003 = getDto().getAsString("yab003");
		yab003MgService.saveYab139(yab003, list);
		return "tojson";
	}

	public String removeYab139() throws Exception {
		List<Key> list = getSelected("yab139Grid");
		String yab003 = getDto().getAsString("yab003");
		yab003MgService.removeYab139(yab003, list);
		return "tojson";
	}

	public String queryYab003() throws Exception {
		List<AppCode> codeList = super.getCodeList("yab003", "9999");
		List list = yab003MgService.queryYab003Tree(getDto().getUserInfo().getNowPosition().getPositionid());
		if (ValidateUtil.isEmpty(list)) {
			setList("yab003Grid2", codeList);
		} else {
			List<AppCode> list2 = new ArrayList();
			for (AppCode appCode : codeList) {
				for (int i = 0; i < list.size(); i++) {
					Yab003LevelMg ylm = (Yab003LevelMg) list.get(i);
					if (appCode.getCodeValue().equals(ylm.getId().getYab003())) {
						list2.add(appCode);
						break;
					}
				}
			}
			setList("yab003Grid2", list2);
		}
		return "tojson";
	}

	public String saveYab003() throws Exception {
		List<Key> list = getSelected("yab003Grid2");
		System.out.println(" list " + list);
		String pyab003 = getDto().getAsString("pyab003");
		System.out.println(" pyab003 " + pyab003);
		String firstTree = getDto().getAsString("firstTree");
		System.out.println(" firstTree " + firstTree);
		yab003MgService.saveYab003(pyab003, list, firstTree);
		return "tojson";
	}

	public String removeYab003() throws Exception {
		String yab003s = getDto().getAsString("yab003s");
		List list1 = (List) JSonFactory.json2bean(yab003s, ArrayList.class);
		yab003MgService.removeYab003(list1);
		List list = yab003MgService.queryYab003Tree(getDto().getUserInfo().getNowPosition().getPositionid());
		List<AppCode> codeList = super.getCodeList("yab003", "9999");
		if (ValidateUtil.isEmpty(list)) {
			setList("yab003Grid2", codeList);
		} else {
			List<AppCode> list2 = new ArrayList();
			for (AppCode appCode : codeList) {
				for (int i = 0; i < list.size(); i++) {
					Yab003LevelMg ylm = (Yab003LevelMg) list.get(i);
					if (appCode.getCodeValue().equals(ylm.getId().getYab003())) {
						break;
					}
					if (i == list.size() - 1) {
						list2.add(appCode);
					}
				}
			}
			setList("yab003Grid2", list2);
		}
		return "tojson";
	}
}
