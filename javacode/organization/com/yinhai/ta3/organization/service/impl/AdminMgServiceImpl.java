package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.Finder;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IOrgOpLogService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.AdminYab003Scope;
import com.yinhai.ta3.system.org.domain.AdminYab003ScopeId;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.OrgMg;
import com.yinhai.ta3.system.org.domain.OrgMgId;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class AdminMgServiceImpl extends OrgBaseService implements IAdminMgService {

	private IGrantService grantService;
	private IOrgOpLogService orgOpLogService;
	private IPositionMgService positionMgService;

	public void setPositionMgService(IPositionMgService positionMgService) {
		this.positionMgService = positionMgService;
	}

	public void setOrgOpLogService(IOrgOpLogService orgOpLogService) {
		this.orgOpLogService = orgOpLogService;
	}

	public void setGrantService(IGrantService grantService) {
		this.grantService = grantService;
	}

	public List<UserInfoVO> getUsersByPositionid(Long positionid) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,p.orgnamepath,u.userid,u.name,u.sex,u.loginid)")
				.append(" from " + getEntityClassName(Org.class) + " o," + getEntityClassName(Position.class) + " p,UserPosition up,"
						+ getEntityClassName(User.class) + " u").append(" where p.effective=?").append(" and p.positiontype=?")
				.append(" and o.effective=?").append(" and (o.destory is null or o.destory=?)").append(" and o.orgid=p.taorg.orgid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?")
				.append(" and u.islock=?").append(" and (p.isadmin is null or p.isadmin=?)").append(" and p.positionid not in(?,?)");

		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			hql.append(" order by p.orgnamepath");
			return hibernateDao.createQuery(hql.toString(),
					new Object[] { "0", "2", "0", "1", "0", "0", "0", positionid, IPosition.ADMIN_POSITIONID }).list();
		}
		hql.append(" and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)").append(" order by p.orgnamepath");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { "0", "2", "0", "1", "0", "0", "0", positionid, IPosition.ADMIN_POSITIONID, positionid }).list();
	}

	public void addAdminMgUser(Long batchNo, Long positionid, ParamDTO dto) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("该人员的岗位id为空,不能添加为管理员");
		}
		Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "24", "03", "新增管理员，无权操作该岗位所在组织");
		p.setIsadmin("1");
		hibernateDao.update(p);

		orgOpLogService.logPositionOp(batchNo, dto.getUserInfo(), "24", p, "");
	}

	public List<UserInfoVO> getAdminMgUsersByPositionid(Long positionid) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,p.orgnamepath,u.userid,u.name,u.sex,u.loginid)")
				.append(" from " + getEntityClassName(Org.class) + " o," + getEntityClassName(Position.class) + " p,UserPosition up,"
						+ getEntityClassName(User.class) + " u").append(" where p.effective=?").append(" and p.positiontype=?")
				.append(" and o.effective=?").append(" and (o.destory is null or o.destory=?)").append(" and o.orgid=p.taorg.orgid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?")
				.append(" and u.islock=?").append(" and p.isadmin=?");

		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			hql.append(" and p.positionid not in(?)").append(" order by p.orgnamepath");

			return hibernateDao.createQuery(hql.toString(), new Object[] { "0", "2", "0", "1", "0", "0", "1", positionid }).list();
		}
		hql.append(" and p.positionid not in(?,?)").append(" and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
				.append(" order by p.orgnamepath");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { "0", "2", "0", "1", "0", "0", "1", positionid, IPosition.ADMIN_POSITIONID, positionid }).list();
	}

	public List<Org> getCurPositionOrgMgScope(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位id为空,无法进行管理部门的分配");
		}
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			return hibernateDao.createQuery(
					"from " + getEntityClassName(Org.class) + " o where o.effective=? and (o.destory=? or o.destory is null) order by o.sort",
					new Object[] { "0", "1" }).list();
		}
		return hibernateDao
				.createQuery(
						"from "
								+ getEntityClassName(Org.class)
								+ " o where o.effective=? and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?) and (o.destory=? or o.destory is null) order by o.sort",
						new Object[] { "0", positionid, "1" }).list();
	}

	public List<Org> getTargetPositionOrgMgScope(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("个人岗位id为空,无法进行管理部门的分配");
		}
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			return hibernateDao.createQuery(
					"from " + getEntityClassName(Org.class) + " o where o.effective=?  and (o.destory=? or o.destory is null) order by o.sort",
					new Object[] { "0", "1" }).list();
		}
		return hibernateDao
				.createQuery(
						"from "
								+ getEntityClassName(Org.class)
								+ " o where o.effective=? and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?) and (o.destory=? or o.destory is null) order by o.sort",
						new Object[] { "0", positionid, "1" }).list();
	}

	public void saveOrgMgScope(Long positionid, List<Key> orgids, ParamDTO dto) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("个人岗位id为空,无法进行管理部门的分配");
		}
		Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "11", "03", "部门管理范围，无权操作该岗位�?��组织");
		for (Key key : orgids) {

			checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), key.getAsLong("id"), "02", "01",
					"部门管理方为，无权操作该组织");
			OrgMgId id = new OrgMgId();
			id.setOrgid(key.getAsLong("id").longValue());
			id.setPositionid(positionid.longValue());
			OrgMg orgMg = new OrgMg();
			orgMg.setId(id);

			if ("true".equals(key.getAsString("checked"))) {
				hibernateDao.save(orgMg);
			} else {
				hibernateDao.delete(orgMg);
			}
		}
	}

	public void removeAdminMgUser(Long positionid, IUser opUser) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空,不能移除管理员");
		}
		IPosition position = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(opUser.getUserid(), opUser.getNowPosition().getPositionid(), positionid, "23", "03", "移除管理员");

		hibernateDao.createQuery("update " + getEntityClassName(Position.class) + " p set p.isadmin=? where p.positionid=?",
				new Object[] { "0", positionid }).executeUpdate();

		hibernateDao.createQuery("delete from OrgMg mg where mg.id.positionid=?", new Object[] { positionid }).executeUpdate();

		List<PositionAuthrity> pas = hibernateDao.createQuery("from PositionAuthrity pa where pa.id.taposition.positionid=?",
				new Object[] { positionid }).list();

		hibernateDao.createQuery("delete from AdminYab003Scope ays where ays.id.positionid=?", new Object[] { positionid }).executeUpdate();

		Long batchNo = Long.valueOf(getStringSeq());
		for (PositionAuthrity positionAuthrity : pas) {
			Date d = timeService.getSysTimestamp();
			Menu menu = positionAuthrity.getId().getTamenu();

			if ("1".equals(menu.getMenutype())) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(menu.getMenuid(), positionid, opUser.getUserid(), d, "0", "3"));

				orgOpLogService.logPermisstionOp(batchNo, opUser, "14", menu, position);
			}
			if ("1".equals(positionAuthrity.getRepermission())) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(menu.getMenuid(), positionid, opUser.getUserid(), d, "0", "4"));

				orgOpLogService.logPermisstionOp(batchNo, opUser, "20", menu, position);
			}
			if ("1".equals(positionAuthrity.getReauthrity())) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(menu.getMenuid(), positionid, opUser.getUserid(), d, "0", "5"));

				orgOpLogService.logPermisstionOp(batchNo, opUser, "22", menu, position);
			}
		}

		orgOpLogService.logPositionOp(batchNo, opUser, "23", position, "");
	}

	public List<UserInfoVO> getAdminMgUsersNoTransformPositionByPositionid(Long positionid, Long transformPosition) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空,不能转移管理员权限");
		}
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,p.orgnamepath,u.userid,u.name,u.sex,u.loginid)")
				.append(" from " + getEntityClassName(Org.class) + " o," + getEntityClassName(Position.class) + " p,UserPosition up,"
						+ getEntityClassName(User.class) + " u").append(" where p.effective=?").append(" and p.positiontype=?")
				.append(" and o.effective=?").append(" and o.orgid=p.taorg.orgid").append(" and (o.destory is null or o.destory=?)")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?")
				.append(" and u.islock=?").append(" and p.isadmin=?");

		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			hql.append(" and p.positionid not in(?,?)").append(" order by p.orgnamepath");

			return hibernateDao.createQuery(hql.toString(), new Object[] { "0", "2", "0", "1", "0", "0", "1", positionid, transformPosition }).list();
		}
		hql.append(" and p.positionid not in(?,?,?)").append(" and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
				.append(" order by p.orgnamepath");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { "0", "2", "0", "1", "0", "0", "1", positionid, IPosition.ADMIN_POSITIONID, transformPosition, positionid }).list();
	}

	public PageBean queryNoAdminUsers(String gridId, ParamDTO dto) {
		Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
		StringBuffer hql = new StringBuffer();
		String str = "";
		String strCount = "";
		strCount = "select p";
		str = "select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,p.orgnamepath,u.userid,u.name,u.sex,u.loginid)";
		hql.append(
				" from " + getEntityClassName(Org.class) + " o," + getEntityClassName(Position.class) + " p,UserPosition up,"
						+ getEntityClassName(User.class) + " u").append(" where p.effective=?").append(" and p.positiontype=?")
				.append(" and o.effective=?").append(" and (o.destory is null or o.destory=?)").append(" and o.orgid=p.taorg.orgid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?")
				.append(" and u.islock=?").append(" and (u.destory is null or u.destory=?)").append(" and (p.isadmin is null or p.isadmin=?)");

		String loginid = dto.getAsString("loginid");
		if (ValidateUtil.isNotEmpty(loginid)) {
			hql.append(" and u.loginid='").append(loginid).append("'");
		}
		String username = dto.getAsString("username");
		if (ValidateUtil.isNotEmpty(username)) {
			hql.append(" and u.name like '%").append(username).append("%'");
		}
		Long orgid = dto.getAsLong("orgid");
		if (!ValidateUtil.isEmpty(orgid)) {
			String isSubOrgUsers = dto.getAsString("isSubOrgUsers");
			if ("0".equals(isSubOrgUsers)) {
				Org o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), orgid);
				String orgidpath = o.getOrgidpath();
				hql.append(" and o.orgidpath like '").append(orgidpath).append("%'");
			} else {
				hql.append(" and o.orgid=").append(orgid);
			}
		}
		PageBean pb = new PageBean();
		Integer skipResults = Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
		Integer maxResults = Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
		pb.setStart(skipResults);
		pb.setLimit(maxResults);
		List<UserInfoVO> users = null;
		Long total = Long.valueOf(0L);
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			hql.append(" and p.positionid not in(?)").append(" order by p.orgnamepath");

			Finder finder = Finder.create(strCount + hql.toString());
			String countHql = finder.getRowCountHql();
			users = hibernateDao.createQuery(str + hql.toString(), new Object[] { "0", "2", "0", "1", "0", "0", "1", "0", positionid })
					.setFirstResult(skipResults.intValue()).setMaxResults(maxResults.intValue()).list();

			total = (Long) hibernateDao.createQuery(countHql, new Object[] { "0", "2", "0", "1", "0", "0", "1", "0", positionid }).uniqueResult();
		} else {
			hql.append(" and p.positionid not in(?,?)").append(" and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" order by p.orgnamepath");

			Finder finder = Finder.create(strCount + hql.toString());
			String countHql = finder.getRowCountHql();
			users = hibernateDao
					.createQuery(str + hql.toString(),
							new Object[] { "0", "2", "0", "1", "0", "0", "1", "0", positionid, IPosition.ADMIN_POSITIONID, positionid })
					.setFirstResult(skipResults.intValue()).setMaxResults(maxResults.intValue()).list();

			total = (Long) hibernateDao.createQuery(countHql,
					new Object[] { "0", "2", "0", "1", "0", "0", "1", "0", positionid, IPosition.ADMIN_POSITIONID, positionid }).uniqueResult();
		}
		pb.setTotal(Integer.valueOf(total.intValue()));
		pb.setList(users);
		return pb;
	}

	public List<PermissionTreeVO> getAdminRePermissionTreeByPositionid(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空,不能进行管理员管理资源的分配");
		}

		List<PermissionTreeVO> permissionnodes = new ArrayList();
		PermissionTreeVO permissionnode = null;
		List<Menu> ms = null;
		String curId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class)
								+ " m where m.effective=? and (m.menutype=? or m.menutype=null)  order by m.menulevel,m.sortno",
						new Object[] { "0", "1" }).list();
			} else {
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class)
								+ " m where m.effective=? and (m.menutype=? or m.menutype=null) and m.syspath=? order by m.menulevel,m.sortno",
						new Object[] { "0", "1", curId }).list();
			}

		} else if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
			ms = hibernateDao
					.createQuery(
							"from "
									+ getEntityClassName(Menu.class)
									+ " m where m.effective=? and (m.menutype=? or m.menutype=null) and m.menuid in (select m1.menuid from "
									+ getEntityClassName(Menu.class)
									+ " m1,PositionAuthrity pa where m1.menuid=pa.id.tamenu.menuid and pa.id.taposition.positionid=? and pa.repermission=?) order by m.menulevel,m.sortno",
							new Object[] { "0", "1", positionid, "1" }).list();
		} else {
			ms = hibernateDao
					.createQuery(
							"from "
									+ getEntityClassName(Menu.class)
									+ " m where m.effective=? and (m.menutype=? or m.menutype=null) and m.menuid in (select m1.menuid from "
									+ getEntityClassName(Menu.class)
									+ " m1,PositionAuthrity pa where m1.menuid=pa.id.tamenu.menuid and pa.id.taposition.positionid=? and pa.repermission=?) and m.syspath=? order by m.menulevel,m.sortno",
							new Object[] { "0", "1", positionid, "1", curId }).list();
		}

		if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
			List<Menu> tempMenus = hibernateDao.createQuery("from " + getEntityClassName(Menu.class) + " m where m.pmenuid=?",
					new Object[] { IMenu.ROOT_ID }).list();
			ms.addAll(tempMenus);
		}
		if (ms != null) {
			for (Menu menu : ms) {
				permissionnode = new PermissionTreeVO();
				permissionnode.setId(menu.getMenuid());
				permissionnode.setPId(menu.getPmenuid());
				permissionnode.setName(menu.getMenuname());
				permissionnode.setPolicy(menu.getSecuritypolicy());
				permissionnode.setIsleaf(menu.getIsleaf());
				permissionnode.setMenulevel(menu.getMenulevel());
				permissionnodes.add(permissionnode);
			}
		}
		return permissionnodes;
	}

	public List<PermissionTreeVO> getAdminUsePermissionTreeByPositionid(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被授权岗位id为空,不能进行使用权限的分配");
		}
		List<PermissionTreeVO> permissionnodes = new ArrayList();
		PermissionTreeVO permissionnode = null;
		List<Menu> ms = null;
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			ms = hibernateDao.createQuery(
					"from " + getEntityClassName(Menu.class) + " m where m.effective=? and (m.menutype=? or m.menutype=null) order by m.sortno",
					new Object[] { "0", "1" }).list();
		} else {
			ms = hibernateDao
					.createQuery(
							"from "
									+ getEntityClassName(Menu.class)
									+ " m where m.effective=? and m.menuid in (select m1.menuid from "
									+ getEntityClassName(Menu.class)
									+ " m1,PositionAuthrity pa where m1.menuid=pa.id.tamenu.menuid and pa.id.taposition.positionid=? and pa.usepermission=?) order by m.sortno",
							new Object[] { "0", positionid, "1" }).list();
		}

		if (ms != null) {
			for (Menu menu : ms) {
				permissionnode = new PermissionTreeVO();
				permissionnode.setId(menu.getMenuid());
				permissionnode.setPId(menu.getPmenuid());
				permissionnode.setName(menu.getMenuname());
				permissionnode.setPolicy(menu.getSecuritypolicy());
				permissionnodes.add(permissionnode);
			}
		}
		return permissionnodes;
	}

	public void saveAdminUsePermission(List<Key> menuids, Long positionid, ParamDTO dto) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被授权岗位id为空,不能进行使用权限的分配");
		}

		Long batchNo = getLongSeq();
		Long userid = dto.getUserInfo().getUserid();
		IUser opUser = dto.getUserInfo();
		IPosition position = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(opUser.getUserid(), opUser.getNowPosition().getPositionid(), positionid, "11", "03", "分配管理员使用权限，无该管理员岗位所在组织的操作权限");
		for (Key key : menuids) {
			String flag = key.getAsString("checked");
			Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), key.getAsLong("id"));

			if ("true".equals(flag)) {
				checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), key.getAsLong("id"), true, "13",
						positionid);
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(key.getAsLong("id"), positionid, userid, timeService
						.getSysTimestamp(), "1", "3"));

				orgOpLogService.logPermisstionOp(batchNo, opUser, "13", m, position);
			} else {
				checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), key.getAsLong("id"), true, "14",
						positionid);
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(key.getAsLong("id"), positionid, userid, timeService
						.getSysTimestamp(), "0", "3"));

				orgOpLogService.logPermisstionOp(batchNo, opUser, "14", m, position);
			}
		}
	}

	public Map<String, List<PermissionTreeVO>> getRePermissionAndAuthrityTreeByPositionid(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位为空!");
		}

		List<PermissionTreeVO> permissionnodes = new ArrayList<PermissionTreeVO>();

		List<PermissionTreeVO> authritynodes = new ArrayList<PermissionTreeVO>();
		PermissionTreeVO permissionnode = null;
		PermissionTreeVO authritynode = null;
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			List<Menu> ms = null;
			boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
			if (isPortal) {
				ms = hibernateDao.createQuery("from " + getEntityClassName(Menu.class) + " m where m.effective=?  order by m.menulevel,m.sortno",
						new Object[] { "0" }).list();
			} else {
				String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class) + " m where m.effective=? and m.syspath=? order by m.menulevel,m.sortno",
						new Object[] { "0", curSyspathId }).list();
			}
			if ((ms != null) && (ms.size() > 0)) {
				for (int i = 0; i < ms.size(); i++) {
					permissionnode = new PermissionTreeVO();
					authritynode = new PermissionTreeVO();
					Menu m = (Menu) ms.get(i);
					permissionnode.setId(m.getMenuid());
					permissionnode.setPId(m.getPmenuid());
					permissionnode.setName(m.getMenuname());
					permissionnode.setTitle(m.getMenuname());
					permissionnode.setPolicy(m.getSecuritypolicy());
					permissionnodes.add(permissionnode);

					authritynode.setId(m.getMenuid());
					authritynode.setPId(m.getPmenuid());
					authritynode.setName(m.getMenuname());
					authritynode.setTitle(m.getMenuname());
					authritynode.setPolicy(m.getSecuritypolicy());

					authritynode.setIsleaf(m.getIsleaf());
					authritynode.setMenulevel(m.getMenulevel());

					authritynodes.add(authritynode);
				}
			}
		} else {
			List<PositionAuthrity> positionAuthrities = null;
			boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
			if (isPortal) {
				positionAuthrities = hibernateDao
						.createQuery(
								"from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and pa.id.tamenu.effective=?  order by pa.id.tamenu.menulevel,pa.id.tamenu.sortno",
								new Object[] { positionid, timeService.getSysDate(), "0" }).list();
			} else {
				String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
				positionAuthrities = hibernateDao
						.createQuery(
								"from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and pa.id.tamenu.effective=? and pa.id.tamenu.syspath=? order by pa.id.tamenu.menulevel,pa.id.tamenu.sortno",
								new Object[] { positionid, timeService.getSysDate(), "0", curSyspathId }).list();
			}

			if (positionAuthrities != null) {
				for (int i = 0; i < positionAuthrities.size(); i++) {
					permissionnode = new PermissionTreeVO();
					authritynode = new PermissionTreeVO();
					PositionAuthrity positionAuthrity = (PositionAuthrity) positionAuthrities.get(i);
					Menu menu = positionAuthrity.getId().getTamenu();
					if ("1".equals(positionAuthrity.getRepermission())) {
						permissionnode.setId(menu.getMenuid());
						permissionnode.setPId(menu.getPmenuid());
						permissionnode.setName(menu.getMenuname());
						permissionnode.setTitle(menu.getMenuname());
						permissionnode.setPolicy(menu.getSecuritypolicy());
						permissionnodes.add(permissionnode);
					}
					if ("1".equals(positionAuthrity.getReauthrity())) {
						authritynode.setId(menu.getMenuid());
						authritynode.setPId(menu.getPmenuid());
						authritynode.setName(menu.getMenuname());
						authritynode.setTitle(menu.getMenuname());
						authritynode.setPolicy(menu.getSecuritypolicy());

						authritynode.setIsleaf(menu.getIsleaf());
						authritynode.setMenulevel(menu.getMenulevel());

						authritynodes.add(authritynode);
					}
				}
			}
		}
		Map<String, List<PermissionTreeVO>> nodes = new java.util.HashMap();
		nodes.put("premissionnodes", permissionnodes);
		nodes.put("authritynodes", authritynodes);
		return nodes;
	}

	public void transformAuthority(Long positionid, List<Key> selected, IUser opUser) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空,不能转移权限");
		}
		Long userid = opUser.getUserid();
		IPosition p = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(userid, opUser.getNowPosition().getPositionid(), positionid, "11", "03", "转移管理员权限，无该管理员岗位所在组织的操作权限");

		List<OrgMg> orgMgs = hibernateDao.createQuery("from OrgMg om where om.id.positionid=?", new Object[] { positionid }).list();
		if ((orgMgs != null) && (orgMgs.size() > 0)) {
			for (OrgMg orgMg : orgMgs) {
				Long orgid = Long.valueOf(orgMg.getId().getOrgid());
				for (Key key : selected) {
					Long poid = key.getAsLong("positionid");
					OrgMgId id = new OrgMgId();
					id.setOrgid(orgid.longValue());
					id.setPositionid(poid.longValue());
					OrgMg omg = (OrgMg) hibernateDao.getSession().get(OrgMg.class, id);
					if (ValidateUtil.isEmpty(omg)) {
						OrgMg o = new OrgMg();
						o.setId(id);
						hibernateDao.save(o);
					}
				}
				hibernateDao.delete(orgMg);
			}
		}

		List<AdminYab003Scope> ayslist = hibernateDao.createQuery("from AdminYab003Scope ays where ays.id.positionid=?", new Object[] { positionid })
				.list();
		for (AdminYab003Scope ays : ayslist) {
			AdminYab003ScopeId id = new AdminYab003ScopeId();
			id.setYab139(ays.getId().getYab139());
			for (Key key : selected) {
				id.setPositionid(key.getAsLong("positionid"));
			}
			Object object = hibernateDao.getSession().get(AdminYab003Scope.class, id);
			if (ValidateUtil.isEmpty(object)) {
				AdminYab003Scope adminYab003Scope = new AdminYab003Scope(id);
				hibernateDao.save(adminYab003Scope);
			}
		}
		hibernateDao.createQuery("delete from AdminYab003Scope ays where ays.id.positionid=?", new Object[] { positionid }).executeUpdate();

		Long batchNo = Long.valueOf(getStringSeq());

		List<PositionAuthrity> pas = hibernateDao.createQuery(
				"from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?)",
				new Object[] { positionid, timeService.getSysTimestamp() }).list();
		for (Iterator i$ = pas.iterator(); i$.hasNext();) {
			PositionAuthrity positionAuthrity = (PositionAuthrity) i$.next();
			Long menuid = positionAuthrity.getId().getTamenu().getMenuid();
			for (Key key : selected) {
				Long poid = key.getAsLong("positionid");

				if (("1".equals(positionAuthrity.getUsepermission())) && ("1".equals(positionAuthrity.getId().getTamenu().getMenutype()))) {
					Date d = timeService.getSysTimestamp();
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, poid, userid, d, "1", "3"));

					orgOpLogService.logPermisstionOp(batchNo, opUser, "13", positionAuthrity.getId().getTamenu(), positionAuthrity.getId()
							.getTaposition());
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, positionid, userid, d, "0", "3"));

					orgOpLogService.logPermisstionOp(batchNo, opUser, "14", positionAuthrity.getId().getTamenu(), positionAuthrity.getId()
							.getTaposition());
				}
				if ("1".equals(positionAuthrity.getRepermission())) {
					Date d = timeService.getSysTimestamp();
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, poid, userid, d, "1", "4"));

					orgOpLogService.logPermisstionOp(batchNo, opUser, "19", positionAuthrity.getId().getTamenu(), positionAuthrity.getId()
							.getTaposition());
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, positionid, userid, d, "0", "4"));

					orgOpLogService.logPermisstionOp(batchNo, opUser, "20", positionAuthrity.getId().getTamenu(), positionAuthrity.getId()
							.getTaposition());
				}
				if ("1".equals(positionAuthrity.getReauthrity())) {
					Date d = timeService.getSysTimestamp();
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, poid, userid, d, "1", "5"));

					orgOpLogService.logPermisstionOp(batchNo, opUser, "21", positionAuthrity.getId().getTamenu(), positionAuthrity.getId()
							.getTaposition());
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, positionid, userid, d, "0", "5"));

					orgOpLogService.logPermisstionOp(batchNo, opUser, "22", positionAuthrity.getId().getTamenu(), positionAuthrity.getId()
							.getTaposition());
				}
			}
		}
		hibernateDao.createQuery("update " + getEntityClassName(Position.class) + " p set p.isadmin=? where p.positionid=?",
				new Object[] { "0", positionid }).executeUpdate();

		Position position = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);
		orgOpLogService.logPositionOp(batchNo, opUser, "23", position, "");
	}

	public void saveRoleScopeAclGranting(ParamDTO dto) {
		Long datchNo = Long.valueOf(getStringSeq());
		Long positionid = dto.getAsLong("positionid");
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空,不能进行授权");
		}
		List<Key> addList = (List) dto.get("addList");
		List<Key> delList = (List) dto.get("delList");
		List<Key> addList2 = (List) dto.get("addList2");
		List<Key> delList2 = (List) dto.get("delList2");
		String type = dto.getAsString("positionType");
		Key key = null;
		IPosition position = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), position.getPositionid(), "11", "03",
				"管理员授权权限，无该管理员岗位所在组织的操作权限");

		for (int i = 0; i < addList.size(); i++) {
			key = (Key) addList.get(i);
			Long menuid = key.getAsLong("id");

			checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), menuid, true, "19", positionid);
			if ("2".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "1", "4"));
			} else if ("1".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "1", "4"));
			}

			Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), menuid);
			orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "19", m, position);
		}

		for (int i = 0; i < delList.size(); i++) {
			key = (Key) delList.get(i);
			Long permissionid = key.getAsLong("id");

			checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "20", positionid);
			if ("2".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "0", "4"));
			} else if ("1".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "0", "4"));
			}

			Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
			orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "20", m, position);
		}

		for (int i = 0; i < addList2.size(); i++) {
			key = (Key) addList2.get(i);
			Long permissionid = key.getAsLong("id");

			checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, false, "21", positionid);
			if ("2".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "1", "5"));
			} else if ("1".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "1", "5"));
			}

			Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
			orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "21", m, position);
		}

		for (int i = 0; i < delList2.size(); i++) {
			key = (Key) delList2.get(i);
			Long permissionid = key.getAsLong("id");

			checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, false, "22", positionid);
			if ("2".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "0", "5"));
			} else if ("1".equals(type)) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo().getUserid(), timeService
						.getSysTimestamp(), "0", "5"));
			}

			Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
			orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "22", m, position);
		}
	}

	public void recycleAuthorityPermissions(List<Key> positionList, List<Key> permissionList, ParamDTO dto) {
		Long datchNo;
		String type;
		if (positionList != null) {
			datchNo = Long.valueOf(getStringSeq());
			type = "2";
			for (Key key : positionList) {
				Long positionid = key.getAsLong("positionid");
				IPosition position = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

				checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), position.getPositionid(), "11", "03",
						"回收授权权限，无该管理员岗位�?��组织操作权限");
				if (permissionList != null)
					for (Key key1 : permissionList) {
						Long permissionid = key1.getAsLong("id");
						PositionAuthrity positionAuthrity = (PositionAuthrity) hibernateDao.createQuery(
								"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?",
								new Object[] { positionid, permissionid }).uniqueResult();

						if (positionAuthrity != null) {
							if ("1".equals(key1.getAsString("permission"))) {

								checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true,
										"20", positionid);
								if ("2".equals(type)) {
									grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
											.getUserid(), timeService.getSysTimestamp(), "0", "4"));
								} else if ("1".equals(type)) {
									grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
											.getUserid(), timeService.getSysTimestamp(), "0", "4"));
								}

								Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
								orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "20", m, position);
							}
							if ("1".equals(key1.getAsString("authrity"))) {

								checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, false,
										"22", positionid);
								if ("2".equals(type)) {
									grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
											.getUserid(), timeService.getSysTimestamp(), "0", "5"));
								} else if ("1".equals(type)) {
									grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
											.getUserid(), timeService.getSysTimestamp(), "0", "5"));
								}

								Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
								orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "22", m, position);
							}
						}
					}
			}
		}
		Long positionid;
		IPosition position;
	}

	public void grantAuthorityPermissions(List<Key> positionList, List<Key> permissionList, ParamDTO dto) {
		Long datchNo;
		String type;
		if (positionList != null) {
			datchNo = Long.valueOf(getStringSeq());
			type = "2";
			for (Key key : positionList) {
				Long positionid = key.getAsLong("positionid");
				IPosition position = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

				checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "11", "03",
						"再授权权限，无该管理员岗位所在组织的操作权限");
				if (permissionList != null)
					for (Key key1 : permissionList) {
						Long permissionid = key1.getAsLong("id");
						if ("1".equals(key1.getAsString("re"))) {

							checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, false, "21",
									positionid);
							if ("2".equals(type)) {
								grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
										.getUserid(), timeService.getSysTimestamp(), "1", "5"));
							} else if ("1".equals(type)) {
								grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
										.getUserid(), timeService.getSysTimestamp(), "1", "5"));

							}

						} else {

							checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "19",
									positionid);
							if ("2".equals(type)) {
								grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
										.getUserid(), timeService.getSysTimestamp(), "1", "4"));
							} else if ("1".equals(type)) {
								grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
										.getUserid(), timeService.getSysTimestamp(), "1", "4"));
							}
						}

						Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
						orgOpLogService.logPermisstionOp(datchNo, dto.getUserInfo(), "21", m, position);
					}
			}
		}
		Long positionid;
		IPosition position;
	}

	@Deprecated
	public List<Map<String, String>> queryAdminYab003Scope(Long userid, Long curPositionid) {
		return queryAdminYab139Scope(userid, curPositionid);
	}

	public List<Map<String, String>> queryAdminYab139Scope(Long userid, Long curPositionid) {
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("该管理员不存在");
		}
		if (ValidateUtil.isEmpty(curPositionid)) {
			throw new AppException("当前岗位不存在");
		}
		List<Map<String, String>> list = new ArrayList();
		Map<String, String> map = null;
		StringBuffer hql = new StringBuffer();
		if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
			List<AppCode> codeList = CodeTableLocator.getCodeList("yab139");
			for (AppCode appCode : codeList) {
				map = new java.util.HashMap();
				map.put("codeValue", appCode.getCodeValue());
				map.put("codeDESC", appCode.getCodeDESC());
				list.add(map);

			}

		} else {

			hql.append("select distinct ays.id.yab139 from AdminYab003Scope ays").append(" where ays.id.positionid=?");
			List<String> yab139s = hibernateDao.createQuery(hql.toString(), new Object[] { curPositionid }).list();
			for (String codeValue : yab139s) {
				map = new java.util.HashMap();
				map.put("codeValue", codeValue);
				map.put("codeDESC", CodeTableLocator.getCodeDesc("yab139", codeValue));
				list.add(map);
			}
		}
		return list;
	}

	public void saveAdminYab003Scope(List<Key> list, ParamDTO dto) {
		Long positionid = dto.getAsLong("positionid");
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被分配人员岗位为空，不能进行分中心的分配");
		}
		Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "11", "03", "分中心管理范围，无该管理员岗位所在组织操作权限");

		hibernateDao.createQuery("delete from AdminYab003Scope ays where ays.id.positionid=?", new Object[] { positionid }).executeUpdate();
		for (Key key : list) {
			String codeValue = key.getAsString("codeValue");
			AdminYab003Scope scope = new AdminYab003Scope();
			AdminYab003ScopeId id = new AdminYab003ScopeId();
			id.setPositionid(positionid);
			id.setYab139(codeValue);
			scope.setId(id);
			hibernateDao.save(scope);
		}
	}

	public List<PermissionTreeVO> getRePermissionTreeByPositionId(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空!");
		}
		String curId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		List<PermissionTreeVO> nodes = new ArrayList();
		PermissionTreeVO node;
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			List<Menu> ms = null;
			if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class) + " m where m.effective=? and m.menutype=? order by m.menulevel,m.sortno",
						new Object[] { "0", "1" }).list();
			} else {
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class)
								+ " m where m.effective=? and m.menutype=? and m.syspath=? order by m.menulevel,m.sortno",
						new Object[] { "0", "1", curId }).list();
			}

			if ((ms != null) && (ms.size() > 0)) {
				node = null;
				for (Menu menu : ms) {
					node = new PermissionTreeVO();
					node.setId(menu.getMenuid());
					node.setPId(menu.getPmenuid());
					node.setName(menu.getMenuname());
					node.setTitle(menu.getMenuname());
					node.setPolicy(menu.getSecuritypolicy());
					node.setMenulevel(menu.getMenulevel());
					node.setIconSkin(menu.getIconSkin());
					node.setUseyab003(menu.getUseyab003());
					nodes.add(node);
				}
			}
		} else {
			List<PositionAuthrity> positionAuthrities = null;
			if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
				positionAuthrities = hibernateDao
						.createQuery(
								"from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and pa.id.tamenu.menutype=? and pa.repermission=? and pa.id.tamenu.effective=? order by pa.id.tamenu.menulevel, pa.id.tamenu.sortno",
								new Object[] { positionid, timeService.getSysTimestamp(), "1", "1", "0" }).list();

			} else {

				positionAuthrities = hibernateDao
						.createQuery(
								"from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and pa.id.tamenu.menutype=? and pa.repermission=? and pa.id.tamenu.effective=? and pa.id.tamenu.syspath=? order by pa.id.tamenu.menulevel, pa.id.tamenu.sortno",
								new Object[] { positionid, timeService.getSysTimestamp(), "1", "1", "0", curId }).list();
			}

			if ((positionAuthrities != null) && (positionAuthrities.size() > 0)) {
				node = null;
				for (PositionAuthrity positionAuthrity : positionAuthrities) {
					Menu menu = positionAuthrity.getId().getTamenu();
					node = new PermissionTreeVO();
					node.setId(menu.getMenuid());
					node.setPId(menu.getPmenuid());
					node.setName(menu.getMenuname());
					node.setTitle(menu.getMenuname());
					node.setPolicy(menu.getSecuritypolicy());
					node.setUseyab003(menu.getUseyab003());
					node.setIconSkin(menu.getIconSkin());
					nodes.add(node);
				}
			}
		}

		if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
			List<Menu> tempMenus = hibernateDao.createQuery("from " + getEntityClassName(Menu.class) + " m where m.pmenuid=?",
					new Object[] { IMenu.ROOT_ID }).list();
			for (Menu menu : tempMenus) {
				PermissionTreeVO node1 = new PermissionTreeVO();
				node1.setId(menu.getMenuid());
				node1.setPId(menu.getPmenuid());
				node1.setName(menu.getMenuname());
				node1.setTitle(menu.getMenuname());
				node1.setPolicy(menu.getSecuritypolicy());
				node1.setUseyab003(menu.getUseyab003());
				node1.setIconSkin(menu.getIconSkin());
				nodes.add(node1);
			}
		}

		return nodes;
	}

	public void grantAdminUsePermissions(List<Key> pList, List<Key> list, ParamDTO dto) {
		positionMgService.grantUsePermissions(pList, list, dto);
	}

	public void recycleAdminUsePermissions(List<Key> pList, List<Key> list, ParamDTO dto) {
		positionMgService.recyclePermissions(pList, list, dto);
	}
}
