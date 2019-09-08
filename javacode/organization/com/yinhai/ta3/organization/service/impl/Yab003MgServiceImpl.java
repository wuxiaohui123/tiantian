package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IAdminMgService;
import com.yinhai.ta3.organization.service.IYab003MgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.Yab003LevelMg;
import com.yinhai.ta3.system.org.domain.Yab003LevelMgId;
import com.yinhai.ta3.system.org.domain.Yab139Mg;
import com.yinhai.ta3.system.org.domain.Yab139MgId;

public class Yab003MgServiceImpl extends OrgBaseService implements IYab003MgService {

	private IAdminMgService adminMgService;

	public List getYab003List(Long positionid) {
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			return super.getCodeList("yab003", "9999");
		}
		List<Org> orgs = adminMgService.getCurPositionOrgMgScope(positionid);
		List list = new ArrayList();
		for (Org org : orgs) {
			AppCode appCode = new AppCode();
			appCode.setCodeType("yab003");
			appCode.setCodeValue(org.getYab003());
			appCode.setCodeDESC(super.getCodeDesc("yab003", org.getYab003(), "9999"));
			list.add(appCode);
		}
		return list;
	}

	public void setAdminMgService(IAdminMgService adminMgService) {
		this.adminMgService = adminMgService;
	}

	public List<Map<String, String>> queryYab139(Long userid, Long positionid, String yab003) {
		List<Map<String, String>> list = new ArrayList();
		Map<String, String> map = null;
		StringBuffer hql = new StringBuffer();
		List list2;
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			List<AppCode> codeList = CodeTableLocator.getCodeList("yab139");
			list2 = queryCurYab139(positionid, yab003);
			if (ValidateUtil.isEmpty(list2)) {
				for (AppCode appCode : codeList) {
					map = new HashMap();
					map.put("codeValue", appCode.getCodeValue());
					map.put("codeDESC", appCode.getCodeDESC());
					list.add(map);
				}
			} else {
				for (AppCode appCode : codeList) {
					map = new HashMap();
					for (int i = 0; i < list2.size(); i++) {
						Map appCode2 = (Map) list2.get(i);
						if (appCode.getCodeValue().equals(appCode2.get("codeValue"))) {
							break;
						}
						if (i == list2.size() - 1) {
							map.put("codeValue", appCode.getCodeValue());
							map.put("codeDESC", appCode.getCodeDESC());
							list.add(map);
						}
					}
				}
			}
		} else {
			list2 = queryCurYab139(positionid, yab003);
			hql.append("select distinct ays.id.yab139 from AdminYab003Scope ays,").append(getEntityClassName(Position.class)).append(" p,")
					.append(getEntityClassName(User.class)).append(" u,").append("UserPosition up").append(" where u.userid=?")
					.append(" and u.userid=up.id.tauser.userid").append(" and up.id.taposition.positionid=p.positionid")
					.append(" and p.positionid=ays.id.positionid");

			List<String> yab139s = hibernateDao.createQuery(hql.toString(), new Object[] { userid }).list();
			if (list2 != null) {
				for (String codeValue : yab139s) {
					for (int i = 0; i < list2.size(); i++) {
						Map map1 = (Map) list2.get(i);
						if (codeValue.equals(map1.get("codeValue"))) {
							break;
						}
						if (i == list2.size() - 1) {
							map = new HashMap();
							map.put("codeValue", codeValue);
							map.put("codeDESC", CodeTableLocator.getCodeDesc("yab139", codeValue));
							list.add(map);
						}
					}
				}
			} else {
				for (String codeValue : yab139s) {
					map = new HashMap();
					map.put("codeValue", codeValue);
					map.put("codeDESC", CodeTableLocator.getCodeDesc("yab139", codeValue));
					list.add(map);
				}
			}
		}
		return list;
	}

	public List queryCurYab139(Long positionid, String yab003) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空");
		}
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			List<Yab139Mg> list = hibernateDao.createQuery("from Yab139Mg ym where ym.id.yab003=?", new Object[] { yab003 }).list();
			List list2 = new ArrayList();
			for (Yab139Mg yab139Mg : list) {
				Map map = new HashMap();
				map.put("codeValue", yab139Mg.getId().getYab139());
				map.put("codeDESC", super.getCodeDesc("yab139", yab139Mg.getId().getYab139(), "9999"));
				list2.add(map);
			}
			return list2;
		}

		List<Yab139Mg> list1 = hibernateDao
				.createQuery(
						"select distinct ym from AdminYab003Scope ays,Yab139Mg ym where ays.id.positionid=? and ays.id.yab139=ym.id.yab139 and ym.id.yab003=?",
						new Object[] { positionid, yab003 }).list();
		if (ValidateUtil.isEmpty(list1)) {
			return null;
		}
		List list2 = new ArrayList();
		for (int i = 0; i < list1.size(); i++) {
			Yab139Mg yab139Mg = (Yab139Mg) list1.get(i);
			Map map = new HashMap();
			map.put("codeValue", yab139Mg.getId().getYab139());
			map.put("codeDESC", super.getCodeDesc("yab139", yab139Mg.getId().getYab139(), "9999"));
			list2.add(map);
		}
		return list2;
	}

	public void saveYab139(String yab003, List<Key> list) {
		for (Key key : list) {
			Yab139MgId id = new Yab139MgId();
			id.setYab003(yab003);
			id.setYab139(key.getAsString("codeValue"));
			Yab139Mg mg = new Yab139Mg(id);
			hibernateDao.save(mg);
		}
	}

	public void removeYab139(String yab003, List<Key> list) {
		for (Key key : list) {
			Yab139MgId id = new Yab139MgId();
			id.setYab003(yab003);
			id.setYab139(key.getAsString("codeValue"));
			Yab139Mg mg = new Yab139Mg(id);
			hibernateDao.delete(mg);
		}
	}

	public List queryYab003(Long positionid, String yab003) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空");
		}
		if (ValidateUtil.isEmpty(yab003)) {
			throw new AppException("�??经办机构为空");
		}
		List<AppCode> codeList = super.getCodeList("yab003", "9999");
		List list2 = queryChildYab003(positionid, yab003);
		List list = new ArrayList();
		if ((IPosition.ADMIN_POSITIONID.equals(positionid)) && (!ValidateUtil.isEmpty(codeList))) {
			for (AppCode appCode : codeList) {
				if (!ValidateUtil.isEmpty(list2)) {
					for (int i = 0; i < list2.size(); i++) {
						Yab003LevelMg mg = (Yab003LevelMg) list2.get(i);
						if (appCode.getCodeValue().equals(mg.getId().getYab003())) {
							break;
						}
						if (i == list2.size() - 1) {
							list.add(appCode);
						}
					}
				} else {
					list = codeList;
					break;
				}
			}
		}

		return list;
	}

	public List queryChildYab003(Long positionid, String yab003) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空");
		}
		if (ValidateUtil.isEmpty(yab003)) {
			throw new AppException("�??经办机构为空");
		}
		List list1 = new ArrayList();
		List<Yab003LevelMg> list = hibernateDao.createQuery("from Yab003LevelMg ylm where ylm.id.pyab003=?", new Object[] { yab003 }).list();
		List<Org> orgs;
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			for (Yab003LevelMg mg : list) {
				AppCode appCode = new AppCode();
				appCode.setCodeType("yab003");
				appCode.setCodeValue(mg.getId().getYab003());
				appCode.setCodeDESC(super.getCodeDesc("yab003", mg.getId().getYab003(), "9999"));
				list1.add(appCode);
			}
		} else {
			orgs = adminMgService.getCurPositionOrgMgScope(positionid);
			for (Yab003LevelMg mg : list) {
				if (!ValidateUtil.isEmpty(orgs)) {
					for (int i = 0; i < orgs.size(); i++) {
						if (mg.getId().getYab003().equals(((Org) orgs.get(i)).getYab003())) {
							AppCode appCode = new AppCode();
							appCode.setCodeType("yab003");
							appCode.setCodeValue(mg.getId().getYab003());
							appCode.setCodeDESC(super.getCodeDesc("yab003", mg.getId().getYab003(), "9999"));
							list1.add(appCode);
							break;
						}
					}
				}
			}
		}
		return list1;
	}

	public List queryYab003Tree(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位为空");
		}
		return hibernateDao.createQuery("select ylm from Yab003LevelMg ylm", new Object[0]).list();
	}

	public void saveYab003(String pyab003, List<Key> list, String firstTree) {
		if ("true".equals(firstTree)) {
			Yab003LevelMgId id = new Yab003LevelMgId();
			id.setPyab003("0");
			id.setYab003(((Key) list.get(0)).getAsString("codeValue"));
			Yab003LevelMg ylm = new Yab003LevelMg(id);
			hibernateDao.save(ylm);
		} else {
			if (ValidateUtil.isEmpty(pyab003)) {
				throw new AppException("父经办机构为空");
			}
			for (Key key : list) {
				Yab003LevelMgId id = new Yab003LevelMgId();
				id.setPyab003(pyab003);
				id.setYab003(key.getAsString("codeValue"));
				Yab003LevelMg ylm = new Yab003LevelMg(id);
				hibernateDao.save(ylm);
			}
		}
	}

	public void removeYab003(List list) {
		if (!ValidateUtil.isEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				hibernateDao.createQuery("delete from Yab003LevelMg ylm where ylm.id.yab003=?", new Object[] { map.get("id") }).executeUpdate();
			}
		}
	}
}
