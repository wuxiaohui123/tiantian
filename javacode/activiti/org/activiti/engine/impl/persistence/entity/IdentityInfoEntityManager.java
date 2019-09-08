package org.activiti.engine.impl.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.AbstractManager;

public class IdentityInfoEntityManager extends AbstractManager {
	public void deleteUserInfoByUserIdAndKey(String userId, String key) {
		IdentityInfoEntity identityInfoEntity = findUserInfoByUserIdAndKey(userId, key);
		if (identityInfoEntity != null) {
			deleteIdentityInfo(identityInfoEntity);
		}
	}

	public void deleteUserInfoByUserId(String userId) {
		List<IdentityInfoEntity> identityInfoEntityList = findUserInfoByUserId(userId);
		if (identityInfoEntityList != null) {
			for (IdentityInfoEntity identityInfoEntity : identityInfoEntityList) {
				deleteIdentityInfo(identityInfoEntity);
			}
		}
	}

	public void deleteIdentityInfo(IdentityInfoEntity identityInfo) {
		getDbSqlSession().delete(identityInfo);
	}

	public void setUserInfo(String userId, String type, String key, String value) {
		IdentityInfoEntity identityInfoEntity = findUserInfoByUserIdAndKey(userId, key);
		if (identityInfoEntity != null) {
			identityInfoEntity.setValue(value);
			getDbSqlSession().update(identityInfoEntity);
		} else {
			identityInfoEntity = new IdentityInfoEntity();
			identityInfoEntity.setUserId(userId);
			identityInfoEntity.setType(type);
			identityInfoEntity.setKey(key);
			identityInfoEntity.setValue(value);
			getDbSqlSession().insert(identityInfoEntity);
		}
	}

	public void setUserInfo(String userId, String type, Map<String, String> userInfos) {
		List<String> userKeys = findUserInfoKeysByUserIdAndType(userId, type);
		for (Map.Entry<String, String> entry : userInfos.entrySet()) {
			if (userKeys.contains(entry.getKey())) {
				IdentityInfoEntity identityInfoEntity = findUserInfoByUserIdAndKey(userId, (String) entry.getKey());
				identityInfoEntity.setValue((String) entry.getValue());
				getDbSqlSession().update(identityInfoEntity);
			} else {
				IdentityInfoEntity identityInfoEntity = new IdentityInfoEntity();
				identityInfoEntity.setUserId(userId);
				identityInfoEntity.setType(type);
				identityInfoEntity.setKey((String) entry.getKey());
				identityInfoEntity.setValue((String) entry.getValue());
				getDbSqlSession().insert(identityInfoEntity);
			}
		}
	}

	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("userId", userId);
		parameters.put("key", key);
		return (IdentityInfoEntity) getDbSqlSession().selectOne("selectIdentityInfoByUserIdAndKey", parameters);
	}

	public List<IdentityInfoEntity> findUserInfoByUserId(String userId) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("userId", userId);
		return (List<IdentityInfoEntity>) getDbSqlSession().selectOne("selectIdentityInfoByUserId", parameters);
	}

	public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("userId", userId);
		parameters.put("type", type);
		return getDbSqlSession().getSqlSession().selectList("selectIdentityInfoKeysByUserIdAndType", parameters);
	}
}
