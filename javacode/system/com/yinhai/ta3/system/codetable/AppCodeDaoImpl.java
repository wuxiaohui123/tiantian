package com.yinhai.ta3.system.codetable;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;

import org.hibernate.Query;
import org.springframework.util.Assert;

import com.yinhai.sysframework.codetable.AppCodeDao;
import com.yinhai.sysframework.codetable.domain.Aa10;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;
import com.yinhai.sysframework.dao.hibernate.BaseDao;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
@WebService
public class AppCodeDaoImpl extends BaseDao<Aa10a1, AppCodeId> implements AppCodeDao {

	public AppCode getAppCode(String codeType, String codeValue, String yab003) {
		Assert.notNull(codeType, "");
		Assert.notNull(codeValue, "");
		Assert.notNull(yab003, "");
		return (AppCode) super.findUnique("from Aa10a1 where aaa100=? and aaa102=?  and (yab003=?) ",
				new Object[] { codeType.toUpperCase(), codeValue, yab003 });
	}

	public List<AppCode> getCodeListByCodeType(String codeType, String yab003, boolean includeUnValid) {
		Assert.notNull(codeType, "");
		Assert.notNull(yab003, "");
		if (includeUnValid) {
			return super.find("from Aa10a1 where aaa100=? and (yab003=?) ",
					new Object[] { codeType.toUpperCase(), yab003 });
		}
		return super.find("from Aa10a1 where aaa100=? and aae120=? and (yab003=?) ",
				new Object[] { codeType.toUpperCase(), "0", yab003 });
	}

	protected Class<Aa10a1> getEntityClass() {
		return Aa10a1.class;
	}

	public List<String> getDistinctYab003() {
		return super.find("select distinct yab003 from Aa10a1", new Object[0]);
	}

	public List<AppCode> getCodeList(String yab003) {
		Assert.notNull(yab003,"");
		return super.find("from Aa10a1 where aae120=? and yab003=? order by aaa100,aaa102",
				new Object[] { "0", yab003 });
	}

	public int getLocalCacheVersion() {
		String sql = "select max(version) from talocalcacheversion";
		Query query = super.createSqlQuery(sql);
		List list = query.list();
		if ((list != null) && (list.size() > 0)) {
			if ((list.get(0) instanceof BigDecimal)) {
				return ((BigDecimal) list.get(0)).intValue();
			}
			return ((Integer) list.get(0)).intValue();
		}
		return 1;
	}

	public void changeLocalCacheVersion(int version, String codeType) {
		String sql = "INSERT INTO talocalcacheversion(version,codetype) VALUES (?,?)";
		Query query = super.createSqlQuery(sql, new Object[] { Integer.valueOf(version), codeType });
		query.executeUpdate();
	}

	public String getCodeListJson(int version, int max) {
		String sql = "SELECT DISTINCT a.codetype from talocalcacheversion a where  a.version > ? and a.version <= ? and a.codetype is not null";
		Query query = super.createSqlQuery(sql, new Object[] { Integer.valueOf(version), Integer.valueOf(max) });
		List<String> codeTypes = query.list();
		StringBuilder sb = new StringBuilder();
		for (String codeType : codeTypes) {
			List<AppCode> list = getCodeListByCodeType(codeType, "9999", true);
			sb.append("\"" + JSonFactory.getJson(codeType) + "\":");
			sb.append("\"[");
			Iterator<AppCode> i;
			if ((list != null) && (list.size() > 0)) {
				for (i = list.iterator(); i.hasNext();) {
					AppCode app = (AppCode) i.next();
					sb.append(JSonFactory.getJson("{\"id\":\"")
							+ JSonFactory.getJson(JSonFactory.getJson(app.getCodeValue()))
							+ JSonFactory.getJson("\","));
					sb.append(JSonFactory.getJson("\"name\":\"")
							+ JSonFactory.getJson(JSonFactory.getJson(app.getCodeDESC())) + JSonFactory.getJson("\","));
					sb.append(JSonFactory.getJson("\"py\":\"") + JSonFactory.getJson(JSonFactory.getJson(app.getPy())));
					if (i.hasNext())
						sb.append(JSonFactory.getJson("\"},"));
					else
						sb.append(JSonFactory.getJson("\"}"));
				}
			}
			sb.append("]\",");
		}

		return sb.toString();
	}

	public List<AppCode> getCodeListByAppCode(AppCode appcode) {
		StringBuffer hql = new StringBuffer("from Aa10a1 a where 1=1 ");
		List<Object> objs = new LinkedList();
		if (ValidateUtil.isNotEmpty(appcode.getCodeDESC())) {
			hql.append(" and a.codeDESC = ?");
			objs.add(appcode.getCodeDESC());
		}
		if (ValidateUtil.isNotEmpty(appcode.getCodeType())) {
			hql.append(" and a.id.codeType = ?");
			objs.add(appcode.getCodeType());
		}
		if (ValidateUtil.isNotEmpty(appcode.getCodeTypeDESC())) {
			hql.append(" and a.codeTypeDESC = ?");
			objs.add(appcode.getCodeTypeDESC());
		}
		if (ValidateUtil.isNotEmpty(appcode.getCodeValue())) {
			hql.append(" and a.id.codeValue = ?");
			objs.add(appcode.getCodeValue());
		}
		if (ValidateUtil.isNotEmpty(appcode.getValidFlag())) {
			hql.append(" and a.validFlag = ?");
			objs.add(appcode.getValidFlag());
		}
		if (ValidateUtil.isNotEmpty(appcode.getYab003())) {
			hql.append(" and a.yab003 = ?");
			objs.add(appcode.getYab003());
		}

		Query query = getSession().createQuery(hql.toString());
		if (ValidateUtil.isNotEmpty(objs)) {
			for (int i = 0; i < objs.size(); i++) {
				query.setParameter(i, objs.get(i));
			}
		}

		return query.list();
	}

	public void insertAa10(Aa10 aa10) {
		super.save(aa10);
	}

	public void updateAa10(Aa10 aa10) {
		StringBuffer hql = new StringBuffer("update Aa10 a set");
		List<Object> objs = new LinkedList();
		if (ValidateUtil.isNotEmpty(aa10.getCodeDESC())) {
			hql.append(" a.codeDESC = ?,");
			objs.add(aa10.getCodeDESC());
		}
		if (ValidateUtil.isNotEmpty(aa10.getCodeTypeDESC())) {
			hql.append(" a.codeTypeDESC = ?,");
			objs.add(aa10.getCodeTypeDESC());
		}
		if (ValidateUtil.isNotEmpty(aa10.getValidFlag())) {
			hql.append(" a.validFlag = ?,");
			objs.add(aa10.getValidFlag());
		}
		if (ValidateUtil.isNotEmpty(aa10.getYab003())) {
			hql.append(" a.yab003 = ?");
			objs.add(aa10.getYab003());
		}

		if (!ValidateUtil.isEmpty(aa10.getId())) {
			hql.append(" where a.id.codeType = ? and a.id.codeValue = ?");
			objs.add(aa10.getId().getCodeType());
			objs.add(aa10.getId().getCodeValue());
		}
		Query query = getSession().createQuery(hql.toString());
		if (ValidateUtil.isNotEmpty(objs)) {
			for (int i = 0; i < objs.size(); i++) {
				query.setParameter(i, objs.get(i));
			}
		}
		query.executeUpdate();
	}

	public void deleteAa10(AppCodeId id) {
		String hql = "delete from Aa10 a where a.id.codeType = ? and a.id.codeValue = ?";
		createQuery(hql, new Object[] { id.getCodeType(), id.getCodeValue() }).executeUpdate();
	}
}
