package com.yinhai.ta3.sysapp.syslogmg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.log.IIllegalOperationLog;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.OrgLogInfoVO;
import com.yinhai.ta3.system.org.domain.OrgOpLog;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class IllegalOperationLogImpl extends BaseService implements IIllegalOperationLog {

	private SimpleDao hibernateDao;

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public void saveIllegalOperationLog(Long batchNo, Long userid, Long positionid, String opttype, String opobjecttype, Long optid,
			String changcontent) {
		OrgOpLog log = new OrgOpLog();
		log.setBatchno(batchNo);
		log.setIspermission("1");
		log.setOpuser(userid);
		log.setOpposition(positionid);
		log.setOptime(getSysTimestamp());
		log.setOpbody(opobjecttype);
		log.setOptype(opttype);
		log.setOpsubjekt(optid);
		log.setChangcontent(changcontent);
		hibernateDao.save(log);
	}

	public void saveIllegalOperationMenuLog(Long batchNo, Long userid, Long positionid, String opttype, String opobjecttype, Long menuid,
			Long opPositionid) {
		OrgOpLog log = new OrgOpLog();
		log.setBatchno(batchNo);
		log.setIspermission("1");
		log.setOpuser(userid);
		log.setOpposition(positionid);
		log.setOptime(getSysTimestamp());
		log.setOpbody(opobjecttype);
		log.setOptype(opttype);
		log.setOpsubjekt(menuid);
		IPosition p = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), opPositionid);
		log.setChangcontent(p.getOrgnamepath() + "/" + p.getPositionname());
		hibernateDao.save(log);
	}

	@SuppressWarnings("unchecked")
	public List<OrgLogInfoVO> queryIllegalOperationLog(Date startDate, Date endDate) {
		List<OrgOpLog> list = null;
		StringBuffer sb = new StringBuffer();
		sb.append("from OrgOpLog ool where ool.ispermission=?");
		if ((!ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			sb.append(" and ool.optime between ? and ?").append(" order by ool.opuser");
			list = hibernateDao.createQuery(sb.toString(), new Object[] { "1", startDate, endDate }).list();
		} else if ((!ValidateUtil.isEmpty(startDate)) && (ValidateUtil.isEmpty(endDate))) {
			sb.append(" and ool.optime >= ?").append(" order by ool.opuser");
			list = hibernateDao.createQuery(sb.toString(), new Object[] { "1", startDate }).list();
		} else if ((ValidateUtil.isEmpty(startDate)) && (!ValidateUtil.isEmpty(endDate))) {
			sb.append(" and ool.optime <= ?").append(" order by ool.opuser");
			list = hibernateDao.createQuery(sb.toString(), new Object[] { "1", endDate }).list();
		} else {
			list = hibernateDao.createQuery(sb.toString(), new Object[] { "1" }).list();
		}
		List<OrgLogInfoVO> listvo = new ArrayList<OrgLogInfoVO>();
		for (OrgOpLog log : list) {
			OrgLogInfoVO vo = new OrgLogInfoVO();
			vo.setBatchno(log.getBatchno());
			vo.setOpsubjekt(log.getOpsubjekt());
			vo.setChangcontent(log.getChangcontent());

			vo.setOpbody(log.getOpbody());
			vo.setOptime(log.getOptime());
			vo.setOptype(log.getOptype());
			User opUser = (User) hibernateDao.getSession().get(getEntityClassName(User.class.getName()), log.getOpuser());
			vo.setOpusername(opUser.getName());
			Position opPosition = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), log.getOpposition());
			vo.setOppositionname(opPosition.getOrgnamepath() + "/" + opPosition.getPositionname());
			if ("01".equals(log.getOpbody())) {
				if ("18".equals(log.getOptype())) {
					vo.setChangcontent(log.getChangcontent());
				} else {
					Org o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), log.getOpsubjekt());
					if (ValidateUtil.isEmpty(o)) {
						vo.setOpsubjektname(log.getChangcontent());
					} else {
						vo.setOpsubjektname(o.getOrgnamepath());
					}
				}
				if ("-1".equals(log.getOptype())) {
					vo.setChangcontent("没有该组织的操作权限");
				}
			} else if ("04".equals(log.getOpbody())) {
				Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class.getName()), log.getOpsubjekt());
				if (!ValidateUtil.isEmpty(m)) {

					vo.setOpsubjektname(m.getMenunamepath());
				}
			} else if ("03".equals(log.getOpbody())) {
				Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class.getName()), log.getOpsubjekt());
				if (ValidateUtil.isEmpty(p)) {
					vo.setOpsubjektname(log.getChangcontent());
				} else {
					vo.setOpsubjektname(p.getOrgnamepath() + "/" + p.getPositionname());
				}
				if ("-1".equals(log.getOptype())) {
					vo.setChangcontent("没有该岗位的操作权限");
				}
			} else if ("02".equals(log.getOpbody())) {
				User u = (User) hibernateDao.getSession().get(getEntityClassName(User.class.getName()), log.getOpsubjekt());
				if (ValidateUtil.isEmpty(u)) {
					vo.setOpsubjektname(log.getChangcontent());
				} else {
					vo.setOpsubjektname(u.getName());
				}
				if ("-1".equals(log.getOptype())) {
					vo.setChangcontent("没有该用户的操作权限");
				}
			}
			listvo.add(vo);
		}
		return listvo;
	}
}
