package com.yinhai.ta3.system;

import java.sql.Timestamp;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.yinhai.sysframework.time.ITimeService;

@WebService
public class TimeService implements ITimeService {

	private SessionFactory sessionFactory;

	@WebMethod(exclude = true)
	public Timestamp getSysTimestamp() {
		Session session = sessionFactory.getCurrentSession();
		return (Timestamp) session.createQuery("SELECT CURRENT_TIMESTAMP() as d from Menu").list().get(0);
	}

	public String getSysStrTimestamp() {
		return getSysTimestamp().toString();
	}

	public Date getSysDate() {
		Session session = sessionFactory.getCurrentSession();
		return (Date) session.createQuery("SELECT CURRENT_DATE() as d from Menu").list().get(0);
	}

	public String getSysStrDate() {
		return getSysDate().toString();
	}

	@WebMethod(exclude = true)
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
