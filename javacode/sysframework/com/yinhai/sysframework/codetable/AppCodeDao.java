package com.yinhai.sysframework.codetable;

import java.util.List;

import javax.jws.WebService;

import com.yinhai.sysframework.codetable.domain.Aa10;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;

@WebService
public interface AppCodeDao {
	
	  public abstract AppCode getAppCode(String paramString1, String paramString2, String paramString3);
	  
	  public abstract List<AppCode> getCodeListByCodeType(String paramString1, String paramString2, boolean paramBoolean);
	  
	  public abstract List<AppCode> getCodeList(String yab003);
	  
	  public abstract List<AppCode> getCodeListByAppCode(AppCode paramAppCode);
	  
	  public abstract void insertAa10(Aa10 paramAa10);
	  
	  public abstract void updateAa10(Aa10 paramAa10);
	  
	  public abstract void deleteAa10(AppCodeId paramAppCodeId);
	  
	  public abstract List<String> getDistinctYab003();
	  
	  public abstract int getLocalCacheVersion();
	  
	  public abstract void changeLocalCacheVersion(int paramInt, String paramString);
	  
	  public abstract String getCodeListJson(int paramInt1, int paramInt2);
}
