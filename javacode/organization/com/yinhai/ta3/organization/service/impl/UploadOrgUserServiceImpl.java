package com.yinhai.ta3.organization.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IOrgService;
import com.yinhai.ta3.organization.service.IOrgMgService;
import com.yinhai.ta3.organization.service.IUploadOrgUserService;
import com.yinhai.ta3.organization.service.IUserMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.OrgMg;
import com.yinhai.ta3.system.org.domain.OrgMgId;
import com.yinhai.ta3.system.org.domain.UploadOrgUserVO;

public class UploadOrgUserServiceImpl extends OrgBaseService implements IUploadOrgUserService {

	private IUserMgService userMgService;
	private IOrgService orgService;
	private IOrgMgService orgMgService;

	public void setUserMgService(IUserMgService userMgService) {
		this.userMgService = userMgService;
	}

	public void setOrgService(IOrgService orgService) {
		this.orgService = orgService;
	}

	public void setOrgMgService(IOrgMgService orgMgService) {
		this.orgMgService = orgMgService;
	}

	public void detachUploadOrgAndUser(List<UploadOrgUserVO> list, ParamDTO dto) {
		Long directorgid = null;
		for (UploadOrgUserVO vo : list) {
			dto = changeDtoByVO(vo, dto);

			if (((!ValidateUtil.isEmpty(vo.getOrgname())) && (!ValidateUtil.isEmpty(vo.getPorgname()))) || (!ValidateUtil.isEmpty(vo.getLoginid()))) {

				if ((!ValidateUtil.isEmpty(vo.getPorgname())) && (!ValidateUtil.isEmpty(vo.getOrgname())) && (!ValidateUtil.isEmpty(vo.getOrgtype()))) {
					Org porg = (Org) orgMgService.getOrgByOrgName(vo.getTempPath(), vo.getPorgname());
					if (porg == null) {
						throw new AppException("父级组织为空，不能在空的组织下添加组织，父组织为空的组织是：" + vo.getOrgname());
					}
					List list1 = hibernateDao.createQuery(
							"from " + getEntityClassName(Org.class.getName())
									+ " o where o.pOrg.orgnamepath=? and o.orgname=? and (o.destory is null or o.destory=?)",
							new Object[] { porg.getOrgnamepath(), vo.getOrgname(), "1" }).list();
					if (list1 != null) {
						throw new AppException("组织:" + porg.getOrgname() + "下已经有了组织：" + vo.getOrgname() + ",不能在同级组织下添加相同名称的组织");
					}
					if ("02".equals(porg.getOrgtype())) {
						if ("01".equals(vo.getOrgtype())) {
							throw new AppException("部门(" + porg.getOrgname() + ")下面只能建部门和组，不能建机�?" + vo.getOrgname() + ")");
						}
					} else if (("04".equals(porg.getOrgtype())) && (!"04".equals(vo.getOrgtype()))) {
						throw new AppException("�?" + porg.getOrgname() + ")下面只能建组，不能建机构或�?部门(" + vo.getOrgname() + ")");
					}

					if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
						Object uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
								new Object[] { dto.getUserInfo().getNowPosition().getPositionid(), porg.getOrgid() }).uniqueResult();
						if (uniqueResult == null) {
							throw new AppException("您没有该组织的父组织" + porg.getOrgname() + "）的操作权限");
						}
					}
					Org org = (Org) dto.toDomainObject(getEntityClassName(Org.class));
					org.setpOrg(porg);
					org.setOrglevel(Long.valueOf(porg.getOrglevel().longValue() + 1L));
					if ("0".equals(porg.getIsleaf())) {
						porg.setIsleaf("1");
						hibernateDao.update(porg);
					}
					org.setOrgtype(vo.getOrgtype());
					org.setOrgname(vo.getOrgname());
					org.setEffective("0");
					org.setCreateuser(dto.getUserInfo().getUserid());
					org.setCreatetime(timeService.getSysTimestamp());
					if (ValidateUtil.isEmpty(org.getYab003())) {
						org.setYab003("9999");
					}
					if (ValidateUtil.isEmpty(org.getYab139())) {
						org.setYab139("9999");
					}
					Org newOrg = orgService.createOrg(org, porg.getOrgid());

					OrgMgId id = new OrgMgId();
					id.setOrgid(newOrg.getOrgid().longValue());
					id.setPositionid(dto.getUserInfo().getNowPosition().getPositionid().longValue());
					OrgMg om = new OrgMg(id);
					hibernateDao.save(om);
					directorgid = newOrg.getOrgid();
				} else if ((!ValidateUtil.isEmpty(vo.getLoginid())) && (!ValidateUtil.isEmpty(vo.getPassword()))
						&& (!ValidateUtil.isEmpty(vo.getUsername()))) {
					dto.append("name", vo.getUsername());
					dto.append("createuser", dto.getUserInfo().getUserid());
					dto.append("orgid", directorgid);
					userMgService.createUser(dto);
				} else {
					throw new AppException("导入失败，请�?��excel格式的正确�?");
				}
			}
		}
	}

	public void detachUploadOrg(List<UploadOrgUserVO> list, ParamDTO dto) {
		for (UploadOrgUserVO vo : list) {
			dto = changeDtoByVO(vo, dto);

			if ((!ValidateUtil.isEmpty(vo.getOrgname())) && (!ValidateUtil.isEmpty(vo.getPorgname()))) {

				Org porg = (Org) orgMgService.getOrgByOrgName(vo.getTempPath(), vo.getPorgname());
				if (porg == null) {
					throw new AppException("父级组织为空，不能在空的组织下添加组织，父组织为空的组织是：" + vo.getOrgname());
				}
				if ("02".equals(porg.getOrgtype())) {
					if ("01".equals(vo.getOrgtype())) {
						throw new AppException("部门(" + porg.getOrgname() + ")下面只能建部门和组，不能建机�?" + vo.getOrgname() + ")");
					}
				} else if (("04".equals(porg.getOrgtype())) && (!"04".equals(vo.getOrgtype()))) {
					throw new AppException("�?" + porg.getOrgname() + ")下面只能建组，不能建机构或�?部门(" + vo.getOrgname() + ")");
				}

				Object uniqueResult = hibernateDao.createQuery("from OrgMg om where om.id.positionid=? and om.id.orgid=?",
						new Object[] { dto.getUserInfo().getNowPosition().getPositionid(), porg.getOrgid() }).uniqueResult();
				if ((!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) && (uniqueResult == null)) {
					throw new AppException("您没有操作该组织的父组织" + porg.getOrgname() + "）的操作权限");
				}
				if ((!ValidateUtil.isEmpty(vo.getPorgname())) && (!ValidateUtil.isEmpty(vo.getOrgname())) && (!ValidateUtil.isEmpty(vo.getOrgtype()))) {
					Org org = (Org) dto.toDomainObject(getEntityClassName(Org.class));
					List list1 = hibernateDao.createQuery(
							"from " + getEntityClassName(Org.class.getName())
									+ " o where o.pOrg.orgnamepath=? and o.orgname=? and (o.destory is null or o.destory=?)",
							new Object[] { porg.getOrgnamepath(), vo.getOrgname(), "1" }).list();
					if (list1 != null) {
						throw new AppException("组织:" + porg.getOrgname() + "下已经有了组织：" + vo.getOrgname() + ",不能在同级组织下添加相同名称的组织");
					}
					org.setpOrg(porg);
					org.setOrglevel(Long.valueOf(porg.getOrglevel().longValue() + 1L));
					if ("0".equals(porg.getIsleaf())) {
						porg.setIsleaf("1");
						hibernateDao.update(porg);
					}
					org.setOrgtype(vo.getOrgtype());
					org.setOrgname(vo.getOrgname());
					org.setEffective("0");
					org.setCreateuser(dto.getUserInfo().getUserid());
					org.setCreatetime(timeService.getSysTimestamp());
					if (ValidateUtil.isEmpty(org.getYab003())) {
						org.setYab003("9999");
					}
					if (ValidateUtil.isEmpty(org.getYab139())) {
						org.setYab139("9999");
					}
					Org createOrg = orgService.createOrg(org, porg.getOrgid());

					OrgMgId id = new OrgMgId();
					id.setOrgid(createOrg.getOrgid().longValue());
					id.setPositionid(dto.getUserInfo().getNowPosition().getPositionid().longValue());
					OrgMg om = new OrgMg(id);
					hibernateDao.save(om);
				}
			}
		}
	}

	public void detachUploadUser(List<UploadOrgUserVO> list, ParamDTO dto) {
		for (UploadOrgUserVO vo : list) {
			dto = changeDtoByVO(vo, dto);

			if ((!ValidateUtil.isEmpty(vo.getLoginid())) && (!ValidateUtil.isEmpty(vo.getUsername()))) {

				Org org = (Org) orgMgService.getOrgByOrgName(vo.getTempPath(), vo.getOrgname());
				if (org == null) {
					throw new AppException("直属组织为空，为空的组织是：" + vo.getOrgname());
				}
				if ("04".equals(org.getOrgtype())) {
					throw new AppException("直属组织类型为组，组不能作为直属组织，出错的人员为：" + vo.getUsername());
				}
				if ((!ValidateUtil.isEmpty(vo.getLoginid())) && (!ValidateUtil.isEmpty(vo.getPassword()))
						&& (!ValidateUtil.isEmpty(vo.getUsername()))) {
					dto.append("name", vo.getUsername());
					dto.append("createuser", dto.getUserInfo().getUserid());
					dto.append("orgid", org.getOrgid());
					userMgService.createUser(dto);
				}
			}
		}
	}

	private ParamDTO changeDtoByVO(UploadOrgUserVO vo, ParamDTO dto) {
		if (vo.getClass().getSuperclass().getName().endsWith(".UploadOrgUserVO")) {
			Field[] fields = vo.getClass().getSuperclass().getDeclaredFields();
			for (Field field : fields) {
				String fieldname = field.getName();
				try {
					PropertyDescriptor pd = new PropertyDescriptor(fieldname, vo.getClass().getSuperclass());
					String fieldvalue = (String) pd.getReadMethod().invoke(vo, new Object[0]);
					dto.append(fieldname, fieldvalue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Field[] fields = vo.getClass().getDeclaredFields();
		for (Field field : fields) {
			String fieldname = field.getName();
			try {
				PropertyDescriptor pd = new PropertyDescriptor(fieldname, vo.getClass());
				String fieldvalue = (String) pd.getReadMethod().invoke(vo, new Object[0]);
				dto.append(fieldname, fieldvalue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dto;
	}

}
