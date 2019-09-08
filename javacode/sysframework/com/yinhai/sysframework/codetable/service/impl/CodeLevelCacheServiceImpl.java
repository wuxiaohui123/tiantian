package com.yinhai.sysframework.codetable.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import com.yinhai.sysframework.codetable.CodeLevelCacheService;
import com.yinhai.sysframework.codetable.domain.AppLevelCode;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.StringUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CodeLevelCacheServiceImpl extends BaseService implements CodeLevelCacheService {
	private String codeTableViewName;

	@Cacheable(value = "appLevelCodeCache")
	public AppLevelCode getAppCode(String paramString1, String paramString2, String paramString3) {
		HashMap localHashMap = new HashMap();
		localHashMap.put("viewName", StringUtil.isEmpty(codeTableViewName) ? "v_aa11tree" : codeTableViewName);
		localHashMap.put("validFlag", "0");
		localHashMap.put("type", paramString1.toUpperCase());
		localHashMap.put("id", paramString2);
		if ((paramString3 == null) || ("".equals(paramString3))) {
			paramString3 = "9999";
		}
		localHashMap.put("orgId", paramString3);
		List localList = dao.queryForList("applevelcode.getAllAppCodesForCache", localHashMap);
		if ((localList != null) && (localList.size() > 0)) {
			return (AppLevelCode) localList.get(0);
		}
		return null;
	}

	@Cacheable(value = "codeLevelListCache")
	public CachAppLevelCodeList getCodeListCache(String paramString1, String paramString2) {
		HashMap localHashMap = new HashMap();
		localHashMap.put("viewName", StringUtil.isEmpty(codeTableViewName) ? "v_aa11tree" : codeTableViewName);
		localHashMap.put("validFlag", "0");
		localHashMap.put("type", paramString1.toUpperCase());
		if ((paramString2 == null) || ("".equals(paramString2))) {
			paramString2 = "9999";
		}
		localHashMap.put("orgId", paramString2);
		List localList = dao.queryForList("applevelcode.getAllAppCodesForCache", localHashMap);
		return new CachAppLevelCodeList(genarateJson(localList), localList);
	}

	public String genarateJson(List<AppLevelCode> paramList) {
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append("[");
		if ((paramList != null) && (paramList.size() > 0)) {
			Iterator localIterator = paramList.iterator();
			while (localIterator.hasNext()) {
				AppLevelCode localAppLevelCode = (AppLevelCode) localIterator.next();
				localStringBuilder.append("{\"id\":\"" + localAppLevelCode.getId() + "\",");
				localStringBuilder.append("\"name\":\"" + localAppLevelCode.getName() + "\",");
				localStringBuilder.append("\"leaf\":\"" + localAppLevelCode.getLeaf() + "\",");
				localStringBuilder.append("\"icon\":\"" + localAppLevelCode.getIcon() + "\",");
				localStringBuilder.append("\"isparent\":\"" + localAppLevelCode.getIsparent() + "\",");
				localStringBuilder.append("\"open\":\"" + localAppLevelCode.getOpen() + "\",");
				localStringBuilder.append("\"pid\":\"" + localAppLevelCode.getPid() + "\",");
				localStringBuilder.append("\"type\":\"" + localAppLevelCode.getType() + "\",");
				localStringBuilder.append("\"level\":\"" + localAppLevelCode.getLevelvalue() + "\",");
				localStringBuilder.append("\"py\":\"" + localAppLevelCode.getPy());
				if (localIterator.hasNext()) {
					localStringBuilder.append("\"},");
				} else {
					localStringBuilder.append("\"}");
				}
			}
		}
		localStringBuilder.append("]");
		return localStringBuilder.toString();
	}

	public void setCodeTableViewName(String paramString) {
		codeTableViewName = paramString;
	}

	public String getCodeTableViewName() {
		return codeTableViewName;
	}
}
