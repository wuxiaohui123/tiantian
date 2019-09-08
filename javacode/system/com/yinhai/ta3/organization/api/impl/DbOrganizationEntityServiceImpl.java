package com.yinhai.ta3.organization.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.springframework.util.Assert;

import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.Md5PasswordEncoder;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.sysapp.domain.Menu;
@SuppressWarnings("all")
public class DbOrganizationEntityServiceImpl extends BaseService implements OrganizationEntityService {

	private Md5PasswordEncoder md5PasswordEncoder;
	private SimpleDao hibernateDao;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setMd5PasswordEncoder(Md5PasswordEncoder md5PasswordEncoder) {
		this.md5PasswordEncoder = md5PasswordEncoder;
	}

	public IUser getUserByUserId(Long userId) {
		return (IUser) hibernateDao
				.createQuery(
						"from " + super.getEntityClassName(User.class.getName())
								+ " u where u.userid=? and (u.destory is null or u.destory=?)",
						new Object[] { userId, "1" })
				.uniqueResult();
	}

	public IUser getUserByLoginId(String loginId) {
		return (IUser) hibernateDao
				.createQuery(
						"from " + super.getEntityClassName(User.class.getName())
								+ " u where u.loginid=? and (u.destory is null or u.destory=?)",
						new Object[] { loginId, "1" })
				.uniqueResult();
	}

	public List<IUser> getUserByName(String username) {
		return hibernateDao.createQuery("from " + super.getEntityClassName(User.class.getName())
				+ " u where u.name=? and (u.destory is null or u.destory=?)", new Object[] { username, "1" }).list();
	}

	public IUser getUserBySameLevelName(Long orgid, String username) {
		List<IUser> list = hibernateDao.createQuery(
				"from " + super.getEntityClassName(User.class.getName())
						+ " u where u.name=? and (u.destory is null or u.destory=?) and u.directorgid=?",
				new Object[] { username, "1", orgid }).list();
		if (!ValidateUtil.isEmpty(list)) {
			return (IUser) list.get(0);
		}
		return null;
	}

