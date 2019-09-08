package com.yinhai.ta3.organization.api.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.PositionAuthrityId;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserPosition;
import com.yinhai.ta3.system.org.domain.UserPositionId;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class GrantServiceImpl implements IGrantService {

	private SimpleDao hibernateDao;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	private String getEntityClassName(String className) {
		return SysConfig.getSysConfig(className, className);
	}

	@Override
	public UserPositionId grantPositionToUser(Long userid, Long positionid, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(userid, "用户id不能为空");
		UserPosition up = new UserPosition();
		UserPositionId id = new UserPositionId();
		Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), positionid);
		User u = (User) hibernateDao.getSession().get(getEntityClassName(User.class.getName()), userid);
		id.setTaposition(p);
		id.setTauser(u);
		up.setId(id);
		up.setCreatetime(operateTime);
		up.setCreateuser(operator);
		up.setMainposition("0");
		UserPositionId newid = (UserPositionId) hibernateDao.save(up);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("positionid", positionid);
		map.put("userid", userid);
		TaEventPublisher.publishEvent(new EventSource(map), "position_user");
		return newid;
	}

	@Override
	public boolean retrievePositionFromUser(Long userid, Long positionid, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(userid, "用户id不能为空");
		UserPosition up = (UserPosition) hibernateDao.createQuery(
				"from UserPosition up where up.id.taposition.positionid=? and up.id.tauser.userid=?", positionid, userid)
				.uniqueResult();
		if ("1".equals(up.getMainposition())) {
			UserPosition up1 = (UserPosition) hibernateDao
					.createQuery(
							"select up from "
									+ getEntityClassName(Position.class.getName())
									+ " p,UserPosition up where up.id.tauser.userid=? and up.id.taposition.positionid=p.positionid and p.taorg.orgid=up.id.tauser.directorgid and p.positiontype=?",
							userid, "2").uniqueResult();
			if (up1 != null) {
				up1.setMainposition("1");
				hibernateDao.update(up1);
			}
		}
		hibernateDao.delete(up);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("positionid", positionid);
		map.put("userid", userid);
		TaEventPublisher.publishEvent(new EventSource(map), "position_user");
		return false;
	}

	public boolean grantUserFunctionUsePermission(Long userid, Long orgid, Long permissionId, Long operator, Date operateTime) {
		Long positionid = (Long) hibernateDao
				.createQuery(
						"select p.positionid from "
								+ getEntityClassName(Position.class.getName())
								+ " p,UserPosition up where p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=? and p.taorg.orgid=?",
						"2", userid, orgid).uniqueResult();
		return grantPositionFunctionUsePermission(positionid, permissionId, operator, operateTime);
	}

	public boolean retrieveUserFunctionUsePermission(Long userid, Long orgid, Long permissionId, Long operator, Date operateTime) {
		Long positionid = (Long) hibernateDao
				.createQuery(
						"select p.positionid from "
								+ getEntityClassName(Position.class.getName())
								+ " p,UserPosition up where p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=? and p.taorg.orgid=?",
						new Object[] { "2", userid, orgid }).uniqueResult();
		return retrievePositionFunctionUsePermission(positionid, permissionId, operator, operateTime);
	}

	public boolean grantUserFunctionAuthrityPermission(Long userid, Long orgid, Long permissionId, boolean reAuthrity, Long operator, Date operateTime) {
		Long positionid = (Long) hibernateDao
				.createQuery(
						"select p.positionid from "
								+ getEntityClassName(Position.class.getName())
								+ " p,UserPosition up where p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=? and p.taorg.orgid=?",
						new Object[] { "2", userid, orgid }).uniqueResult();
		return grantPositionFunctionAuthrityPermission(positionid, permissionId, reAuthrity, operator, operateTime);
	}

	public boolean retrieveUserFunctionAuthtiryPermission(Long userid, Long orgid, Long permissionId, Long operator, Date operateTime) {
		Long positionid = (Long) hibernateDao
				.createQuery(
						"select p.positionid from "
								+ getEntityClassName(Position.class.getName())
								+ " p,UserPosition up where p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=? and p.taorg.orgid=?",
						new Object[] { "2", userid, orgid }).uniqueResult();
		return retrievePositionFunctionAuthtiryPermission(positionid, permissionId, operator, operateTime);
	}

	public boolean retrieveUserFunctionReAuthtiryPermission(Long userid, Long orgid, Long permissionId, Long operator, Date operateTime) {
		Long positionid = (Long) hibernateDao
				.createQuery(
						"select p.positionid from "
								+ getEntityClassName(Position.class.getName())
								+ " p,UserPosition up where p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=? and p.taorg.orgid=?",
						new Object[] { "2", userid, orgid }).uniqueResult();
		return retrievePositionFunctionReAuthtiryPermission(positionid, permissionId, operator, operateTime);
	}

	public boolean grantPositionFunctionUsePermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(permissionId, "资源id不能为空");
		PositionAuthrity pa = (PositionAuthrity) hibernateDao.createQuery(
				"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?", new Object[] { positionid, permissionId })
				.uniqueResult();
		if (pa != null) {
			hibernateDao.createQuery(
					"update PositionAuthrity pa set pa.usepermission=? where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?",
					new Object[] { "1", positionid, permissionId }).executeUpdate();
		} else {
			pa = new PositionAuthrity();
			Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), positionid);
			Menu tamenu = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class.getName()), permissionId);
			PositionAuthrityId id = new PositionAuthrityId();
			id.setTamenu(tamenu);
			id.setTaposition(p);
			pa.setId(id);
			pa.setCreatetime(operateTime);
			pa.setCreateuser(operator);
			pa.setUsepermission("1");
			if ("1".equals(tamenu.getIsaudite())) {
				pa.setAuditstate("0");
				pa.setAuditeaccessdate(operateTime);
				pa.setAuditeuser(operator);
			} else if ("0".equals(tamenu.getIsaudite())) {
				pa.setAuditstate("1");
			}
			hibernateDao.save(pa);
		}
		return false;
	}

	public boolean retrievePositionFunctionUsePermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(permissionId, "资源id不能为空");
		PositionAuthrity pa = (PositionAuthrity) hibernateDao.createQuery(
				"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?", new Object[] { positionid, permissionId })
				.uniqueResult();

		if (!ValidateUtil.isEmpty(pa)) {
			if ((ValidateUtil.isEmpty(pa.getRepermission())) || ("0".equals(pa.getRepermission()))) {
				hibernateDao.delete(pa);
			} else {
				pa.setUsepermission("0");
				hibernateDao.update(pa);
			}
		}
		return false;
	}

	public boolean grantPositionFunctionAuthrityPermission(Long positionid, Long permissionId, boolean reAuthrity, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(permissionId, "资源id不能为空");
		PositionAuthrity pa = (PositionAuthrity) hibernateDao.createQuery(
				"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?", new Object[] { positionid, permissionId })
				.uniqueResult();
		boolean flag = false;
		if (pa != null) {
			String hql = "";
			int executeUpdate = 0;
			if (reAuthrity) {
				hql = "update PositionAuthrity pa set pa.repermission=?,pa.reauthrity=? where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?";
				executeUpdate = hibernateDao.createQuery(hql, new Object[] { "1", "1", positionid, permissionId }).executeUpdate();
			} else {
				hql = "update PositionAuthrity pa set pa.repermission=? where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?";
				executeUpdate = hibernateDao.createQuery(hql, new Object[] { "1", positionid, permissionId }).executeUpdate();
			}

			flag = executeUpdate == 1;
		} else {
			pa = new PositionAuthrity();
			Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), positionid);
			Menu tamenu = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class.getName()), permissionId);
			PositionAuthrityId id = new PositionAuthrityId();
			id.setTamenu(tamenu);
			id.setTaposition(p);
			pa.setId(id);
			pa.setCreatetime(operateTime);
			pa.setCreateuser(operator);
			pa.setRepermission("1");
			if (reAuthrity) {
				pa.setReauthrity("1");
			}
			if ("1".equals(tamenu.getIsaudite())) {
				pa.setAuditstate("0");
				pa.setAuditeaccessdate(operateTime);
				pa.setAuditeuser(operator);
			} else if ("0".equals(tamenu.getIsaudite())) {
				pa.setAuditstate("1");
			}
			Serializable save = hibernateDao.save(pa);
			flag = save != null;
		}
		return flag;
	}

	public boolean retrievePositionFunctionAuthtiryPermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(permissionId, "资源id不能为空");
		PositionAuthrity pa = (PositionAuthrity) hibernateDao.createQuery(
				"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?", new Object[] { positionid, permissionId })
				.uniqueResult();

		pa.setRepermission("0");
		hibernateDao.update(pa);

		return false;
	}

	public boolean retrievePositionFunctionReAuthtiryPermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(permissionId, "资源id不能为空");
		PositionAuthrity pa = (PositionAuthrity) hibernateDao.createQuery(
				"from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?", new Object[] { positionid, permissionId })
				.uniqueResult();
		pa.setReauthrity("0");

		if (((ValidateUtil.isEmpty(pa.getUsepermission())) || ("0".equals(pa.getUsepermission())))
				&& ((ValidateUtil.isEmpty(pa.getRepermission())) || ("0".equals(pa.getRepermission())))) {
			hibernateDao.delete(pa);
		} else {
			hibernateDao.update(pa);
		}
		return false;
	}

	public boolean grantUserFunctionUsePermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		grantPositionFunctionUsePermission(positionid, permissionId, operator, operateTime);
		return false;
	}

	public boolean retrieveUserFunctionUsePermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		retrievePositionFunctionUsePermission(positionid, permissionId, operator, operateTime);
		return false;
	}

	public boolean grantUserFunctionAuthrityPermission(Long positionid, Long permissionId, boolean reAuthrity, Long operator, Date operateTime) {
		grantPositionFunctionAuthrityPermission(positionid, permissionId, reAuthrity, operator, operateTime);
		return false;
	}

	public boolean retrieveUserFunctionAuthtiryPermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		retrievePositionFunctionAuthtiryPermission(positionid, permissionId, operator, operateTime);
		return false;
	}

	public boolean retrieveUserFunctionReAuthtiryPermission(Long positionid, Long permissionId, Long operator, Date operateTime) {
		retrievePositionFunctionReAuthtiryPermission(positionid, permissionId, operator, operateTime);
		return false;
	}

	public boolean permissionChangeUniteFunction(PermissionInfoVO vo) {
		if ("3".equals(vo.getPermissiontype())) {
			if ("0".equals(vo.getOpertype())) {
				retrievePositionFunctionUsePermission(vo.getPositionid(), vo.getPermissionid(), vo.getOperator(), vo.getOperateTime());
			}
			if ("1".equals(vo.getOpertype())) {
				grantPositionFunctionUsePermission(vo.getPositionid(), vo.getPermissionid(), vo.getOperator(), vo.getOperateTime());
			}
		}
		if ("4".equals(vo.getPermissiontype())) {
			if ("0".equals(vo.getOpertype())) {
				retrievePositionFunctionAuthtiryPermission(vo.getPositionid(), vo.getPermissionid(), vo.getOperator(), vo.getOperateTime());
			}
			if ("1".equals(vo.getOpertype())) {
				grantPositionFunctionAuthrityPermission(vo.getPositionid(), vo.getPermissionid(), false, vo.getOperator(), vo.getOperateTime());
			}
		}
		if ("5".equals(vo.getPermissiontype())) {
			if ("0".equals(vo.getOpertype())) {
				retrievePositionFunctionReAuthtiryPermission(vo.getPositionid(), vo.getPermissionid(), vo.getOperator(), vo.getOperateTime());
			}
			if ("1".equals(vo.getOpertype())) {
				grantPositionFunctionAuthrityPermission(vo.getPositionid(), vo.getPermissionid(), true, vo.getOperator(), vo.getOperateTime());
			}
		}
		if ("6".equals(vo.getPermissiontype())) {
			hibernateDao.createQuery("delete from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?",
					new Object[] { vo.getPositionid(), vo.getPermissionid() }).executeUpdate();
		}
		if ("7".equals(vo.getPermissiontype())) {
			hibernateDao.createQuery("update PositionAuthrity pa set pa.effecttime=? where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?",
					new Object[] { vo.getEffectivetime(), vo.getPositionid(), vo.getPermissionid() }).executeUpdate();
		}
		TaEventPublisher.publishEvent(new EventSource(vo), "permission_change");
		return false;
	}

}
