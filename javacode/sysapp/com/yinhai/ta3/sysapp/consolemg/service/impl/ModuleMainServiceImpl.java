package com.yinhai.ta3.sysapp.consolemg.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.consolemg.domain.ConsoleModule;
import com.yinhai.ta3.sysapp.consolemg.domain.ConsoleModulePrivilege;
import com.yinhai.ta3.sysapp.consolemg.service.ModuleMainService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;

public class ModuleMainServiceImpl extends BaseService implements ModuleMainService {

	private SimpleDao hibernateDao;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public List getModuleList(ParamDTO dto) {
		return hibernateDao.createQuery("from ConsoleModule cm where cm.modulesta = '1'", new Object[0]).list();
	}

	public int addModuleItem(ParamDTO dto) {
		ConsoleModule cm = new ConsoleModule();
		cm.setModulename(dto.getAsString("moduleName"));
		cm.setModuleurl(dto.getAsString("moduleURL"));
		String height = dto.getAsString("moduleHeight");
		if (ValidateUtil.isNotEmpty(height)) {
			cm.setModuleheight(height);
		}
		hibernateDao.save(cm);
		return 0;
	}

	public int updateModuleItem(ParamDTO dto) {
		ConsoleModule cm = new ConsoleModule();
		cm.setModulename(dto.getAsString("moduleName"));
		cm.setModuleurl(dto.getAsString("moduleURL"));
		cm.setModuledefault(dto.getAsString("moduleDefault"));
		cm.setModuleheight(dto.getAsString("moduleHeight"));
		cm.setModuleid(dto.getAsLong("moduleId"));
		try {
			if ("0".equals(dto.getAsString("sta"))) {
				hibernateDao.createQuery("update ConsoleModule cm set cm.modulesta=? where cm.moduleid=?",
						new Object[] { dto.getAsString("sta"), dto.getAsLong("moduleId") }).executeUpdate();
			} else {
				hibernateDao.update(cm);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Map getModuleItem(ParamDTO dto) {
		ConsoleModule object = (ConsoleModule) hibernateDao.createQuery("from ConsoleModule cm WHERE cm.modulesta = '1' and cm.moduleid=?",
				new Object[] { dto.getAsLong("moduleId") }).uniqueResult();
		Map m = new HashMap();
		m.put("moduleId", object.getModuleid());
		m.put("moduleName", object.getModulename());
		m.put("moduleURL", object.getModuleurl());
		m.put("moduleDefault", object.getModuledefault());
		m.put("moduleHeight", object.getModuleheight());
		return m;
	}

	public void saveGrant(ParamDTO dto, List<Map> lst) {
		hibernateDao.createQuery("delete ConsoleModulePrivilege cmp where cmp.positionid=?", new Object[] { dto.getAsLong("roleId") })
				.executeUpdate();
		if ((null != lst) && (lst.size() > 0)) {
			for (int i = 0; i < lst.size(); i++) {

				ConsoleModulePrivilege consoleModulePrivilege = new ConsoleModulePrivilege();
				consoleModulePrivilege.setModuleid(Long.valueOf(((Map) lst.get(i)).get("moduleid").toString()));
				consoleModulePrivilege.setPositionid(dto.getAsLong("roleId"));
				hibernateDao.save(consoleModulePrivilege);
			}
		}
	}

	public List getGrantList(ParamDTO dto) {
		List<Map> lst = new ArrayList();
		List<ConsoleModulePrivilege> list = hibernateDao.createQuery("from ConsoleModulePrivilege cmp where cmp.positionid = ?",
				new Object[] { dto.getAsLong("roleId") }).list();
		for (ConsoleModulePrivilege consoleModulePrivilege : list) {
			Map map = new HashMap();
			map.put("moduleid", consoleModulePrivilege.getModuleid());
			lst.add(map);
		}
		return lst;
	}

	public List<Position> getAllPositions(ParamDTO dto) {
		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		Long orgid = dto.getAsLong("orgid");
		if (ValidateUtil.isEmpty(orgid)) {
			throw new AppException("�??组织机构id为空，不能进行操作");
		}
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id位空，不能进行操作");
		}
		String positionClassName = SysConfig.getSysConfig(Position.class.getName(), Position.class.getName());
		String orgClassName = SysConfig.getSysConfig(Org.class.getName(), Org.class.getName());
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			return hibernateDao.createQuery(
					"from " + positionClassName + " p where p.effective=? and p.taorg.orgidpath like (select o.orgidpath from " + orgClassName
							+ " o where o.orgid=?)||'%' and (p.taorg.destory is null or p.taorg.destory=?)", new Object[] { "0", orgid, "1" }).list();
		}
		return hibernateDao
				.createQuery(
						"from "
								+ positionClassName
								+ " p where p.effective=? and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?) and p.taorg.orgidpath like (select o.orgidpath from "
								+ orgClassName + " o where o.orgid=?)||'%' and (p.taorg.destory is null or p.taorg.destory=?)",
						new Object[] { "0", positionid, orgid, "1" }).list();
	}
}
