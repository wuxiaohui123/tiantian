package com.yinhai.sysframework.dao.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.yinhai.sysframework.dao.hibernate.pagenation.Pagination;
import com.yinhai.sysframework.util.ReflectUtil;

public class SimpleDao {

	private static Logger logger = LogManager.getLogger(SimpleDao.class);

	public static final String ORDER_ENTRIES = "orderEntries";

	public SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Serializable save(Object o) {
		return getSession().save(o);
	}

	public void update(Object o) {
		getSession().update(o);
	}

	public void delete(Object o) {
		getSession().delete(o);
	}

	public List find(String hql, Object... values) {
		return createQuery(hql, values).list();
	}

	public Object findUnique(String hql, Object... values) {
		return createQuery(hql, values).uniqueResult();
	}

	public Pagination find(Finder finder, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList<Object>());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}

	public List find(Finder finder) {
		Query query = finder.createQuery(getSession());
		List list = query.list();
		return list;
	}

	public Query createQuery(String queryString, Object... values) {
		Assert.hasText(queryString, "");
		Query queryObject = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject;
	}

	public Query createSqlQuery(String queryString, Object... values) {
		Assert.hasText(queryString, "");
		Query queryObject = getSession().createSQLQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject;
	}

	public Pagination findByCriteria(Criteria crit, int pageNo, int pageSize) {
		CriteriaImpl impl = (CriteriaImpl) crit;
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();
		List<CriteriaImpl.OrderEntry> orderEntries;
		try {
			orderEntries = (List) ReflectUtil.getFieldValue(impl, ORDER_ENTRIES);
			ReflectUtil.setFieldValue(impl, ORDER_ENTRIES, new ArrayList());
		} catch (Exception e) {
			throw new RuntimeException("cannot read/write 'orderEntries' from CriteriaImpl", e);
		}
		int totalCount = ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).intValue();
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		crit.setProjection(projection);
		if (projection == null) {
			crit.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			crit.setResultTransformer(transformer);
		}
		try {
			ReflectUtil.setFieldValue(impl, ORDER_ENTRIES, orderEntries);
		} catch (Exception e) {
			throw new RuntimeException("set 'orderEntries' to CriteriaImpl faild", e);
		}
		crit.setFirstResult(p.getFirstResult());
		crit.setMaxResults(p.getPageSize());
		p.setList(crit.list());
		return p;
	}

	public int countQueryResult(Finder finder) {
		Query query = getSession().createQuery(finder.getRowCountHql());
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return ((Number) query.getFirstResult()).intValue();
	}

	public int insertSQLBatch(final List<String> sqllist, int commitNum) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		int len = sqllist.size();
		for (int i = 0; i < len; i++) {
			session.createQuery(sqllist.get(i)).executeUpdate();
			if (i % commitNum == 0) {
				session.flush(); // 清理缓存，执行批量插入
				session.clear(); // 清空缓存中的 对象
			}
		}
		session.flush(); // 清理缓存，执行批量插入
		session.clear(); // 清空缓存中的 对象
		tx.commit();
		return len;
	}

	public int updateSQLBatch(final List<String> sqllist, int commitNum) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		int len = sqllist.size();
		for (int i = 0; i < len; i++) {
			session.createQuery(sqllist.get(i)).executeUpdate();
			if (i % commitNum == 0) {
				session.flush(); // 清理缓存，执行批量插入
				session.clear(); // 清空缓存中的 对象
			}
		}
		session.flush(); // 清理缓存，执行批量插入
		session.clear(); // 清空缓存中的 对象
		tx.commit();
		return len;
	}

	public int deleteSQLBatch(final List<String> sqllist, int commitNum) {
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		int len = sqllist.size();
		for (int i = 0; i < len; i++) {
			session.createQuery(sqllist.get(i)).executeUpdate();
			if (i % commitNum == 0) {
				session.flush(); // 清理缓存，执行批量插入
				session.clear(); // 清空缓存中的 对象
			}
		}
		session.flush(); // 清理缓存，执行批量插入
		session.clear(); // 清空缓存中的 对象
		tx.commit();
		return len;
	}

	// insert into user(name,password,email,birthday) values(?,?,?,?)"
	public int executeBatchByJDBC(final String sql, List<Map<String, String>> paramList) {
		Connection conn = getSession().disconnect();
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (Map<String, String> map : paramList) {
				int mindex = 1;
				for (String key : map.keySet()) {
					ps.setString(mindex, map.get(key));
					mindex++;
				}
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException el2) {
				el2.printStackTrace();
			} catch (Exception el3) {
				el3.printStackTrace();
			}

		}
		return paramList.size();
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public Dialect getDialect() {
		return ((SessionFactoryImpl) sessionFactory).getDialect();
		
	}
}
