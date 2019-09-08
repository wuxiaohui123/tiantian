package com.yinhai.ta3.organization.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

import org.hibernate.Query;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.Finder;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IDataAccessApi;
import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.DateUtil;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.organization.api.IPositionService;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IOrgOpLogService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.DataAccessDimension;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.PositionAuthrityId;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.SharePosition;
import com.yinhai.ta3.system.org.domain.SharePositionId;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.UserPosition;
import com.yinhai.ta3.system.sysapp.domain.Menu;
import org.hibernate.metadata.ClassMetadata;

public class PositionMgService extends OrgBaseService implements IPositionMgService {

    private IPositionService positionService;
    private IGrantService grantService;
    private IOrgOpLogService orgOpLogService;
    private OrganizationEntityService organizationEntityService;
    private IDataAccessApi api;

    public void setOrgOpLogService(IOrgOpLogService orgOpLogService) {
        this.orgOpLogService = orgOpLogService;
    }

    public void setGrantService(IGrantService grantService) {
        this.grantService = grantService;
    }

    public void setPositionService(IPositionService positionService) {
        this.positionService = positionService;
    }

    public Position getPosition(Long positionid) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位id为空");
        }
        return (Position) organizationEntityService.getPositionByPositionId(positionid);
    }

    public Position createPosition(ParamDTO dto) {
        Position p = (Position) dto.toDomainObject(getEntityClassName(Position.class));
        Position parent = (Position) dto.getUserInfo().getNowPosition();
        p.setCreatepositionid(parent.getPositionid());
        p.setCreatetime(timeService.getSysTimestamp());
        p.setCreateuser(dto.getUserInfo().getUserid());
        p.setEffective("0");
        Position position = positionService.createPosition(p, dto.getAsLong("orgid"));

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), position.getPositionid(), "10", "03",
                "创建岗位，无该组织的操作权限");

        orgOpLogService.logPositionOp(dto.getAsLong("batchNo") == null ? getLongSeq() : dto.getAsLong("batchNo"), dto.getUserInfo(), "10", position,
                position.getOrgnamepath() + "/" + position.getPositionname());
        TaEventPublisher.publishEvent(new EventSource(position, dto), "position_create");
        return position;
    }

    public Position updatePosition(ParamDTO dto) {
        if (ValidateUtil.isEmpty(dto.getAsLong("editpositionid"))) {
            throw new AppException("岗位为空,不能编辑岗位");
        }

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), dto.getAsLong("editpositionid"), "11", "03",
                "无该岗位所在组织的操作权限");
        Position newPosition = (Position) dto.toDomainObject(getEntityClassName(Position.class));
        Position position = getPosition(dto.getAsLong("positionid"));
        String pJson = position.toJson();
        ReflectUtil.copyObjectToObjectNotNull(newPosition, position);
        Long orgid = dto.getAsLong("orgid");
        if (orgid != null) {
            Org org = (Org) organizationEntityService.getDepart(orgid);
            position.setOrgidpath(org.getOrgidpath());
            position.setOrgnamepath(org.getOrgnamepath());
            position.setTaorg(org);
        }
        positionService.updatePosition(position, dto.getUserInfo().getUserid());

        orgOpLogService.logPositionOp(Long.valueOf(getStringSeq()), dto.getUserInfo(), "11", position, pJson + "-->" + position.toJson());
        TaEventPublisher.publishEvent(new EventSource(position, dto), "position_update");
        return position;
    }

    public void removePosition(List<Key> pos, ParamDTO dto) {
        Long batchNo;
        if (!ValidateUtil.isEmpty(pos)) {
            batchNo = getLongSeq();
            for (Key key : pos) {
                Long positionId = key.getAsLong("positionid");

                Position p = getPosition(positionId);

                checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionId, "25", "03", "无该岗位所在组织的操作权限");

                Set<PositionAuthrity> pas = p.getTapositionauthrities();
                for (Iterator<PositionAuthrity> it = pas.iterator(); it.hasNext(); ) {
                    PositionAuthrity pa = (PositionAuthrity) it.next();
                    grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(), positionId, dto.getUserInfo()
                            .getUserid(), timeService.getSysTimestamp(), "0", "6"));
                }
                if ("1".equals(p.getIsshare())) {
                    List<SharePosition> sps = hibernateDao
                            .createQuery("from SharePosition sp where sp.id.spositionid=?", new Object[]{positionId}).list();
                    for (SharePosition sp : sps) {
                        Position dp = getPosition(Long.valueOf(sp.getId().getDpositionid()));
                        Set<PositionAuthrity> dpas = dp.getTapositionauthrities();
                        for (Iterator<PositionAuthrity> it = dpas.iterator(); it.hasNext(); ) {
                            PositionAuthrity pa = (PositionAuthrity) it.next();
                            grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(), dp.getPositionid(),
                                    dto.getUserInfo().getUserid(), timeService.getSysTimestamp(), "0", "6"));
                        }

                        List<Long> userids = hibernateDao.createQuery(
                                "select up.id.tauser.userid from UserPosition up where up.mainposition=? and up.id.taposition.positionid=?",
                                new Object[]{"1", dp.getPositionid()}).list();
                        for (Long userid : userids) {
                            IPosition position = organizationEntityService.getPositionByUserId(userid);
                            hibernateDao.createQuery(
                                    "update UserPosition up set up.mainposition=? where up.id.taposition.positionid=? and up.id.tauser.userid=?",
                                    new Object[]{"1", position.getPositionid(), userid}).executeUpdate();
                        }
                        hibernateDao.delete(sp);
                        hibernateDao.delete(dp);
                    }
                }
                List<Long> userids = hibernateDao.createQuery(
                        "select up.id.tauser.userid from UserPosition up where up.mainposition=? and up.id.taposition.positionid=?",
                        new Object[]{"1", p.getPositionid()}).list();
                for (Long userid : userids) {
                    IPosition position = organizationEntityService.getPositionByUserId(userid);
                    hibernateDao.createQuery(
                            "update UserPosition up set up.mainposition=? where up.id.taposition.positionid=? and up.id.tauser.userid=?",
                            new Object[]{"1", position.getPositionid(), userid}).executeUpdate();
                }
                hibernateDao.delete(p);
                orgOpLogService.logPositionOp(batchNo, dto.getUserInfo(), "25", p, p.getOrgnamepath() + "/" + p.getPositionname());
                TaEventPublisher.publishEvent(new EventSource(p, dto), "position_delete");
            }
        }
    }

    public void unUsePosition(Long positionId, ParamDTO dto, String effevtive) {
        Position position = getPosition(positionId);

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionId, "12", "03", "无该岗位所在组织的操作权限");
        if ("0".equals(effevtive)) {
            positionService.unUsePosition(positionId, dto.getUserInfo().getUserid(), timeService.getSysTimestamp());
        }
        orgOpLogService.logPositionOp(getLongSeq(), dto.getUserInfo(), "12", position, "");
        TaEventPublisher.publishEvent(new EventSource(position, dto), "position_unuse");
    }

    public void unUsePosition(List<Key> list, ParamDTO dto) {
        Long batchNo = getLongSeq();
        Timestamp timestamp = timeService.getSysTimestamp();
        for (Key key : list) {
            Long positionid = key.getAsLong("positionid");
            Position position = getPosition(positionid);

            checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "12", "03", "无该岗位所在组织的操作权限");
            String effective = key.getAsString("effective");
            if ("0".equals(effective))
                positionService.unUsePosition(positionid, dto.getUserInfo().getUserid(), timestamp);
            List<IPosition> sharePositions = organizationEntityService.getSharePositionBySPositionId(positionid);
            for (IPosition dposition : sharePositions) {
                positionService.unUsePosition(dposition.getPositionid(), dto.getUserInfo().getUserid(), timestamp);
            }

            orgOpLogService.logPositionOp(batchNo, dto.getUserInfo(), "12", position, "");
            TaEventPublisher.publishEvent(new EventSource(position, dto), "position_unuse");
        }
    }

    public void usePosition(Long positionId, ParamDTO dto, String effevtive) {
        Position position = getPosition(positionId);

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionId, "15", "03", "无该岗位所在组织的操作权限");
        if ("1".equals(effevtive)) {
            Org org = (Org) organizationEntityService.getDepartByPositionId(positionId);
            if ("1".equals(org.getEffective())) {
                throw new AppException("该岗位所在组织已被禁用，无法启用该岗位");
            }
            positionService.reUsePosition(positionId, dto.getUserInfo().getUserid(), timeService.getSysTimestamp());

            orgOpLogService.logPositionOp(getLongSeq(), dto.getUserInfo(), "15", position, "");
            TaEventPublisher.publishEvent(new EventSource(position, dto), "position_use");
        }
    }

    public void usePosition(List<Key> list, ParamDTO dto) {
        Long batchNo = Long.valueOf(getStringSeq());
        Timestamp timestamp = timeService.getSysTimestamp();
        for (Key key : list) {
            Long positionid = key.getAsLong("positionid");
            Position position = getPosition(positionid);

            checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "15", "03", "无该岗位所在组织的操作权限");
            String effective = key.getAsString("effective");
            if ("1".equals(effective)) {
                Org org = (Org) organizationEntityService.getDepartByPositionId(positionid);
                if ("1".equals(org.getEffective())) {
                    throw new AppException("该岗位所在组织已被禁用，无法启用该岗位");
                }
                positionService.reUsePosition(positionid, dto.getUserInfo().getUserid(), timeService.getSysTimestamp());
                List<IPosition> sharePositions = organizationEntityService.getSharePositionBySPositionId(positionid);
                for (IPosition dposition : sharePositions) {
                    positionService.reUsePosition(dposition.getPositionid(), dto.getUserInfo().getUserid(), timestamp);
                }

                orgOpLogService.logPositionOp(batchNo, dto.getUserInfo(), "15", position, "");
                TaEventPublisher.publishEvent(new EventSource(position, dto), "position_use");
            }
        }
    }

    public PageBean getDescendantsPositionsByCount(String gridId, ParamDTO dto) {
        if ("2".equals(dto.getAsString("positionType"))) {
            User u = (User) dto.toDomainObject(getEntityClassName(User.class));
            u.setName(dto.getAsString("username"));
            StringBuffer hql = new StringBuffer();
            hql.append(
                    "select distinct p from " + getEntityClassName(User.class) + " u, " + getEntityClassName(Position.class)
                            + " p , UserPosition up," + getEntityClassName(Org.class) + " o").append(" where u.userid=up.id.tauser.userid")
                    .append(" and up.id.taposition.positionid=p.positionid").append(" and p.taorg.orgid=o.orgid").append(" and p.positiontype=")
                    .append("2").append(" and (u.destory is null or u.destory=").append("1").append(")");

            if ((ValidateUtil.isNotEmpty(u.getEffective())) && (!"-1".equals(u.getEffective()))) {
                if ("0".equals(u.getEffective())) {
                    hql.append(" and p.effective=").append("0");
                } else if ("1".equals(u.getEffective())) {
                    hql.append(" and p.effective=").append("1");
                }
            }

            Field[] pField = u.getClass().getDeclaredFields();
            for (int i = 0; i < pField.length; i++) {
                String fieldName = pField[i].getName();
                if ((!"name".equals(fieldName)) && (!"loginid".equals(fieldName)) && (!"effective".equals(fieldName))) {
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor(pField[i].getName(), u.getClass());
                        Object invoke = pd.getReadMethod().invoke(u, new Object[0]);
                        if ((invoke != null) && ((invoke instanceof String)) && (!"-1".equals(invoke))) {
                            hql.append(" and u.").append(fieldName).append("='").append(invoke.toString().replace("'", "'")).append("'");
                        }

                        if ((invoke != null) && ((invoke instanceof Long))) {
                            hql.append(" and u.").append(fieldName).append("=").append(invoke);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            String username = dto.getAsString("username");
            if (ValidateUtil.isNotEmpty(username)) {
                hql.append(" and u.name like :username");
            }

            String loginids = dto.getAsString("loginids");
            if (ValidateUtil.isNotEmpty(loginids)) {
                hql.append(" and u.loginid in (");

                loginids = loginids.replace("，", ",");
                String[] temp = loginids.split(",");
                for (int i = 0; i < temp.length; i++) {
                    hql.append("'" + temp[i] + "'");
                    if (i < temp.length - 1) {
                        hql.append(",");
                    }
                }
                hql.append(")");
            }
            Long curPositionId = dto.getUserInfo().getNowPosition().getPositionid();
            if (!IPosition.ADMIN_POSITIONID.equals(curPositionId)) {
                hql.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=").append(curPositionId).append(")");
                hql.append(" and p.positionid not in(" + curPositionId + "," + IPosition.ADMIN_POSITIONID + ")");
            } else {
                hql.append(" and p.positionid <>").append(dto.getUserInfo().getNowPosition().getPositionid());
            }
            Long orgid = dto.getAsLong("orgid");
            if (orgid != null) {
                if ("0".equals(dto.getAsString("isDisDecPositions"))) {
                    Org o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), orgid);
                    hql.append(" and p.taorg.orgidpath like '").append(o.getOrgidpath()).append("%'").append(" and p.taorg.effective=").append("0");
                } else {
                    hql.append(" and p.taorg.orgid =:orgid ");
                }
            }

            hql.append(" order by p.orgidpath");
            Integer skipResults = Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
            Integer maxResults = Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
            PageBean pb = new PageBean();
            pb.setStart(skipResults);
            pb.setLimit(maxResults);
            Query query = hibernateDao.createQuery(hql.toString(), new Object[0]).setFirstResult(skipResults.intValue())
                    .setMaxResults(maxResults.intValue());
            Finder finder = Finder.create(hql.toString());
            String countHql = finder.getRowCountHql();
            Query queryCount = hibernateDao.createQuery(countHql, new Object[0]);
            if (ValidateUtil.isNotEmpty(username)) {
                query.setString("username", "%" + username + "%");
                queryCount.setString("username", "%" + username + "%");
            }
            if ((orgid != null) && (!"0".equals(dto.getAsString("isDisDecPositions")))) {
                query.setLong("orgid", orgid.longValue());
                queryCount.setLong("orgid", orgid.longValue());
            }

            List<Position> positions = query.list();
            Long total = (Long) queryCount.uniqueResult();
            pb.setTotal(Integer.valueOf(total.intValue()));
            List<PositionInfoVO> positionInfos = buildPositionInfos(positions, dto.getUserInfo(), "2");
            pb.setList(positionInfos);

            return pb;
        }
        if ("1".equals(dto.getAsString("positionType"))) {
            Position p = (Position) dto.toDomainObject(getEntityClassName(Position.class));
            p.setEffective(dto.getAsString("effective"));
            boolean isDisSubOrgs = false;

            if ("0".equals(dto.getAsString("isDisDecPositions"))) {
                isDisSubOrgs = true;
            }
            Integer skipResults = Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
            Integer maxResults = Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
            PageBean pb = positionService.queryPositions(p, dto.getUserInfo().getNowPosition().getPositionid(), dto.getAsLong("orgid"), isDisSubOrgs,
                    skipResults.intValue(), maxResults.intValue());
            pb.setGridId(gridId);
            pb.setList(buildPositionInfos(pb.getList(), dto.getUserInfo(), "1"));
            return pb;
        }
        return null;
    }

    public List<Position> getPositionsByOrgId(Long orgid) {
        return hibernateDao.createQuery(
                "from " + getEntityClassName(Position.class)
                        + " p where p.taorg.orgid=? and p.effective=? and (p.validtime is null or p.validtime >= ?)",
                new Object[]{orgid, "0", timeService.getSysDate()}).list();
    }

    public List<UserInfoVO> getHaveThePositionUsersByPositionId(Long positionid) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位为空,不能分配人员");
        }
        StringBuffer hql = new StringBuffer();
        hql.append("select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.orgnamepath,u.userid,u.name,u.sex,u.loginid)").append(" from ")
                .append(getEntityClassName(User.class)).append(" u,UserPosition up,").append(getEntityClassName(Position.class)).append(" p")
                .append(",UserPosition up1").append(" where up.id.taposition.positionid=?").append(" and u.userid=up.id.tauser.userid")
                .append(" and u.userid=up1.id.tauser.userid").append(" and up1.id.taposition.positionid=p.positionid")
                .append(" and u.directorgid=p.taorg.orgid").append(" and (u.destory is null or u.destory =?)").append(" and u.effective=?")
                .append(" and p.positiontype=?").append(" and p.effective=?");

        List<UserInfoVO> users = hibernateDao.createQuery(hql.toString(), new Object[]{positionid, "1", "0", "2", "0"}).list();

        return users;
    }

    public List<User> getAllUsersNotInThePosition(Long positionid) {
        List<User> users = hibernateDao.createQuery(
                "from " + getEntityClassName(User.class) + " u where u.userid not in(select up.id.tauser.userid from "
                        + getEntityClassName(Position.class)
                        + " p ,UserPosition up where p.positionid=? and p.positionid=up.id.taposition.positionid) and u.effective=?",
                new Object[]{positionid, "0"}).list();
        return users;
    }

    public List<PermissionTreeVO> getUsePermissionTreeByPositionId(Long positionid) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位为空!");
        }
        List<PermissionTreeVO> nodes = new ArrayList();
        StringBuffer hql = new StringBuffer();
        hql.append("from PositionAuthrity pa").append(" where pa.id.taposition.positionid=?").append(" and pa.usepermission=?")
                .append(" and (pa.id.tamenu.menutype<>?)").append(" and pa.id.tamenu.effective=?").append(" order by pa.id.tamenu.sortno");

        List<PositionAuthrity> positionAuthrities = hibernateDao.createQuery(hql.toString(), new Object[]{positionid, "1", "1", "0"}).list();
        PermissionTreeVO node;
        if ((positionAuthrities != null) && (positionAuthrities.size() > 0)) {
            node = null;
            for (PositionAuthrity positionAuthrity : positionAuthrities) {
                Menu menu = positionAuthrity.getId().getTamenu();
                node = new PermissionTreeVO();
                node.setId(menu.getMenuid());
                node.setPId(menu.getPmenuid());
                node.setName(menu.getMenuname());
                if ((!ValidateUtil.isEmpty(positionAuthrity.getEffecttime()))
                        && (DateUtil.computeDateOnly(positionAuthrity.getEffecttime(), timeService.getSysDate()) < 0)) {
                    node.setEffectivetimeover(true);
                }

                node.setUseyab003(menu.getUseyab003());
                nodes.add(node);
            }
        }
        return nodes;
    }

    public List<PermissionTreeVO> getRePermissionTreeByPositionId(Long positionid) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("当前岗位为空!");
        }
        List<PermissionTreeVO> nodes = new ArrayList();
        PermissionTreeVO node;
        if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
            boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
            List<Menu> ms = null;
            if (isPortal) {
                ms = hibernateDao.createQuery(
                        "from " + getEntityClassName(Menu.class) + " m where m.effective=? and (m.menutype<>?) order by m.sortno",
                        new Object[]{"0", "1"}).list();
            } else {
                String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
                ms = hibernateDao.createQuery(
                        "from " + getEntityClassName(Menu.class) + " m where m.effective=? and (m.menutype<>?) and m.syspath=? order by m.sortno",
                        new Object[]{"0", "1", curSyspathId}).list();
            }

            if ((ms != null) && (ms.size() > 0)) {
                node = null;
                for (Menu menu : ms) {
                    node = new PermissionTreeVO();
                    node.setId(menu.getMenuid());
                    node.setPId(menu.getPmenuid());
                    node.setName(menu.getMenuname());
                    node.setTitle(menu.getMenuname());
                    node.setPolicy(menu.getSecuritypolicy());
                    node.setMenulevel(menu.getMenulevel());
                    node.setIconSkin(menu.getIconSkin());
                    node.setUseyab003(menu.getUseyab003());
                    if ("0".equals(menu.getIsaudite())) {
                        node.setIsaudite(true);
                    }
                    nodes.add(node);
                }
            }
        } else {
            List<PositionAuthrity> positionAuthrities = null;
            boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);
            if (isPortal) {
                positionAuthrities = hibernateDao
                        .createQuery(
                                "from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and pa.id.tamenu.menutype<>?  and pa.repermission=? and pa.id.tamenu.effective=? order by pa.id.tamenu.menuid",
                                new Object[]{positionid, timeService.getSysDate(), "1", "1", "0"}).list();

            } else {

                String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
                positionAuthrities = hibernateDao
                        .createQuery(
                                "from PositionAuthrity pa where pa.id.taposition.positionid=? and (pa.effecttime is null or pa.effecttime >=?) and pa.id.tamenu.menutype<>?  and pa.repermission=? and pa.id.tamenu.effective=? and pa.id.tamenu.syspath=? order by pa.id.tamenu.menuid",
                                new Object[]{positionid, timeService.getSysDate(), "1", "1", "0", curSyspathId}).list();
            }

            if ((positionAuthrities != null) && (positionAuthrities.size() > 0)) {
                node = null;
                for (PositionAuthrity positionAuthrity : positionAuthrities) {
                    Menu menu = positionAuthrity.getId().getTamenu();
                    node = new PermissionTreeVO();
                    node.setId(menu.getMenuid());
                    node.setPId(menu.getPmenuid());
                    node.setName(menu.getMenuname());
                    node.setTitle(menu.getMenuname());
                    node.setPolicy(menu.getSecuritypolicy());
                    node.setUseyab003(menu.getUseyab003());
                    node.setIconSkin(menu.getIconSkin());
                    nodes.add(node);
                }
            }
        }
        return nodes;
    }

    public List<PositionInfoVO> getPubPositionsCurUserid(Long userid) {
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("人员id为空,无法分配岗位");
        }
        StringBuffer hql = new StringBuffer();
        hql.append(
                "select new com.yinhai.ta3.system.org.domain.PositionInfoVO(p.positionid,p.positionname,p.orgnamepath,p.positiontype,up.mainposition,p.isshare,p.iscopy)")
                .append(" from " + getEntityClassName(Position.class) + " p ,UserPosition up, " + getEntityClassName(User.class) + " u")
                .append(" where p.positionid=up.id.taposition.positionid").append(" and up.id.tauser.userid=u.userid")
                .append(" and p.positiontype<>?").append(" and p.effective=?").append(" and u.effective=?").append(" and u.userid=?")
                .append(" order by p.orgnamepath");

        return hibernateDao.createQuery(hql.toString(), new Object[]{"3", "0", "0", userid}).list();
    }

    public void saveRoleScopeAclOperate(Long batchNo, List<Key> list, ParamDTO dto) {
        if (batchNo == null)
            batchNo = getLongSeq();
        Long positionid = dto.getAsLong("positionid");
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位id为空,不能进行授权");
        }
        Position position = getPosition(positionid);

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "11", "03", "不具有该岗位的操作权限（赋予使用权）");

        List<SharePosition> sps = new ArrayList();
        if ("1".equals(position.getIsshare())) {
            sps = hibernateDao.createQuery("from SharePosition sp where sp.id.spositionid=?", new Object[]{positionid}).list();
        }
        Long userid = dto.getUserInfo().getUserid();
        List<Long> ulist = hibernateDao.createQuery("select up.id.tauser.userid from UserPosition up where up.id.taposition.positionid=?",
                new Object[]{positionid}).list();
        for (int i = 0; i < list.size(); i++) {
            Key key = (Key) list.get(i);
            Long permissionid = key.getAsLong("id");
            String flag = key.getAsString("checked");
            String type = dto.getAsString("positionType");
            String isyab003 = key.getAsString("isyab003");
            if (ValidateUtil.isEmpty(isyab003)) {
                String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
                Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
                if ("false".equals(flag)) {
                    checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "14", positionid);
                    if ("2".equals(type)) {
                        grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
                                .getSysTimestamp(), "0", "3"));
                        if ("0".equals(m.getUseyab003())) {
                            hibernateDao
                                    .createQuery(
                                            "delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
                                            new Object[]{positionid, permissionid, "YAB139", curSyspathId}).executeUpdate();
                            api.clearCache(permissionid, positionid, "YAB139");
                        }
                    } else if ("1".equals(type)) {
                        grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
                                .getSysTimestamp(), "0", "3"));
                        if ("0".equals(m.getUseyab003())) {
                            hibernateDao
                                    .createQuery(
                                            "delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
                                            new Object[]{positionid, permissionid, "YAB139", curSyspathId}).executeUpdate();
                            api.clearCache(permissionid, positionid, "YAB139");
                        }
                        for (SharePosition sharePosition : sps) {
                            grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, Long.valueOf(sharePosition.getId()
                                    .getDpositionid()), userid, timeService.getSysTimestamp(), "0", "3"));
                            if ("0".equals(m.getUseyab003())) {
                                hibernateDao
                                        .createQuery(
                                                "delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
                                                new Object[]{Long.valueOf(sharePosition.getId().getDpositionid()), permissionid, "YAB139",
                                                        curSyspathId}).executeUpdate();
                                api.clearCache(permissionid, Long.valueOf(sharePosition.getId().getDpositionid()), "YAB139");
                            }
                        }
                    }

                    orgOpLogService.logPermisstionOp(batchNo, dto.getUserInfo(), "14", m, position);
                } else if ("true".equals(flag)) {
                    checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "13", positionid);
                    if ("2".equals(type)) {
                        grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
                                .getSysTimestamp(), "1", "3"));
                        if ("0".equals(m.getUseyab003())) {
                            DataAccessDimension dad = new DataAccessDimension();
                            dad.setMenuid(permissionid);
                            dad.setPositionid(positionid);
                            dad.setDimensiontype("YAB139");
                            IOrg iOrg = organizationEntityService.getDepartByPositionId(positionid);
                            dad.setDimensionpermissionid(iOrg.getYab139());
                            dad.setAllaccess("1");
                            dad.setSyspath(curSyspathId);

                            if (!ValidateUtil.isEmpty(iOrg.getYab139())) {
                                hibernateDao.save(dad);
                            }
                        }
                    } else if ("1".equals(type)) {
                        grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, userid, timeService
                                .getSysTimestamp(), "1", "3"));
                        if ("0".equals(m.getUseyab003())) {
                            DataAccessDimension dad = new DataAccessDimension();
                            dad.setMenuid(permissionid);
                            dad.setPositionid(positionid);
                            dad.setDimensiontype("YAB139");
                            IOrg iOrg = organizationEntityService.getDepartByPositionId(positionid);
                            dad.setDimensionpermissionid(iOrg.getYab139());
                            dad.setAllaccess("1");
                            dad.setSyspath(curSyspathId);

                            if (!ValidateUtil.isEmpty(iOrg.getYab139())) {
                                hibernateDao.save(dad);
                            }
                        }
                        for (SharePosition sharePosition : sps) {
                            grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, Long.valueOf(sharePosition.getId()
                                    .getDpositionid()), userid, timeService.getSysTimestamp(), "1", "3"));
                            if ("0".equals(m.getUseyab003())) {
                                DataAccessDimension dad = new DataAccessDimension();
                                dad.setMenuid(permissionid);
                                dad.setPositionid(positionid);
                                dad.setDimensiontype("YAB139");
                                IOrg isOrg = organizationEntityService.getDepartByPositionId(Long.valueOf(sharePosition.getId().getDpositionid()));
                                dad.setDimensionpermissionid(isOrg.getYab139());
                                dad.setAllaccess("1");
                                dad.setSyspath(curSyspathId);

                                if (!ValidateUtil.isEmpty(isOrg.getYab139())) {
                                    hibernateDao.save(dad);
                                }
                            }
                        }
                    }

                    orgOpLogService.logPermisstionOp(batchNo, dto.getUserInfo(), "13", m, position);
                }
            }
        }
    }

    public void recyclePermissions(List<Key> positionsList, List<Key> permissionsList, ParamDTO dto) {
        if (positionsList != null) {
            Long batchNo = getLongSeq();
            String type = dto.getAsString("positionType");
            for (int i = 0; i < positionsList.size(); i++) {
                Key key = (Key) positionsList.get(i);
                Long positionid = key.getAsLong("positionid");
                Position position = getPosition(positionid);

                checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "11", "03",
                        "不具有该岗位的操作权限（回收权限）");

                if (permissionsList != null) {
                    String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
                    for (int j = 0; j < permissionsList.size(); j++) {
                        Key key1 = (Key) permissionsList.get(j);
                        Long permissionid = key1.getAsLong("permissionid");
                        String isyab003 = key1.getAsString("isyab003");
                        if (ValidateUtil.isEmpty(isyab003)) {

                            checkMenu(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), permissionid, true, "14",
                                    positionid);
                            PositionAuthrity positionAuthrity = (PositionAuthrity) hibernateDao.createQuery(
                                    "from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?",
                                    new Object[]{positionid, permissionid}).uniqueResult();

                            if (positionAuthrity != null) {
                                if ("2".equals(type)) {
                                    grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
                                            .getUserid(), timeService.getSysTimestamp(), "0", "3"));

                                    hibernateDao
                                            .createQuery(
                                                    "delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
                                                    new Object[]{positionid, permissionid, "YAB139", curSyspathId}).executeUpdate();
                                    api.clearCache(permissionid, positionid, "YAB139");
                                } else if ("1".equals(type)) {
                                    grantService.permissionChangeUniteFunction(new PermissionInfoVO(permissionid, positionid, dto.getUserInfo()
                                            .getUserid(), timeService.getSysTimestamp(), "0", "3"));

                                    hibernateDao
                                            .createQuery(
                                                    "delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?",
                                                    new Object[]{positionid, permissionid, "YAB139", curSyspathId}).executeUpdate();
                                    api.clearCache(permissionid, positionid, "YAB139");
                                }
                            }

                            Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), permissionid);
                            orgOpLogService.logPermisstionOp(batchNo, dto.getUserInfo(), "14", m, position);
                        }
                    }
                }
            }
        }
    }

    public void grantUsePermissions(List<Key> positionsList, List<Key> permissionsList, ParamDTO dto) {
        if (positionsList != null) {
            Long bathcNo = Long.valueOf(getStringSeq());

            for (int i = 0; i < positionsList.size(); i++) {
                Key key = (Key) positionsList.get(i);
                dto.put("positionid", key.getAsLong("positionid"));
                saveRoleScopeAclOperate(bathcNo, permissionsList, dto);
            }
        }
    }

    public void clonePermissions(Long positionid, String[] poids, IUser opUser) {
        checkOrg(opUser.getUserid(), opUser.getNowPosition().getPositionid(), positionid, "11", "03", "不具有该岗位的操作权限（克隆岗位权限）");

        List<PositionAuthrity> pas = hibernateDao.createQuery("from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.usepermission=? and (pa.effecttime is null or pa.effecttime >=?)", positionid, "1", timeService.getSysTimestamp()).list();
        Long bathcNo = getLongSeq();
        for (PositionAuthrity positionAuthrity : pas) {
            checkMenu(opUser.getUserid(), opUser.getNowPosition().getPositionid(), positionAuthrity.getId().getTamenu().getMenuid(), true, "13",
                    Long.valueOf(poids[0]));
            for (String poid : poids) {
                grantService.permissionChangeUniteFunction(new PermissionInfoVO(positionAuthrity.getId().getTamenu().getMenuid(), Long.valueOf(poid),
                        opUser.getUserid(), timeService.getSysTimestamp(), "1", "3"));

                Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), positionAuthrity.getId().getTamenu().getMenuid());
                IPosition position = (IPosition) hibernateDao.getSession().get(getEntityClassName(Position.class), Long.valueOf(poid));
                orgOpLogService.logPermisstionOp(bathcNo, opUser, "13", m, position);
            }
        }
    }

    public List<Position> getPubPositionsNoCurUseridByOrgId(ParamDTO dto) {
        Long orgid = dto.getAsLong("orgid");
        if (ValidateUtil.isEmpty(orgid)) {
            throw new AppException("部门为空,不能进行查询");
        }
        Long userid = dto.getAsLong("userid");
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("人员id为空,无法分配岗位");
        }
        Position p = getPosition(dto.getUserInfo().getNowPosition().getPositionid());
        StringBuffer sb = new StringBuffer();
        sb.append("from " + getEntityClassName(Position.class) + " p where p.effective=").append("0");
        sb.append(" and p.positiontype=").append("1");
        sb.append(" and p.positionid not in(");

        if (IPosition.ADMIN_POSITIONID.equals(p.getPositionid())) {
            sb.append(p.getPositionid());
        } else {
            sb.append(p.getPositionid()).append(",").append(IPosition.ADMIN_POSITIONID);
        }

        String positionids = dto.getAsString("positionids");

        if (ValidateUtil.isNotEmpty(positionids)) {
            String[] positionidsTemp = positionids.split(",");
            sb.append(",");
            for (int i = 0; i < positionidsTemp.length; i++) {
                sb.append(Long.valueOf(positionidsTemp[i]));
                if (i < positionidsTemp.length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(")");
        if (orgid != null) {
            if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
                sb.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=").append(p.getPositionid()).append(")");
            }
            if ("0".equals(dto.getAsString("isDisSubOrgs"))) {
                Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), orgid);
                if (org != null) {
                    sb.append(" and (p.orgidpath = '").append(org.getOrgidpath()).append("' or p.orgidpath like '").append(org.getOrgidpath())
                            .append("/%')").append(" and p.taorg.effective=").append("0").append(" and (p.taorg.destory is null or p.taorg.destory=")
                            .append("1").append(")");
                }
            } else {
                sb.append(" and p.taorg.orgid=").append(orgid);
            }
        }

        sb.append(" order by p.orgidpath");
        return hibernateDao.createQuery(sb.toString(), new Object[0]).list();
    }

    public void removeUserPosition(ParamDTO dto) {
        Long positionid = dto.getAsLong("positionid");
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位id为空,不能移除岗位");
        }
        Long userid = dto.getAsLong("userid");
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("人员id为空,不能移除岗位");
        }

        grantService.retrievePositionFromUser(userid, positionid, dto.getUserInfo().getUserid(), timeService.getSysTimestamp());
    }

    public void saveUserAddPositions(List<Key> selected, ParamDTO dto) {
        Long userid = dto.getAsLong("userid");
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("人员id为空,不能分配岗位");
        }
        User user = (User) organizationEntityService.getUserByUserId(userid);

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), user.getUserid(), "05", "02",
                "人员分配岗位，无该人员所在组织的操作权限");
        Long batchNo = Long.valueOf(getStringSeq());
        for (Key key : selected) {
            checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), key.getAsLong("positionid"), "11", "03",
                    "人员分配岗位，无该岗位所在组织的操作权限");
            grantService.grantPositionToUser(userid, key.getAsLong("positionid"), dto.getUserInfo().getUserid(), timeService.getSysTimestamp());

            orgOpLogService.logUserOp(batchNo, dto.getUserInfo(), "08", user, key.getAsString("positionid"));
        }
    }

    public void setMainPosition(Long positionid, Long userid, ParamDTO dto) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位id为空,不能设置主岗位");
        }
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("人员id为空,不能设置主岗位");
        }

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), positionid, "09", "03", "");

        List<UserPosition> ups = hibernateDao.createQuery(
                "select up from UserPosition up," + getEntityClassName(Position.class)
                        + " p where up.id.taposition.positionid=p.positionid and up.id.tauser.userid=? and up.mainposition=?",
                new Object[]{userid, "1"}).list();
        if (ups != null) {
            for (UserPosition userPosition : ups) {
                checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), userPosition.getId().getTaposition()
                        .getPositionid(), "09", "03", "");
                userPosition.setMainposition("0");
                hibernateDao.update(userPosition);
            }
        }

        hibernateDao.createQuery("update UserPosition up set up.mainposition=? where up.id.taposition.positionid=? and up.id.tauser.userid=?",
                new Object[]{"1", positionid, userid}).executeUpdate();

        IUser user = organizationEntityService.getUserByUserId(userid);
        orgOpLogService.logUserOp(Long.valueOf(getStringSeq()), dto.getUserInfo(), "09", user, positionid.toString());
    }

    public List<UserInfoVO> queryUsers(ParamDTO dto) {
        Long orgid = dto.getAsLong("orgid");
        String isChildren = dto.getAsString("isChildren");
        StringBuffer sb = new StringBuffer();
        sb.append("select distinct new com.yinhai.ta3.system.org.domain.UserInfoVO(p.orgnamepath,u.userid,u.name,u.sex,u.loginid)")
                .append(" from UserPosition up," + getEntityClassName(User.class) + " u," + getEntityClassName(Position.class) + " p,"
                        + getEntityClassName(Org.class) + " o").append(" where u.effective=?").append(" and u.userid=up.id.tauser.userid")
                .append(" and p.positionid=up.id.taposition.positionid").append(" and p.positiontype=?").append(" and u.directorgid=p.taorg.orgid")
                .append(" and (p.validtime is null or p.validtime >=?)").append(" and p.effective=?")
                .append(" and (u.destory is null or u.destory = ?)");

        if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
            sb.append(" and p.taorg.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=")
                    .append(dto.getUserInfo().getNowPosition().getPositionid()).append(")");
        }
        if ((ValidateUtil.isNotEmpty(isChildren)) && ("isChildren".equals(isChildren))) {
            sb.append(" and p.orgidpath like o.orgidpath||'%' and o.orgid=?");
        } else {
            sb.append(" and p.taorg.orgid=?");
        }
        List<Key> userids = (List) dto.get("userids");
        if ((userids != null) && (userids.size() > 0)) {
            sb.append(" and u.userid not in(");
            for (int i = 0; i < userids.size(); i++) {
                Key key = (Key) userids.get(i);
                sb.append(key.getAsLong("userid"));
                sb.append(",");
            }
            sb.append(IUser.ROOT_USERID);
            sb.append(",").append(dto.getUserInfo().getUserid());
            sb.append(")");
        } else {
            sb.append(" and u.userid not in(").append(IUser.ROOT_USERID).append(",").append(dto.getUserInfo().getUserid()).append(")");
        }
        sb.append("order by p.orgnamepath,u.loginid");
        List<UserInfoVO> users = hibernateDao.createQuery(sb.toString(), "0", "2", timeService.getSysDate(), "0", "1", orgid).list();
        return users;
    }

    public PageBean queryUsersByDto(ParamDTO dto) {
        Long orgid = dto.getAsLong("orgid");
        String isChildren = dto.getAsString("isDisDecPositions");
        String loginids = dto.getAsString("loginids");
        String username = dto.getAsString("username");
        StringBuffer sb = new StringBuffer();

        sb.append("select distinct p from ").append(super.getEntityClassName(Org.class)).append(" o,").append(getEntityClassName(Position.class))
                .append(" p,UserPosition up,").append(getEntityClassName(User.class)).append(" u")
                .append(" where 1=1 and p.positionid=up.id.taposition.positionid").append(" and u.effective=").append("0")
                .append(" and p.positiontype=").append("2").append(" and (p.validtime is null or p.validtime >='").append(timeService.getSysDate())
                .append("')").append(" and p.effective=").append("0").append(" and (u.destory is null or u.destory = ").append("1").append(")")
                .append(" and up.id.tauser.userid=u.userid").append(" and u.directorgid = p.taorg.orgid");

        if (ValidateUtil.isEmpty(orgid)) {
            sb.append(" and p.orgidpath like o.orgidpath||'%' and o.orgid=:orgid");
        } else if ((ValidateUtil.isNotEmpty(isChildren)) && ("0".equals(isChildren))) {
            sb.append(" and p.orgidpath like o.orgidpath||'%' and o.orgid=:orgid");
        } else {
            sb.append(" and p.taorg.orgid = o.orgid and o.orgid=:orgid");
        }

        if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
            sb.append(" and p.taorg.orgid in(select om.id.orgid from OrgMg om where om.id.positionid=")
                    .append(dto.getUserInfo().getNowPosition().getPositionid()).append(")");
            sb.append(" and p.positionid<>:developerId");
        }

        if (ValidateUtil.isNotEmpty(username)) {
            sb.append(" and u.name like :username");
        }
        if (ValidateUtil.isNotEmpty(loginids)) {
            sb.append(" and u.loginid in(");

            loginids = loginids.replace("，", ",");
            String[] temp = loginids.split(",");
            for (int i = 0; i < temp.length; i++) {
                sb.append("'" + temp[i] + "'");
                if (i < temp.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }

        Query query = null;
        Finder finder = Finder.create(sb.toString());
        String countHql = finder.getRowCountHql();
        Query queryCount = hibernateDao.createQuery(countHql);
        query = hibernateDao.createQuery(sb.toString());
        if (ValidateUtil.isNotEmpty(username)) {
            query.setString("username", "%" + username + "%");
            queryCount.setString("username", "%" + username + "%");
        }
        if (ValidateUtil.isEmpty(orgid)) {
            query.setLong("orgid", Org.ORG_ROOT_ID.longValue());
            queryCount.setLong("orgid", Org.ORG_ROOT_ID.longValue());
        } else if ((ValidateUtil.isNotEmpty(isChildren)) && ("0".equals(isChildren))) {
            query.setLong("orgid", orgid.longValue());
            queryCount.setLong("orgid", orgid.longValue());
        } else {
            query.setLong("orgid", orgid.longValue());
            queryCount.setLong("orgid", orgid.longValue());
        }
        if (!IPosition.ADMIN_POSITIONID.equals(dto.getUserInfo().getNowPosition().getPositionid())) {
            query.setLong("developerId", IPosition.ADMIN_POSITIONID.longValue());
            queryCount.setLong("developerId", IPosition.ADMIN_POSITIONID.longValue());
        }
        Integer skipResults = Integer.valueOf(dto.getStart("userGrid") == null ? 0 : dto.getStart("userGrid").intValue());
        Integer maxResults = Integer.valueOf(dto.getLimit("userGrid") == null ? 0 : dto.getLimit("userGrid").intValue());
        PageBean pb = new PageBean();
        pb.setGridId("userGrid");
        Long total = (Long) queryCount.uniqueResult();
        pb.setTotal(Integer.valueOf(total.intValue()));
        List<Position> positions = query.list();
        List<PositionInfoVO> list = buildPositionInfos(positions, dto.getUserInfo(), "2");
        pb.setList(list);
        pb.setStart(skipResults);
        pb.setLimit(maxResults);
        return pb;
    }

    public void saveAssignUsers(List<Key> selected, ParamDTO dto) {
        Long batchNo = getLongSeq();

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), dto.getAsLong("positionid"), "24", "03", "");
        for (Key key : selected) {
            User u = (User) organizationEntityService.getUserByUserId(key.getAsLong("userid"));

            checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), u.getUserid(), "05", "02",
                    "岗位设置人员，对该人员所在组织无操作权限");
            grantService.grantPositionToUser(key.getAsLong("userid"), dto.getAsLong("positionid"), dto.getUserInfo().getUserid(),
                    timeService.getSysTimestamp());

            IUser user = u;
            orgOpLogService.logUserOp(batchNo, dto.getUserInfo(), "08", user, dto.getAsLong("positionid").toString());
        }
    }

    public void removeAssignUsers(List<Key> selected, Long positionid, Long userid) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位为空");
        }

        checkOrg(userid, positionid, positionid, "11", "03", "岗位移除人员，对该岗位所在组织无操作权限");
        for (Key key : selected) {
            User u = (User) organizationEntityService.getUserByUserId(key.getAsLong("userid"));

            checkOrg(userid, positionid, u.getUserid(), "05", "02", "岗位移除人员，对该人员所在组织无操作权限");
            grantService.retrievePositionFromUser(key.getAsLong("userid"), positionid, userid, timeService.getSysTimestamp());
        }
    }

    private List<PositionInfoVO> buildPositionInfos(List<Position> positions, IUser curUser, String positionType) {
        List<IPosition> list = organizationEntityService.getPositionsByUserId(curUser.getUserid());
        List<PositionInfoVO> positionInfos = new ArrayList<PositionInfoVO>();
        for (Position child : positions)
            if (!list.contains(child)) {
                PositionInfoVO positionInfo = new PositionInfoVO();
                positionInfo.setOrgid(child.getOrgid());

                positionInfo.setCreatetime(child.getCreatetime());
                positionInfo.setEffective(child.getEffective());

                positionInfo.setOrgnamepath(child.getOrgnamepath());

                positionInfo.setPositionid(child.getPositionid());

                positionInfo.setPositionname(child.getPositionname());
                User puser = (User) hibernateDao.getSession().get(getEntityClassName(User.class), child.getCreateuser());
                if (puser != null) {
                    positionInfo.setUsername(puser.getName());
                }
                if ("2".equals(positionType)) {
                    System.out.println(child.getPositionid());
                    User user = (User) hibernateDao.createQuery(
                            "select u from " + getEntityClassName(User.class) + " u ,UserPosition up," + getEntityClassName(Position.class)
                                    + " p where p.positionid=? and p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=u.userid",
                            child.getPositionid(), "2").uniqueResult();
                    if (!ValidateUtil.isEmpty(user)) {
                        positionInfo.setUserid(user.getUserid());
                        positionInfo.setIslock(user.getIslock());
                        positionInfo.setLoginid(user.getLoginid());
                        positionInfo.setSex(user.getSex());
                    }
                }
                positionInfos.add(positionInfo);
            }
        return positionInfos;
    }

    public List<Position> getAllPositions(ParamDTO dto) {
        Long positionid = dto.getUserInfo().getNowPosition().getPositionid();
        Long orgid = dto.getAsLong("orgid");
        if (ValidateUtil.isEmpty(orgid)) {
            throw new AppException("所选组织机构id为空，不能进行操作");
        }
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位id为空，不能进行操作");
        }
        if (IPosition.ADMIN_POSITIONID.equals(positionid)) {
            return hibernateDao.createQuery(
                    "from " + getEntityClassName(Position.class) + " p where p.effective=? and p.taorg.orgidpath like (select o.orgidpath from "
                            + getEntityClassName(Org.class) + " o where o.orgid=?)||'%'", "0", orgid).list();
        }
        return hibernateDao.createQuery("from " + getEntityClassName(Position.class)
                + " p where p.effective=? and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=?) and p.taorg.orgidpath like (select o.orgidpath from "
                + getEntityClassName(Org.class) + " o where o.orgid=?)||'%'", "0", positionid, orgid).list();
    }

    public void saveSharePositions(ParamDTO dto, List<Key> orgids) {
        Long sposition = dto.getAsLong("positionid");
        if (ValidateUtil.isEmpty(sposition)) {
            throw new AppException("共享岗位id为空，不能进行操作");
        }
        Position sharePosition = getPosition(sposition);

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), sharePosition.getPositionid(), "11", "03",
                "共享岗位，没有该岗位所在组织的操作权限");

        if (!"1".equals(sharePosition.getIsshare())) {
            sharePosition.setIsshare("1");
            hibernateDao.update(sharePosition);
        }
        Date curDate = timeService.getSysDate();

        List<Menu> menus = hibernateDao.createQuery(
                "select distinct m from " + super.getEntityClassName(Menu.class)
                        + " m,PositionAuthrity pa where pa.id.taposition.positionid=? and m.menuid=pa.id.tamenu.menuid",
                sharePosition.getPositionid()).list();

        for (Key key : orgids) {
            Long orgid = key.getAsLong("id");
            Org org = (Org) organizationEntityService.getDepart(orgid);
            dto.append("orgid", orgid);
            dto.append("positionname", sharePosition.getPositionname());
            dto.append("positiontype", "1");
            dto.append("positioncategory", sharePosition.getPositioncategory());
            String check = key.getAsString("checked");
            String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
            Position p;
            if ("true".equals(check)) {
                dto.append("iscopy", "1");
                p = createPosition(dto);
                SharePositionId sid = new SharePositionId();
                sid.setSpositionid(sposition.longValue());
                sid.setDpositionid(p.getPositionid().longValue());
                SharePosition sp = new SharePosition();
                sp.setId(sid);
                hibernateDao.save(sp);
                for (Menu m : menus) {
                    grantService.permissionChangeUniteFunction(new PermissionInfoVO(m.getMenuid(), p.getPositionid(), dto.getUserInfo().getUserid(),
                            curDate, "1", "3"));
                    if ("0".equals(m.getUseyab003())) {
                        DataAccessDimension dad = new DataAccessDimension();
                        dad.setMenuid(m.getMenuid());
                        dad.setPositionid(p.getPositionid());
                        dad.setDimensiontype("YAB139");
                        IOrg iOrg = organizationEntityService.getDepartByPositionId(p.getPositionid());
                        dad.setDimensionpermissionid(iOrg.getYab139());
                        dad.setAllaccess("1");
                        dad.setSyspath(curSyspathId);
                        hibernateDao.save(dad);
                    }
                }
            } else if ("false".equals(check)) {
                List<Position> pList = hibernateDao.createQuery("select p from SharePosition sp," + super.getEntityClassName(Position.class) + " p," + super.getEntityClassName(Org.class)
                                + " o where sp.id.spositionid=? and sp.id.dpositionid=p.positionid and p.taorg.orgid=? and p.iscopy=?",
                        sposition, orgid, "1").list();
                if (!ValidateUtil.isEmpty(pList)) {
                    Position dposition = (Position) pList.get(0);
                    if (dposition.getOrgid().equals(org.getOrgid())) {
                        hibernateDao.createQuery("delete from SharePosition sp where sp.id.spositionid=? and sp.id.dpositionid=?", sposition, dposition.getPositionid()).executeUpdate();

                        List<Menu> list = hibernateDao.createQuery("select distinct m from " + super.getEntityClassName(Menu.class) + " m,PositionAuthrity pa where pa.id.taposition.positionid=? and m.menuid=pa.id.tamenu.menuid", dposition.getPositionid()).list();
                        for (Menu m : list) {
                            grantService.permissionChangeUniteFunction(new PermissionInfoVO(m.getMenuid(), dposition.getPositionid(), dto.getUserInfo().getUserid(), curDate, "0", "6"));
                            hibernateDao.createQuery("delete from DataAccessDimension dad where dad.positionid=? and dad.menuid=? and dad.dimensiontype=? and dad.syspath=?", dposition.getPositionid(), m.getMenuid(), "YAB139", curSyspathId).executeUpdate();
                            api.clearCache(m.getMenuid(), dposition.getPositionid(), "YAB139");
                        }

                        hibernateDao.createQuery("delete from UserPosition up where up.id.taposition.positionid=?", dposition.getPositionid()).executeUpdate();
                        hibernateDao.delete(dposition);
                    }
                }
            }
        }
    }

    public List<Org> queryCopyPositionInOrgBySharePositionId(Long positionid) {
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("共享岗位id为空，不能进行操作");
        }
        return hibernateDao.createQuery("select distinct o from SharePosition sp," + getEntityClassName(Position.class) + " p," + getEntityClassName(Org.class)
                + " o where sp.id.dpositionid = p.positionid and p.taorg.orgid = o.orgid and sp.id.spositionid=:positionId").setParameter("positionId", positionid).list();
    }

    public void saveEffectiveTimePanel(ParamDTO dto) {
        Long menuid = dto.getAsLong("menuid");
        Long positionid = dto.getAsLong("positionid");
        Date effectivetime = dto.getAsDate("effectiveTime");
        if (ValidateUtil.isEmpty(menuid)) {
            throw new AppException("菜单id为空，不能设置权限有效时间");
        }
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("岗位id为空，不能设置权限有效时间");
        }
        PositionAuthrity pa = (PositionAuthrity) hibernateDao.createQuery("from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?", positionid, menuid).uniqueResult();
        if (ValidateUtil.isEmpty(pa)) {
            PositionAuthrityId id = new PositionAuthrityId();
            Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), menuid);
            Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), positionid);
            id.setTamenu(m);
            id.setTaposition(p);
            pa = new PositionAuthrity();
            pa.setId(id);
            pa.setEffecttime(effectivetime);
            pa.setCreateuser(dto.getUserInfo().getUserid());
            pa.setCreatetime(timeService.getSysTimestamp());
            hibernateDao.save(pa);
        } else {
            grantService.permissionChangeUniteFunction(new PermissionInfoVO(menuid, positionid, effectivetime, dto.getUserInfo().getUserid(),
                    timeService.getSysDate(), "7", "7"));
        }
    }

    public Date queryEffectiveTime(Long menuid, Long positionid) {
        return (Date) hibernateDao.createQuery("select pa.effecttime from PositionAuthrity pa where pa.id.tamenu.menuid=? and pa.id.taposition.positionid=?", menuid, positionid).uniqueResult();
    }

    public PageBean queryPubAndSharePositions(ParamDTO dto) {
        StringBuffer hql = new StringBuffer();
        Position p = (Position) dto.toDomainObject(Position.class);
        boolean isDisSubOrgs = false;

        if ("0".equals(dto.getAsString("isDisDecPositions"))) {
            isDisSubOrgs = true;
        }
        hql.append("select distinct p from ").append(super.getEntityClassName(Position.class)).append(" p");
        Long curPos = dto.getUserInfo().getNowPosition().getPositionid();
        if (IPosition.ADMIN_POSITIONID.equals(curPos)) {
            hql.append(" where 1=1 and p.positionid<>").append(curPos);
        } else {
            hql.append(" where 1=1 and p.positionid not in(").append(curPos).append(",").append(IPosition.ADMIN_POSITIONID).append(")");
            hql.append(" and p.taorg.orgid in (select om.id.orgid from OrgMg om where om.id.positionid=").append(curPos).append(")");
        }
        Long orgid = dto.getAsLong("orgid");
        if (ValidateUtil.isEmpty(orgid)) {
            orgid = Org.ORG_ROOT_ID;
            Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), orgid);
            hql.append(" and p.orgidpath like '").append(org.getOrgidpath()).append("%'").append(" and p.taorg.effective=").append("0")
                    .append(" and (p.taorg.destory is null or p.taorg.destory=").append("1)");

        } else if (isDisSubOrgs) {
            Org org = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class.getName()), orgid);
            hql.append(" and p.orgidpath like '").append(org.getOrgidpath()).append("%'").append(" and p.taorg.effective=").append("0")
                    .append(" and (p.taorg.destory is null or p.taorg.destory=").append("1)");
        } else {
            hql.append(" and p.taorg.orgid=").append(orgid);
        }

        String positionname = p.getPositionname();
        if (ValidateUtil.isNotEmpty(positionname)) {
            hql.append(" and p.positionname like :positionname");
        }
        hql.append(" and p.effective=").append("0").append(" and p.positiontype=").append("1").append(" order by p.orgidpath");

        String gridId = dto.getAsString("gridId");
        Integer skipResults = Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
        Integer maxResults = Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
        Query query = hibernateDao.createQuery(hql.toString()).setFirstResult(skipResults.intValue())
                .setMaxResults(maxResults.intValue());
        PageBean pb = new PageBean();
        pb.setStart(skipResults);
        pb.setLimit(maxResults);
        Finder finder = Finder.create(hql.toString());
        String countHql = finder.getRowCountHql();
        Query queryCount = hibernateDao.createQuery(countHql);
        if (ValidateUtil.isNotEmpty(positionname)) {
            query.setString("positionname", "%" + positionname + "%");
            queryCount.setString("positionname", "%" + positionname + "%");
        }
        List<Position> positions = query.list();
        pb.setList(positions);
        Long total = (Long) queryCount.uniqueResult();
        pb.setTotal(Integer.valueOf(total.intValue()));
        return pb;
    }

    public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
        this.organizationEntityService = organizationEntityService;
    }

    public void setApi(IDataAccessApi api) {
        this.api = api;
    }

}
