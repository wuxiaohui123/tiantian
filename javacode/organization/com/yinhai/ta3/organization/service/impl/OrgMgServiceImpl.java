package com.yinhai.ta3.organization.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.hibernate.Query;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.organization.api.IOrgService;
import com.yinhai.ta3.organization.api.IPositionService;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IOrgOpLogService;
import com.yinhai.ta3.system.org.domain.ManagerMg;
import com.yinhai.ta3.system.org.domain.ManagerMgId;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.OrgMg;
import com.yinhai.ta3.system.org.domain.OrgMgId;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;

public class OrgMgServiceImpl extends OrgBaseService implements IOrgMgService {

	private IOrgService orgService;
	private IPositionService positionService;
	private IOrgOpLogService orgOpLogService;
	private IGrantService grantService;
	private OrganizationEntityService organizationEntityService;

	public Org createOrg(ParamDTO dto) {
		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(),
				dto.getAsLong("porgid"), "01", "01", "创建组织" + dto.getAsString("orgname"));

		Org org = (Org) dto.toDomainObject(getEntityClassName(Org.class));

		if (!ValidateUtil.isEmpty(org.getPorgid())) {
			IOrg checkOrg = organizationEntityService.getDepartBySameLevelDepartName(org.getPorgid(), org.getOrgname());
			if (!ValidateUtil.isEmpty(checkOrg)) {
				throw new AppException("在同�?��组织下不能拥有相同的组织名称", "orgname");
			}
		}

		ParamDTO newDto = new ParamDTO();
		Org porg = (Org) newDto.toDomainObject(getEntityClassName(Org.class));
		porg.setOrgid(dto.getAsLong("porgid"));
		org.setpOrg(porg);
		if (ValidateUtil.isEmpty(org.getpOrg().getOrgid()))
			throw new AppException("错误的组织编号");
		if (ValidateUtil.isEmpty(org.getOrgname())) {
			throw new AppException("错误的组织名称");
		}
		if (dto.getUserInfo() == null) {
			throw new AppException("错误的登录用户");
		}
		if (ValidateUtil.isEmpty(dto.getAsLong("orglevel"))) {
			org.setOrglevel(Long.valueOf(1L));
		} else {
			org.setOrglevel(Long.valueOf(dto.getAsLong("orglevel").longValue() + 1L));
		}

		if ("0".equals(dto.getAsString("isleaf"))) {
			Org porg2 = (Org) organizationEntityService.getDepart(porg.getOrgid());
			porg2.setIsleaf("1");
			hibernateDao.update(porg2);
		}

		org.setCreateuser(dto.getUserInfo().getUserid());
		org.setCreatetime(timeService.getSysTimestamp());

		String isOpenAgencies = SysConfig.getSysConfig("isOpenAgencies", "false");
		if ("false".equals(isOpenAgencies)) {
			String yab139 = dto.getAsString("yab139");
			org.setYab003(yab139);
		}

		Org createOrg = orgService.createOrg(org, dto.getAsLong("porgid"));

		OrgMgId id = new OrgMgId();
		id.setOrgid(createOrg.getOrgid().longValue());
		id.setPositionid(dto.getUserInfo().getNowPosition().getPositionid().longValue());
		OrgMg orgMg = new OrgMg();
		orgMg.setId(id);
		hibernateDao.save(orgMg);

		saveManagerDeputy(createOrg, dto);

		orgOpLogService.logOrgOp(getLongSeq(), dto.getUserInfo(), "01", createOrg, createOrg.getOrgnamepath());

