package com.yinhai.ta3.organization.service.impl;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.service.IOrgOpLogService;
import com.yinhai.ta3.system.org.domain.OrgOpLog;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class OrgOpLogServiceImpl implements IOrgOpLogService {

	private SimpleDao hibernateDao;
	private ITimeService timeService;

	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	public void setHibernateDao(SimpleDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public Long logOrgOp(Long batchNo, IUser opUser, String opType, IOrg org, String opObjectContent) {
		OrgOpLog log = new OrgOpLog();

		log.setBatchno(batchNo);

		log.setOptype(opType);

		log.setOpbody("01");

		log.setOpsubjekt(org.getOrgid());

		log.setChangcontent(opObjectContent);
		log.setOptime(timeService.getSysTimestamp());
		log.setOpuser(opUser.getUserid());
		log.setOpposition(opUser.getNowPosition().getPositionid());
		return (Long) hibernateDao.save(log);
	}

	public Long logUserOp(Long batchNo, IUser opUser, String opType, IUser user, String opObjectContent) {
		OrgOpLog log = new OrgOpLog();

		log.setBatchno(batchNo);

		log.setOptype(opType);

		log.setOpbody("02");

		log.setOpsubjekt(user.getUserid());

		log.setChangcontent(opObjectContent);
		log.setOptime(timeService.getSysTimestamp());
		if (ValidateUtil.isEmpty(opUser)) {
			log.setOpuser(user.getUserid());
			Long positionid = (Long) hibernateDao
					.createQuery(
							"select up.id.taposition.positionid from "
									+ SysConfig.getSysConfig(Position.class.getName(), Position.class.getName())
									+ " p,UserPosition up "
									+ "where p.positionid=up.id.taposition.positionid and up.mainposition=? and up.id.tauser.userid=?",
							new Object[] { "1", user.getUserid() }).uniqueResult();

			log.setOpposition(positionid);
		} else {
			log.setOpuser(opUser.getUserid());
			log.setOpposition(opUser.getNowPosition().getPositionid());
		}
		return (Long) hibernateDao.save(log);
	}

	public Long logPositionOp(Long batchNo, IUser opUser, String opType, IPosition position, String opObjectContent) {
		OrgOpLog log = new OrgOpLog();
		log.setBatchno(batchNo);
		log.setOptype(opType);
		if (opType.equals("10")) {
			log.setInfluencebodytype("01");
			log.setInfluencebody(position.getOrgid());
		}

		log.setOpbody("03");
		log.setOpsubjekt(position.getPositionid());
		log.setChangcontent(opObjectContent);
		log.setOptime(timeService.getSysTimestamp());
		log.setOpuser(opUser.getUserid());
		log.setOpposition(opUser.getNowPosition().getPositionid());
		return (Long) hibernateDao.save(log);
	}

	public Long logPermisstionOp(Long batchNo, IUser opUser, String opType, Menu permissionSource, IPosition position) {
		OrgOpLog log = new OrgOpLog();

		log.setBatchno(batchNo);

		log.setOptype(opType);

		log.setInfluencebodytype("03");

		log.setInfluencebody(position.getPositionid());

		log.setOpbody("04");

		log.setOpsubjekt(permissionSource.getMenuid());
		log.setChangcontent(position.getPositionname() + "(" + position.getPositionid() + ")");
		log.setOptime(timeService.getSysTimestamp());
		log.setOpuser(opUser.getUserid());
		log.setOpposition(opUser.getNowPosition().getPositionid());
		return (Long) hibernateDao.save(log);
	}

}
