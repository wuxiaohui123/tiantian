package com.yinhai.sysframework.codetable;

import java.io.Serializable;
import java.util.List;

import com.yinhai.sysframework.codetable.domain.Aa10;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;

public interface CodeCacheService {

	public static final String SERVICEKEY = "codeCacheService";

	public abstract AppCode getAppCode(String paramString1, String paramString2, String paramString3);

	public abstract AppCode getAppCode(String paramString1, String paramString2);

	public abstract CachAppCodeList getCodeListCache(String paramString1, String paramString2);

	public abstract CachAppCodeList getCodeListCache(String paramString);

	public abstract String genarateJson(List<AppCode> paramList);

	public abstract List<AppCode> getAppCodeListByCondition(AppCode paramAppCode);

	public abstract void insertAa10(Aa10 paramAa10);

	public abstract void updateAa10(Aa10 paramAa10);

	public abstract void deleteAa10(AppCodeId paramAppCodeId);

	public abstract void changeLocalCacheVersion(int paramInt, String paramString);

	public abstract int getLocalCacheVersion();

	public abstract String getChangeLocalCacheJson(int paramInt1, int paramInt2);

	public static class CachAppCodeList implements Serializable {
		private String json;
		private List<AppCode> list;

		public CachAppCodeList(String json, List<AppCode> list) {
			this.json = json;
			this.list = list;
		}

		public String getJson() {
			return json;
		}

		public void setJson(String json) {
			this.json = json;
		}

		public List<AppCode> getList() {
			return list;
		}

		public void setList(List<AppCode> list) {
			this.list = list;
		}
	}
}
