package com.yinhai.ta3.organization.api.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.util.Assert;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.ta3.organization.api.IOrgService;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.system.org.domain.Org;

public class OrgServce implements IOrgService {

	private SimpleDao hibernateDao;
	private OrganizationEntityService organizationEntityService;

	public boolean ascSortOrg(List<Long> orgids, Long operator) {
		Assert.notNull(orgids, "org入参不能为空");
		Assert.notNull(operator, "operator入参不能为空");
		for (int i = 0; i < orgids.size(); i++) {
			hibernateDao.createQuery(
					"update " + SysConfig.getSysConfig(Org.class.getName(), Org.class.getName()) + " o set o.sort=? where o.orgid=?",
					new Object[] { Integer.valueOf(i), orgids.get(i) }).executeUpdate();
		}
		return true;
	}

	public Org createOrg(Org org, Long porgid) {
		Assert.notNull(org, "org入参不能为空");
		Assert.notNull(porgid, "porgid入参不能为空");

		Org po = (Org) organizationEntityService.getDepart(porgid);
		org.setOrgnamepath(po.getOrgnamepath() + "/" + org.getOrgname());

		Query maxSortNoHQ = hibernateDao.createQuery("select max(o.sort) from " + SysConfig.getSysConfig(Org.class.getName(), Org.class.getName())
				+ " o where o.pOrg.orgid=?", new Object[] { porgid });
		Object maxSortNo = maxSortNoHQ.uniqueResult();
		if (maxSortNo != null) {
			org.setSort(Integer.valueOf(Integer.parseInt(maxSortNo.toString()) + 1));
		} else {
			org.setSort(Integer.valueOf(0));
		}
		org.setIsleaf("0");
		hibernateDao.save(org);
		org.setOrgidpath(po.getOrgidpath() + "/" + org.getOrgid());
		hibernateDao.update(org);
		return org;
	}

	public List<IOrg> queryAllOrg() {
		return organizationEntityService.getAllDeparts();
	}

	@SuppressWarnings("unchecked")
	public List<Org> querySubOrgs(Long porgid, boolean containtAllSubs, boolean containtSelf, String effective) {
		StringBuffer hql = null;

		if (containtAllSubs) {
			hql = new StringBuffer("select so from " + SysConfig.getSysConfig(Org.class.getName(), Org.class.getName()) + " o,"
					+ SysConfig.getSysConfig(Org.class.getName(), Org.class.getName()) + " so where 1=1");
			hql.append(" and (so.destory is null or so.destory=:destory)");
			if (!"-1".equals(effective))
				hql.append(" and so.effective=:effective");
			hql.append(" and o.orgid=:orgid");

			if (containtSelf) {
				hql.append(" and so.orgidpath like o.orgidpath||'%'");
			} else {
				hql.append(" and so.orgidpath like o.orgidpath||'/%'");
			}

			hql.append(" order by so.sort");
		} else {
			hql = new StringBuffer("select o from " + SysConfig.getSysConfig(Org.class.getName(), Org.class.getName()) + " o where 1=1");
			hql.append(" and (o.destory is null or o.destory=:destory)");
			if (containtSelf) {
				hql.append(" and (o.pOrg.orgid=:orgid or o.orgid=:orgid)");
			} else {
				hql.append(" and o.pOrg.orgid=:orgid");
			}
			if (!"-1".equals(effective)) {
				hql.append(" and o.effective =:effective");
			}

			hql.append(" order by o.sort");
		}
		Query orgs = hibernateDao.createQuery(hql.toString(), new Object[0]).setLong("orgid", porgid.longValue()).setString("destory", "1");

		if ("0".equals(effective)) {
			orgs.setString("effective", "0");
		} else if ("1".equals(effective)) {
			orgs.setString("effective", "1");
		}
		return orgs.list();
	}

	public void updateOrg(Org org, Long operator) {
		Assert.notNull(org.getOrgid(), "org中id不能为空");
		hibernateDao.update(org);
	}

	public SimpleDao getHibernateDao() {
		return hibernateDao;
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
		this.organizationEntityService = organizationEntityService;
	}

}
