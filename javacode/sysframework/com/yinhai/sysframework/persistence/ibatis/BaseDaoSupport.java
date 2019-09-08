package com.yinhai.sysframework.persistence.ibatis;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.dto.ParamDTO;

public class BaseDaoSupport extends AbstractDaoSupport {

	private SaveOldFieldService saveOldFieldData;

	public int saveOldFieldData(ParamDTO pdto, BaseDomain domainClass, String menuId, String menuName) {
		if (saveOldFieldData != null) {
			return saveOldFieldData.saveOldFieldData(pdto, domainClass, menuId, menuName);
		}
		return 0;
	}

	public SaveOldFieldService getSaveOldFieldData() {
		return saveOldFieldData;
	}

	public void setSaveOldFieldData(SaveOldFieldService saveOldFieldData) {
		this.saveOldFieldData = saveOldFieldData;
	}
}
