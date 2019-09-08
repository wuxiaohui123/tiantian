package com.yinhai.ta3.system.cache.ehcache.dao.impl;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;

import com.yinhai.sysframework.cache.ehcache.ServerAddress;
import com.yinhai.sysframework.cache.ehcache.dao.ServerAddressDao;
import com.yinhai.sysframework.cache.ehcache.domain.ServeraddressDomain;
import com.yinhai.sysframework.dao.hibernate.BaseDao;
import com.yinhai.sysframework.util.ValidateUtil;

@SuppressWarnings("unchecked")
public class ServerAddressDaoImpl extends BaseDao<ServerAddress, String> implements ServerAddressDao {

	protected Class<ServerAddress> getEntityClass() {
		return ServerAddress.class;
	}

	public List<ServeraddressDomain> getList(ServerAddress server) {
		StringBuffer hql = new StringBuffer("from ServeraddressDomain a where 1=1");
		List<Object> objs = new LinkedList<Object>();
		if (ValidateUtil.isNotEmpty(server.getAddress())) {
			hql.append(" and a.address = ?");
			objs.add(server.getAddress());
		}
		if (ValidateUtil.isNotEmpty(server.getCause())) {
			hql.append(" and a.canuse = ?");
			objs.add(server.getCause());
		}

		Query query = getSession().createQuery(hql.toString());
		if (ValidateUtil.isNotEmpty(objs)) {
			for (int i = 0; i < objs.size(); i++) {
				query.setParameter(i, objs.get(i));
			}
		}

		return query.list();
	}

	public void insert(ServeraddressDomain serverAddress) {
		save(serverAddress);
	}

	public ServerAddress getServerAddress(String address) {
		String hql = "from ServeraddressDomain where address = ?";
		return (ServerAddress) findUnique(hql, address);
	}

	public List<String> getAllUsefulServerAddress() {
		String hql = "select address from ServeraddressDomain where canuse = 0";
		return createQuery(hql).list();
	}

	public int delete(ServerAddress serverAddress) {
		String hql = "delete from  ServeraddressDomain where address = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, serverAddress.getAddress());
		return query.executeUpdate();
	}

	public int update(ServerAddress serverAddress) {
		String hql = "update ServeraddressDomain set canuse = ? where address = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, serverAddress.getCause());
		query.setParameter(1, serverAddress.getAddress());
		return query.executeUpdate();
	}
}
