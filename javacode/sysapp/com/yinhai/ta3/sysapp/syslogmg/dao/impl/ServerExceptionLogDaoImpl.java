package com.yinhai.ta3.sysapp.syslogmg.dao.impl;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.log.Taserverexceptionlog;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.syslogmg.dao.ServerExceptionLogDao;
import org.hibernate.Query;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@WebService
public class ServerExceptionLogDaoImpl implements ServerExceptionLogDao {

	private SimpleDao dao;

	@WebMethod(exclude = true)
	public SimpleDao getDao() {
		return dao;
	}

	@WebMethod(exclude = true)
	public void setDao(SimpleDao dao) {
		this.dao = dao;
	}

	public void insert(Taserverexceptionlog log) {
		dao.save(log);
	}

	@WebMethod(exclude = true)
	public List<Taserverexceptionlog> getList(Timestamp begin, Timestamp end) {
		List<Timestamp> params = new LinkedList<Timestamp>();
		StringBuffer hql = new StringBuffer("from Taserverexceptionlog t where 1=1 ");
		if (!ValidateUtil.isEmpty(begin)) {
			hql.append(" and t.time >= ?");
			params.add(begin);
		}
		if (!ValidateUtil.isEmpty(end)) {
			hql.append(" and t.time <= ?");
			params.add(end);
		}
		hql.append(" order by t.time desc");
		Query query = dao.getSession().createQuery(hql.toString());
		if (!ValidateUtil.isEmpty(params)) {
			for (int i = 0; i < params.size(); i++) {
				query.setTimestamp(i, (Date) params.get(i));
			}
		}
		return query.list();
	}

	@WebMethod(exclude = true)
	public PageBean getPage(String gridId, Timestamp begin, Timestamp end, int start, int limit) {
		List<Timestamp> params = new LinkedList<Timestamp>();
		StringBuffer hql = new StringBuffer("from Taserverexceptionlog t where 1=1 ");
		if (!ValidateUtil.isEmpty(begin)) {
			hql.append(" and t.time >= ?");
			params.add(begin);
		}
		if (!ValidateUtil.isEmpty(end)) {
			hql.append(" and t.time <= ?");
			params.add(end);
		}
		hql.append(" order by t.time desc");
		Query query = dao.getSession().createQuery(hql.toString());
		if (!ValidateUtil.isEmpty(params)) {
			for (int i = 0; i < params.size(); i++) {
				query.setTimestamp(i, (Date) params.get(i));
			}
		}

		String countHql = "select count(t.id)" + hql.toString();
		Query countQuery = dao.getSession().createQuery(countHql);
		if (!ValidateUtil.isEmpty(params)) {
			for (int i = 0; i < params.size(); i++) {
				countQuery.setTimestamp(i, (Date) params.get(i));
			}
		}

		query.setFirstResult(start).setMaxResults(limit);
		PageBean pg = new PageBean();
		pg.setGridId(gridId);
		pg.setLimit(Integer.valueOf(limit));
		pg.setStart(Integer.valueOf(start));
		pg.setList(query.list());
		pg.setTotal(Integer.valueOf(((Long) countQuery.uniqueResult()).intValue()));
		return pg;
	}

	@WebMethod(exclude = true)
	public PageBean getPageByCount(String gridId, Timestamp begin, Timestamp end, int start, int limit) {
		List<Timestamp> params = new LinkedList<Timestamp>();
		StringBuffer hql = new StringBuffer("from Taserverexceptionlog t where 1=1 ");
		if (!ValidateUtil.isEmpty(begin)) {
			hql.append(" and t.time >= ?");
			params.add(begin);
		}
		if (!ValidateUtil.isEmpty(end)) {
			hql.append(" and t.time <= ?");
			params.add(end);
		}
		hql.append(" order by t.time desc");
		Query query = dao.getSession().createQuery(hql.toString());
		if (!ValidateUtil.isEmpty(params)) {
			for (int i = 0; i < params.size(); i++) {
				query.setTimestamp(i, (Date) params.get(i));
			}
		}
		query.setFirstResult(start).setMaxResults(limit);
		PageBean pg = new PageBean();
		pg.setGridId(gridId);
		pg.setLimit(Integer.valueOf(limit));
		pg.setStart(Integer.valueOf(start));
		pg.setList(query.list());
		return pg;
	}

	@WebMethod(exclude = true)
	public Taserverexceptionlog getTaserverexceptionlog(String id) {
		String hql = "from Taserverexceptionlog where id = ?";

		return (Taserverexceptionlog) dao.findUnique(hql, new Object[]{id});
	}

	@WebMethod(exclude = true)
	public void delete(String id) {
		dao.delete(getTaserverexceptionlog(id));
	}
}
