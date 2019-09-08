package com.yinhai.ta3.system.sequence;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.sequence.ISequenceService;

public class SequenceService implements ISequenceService {

	SimpleDao hibernateDao;
	String defaultSeq;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setDefaultSeq(String defaultSeq) {
		this.defaultSeq = defaultSeq;
	}

	public String getStringSeq(String seqName) {
		String dialect = hibernateDao.getDialect().toString().toLowerCase();
		if (dialect.contains("mysql"))
			return hibernateDao.createSqlQuery("SELECT seq(?)", new Object[] { seqName }).uniqueResult().toString();
		if (dialect.contains("oracle"))
			return hibernateDao.createSqlQuery("select " + seqName + ".nextval as seq from dual", new Object[0]).uniqueResult().toString();
		if (dialect.contains("sqlserver")) {
			hibernateDao.createSqlQuery("update seq set val=val+1 where name=?", new Object[] { seqName }).executeUpdate();
			return hibernateDao.createSqlQuery("select val from seq where name=?", new Object[] { seqName }).uniqueResult().toString();
		}
		return "";
	}

	public Long getLongSeq(String seqName) {
		return Long.valueOf(getStringSeq(seqName));
	}

	public String getStringSeq() {
		String dialect = hibernateDao.getDialect().toString().toLowerCase();
		if (dialect.contains("mysql"))
			return hibernateDao.createSqlQuery("SELECT seq(?)", new Object[] { defaultSeq }).uniqueResult().toString();
		if (dialect.contains("oracle"))
			return hibernateDao.createSqlQuery("select " + defaultSeq + ".nextval as seq from dual", new Object[0]).uniqueResult().toString();
		if (dialect.contains("sqlserver")) {
			hibernateDao.createSqlQuery("update seq set val=val+1 where name=?", new Object[] { defaultSeq }).executeUpdate();
			return hibernateDao.createSqlQuery("select val from seq where name=?", new Object[] { defaultSeq }).uniqueResult().toString();
		}
		return "";
	}

	public Long getLongSeq() {
		return Long.valueOf(getStringSeq());
	}

	public String getSequence(String seqName) {
		return getStringSeq(seqName);
	}

}
