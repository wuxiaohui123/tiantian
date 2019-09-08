package com.yinhai.ta3.sysapp.codetablemg.action;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;

import com.yinhai.sysframework.codetable.CodeCacheService;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.webframework.BaseAction;

@Namespace("/sysapp")
@AllowedMethods({"getCacheByCollection"})
@Action(value = "appCodeCacheAction", results = {})
public class AppCodeCacheAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5022689122597165278L;
	private CacheManager ehCacheManager = getService("ehCacheManager", CacheManager.class);
	private CodeCacheService codeCacheService = getService("codeCacheService", CodeCacheService.class);
	private int version;

	private String collection;

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String pushLocalCache() throws Exception {
		Ehcache codeListLocalCache = ehCacheManager.getEhcache("codeListLocalCache");
		int max = codeCacheService.getLocalCacheVersion();

		if (version == 0) {
			writeJsonToClient(codeListLocalCache.get(codeListLocalCache.getKeys().get(0)).getValue());
		} else if (version < max) {
			writeJsonToClient(codeCacheService.getChangeLocalCacheJson(version, max));
		} else {
			writeJsonToClient("{}");
		}
		return null;
	}

	public String getCacheByCollection() throws Exception {
		String orgId = null;
		IUser user = WebUtil.getUserInfo(request);
		if (user != null) {
			orgId = user.getOrgId();
		}
		CodeTableLocator.getInstance();
		writeJsonToClient(CodeTableLocator.getCodeListJson(collection, orgId));
		return null;
	}
}
