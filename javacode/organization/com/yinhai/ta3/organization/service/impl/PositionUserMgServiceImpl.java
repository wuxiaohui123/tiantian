package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.Finder;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IDataAccessApi;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IOrgOpLogService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.IPositionUserMgService;
import com.yinhai.ta3.organization.service.IUserMgService;
import com.yinhai.ta3.system.org.domain.DataAccessDimension;
import com.yinhai.ta3.system.org.domain.MenuPositionVO;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.SharePosition;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.Yab139Mg;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class PositionUserMgServiceImpl extends OrgBaseService implements IPositionUserMgService {

	private IPositionMgService positionMgService;
	private IUserMgService userMgService;
	private IGrantService grantService;
	private IOrgOpLogService orgOpLogService;
	private IDataAccessApi api;
	private OrganizationEntityService organizationEntityService;

	public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
		this.organizationEntityService = organizationEntityService;
	}

	public void setApi(IDataAccessApi api) {
		this.api = api;
	}

	public void setOrgOpLogService(IOrgOpLogService orgOpLogService) {
		this.orgOpLogService = orgOpLogService;
	}

	public void setGrantService(IGrantService grantService) {
		this.grantService = grantService;
	}

	public IUserMgService getUserMgService() {
		return userMgService;
	}

	public void setUserMgService(IUserMgService userMgService) {
		this.userMgService = userMgService;
	}

	public IPositionMgService getPositionMgService() {
		return positionMgService;
	}

	public void setPositionMgService(IPositionMgService positionMgService) {
		this.positionMgService = positionMgService;
	}

	public PageBean queryUsersByParamDto(String gridId, ParamDTO dto) {
		PageBean pb = positionMgService.getDescendantsPositionsByCount(gridId, dto);
		return pb;
	}

	public PageBean queryPositionByParamDto(String gridId, ParamDTO dto) {
		StringBuffer hql = new StringBuffer();
		Position p = (Position) dto.toDomainObject(Position.class);
		boolean isDisSubOrgs = false;

		if ("0".equals(dto.getAsString("isDisDecPositions"))) {
			isDisSubOrgs = true;
		}
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class)).append(" p");
		Long curPos = dto.getUserInfo().getNowPosition().getPositionid();
		if (IPosition.ADMIN_POSITIONID.equals(curPos)) {
			hql.append(" where 1=1 and p.positionid<>").append(curPos);
		} else {
			hql.append(" where 1=1 and p.positionid not in(").append(curPos).append(",").append(IPosition.ADMIN_POSITIONID).append(")");
			hql.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=").append(curPos).append(")");
		}
		Long orgid = dto.getAsLong("orgid");
		if (ValidateUtil.isEmpty(orgid)) {
			orgid = Org.ORG_ROOT_ID;
			Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), orgid);
			hql.append(" and p.orgidpath like '").append(org.getOrgidpath()).append("%'").append(" and p.taorg.effective=").append("0")
					.append(" and (p.taorg.destory is null or p.taorg.destory=").append("1)");

		} else if (isDisSubOrgs) {
			Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), orgid);
			hql.append(" and p.orgidpath like '").append(org.getOrgidpath()).append("%'").append(" and p.taorg.effective=").append("0")
					.append(" and (p.taorg.destory is null or p.taorg.destory=").append("1)");
		} else {
			hql.append(" and p.taorg.orgid=").append(orgid);
		}

		String positionname = p.getPositionname();
		if (ValidateUtil.isNotEmpty(positionname)) {
			hql.append(" and p.positionname like :positionname");
		}
		hql.append(" and p.positiontype=").append("1").append(" order by p.orgidpath");

		Integer skipResults = Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
		Integer maxResults = Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
		Query query = hibernateDao.createQuery(hql.toString(), new Object[0]).setFirstResult(skipResults.intValue())
				.setMaxResults(maxResults.intValue());
		PageBean pb = new PageBean();
		pb.setStart(skipResults);
		pb.setLimit(maxResults);
		Finder finder = Finder.create(hql.toString());
		String countHql = finder.getRowCountHql();
		Query queryCount = hibernateDao.createQuery(countHql, new Object[0]);
		if (ValidateUtil.isNotEmpty(positionname)) {
			query.setString("positionname", "%" + positionname + "%");
			queryCount.setString("positionname", "%" + positionname + "%");
		}
		List<Position> positions = query.list();
		pb.setList(positions);
		Long total = (Long) queryCount.uniqueResult();
		pb.setTotal(Integer.valueOf(total.intValue()));
		return pb;
	}

	public PositionInfoVO queryPerMission() {
		return null;
	}

	public List<PositionInfoVO> queryPositionByUserid(Long userid) {
		return positionMgService.getPubPositionsCurUserid(userid);
	}

	public User queryUserByUserid(Long userid) {
		return userMgService.getUser(userid);
	}

	public List<MenuPositionVO> queryPositionPermissionsByUserId(Long userid) {
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("用户id为空");
		}
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select new com.yinhai.ta3.system.org.domain.MenuPositionVO(m.menuid,m.pmenuid,m.menuidpath,m.isleaf,m.useyab003,m.securitypolicy, m.menuname, m.menulevel,pa.effecttime, pa.auditstate) from "
						+ super.getEntityClassName(User.class.getName())
						+ " u,UserPosition up,"
						+ super.getEntityClassName(Position.class.getName())
						+ " p,PositionAuthrity pa," + super.getEntityClassName(Menu.class.getName()) + " m").append(" where 1=1")
				.append(" and u.userid=?").append(" and u.userid=up.id.tauser.userid").append(" and up.id.taposition.positionid=p.positionid")
				.append(" and p.effective=?").append(" and (p.validtime is null or p.validtime >= ?)")
				.append(" and p.positionid=pa.id.taposition.positionid").append(" and pa.usepermission=?")
				.append(" and pa.id.tamenu.menuid=m.menuid").append(" and m.menutype<>?").append(" order by m.menulevel,m.sortno");

		List<MenuPositionVO> result = hibernateDao.createQuery(hql.toString(), new Object[] { userid, "0", super.getSysDate(), "1", "1" }).list();

		List<MenuPositionVO> ret = new ArrayList();
		for (MenuPositionVO mvo : result) {
			if (!ret.contains(mvo)) {
				ret.add(mvo);
			}
		}
		return ret;
	}

	private List<MenuPositionVO> queryPositionPermissionsByUserIdOld(Long userid) {
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("用户id为空");
		}
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select new com.yinhai.ta3.system.org.domain.MenuPositionVO(m.menuid,m.pmenuid,m.menuidpath,m.isleaf,m.useyab003,m.securitypolicy, m.menuname, m.menulevel,pa.effecttime, pa.auditstate, p.positionid,p.positionname, p.orgnamepath||'/'||p.positionname,p.positiontype) from "
						+ super.getEntityClassName(User.class.getName())
						+ " u,UserPosition up,"
						+ super.getEntityClassName(Position.class.getName())
						+ " p,PositionAuthrity pa," + super.getEntityClassName(Menu.class.getName()) + " m").append(" where 1=1")
				.append(" and u.userid=?").append(" and u.userid=up.id.tauser.userid").append(" and up.id.taposition.positionid=p.positionid")
				.append(" and p.effective=?").append(" and (p.validtime is null or p.validtime >= ?)")
				.append(" and p.positionid=pa.id.taposition.positionid").append(" and pa.usepermission=?")
				.append(" and pa.id.tamenu.menuid=m.menuid").append(" order by p.positionid,m.menulevel,m.sortno");

		return hibernateDao.createQuery(hql.toString(), new Object[] { userid, "0", super.getSysDate(), "1" }).list();
	}

	public List<MenuPositionVO> queryPositionPermissionsByPositionId(Long positionid) {
		StringBuffer hql = new StringBuffer();
		hql.append("from PositionAuthrity pa").append(" where pa.id.taposition.positionid=?").append(" and pa.usepermission=?")
				.append(" and (pa.id.tamenu.menutype<>?)").append(" and pa.id.tamenu.effective=?")
				.append(" order by pa.id.tamenu.menulevel, pa.id.tamenu.sortno");

		List<PositionAuthrity> positionAuthrities = hibernateDao.createQuery(hql.toString(), new Object[] { positionid, "1", "1", "0" }).list();
		List<MenuPositionVO> menuList = new ArrayList();
		for (PositionAuthrity pa : positionAuthrities) {
			Menu menu = pa.getId().getTamenu();
			MenuPositionVO mvo = new MenuPositionVO();
			mvo.setEffecttime(pa.getEffecttime());
			mvo.setAuditstate(pa.getAuditstate());
			mvo.setMenuid(menu.getMenuid());
			mvo.setMenulevel(menu.getMenulevel());
			mvo.setMenuname(menu.getMenuname());
			mvo.setMenuidpath(menu.getMenuidpath());
			mvo.setPmenuid(menu.getPmenuid());
			mvo.setIsleaf(menu.getIsleaf());
			mvo.setPositionid(positionid);

			menuList.add(mvo);
		}

		return menuList;
	}

	public List<UserInfoVO> queryUserInPosition(Long positionid) {
		return positionMgService.getHaveThePositionUsersByPositionId(positionid);
	}

	public List<Org> querySharePosition(Long positionid) {
		return positionMgService.queryCopyPositionInOrgBySharePositionId(positionid);
	}

	public PageBean queryUsers(ParamDTO dto, String gridId, int start, int limit) {
		Long orgid = dto.getAsLong("orgid");
		String isChildren = dto.getAsString("isChildren");
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.orgnamepath,u.userid,u.name,u.sex,u.loginid)")
				.append(" from UserPosition up," + getEntityClassName(User.class) + " u," + getEntityClassName(Position.class) + " p,"
						+ getEntityClassName(Org.class) + " o").append(" where u.effective=?").append(" and u.userid=up.id.tauser.userid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and p.positiontype=?").append(" and u.directorgid=p.taorg.orgid")
				.append(" and (p.validtime is null or p.validtime >=?)").append(" and p.effective=?")
				.append(" and (u.destory is null or u.destory = ?)");

		if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
			sb.append(" and p.taorg.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=")
					.append(dto.getUserInfo().getNowPosition().getPositionid()).append(")");
		}
		if ((ValidateUtil.isNotEmpty(isChildren)) && ("isChildren".equals(isChildren))) {
			sb.append(" and p.orgidpath like o.orgidpath||'%' and o.orgid=?");
		} else {
			sb.append(" and p.taorg.orgid=?");
		}
		List<Key> userids = (List) dto.get("userids");
		if ((userids != null) && (userids.size() > 0)) {
			sb.append(" and u.userid not in(");
			for (int i = 0; i < userids.size(); i++) {
				Key key = (Key) userids.get(i);
				sb.append(key.getAsLong("userid"));
				sb.append(",");
			}
			sb.append(IUser.ROOT_USERID);
			sb.append(",").append(dto.getUserInfo().getUserid());
			sb.append(")");
		} else {
			sb.append(" and u.userid not in(").append(IUser.ROOT_USERID).append(",").append(dto.getUserInfo().getUserid()).append(")");
		}
		sb.append("order by p.orgnamepath,u.loginid");

		Query query = hibernateDao.createQuery(sb.toString(), new Object[] { "0", "2", timeService.getSysDate(), "0", "1", orgid });
		StringBuffer count = new StringBuffer("select count(*) ");
		count.append(sb.toString().substring(sb.toString().indexOf("from")));
		System.out.println(count.toString());
		Query countQuery = hibernateDao.createQuery(count.toString(), new Object[] { "0", "2", timeService.getSysDate(), "0", "1", orgid });
		query.setFirstResult(start).setMaxResults(limit);
		PageBean pb = new PageBean();
		pb.setLimit(Integer.valueOf(limit));
		pb.setGridId(gridId);
		pb.setStart(Integer.valueOf(start));
		pb.setList(query.list());
		pb.setTotal(Integer.valueOf(((Long) countQuery.uniqueResult()).intValue()));
		return pb;
	}

	public PageBean getPubPositionsNoCurUseridByOrgId(ParamDTO dto, String gridId, int start, int limit) {
		Long orgid = dto.getAsLong("orgid");
		Long userid = dto.getAsLong("userid");
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("人员id为空,无法分配岗位");
		}
		Position p = positionMgService.getPosition(dto.getUserInfo().getNowPosition().getPositionid());
		StringBuffer sb = new StringBuffer();
		sb.append("from " + getEntityClassName(Position.class) + " p where p.effective=").append("0");
		sb.append(" and p.positiontype=").append("1");
		String positionname = dto.getAsString("positionname");
		if (ValidateUtil.isNotEmpty(positionname)) {
			sb.append(" and p.positionname like :positionname");
		}
		sb.append(" and p.positionid not in(");

		if (IPosition.ADMIN_POSITIONID.equals(p.getPositionid())) {
			sb.append(p.getPositionid());
		} else {
			sb.append(p.getPositionid()).append(",").append(IPosition.ADMIN_POSITIONID);
		}

		String positionids = dto.getAsString("positionids");

		if (ValidateUtil.isNotEmpty(positionids)) {
			String[] positionidsTemp = positionids.split(",");
			sb.append(",");
			for (int i = 0; i < positionidsTemp.length; i++) {
				sb.append(Long.valueOf(positionidsTemp[i]));
				if (i < positionidsTemp.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		if (orgid != null) {
			if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
				sb.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=").append(p.getPositionid()).append(")");
			}
			if ("0".equals(dto.getAsString("isDisSubOrgs"))) {
				Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), orgid);
				if (org != null) {
					sb.append(" and (p.orgidpath = '").append(org.getOrgidpath()).append("' or p.orgidpath like '").append(org.getOrgidpath())
							.append("/%')").append(" and p.taorg.effective=").append("0").append(" and (p.taorg.destory is null or p.taorg.destory=")
							.append("1").append(")");
				}
			} else {
				sb.append(" and p.taorg.orgid=").append(orgid);
			}
		}

		sb.append(" order by p.orgidpath");
		Query query = hibernateDao.createQuery(sb.toString(), new Object[0]);
		if (ValidateUtil.isNotEmpty(positionname)) {
			query.setString("positionname", "%" + positionname + "%");
		}

		StringBuffer count = new StringBuffer("select count(p.positionid) ");
		count.append(sb);
		Query countQuery = hibernateDao.createQuery(count.toString(), new Object[0]);
		if (ValidateUtil.isNotEmpty(positionname)) {
			countQuery.setString("positionname", "%" + positionname + "%");
		}

		query.setFirstResult(start).setMaxResults(limit);
		PageBean pb = new PageBean();
		pb.setLimit(Integer.valueOf(limit));
		pb.setGridId(gridId);
		pb.setStart(Integer.valueOf(start));
		pb.setList(query.list());
		pb.setTotal(Integer.valueOf(((Long) countQuery.uniqueResult()).intValue()));
		return pb;
	}

	public void recyclePermissions(List<Key> positionsList, List<Key> permissionsList, ParamDTO dto) {
		positionMgService.recyclePermissions(positionsList, permissionsList, dto);
	}

	public List<Menu> queryReUsePermissions(Long curPositionid) {
		if (ValidateUtil.isEmpty(curPositionid)) {
			throw new AppException("当前岗位为空!");
		}
		Menu rootMenu = (Menu) hibernateDao.getSession().get(Menu.class, IMenu.ROOT_ID);
		List<Menu> allMenu = new ArrayList();
		allMenu.add(rootMenu);
		allMenu.addAll(queryReUsePermissions(IMenu.ROOT_ID, curPositionid));
		if (allMenu.size() > 1) {
			return allMenu;
		}
		return null;
	}

	public List<PositionAuthrity> queryUsePermissions(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被授权岗位为�?");
		}
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct pa from PositionAuthrity pa,").append(getEntityClassName(Menu.class)).append(" m")
				.append(" where pa.id.taposition.positionid=?").append(" and pa.id.tamenu.menuid=m.menuid").append(" and pa.usepermission=?")
				.append(" and (m.menutype<>?)").append(" and m.effective=?");

		return hibernateDao.createQuery(hql.toString(), new Object[] { positionid, "1", "1", "0" }).list();
	}

	public String queryDefaultYab139s(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被授权岗位为空");
		}
		IOrg o = organizationEntityService.getDepartByPositionId(positionid);
		List<Yab139Mg> list = hibernateDao.createQuery("from Yab139Mg ym where ym.id.yab003=?", new Object[] { o.getYab003() }).list();
		String yab139s = "";
		if (!ValidateUtil.isEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				yab139s = yab139s + CodeTableLocator.getCodeDesc("YAB139", ((Yab139Mg) list.get(i)).getId().getYab139(), "9999");
				if (i < list.size() - 1) {
					yab139s = yab139s + ",";
				}
			}
		}
		return yab139s;
	}

	private List<Key> buildPermissionList(List newList, List cancleList) {
		List<Key> list = new ArrayList();
		if (newList.size() > 0) {
			for (int i = 0; i < newList.size(); i++) {
				Key key = new Key();
				key.append("id", newList.get(i));
				key.append("checked", "true");
				list.add(key);
			}
		}
		if (cancleList.size() > 0) {
			for (int i = 0; i < cancleList.size(); i++) {
				Key key = new Key();
				key.append("id", cancleList.get(i));
				key.append("checked", "false");
				list.add(key);
			}
		}
		return list;
	}

	private List buildSourceList(List allList, List newList) {
		List<Long> list = new ArrayList();
		for (Object menuid : allList) {
			if (newList.size() > 0) {
				for (int i = 0; i < newList.size(); i++) {
					if (menuid.equals(newList.get(i))) {
						break;
					}
					if (i == newList.size() - 1) {
						list.add(new Long(menuid.toString()));
					}
				}
			} else {
				list.add(new Long(menuid.toString()));
			}
		}
		return list;
	}

	public void saveRoleScopeAclOperate(ParamDTO dto) {
		List yab139Cancle = (List) JSonFactory.json2bean(dto.getAsString("yab139Cancle"), ArrayList.class);
		List outtimeList = (List) JSonFactory.json2bean(dto.getAsString("outtime"), ArrayList.class);
		List allList = (List) JSonFactory.json2bean(dto.getAsString("allChecked"), ArrayList.class);
		List cancleList = (List) JSonFactory.json2bean(dto.getAsString("cancleChecked"), ArrayList.class);
		List newList = (List) JSonFactory.json2bean(dto.getAsString("newChecked"), ArrayList.class);
		List<Key> list = buildPermissionList(newList, cancleList);
		List<Long> sourceList = buildSourceList(allList, newList);
		Long batchNo = getLongSeq();
		Long positionid = dto.getAsLong("positionid");
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位id为空,不能进行授权");
		}
		Position position = (Position) hibernateDao.getSession().get(Position.class, positionid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "11", "03", "不具有该岗位的操作权限（赋予使用权）");

		List<SharePosition> sps = new ArrayList();
		if ("1".equals(position.getIsshare())) {
			sps = hibernateDao.createQuery("from SharePosition sp where sp.id.spositionid=?", new Object[] { positionid }).list();
		}
		Long userid = dto.getUserInfo().getUserid();
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		for (int i = 0; i < list.size(); i++) {
			Key key = (Key) list.get(i);
			Long permissionid = key.getAsLong("id");
			String flag = key.getAsString("checked");
			String type = dto.getAsString("positionType");
			String isyab003 = key.getAsString("isyab003");
			if (ValidateUtil.isEmpty(isyab003)) {
				Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
				if ("false".equals(flag)) {
					checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "14", positionid);
					if ("2".equals(type)) {
						grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
								.getSysTimestamp(), "0", "3"));
						if ("0".equals(m.getUseyab003())) {
							hibernateDao
									.createQuery(
											"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
											new Object[] { positionid, permissionid, "YAB139", curSyspathId }).executeUpdate();
							api.clearCache(permissionid, positionid, "YAB139");
						}
					} else if ("1".equals(type)) {
						grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
								.getSysTimestamp(), "0", "3"));
						if ("0".equals(m.getUseyab003())) {
							hibernateDao
									.createQuery(
											"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
											new Object[] { positionid, permissionid, "YAB139", curSyspathId }).executeUpdate();
							api.clearCache(permissionid, positionid, "YAB139");
						}
						for (SharePosition sharePosition : sps) {
							grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, Long.valueOf(sharePosition.getId()
									.getDpositionid()), userid, timeService.getSysTimestamp(), "0", "3"));
							if ("0".equals(m.getUseyab003())) {
								hibernateDao
										.createQuery(
												"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
												new Object[] { Long.valueOf(sharePosition.getId().getDpositionid()), permissionid, "YAB139",
														curSyspathId }).executeUpdate();
								api.clearCache(permissionid, Long.valueOf(sharePosition.getId().getDpositionid()), "YAB139");
							}
						}
					}

					orgOpLogService.logPermisstionOp(batchNo, dto.getUserInfo(), "14", m, position);
				} else if ("true".equals(flag)) {
					checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "13", positionid);
					if ("2".equals(type)) {
						grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
								.getSysTimestamp(), "1", "3"));
						if ("0".equals(m.getUseyab003())) {
							String yab139s = dto.getAsString("yab139_" + permissionid);
							if (!ValidateUtil.isEmpty(yab139s)) {
								String[] yab139arr = yab139s.split(",");

								createYab139s(yab139arr, permissionid, positionid);
							} else {
								createDefaultYab139s(permissionid, positionid, dto.getUserInfo().getNowPosition().getPositionid());
							}
						}
					} else if ("1".equals(type)) {
						grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
								.getSysTimestamp(), "1", "3"));
						if ("0".equals(m.getUseyab003())) {
							String yab139s = dto.getAsString("yab139_" + permissionid);
							if (!ValidateUtil.isEmpty(yab139s)) {
								String[] yab139arr = yab139s.split(",");
								createYab139s(yab139arr, permissionid, positionid);
							} else {
								createDefaultYab139s(permissionid, positionid, dto.getUserInfo().getNowPosition().getPositionid());
							}
						}
						for (SharePosition sharePosition : sps) {
							grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, Long.valueOf(sharePosition.getId()
									.getDpositionid()), userid, timeService.getSysTimestamp(), "1", "3"));
							if ("0".equals(m.getUseyab003())) {
								String yab139s = dto.getAsString("yab139_" + permissionid);
								if (!ValidateUtil.isEmpty(yab139s)) {
									String[] yab139arr = yab139s.split(",");
									createYab139s(yab139arr, permissionid, Long.valueOf(sharePosition.getId().getDpositionid()));
								} else {
									createDefaultYab139s(permissionid, Long.valueOf(sharePosition.getId().getDpositionid()), dto.getUserInfo()
											.getNowPosition().getPositionid());
								}
							}
						}
					}

					orgOpLogService.logPermisstionOp(batchNo, dto.getUserInfo(), "13", m, position);
				}
			}
		}

		if (outtimeList.size() > 0) {
			for (int i = 0; i < outtimeList.size(); i++) {
				if (allList.contains(outtimeList.get(i))) {
					hibernateDao.createQuery(
							"update PositionAuthrity pa set pa.effecttime=? where pa.id.tamenu.menuid=? and pa.id.taposition.positionid=?",
							new Object[] { dto.getAsDate("h_effecttime_" + outtimeList.get(i)), Long.valueOf(String.valueOf(outtimeList.get(i))),
									positionid }).executeUpdate();
					for (SharePosition sp : sps) {
						hibernateDao.createQuery(
								"update PositionAuthrity pa set pa.effecttime=? where pa.id.tamenu.menuid=? and pa.id.taposition.positionid=?",
								new Object[] { dto.getAsDate("h_effecttime_" + outtimeList.get(i)), Long.valueOf(String.valueOf(outtimeList.get(i))),
										Long.valueOf(sp.getId().getDpositionid()) }).executeUpdate();
					}
				}
			}
		}
		Long menuid;
		String yab139s;
		for (Iterator i$ = sourceList.iterator(); i$.hasNext();) {
			menuid = (Long) i$.next();
			yab139s = dto.getAsString("yab139_" + menuid);
			if (!ValidateUtil.isEmpty(yab139s)) {
				String[] yab139arr = yab139s.split(",");
				createYab139s(yab139arr, menuid, positionid);
			}
			if (("1".equals(dto.getAsString("positionType"))) && (!ValidateUtil.isEmpty(sps)))
				for (SharePosition sp : sps)
					if (!ValidateUtil.isEmpty(yab139s)) {
						String[] yab139arr = yab139s.split(",");
						createYab139s(yab139arr, menuid, Long.valueOf(sp.getId().getDpositionid()));
					}
		}
		
		if (!ValidateUtil.isEmpty(yab139Cancle)) {
			for (int j = 0; j < yab139Cancle.size(); j++) {
				Map<String, String> map = (Map) yab139Cancle.get(j);
				for (Map.Entry<String, String> entry : map.entrySet()) {
					if (sourceList.contains(new Long((String) entry.getKey()))) {
						String yab139CancleValue = (String) entry.getValue();
						if (!ValidateUtil.isEmpty(yab139CancleValue)) {
							String[] yab139CancleArr = yab139CancleValue.split(",");
							for (int k = 0; k < yab139CancleArr.length; k++) {
								hibernateDao
										.createQuery(
												"delete from DataAccessDimension dad where dad.dimensionpermissionid=? and dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
												new Object[] { yab139CancleArr[k], positionid, new Long((String) entry.getKey()), "YAB139",
														curSyspathId }).executeUpdate();
								for (SharePosition sp : sps)
									hibernateDao
											.createQuery(
													"delete from DataAccessDimension dad where dad.dimensionpermissionid=? and dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
													new Object[] { yab139CancleArr[k], Long.valueOf(sp.getId().getDpositionid()),
															new Long((String) entry.getKey()), "YAB139", curSyspathId }).executeUpdate();
							}
						}
					}
				}
			}
		}
	}

	private void createDefaultYab139s(Long permissionid, Long positionid, Long curPositionid) {
		IOrg o = organizationEntityService.getDepartByPositionId(positionid);
		List<Yab139Mg> yab139List = hibernateDao.createQuery("from Yab139Mg ym where ym.id.yab003=?", new Object[] { o.getYab003() }).list();
		if (!ValidateUtil.isEmpty(yab139List)) {
			for (Yab139Mg ym : yab139List) {
				if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
					saveYab139(ym.getId().getYab139(), permissionid, positionid);
				} else {
					List<String> list = queryAdminMgYab139List(curPositionid);
					if (list.contains(ym.getId().getYab139())) {
						saveYab139(ym.getId().getYab139(), permissionid, positionid);
					}
				}
			}
			api.clearCache(permissionid, positionid, "YAB139");
		}
	}

	private void createYab139s(String[] yab139arr, Long permissionid, Long positionid) {
		List<AppCode> curYab139List = api.query(permissionid, positionid, "YAB139");
		for (int j = 0; j < yab139arr.length; j++) {
			if (ValidateUtil.isEmpty(curYab139List)) {
				saveYab139(yab139arr[j], permissionid, positionid);
			} else {
				for (int k = 0; k < curYab139List.size(); k++) {
					if (((AppCode) curYab139List.get(k)).getCodeValue().equals(yab139arr[j])) {
						break;
					}
					if (k == curYab139List.size() - 1) {
						saveYab139(yab139arr[j], permissionid, positionid);
					}
				}
			}
		}
		api.clearCache(permissionid, positionid, "YAB139");
	}

	private void saveYab139(String yab139, Long permissionid, Long positionid) {
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		DataAccessDimension dad = new DataAccessDimension();
		dad.setMenuid(permissionid);
		dad.setPositionid(positionid);
		dad.setDimensiontype("YAB139");
		dad.setDimensionpermissionid(yab139);
		dad.setAllaccess("1");
		dad.setSyspath(curSyspathId);
		hibernateDao.save(dad);
	}

	public List<Yab139Mg> queryDefaultYab139List(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被授权岗位为空");
		}
		IOrg o = organizationEntityService.getDepartByPositionId(positionid);
		return hibernateDao.createQuery("from Yab139Mg ym where ym.id.yab003=?", new Object[] { o.getYab003() }).list();
	}

	public List<String> queryAdminMgYab139List(Long curPositionid) {
		if (ValidateUtil.isEmpty(curPositionid)) {
			throw new AppException("当前岗位为空");
		}
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct ays.id.yab139 from AdminYab003Scope ays,").append(getEntityClassName(Position.class)).append(" p,")
				.append("UserPosition up").append(" where p.positionid=?").append(" and up.id.taposition.positionid=p.positionid")
				.append(" and p.positionid=ays.id.positionid");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionid }).list();
	}

	public List<AppCode> queryYab139List(Long positionid, Long menuid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("被授权岗位为空");
		}
		if (ValidateUtil.isEmpty(menuid)) {
			throw new AppException("菜单为空");
		}
		return api.query(menuid, positionid, "YAB139");
	}

	private List<Menu> queryReUsePermissions(Long pmenuid, Long curPositionid) {
		List<Menu> newList = new ArrayList();
		List<Menu> ms = null;
		boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		if (IPosition.ADMIN_POSITIONID.equals(curPositionid)) {
			if (isPortal) {
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class) + " m where m.effective=? and (m.menutype<>?) order by m.menulevel,m.sortno",
						new Object[] { "0", "1" }).list();
			} else {
				ms = hibernateDao.createQuery(
						"from " + getEntityClassName(Menu.class)
								+ " m where m.effective=? and (m.menutype<>?) and m.syspath=? order by m.menulevel,m.sortno",
						new Object[] { "0", "1", curSyspathId }).list();
			}
		} else if (isPortal) {
			ms = hibernateDao.createQuery(
					"select distinct m from PositionAuthrity pa," + getEntityClassName(Menu.class)
							+ " m where pa.id.tamenu.menuid=m.menuid and pa.id.taposition.positionid=? "
							+ "and (pa.effecttime is null or pa.effecttime >=?) and m.menutype<>?  "
							+ "and pa.repermission=? and m.effective=? order m.menulevel,m.sortno",
					new Object[] { curPositionid, timeService.getSysDate(), "1", "1", "0" }).list();

		} else {

			ms = hibernateDao.createQuery(
					"select distinct m from PositionAuthrity pa," + getEntityClassName(Menu.class)
							+ " m  where pa.id.tamenu.menuid=m.menuid and pa.id.taposition.positionid=? "
							+ "and (pa.effecttime is null or pa.effecttime >=?) and m.menutype<>?  "
							+ "and pa.repermission=? and m.effective=? and m.syspath=? order by m.menulevel,m.sortno",
					new Object[] { curPositionid, timeService.getSysDate(), "1", "1", "0", curSyspathId }).list();
		}

		newList = queryChildrenMenus(IMenu.ROOT_ID, ms, Long.valueOf(2L), new ArrayList());
		return newList;
	}

	private List<Menu> queryChildrenMenus(Long pmenuid, List<Menu> ms, Long menulevel, List<Menu> menuLevelList) {
		Iterator<Menu> iterator = ms.iterator();
		while (iterator.hasNext()) {
			Menu m = (Menu) iterator.next();
			if ((menulevel.equals(m.getMenulevel())) && (pmenuid.equals(m.getPmenuid()))) {
				menuLevelList.add(m);
				if ("1".equals(m.getIsleaf())) {
					menuLevelList.addAll(queryChildrenMenus(m.getMenuid(), ms, Long.valueOf(menulevel.longValue() + 1L), new ArrayList()));
				}
			}
		}

		return menuLevelList;
	}

	public void delDataAccessDimension(Long menuid, Long positionid) {
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");

		hibernateDao.createQuery(
				"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
				new Object[] { positionid, menuid, "YAB139", curSyspathId }).executeUpdate();
		api.clearCache(menuid, positionid, "YAB139");
	}

	public List queryPositionsHaveMenuUsePermission(Long menuid, Long userid) {
		if (ValidateUtil.isEmpty(menuid)) {
			throw new AppException("菜单id为空");
		}
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("人员id为空");
		}
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class)).append(" p,")
				.append(super.getEntityClassName(User.class)).append(" u,UserPosition up,PositionAuthrity pa").append(" where u.userid=?")
				.append(" and u.userid=up.id.tauser.userid").append(" and up.id.taposition.positionid=p.positionid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime > ?)").append(" and p.positionid=pa.id.taposition.positionid")
				.append(" and pa.usepermission=?").append(" and pa.id.tamenu.menuid=?");

		return hibernateDao.createQuery(hql.toString(), new Object[] { userid, "0", super.getSysDate(), "1", menuid }).list();
	}

}
