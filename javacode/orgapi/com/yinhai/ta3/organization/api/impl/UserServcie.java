package com.yinhai.ta3.organization.api.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.util.Assert;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IPositionService;
import com.yinhai.ta3.organization.api.IUserService;
import com.yinhai.ta3.organization.api.UserAuthrityVO;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserPosition;
import com.yinhai.ta3.system.org.domain.UserPositionId;
@SuppressWarnings("unchecked")
public class UserServcie implements IUserService {

	public static String userClassName = SysConfig.getSysConfig(User.class.getName(), User.class.getName());
	public static String positionClassName = SysConfig.getSysConfig(Position.class.getName(), Position.class.getName());
	public static String orgClassName = SysConfig.getSysConfig(Org.class.getName(), Org.class.getName());

	private ITimeService timeService;

	private SimpleDao hibernateDao;

	private IPositionService positionService;

	public boolean batchChangeOrg(Long[] userids, Long neworgid, Long operator) {
		if (SysConfig.getSysconfigToBoolean("allowMultiOrg")) {
			return false;
		}
		Timestamp sysTimestamp = timeService.getSysTimestamp();
		Position position = null;
		for (Long userid : userids) {
			StringBuffer sHQ = new StringBuffer();
			sHQ.append("select p.positionid,u.username from UserPosition up, " + positionClassName + " p, " + userClassName + " u where 1=1")
					.append(" and u.userid=?").append(" and p.positiontype=?").append(" and up.id.tauser.userid=u.userid ")
					.append(" and up.id.taposition.positionid=p.positionid");

			Query createQuery = hibernateDao.createQuery(sHQ.toString(), new Object[] { userid, "2" });
			Object[] objects = (Object[]) createQuery.uniqueResult();
			positionService.unUsePosition((Long) objects[0], operator, sysTimestamp);

			position = new Position();
			position.setPositionname(String.valueOf(objects[1]));
			position.setPositiontype("2");
			position.setCreateuser(operator);
			position.setCreatetime(sysTimestamp);
			position.setEffective("0");

			positionService.createPosition(position, neworgid);

			UserPosition up = new UserPosition();
			UserPositionId upid = new UserPositionId();
			upid.setTaposition(position);
			User user = new User();
			user.setUserid(userid);
			upid.setTauser(user);
			up.setId(upid);
			hibernateDao.save(up);
		}

		return true;
	}

