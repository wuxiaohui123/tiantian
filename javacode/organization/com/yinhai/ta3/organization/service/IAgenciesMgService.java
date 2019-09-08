package com.yinhai.ta3.organization.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.service.Service;

public interface IAgenciesMgService extends Service {

	public static final String SERVICEKEY = "agenciesMgService";

	public abstract List queryYab003Tree(Long paramLong);

	public abstract List queryCurYab139(Long paramLong, String paramString);

	public abstract List<Map<String, String>> queryUnDistrbutedData(Long paramLong1, Long paramLong2, String paramString);

	public abstract void addTreeNode(List paramList);

	public abstract void editTreeNode(List paramList);

	public abstract void removeYab003(List paramList);

	public abstract void saveYab139(String paramString, List<Key> paramList);

	public abstract void removeYab139(String paramString, List<Key> paramList);

	public abstract void saveYab003(String paramString1, List<Key> paramList, String paramString2);
}