		TaEventPublisher.publishEvent(new EventSource(createOrg, dto), "org_create");
		return org;
	}

	private void saveManagerDeputy(Org org, ParamDTO dto) {
		String positionids = dto.getAsString("orgmanager_deputy");
		if (ValidateUtil.isNotEmpty(positionids)) {
			String[] arr = positionids.split(",");
			for (String positionid : arr) {
				ManagerMgId id = new ManagerMgId();
				Long orgid = org.getOrgid();
				id.setOrgid(orgid.longValue());
				id.setPositionid(Long.valueOf(positionid).longValue());
				ManagerMg mg = new ManagerMg();
				mg.setId(id);
				hibernateDao.save(mg);
			}
		}
	}

	public void updateOrg(ParamDTO dto) {
		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(),
				dto.getAsLong("orgid"), "02", "01", "更新组织");

		Long batchNo = getLongSeq();
		dto.put("batchNo", batchNo);
		Long orgid = dto.getAsLong("orgid");
		if (ValidateUtil.isEmpty(orgid)) {
			throw new AppException("错误的组织编号");
		}
		Org orgNew = (Org) dto.toDomainObject(getEntityClassName(Org.class));
		Org orgOld = (Org) organizationEntityService.getDepart(orgid);

		String isOpenAgencies = SysConfig.getSysConfig("isOpenAgencies", "false");
		if (("false".equals(isOpenAgencies)) && (!StringUtil.equals(orgOld.getYab139(), orgNew.getYab139()))) {
			orgNew.setYab003(orgNew.getYab139());
		}

		String oldOrgName = orgOld.getOrgname();
		String nowOrgName = orgNew.getOrgname();
		String orgOldJson = orgOld.toJson();

		if ((nowOrgName == null) || ("".equals(nowOrgName))) {
			throw new AppException("组织名称不能为空");
		}

		if ((dto.isNotEmpty("effective")) && (!orgOld.getEffective().equals(orgNew.getEffective()))) {
			dto.put("batchNo", batchNo);
			if ("1".equals(orgNew.getEffective())) {
				unUseOrg(dto);
			} else
				reUseOrg(dto);
		}
		String oldNamepath;
		String newNamepath;
		if ((oldOrgName != null) && (oldOrgName.equals(nowOrgName))) {
			ReflectUtil.copyObjectToObjectNotNull(orgNew, orgOld);

			Long orgmanager = dto.getAsLong("orgmanager");
			if (ValidateUtil.isEmpty(orgmanager)) {
				orgOld.setOrgmanager(null);
			}
			orgService.updateOrg(orgOld, dto.getUserInfo().getUserid());

			orgOpLogService.logOrgOp(batchNo, dto.getUserInfo(), "02", orgNew, orgOldJson + "-->" + orgOld.toJson());
		} else {
			if (!ValidateUtil.isEmpty(orgOld.getPorgid())) {
				IOrg checkOrg = organizationEntityService
						.getDepartBySameLevelDepartName(orgOld.getPorgid(), nowOrgName);
				if (!ValidateUtil.isEmpty(checkOrg)) {
					throw new AppException("在同一级组织下不能拥有相同的组织名称", "orgname");
				}
			}

			oldNamepath = orgOld.getOrgnamepath();

			newNamepath = oldNamepath.substring(0, oldNamepath.indexOf(orgOld.getOrgname())) + orgNew.getOrgname();

			ReflectUtil.copyObjectToObjectNotNull(orgNew, orgOld);
			orgOld.setOrgnamepath(newNamepath);

			Long orgmanager = dto.getAsLong("orgmanager");
			if (ValidateUtil.isEmpty(orgmanager)) {
				orgOld.setOrgmanager(null);
			}
			hibernateDao.update(orgOld);

			orgOpLogService.logOrgOp(batchNo, dto.getUserInfo(), "02", orgNew, orgOldJson + "-->" + orgOld.toJson());

			List<IOrg> list = organizationEntityService.getDepartsByDepartId(orgOld.getOrgid());
			for (IOrg temp : list) {
				Org upOrg = (Org) temp;
				String oldNamePath = upOrg.getOrgnamepath();
				upOrg.setOrgnamepath(upOrg.getOrgnamepath().replaceFirst(oldNamepath, newNamepath));
				orgService.updateOrg(upOrg, dto.getUserInfo().getUserid());

				orgOpLogService.logOrgOp(batchNo, dto.getUserInfo(), "02", upOrg, "组织路径" + oldNamePath + "变更�?"
						+ upOrg.getOrgnamepath());
			}
		}

		String positionids = dto.getAsString("orgmanager_deputy");
		if (ValidateUtil.isNotEmpty(positionids)) {
			hibernateDao.createQuery("delete from ManagerMg mg where mg.id.orgid=?", new Object[] { orgid })
					.executeUpdate();
			saveManagerDeputy(orgNew, dto);
		} else {
			hibernateDao.createQuery("delete from ManagerMg mg where mg.id.orgid=?", new Object[] { orgid })
					.executeUpdate();
		}
		TaEventPublisher.publishEvent(new EventSource(orgOld, dto), "org_update");
	}

	public void checkOrgInOrgMg(Long positionid, Long orgid) {
		if (!Position.ADMIN_POSITIONID.equals(positionid)) {
			Object uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
					new Object[] { positionid, orgid }).uniqueResult();
			if (uniqueResult == null) {
				throw new AppException("该组织没有操作权限!");
			}
		}
	}

	public Org queryOrgNode(ParamDTO dto) {
		if (dto.isEmpty("orgid")) {
			throw new AppException("错误的组织编号");
		}

		return (Org) organizationEntityService.getDepart(dto.getAsLong("orgid"));
	}

	public String getMaxCostomNo(Long porgid) {
		if (porgid == null) {
			porgid = Org.ORG_ROOT_ID;
		}
		Object uniqueResult = hibernateDao.createQuery(
				"select max(o.costomno) from " + getEntityClassName(Org.class) + " o," + getEntityClassName(Org.class)
						+ " oo where o.pOrg.orgid=oo.orgid and oo.orgid=?", new Object[] { porgid }).uniqueResult();
		if (uniqueResult == null) {
			return "0";
		}
		return String.valueOf(uniqueResult);
	}

	public Integer getMaxSortNo(ParamDTO pdto) {
		if (pdto.isNotEmpty("pOrgId")) {
			Query setLong = hibernateDao
					.getSession()
					.createQuery(
							"select max(o.sort) from " + getEntityClassName(Org.class)
									+ " o where o.pOrg.orgid=:porgid")
					.setLong("porgid", pdto.getAsLong("pOrgId").longValue());
			Object uniqueResult = setLong.uniqueResult();
			if (uniqueResult == null)
				return Integer.valueOf(0);
			return (Integer) uniqueResult;
		}
		throw new AppException("错误的组织编号");
	}

	public void unUseOrg(ParamDTO dto) {
		Long orgid = dto.getAsLong("orgid");
		if (orgid == null) {
			throw new AppException("错误的组织编号");
		}

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), orgid, "03", "01",
				"");
		Org org = (Org) organizationEntityService.getDepart(orgid);

		List<IOrg> orgs = organizationEntityService.getDepartsAndSelfByDepartId(orgid);
		Long batchNo = getLongSeq();
		for (IOrg temp : orgs) {
			Org o = (Org) temp;

			List<IPosition> positions = organizationEntityService.getAllPositionsByDepartId(o.getOrgid());
			for (IPosition p : positions) {
				positionService.unUsePosition(p.getPositionid(), dto.getUserInfo().getUserid(),
						timeService.getSysTimestamp());
				if (ValidateUtil.isEmpty(dto.getAsLong("batchNo"))) {
					orgOpLogService.logPositionOp(batchNo, dto.getUserInfo(), "12", p, "");
				} else {
					orgOpLogService.logPositionOp(dto.getAsLong("batchNo"), dto.getUserInfo(), "12", p, "");
				}
			}
			o.setEffective("1");
			hibernateDao.update(o);

			if (ValidateUtil.isEmpty(dto.getAsLong("batchNo"))) {
				orgOpLogService.logOrgOp(batchNo, dto.getUserInfo(), "03", o, "");
			} else {
				orgOpLogService.logOrgOp(dto.getAsLong("batchNo"), dto.getUserInfo(), "03", o, "");
			}
		}
		TaEventPublisher.publishEvent(new EventSource(org, dto), "org_unuse");
	}

	public void deleteOrg(ParamDTO dto) {
		Long orgid = dto.getAsLong("orgid");
		if (orgid == null) {
			throw new AppException("错误的组织编号");
		}

		checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), orgid, "18", "01",
				"");

		List<IOrg> list = getDepartsAndSelfByDepartId(orgid);

		Long batchNo = getLongSeq();
		for (IOrg temp : list) {
			Org org = (Org) temp;
			Long cOrgid = org.getOrgid();
			hibernateDao.createQuery("delete from OrgMg om where om.id.orgid=?", new Object[] { cOrgid })
					.executeUpdate();
			hibernateDao.createQuery("delete from ManagerMg mm where mm.id.orgid=?", new Object[] { cOrgid })
					.executeUpdate();

			hibernateDao.createQuery(
					"update " + getEntityClassName(User.class) + " u set u.destory=? where u.directorgid=?",
					new Object[] { "0", cOrgid }).executeUpdate();

			List<Position> listp = hibernateDao
					.createQuery(
							"select p from "
									+ getEntityClassName(User.class)
									+ " u,UserPosition up,"
									+ getEntityClassName(Position.class)
									+ " p where u.userid=up.id.tauser.userid and up.id.taposition.positionid=p.positionid and p.taorg.orgid=u.directorgid and p.positiontype=? and p.taorg.orgid=?",
							new Object[] { "2", cOrgid }).list();
			for (Position position : listp) {
				position.setEffective("1");
				hibernateDao.update(position);
			}
			hibernateDao
					.createQuery(
							"delete from UserPosition up where up.id.taposition.positionid in(select p.positionid from "
									+ getEntityClassName(Position.class) + " p where p.taorg.orgid=?)",
							new Object[] { cOrgid }).executeUpdate();

			List<IPosition> ps = organizationEntityService.getAllPositionsByDepartId(cOrgid);
			for (Iterator i$ = ps.iterator(); i$.hasNext();) {
				IPosition p;
				p = (IPosition) i$.next();
				List<PositionAuthrity> pas = hibernateDao.createQuery(
						"from PositionAuthrity pa where pa.id.taposition.positionid=?",
						new Object[] { p.getPositionid() }).list();
				for (PositionAuthrity pa : pas) {
					grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(),
							p.getPositionid(), dto.getUserInfo().getUserid(), timeService.getSysTimestamp(), "0", "6"));
				}
			}

			
			if (org.getOrgid().equals(orgid)) {
				List<Org> listc = querySubOrgs(org.getPorgid(), false, false, "-1");
				if (listc.size() == 1) {
					Org porg = org.getpOrg();
					porg.setIsleaf("0");
					hibernateDao.update(porg);
				}
			}
			org.setDestory("0");
			hibernateDao.update(org);

			orgOpLogService.logOrgOp(batchNo, dto.getUserInfo(), "18", org, org.getOrgnamepath());
			TaEventPublisher.publishEvent(new EventSource(org, dto), "org_delete");
		}
	}

	private List<IOrg> getDepartsAndSelfByDepartId(Long departId) {
		StringBuffer hql = null;
		hql = new StringBuffer("select so from " + super.getEntityClassName(Org.class.getName()) + " o,"
				+ super.getEntityClassName(Org.class.getName()) + " so ");
		Long curPositionId = WebUtil.getUserInfo(ServletActionContext.getRequest()).getNowPosition().getPositionid();
		if (IPosition.ADMIN_POSITIONID.equals(curPositionId)) {
			hql.append("where 1=1").append(" and o.orgid=?").append(" and (so.destory is null or so.destory=?)")
					.append(" and so.orgidpath like o.orgidpath||'%'").append(" order by so.sort");

			return hibernateDao.createQuery(hql.toString(), new Object[] { departId, "1" }).list();
		}
		hql.append(",OrgMg om").append(" where 1=1").append(" and om.id.orgid=so.orgid")
				.append(" and om.id.positionid=?").append(" and o.orgid=?")
				.append(" and (so.destory is null or so.destory=?)").append(" and so.orgidpath like o.orgidpath||'%'")
				.append(" order by so.sort");

		return hibernateDao.createQuery(hql.toString(), new Object[] { curPositionId, departId, "1" }).list();
	}

	public void reUseOrg(ParamDTO dto) {
		Long id = dto.getAsLong("orgid");
		if (id == null)
			throw new AppException("错误的组织编号");
		Org org = (Org) organizationEntityService.getDepart(id);

		if ("1".equals(org.getpOrg().getEffective())) {
			throw new AppException("上级组织已禁用");
		}
		org.setEffective("0");
		hibernateDao.update(org);
		Long batchNo = getLongSeq();

		List<IPosition> positions = organizationEntityService.getPerPositionsByDepartId(org.getOrgid(),
				Boolean.valueOf(false));
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p from ").append(super.getEntityClassName(Position.class.getName())).append(" p,")
				.append(super.getEntityClassName(Org.class.getName())).append(" o").append(" where 1=1")
				.append(" and o.orgid=?").append(" and p.taorg.orgid=o.orgid").append(" and p.effective=?")
				.append(" and (p.validtime is null or p.validtime >=?)").append(" and p.positiontype=?");

		List<IPosition> pubpositions = hibernateDao.createQuery(hql.toString(),
				new Object[] { org.getOrgid(), "1", super.getSysDate(), "1" }).list();
		positions.addAll(pubpositions);
		for (IPosition p : positions) {
			positionService.reUsePosition(p.getPositionid(), dto.getUserInfo().getUserid(),
					timeService.getSysTimestamp());
			if (ValidateUtil.isEmpty(dto.getAsLong("batchNo"))) {
				orgOpLogService.logPositionOp(batchNo, dto.getUserInfo(), "15", p, "");
			} else {
				orgOpLogService.logPositionOp(dto.getAsLong("batchNo"), dto.getUserInfo(), "15", p, "");
			}
		}

		if (ValidateUtil.isEmpty(dto.getAsLong("batchNo"))) {
			orgOpLogService.logOrgOp(batchNo, dto.getUserInfo(), "17", org, "");
		} else {
			orgOpLogService.logOrgOp(dto.getAsLong("batchNo"), dto.getUserInfo(), "17", org, "");
		}
		TaEventPublisher.publishEvent(new EventSource(org, dto), "org_use");
	}

	public List<Org> querySubOrgs(Long porgid, boolean showChildren, boolean showSelf, String effective) {
		if (porgid == null)
			throw new AppException("错误的组织编号");
		if (ValidateUtil.isEmpty(effective))
			effective = "-1";
		return orgService.querySubOrgs(porgid, showChildren, showSelf, effective);
	}

	public List<Long> queryPositionCouldManageOrgIds(Long positionid) {
		if (positionid == null) {
			throw new AppException("错误的岗位编号");
		}
		return hibernateDao.createQuery("select om.id.orgid from OrgMg om where om.id.positionid=?",
				new Object[] { positionid }).list();
	}

	public boolean sortOrg(List<Long> sortidslong, Long operator) {
		if (ValidateUtil.isEmpty(sortidslong))
			throw new AppException("错误的组织排序编号");
		return orgService.ascSortOrg(sortidslong, operator);
	}

	public List<UserInfoVO> getUserInfo(ParamDTO dto) {
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,o.orgnamepath,u.userid,u.name,u.sex,u.loginid) from ")
				.append(getEntityClassName(Org.class)).append(" o").append(",")
				.append(getEntityClassName(Position.class)).append(" p").append(",UserPosition up,")
				.append(getEntityClassName(User.class)).append(" u").append(" where o.orgid=p.taorg.orgid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and p.effective=?")
				.append(" and p.positiontype=?").append(" and up.id.tauser.userid=u.userid").append(" and u.islock=?")
				.append(" and u.effective=?").append(" and p.positionid <> ?");

		String loginid = dto.getAsString("loginid");
		if (ValidateUtil.isNotEmpty(loginid)) {
			hql.append(" and u.loginid=:loginid");
		}
		String username = dto.getAsString("username");
		if (ValidateUtil.isNotEmpty(username)) {
			hql.append(" and u.name like :username");
		}
		Long orgid = dto.getAsLong("orgid");
		if (!ValidateUtil.isEmpty(orgid)) {
			hql.append(" and o.orgid=:orgid");
		}
		hql.append(" order by o.orgnamepath");
		Query query = hibernateDao.createQuery(hql.toString(), new Object[] { "0", "2", "0", "0",
				IPosition.ADMIN_POSITIONID });
		if (ValidateUtil.isNotEmpty(loginid)) {
			query.setString("loginid", loginid);
		}
		if (ValidateUtil.isNotEmpty(username)) {
			query.setString("username", "%" + username + "%");
		}
		if (!ValidateUtil.isEmpty(orgid)) {
			query.setLong("orgid", orgid.longValue());
		}
		return query.list();
	}

	public Map getDeputyInfo(Long orgid) {
		List<ManagerMg> list = hibernateDao
				.createQuery("from ManagerMg mg where mg.id.orgid=?", new Object[] { orgid }).list();
		Map map = new HashMap();
		StringBuffer sbid = new StringBuffer();
		StringBuffer sbname = new StringBuffer();
		for (ManagerMg mg : list) {
			sbid.append(mg.getId().getPositionid()).append(",");
			Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class),
					Long.valueOf(mg.getId().getPositionid()));
			sbname.append(p.getPositionname()).append(",");
		}
		if (sbid.toString().endsWith(",")) {
			sbid.deleteCharAt(sbid.length() - 1);
		}
		if (sbname.toString().endsWith(",")) {
			sbname.deleteCharAt(sbname.length() - 1);
		}
		map.put("orgmanager_deputy", sbid);
		map.put("orgmanager_deputy_name", sbname);
		return map;
	}

	public List getManagers(String managerType, Long chief, String deputies) {
		List list = new ArrayList();

		if ("1".equals(managerType)) {
			if (!ValidateUtil.isEmpty(chief)) {

				StringBuffer hql = new StringBuffer();
				hql.append(
						"select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,o.orgnamepath,u.userid,u.name,u.sex,u.loginid) from ")
						.append(getEntityClassName(Org.class)).append(" o").append(",")
						.append(getEntityClassName(Position.class)).append(" p").append(",UserPosition up,")
						.append(getEntityClassName(User.class)).append(" u").append(" where o.orgid=p.taorg.orgid")
						.append(" and p.positionid=up.id.taposition.positionid").append(" and p.effective=?")
						.append(" and up.id.tauser.userid=u.userid").append(" and u.islock=?")
						.append(" and u.effective=?").append(" and p.positionid=?");

				list = hibernateDao.createQuery(hql.toString(), new Object[] { "0", "0", "0", chief }).list();
			}
		} else if (("2".equals(managerType)) && (!ValidateUtil.isEmpty(deputies))) {

			String[] d = deputies.split(",");
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.positionid,o.orgnamepath,u.userid,u.name,u.sex,u.loginid) from ")
					.append(getEntityClassName(Org.class)).append(" o").append(",")
					.append(getEntityClassName(Position.class)).append(" p").append(",UserPosition up,")
					.append(getEntityClassName(User.class)).append(" u").append(" where o.orgid=p.taorg.orgid")
					.append(" and p.positionid=up.id.taposition.positionid").append(" and p.effective=?")
					.append(" and up.id.tauser.userid=u.userid").append(" and u.islock=?").append(" and u.effective=?")
					.append(" and p.positionid in(");

			for (int i = 0; i < d.length; i++) {
				Long positionid = Long.valueOf(d[i]);
				if (i < d.length - 1) {
					hql.append(positionid).append(",");
				} else {
					hql.append(positionid);
				}
			}
			hql.append(")");
			list = hibernateDao.createQuery(hql.toString(), new Object[] { "0", "0", "0" }).list();
		}

		return list;
	}

	public List<Org> queryAffiliatedOrgs(Long userid) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct o from ").append(getEntityClassName(Org.class)).append(" o,")
				.append(getEntityClassName(Position.class)).append(" p,").append("UserPosition up,")
				.append(getEntityClassName(User.class)).append(" u").append(" where o.orgid=p.taorg.orgid")
				.append(" and p.positionid=up.id.taposition.positionid").append(" and p.positiontype=?")
				.append(" and up.id.tauser.userid=u.userid").append(" and u.userid=?")
				.append(" and u.directorgid<>o.orgid");

		return hibernateDao.createQuery(hql.toString(), new Object[] { "2", userid }).list();
	}

	public IOrg getOrgByOrgName(String orgnamepath, String orgname) {
		if (ValidateUtil.isNotEmpty(orgnamepath)) {
			return (IOrg) hibernateDao.createQuery(
					"from " + getEntityClassName(Org.class)
							+ " o where o.orgname=? and (o.destory is null or o.destory=?) and o.orgnamepath=?",
					new Object[] { orgname, "1", orgnamepath }).uniqueResult();
		}
		List<Org> list = hibernateDao
				.createQuery(
						"from " + getEntityClassName(Org.class)
								+ " o where o.orgname=? and (o.destory is null or o.destory=?)",
						new Object[] { orgname, "1" }).list();
		if (!ValidateUtil.isEmpty(list)) {
			return (IOrg) list.get(0);
		}
		return null;
	}

	public List<IOrg> queryOrgsByOrgName(String orgnametemp) {
		if (ValidateUtil.isEmpty(orgnametemp)) {
			throw new AppException("组织名称不能为空，请输入后再进行查询");
		}
		Query query = hibernateDao.createQuery("from " + getEntityClassName(Org.class)
				+ " o where o.orgname like :orgnametemp and (o.destory is null or o.destory=:destory)", new Object[0]);
		query.setString("orgnametemp", "%" + orgnametemp + "%");
		query.setString("destory", "1");
		return query.list();
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	public SimpleDao getHibernateDao() {
		return hibernateDao;
	}

	public ITimeService getTimeService() {
		return timeService;
	}

	public IOrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(IOrgService orgService) {
		this.orgService = orgService;
	}

	public void setPositionService(IPositionService positionService) {
		this.positionService = positionService;
	}

	public void setOrgOpLogService(IOrgOpLogService orgOpLogService) {
		this.orgOpLogService = orgOpLogService;
	}

	public void setGrantService(IGrantService grantService) {
		this.grantService = grantService;
	}

	public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
		this.organizationEntityService = organizationEntityService;
	}

}
