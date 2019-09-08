package com.yinhai.sysframework.codetable.service;

import java.util.ArrayList;
import java.util.List;

import com.yinhai.sysframework.cache.ehcache.CacheUtil;
import com.yinhai.sysframework.codetable.CodeCacheService;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.service.ServiceLocator;

public class CodeTableLocator {

	private static CodeCacheService codeCacheService;

	public static CodeTableLocator getInstance() {
		return (CodeTableLocator) ServiceLocator.getService("codeTableLocator");
	}
	
	public static String getCodeDesc(String codeType, String codeValue) {
		return getCodeDesc(codeType, codeValue, "9999");
	}

	public static String getCodeDesc(String codeType, String codeValue, String orgId) {
		AppCode appCode = getAppCode(codeType, codeValue, orgId);
		String ret = codeValue;
		if (appCode != null) {
			ret = appCode.getCodeDESC();
			ret = (ret == null) || ("".equals(ret)) ? codeValue : ret;
		}
		return ret;
	}

	public static AppCode getAppCode(String codeType, String codeValue) {
		return getAppCode(codeType, codeValue, null);
	}

	public static AppCode getAppCode(String codeType, String szValue, String yab003) {
		if (!"9999".equals(yab003)) {
			AppCode appCode = codeCacheService.getAppCode(codeType.toUpperCase(), szValue, yab003);
			if (appCode == null)
				return codeCacheService.getAppCode(codeType.toUpperCase(), szValue);
			return appCode;
		}
		return codeCacheService.getAppCode(codeType.toUpperCase(), szValue);
	}

	public static List<AppCode> getCodeList(String codeType) {
		return getCodeList(codeType, null);
	}

	public static List<AppCode> getCodeList(String codeType, String orgId) {
		List<AppCode> listCommonCode = new ArrayList<AppCode>();
		CodeCacheService.CachAppCodeList codeCommonListCache = codeCacheService.getCodeListCache(codeType.toUpperCase());
		if (codeCommonListCache != null) {
			listCommonCode = codeCommonListCache.getList();
		}
		if ((orgId != null) && (!"9999".equals(orgId))) {
			CodeCacheService.CachAppCodeList codeYab003ListCache = codeCacheService.getCodeListCache(
					codeType.toUpperCase(), orgId);
			if (codeYab003ListCache != null) {
				listCommonCode.addAll(codeYab003ListCache.getList());
				return listCommonCode;
			}
		}
		return listCommonCode;
	}

	public static String getCodeListJson(String codeType, String orgId) {
		String jsonCommonCode = "";
		String jsonYab003Code = "";
		CodeCacheService.CachAppCodeList codeCommonListCache = codeCacheService.getCodeListCache(codeType.toUpperCase());
		if (codeCommonListCache != null)
			jsonCommonCode = codeCommonListCache.getJson();
		if ((orgId != null) && (!"9999".equals(orgId))) {
			CodeCacheService.CachAppCodeList codeYab003ListCache = codeCacheService.getCodeListCache(codeType.toUpperCase(), orgId);
			if (codeYab003ListCache != null) {
				jsonYab003Code = codeYab003ListCache.getJson();
			}
		}
		if ((jsonCommonCode.length() > 2) && (jsonYab003Code.length() > 2)) {
			jsonCommonCode = jsonCommonCode.substring(0, jsonCommonCode.length() - 1) + ",";
			jsonCommonCode = jsonCommonCode + jsonYab003Code.substring(1, jsonYab003Code.length());
			return jsonCommonCode;
		}
		if ((jsonCommonCode.length() <= 2) && (jsonYab003Code.length() > 2))
			return jsonYab003Code;
		if ((jsonCommonCode.length() > 2) && (jsonYab003Code.length() <= 2)) {
			return jsonCommonCode;
		}
		return "[]";
	}

	public static String getCodeLevelListJson(String codeType, String orgId) {
		return "[]";
	}

	public void setCodeCacheService(CodeCacheService codeCacheService) {
		CodeTableLocator.codeCacheService = codeCacheService;
	}

	public static void reflashCodeCacheForCURD(String codeType, String codeValue, String orgId) {
		if ((orgId == null) || ("".equals(orgId))) {
			orgId = "9999";
		}
		CacheUtil.cacheElementRemove("appCodeCache", codeType + "." + codeValue + "." + orgId);
		CacheUtil.cacheElementRemove("appCodeCache", codeType + "." + codeValue);
		CacheUtil.cacheElementRemove("codeListCache", codeType + "." + orgId);
		CacheUtil.cacheElementRemove("codeListCache", codeType);
	}
}
