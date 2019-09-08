package com.yinhai.ta3.organization.service.impl;

import java.sql.Timestamp;
import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.organization.api.IPositionService;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IDelegatePositionService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.UserPosition;
import com.yinhai.ta3.system.org.domain.UserPositionId;

public class DelegatePositionServiceImpl extends OrgBaseService implements IDelegatePositionService {

	private IGrantService grantService;
	private IPositionService positionService;
	private OrganizationEntityService organizationEntityService;

	public void setGrantService(IGrantService grantService) {
		this.grantService = grantService;
	}

	public void setPositionService(IPositionService positionService) {
		this.positionService = positionService;
	}

	public List<UserInfoVO> queryScropOrgUsers(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空，不能委派岗位");
		}
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select distinct new com.zhongzhi.ta3.system.org.domain.UserInfoVO(o.orgnamepath,u.userid,u.name,u.sex,u.loginid) from ")
				.append(getEntityClassName(User.class))
				.append(" u,")
				.append(getEntityClassName(Org.class))
				.append(" o")
				.append(" where o.effective=?")
				.append(" and (o.destory is null or o.destory=?)")
				.append(" and u.effective=?")
				.append(" and (u.destory is null or u.destory=?)")
				.append(" and o.orgid=u.directorgid")
				.append(" and u.userid not in(select up.id.tauser.userid from UserPosition up,"
						+ getEntityClassName(Position.class)
						+ " p where up.id.taposition.positionid=? or (up.id.taposition.positionid=p.positionid and p.createpositionid=? and p.positiontype=?))");

		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			return hibernateDao.createQuery(hql.toString(),
					new Object[] { "0", "1", "0", "1", positionid, positionid, "3" }).list();
		}
		hql.append(" and o.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?)");
		return hibernateDao.createQuery(hql.toString(),
				new Object[] { "0", "1", "0", "1", positionid, positionid, "3", positionid }).list();
	}

	public void deletegatePosition(List<Key> ids, ParamDTO dto) {
		Long userid = dto.getAsLong("userid");
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("被委派人员为空，不能进行委派");
		}
		Position curP = (Position) dto.getUserInfo().getNowPosition();
		User u = (User) organizationEntityService.getUserByUserId(userid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), u.getUserid(),
				"05", "02", "委派岗位，无该人员所在组织的操作权限");

		Position p = (Position) dto.toDomainObject(getEntityClassName(Position.class));
		p.setCreatepositionid(curP.getPositionid());
		p.setCreatetime(getSysTimestamp());
		p.setCreateuser(dto.getUserInfo().getUserid());
		p.setEffective("0");
		p.setPositionname(u.getName());
		p.setPositiontype("3");
		p.setValidtime(dto.getAsTimestamp("deletegateTime"));
		Position newP = positionService.createPosition(p, u.getDirectorgid());

		UserPosition up = new UserPosition();
		UserPositionId upid = new UserPositionId();
		up.setMainposition("0");
		up.setCreatetime(getSysDate());
		up.setCreateuser(dto.getUserInfo().getUserid());
		upid.setTaposition(newP);
		upid.setTauser(u);
		up.setId(upid);
		hibernateDao.save(up);

		for (Key key : ids) {
			String isyab003 = key.getAsString("isyab003");
			if (!ValidateUtil.isNotEmpty(isyab003)) {

				Long menuid = key.getAsLong("id");

				checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), menuid,
						true, "13", newP.getPositionid());
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, newP.getPositionid(), dto
						.getUserInfo().getUserid(), timeService.getSysTimestamp(), "1", "3"));
			}
		}
	}

	public List<Position> queryDelegateeUsers(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空，不能委派岗位");
		}
		return hibernateDao.createQuery(
				"from " + getEntityClassName(Position.class)
						+ " p where p.createpositionid=? and p.positiontype=? order by p.positionid",
				new Object[] { positionid, "3" }).list();
	}

	public void recycleDeletegatePosition(List<Key> positionids, IUser opUser) {
		if (ValidateUtil.isEmpty(positionids)) {
			throw new AppException("委派岗位为空，不能删除");
		}
		for (Key key : positionids) {
			Long positionid = key.getAsLong("positionid");
			IPosition p = organizationEntityService.getPositionByPositionId(positionid);

			checkOrg(opUser.getUserid(), opUser.getNowPosition().getPositionid(), p.getPositionid(), "25", "03",
					"回收委派岗位，无该岗位所在组织的操作权限");

			hibernateDao.createQuery("delete from UserPosition up where up.id.taposition.positionid=?",
					new Object[] { positionid }).executeUpdate();
			List<PositionAuthrity> list = hibernateDao.createQuery(
					"from PositionAuthrity pa where pa.id.taposition.positionid=?", new Object[] { positionid }).list();
			for (PositionAuthrity pa : list) {
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(),
						positionid, opUser.getUserid(), timeService.getSysTimestamp(), "0", "6"));
			}

			hibernateDao.createQuery("delete from " + getEntityClassName(Position.class) + " p where p.positionid=?",
					new Object[] { positionid }).executeUpdate();
		}
	}

	public void updateDeletegatePositionPermissions(List<Key> ids, ParamDTO dto) {
		Long positionid = dto.getAsLong("positionid");
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("委派人员为空，不能修改权限！");
		}
		Position p = (Position) organizationEntityService.getPositionByPositionId(positionid);

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), p.getPositionid(),
				"11", "03", "更新委派岗位，无该岗位所在组织的操作权限");
		Timestamp validTime = dto.getAsTimestamp("validtime");
		if ((!ValidateUtil.isEmpty(validTime)) && (!validTime.toString().equals(p.getValidtime().toString()))) {
			p.setValidtime(validTime);
			hibernateDao.update(p);
		}
		Long userid = dto.getUserInfo().getUserid();
		Long cuserid = (Long) hibernateDao.createQuery(
				"select up.id.tauser.userid from UserPosition up where up.id.taposition.positionid=?",
				new Object[] { positionid }).uniqueResult();
		for (int i = 0; i < ids.size(); i++) {
			Key key = (Key) ids.get(i);
			Long permissionid = key.getAsLong("id");
			String flag = key.getAsString("checked");
			String isyab003 = key.getAsString("isyab003");
			if ("false".equals(flag)) {
				if (!ValidateUtil.isNotEmpty(isyab003)) {

					checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(),
							permissionid, true, "14", positionid);
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid,
							timeService.getSysTimestamp(), "0", "3"));
				}
			} else if (("true".equals(flag)) && (!ValidateUtil.isNotEmpty(isyab003))) {

				checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(),
						permissionid, true, "13", positionid);
				grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid,
						timeService.getSysTimestamp(), "1", "3"));
			}
		}
	}

	public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
		this.organizationEntityService = organizationEntityService;
	}

}
