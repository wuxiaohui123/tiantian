package com.yinhai.sysframework.codetable;

import java.io.Serializable;
import java.util.List;

import com.yinhai.sysframework.codetable.domain.AppLevelCode;
import com.yinhai.sysframework.service.Service;

public interface CodeLevelCacheService extends Service{

	public abstract AppLevelCode getAppCode(String paramString1, String paramString2, String paramString3);

	public abstract CachAppLevelCodeList getCodeListCache(String paramString1, String paramString2);

	public abstract String genarateJson(List<AppLevelCode> paramList);

	public abstract String getCodeTableViewName();

	public static class CachAppLevelCodeList implements Serializable {
		private static final long serialVersionUID = 3365820970896302883L;
		private String json;
		private List<AppLevelCode> list;

		public CachAppLevelCodeList(String json, List<AppLevelCode> list) {
			this.json = json;
			this.list = list;
		}

		public String getJson() {
			return json;
		}

		public void setJson(String json) {
			this.json = json;
		}

		public List<AppLevelCode> getList() {
			return list;
		}

		public void setList(List<AppLevelCode> list) {
			this.list = list;
		}
	}
}
