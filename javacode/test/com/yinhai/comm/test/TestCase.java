package com.yinhai.comm.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class TestCase {

	public static void main(String[] args) {
		/*ClassPathXmlApplicationContext cpx = new ClassPathXmlApplicationContext("app-context.xml");
		SimpleDao dao = cpx.getBean("hibernateDao",SimpleDao.class);
		System.out.println(dao);
		String hql = "from User t where t.name like '%唐%'";
		List list =  dao.getSessionFactory()
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		
		String hql = "from User t where t.name like '%唐%'";
		SessionFactory factory = new AnnotationConfiguration().configure("hibernateconfig.xml").buildSessionFactory();
		Session session = factory.getCurrentSession();
		List list = session.createQuery(hql).list();
		*/
	}
}