	public List<IUser> getAllUsers(Boolean isEffective, Boolean isLock) {
		StringBuffer hql = new StringBuffer();
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			if ((!ValidateUtil.isEmpty(isEffective)) && (!ValidateUtil.isEmpty(isLock))) {
				if ((isEffective.booleanValue()) && (!isLock.booleanValue()))
					return hibernateDao.createQuery(
							"from " + super.getEntityClassName(User.class.getName())
									+ " u where (u.destory is null or u.destory=?) and u.effective=? and u.islock=?  order by u.directorgid",
							new Object[] { "1", "0", "0" }).list();
				if ((isEffective.booleanValue()) && (isLock.booleanValue()))
					return hibernateDao.createQuery(
							"from " + super.getEntityClassName(User.class.getName())
									+ " u where (u.destory is null or u.destory=?) and u.effective=? and u.islock=?  order by u.directorgid",
							new Object[] { "1", "0", "1" }).list();
				if ((!isEffective.booleanValue()) && (!isLock.booleanValue()))
					return hibernateDao.createQuery(
							"from " + super.getEntityClassName(User.class.getName())
									+ " u where (u.destory is null or u.destory=?) and u.effective=? and u.islock=? order by u.directorgid",
							new Object[] { "1", "1", "0" }).list();
				if ((!isEffective.booleanValue()) && (isLock.booleanValue()))
					return hibernateDao.createQuery(
							"from " + super.getEntityClassName(User.class.getName())
									+ " u where (u.destory is null or u.destory=?) and u.effective=? and u.islock=? order by u.directorgid",
							new Object[] { "1", "1", "1" }).list();
			} else {
				if ((!ValidateUtil.isEmpty(isEffective)) && (ValidateUtil.isEmpty(isLock))) {
					if (isEffective.booleanValue()) {
						return hibernateDao.createQuery(
								"from " + super.getEntityClassName(User.class.getName())
										+ " u where (u.destory is null or u.destory=?) and u.effective=? order by u.directorgid",
								new Object[] { "1", "0" }).list();
					}
					return hibernateDao.createQuery(
							"from " + super.getEntityClassName(User.class.getName())
									+ " u where (u.destory is null or u.destory=?) and u.effective=? order by u.directorgid",
							new Object[] { "1", "1" }).list();
				}
				if ((ValidateUtil.isEmpty(isEffective)) && (!ValidateUtil.isEmpty(isLock))) {
					if (isLock.booleanValue()) {
						return hibernateDao.createQuery(
								"from " + super.getEntityClassName(User.class.getName())
										+ " u where (u.destory is null or u.destory=?) and u.islock=? order by u.directorgid",
								new Object[] { "1", "1" }).list();
					}
					return hibernateDao.createQuery(
							"from " + super.getEntityClassName(User.class.getName())
									+ " u where (u.destory is null or u.destory=?) and u.islock=? order by u.directorgid",
							new Object[] { "1", "0" }).list();
				}
			}
			return hibernateDao.createQuery(
					"from " + super.getEntityClassName(User.class.getName())
							+ " u where (u.destory is null or u.destory=?) order by u.directorgid",
					new Object[] { "1" }).list();
		}
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName())).append(" u,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o,OrgMg om")
				.append(" where om.id.orgid=o.orgid").append(" and om.id.positionid=?")
				.append(" and o.orgid=u.directorgid").append(" and o.effective=?")
				.append(" and (o.destory is null or o.destory=?)").append(" and (u.destory is null or u.destory=?)");

		if ((!ValidateUtil.isEmpty(isEffective)) && (!ValidateUtil.isEmpty(isLock))) {
			hql.append(" and u.effective=? and u.islock=?  order by u.directorgid");
			if ((isEffective.booleanValue()) && (!isLock.booleanValue()))
				return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "0", "0" })
						.list();
			if ((isEffective.booleanValue()) && (isLock.booleanValue()))
				return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "0", "1" })
						.list();
			if ((!isEffective.booleanValue()) && (!isLock.booleanValue()))
				return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "1", "0" })
						.list();
			if ((!isEffective.booleanValue()) && (isLock.booleanValue()))
				return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "1", "1" })
						.list();
		} else {
			if ((!ValidateUtil.isEmpty(isEffective)) && (ValidateUtil.isEmpty(isLock))) {
				hql.append(" and u.effective=? order by u.directorgid");
				if (isEffective.booleanValue()) {
					return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "0" })
							.list();
				}
				return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "1" })
						.list();
			}
			if ((ValidateUtil.isEmpty(isEffective)) && (!ValidateUtil.isEmpty(isLock))) {
				if (isLock.booleanValue()) {
					hql.append(" and u.islock=? order by u.directorgid");
					return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "1" })
							.list();
				}
				return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1", "0" })
						.list();
			}
		}
		hql.append(" order by u.directorgid");
		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", "1" }).list();
	}

	public List<IUser> getAllUsersByOrg(Org org) {
		if (ValidateUtil.isEmpty(org)) {
			throw new AppException("组织为空");
		}
		return getAllUsersByDepartId(org.getOrgid());
	}

	public List<IUser> getAllUsersByDepartId(Long departId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName())).append(" u,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o,")
				.append(super.getEntityClassName(Org.class.getName())).append(" so");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append(" where 1= 1").append(" and so.orgidpath like o.orgidpath||'%'").append(" and so.effective=?")
					.append(" and (so.destory is null or so.destory=?)").append(" and o.orgid=?")
					.append(" and so.orgid=u.directorgid").append(" and u.effective=?")
					.append(" and (u.destory is null or u.destory=?)").append(" order by u.directorgid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { "0", "1", departId, "0", "1" }).list();
		}
		hql.append(" ,OrgMg om").append(" where 1= 1 and so.orgid=om.id.orgid").append(" and om.id.positionid=?")
				.append(" and so.orgidpath like o.orgidpath||'%'").append(" and so.effective=?")
				.append(" and (so.destory is null or so.destory=?)").append(" and o.orgid=?")
				.append(" and so.orgid=u.directorgid").append(" and u.effective=?")
				.append(" and (u.destory is null or u.destory=?)").append(" order by u.directorgid");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, "0", "1", departId, "0", "1" })
				.list();
	}

	public List<IUser> getUsersByOrg(Org org) {
		if (ValidateUtil.isEmpty(org)) {
			throw new AppException("组织为空");
		}
		return getUsersByDepartId(org.getOrgid());
	}

	public List<IUser> getUsersByDepartId(Long departId) {
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			return hibernateDao.createQuery(
					"from " + super.getEntityClassName(User.class.getName())
							+ " u where u.directorgid=? and (u.destory is null or u.destory=?)  and u.effective=? ",
					new Object[] { departId, "1", "0" }).list();
		}
		Object uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.orgid=? and om.id.positionid=?",
				new Object[] { departId, curPositionId }).uniqueResult();
		if (uniqueResult == null) {
			throw new AppException("你没有该组织的操作权限，无法查询该组织下的人员列表");
		}
		return hibernateDao.createQuery(
				"from " + super.getEntityClassName(User.class.getName())
						+ " u where u.directorgid=? and (u.destory is null or u.destory=?) and u.effective=?",
				new Object[] { departId, "1", "0" }).list();
	}

	public List<IUser> getUsersByPositionId(Long positionId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,").append(super.getEntityClassName(Position.class.getName())).append(" p");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append(" where p.positionid=?").append(" and p.positionid=up.id.taposition.positionid")
					.append(" and up.id.tauser.userid=u.userid").append(" and (u.destory is null or u.destory=?)")
					.append(" and u.effective=?").append(" order by u.directorgid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { positionId, "1", "0" }).list();
		}
		IPosition position = getPositionByPositionId(positionId);
		Object uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.orgid=? and om.id.positionid=?",
				new Object[] { position.getOrgid(), curPositionId }).uniqueResult();
		if (uniqueResult == null) {
			throw new AppException("你没有该岗位的操作权限，无法查询该岗位下的人员列表");
		}
		hql.append(",OrgMg om").append(" where om.id.positionid=?").append(" and om.id.orgid=u.directorgid")
				.append(" and p.positionid=?").append(" and p.positionid=up.id.taposition.positionid")
				.append(" and up.id.tauser.userid=u.userid").append(" and (u.destory is null or u.destory=?)")
				.append(" and u.effective=?").append(" order by u.directorgid");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, positionId, "1", "0" }).list();
	}

	public List<IUser> getUsersByMenuId(Long menuId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,").append(super.getEntityClassName(Position.class.getName()))
				.append(" p,PositionAuthrity pa,").append(super.getEntityClassName(Menu.class.getName())).append(" m");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append(" where m.effective=?").append(" and m.menuid=?").append(" and m.menuid=pa.id.tamenu.menuid")
					.append(" and (pa.effecttime is null or pa.effecttime>=?)").append(" and pa.usepermission=?")
					.append(" and pa.id.taposition.positionid=p.positionid").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime>=?)")
					.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
					.append(" and (u.destory is null or u.destory=?)").append(" and u.effective=?")
					.append(" order by u.directorgid");

			return hibernateDao
					.createQuery(hql.toString(),
							new Object[] { "0", menuId, super.getSysDate(), "1", "0", super.getSysDate(), "1", "0" })
					.list();
		}
		hql.append(",OrgMg om").append(" where m.effective=?").append(" and om.id.orgid=u.directorgid")
				.append(" and om.id.positionid=?").append(" and m.menuid=?").append(" and m.menuid=pa.id.tamenu.menuid")
				.append(" and (pa.effecttime is null or pa.effecttime>=?)").append(" and pa.usepermission=?")
				.append(" and pa.id.taposition.positionid=p.positionid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime>=?)")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
				.append(" and (u.destory is null or u.destory=?)").append(" and u.effective=?")
				.append(" order by u.directorgid");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { "0", curPositionId, menuId, super.getSysDate(), "1", "0", super.getSysDate(), "1", "0" })
				.list();
	}

	public IUser getOrgmanagerUserByDepartId(Long departId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,").append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where o.orgid=?")
				.append(" and o.orgmanager=p.positionid").append(" and p.positionid=up.id.taposition.positionid")
				.append(" and up.id.tauser.userid=u.userid").append(" and (u.destory is null or u.destory=?)")
				.append(" and u.effective=?").append(" and u.islock=?");

		return (User) hibernateDao.createQuery(hql.toString(), new Object[] { departId, "1", "0", "0" }).uniqueResult();
	}

	public List<IUser> getDeputyUsersByDepartId(Long departId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,ManagerMg mm,").append(super.getEntityClassName(Position.class.getName()))
				.append(" p").append(" where mm.id.orgid=?").append(" and mm.id.positionid=p.positionid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
				.append(" and u.effective=?").append(" and u.islock=?")
				.append(" and (u.destory is null or u.destory=?)");

		return hibernateDao.createQuery(hql.toString(), new Object[] { departId, "0", "0", "1" }).list();
	}

	public IOrg getDepart(Long departId) {
		return (IOrg) hibernateDao
				.createQuery(
						"from " + super.getEntityClassName(Org.class.getName())
								+ " o where o.orgid=? and (o.destory is null or o.destory=?)",
						new Object[] { departId, "1" })
				.uniqueResult();
	}

	public IOrg getDepartByUser(User user) {
		if (ValidateUtil.isEmpty(user)) {
			throw new AppException("用户为空");
		}
		return getDepart(user.getDirectorgid());
	}

	public IOrg getDepartByUserId(Long userId) {
		IUser iUser = getUserByUserId(userId);
		return getDepartByUser((User) iUser);
	}

	public IOrg getDepartByPositionId(Long positionId) {
		return (IOrg) hibernateDao
				.createQuery("select o from " + super.getEntityClassName(Org.class.getName()) + " o,"
						+ super.getEntityClassName(Position.class.getName())
						+ " p where p.positionid=? and p.taorg.orgid=o.orgid", new Object[] { positionId })
				.uniqueResult();
	}

	public List<IOrg> getDepartByDepartName(String departName) {
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			return hibernateDao.createQuery(
					"from " + super.getEntityClassName(Org.class.getName())
							+ " o where o.orgname=? and (o.destory is null or o.destory=?) and o.effective=?",
					new Object[] { departName, "1", "0" }).list();
		}
		return hibernateDao.createQuery(
				"select distinct o from " + super.getEntityClassName(Org.class.getName())
						+ " o,OrgMg om where o.orgname=? and (o.destory is null or o.destory=?) and o.effective=? and om.id.orgid=o.orgid and om.id.positionid=?",
				new Object[] { departName, "1", "0", curPositionId }).list();
	}

	public IOrg getDepartBySameLevelDepartName(Long pDepartId, String departName) {
		Assert.notNull(pDepartId, "父组织id为空");
		Assert.notNull(departName, "组织名称为空");
		return (IOrg) hibernateDao.createQuery(
				"from " + super.getEntityClassName(Org.class.getName())
						+ " o where o.pOrg.orgid=? and o.orgname=? and (o.destory is null or o.destory=?)",
				new Object[] { pDepartId, departName, "1" }).uniqueResult();
	}

	public List<IOrg> getAllDeparts() {
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			return hibernateDao.createQuery(
					"select from " + super.getEntityClassName(Org.class.getName())
							+ " o where (o.destory is null or o.destory=?) and o.effective=?",
					new Object[] { "1", "0" }).list();
		}
		return hibernateDao.createQuery(
				"select distinct o from " + super.getEntityClassName(Org.class.getName())
						+ " o,OrgMg om where (o.destory is null or o.destory=?) and o.effective=? and om.id.orgid=o.orgid and om.id.positionid=?",
				new Object[] { "1", "0", curPositionId }).list();
	}

	public List<IOrg> getDepartsByDepartId(Long departId) {
		StringBuffer hql = null;
		hql = new StringBuffer("select so from " + super.getEntityClassName(Org.class.getName()) + " o,"
				+ super.getEntityClassName(Org.class.getName()) + " so ");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append("where 1=1").append(" and o.orgid=?").append(" and (so.destory is null or so.destory=?)")
					.append(" and so.effective=?").append(" and so.orgidpath like o.orgidpath||'/%'")
					.append(" order by so.sort");

			return hibernateDao.createQuery(hql.toString(), new Object[] { departId, "1", "0" }).list();
		}
		hql.append(",OrgMg om").append(" where 1=1").append(" and om.id.orgid=so.orgid")
				.append(" and om.id.positionid=?").append(" and o.orgid=?")
				.append(" and (so.destory is null or so.destory=?)").append(" and so.effective=?")
				.append(" and so.orgidpath like o.orgidpath||'/%'").append(" order by so.sort");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, departId, "1", "0" }).list();
	}

	public List<IOrg> getDepartsAndSelfByDepartId(Long departId) {
		StringBuffer hql = null;
		hql = new StringBuffer("select so from " + super.getEntityClassName(Org.class.getName()) + " o,"
				+ super.getEntityClassName(Org.class.getName()) + " so ");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append("where 1=1").append(" and o.orgid=?").append(" and (so.destory is null or so.destory=?)")
					.append(" and so.effective=?").append(" and so.orgidpath like o.orgidpath||'%'")
					.append(" order by so.sort");

			return hibernateDao.createQuery(hql.toString(), new Object[] { departId, "1", "0" }).list();
		}
		hql.append(",OrgMg om").append(" where 1=1").append(" and om.id.orgid=so.orgid")
				.append(" and om.id.positionid=?").append(" and o.orgid=?")
				.append(" and (so.destory is null or so.destory=?)").append(" and so.effective=?")
				.append(" and so.orgidpath like o.orgidpath||'%'").append(" order by so.sort");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, departId, "1", "0" }).list();
	}

	public List<IOrg> getChildDepartsByPorg(Org porg) {
		if (ValidateUtil.isEmpty(porg)) {
			throw new AppException("组织为空");
		}
		return getChildDepartsByDepartId(porg.getOrgid());
	}

	public List<IOrg> getChildDepartsByDepartId(Long pDepartId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct so from ").append(super.getEntityClassName(Org.class.getName())).append(" o,")
				.append(super.getEntityClassName(Org.class.getName())).append(" so ");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append(" where 1=1").append(" and o.orgid=?").append(" and o.orgid=so.pOrg.orgid")
					.append(" and (so.destory is null or so.destory=?)").append(" and so.effective=?");

			return hibernateDao.createQuery(hql.toString(), new Object[] { pDepartId, "1", "0" }).list();
		}
		hql.append(",OrgMg om").append(" where 1=1").append(" and om.id.orgid=so.orgid")
				.append(" and om.id.positionid=?").append(" and o.orgid=?").append(" and o.orgid=so.pOrg.orgid")
				.append(" and (so.destory is null or so.destory=?)").append(" and so.effective=?");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, pDepartId, "1", "0" }).list();
	}

	public IOrg getParentDepartByOrg(Org childOrg) {
		return getDepart(childOrg.getPorgid());
	}

	public IOrg getParentDepartByDepartId(Long childDepartId) {
		return getDepart(getDepart(childDepartId).getPorgid());
	}

	public IPosition getPositionByPositionId(Long positionId) {
		return (IPosition) hibernateDao.getSession().get(super.getEntityClassName(Position.class.getName()),
				positionId);
	}

	public IPosition getPositionBySameLevelPositionName(Long orgid, String positionName) {
		Assert.notNull(orgid, "组织id为空");
		Assert.notNull(positionName, "岗位名称为空");
		return (IPosition) hibernateDao
				.createQuery("from " + super.getEntityClassName(Position.class.getName())
						+ " p where p.taorg.orgid=? and p.positionname=?", new Object[] { orgid, positionName })
				.uniqueResult();
	}

	public IPosition getPositionByUserId(Long userId) {
		if (ValidateUtil.isEmpty(userId)) {
			throw new AppException("用户id为空");
		}
		return (IPosition) hibernateDao.createQuery(
				"select p from " + super.getEntityClassName(Position.class.getName())
						+ " p,UserPosition up where up.id.tauser.userid=? and up.id.taposition.positionid=p.positionid and p.taorg.orgid=up.id.tauser.directorgid and p.positiontype=?",
				new Object[] { userId, "2" }).uniqueResult();
	}

	public List<IPosition> getAllPositionsByOrg(Org org) {
		if (ValidateUtil.isEmpty(org)) {
			throw new AppException("组织为空");
		}
		return getAllPositionsByDepartId(org.getOrgid());
	}

	public List<IPosition> getPositionsByMenuId(Long menuId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName()))
				.append(" p,PositionAuthrity pa,").append(super.getEntityClassName(Menu.class.getName())).append(" m");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append(" where 1=1").append(" and m.menuid=?").append(" and m.menuid=pa.id.tamenu.menuid")
					.append(" and pa.usepermission=?").append(" and (pa.effecttime is null or pa.effecttime >= ?)")
					.append(" and pa.id.taposition.positionid=p.positionid").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime >= ?)").append(" order by p.positionid");

			return hibernateDao.createQuery(hql.toString(),
					new Object[] { menuId, "1", super.getSysDate(), "0", super.getSysDate() }).list();
		}
		hql.append(",OrgMg om").append(" where 1=1").append(" and m.menuid=?")
				.append(" and m.menuid=pa.id.tamenu.menuid").append(" and pa.usepermission=?")
				.append(" and (pa.effecttime is null or pa.effecttime >= ?)")
				.append(" and pa.id.taposition.positionid=p.positionid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime >= ?)").append(" and p.taorg.orgid=om.id.orgid")
				.append(" and om.id.positionid=?").append(" order by p.positionid");

		return hibernateDao
				.createQuery(hql.toString(),
						new Object[] { menuId, "1", super.getSysDate(), "0", super.getSysDate(), curPositionId })
				.list();
	}

	public List<IPosition> getPositionsByUserId(Long userId) {
		return getPositions(userId, "");
	}

	public IPosition getDirectPerPositionByUserId(Long userId) {
		return (IPosition) hibernateDao
				.createQuery("select p from " + super.getEntityClassName(Position.class.getName()) + " p,"
						+ super.getEntityClassName(new StringBuilder().append(User.class.getName())
								.append(" u,UserPosition up where u.userid=? and u.userid=up.id.tauser.userid and up.id.taposition.positionid=p.positionid and u.directorgid=p.taorg.orgid and p.positiontype=?")
								.toString()),
						new Object[] { userId, "2" })
				.uniqueResult();
	}

	public List<IPosition> getSharePositionBySPositionId(Long spositionid) {
		return hibernateDao.createQuery(
				"select distinct p from SharePosition sp," + super.getEntityClassName(Position.class.getName())
						+ " p where sp.id.spositionid=? and sp.id.dpositionid=p.positionid",
				new Object[] { spositionid }).list();
	}

	public List<IPosition> getAllPositionsByDepartId(Long departId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where 1=1")
				.append(" and o.orgid=?").append(" and p.taorg.orgid=o.orgid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime >=?)");

		return hibernateDao.createQuery(hql.toString(), new Object[] { departId, "0", super.getSysDate() }).list();
	}

	public List<IPosition> getPubPositionsByOrg(Org org) {
		if (ValidateUtil.isEmpty(org)) {
			throw new AppException("组织为空");
		}
		return getPubPositionsByDepartId(org.getOrgid());
	}

	public List<IPosition> getPubPositionsByDepartId(Long departId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where 1=1")
				.append(" and o.orgid=?").append(" and p.taorg.orgid=o.orgid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime >=?)").append(" and p.positiontype=?");

		return hibernateDao.createQuery(hql.toString(), new Object[] { departId, "0", super.getSysDate(), "1" }).list();
	}

	public List<IPosition> getPerPositionsByDepartId(Long departId, Boolean iseffective) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where 1=1")
				.append(" and o.orgid=?").append(" and p.taorg.orgid=o.orgid")
				.append(" and (p.validtime is null or p.validtime >=?)").append(" and p.positiontype=?");

		if (ValidateUtil.isEmpty(iseffective)) {
			return hibernateDao.createQuery(hql.toString(), new Object[] { departId, super.getSysDate(), "2" }).list();
		}
		hql.append(" and p.effective=?");
		if (iseffective.booleanValue()) {
			return hibernateDao.createQuery(hql.toString(), new Object[] { departId, super.getSysDate(), "2", "0" })
					.list();
		}
		return hibernateDao.createQuery(hql.toString(), new Object[] { departId, super.getSysDate(), "2", "1" }).list();
	}

	public List<IPosition> getPubPositionsByUserId(Long userId) {
		return getPositions(userId, "1");
	}

	public List<IPosition> getPerPositionsByUserId(Long userId) {
		return getPositions(userId, "2");
	}

	public boolean checkUserLoginIdAndPass(String loginId, String password) {
		User user = (User) getUserByLoginId(loginId);
		return md5PasswordEncoder.isPasswordValid(user.getPassword(), password, loginId);
	}

	@Deprecated
	public List<IUser> getUsersByYab003AndPositionName(String yab003, String positionname, String positionCategory) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,").append(super.getEntityClassName(Position.class.getName())).append(" p")
				.append(" where p.taorg.yab003 = ?").append(" and p.positionname = ?")
				.append(" and p.positionid = up.id.taposition.positionid").append(" and up.id.tauser.userid = u.userid")
				.append(" and u.effective = ?").append(" and (u.destory is null or u.destory = ?)");
		if ("01".equals(positionCategory)) {
			hql.append(" and p.positioncategory = ?").append(" order by u.userid");
			return hibernateDao.createQuery(hql.toString(), yab003, positionname, "0", "1", "01").list();
		}
		if ("02".equals(positionCategory)) {
			hql.append(" and p.positioncategory = ?").append(" order by u.userid");
			return hibernateDao.createQuery(hql.toString(), yab003, positionname, "0", "1", "02").list();
		}
		return hibernateDao.createQuery(hql.toString(), yab003, positionname, "0", "1").list();
	}

	@Deprecated
	public List<IUser> queryUserByMenuIdAndYab003(Long menuid, String yab003, String positionCategory) {
		if (ValidateUtil.isEmpty(menuid)) {
			throw new AppException("菜单id不能为空");
		}
		StringBuffer hql = new StringBuffer();
		if (ValidateUtil.isEmpty(yab003)) {
			hql.append("select distinct u from ").append(super.getEntityClassName(Menu.class.getName()))
					.append(" m,PositionAuthrity pa,").append(getEntityClassName(Position.class.getName()))
					.append(" p,").append(" UserPosition up,").append(getEntityClassName(User.class.getName()))
					.append(" u").append(" where 1=1").append(" and m.menuid=?")
					.append(" and m.menuid=pa.id.tamenu.menuid").append(" and pa.id.taposition.positionid=p.positionid")
					.append(" and p.effective=?").append(" and (p.validtime is null or p.validtime>=?)")
					.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
					.append(" and u.effective=?");

			if (ValidateUtil.isNotEmpty(positionCategory)) {
				hql.append(" and p.positioncategory=?");
				return hibernateDao
						.createQuery(hql.toString(), menuid, "0", timeService.getSysDate(), "0", positionCategory)
						.list();
			}
			return hibernateDao.createQuery(hql.toString(), menuid, "0", timeService.getSysDate(), "0").list();
		}

		hql.append("select distinct u from ").append(getEntityClassName(Menu.class.getName()))
				.append(" m,PositionAuthrity pa,").append(getEntityClassName(Position.class.getName())).append(" p,")
				.append(getEntityClassName(Org.class.getName())).append(" o,UserPosition up,")
				.append(getEntityClassName(User.class.getName())).append(" u").append(" where 1=1")
				.append(" and m.menuid=?").append(" and m.menuid=pa.id.tamenu.menuid")
				.append(" and pa.id.taposition.positionid=p.positionid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.taorg.orgid=o.orgid")
				.append(" and o.yab003=?").append(" and p.positionid=up.id.taposition.positionid")
				.append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?");

		if (ValidateUtil.isNotEmpty(positionCategory)) {
			hql.append(" and p.positioncategory=?");
			return hibernateDao
					.createQuery(hql.toString(),
							new Object[] { menuid, "0", timeService.getSysDate(), yab003, "0", positionCategory })
					.list();
		}
		return hibernateDao
				.createQuery(hql.toString(), new Object[] { menuid, "0", timeService.getSysDate(), yab003, "0" })
				.list();
	}

	public List<IUser> getUsersByYab139AndPositionName(String yab139, String positionname, String positionCategory) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,").append(super.getEntityClassName(Position.class.getName())).append(" p")
				.append(" where p.taorg.yab139=?").append(" and p.positionname=?")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
				.append(" and u.effective=?").append(" and (u.destory is null or u.destory=?)");

		if ("01".equals(positionCategory)) {
			hql.append(" and p.positioncategory=?").append(" order by u.userid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, positionname, "0", "1", "01" })
					.list();
		}
		if ("02".equals(positionCategory)) {
			hql.append(" and p.positioncategory=?").append(" order by u.userid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, positionname, "0", "1", "02" })
					.list();
		}
		return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, positionname, "0", "1" }).list();
	}

	public List<IUser> queryUserByMenuIdAndYab139(Long menuid, String yab139, String positionCategory) {
		if (ValidateUtil.isEmpty(menuid)) {
			throw new AppException("菜单id不能为空");
		}
		StringBuffer hql = new StringBuffer();
		if (ValidateUtil.isEmpty(yab139)) {
			hql.append("select distinct u from ").append(super.getEntityClassName(Menu.class.getName()))
					.append(" m,PositionAuthrity pa,").append(getEntityClassName(Position.class.getName()))
					.append(" p,").append(" UserPosition up,").append(getEntityClassName(User.class.getName()))
					.append(" u").append(" where 1=1").append(" and m.menuid=?")
					.append(" and m.menuid=pa.id.tamenu.menuid").append(" and pa.id.taposition.positionid=p.positionid")
					.append(" and p.effective=?").append(" and (p.validtime is null or p.validtime>=?)")
					.append(" and p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
					.append(" and u.effective=?");

			if (ValidateUtil.isNotEmpty(positionCategory)) {
				hql.append(" and p.positioncategory=?");
				return hibernateDao.createQuery(hql.toString(),
						new Object[] { menuid, "0", timeService.getSysDate(), "0", positionCategory }).list();
			}
			return hibernateDao.createQuery(hql.toString(), new Object[] { menuid, "0", timeService.getSysDate(), "0" })
					.list();
		}

		hql.append("select distinct u from ").append(getEntityClassName(Menu.class.getName()))
				.append(" m,PositionAuthrity pa,").append(getEntityClassName(Position.class.getName())).append(" p,")
				.append(getEntityClassName(Org.class.getName())).append(" o,UserPosition up,")
				.append(getEntityClassName(User.class.getName())).append(" u").append(" where 1=1")
				.append(" and m.menuid=?").append(" and m.menuid=pa.id.tamenu.menuid")
				.append(" and pa.id.taposition.positionid=p.positionid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.taorg.orgid=o.orgid")
				.append(" and o.yab139=?").append(" and p.positionid=up.id.taposition.positionid")
				.append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?");

		if (ValidateUtil.isNotEmpty(positionCategory)) {
			hql.append(" and p.positioncategory=?");
			return hibernateDao
					.createQuery(hql.toString(),
							new Object[] { menuid, "0", timeService.getSysDate(), yab139, "0", positionCategory })
					.list();
		}
		return hibernateDao
				.createQuery(hql.toString(), new Object[] { menuid, "0", timeService.getSysDate(), yab139, "0" })
				.list();
	}

	public List<String> queryYab139ByYab003(String yab003) {
		return hibernateDao.createQuery(
				"select distinct o.yab139 from " + super.getEntityClassName(Org.class.getName())
						+ " o where o.yab003=? and o.effective=? and (o.destory is null or o.destory=?)",
				new Object[] { yab003, "0", "1" }).list();
	}

	public List<String> queryYab003ByYab139(String yab139) {
		return hibernateDao.createQuery(
				"select distinct o.yab003 from " + super.getEntityClassName(Org.class.getName())
						+ " o where o.yab139=? and o.effective=? and (o.destory is null or o.destory=?)",
				new Object[] { yab139, "0", "1" }).list();
	}

	private boolean isDeveloper(Long curPositionId) {
		if (IPosition.ADMIN_POSITIONID.equals(curPositionId)) {
			return true;
		}
		return false;
	}

	private List<IPosition> getPositions(Long userId, String positionType) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName()))
				.append(" p,UserPosition up,").append(super.getEntityClassName(User.class.getName())).append(" u");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (isDeveloper(curPositionId)) {
			hql.append(" where 1=1").append(" and u.userid=?").append(" and u.userid=up.id.tauser.userid")
					.append(" and up.id.taposition.positionid=p.positionid").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime >= ?)");

			if ("".equals(positionType)) {
				return hibernateDao.createQuery(hql.toString(), new Object[] { userId, "0", super.getSysDate() })
						.list();
			}
			hql.append(" and p.positiontype=?");
			if ("1".equals(positionType)) {
				return hibernateDao.createQuery(hql.toString(), new Object[] { userId, "0", super.getSysDate(), "1" })
						.list();
			}
			return hibernateDao.createQuery(hql.toString(), new Object[] { userId, "0", super.getSysDate(), "2" })
					.list();
		}

		hql.append(",OrgMg om").append(" where 1=1").append(" and u.userid=?")
				.append(" and u.userid=up.id.tauser.userid").append(" and up.id.taposition.positionid=p.positionid")
				.append(" and p.effective=?").append(" and (p.validtime is null or p.validtime >= ?)")
				.append(" and om.id.positionid=?").append(" and p.taorg.orgid=om.id.orgid");

		if ("".equals(positionType)) {
			return hibernateDao
					.createQuery(hql.toString(), new Object[] { userId, "0", super.getSysDate(), curPositionId })
					.list();
		}
		hql.append(" and p.positiontype=?");
		if ("1".equals(positionType)) {
			return hibernateDao
					.createQuery(hql.toString(), new Object[] { userId, "0", super.getSysDate(), curPositionId, "1" })
					.list();
		}
		return hibernateDao
				.createQuery(hql.toString(), new Object[] { userId, "0", super.getSysDate(), curPositionId, "2" })
				.list();
	}

	public List<AppCode> queryYab139ByUserIdAndMenuId(Long userid, Long menuid) {
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select a from Aa10a1 a,DataField df where df.id.userid=? and df.id.menuid=? and df.id.yab139=a.id.codeValue and a.validFlag=? and a.id.codeType=?");
		return hibernateDao.createQuery(hql.toString(), new Object[] { userid, menuid, "0", "YAB139" }).list();
	}

	public List<IMenu> queryMenusByPositionId(Long positionId) {
		if (ValidateUtil.isEmpty(positionId)) {
			throw new AppException("岗位为空");
		}
		if (IPosition.ADMIN_POSITIONID.equals(positionId)) {
			if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
				return hibernateDao
						.createQuery(
								"from " + getEntityClassName(Menu.class.getName())
										+ " where effective=? and resourcetype=? and (securitypolicy=? or securitypolicy=?) and syspath=? order by sortno",
								new Object[] { "0", "01", "4", "1", SysConfig.getSysConfig("curSyspathId", "sysmg") })
						.list();
			}
			return hibernateDao.createQuery(
					"from " + getEntityClassName(Menu.class.getName())
							+ " where effective=? and resourcetype=? and (securitypolicy=? or securitypolicy=?)  order by sortno",
					new Object[] { "0", "01", "4", "1" }).list();
		}

		List<IMenu> list = new ArrayList();
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct a from " + getEntityClassName(Menu.class.getName()) + " a,PositionAuthrity d,"
				+ getEntityClassName(Position.class.getName()) + " e,UserPosition f ").append("where ")
				.append(" e.effective=?").append("and (e.validtime is null or e.validtime >=?) ")
				.append("and e.positionid = f.id.taposition.positionid ").append("and a.effective=? ")
				.append("and d.id.tamenu.menuid = a.menuid ").append("and d.usepermission=? ")
				.append("and a.resourcetype=? ").append(" and a.securitypolicy<>?")
				.append("and d.id.taposition.positionid = e.positionid ")
				.append("and (d.effecttime is null or d.effecttime >= ?) ")
				.append(" and (d.auditstate=? or d.auditstate=?)");

		if (SysConfig.getSysconfigToBoolean("isPortal", false)) {
			hql.append(" and a.syspath=?");
			hql.append("order by a.sortno");
			return hibernateDao.createQuery(hql.toString(), new Object[] { "0", super.getSysDate(), "0", "1", "01", "2",
					super.getSysDate(), "0", "2", SysConfig.getSysConfig("curSyspathId", "sysmg") }).list();
		}

		hql.append("order by a.sortno");
		return hibernateDao
				.createQuery(hql.toString(),
						new Object[] { "0", super.getSysDate(), "0", "1", "01", "2", super.getSysDate(), "0", "2" })
				.list();
	}

	public List<IUser> queryUsersByYab139AndCategory(String yab139, String positionCategory) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct u from ").append(super.getEntityClassName(User.class.getName()))
				.append(" u,UserPosition up,").append(super.getEntityClassName(Position.class.getName())).append(" p")
				.append(" where p.taorg.yab139=?").append(" and p.positionid=up.id.taposition.positionid")
				.append(" and up.id.tauser.userid=u.userid").append(" and u.effective=?")
				.append(" and (u.destory is null or u.destory=?)");

		if ("01".equals(positionCategory)) {
			hql.append(" and p.positioncategory=?").append(" order by u.userid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, "0", "1", "01" }).list();
		}
		if ("02".equals(positionCategory)) {
			hql.append(" and p.positioncategory=?").append(" order by u.userid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, "0", "1", "02" }).list();
		}
		if ("03".equals(positionCategory)) {
			hql.append(" and p.positioncategory=?").append(" order by u.userid");

			return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, "0", "1", "03" }).list();
		}
		return hibernateDao.createQuery(hql.toString(), new Object[] { yab139, "0", "1" }).list();
	}
}