	public User createUser(User user, Long[] belongorgids, IPosition createPosition) {
		Assert.notEmpty(belongorgids, "至少一个组织");
		System.out.println(user.getName());
        System.out.println(user.getBirth());
		hibernateDao.save(user);
		Position position = null;

		for (int i = 0; i < belongorgids.length; i++) {
			try {
				position = (Position) Class.forName(positionClassName).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			position.setPositionname(user.getName());
			position.setPositiontype("2");
			position.setCreateuser(user.getCreateuser());
			position.setCreatetime(user.getCreatetime());
			position.setEffective("0");
			position.setCreatepositionid(createPosition.getPositionid());

			UserPosition up = new UserPosition();
			UserPositionId upid = new UserPositionId();
			if (i == 0) {
				up.setMainposition("1");
			} else {
				up.setMainposition("0");
			}
			up.setCreatetime(user.getCreatetime());
			up.setCreateuser(user.getUserid());
			upid.setTaposition(position);
			upid.setTauser(user);
			up.setId(upid);
			hibernateDao.save(up);
		}
		return user;
	}

	
	public List<Position> queryUserPositions(Long userid) {
		Assert.isNull(userid, "用户id不存在");
		Query createQuery = hibernateDao.createQuery("select p from " + positionClassName
				+ " p,UserPosition up where up.id.tauser.userid=? and up.id.taposition.positionid=p.positionid", new Object[] { userid });
		return createQuery.list();
	}

	public List<UserAuthrityVO> queryUserUserAuthrity(Long userid) {
		StringBuffer sHQ = new StringBuffer();
		sHQ.append("select new com.yinhai.ta3.organization.api.UserAuthrityVO(")
				.append("p.permissionid")
				.append(",p.permissionname")
				.append(",p.namemenupath")
				.append(",p.permissiontype")
				.append(",pa.userpermission")
				.append(",pa.repermission")
				.append(",pa.reauthrity")
				.append(",ps.positionid")
				.append(",ps.positionnamepath")
				.append(",u.createtime")
				.append(",u.createuser")
				.append(")")
				.append(" from UserPosition up, " + positionClassName + " ps, " + userClassName + " u, PositionAuthrity pa, " + positionClassName
						+ " p,PermissionSource pr where 1=1").append(" and u.userid=?").append(" and u.userid = up.id.tauser.userid")
				.append(" and up.id.taposition.positionid = ps.positionid").append(" and ps.effective=?")
				.append(" and ps.positionid = pa.id.taposition.positionid").append(" and pa.id.taperrmissionsource.permissionid = pr.permissionid");

		Query createQuery = hibernateDao.createQuery(sHQ.toString(), new Object[] { userid, "0" });
		return createQuery.list();
	}

	public boolean reUseUser(Long userid, Long operator, Date operateTime) {
		User user = (User) hibernateDao.getSession().get(User.class, userid);
		user.setEffective("0");
		hibernateDao.update(user);

		StringBuffer sHQ = new StringBuffer("select p.positionid from UserPosition up, " + positionClassName + " p where 1=1");
		sHQ.append(" and up.id.tauser.userid=?").append(" and p.positiontype=?").append(" and up.id.taposition.positionid=p.positionid");

		Query createQuery = hibernateDao.createQuery(sHQ.toString(), new Object[] { userid, "2" });
		List<Long> positionids = createQuery.list();
		for (Long positionid : positionids) {
			positionService.reUsePosition(positionid, operator, operateTime);
		}
		return false;
	}

	public boolean unUseUser(Long userid, Long operator, Date operateTime) {
		StringBuffer sHQ = new StringBuffer("select p.positionid from UserPosition up, " + positionClassName + " p where 1=1");
		sHQ.append(" and up.id.tauser.userid=?").append(" and p.positiontype=?").append(" and up.id.taposition.positionid=p.positionid");

		List<Long> positionids = hibernateDao.createQuery(sHQ.toString(), new Object[] { userid, "2" }).list();
		for (Long positionid : positionids) {
			positionService.unUsePosition(positionid, operator, operateTime);
		}

		User u = (User) hibernateDao.getSession().get(userClassName, userid);
		u.setEffective("1");
		hibernateDao.update(u);
		return true;
	}

	public boolean updateUser(User user, Long[] belongorg_ids, Long operator) {
		if (belongorg_ids.length < 1) {
			hibernateDao.update(user);
		}
		return true;
	}

	public PageBean queryUsers(User user, Long orgid, Long positionid, boolean isDisSubOrgs, int start, int limit) {
		StringBuffer sql = new StringBuffer("select distinct u from " + userClassName + " u," + positionClassName + " p, " + orgClassName
				+ " o, UserPosition up where 1=1");

		Field[] pField = user.getClass().getDeclaredFields();
		Arrays.stream(pField).forEach(field -> {
			if ("userid".equals(field.getName()) || "name".equals(field.getName()) || "loginid".equals(field.getName())){
				return;
			}
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), user.getClass());
				Object invoke = pd.getReadMethod().invoke(user);
				if (invoke != null && invoke instanceof String && !"-1".equals(invoke)) {
					sql.append(" and u.").append(field.getName()).append("='").append(invoke.toString().replace("'", "'")).append("'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});


		if (ValidateUtil.isNotEmpty(user.getLoginid())) {
			String[] logindis = user.getLoginid().replaceAll("，", ",").split(",");
			for (int i = 0; i < logindis.length; i++) {
				if (i == 0) {
					sql.append(" and (u.loginid='").append(logindis[i].replace("'", "'")).append("'");
				} else {
					sql.append(" or u.loginid='").append(logindis[i].replace("'", "'")).append("'");
				}
			}
			sql.append(")");
		}

		if (ValidateUtil.isNotEmpty(user.getName())) {
			sql.append(" and u.name like :name ");
		}

		sql.append(" and o.orgid=:orgid");
		sql.append(" and u.userid <> :userid");
		if (isDisSubOrgs) {
			sql.append(" and p.orgidpath like o.orgidpath||'%'");
		} else {
			sql.append(" and p.taorg.orgid = o.orgid");
		}
		if (!IPosition.ADMIN_POSITIONID.equals(positionid)) {
			sql.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=:positionid)");
		}

		sql.append(" and p.positionid = up.id.taposition.positionid").append(" and up.id.tauser.userid = u.userid")
				.append(" and p.positiontype =:positiontype").append(" and (u.destory is null or u.destory=:destory)")
				.append(" and u.userid<>:developerId").append(" and p.taorg.orgid = u.directorgid").append(" order by u.sort");

		Query usersQuery = hibernateDao.createQuery(sql.toString());
		usersQuery.setString("positiontype", "2");
		usersQuery.setString("destory", "1");
		if (!IPosition.ADMIN_POSITIONID.equals(positionid))
			usersQuery.setLong("positionid", positionid.longValue());
		usersQuery.setLong("orgid", orgid.longValue());
		usersQuery.setLong("userid", user.getUserid().longValue());
		usersQuery.setLong("developerId", IUser.ROOT_USERID.longValue());

		usersQuery.setFirstResult(start);
		usersQuery.setMaxResults(limit);

		String countSql = sql.toString().replaceAll("distinct u", "count(distinct u)").replaceAll("order by u.sort", "");
		Query countQuery = hibernateDao.createQuery(countSql);
		countQuery.setString("positiontype", "2");
		countQuery.setString("destory", "1");
		if (!IPosition.ADMIN_POSITIONID.equals(positionid))
			countQuery.setLong("positionid", positionid.longValue());
		countQuery.setLong("orgid", orgid.longValue());
		countQuery.setLong("userid", user.getUserid().longValue());
		countQuery.setLong("developerId", IUser.ROOT_USERID.longValue());

		if (ValidateUtil.isNotEmpty(user.getName())) {
			usersQuery.setString("name", "%" + user.getName() + "%");
			countQuery.setString("name", "%" + user.getName() + "%");
		}

		PageBean pg = new PageBean();
		pg.setStart(start);
		pg.setLimit(limit);
		pg.setTotal(1000);
		pg.setList(usersQuery.list());
		pg.setTotal(Integer.valueOf(((Long) countQuery.iterate().next()).intValue()));
		return pg;
	}

	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setPositionService(IPositionService positionService) {
		this.positionService = positionService;
	}

}
