package com.yinhai.ta3.organization.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.cache.ehcache.CacheUtil;
import com.yinhai.sysframework.codetable.CodeCacheService;
import com.yinhai.sysframework.codetable.domain.Aa10;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IAgenciesMgService;
import com.yinhai.ta3.system.org.domain.Yab003LevelMg;
import com.yinhai.webframework.BaseAction;

public class AgenciesMgAction extends BaseAction {

	private IAgenciesMgService agenciesMgService = (IAgenciesMgService) super.getService("agenciesMgService");
	private CodeCacheService codeCacheService = (CodeCacheService) getService("codeCacheService");
	private CacheManager ehCacheManager = (CacheManager) super.getService("ehCacheManager");

	public String execute() throws Exception {
		List list = agenciesMgService.queryYab003Tree(getDto().getUserInfo().getNowPosition().getPositionid());
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
		return super.execute();
	}

	public String queryCurYab139() throws Exception {
		String yab003 = getDto().getAsString("yab003");
		List list = agenciesMgService.queryCurYab139(getDto().getUserInfo().getNowPosition().getPositionid(), yab003);
		setList("yab139Grid", list);
		return "tojson";
	}

	public String queryUnDistrbutedData() throws Exception {
		String yab003 = getDto().getAsString("yab003");
		List<Map<String, String>> list = agenciesMgService.queryUnDistrbutedData(getDto().getUserInfo().getUserid(), getDto().getUserInfo()
				.getNowPosition().getPositionid(), yab003);
		setList("unDistrbutedData", list);
		return "tojson";
	}

	public String saveYab003() throws Exception {
		String codeType = getDto().getAsString("codeType").toUpperCase();
		String codeValue = getDto().getAsString("codeValue");
		String codeTypeDESC = getDto().getAsString("codeTypeDESC");
		String codeDESC = getDto().getAsString("codeDESC");
		List<Key> list = new ArrayList<Key>();
		Key key = new Key();
		key.put("orgId", "9999");
		key.put("codeType", codeType);
		key.put("codeValue", codeValue);
		key.put("codeTypeDESC", codeTypeDESC);
		key.put("codeDESC", codeDESC);
		list.add(key);
		String pyab003 = getDto().getAsString("yab003");
		agenciesMgService.saveYab003(pyab003, list, "no");
		return "tojson";
	}

	public String addYab003() throws Exception {
		String treeId = getDto().getAsString("treeId");
		String treeNode = getDto().getAsString("treeNode");
		String treeNodeName = getDto().getAsString("getNode");
		setData("treeId", treeId);
		setData("treeNodeId", treeNode);
		setData("treeNodeName", treeNodeName);
		String isInsertAa10Agencies = SysConfig.getSysConfig("isInsertAa10Agencies", "true");
		if ("true".equals(isInsertAa10Agencies)) {
			return "addYab003";
		}
		List<AppCode> newList = new ArrayList<AppCode>();
		List<AppCode> codeList = super.getCodeList("YAB003", "9999");
		List<Yab003LevelMg> list = agenciesMgService.queryYab003Tree(getDto().getUserInfo().getNowPosition().getPositionid());
		if (!ValidateUtil.isEmpty(list)) {
			for (AppCode appCode : codeList) {
				for (int i = 0; i < list.size(); i++) {
					if (appCode.getCodeValue().equals(((Yab003LevelMg) list.get(i)).getId().getYab003())) {
						break;
					}
					if (i == list.size() - 1) {
						newList.add(appCode);
					}
				}
			}
			setList("yab003ChildGrid", newList);
		} else {
			setList("yab003ChildGrid", codeList);
		}
		setData("yab003", getDto().getAsString("yab003"));
		return "addYab003Grid";
	}

	public String editYab003() throws Exception {
		String yab003 = getDto().getAsString("yab003");
		String yab003desc = getDto().getAsString("yab003desc");
		setData("t_codeValue", yab003);
		setData("t_codeDESC", yab003desc);
		return "editYab003";
	}

