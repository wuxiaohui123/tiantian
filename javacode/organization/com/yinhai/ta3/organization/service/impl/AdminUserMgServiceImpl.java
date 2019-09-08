package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IAdminUserMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class AdminUserMgServiceImpl extends OrgBaseService implements IAdminUserMgService {

	private OrganizationEntityService organizationEntityService;
	private IAdminMgService adminMgService;

	public void setAdminMgService(IAdminMgService adminMgService) {
		this.adminMgService = adminMgService;
	}

	public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
		this.organizationEntityService = organizationEntityService;
	}

	public PageBean queryNoAdminUsers(String gridId, ParamDTO dto) {
		return adminMgService.queryNoAdminUsers(gridId, dto);
	}

	public List queryYab139sByPositionId(Long positionid) {
		IOrg org = organizationEntityService.getDepartByPositionId(positionid);
		if (ValidateUtil.isEmpty(org)) {
			throw new AppException("该人员所在组织为空");
		}
		List<IOrg> list = organizationEntityService.getDepartsAndSelfByDepartId(org.getOrgid());

		StringBuffer hql = new StringBuffer();
		if (!ValidateUtil.isEmpty(list)) {
			hql.append("select distinct ym.id.yab139 from Yab139Mg ym where ym.id.yab003 in (");
			for (int i = 0; i < list.size(); i++) {
				hql.append(((IOrg) list.get(i)).getYab003());
				if (i != list.size() - 1) {
					hql.append(",");
				}
			}
			hql.append(")");
			return hibernateDao.createQuery(hql.toString(), new Object[0]).list();
		}
		return null;
	}

	public void addAdminUser(ParamDTO dto) {
		Long positionid = dto.getAsLong("positionid");

		adminMgService.addAdminMgUser(getLongSeq(), positionid, dto);
		String yab139s = dto.getAsString("yab139s");
		Long orgid = dto.getAsLong("orgid");

		if (!ValidateUtil.isEmpty(orgid)) {
			List<Org> list = new ArrayList();
			List<Key> orgids = new ArrayList();
			if (IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
				list = hibernateDao
						.createQuery(
								"select distinct oo from "
										+ super.getEntityClassName(Org.class.getName())
										+ " o,"
										+ super.getEntityClassName(Org.class.getName())
										+ " oo where o.orgid=? and oo.orgidpath like o.orgidpath||'/%' and (oo.destory is null or oo.destory=?)",
								new Object[] { orgid, "1" }).list();
				Key key = new Key();
				key.put("checked", Boolean.valueOf(true));
				key.put("id", orgid);
				orgids.add(key);
			} else {
				list = hibernateDao
						.createQuery(
								"select distinct oo from "
										+ super.getEntityClassName(Org.class.getName())
										+ " o,"
										+ super.getEntityClassName(Org.class.getName())
										+ " oo where o.orgid=? and oo.orgidpath like o.orgidpath||'/%' and (oo.destory is null or oo.destory=?) and oo.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)",
								new Object[] { orgid, "1", dto.getUserInfo().getNowPosition().getPositionid() }).list();
				Object uniqueResult = hibernateDao.createQuery(
						"from OrgMg om where om.id.positionid=? and om.id.orgid=?",
						new Object[] { dto.getUserInfo().getNowPosition().getPositionid(), orgid }).uniqueResult();
				if (!ValidateUtil.isEmpty(uniqueResult)) {
					Key key = new Key();
					key.put("checked", Boolean.valueOf(true));
					key.put("id", orgid);
					orgids.add(key);
				}
			}
			Key key;
			if (!ValidateUtil.isEmpty(list)) {
				key = null;
				for (Org org : list) {
					key = new Key();
					key.put("checked", Boolean.valueOf(true));
					key.put("id", org.getOrgid());
					orgids.add(key);
				}
			}

			if (!ValidateUtil.isEmpty(orgids)) {
				adminMgService.saveOrgMgScope(positionid, orgids, dto);
			}
		}

		List<Map<String, String>> list2 = adminMgService.queryAdminYab139Scope(dto.getUserInfo().getUserid(), dto
				.getUserInfo().getNowPosition().getPositionid());
		if (!ValidateUtil.isEmpty(list2)) {
			List yab139scope = new ArrayList();
			for (int i = 0; i < list2.size(); i++) {
				yab139scope.add(((Map) list2.get(i)).get("codeValue"));
			}
			if (!ValidateUtil.isEmpty(yab139s)) {
				String[] yab139Arr = yab139s.split(",");
				List<Key> yab139List = new ArrayList();
				for (int i = 0; i < yab139Arr.length; i++) {
					if (yab139scope.contains(yab139Arr[i])) {
						Key key = new Key();
						key.put("codeValue", yab139Arr[i]);
						yab139List.add(key);
					}
				}
				adminMgService.saveAdminYab003Scope(yab139List, dto);
			}
		}
	}

	public List<UserInfoVO> getAdminMgUsersByPositionid(Long positionid) {
		return adminMgService.getAdminMgUsersByPositionid(positionid);
	}

	public List<UserInfoVO> getAdminMgUsersNoTransformPositionByPositionid(Long positionid, Long transformPosition) {
		return adminMgService.getAdminMgUsersNoTransformPositionByPositionid(positionid, transformPosition);
	}

	public void transformAuthority(Long positionid, List<Key> selected, IUser userInfo) {
		adminMgService.transformAuthority(positionid, selected, userInfo);
	}

	public void removeAdminMgUser(Long positionid, IUser u) {
		adminMgService.removeAdminMgUser(positionid, u);
	}

	public List<PermissionTreeVO> getRePermissionTreeByPositionId(Long positionid) {
		return adminMgService.getRePermissionTreeByPositionId(positionid);
	}

	public Map<String, List<PermissionTreeVO>> getRePermissionAndAuthrityTreeByPositionid(Long positionid) {
		return adminMgService.getRePermissionAndAuthrityTreeByPositionid(positionid);
	}

	public void grantAdminUsePermissions(List<Key> pList, List<Key> list, ParamDTO dto) {
		if (!ValidateUtil.isEmpty(list)) {
			Key key = new Key();
			key.append("id", Long.valueOf(1L));
			key.append("checked", "true");
			list.add(key);
		}
		adminMgService.grantAdminUsePermissions(pList, list, dto);
	}

	public void recycleAdminUsePermissions(List<Key> pList, List<Key> list, ParamDTO dto) {
		adminMgService.recycleAdminUsePermissions(pList, list, dto);
	}

	public List<PermissionTreeVO> getAdminRePermissionTreeByPositionid(Long positionid) {
		return adminMgService.getAdminRePermissionTreeByPositionid(positionid);
	}

	public List<PermissionTreeVO> getAdminUsePermissionTreeByPositionid(Long positionid) {
		return adminMgService.getAdminUsePermissionTreeByPositionid(positionid);
	}

	public List<Org> getCurPositionOrgMgScope(Long positionid) {
		return adminMgService.getCurPositionOrgMgScope(positionid);
	}

	public List<Org> getTargetPositionOrgMgScope(Long positionid) {
		return adminMgService.getTargetPositionOrgMgScope(positionid);
	}

	public List<Map<String, String>> queryAdminYab003Scope(Long userid, Long curPositionid) {
		return adminMgService.queryAdminYab139Scope(userid, curPositionid);
	}

	public void saveAdminYab003Scope(List<Key> list, ParamDTO dto) {
		adminMgService.saveAdminYab003Scope(list, dto);
	}

	public void grantAuthorityPermissions(List<Key> positionList, List<Key> permissionList, ParamDTO dto) {
		adminMgService.grantAuthorityPermissions(positionList, permissionList, dto);
	}

	public void recycleAuthorityPermissions(List<Key> positionList, List<Key> permissionList, ParamDTO dto) {
		adminMgService.recycleAuthorityPermissions(positionList, permissionList, dto);
	}

	public void saveAdminUsePermission(List<Key> menuids, Long positionid, ParamDTO dto) {
		if (!ValidateUtil.isEmpty(menuids)) {
			Key key = new Key();
			key.append("id", Long.valueOf(1L));
			key.append("checked", "true");
			menuids.add(key);
		}
		adminMgService.saveAdminUsePermission(menuids, positionid, dto);
	}

	public void saveRoleScopeAclGranting(ParamDTO dto) {
		adminMgService.saveRoleScopeAclGranting(dto);
	}

	public void saveOrgMgScope(Long positionid, List<Key> orgids, ParamDTO dto) {
		adminMgService.saveOrgMgScope(positionid, orgids, dto);
	}

	public List<Menu> queryChildrenMenus(Long pmenuid, Long positionid, Long curPositionid) {
		if (ValidateUtil.isEmpty(pmenuid)) {
			throw new AppException("菜单id为空");
		}
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被修改权限的岗位id为空");
		}
		if (ValidateUtil.isEmpty(curPositionid)) {
			throw new AppException("当前岗位为空");
		}
		Menu pmenu = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), pmenuid);
		if (ValidateUtil.isEmpty(pmenu)) {
			throw new AppException("查无此菜单");
		}
		List<Menu> list = new ArrayList();
		if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
			Query query = hibernateDao
					.createQuery(
							"select distinct m from "
									+ getEntityClassName(Menu.class)
									+ " m,PositionAuthrity pa where m.menuid=pa.id.tamenu.menuid and pa.id.taposition.positionid=:positionid and pa.usepermission=:usepermission and m.menuidpath like :menuidpath and m.effective=:effective and (m.menutype=:menutype or m.menutype=null) order by m.sortno",
							new Object[0]);

			query.setLong("positionid", positionid.longValue());
			query.setString("usepermission", "1");
			query.setString("effective", "0");
			query.setString("menutype", "1");
			query.setString("menuidpath", pmenu.getMenuidpath() + "/%");
			list = query.list();
			list.add(pmenu);
		} else {
			Query query = hibernateDao
					.createQuery(
							"select distinct m from "
									+ getEntityClassName(Menu.class)
									+ " m,PositionAuthrity pa where m.menuid=pa.id.tamenu.menuid and pa.id.taposition.positionid=:positionid and pa.usepermission=:usepermission and m.menuidpath like :menuidpath and m.effective=:effective and (m.menutype=:menutype or m.menutype=null) and m.menuid in(select m1.menuid from "
									+ getEntityClassName(Menu.class)
									+ " m1,PositionAuthrity pa1 where m1.menuid=pa1.id.tamenu.menuid and pa1.repermission=:repermission and (m1.menutype=:menutype or m1.menutype=null) and pa1.id.taposition.positionid=:curPositionid) order by m.sortno",
							new Object[0]);
			query.setLong("positionid", positionid.longValue());
			query.setLong("curPositionid", curPositionid.longValue());
			query.setString("usepermission", "1");
			query.setString("effective", "0");
			query.setString("menutype", "1");
			query.setString("repermission", "1");
			query.setString("menuidpath", pmenu.getMenuidpath() + "/%");
			list = query.list();
			Object o = hibernateDao
					.createQuery(
							"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=? and pa.repermission=?",
							new Object[] { curPositionid, pmenu.getMenuid(), "1" }).uniqueResult();
			if (!ValidateUtil.isEmpty(o)) {
				list.add(pmenu);
			}
		}
		return list;
	}

	public List<Org> queryChildrenOrgs(Long porgid, Long positionid, Long curPositionid) {
		if (ValidateUtil.isEmpty(porgid)) {
			throw new AppException("组织id为空");
		}
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被修改权限的岗位id为空");
		}
		if (ValidateUtil.isEmpty(curPositionid)) {
			throw new AppException("当前岗位为空");
		}
		Org porg = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), porgid);
		if (ValidateUtil.isEmpty(porg)) {
			throw new AppException("查无此组织");
		}
		List<Org> orgList = new ArrayList();
		if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
			Query query = hibernateDao
					.createQuery(
							"select distinct o from "
									+ getEntityClassName(Org.class)
									+ " o where o.orgidpath like :orgidpath and (o.destory is null or o.destory=:destory) and o.effective=:effective and o.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=:positionid)",
							new Object[0]);

			query.setString("destory", "1");
			query.setString("effective", "0");
			query.setLong("positionid", positionid.longValue());
			query.setString("orgidpath", porg.getOrgidpath() + "/%");
			orgList = query.list();
			orgList.add(porg);
		} else {
			Query query = hibernateDao
					.createQuery(
							"select distinct o from "
									+ getEntityClassName(Org.class)
									+ " o where o.orgidpath like :orgidpath and (o.destory is null or o.destory=:destory) and o.effective=:effective and o.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=:positionid)",
							new Object[0]);
			query.setString("destory", "1");
			query.setString("effective", "0");
			query.setLong("positionid", positionid.longValue());
			query.setString("orgidpath", porg.getOrgidpath() + "/%");
			List<Org> list = query.list();
			List<Org> list2 = hibernateDao.createQuery("select distinct o from "+ getEntityClassName(Org.class)
				              + " o where (o.destory is null or o.destory=?) and o.effective=? and o.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=?)",
							  new Object[] { "1", "0", curPositionid }).list();
			for (Iterator i$ = list.iterator(); i$.hasNext();) {
				for (Org o2 : list2)
					if (o2.getOrgid().equals(o2.getOrgid())) {
						orgList.add(o2);
						break;
					}
			}
			Org o;
			Object object = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
					new Object[] { curPositionid, porgid }).uniqueResult();
			if (!ValidateUtil.isEmpty(object)) {
				orgList.add(porg);
			}
		}
		return orgList;
	}

}
