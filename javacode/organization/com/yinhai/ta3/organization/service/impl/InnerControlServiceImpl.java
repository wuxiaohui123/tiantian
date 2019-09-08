package com.yinhai.ta3.organization.service.impl;

import java.sql.Timestamp;
import java.util.List;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IInnerControlService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class InnerControlServiceImpl extends OrgBaseService implements IInnerControlService {

	public List queryAdminByOrgId(Long orgid, String isShowSubOrg, Long positionid) {
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			if (ValidateUtil.isEmpty(orgid)) {
				return hibernateDao
						.createQuery(
								"from "
										+ super.getEntityClassName(Position.class.getName())
										+ " p where p.isadmin=? and p.effective=? and (p.validtime is null or p.validtime>=?) and p.positionid<>? order by p.orgidpath",
								new Object[] { "1", "0", super.getSysDate(), IPosition.ADMIN_POSITIONID }).list();
			}
			if ("true".equals(isShowSubOrg)) {
				return hibernateDao
						.createQuery(
								"select distinct p from "
										+ super.getEntityClassName(Position.class.getName())
										+ " p,"
										+ super.getEntityClassName(Org.class.getName())
										+ " o where o.orgid=? and p.orgidpath like o.orgidpath||'%' and p.taorg.effective=? and (p.taorg.destory is null or p.taorg.destory=?) and p.effective=? and (p.validtime is null or p.validtime>=?) and p.positionid<>? and  p.isadmin=?  order by o.orgidpath",
								new Object[] { orgid, "0", "1", "0", super.getSysDate(), IPosition.ADMIN_POSITIONID,
										"1" }).list();
			}
			return hibernateDao
					.createQuery(
							"select distinct p from "
									+ super.getEntityClassName(Position.class.getName())
									+ " p,"
									+ super.getEntityClassName(Org.class.getName())
									+ " o where o.orgid=? and p.taorg.orgid=o.orgid and p.effective=? and (p.validtime is null or p.validtime>=?) and p.positionid<>? and p.isadmin=? order by o.orgidpath",
							new Object[] { orgid, "0", super.getSysDate(), IPosition.ADMIN_POSITIONID, "1" }).list();
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p");
		if (ValidateUtil.isEmpty(orgid)) {
			hql.append(" where p.taorg.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" and p.isadmin=?").append(" and p.taorg.effective=?")
					.append(" and (p.taorg.destory is null or p.taorg.destory=?)").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.positionid not in(?,?)")
					.append(" order by p.orgidpath");

			return hibernateDao.createQuery(
					hql.toString(),
					new Object[] { positionid, "1", "0", "1", "0", super.getSysDate(), IPosition.ADMIN_POSITIONID,
							positionid }).list();
		}
		if ("true".equals(isShowSubOrg)) {
			hql.append(",").append(super.getEntityClassName(Org.class.getName())).append(" o")
					.append(" where o.orgid=?").append(" and p.orgidpath like o.orgidpath||'%'")
					.append(" and p.taorg.orgid in ( select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" and p.taorg.effective=?").append(" and (p.taorg.destory is null or p.taorg.destory=?)")
					.append(" and p.effective=?").append(" and (p.validtime is null or p.validtime>=?)")
					.append(" and p.isadmin=?").append(" and p.positionid not in(?,?)").append(" order by p.orgidpath");

			return hibernateDao.createQuery(
					hql.toString(),
					new Object[] { orgid, positionid, "0", "1", "0", super.getSysDate(), "1",
							IPosition.ADMIN_POSITIONID, positionid }).list();
		}
		hql.append(",").append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where o.orgid=?")
				.append(" and p.taorg.orgid=o.orgid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.isadmin=?")
				.append(" and p.positionid not in(?,?)").append(" order by p.orgidpath");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { orgid, "0", super.getSysDate(), "1", IPosition.ADMIN_POSITIONID, positionid }).list();
	}

	public List queryPositionByOrgId(Long orgid, String isShowSubOrg, Long positionid) {
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			if (ValidateUtil.isEmpty(orgid)) {
				return hibernateDao
						.createQuery(
								"from "
										+ super.getEntityClassName(Position.class.getName())
										+ " p where p.effective=? and p.positiontype=? and (p.validtime is null or p.validtime>=?) and p.positionid<>? order by p.orgidpath",
								new Object[] { "0", "1", super.getSysDate(), IPosition.ADMIN_POSITIONID }).list();
			}
			if ("true".equals(isShowSubOrg)) {
				return hibernateDao
						.createQuery(
								"select distinct p from "
										+ super.getEntityClassName(Position.class.getName())
										+ " p,"
										+ super.getEntityClassName(Org.class.getName())
										+ " o where o.orgid=? and p.orgidpath like o.orgidpath||'%' and p.effective=? and p.positiontype=? and p.taorg.effective=? and (p.taorg.destory is null or p.taorg.destory=?) and (p.validtime is null or p.validtime>=?) and p.positionid<>?  order by p.orgidpath",
								new Object[] { orgid, "0", "1", "0", "1", super.getSysDate(),
										IPosition.ADMIN_POSITIONID }).list();
			}
			return hibernateDao
					.createQuery(
							"select distinct p from "
									+ super.getEntityClassName(Position.class.getName())
									+ " p,"
									+ super.getEntityClassName(Org.class.getName())
									+ " o where o.orgid=? and p.taorg.orgid=o.orgid and p.effective=? and p.positiontype=? and (p.validtime is null or p.validtime>=?) and p.positionid<>? order by p.orgidpath",
							new Object[] { orgid, "0", "1", super.getSysDate(), IPosition.ADMIN_POSITIONID }).list();
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p");
		if (ValidateUtil.isEmpty(orgid)) {
			hql.append(" where 1=1 and p.taorg.orgid in(")
					.append("select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" and p.taorg.effective=?").append(" and (p.taorg.destory is null or p.taorg.destory=?)")
					.append(" and p.effective=?").append(" and p.positiontype=?")
					.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.positionid<>?")
					.append(" order by p.orgidpath");

			return hibernateDao.createQuery(hql.toString(),
					new Object[] { positionid, "0", "1", "0", "1", super.getSysDate(), IPosition.ADMIN_POSITIONID })
					.list();
		}
		if ("true".equals(isShowSubOrg)) {
			hql.append(",").append(super.getEntityClassName(Org.class.getName())).append(" o")
					.append(" where 1=1 and o.orgid=?").append(" and p.orgidpath like o.orgidpath||'%'")
					.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" and p.taorg.effective=?").append(" and (p.taorg.destory is null or p.taorg.destory=?)")
					.append(" and p.effective=?").append(" and p.positiontype=?")
					.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.positionid<>?")
					.append(" order by p.orgidpath");

			return hibernateDao.createQuery(
					hql.toString(),
					new Object[] { orgid, positionid, "0", "1", "0", "1", super.getSysDate(),
							IPosition.ADMIN_POSITIONID }).list();
		}
		hql.append(",").append(super.getEntityClassName(Org.class.getName())).append(" o")
				.append(" where 1=1 and o.orgid=?").append(" and p.taorg.orgid=o.orgid").append(" and p.effective=?")
				.append(" and p.positiontype=?").append(" and (p.validtime is null or p.validtime>=?)")
				.append(" and p.positionid<>?").append(" order by p.orgidpath");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { orgid, "0", "1", super.getSysDate(), IPosition.ADMIN_POSITIONID }).list();
	}

	public List queryLogByAdmin(ParamDTO dto) {
		Long adminPositionId = dto.getAsLong("adminPositionId");
		Long positionId = dto.getAsLong("positionId");
		if (ValidateUtil.isEmpty(adminPositionId)) {
			throw new AppException("管理员不能为空");
		}
		if (ValidateUtil.isEmpty(positionId)) {
			throw new AppException("岗位不能为空");
		}
		Timestamp startDate = dto.getAsTimestamp("startDate");
		Timestamp endDate = dto.getAsTimestamp("endDate");
		if ((!ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate)))
			return hibernateDao
					.createQuery(
							"select new  com.yinhai.ta3.system.org.domain.InnerControlVO(o.orgnamepath,u.userid,u.name,u.sex,u.loginid,ool.optime) from OrgOpLog ool,"
									+ super.getEntityClassName(User.class.getName())
									+ " u,"
									+ super.getEntityClassName(Org.class.getName())
									+ " o  where 1=1 and ool.opposition=? and ool.changcontent=? and ool.optype=? and ool.opsubjekt=u.userid and (ool.optime >=? and ool.optime<=?)  and u.directorgid=o.orgid ",
							new Object[] { adminPositionId, String.valueOf(positionId), "08", startDate, endDate })
					.list();
		if ((!ValidateUtil.isEmpty(startDate)) && (ValidateUtil.isEmpty(endDate)))
			return hibernateDao
					.createQuery(
							"select new  com.yinhai.ta3.system.org.domain.InnerControlVO(o.orgnamepath,u.userid,u.name,u.sex,u.loginid,ool.optime) from OrgOpLog ool,"
									+ super.getEntityClassName(User.class.getName())
									+ " u,"
									+ super.getEntityClassName(Org.class.getName())
									+ " o  where 1=1 and ool.opposition=? and ool.changcontent=? and ool.optype=? and ool.opsubjekt=u.userid and ool.optime >=? and u.directorgid=o.orgid",
							new Object[] { adminPositionId, String.valueOf(positionId), "08", startDate }).list();
		if ((ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			return hibernateDao
					.createQuery(
							"select new  com.yinhai.ta3.system.org.domain.InnerControlVO(o.orgnamepath,u.userid,u.name,u.sex,u.loginid,ool.optime) from OrgOpLog ool,"
									+ super.getEntityClassName(User.class.getName())
									+ " u,"
									+ super.getEntityClassName(Org.class.getName())
									+ " o  where 1=1 and ool.opposition=? and ool.changcontent=? and ool.optype=? and ool.opsubjekt=u.userid and ool.optime<=?  and u.directorgid=o.orgid",
							new Object[] { adminPositionId, String.valueOf(positionId), "08", endDate }).list();
		}
		return hibernateDao
				.createQuery(
						"select new  com.yinhai.ta3.system.org.domain.InnerControlVO(o.orgnamepath,u.userid,u.name,u.sex,u.loginid,ool.optime) from OrgOpLog ool,"
								+ super.getEntityClassName(User.class.getName())
								+ " u,"
								+ super.getEntityClassName(Org.class.getName())
								+ " o where 1=1 and ool.opposition=? and ool.changcontent=? and ool.optype=? and ool.opsubjekt=u.userid and u.directorgid=o.orgid",
						new Object[] { adminPositionId, String.valueOf(positionId), "08" }).list();
	}

	public List queryBusinessByOrgId(Long orgid, String isShowSubOrg, Long positionid) {
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			if (ValidateUtil.isEmpty(orgid)) {
				return hibernateDao
						.createQuery(
								"from "
										+ super.getEntityClassName(Position.class.getName())
										+ " p where 1=1 and p.positiontype=? and  p.effective=? and (p.validtime is null or p.validtime>=?) order by p.orgidpath",
								new Object[] { "2", "0", super.getSysDate() }).list();
			}
			if ("true".equals(isShowSubOrg)) {
				return hibernateDao
						.createQuery(
								"select distinct p from "
										+ super.getEntityClassName(Position.class.getName())
										+ " p,"
										+ super.getEntityClassName(Org.class.getName())
										+ " o where 1=1 and  o.orgid=? and p.orgidpath like o.orgidpath||'%' and p.taorg.effective=? and (p.taorg.destory is null or p.taorg.destory=?) and p.positiontype=? and p.effective=? and (p.validtime is null or p.validtime>=?) order by p.orgidpath",
								new Object[] { orgid, "0", "1", "2", "0", super.getSysDate() }).list();
			}
			return hibernateDao
					.createQuery(
							"select distinct p from "
									+ super.getEntityClassName(Position.class.getName())
									+ " p,"
									+ super.getEntityClassName(Org.class.getName())
									+ " o where 1=1 and  o.orgid=? and p.taorg.orgid=o.orgid and p.positiontype=? and p.effective=? and (p.validtime is null or p.validtime>=?) order by p.orgidpath",
							new Object[] { orgid, "2", "0", super.getSysDate() }).list();
		}

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p");
		if (ValidateUtil.isEmpty(orgid)) {
			hql.append(" where 1=1")
					.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" and p.taorg.effective=?").append(" and (p.taorg.destory is null or p.taorg.destory=?)")
					.append(" and p.positiontype=?").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.positionid<>?")
					.append("  order by p.orgidpath");

			return hibernateDao.createQuery(hql.toString(),
					new Object[] { positionid, "0", "1", "2", "0", super.getSysDate(), positionid }).list();
		}
		if ("true".equals(isShowSubOrg)) {
			hql.append(",").append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where 1=1")
					.append(" and o.orgid=?").append(" and p.orgidpath like o.orgidpath||'%'")
					.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
					.append(" and p.taorg.effective=?").append(" and (p.taorg.destory is null or p.taorg.destory=?)")
					.append(" and p.positiontype=?").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.positionid<>?")
					.append("  order by p.orgidpath");

			return hibernateDao.createQuery(hql.toString(),
					new Object[] { orgid, positionid, "0", "1", "2", "0", super.getSysDate(), positionid }).list();
		}
		hql.append(",").append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where 1=1")
				.append(" and o.orgid=?").append(" and p.taorg.orgid=o.orgid")
				.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)")
				.append(" and p.taorg.effective=?").append(" and (p.taorg.destory is null or p.taorg.destory=?)")
				.append(" and p.positiontype=?").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime>=?)").append(" and p.positionid<>?")
				.append("  order by p.orgidpath");

		return hibernateDao.createQuery(hql.toString(),
				new Object[] { orgid, positionid, "0", "1", "2", "0", super.getSysDate(), positionid }).list();
	}

	public List queryLogByBusiness(ParamDTO dto) {
		Long businessPositionId = dto.getAsLong("businessPositionId");
		if (ValidateUtil.isEmpty(businessPositionId)) {
			throw new AppException("业务人员不能为空");
		}
		Timestamp startDate = dto.getAsTimestamp("startDate");
		Timestamp endDate = dto.getAsTimestamp("endDate");
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select new  com.yinhai.ta3.system.org.domain.InnerControlVO(u.name,o.orgnamepath||'/'||u.name,p.orgnamepath||'/'||p.positionname,p.positionname,ool.optime) from OrgOpLog ool,UserPosition up,")
				.append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(User.class.getName())).append(" u,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o")
				.append(" where 1=1 and ool.opsubjekt=up.id.tauser.userid")
				.append(" and up.id.taposition.positionid=?").append(" and ool.optype=?")
				.append(" and ool.changcontent=p.positionid").append(" and ool.opuser=u.userid")
				.append(" and u.directorgid=o.orgid");

		if ((!ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			hql.append(" and ool.optime >= ? and ool.optime<=?");
			return hibernateDao.createQuery(hql.toString(),
					new Object[] { businessPositionId, "08", startDate, endDate }).list();
		}
		if ((!ValidateUtil.isEmpty(startDate)) && (ValidateUtil.isEmpty(endDate))) {
			hql.append(" and ool.optime >= ?");
			return hibernateDao.createQuery(hql.toString(), new Object[] { businessPositionId, "08", startDate })
					.list();
		}
		if ((ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			hql.append(" and ool.optime <= ?");
			return hibernateDao.createQuery(hql.toString(), new Object[] { businessPositionId, "08", endDate }).list();
		}
		return hibernateDao.createQuery(hql.toString(), new Object[] { businessPositionId, "08" }).list();
	}

	public List queryLogByMenu(ParamDTO dto) {
		Long menuId = dto.getAsLong("menuid");
		if (ValidateUtil.isEmpty(menuId)) {
			throw new AppException("功能菜单不能为空");
		}
		Timestamp startDate = dto.getAsTimestamp("startDate");
		Timestamp endDate = dto.getAsTimestamp("endDate");
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select new  com.yinhai.ta3.system.org.domain.InnerControlVO(u.name,p.orgnamepath||'/'||u.name,p.orgnamepath||'/'||p.positionname,p.positionname,ool.optime) from OrgOpLog ool,")
				.append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(User.class.getName())).append(" u").append(" where 1=1")
				.append(" and ool.optype=?").append(" and ool.opsubjekt=?").append(" and ool.opuser=u.userid")
				.append(" and ool.influencebody=p.positionid");

		if ((!ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			hql.append(" and ool.optime >= ? and ool.optime<=?").append(" order by p.orgidpath");

			return hibernateDao.createQuery(hql.toString(), new Object[] { "13", menuId, startDate, endDate }).list();
		}
		if ((!ValidateUtil.isEmpty(startDate)) && (ValidateUtil.isEmpty(endDate))) {
			hql.append(" and ool.optime >= ?").append(" order by p.orgidpath");

			return hibernateDao.createQuery(hql.toString(), new Object[] { "13", menuId, startDate }).list();
		}
		if ((ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			hql.append(" and ool.optime <= ?").append(" order by p.orgidpath");

			return hibernateDao.createQuery(hql.toString(), new Object[] { "13", menuId, endDate }).list();
		}
		hql.append(" order by p.orgidpath");
		return hibernateDao.createQuery(hql.toString(), new Object[] { "13", menuId }).list();
	}

	public Menu getMenu(Long menuId) {
		return (Menu) hibernateDao.getSession().get(Menu.class, menuId);
	}

	public List<Menu> getChildMenus(Long menuid) {
		if (!SysConfig.getSysconfigToBoolean("isPortal", false)) {
			return hibernateDao.createQuery(
					"from " + super.getEntityClassName(Menu.class.getName())
							+ " where pmenuid=? and syspath=? order by sortno",
					new Object[] { menuid, SysConfig.getSysConfig("curSyspathId", "sysmg") }).list();
		}
		return hibernateDao.createQuery(
				"from " + getEntityClassName(Menu.class.getName()) + " where pmenuid=? order by sortno",
				new Object[] { menuid }).list();
	}

}
