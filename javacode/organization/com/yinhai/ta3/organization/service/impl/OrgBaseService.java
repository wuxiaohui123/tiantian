package com.yinhai.ta3.organization.service.impl;

import java.util.List;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.log.IIllegalOperationLog;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class OrgBaseService extends BaseService {

	protected SimpleDao hibernateDao;
	private IIllegalOperationLog illegalOperationLog;

	public void setIllegalOperationLog(IIllegalOperationLog illegalOperationLog) {
		this.illegalOperationLog = illegalOperationLog;
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	protected boolean checkAuthorityByUserOrPosition() {
		return SysConfig.getSysconfigToBoolean("checkAuthorityByUser", true);
	}

	protected String getEntityClassName(Class o) {
		return SysConfig.getSysConfig(o.getName(), o.getName());
	}

	protected void checkOrg(Long userid, Long positionid, Long id, String optype, String opobjecttype,
			String changcontent) {
		if (checkAuthorityByUserOrPosition()) {
			checkOrgByUser(userid, positionid, id, optype, opobjecttype, changcontent);
		} else
			checkOrgByPosition(userid, positionid, id, optype, opobjecttype, changcontent);
	}

	protected void checkMenu(Long userid, Long positionid, Long menuid, boolean isRePermission, String opttype,
			Long opPositionid) {
		if (checkAuthorityByUserOrPosition()) {
			checkMenuByUser(userid, positionid, menuid, isRePermission, opttype, opPositionid);
		} else {
			checkMenuByPosition(userid, positionid, menuid, isRePermission, opttype, opPositionid);
		}
	}

	protected void checkOrgByPosition(Long userid, Long positionid, Long id, String optype, String opobjecttype,
			String changcontent) {
		if (!IPosition.ADMIN_POSITIONID.equals(positionid)) {
			Object uniqueResult = null;
			Org o = null;
			if ("03".equals(opobjecttype)) {
				Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), id);
				uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
						new Object[] { positionid, p.getOrgid() }).uniqueResult();
				o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), p.getOrgid());
			} else if ("02".equals(opobjecttype)) {
				User u = (User) hibernateDao.getSession().get(getEntityClassName(User.class), id);
				uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
						new Object[] { positionid, u.getDirectorgid() }).uniqueResult();
				o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), u.getDirectorgid());
			} else if ("01".equals(opobjecttype)) {
				uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
						new Object[] { positionid, id }).uniqueResult();
				o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), id);
			}
			if (uniqueResult == null) {
				changcontent = changcontent + "(" + o.getOrgname() + ")";
				illegalOperationLog.saveIllegalOperationLog(getLongSeq(), userid, positionid, optype, opobjecttype, id,
						changcontent);
				throw new AppException("非法操作！");
			}
		}
	}

	protected void checkOrgByUser(Long userid, Long positionid, Long id, String optype, String opobjecttype,
			String changcontent) {
		if (!IUser.ROOT_USERID.equals(userid)) {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct om.id.orgid from OrgMg om,").append(getEntityClassName(User.class))
					.append(" u,UserPosition up,").append(getEntityClassName(Position.class)).append(" p")
					.append(" where u.userid=up.id.tauser.userid").append(" and u.userid=?")
					.append(" and up.id.taposition.positionid=p.positionid").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime>=?)")
					.append(" and p.positionid=om.id.positionid");

			List list = hibernateDao
					.createQuery(hql.toString(), new Object[] { userid, "0", timeService.getSysDate() }).list();
			boolean flag = false;
			Org o = null;
			if ("03".equals(opobjecttype)) {
				Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), id);
				flag = list.contains(p.getOrgid());
				o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), p.getOrgid());
			} else if ("02".equals(opobjecttype)) {
				User u = (User) hibernateDao.getSession().get(getEntityClassName(User.class), id);
				flag = list.contains(u.getDirectorgid());
				o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), u.getDirectorgid());
			} else if ("01".equals(opobjecttype)) {
				flag = list.contains(id);
				o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), id);
			}
			if (!flag) {
				changcontent = changcontent + "(" + o.getOrgname() + ")";
				illegalOperationLog.saveIllegalOperationLog(getLongSeq(), userid, positionid, optype, opobjecttype, id,
						changcontent);
				throw new AppException("非法操作！");
			}
		}
	}

	protected void checkMenuByPosition(Long userid, Long positionid, Long menuid, boolean isRePermission,
			String opttype, Long opPositionid) {
		if (!IPosition.ADMIN_POSITIONID.equals(positionid)) {
			PositionAuthrity pa = (PositionAuthrity) hibernateDao
					.createQuery(
							"select pa from "
									+ getEntityClassName(Position.class)
									+ " p,PositionAuthrity pa,"
									+ getEntityClassName(Menu.class)
									+ " m where p.positionid=? and p.positionid=pa.id.taposition.positionid and pa.id.tamenu.menuid=m.menuid and m.menuid=?",
							new Object[] { positionid, menuid }).uniqueResult();
			if (ValidateUtil.isEmpty(pa)) {
				illegalOperationLog.saveIllegalOperationMenuLog(getLongSeq(), userid, positionid, opttype, "04",
						menuid, opPositionid);
				throw new AppException("非法操作！");
			}
			if (isRePermission) {
				if (!"1".equals(pa.getRepermission())) {
					illegalOperationLog.saveIllegalOperationMenuLog(getLongSeq(), userid, positionid, opttype, "04",
							menuid, opPositionid);
					throw new AppException("非法操作！");
				}
			} else if (!"1".equals(pa.getReauthrity())) {
				illegalOperationLog.saveIllegalOperationMenuLog(getLongSeq(), userid, positionid, opttype, "04",
						menuid, opPositionid);
				throw new AppException("非法操作！");
			}
		}
	}

	protected void checkMenuByUser(Long userid, Long positionid, Long menuid, boolean isRePermission, String opttype,
			Long opPositionid) {
		if (!IUser.ROOT_USERID.equals(userid)) {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct m.menuid from ").append(getEntityClassName(Menu.class)).append(" m,")
					.append("PositionAuthrity pa,").append(getEntityClassName(User.class))
					.append(" u,UserPosition up,").append(getEntityClassName(Position.class)).append(" p")
					.append(" where u.userid=up.id.tauser.userid").append(" and u.userid=?")
					.append(" and up.id.taposition.positionid=p.positionid").append(" and p.effective=?")
					.append(" and (p.validtime is null or p.validtime>=?)")
					.append(" and p.positionid=pa.id.taposition.positionid")
					.append(" and pa.id.tamenu.menuid=m.menuid");

			List list = null;
			if (isRePermission) {
				hql.append(" and pa.repermission=?");
				list = hibernateDao.createQuery(hql.toString(),
						new Object[] { userid, "0", timeService.getSysDate(), "1" }).list();
			} else {
				hql.append(" and pa.reauthrity=?");
				list = hibernateDao.createQuery(hql.toString(),
						new Object[] { userid, "0", timeService.getSysDate(), "1" }).list();
			}
			boolean flag = list.contains(menuid);
			if (!flag) {
				illegalOperationLog.saveIllegalOperationMenuLog(getLongSeq(), userid, positionid, opttype, "04",
						menuid, opPositionid);
				throw new AppException("非法操作！");
			}
		}
	}
}
