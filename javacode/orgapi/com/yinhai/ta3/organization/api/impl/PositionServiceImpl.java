package com.yinhai.ta3.organization.api.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.util.Assert;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.Finder;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IPositionService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;

public class PositionServiceImpl implements IPositionService {
	private SimpleDao hibernateDao;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	private String getEntityClassName(String className) {
		return SysConfig.getSysConfig(className, className);
	}

	@SuppressWarnings("unchecked")
	public PageBean queryPositions(Position conditionPosition, Long nowPosition, Long orgid, boolean isDisSubOrgs, int start, int limit) {
		StringBuffer sb = new StringBuffer();
		if (nowPosition.equals(IPosition.ADMIN_POSITIONID)) {
			sb.append("from " + getEntityClassName(Position.class.getName()) + " p where p.positionid <>").append(nowPosition);
		} else {
			sb.append("from " + getEntityClassName(Position.class.getName()) + " p where p.positionid not in(").append(nowPosition).append(",")
					.append(IPosition.ADMIN_POSITIONID).append(")");
			sb.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=").append(nowPosition).append(")");
		}
		if (orgid != null) {
			if (isDisSubOrgs) {
				Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), orgid);
				if (org != null) {
					sb.append(" and p.orgidpath like '").append(org.getOrgidpath()).append("%'").append(" and p.taorg.effective=").append("0");
				}

			} else {
				sb.append(" and p.taorg.orgid=").append(orgid).append(" and p.taorg.effective=").append("0");
			}
		}

		String positionname = conditionPosition.getPositionname();
		if (ValidateUtil.isNotEmpty(positionname)) {
			sb.append(" and p.positionname like :positionname");
		}

		String effective = conditionPosition.getEffective();
		if ((ValidateUtil.isNotEmpty(effective)) && (!"-1".equals(effective))) {
			sb.append(" and p.effective=").append(effective);
		}
		sb.append(" and p.positiontype=").append("1");

		Field[] pField = conditionPosition.getClass().getDeclaredFields();
		for (int i = 0; i < pField.length; i++) {
			String fieldName = pField[i].getName();
			if ((!"positionname".equals(fieldName)) && (!"orgid".equals(fieldName)) && (!"effective".equals(fieldName))) {
				try {
					PropertyDescriptor pd = new PropertyDescriptor(pField[i].getName(), conditionPosition.getClass());
					Object invoke = pd.getReadMethod().invoke(conditionPosition, new Object[0]);
					if ((invoke != null) && ((invoke instanceof String)) && (!"-1".equals(invoke))) {
						sb.append(" and p.").append(fieldName).append("='").append(invoke.toString().replace("'", "'")).append("'");
					}

					if ((invoke != null) && ((invoke instanceof Long))) {
						sb.append(" and p.").append(fieldName).append("=").append(invoke);
					}
				} catch (Exception e) {
				}
			}
		}

		sb.append(" and (p.iscopy is null or p.iscopy= ").append("0").append(")");
		sb.append(" order by p.orgidpath");
		Query query = hibernateDao.createQuery(sb.toString(), new Object[0]).setFirstResult(start).setMaxResults(limit);
		PageBean pb = new PageBean();
		pb.setStart(Integer.valueOf(start));
		pb.setLimit(Integer.valueOf(limit));
		Finder finder = Finder.create(sb.toString());
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

	public Position createPosition(Position position, Long belongorgid) {
		Assert.notNull(position, "岗位不能为空");
		Assert.notNull(belongorgid, "部门id不能为空");
		Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), belongorgid);
		position.setOrgidpath(org.getOrgidpath());
		position.setOrgnamepath(org.getOrgnamepath());
		position.setTaorg(org);
		Long id = (Long) hibernateDao.save(position);
		return (Position) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), id);
	}

	public boolean updatePosition(Position position, Long operator) {
		Assert.notNull(position, "岗位不能为空");
		Assert.notNull(operator, "operator入参不能为空");
		hibernateDao.update(position);
		return false;
	}

	public boolean unUsePosition(Long positionid, Long operator, Date operatorTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(operator, "operator入参不能为空");
		Assert.notNull(operatorTime, "operatorTime入参不能为空");
		int executeUpdate = hibernateDao.createQuery(
				"update " + getEntityClassName(Position.class.getName()) + " p set p.effective=? where p.positionid=?",
				new Object[] { "1", positionid }).executeUpdate();
		return executeUpdate == 1;
	}

	public boolean reUsePosition(Long positionid, Long operator, Date operatorTime) {
		Assert.notNull(positionid, "岗位id不能为空");
		Assert.notNull(operator, "operator入参不能为空");
		Assert.notNull(operatorTime, "operatorTime入参不能为空");
		int executeUpdate = hibernateDao.createQuery(
				"update " + getEntityClassName(Position.class.getName()) + " p set p.effective=? where p.positionid=?",
				new Object[] { "0", positionid }).executeUpdate();
		return executeUpdate == 1;
	}

}
