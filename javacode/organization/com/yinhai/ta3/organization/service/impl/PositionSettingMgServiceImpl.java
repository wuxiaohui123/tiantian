package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IPositionSettingMgService;
import com.yinhai.ta3.system.org.domain.Org;

public class PositionSettingMgServiceImpl extends OrgBaseService implements IPositionSettingMgService {

	@Override
	public List<Org> getTargetPositionOrgMgScope(Long positionid) {
		List list = new ArrayList();
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空，不能查询该岗位所能管理的部门");
		}
		if (positionid.equals(IPosition.ADMIN_POSITIONID)) {
			list = hibernateDao.createQuery("from " + getEntityClassName(Org.class), new Object[0]).list();
		} else {
			list = hibernateDao.createQuery(
					"select o from " + getEntityClassName(Org.class) + " o,OrgMg om where o.orgid = om.id.orgid and om.id.positionid=?",
					new Object[] { positionid }).list();
		}

		return list;
	}

}
