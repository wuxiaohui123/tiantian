package com.yinhai.ta3.system.codetable;

import java.util.Iterator;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import com.yinhai.sysframework.codetable.AppCodeDao;
import com.yinhai.sysframework.codetable.CodeCacheService;
import com.yinhai.sysframework.codetable.domain.Aa10;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;
import com.yinhai.sysframework.util.json.JSonFactory;

public class CodeCacheServiceImpl implements CodeCacheService {

	private AppCodeDao appCodeDao;

	public void setAppCodeDao(AppCodeDao appCodeDao) {
		this.appCodeDao = appCodeDao;
	}

	@Cacheable(value = "appCodeCache")
	public AppCode getAppCode(String aaa100, String aaa102, String yab003) {
		if ((yab003 == null) || ("".equals(yab003))) {
			yab003 = "9999";
		}
		AppCode appCode = appCodeDao.getAppCode(aaa100, aaa102, yab003);
		if ((appCode != null) && (appCode.getValidFlag() == "1")) {
			return null;
		}
		return appCode;
	}

	@Cacheable(value = "appCodeCache")
	public AppCode getAppCode(String aaa100, String aaa102) {
		AppCode appCode = appCodeDao.getAppCode(aaa100, aaa102, "9999");
		if ((appCode != null) && (appCode.getValidFlag() == "1")) {
			return null;
		}
		return appCode;
	}

	@Cacheable(value = "codeListCache")
	public CodeCacheService.CachAppCodeList getCodeListCache(String aaa100, String yab003) {
		if ((yab003 == null) || ("".equals(yab003))) {
			yab003 = "9999";
		}
		List<AppCode> list = appCodeDao.getCodeListByCodeType(aaa100, yab003, false);
		return new CodeCacheService.CachAppCodeList(genarateJson(list), list);
	}

	@Cacheable(value = "codeListCache")
	public CodeCacheService.CachAppCodeList getCodeListCache(String aaa100) {
		List<AppCode> list = appCodeDao.getCodeListByCodeType(aaa100, "9999", false);
		return new CodeCacheService.CachAppCodeList(genarateJson(list), list);
	}

	public String genarateJson(List<AppCode> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Iterator<AppCode> i;
		if ((list != null) && (list.size() > 0)) {
			for (i = list.iterator(); i.hasNext();) {
				AppCode app = (AppCode) i.next();
				sb.append("{\"id\":\"" + JSonFactory.getJson(app.getCodeValue()).replace("\\\"", "\\\"") + "\",");
				sb.append("\"name\":\"" + JSonFactory.getJson(app.getCodeDESC()).replace("\\\"", "\\\"") + "\",");
				sb.append("\"py\":\"" + JSonFactory.getJson(app.getPy()).replace("\\\"", "\\\""));
				if (i.hasNext())
					sb.append("\"},");
				else
					sb.append("\"}");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public void changeLocalCacheVersion(int version, String codeType) {
		appCodeDao.changeLocalCacheVersion(version, codeType);
	}

	public int getLocalCacheVersion() {
		return appCodeDao.getLocalCacheVersion();
	}

	public String getChangeLocalCacheJson(int version, int max) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(appCodeDao.getCodeListJson(version, max));
		sb.append("\"VERSION\":");
		sb.append("\"");
		sb.append(max);
		sb.append("\"");
		sb.append("}");

		return sb.toString();
	}

	public List<AppCode> getAppCodeListByCondition(AppCode appcode) {
		return appCodeDao.getCodeListByAppCode(appcode);
	}

	public void insertAa10(Aa10 aa10) {
		appCodeDao.insertAa10(aa10);
	}

	public void updateAa10(Aa10 aa10) {
		appCodeDao.updateAa10(aa10);
	}

	public void deleteAa10(AppCodeId id) {
		appCodeDao.deleteAa10(id);
	}
}