	public String addOrEditTreeNode() throws Exception {
		String isInsertAa10Agencies = SysConfig.getSysConfig("isInsertAa10Agencies", "true");
		if ("true".equals(isInsertAa10Agencies)) {
			ParamDTO dto = reBuildDto("t_", getDto());
			String codeType = dto.getAsString("codeType").toUpperCase();
			String codeValue = dto.getAsString("codeValue");
			String codeTypeDESC = dto.getAsString("codeTypeDESC");
			String codeDESC = dto.getAsString("codeDESC");
			Aa10 aa10 = new Aa10();
			AppCodeId id = new AppCodeId();
			id.setCodeType(codeType);
			id.setCodeValue(codeValue);
			aa10.setId(id);
			aa10.setCodeDESC(codeDESC);
			aa10.setCodeTypeDESC(codeTypeDESC);
			aa10.setYab003("9999");
			aa10.setValidFlag("0");
			String flag = dto.getAsString("insertApp");
			if ((ValidateUtil.isNotEmpty(flag)) && ("1".equals(flag))) {
				aa10.setYab003("9999");
				codeCacheService.insertAa10(aa10);
				List<Key> list = new ArrayList<Key>();
				Key key = new Key();
				key.put("orgId", "9999");
				key.put("codeType", codeType);
				key.put("codeValue", codeValue);
				key.put("codeTypeDESC", codeTypeDESC);
				key.put("codeDESC", codeDESC);
				list.add(key);
				String pyab003 = getDto().getAsString("yab003");
				agenciesMgService.saveYab003(pyab003, list, "no");
			} else {
				codeCacheService.updateAa10(aa10);
			}
			int version = codeCacheService.getLocalCacheVersion() + 1;
			codeCacheService.changeLocalCacheVersion(version, codeType);
			CodeTableLocator.reflashCodeCacheForCURD(codeType, codeValue, "9999");
			clearCacheSynCode(codeType, codeValue);
		} else {
			List<Key> list = getSelected("yab003ChildGrid");
			String pyab003 = getDto().getAsString("yab003");
			agenciesMgService.saveYab003(pyab003, list, "no");
		}
		return "tojson";
	}

	public String removeYab003() throws Exception {
		String isInsertAa10Agencies = SysConfig.getSysConfig("isInsertAa10Agencies", "true");
		String yab003s = getDto().getAsString("yab003p");
		List list1 = JSonFactory.json2bean(yab003s, ArrayList.class);
		agenciesMgService.removeYab003(list1);
		if ("true".equals(isInsertAa10Agencies)) {
			String codeType = "YAB003";
			String orgId = "9999";
			if (!ValidateUtil.isEmpty(list1)) {
				for (int i = 0; i < list1.size(); i++) {
					Map map = (Map) list1.get(i);
					String codeValue = (String) map.get("id");
					AppCodeId id = new AppCodeId();
					id.setCodeType(codeType);
					id.setCodeValue(codeValue);
					codeCacheService.deleteAa10(id);
					CodeTableLocator.reflashCodeCacheForCURD(codeType, codeValue, orgId);
					clearCacheSynCode(codeType, codeValue);
				}
				int version = codeCacheService.getLocalCacheVersion() + 1;
				codeCacheService.changeLocalCacheVersion(version, codeType);
			}
		}
		return "tojson";
	}

	public String batchAddData() throws Exception {
		List<Key> list = getSelected("unDistrbutedData");
		String yab003 = getDto().getAsString("yab003");
		agenciesMgService.saveYab139(yab003, list);
		return "tojson";
	}

	public String batchDelete() throws Exception {
		List<Key> list = getSelected("yab139Grid");
		String yab003 = getDto().getAsString("yab003");
		agenciesMgService.removeYab139(yab003, list);
		return "tojson";
	}

	private void clearCacheSynCode(String codeType, String codeValue) {
		Cache codeListCache = ehCacheManager.getCache("codeListCache");
		Cache appCodeCache = ehCacheManager.getCache("appCodeCache");
		CacheUtil.cacheSynCodeRemove(appCodeCache, codeType + "." + codeValue);
		CacheUtil.cacheSynCodeRemove(codeListCache, codeType);
	}
}
