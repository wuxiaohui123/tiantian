package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.Finder;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IDataAccessApi;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.DateUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IDataAccessDimensionManagementService;
import com.yinhai.ta3.redis.annotation.CacheMethod;
import com.yinhai.ta3.redis.annotation.LapseMethod;
import com.yinhai.ta3.system.org.domain.DataAccessDimension;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class DataAccessDimensionManagementServiceImpl extends OrgBaseService implements IDataAccessDimensionManagementService, IDataAccessApi {

	private SimpleDao hibernateDao;
	private OrganizationEntityService organizationEntityService;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
		this.organizationEntityService = organizationEntityService;
	}

	public void save(Long menuid, Long positionid, String dimensiontype, String allaccess, List<Key> list) {
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		if ("allAccess".equals(allaccess)) {
			hibernateDao
					.createQuery(
							"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
							new Object[] { positionid, menuid, dimensiontype, curSyspathId }).executeUpdate();
			DataAccessDimension dad = new DataAccessDimension();
			dad.setMenuid(menuid);
			dad.setPositionid(positionid);
			dad.setDimensiontype(dimensiontype);
			dad.setAllaccess("0");
			dad.setSyspath(curSyspathId);
			hibernateDao.save(dad);
		} else {
			for (Key key : list) {
				DataAccessDimension dad = new DataAccessDimension();
				dad.setMenuid(menuid);
				dad.setPositionid(positionid);
				dad.setDimensiontype(dimensiontype);
				dad.setDimensionpermissionid(key.getAsString("codeValue"));
				dad.setAllaccess("1");
				dad.setSyspath(curSyspathId);
				hibernateDao.save(dad);
			}
		}
	}

	public void saveAll(Long menuid, Long positionid, String dimensiontype, String allaccess) {
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		if ("allAccess".equals(allaccess)) {
			hibernateDao
					.createQuery(
							"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
							new Object[] { positionid, menuid, dimensiontype, curSyspathId }).executeUpdate();
			DataAccessDimension dad = new DataAccessDimension();
			dad.setMenuid(menuid);
			dad.setPositionid(positionid);
			dad.setDimensiontype(dimensiontype);
			dad.setAllaccess("0");
			dad.setSyspath(curSyspathId);
			hibernateDao.save(dad);
		} else {
			hibernateDao
					.createQuery(
							"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
							new Object[] { positionid, menuid, dimensiontype, curSyspathId }).executeUpdate();
		}
	}

	@CacheMethod(expires = 28800)
	public List<AppCode> query(Long menuid, Long positionid, String dimensiontype) {
		List<AppCode> tempList = null;
		List<DataAccessDimension> list = hibernateDao.createQuery(
				"from DataAccessDimension dad where dad.menuid=? and dad.positionid=? and dad.dimensiontype=?",
				new Object[] { menuid, positionid, dimensiontype }).list();
		IOrg org;
		if (!ValidateUtil.isEmpty(list)) {
			if (list.size() == 1) {
				DataAccessDimension dad = (DataAccessDimension) list.get(0);
				org = organizationEntityService.getDepartByPositionId(dad.getPositionid());
				if ("0".equals(dad.getAllaccess())) {
					tempList = getCodeList(dimensiontype, org.getYab139());
				} else {
					AppCode appCode = CodeTableLocator.getAppCode(dad.getDimensiontype(),
							dad.getDimensionpermissionid(), org.getYab139());
					tempList = new ArrayList();
					tempList.add(appCode);
				}
			} else {
				tempList = new ArrayList();
				org = null;
				for (DataAccessDimension dad : list) {
					if (org == null) {
						org = organizationEntityService.getDepartByPositionId(dad.getPositionid());
					}
					AppCode appCode = CodeTableLocator.getAppCode(dad.getDimensiontype(),
							dad.getDimensionpermissionid(), org.getYab139());
					tempList.add(appCode);
				}
			}
		}

		return tempList;
	}

	@LapseMethod(name = "query")
	public void clearCache(Long menuid, Long positionid, String dimensiontype) {
	}

	public PageBean queryPos(ParamDTO dto) {
		Long curPosId = dto.getUserInfo().getNowPosition().getPositionid();
		if (ValidateUtil.isEmpty(curPosId)) {
			throw new AppException("当前岗位为空，不能进行查�?");
		}
		Long orgid = dto.getAsLong("orgid");
		String issub = dto.getAsString("issub");
		String positiontype = dto.getAsString("positiontype");
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class)).append(" p,")
				.append(super.getEntityClassName(Org.class)).append(" o ");
		if (ValidateUtil.isEmpty(orgid)) {
			if (IPosition.ADMIN_POSITIONID.equals(curPosId)) {
				hql.append("where 1=1 and p.orgidpath like o.orgidpath||'%'").append(" and o.orgid=")
						.append(Org.ORG_ROOT_ID);
			} else {
				hql.append(",OrgMg om").append(" where 1=1").append(" and om.id.positionid=").append(curPosId)
						.append(" and om.id.orgid=o.orgid").append(" and o.orgid=p.taorg.orgid")
						.append(" and p.positionid<>").append(IPosition.ADMIN_POSITIONID);

			}

		} else if (IPosition.ADMIN_POSITIONID.equals(curPosId)) {
			if ("0".equals(issub)) {
				hql.append("where 1=1 and p.orgidpath like o.orgidpath||'%'").append(" and o.orgid=").append(orgid);
			} else {
				hql.append("where 1=1 and p.taorg.orgid=o.orgid").append(" and o.orgid=").append(orgid);
			}
		} else {
			if ("0".equals(issub)) {
				hql.append(" where 1=1").append(" and p.taorg.orgid in (")
						.append(" select om.id.orgid from OrgMg om where om.id.positionid=").append(curPosId)
						.append(")").append(" and p.orgidpath like o.orgidpath||'%'").append(" and o.orgid=")
						.append(orgid);

			} else {

				hql.append("where 1=1 and p.taorg.orgid=o.orgid").append(" and o.orgid=").append(orgid);
			}

			hql.append(" and p.positionid<>").append(IPosition.ADMIN_POSITIONID);
		}

		if ("1".equals(positiontype)) {
			hql.append(" and p.positiontype=").append("1");
		} else if ("2".equals(positiontype)) {
			hql.append(" and p.positiontype=").append("2");
		} else if ("3".equals(positiontype)) {
			hql.append(" and p.positiontype=").append("3");
		} else if ("4".equals(positiontype)) {
			hql.append(" and p.positiontype=").append("1").append(" and p.isshare=").append("1");
		} else if ("5".equals(positiontype)) {
			hql.append(" and p.positiontype=").append("1").append(" and p.iscopy=").append("1");
		}

		hql.append(" and (p.taorg.destory is null or p.taorg.destory=").append("1").append(")")
				.append(" and p.taorg.effective=").append("0").append(" and p.effective=").append("0")
				.append(" and (p.validtime is null or p.validtime >=:date)").append(" and p.positionid<>")
				.append(curPosId).append(" order by p.orgidpath");

		Query query = null;
		Finder finder = Finder.create(hql.toString());
		String countHql = finder.getRowCountHql();
		Query queryCount = hibernateDao.createQuery(countHql, new Object[0]);
		query = hibernateDao.createQuery(hql.toString(), new Object[0]);
		queryCount.setDate("date", getSysDate());
		query.setDate("date", getSysDate());
		Integer skipResults = Integer.valueOf(dto.getStart("posGrid") == null ? 0 : dto.getStart("posGrid").intValue());
		Integer maxResults = Integer.valueOf(dto.getLimit("posGrid") == null ? 0 : dto.getLimit("posGrid").intValue());
		PageBean pb = new PageBean();
		pb.setGridId("posGrid");
		Long total = (Long) queryCount.uniqueResult();
		pb.setTotal(Integer.valueOf(total.intValue()));
		List list = query.list();
		pb.setList(list);
		pb.setStart(skipResults);
		pb.setLimit(maxResults);
		return pb;
	}

	public List<PermissionTreeVO> queryTrree(Long positionid) {
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("岗位为空!");
		}
		List<PermissionTreeVO> nodes = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("from PositionAuthrity pa").append(" where pa.id.taposition.positionid=?")
				.append(" and pa.usepermission=?").append(" and (pa.id.tamenu.menutype<>?)")
				.append(" and pa.id.tamenu.effective=?").append(" order by pa.id.tamenu.sortno");

		List<PositionAuthrity> positionAuthrities = hibernateDao.createQuery(hql.toString(),
				new Object[] { positionid, "1", "1", "0" }).list();
		PermissionTreeVO node;
		if ((positionAuthrities != null) && (positionAuthrities.size() > 0)) {
			node = null;
			for (PositionAuthrity positionAuthrity : positionAuthrities) {
				Menu menu = positionAuthrity.getId().getTamenu();
				node = new PermissionTreeVO();
				node.setId(menu.getMenuid());
				node.setPId(menu.getPmenuid());
				node.setName(menu.getMenuname());
				node.setOpen(true);
				if ((!ValidateUtil.isEmpty(positionAuthrity.getEffecttime()))
						&& (DateUtil.computeDateOnly(positionAuthrity.getEffecttime(), timeService.getSysDate()) < 0)) {
					node.setEffectivetimeover(true);
				}

				node.setUseyab003(menu.getUseyab003());
				if ("0".equals(menu.getUseyab003())) {
					node.setChecked(true);
				} else {
					node.setNocheck(true);
				}
				nodes.add(node);
			}
		}
		return nodes;
	}

	public void saveAccess(Long positionid, String dimensiontype, String allaccess, List<Key> menulist,
			List<Key> yab139list) {
		for (Key key : menulist) {
			Long menuid = key.getAsLong("id");
			save(menuid, positionid, dimensiontype, allaccess, yab139list);
		}
	}

	public List<Map<String, String>> queryAdminYab139ScopeNoSelected(Long userid, Long positionid, List<AppCode> list1,
			Long dpositionid) {
		if (ValidateUtil.isEmpty(userid)) {
			throw new AppException("该管理员不存在");
		}
		if (ValidateUtil.isEmpty(positionid)) {
			throw new AppException("当前岗位不存在");
		}
		List<Map<String, String>> list = new ArrayList();
		Map<String, String> map = null;
		StringBuffer hql = new StringBuffer();
		if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
			List<AppCode> codeList = CodeTableLocator.getCodeList("yab139");
			for (AppCode appCode : codeList) {
				map = new HashMap();
				if (list1 != null) {
					for (int i = 0; i < list1.size(); i++) {
						if (appCode.getCodeValue().equals(((AppCode) list1.get(i)).getCodeValue())) {
							break;
						}
						if (i == list1.size() - 1) {
							map.put("codeValue", appCode.getCodeValue());
							map.put("codeDESC", appCode.getCodeDESC());
							list.add(map);
						}
					}
				} else {
					map.put("codeValue", appCode.getCodeValue());
					map.put("codeDESC", appCode.getCodeDESC());
					list.add(map);
				}
			}
		} else {
			hql.append("select distinct ays.id.yab139 from AdminYab003Scope ays,")
					.append(getEntityClassName(Position.class))
					.append(" p,")
					.append(getEntityClassName(User.class))
					.append(" u,")
					.append("UserPosition up")
					.append(" where u.userid=?")
					.append(" and u.userid=up.id.tauser.userid")
					.append(" and up.id.taposition.positionid=p.positionid")
					.append(" and p.positionid=ays.id.positionid")
					.append(" and ays.id.yab139 not in (select dad.dimensionpermissionid from DataAccessDimension dad where dad.positionid=?)");

			List<String> yab139s = hibernateDao.createQuery(hql.toString(), new Object[] { userid, dpositionid })
					.list();
			for (String codeValue : yab139s) {
				map = new HashMap();
				map.put("codeValue", codeValue);
				map.put("codeDESC", CodeTableLocator.getCodeDesc("yab139", codeValue));
				list.add(map);
			}
		}
		return list;
	}

	public void removeYab139(Long menuid, Long positionid, String dimensiontype, List<Key> list) {
		String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
		for (Key key : list) {
			hibernateDao
					.createQuery(
							"delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.dimensionpermissionid=? and dad.syspath=?",
							new Object[] { positionid, menuid, dimensiontype, key.getAsString("codeValue"),
									curSyspathId }).executeUpdate();
		}
	}

	public IMenu getMenu(Long menuId) {
		return (Menu) hibernateDao.getSession().get(Menu.class, menuId);
	}

	public List<Menu> getChildMenus(Long menuId) {
		if (!SysConfig.getSysconfigToBoolean("isPortal", false)) {
			return hibernateDao.createQuery(
					"from " + super.getEntityClassName(Menu.class.getName())
							+ " where pmenuid=? and syspath=? order by sortno",
					new Object[] { menuId, SysConfig.getSysConfig("curSyspathId", "sysmg") }).list();
		}
		return hibernateDao.createQuery(
				"from " + getEntityClassName(Menu.class.getName()) + " where pmenuid=? order by sortno",
				new Object[] { menuId }).list();
	}

	public boolean checkAllAccess(Long menuid, Long positionid, String dimensiontype) {
		List<DataAccessDimension> list = hibernateDao.createQuery(
				"from DataAccessDimension dad where dad.menuid=? and dad.positionid=? and dad.dimensiontype=?",
				new Object[] { menuid, positionid, dimensiontype }).list();
		if (!ValidateUtil.isEmpty(list)) {
			if (list.size() == 1) {
				DataAccessDimension dad = (DataAccessDimension) list.get(0);
				IOrg org = organizationEntityService.getDepartByPositionId(dad.getPositionid());
				if ("0".equals(dad.getAllaccess())) {
					return true;
				}
				return false;
			}

			return false;
		}

		return false;
	}
}
