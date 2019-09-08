package com.yinhai.sysframework.codetable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.yinhai.sysframework.cache.ehcache.CacheUtil;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

import net.sf.ehcache.Ehcache;

public class CodeTableInitService {

	private static Log logger = LogFactory.getLog(CodeTableInitService.class);
	private CodeCacheService codeCacheService;
	private AppCodeDao appCodeDao;
	private CacheManager ehCacheManager;

	public void setCodeCacheService(CodeCacheService codeCacheService) {
		this.codeCacheService = codeCacheService;
	}

	public void setCodeLevelCacheService(CodeLevelCacheService codeLevelCacheService) {
	}

	public void setAppCodeDao(AppCodeDao appCodeDao) {
		this.appCodeDao = appCodeDao;
	}

	public void setEhCacheManager(CacheManager ehCacheManager) {
		this.ehCacheManager = ehCacheManager;
	}

	public void init() {
		try {
			logger.info("正在启动代码表的插件...");
			initAppCode();
			if (!"false".equals(SysConfig.getSysConfig("codelevelTableViewName")))
				initAppLevel();
			logger.info("代码表插件启动完成...");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("代码表插件加载失败...", e);
			}
		}
	}

	private void initAppCode() {
		List<String> yab003List = appCodeDao.getDistinctYab003();
		Cache codeListCache = ehCacheManager.getCache("codeListCache");
		Cache appCodeCache = ehCacheManager.getCache("appCodeCache");

		String yab003 = "";

		for (int j = 0; j < yab003List.size(); j++) {
			yab003 = (String) yab003List.get(j);
			List<AppCode> list = appCodeDao.getCodeList(yab003);
			List<AppCode> v = null;
			String tmpCodeType = null;
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					AppCode ct = (AppCode) list.get(i);
					if ("9999".equals(yab003)) {
						appCodeCache.put(ct.getCodeType().toUpperCase() + "." + ct.getCodeValue(), ct);
					} else {
						appCodeCache.put(
								ct.getCodeType().toUpperCase() + "." + ct.getCodeValue() + "." + ct.getYab003(), ct);
					}
					if ((tmpCodeType == null) || (!tmpCodeType.equalsIgnoreCase(ct.getCodeType()))) {
						if (tmpCodeType != null) {
							if ("9999".equals(yab003)) {
								codeListCache.put(tmpCodeType.toUpperCase(),
										new CodeCacheService.CachAppCodeList(codeCacheService.genarateJson(v), v));
							} else {
								codeListCache.put(tmpCodeType + "." + yab003,
										new CodeCacheService.CachAppCodeList(codeCacheService.genarateJson(v), v));
							}
						}
						v = new ArrayList<AppCode>();
						tmpCodeType = ct.getCodeType();
					}
					v.add(ct);
				}
			}
			if ((tmpCodeType != null) && (v != null)) {
				if ("9999".equals(yab003)) {
					codeListCache.put(tmpCodeType.toUpperCase(),
							new CodeCacheService.CachAppCodeList(codeCacheService.genarateJson(v), v));
				} else {
					codeListCache.put(tmpCodeType.toUpperCase() + "." + yab003,
							new CodeCacheService.CachAppCodeList(codeCacheService.genarateJson(v), v));
				}
			}
		}
		initLocalCache(codeListCache, yab003);
	}

	private void initLocalCache(Cache codeListCache, String yab003) {
		Cache codeListLocalCache = ehCacheManager.getCache("codeListLocalCache");

		StringBuffer sb = new StringBuffer();
		String type = "";
		sb.append("{");
		List keys = ((Ehcache) codeListCache.getNativeCache()).getKeys();
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);

			CodeCacheService.CachAppCodeList list = (CodeCacheService.CachAppCodeList) codeListCache.get(keys.get(i))
					.get();

			List<AppCode> apps = list.getList();
			if (ValidateUtil.isNotEmpty(apps)) {
				for (AppCode app : apps) {
					type = app.getCodeType();
				}
				sb.append("\"" + type.toUpperCase(Locale.ENGLISH) + "\"");
				sb.append(":");
				sb.append("\"" + JSonFactory.getJson(list.getJson()) + "\"");
				sb.append(",");
			}
		}

		sb.append("\"VERSION\":\"");
		sb.append("1");
		sb.append("\"");
		sb.append("}");
		codeListLocalCache.put("LocalCache." + yab003, sb.toString());
	}

	private void initAppLevel() {
	}

	public void destroy() {
		logger.info("代码表缓存清除...");
		ehCacheManager.getCache("codeListCache").clear();
		ehCacheManager.getCache("appCodeCache").clear();
	}

	public void reflashCodeCacheForCURD(String codeType, String codeValue, String orgId) {
		if ((orgId == null) || ("".equals(orgId))) {
			orgId = "9999";
		}
		CacheUtil.cacheElementRemove("appCodeCache", codeType + "." + codeValue + "." + orgId);
		CacheUtil.cacheElementRemove("codeListCache", codeType + "." + orgId);
	}
}
