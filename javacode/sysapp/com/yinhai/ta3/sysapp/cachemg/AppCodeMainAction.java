package com.yinhai.ta3.sysapp.cachemg;

import java.net.URLDecoder;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.yinhai.sysframework.cache.ehcache.CacheUtil;
import com.yinhai.sysframework.codetable.CodeCacheService;
import com.yinhai.sysframework.codetable.domain.Aa10;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.webframework.BaseAction;

import net.sf.ehcache.Ehcache;

public class AppCodeMainAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7715260116048180585L;
	private CodeCacheService codeCacheService = getService("codeCacheService", CodeCacheService.class);
	private CacheManager ehCacheManager = getService("ehCacheManager", CacheManager.class);
	private int version;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String query() throws Exception {
		ParamDTO dto = getDto();

		AppCode appcode = (AppCode) dto.toDomainObject(AppCode.class);
		List<AppCode> list = codeCacheService.getAppCodeListByCondition(appcode);
		setList("appCodeList", list);
		return JSON;
	}

	public String edit() throws Exception {
		String t_codeType = URLDecoder.decode(getDto().getAsString("codeType"), "UTF-8");
		String t_codeTypeDESC = URLDecoder.decode(getDto().getAsString("codeTypeDESC"), "UTF-8");
		String t_codeValue = URLDecoder.decode(getDto().getAsString("codeValue"), "UTF-8");
		String t_codeDESC = URLDecoder.decode(getDto().getAsString("codeDESC"), "UTF-8");
		String t_orgId = getDto().getAsString("orgId");
		setData("t_codeType", t_codeType);
		setData("t_codeTypeDESC", t_codeTypeDESC);

		setData("t_codeValue", t_codeValue);
		setData("t_codeDESC", t_codeDESC);
		setData("t_orgId", t_orgId);
		return "cacheEdit";
	}

	public String remove() throws Exception {
		String codeType = getDto().getAsString("codeType").toUpperCase();
		String codeValue = getDto().getAsString("codeValue");
		String orgId = getDto().getAsString("orgId");
		AppCodeId id = new AppCodeId();
		id.setCodeType(codeType);
		id.setCodeValue(codeValue);
		codeCacheService.deleteAa10(id);
		int version = codeCacheService.getLocalCacheVersion() + 1;
		codeCacheService.changeLocalCacheVersion(version, codeType);
		CodeTableLocator.reflashCodeCacheForCURD(codeType, codeValue, orgId);
		clearCacheSynCode(codeType, codeValue);
		return JSON;
	}

	public String saveEdit() throws Exception {
		String codeType = getDto().getAsString("t_codeType").toUpperCase();
		String codeValue = getDto().getAsString("t_codeValue");
		String codeTypeDESC = getDto().getAsString("t_codeTypeDESC");
		String codeDESC = getDto().getAsString("t_codeDESC");
		String orgId = getDto().getAsString("t_orgId");
		Aa10 aa10 = new Aa10();
		AppCodeId id = new AppCodeId();
		id.setCodeType(codeType);
		id.setCodeValue(codeValue);
		aa10.setId(id);
		aa10.setCodeDESC(codeDESC);
		aa10.setCodeTypeDESC(codeTypeDESC);
		aa10.setYab003(orgId);
		aa10.setValidFlag("0");
		String flag = getDto().getAsString("insertApp");
		if ((ValidateUtil.isNotEmpty(flag)) && ("1".equals(flag))) {
			aa10.setYab003("9999");
			codeCacheService.insertAa10(aa10);
		} else {
			codeCacheService.updateAa10(aa10);
		}
		int version = codeCacheService.getLocalCacheVersion() + 1;
		codeCacheService.changeLocalCacheVersion(version, codeType);
		CodeTableLocator.reflashCodeCacheForCURD(codeType, codeValue, orgId);
		clearCacheSynCode(codeType, codeValue);
		return JSON;
	}

	public String clearcache() throws Exception {
		String codeType = getDto().getAsString("codeType").toUpperCase();
		String codeValue = getDto().getAsString("codeValue");
		String orgId = getDto().getAsString("orgId");
		CodeTableLocator.reflashCodeCacheForCURD(codeType, codeValue, orgId);
		clearCacheSynCode(codeType, codeValue);
		return JSON;
	}

	public String pushLocalCache() throws Exception {
		Cache codeListLocalCache = ehCacheManager.getCache("codeListLocalCache");
		int max = codeCacheService.getLocalCacheVersion();

		if (version == 0) {
			writeJsonToClient(codeListLocalCache.get(((Ehcache) codeListLocalCache).getKeys().get(0)));
		} else if (version < max) {
			writeJsonToClient(codeCacheService.getChangeLocalCacheJson(version, max));
		} else {
			writeJsonToClient("{}");
		}

		return null;
	}

	private void clearCacheSynCode(String codeType, String codeValue) {
		Cache codeListCache = ehCacheManager.getCache("codeListCache");
		Cache appCodeCache = ehCacheManager.getCache("appCodeCache");
		CacheUtil.cacheSynCodeRemove(appCodeCache, codeType + "." + codeValue);
		CacheUtil.cacheSynCodeRemove(codeListCache, codeType);
	}
}
