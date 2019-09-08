package com.yinhai.ta3.organization.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.service.Service;

public interface IYab003MgService extends Service {

	public static final String SERVICEKEY = "yab003MgService";

	public abstract List getYab003List(Long paramLong);

	public abstract List<Map<String, String>> queryYab139(Long paramLong1, Long paramLong2, String paramString);

	public abstract List queryCurYab139(Long paramLong, String paramString);

	public abstract void saveYab139(String paramString, List<Key> paramList);

	public abstract void removeYab139(String paramString, List<Key> paramList);

	public abstract List queryYab003(Long paramLong, String paramString);

	public abstract List queryChildYab003(Long paramLong, String paramString);

	public abstract List queryYab003Tree(Long paramLong);

	public abstract void saveYab003(String paramString1, List<Key> paramList, String paramString2);

	public abstract void removeYab003(List paramList);
}
