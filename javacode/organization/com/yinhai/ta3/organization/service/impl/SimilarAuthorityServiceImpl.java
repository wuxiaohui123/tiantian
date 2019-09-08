package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.ISimilarAuthorityService;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class SimilarAuthorityServiceImpl extends OrgBaseService implements ISimilarAuthorityService {

	public List<PermissionTreeVO> getRePermissionTreeByPositionid(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位为空!");
		}

		List<PermissionTreeVO> permissionnodes = new ArrayList();
		PermissionTreeVO permissionnode = null;
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			List<Menu> ms = hibernateDao.createQuery(
					"from " + getEntityClassName(Menu.class) + " m where m.effective=? and (m.menutype=? or m.menutype=?)  order by m.menuid",
					new Object[] { "0", "2", "0" }).list();

			if ((ms != null) && (ms.size() > 0)) {
				for (int i = 0; i < ms.size(); i++) {
					permissionnode = new PermissionTreeVO();
					Menu m = (Menu) ms.get(i);
					permissionnode.setId(m.getMenuid());
					permissionnode.setPId(m.getPmenuid());
					permissionnode.setName(m.getMenuname());
					permissionnode.setTitle(m.getMenuname());
					permissionnode.setPolicy(m.getSecuritypolicy());
					permissionnodes.add(permissionnode);
				}
			}
		} else {
			List<PositionAuthrity> positionAuthrities = hibernateDao
					.createQuery(
							"from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and (pa.id.tamenu.menutype=? or pa.id.tamenu.menutype=?)  order by pa.id.tamenu.menuid",
							new Object[] { positionid, timeService.getSysTimestamp(), "2", "0" }).list();

			if (positionAuthrities != null) {
				for (int i = 0; i < positionAuthrities.size(); i++) {
					permissionnode = new PermissionTreeVO();
					PositionAuthrity positionAuthrity = (PositionAuthrity) positionAuthrities.get(i);
					Menu menu = positionAuthrity.getId().getTamenu();
					if (("0".equals(menu.getEffective())) && ("1".equals(positionAuthrity.getRepermission()))) {
						permissionnode.setId(menu.getMenuid());
						permissionnode.setPId(menu.getPmenuid());
						permissionnode.setName(menu.getMenuname());
						permissionnode.setTitle(menu.getMenuname());
						permissionnode.setPolicy(menu.getSecuritypolicy());
						permissionnodes.add(permissionnode);
					}
				}
			}
		}

		return permissionnodes;
	}

	public List<Position> querypositionsByAuthorities(List<Key> ids, Long curPositionId) {
		if (ValidateUtil.isEmpty(curPositionId)) {
			throw new AppException("当前岗位为空，不能分配权限");
		}
		List<Position> pList = new ArrayList();
		List list = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select p.positionid,count(pa.id.taposition.positionid)").append(" from PositionAuthrity pa,")
				.append(getEntityClassName(Position.class)).append(" p").append(" where pa.id.taposition.positionid=p.positionid")
				.append(" and pa.usepermission=?").append(" and p.effective=?").append(" and (p.positiontype=? or p.positiontype=?)")
				.append(" and (p.iscopy is null or p.iscopy=?)").append(" and p.positionid <> ?");

		if (!ValidateUtil.isEmpty(ids)) {
			hql.append(" and pa.id.tamenu.menuid in(");
			for (int i = 0; i < ids.size(); i++) {
				Key key = (Key) ids.get(i);
				Long menuid = key.getAsLong("id");
				hql.append(menuid);
				if (i < ids.size() - 1) {
					hql.append(",");
				}
			}
			hql.append(")");
		}

		if (!IPosition.ADMIN_POSITIONID.equals(curPositionId)) {
			hql.append(" and p.taorg.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=?)");
			hql.append(" group by p having count(pa.id.taposition.positionid)=?");
			list = hibernateDao.createQuery(hql.toString(),
					new Object[] { "1", "0", "1", "2", "0", curPositionId, curPositionId, Long.valueOf(ids.size()) }).list();
		} else {
			hql.append(" group by p having count(pa.id.taposition.positionid)=?");
			list = hibernateDao.createQuery(hql.toString(), new Object[] { "1", "0", "1", "2", "0", curPositionId, Long.valueOf(ids.size()) }).list();
		}
		for (Object object : list) {
			if ((object instanceof Object[])) {
				Object[] obs = (Object[]) object;

				Position p = (Position) hibernateDao.getSession().get(SysConfig.getSysConfig(Position.class.getName(), Position.class.getName()),
						Long.valueOf(String.valueOf(obs[0])));
				pList.add(p);
			}
		}
		return pList;
	}
}
