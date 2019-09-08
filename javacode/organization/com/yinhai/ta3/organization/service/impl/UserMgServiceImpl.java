package com.yinhai.ta3.organization.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

import org.hibernate.Query;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.Finder;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.Md5PasswordEncoder;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.organization.api.IGrantService;
import com.yinhai.ta3.organization.api.IPositionService;
import com.yinhai.ta3.organization.api.IUserService;
import com.yinhai.ta3.organization.api.OrganizationEntityService;
import com.yinhai.ta3.organization.service.IOrgOpLogService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.organization.service.IUserMgService;
import com.yinhai.ta3.system.org.domain.Org;
import com.yinhai.ta3.system.org.domain.OrgLogInfoVO;
import com.yinhai.ta3.system.org.domain.OrgOpLog;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.org.domain.PositionInfoVO;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.ta3.system.org.domain.UserPerrmissionVO;
import com.yinhai.ta3.system.org.domain.UserPosition;
import com.yinhai.ta3.system.org.domain.UserPositionId;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public class UserMgServiceImpl extends OrgBaseService implements IUserMgService {

    private IPositionMgService positionMgService;
    private IUserService userService;
    private IPositionService positionService;
    private IOrgOpLogService orgOpLogService;
    private IGrantService grantService;
    private Md5PasswordEncoder md5PasswordEncoder;
    private OrganizationEntityService organizationEntityService;

    public User createUser(ParamDTO dto) {
        User user = (User) dto.toDomainObject(User.getCurrentClassName());
        user.setPassword(md5PasswordEncoder.encodePassword(dto.getAsString("password"), user.getLoginid()));

        user.setCreateuser(dto.getUserInfo().getUserid());

        if (ValidateUtil.isEmpty(user))
            throw new AppException("用户名账号为空！");
        if (ValidateUtil.isEmpty(user.getLoginid()))
            throw new AppException("用户名账号为空！");
        if (checkSameUserId(dto)) {
            throw new AppException("用户已存在！");
        }
        if (ValidateUtil.isEmpty(user.getPassword()))
            throw new AppException("用户密码为空！");
        if (ValidateUtil.isEmpty(user.getCreateuser()))
            throw new AppException("创建该用户的创建人不存在");
        if (ValidateUtil.isEmpty(user.getName())) {
            throw new AppException("用户姓名为空！");
        }

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), dto.getAsLong("orgid"), "04", "01",
                dto.getAsString("name"));

        user.setPasswordfaultnum(Integer.valueOf(0));
        user.setBirth(dto.getAsDate("birth"));
        user.setCreatetime(timeService.getSysTimestamp());
        user.setEffective("0");
        user.setIslock("0");
        Long[] orgids = null;

        String fsOrg = dto.getAsString("w1_orgid");
        if (ValidateUtil.isNotEmpty(fsOrg)) {
            String[] fsOrgs = fsOrg.split(",");
            orgids = new Long[fsOrgs.length + 1];
            orgids[0] = dto.getAsLong("orgid");
            for (int i = 1; i <= fsOrgs.length; i++) {
                orgids[i] = Long.valueOf(fsOrgs[(i - 1)]);

                checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), Long.valueOf(fsOrgs[(i - 1)]), "10",
                        "01", "人员新增附属组织");
            }
        } else {
            orgids = new Long[1];
            orgids[0] = dto.getAsLong("orgid");
        }

        user.setDirectorgid(dto.getAsLong("orgid"));
        User newUser = userService.createUser(user, orgids, dto.getUserInfo().getNowPosition());

        Org o = (Org) organizationEntityService.getDepart(dto.getAsLong("orgid"));
        orgOpLogService.logUserOp(Long.valueOf(getStringSeq()), dto.getUserInfo(), "04", newUser, o.getOrgnamepath() + "/" + user.getName());
        TaEventPublisher.publishEvent(new EventSource(user, dto), "user_create");
        return user;
    }

    public Position createUserPosition(ParamDTO dto) {
        dto.put("positiontype", "2");
        if (ValidateUtil.isEmpty(dto.getAsString("positionname")))
            throw new AppException("岗位名称为空");
        if (ValidateUtil.isEmpty(dto.getAsString("orgid"))) {
            throw new AppException("岗位组织不存在");
        }
        return positionMgService.createPosition(dto);
    }

    public UserPosition createUserPositionRefrence(ParamDTO dto) {
        if (ValidateUtil.isEmpty(dto.getAsLong("userid")))
            throw new AppException("用户编号错误");
        if (ValidateUtil.isEmpty(dto.getAsLong("positionid"))) {
            throw new AppException("岗位组织不存在");
        }

        UserPositionId userpKey = new UserPositionId();
        User u = new User();
        u.setUserid(dto.getAsLong("userid"));
        Position p = new Position();
        p.setPositionid(dto.getAsLong("positionid"));
        userpKey.setTaposition(p);

        UserPosition userp = new UserPosition();
        userpKey.setTauser(u);
        userp.setCreatetime(timeService.getSysTimestamp());
        userp.setId(userpKey);

        if ("1".equals(dto.getAsString("mainposition"))) {
            userp.setMainposition("1");
        } else {
            userp.setMainposition("0");
        }
        hibernateDao.save(userp);
        return userp;
    }

    public void deleteUsers(List<Key> users, ParamDTO dto) {
        Long batchNo;
        if (!ValidateUtil.isEmpty(users)) {
            batchNo = getLongSeq();
            for (Key key : users) {
                Long userid = key.getAsLong("userid");
                User user = getUser(userid);

                checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), user.getUserid(), "26", "02", "");
                List<IPosition> ps = organizationEntityService.getPerPositionsByUserId(userid);

                hibernateDao.createQuery("delete from UserPosition up where up.id.tauser.userid=?", userid).executeUpdate();
                for (IPosition position : ps) {
                    List<PositionAuthrity> pas = hibernateDao.createQuery("from PositionAuthrity pa where pa.id.taposition.positionid=?", position.getPositionid()).list();
                    for (PositionAuthrity pa : pas) {
                        grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(), position.getPositionid(),
                                dto.getUserInfo().getUserid(), timeService.getSysTimestamp(), "0", "6"));
                    }

                    hibernateDao.createQuery("delete from AdminYab003Scope ays where ays.id.positionid=?", position.getPositionid()).executeUpdate();
                    hibernateDao.createQuery("update " + getEntityClassName(Org.class) + " o set o.orgmanager=null where o.orgmanager=?", position.getPositionid()).executeUpdate();
                    hibernateDao.createQuery("delete from ManagerMg mm where mm.id.positionid=?", position.getPositionid()).executeUpdate();
                    hibernateDao.createQuery("delete from OrgMg om where om.id.positionid=?", position.getPositionid()).executeUpdate();
                    hibernateDao.delete(position);
                }

                user.setDestory("0");
                hibernateDao.update(user);
                Org o = (Org) organizationEntityService.getDepart(user.getDirectorgid());
                orgOpLogService.logUserOp(batchNo, dto.getUserInfo(), "26", user, o.getOrgnamepath() + "/" + user.getName());
                TaEventPublisher.publishEvent(new EventSource(user, dto), "user_delete");
            }
        }
    }

    public void updateUser(ParamDTO dto) {
        if (ValidateUtil.isEmpty(dto.getAsLong("userid"))) {
            throw new AppException("没有找到用户id");
        }
        User userNew = (User) dto.toDomainObject(getEntityClassName(User.class));
        userNew.setBirth(dto.getAsDate("birth"));
        User userOld = getUser(dto.getAsLong("userid"));

        checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), userOld.getUserid(), "05", "02", "");
        String newUserName = userNew.getName();
        String oldUserName = userOld.getName();
        String userOldJson = userOld.toJson();
        ReflectUtil.copyObjectToObjectNotNull(userNew, userOld);

        userService.updateUser(userOld, new Long[0], dto.getUserInfo().getUserid());

        if (!oldUserName.equals(newUserName)) {
            List<Position> ps = hibernateDao.createQuery("select distinct p from " + getEntityClassName(Position.class)
                            + " p,UserPosition up where p.positiontype=? and p.positionid=up.id.taposition.positionid and up.id.tauser.userid=?",
                    "2", dto.getAsLong("userid")).list();
            for (Position position : ps) {
                position.setPositionname(newUserName);
                hibernateDao.update(position);
            }
        }

        orgOpLogService.logUserOp(Long.valueOf(getStringSeq()), dto.getUserInfo(), "05", userOld, userOldJson + "-->" + userOld.toJson());
        TaEventPublisher.publishEvent(new EventSource(userOld, dto), "user_update");
    }

    public void unUseUser(Long batchNo, Long userid, ParamDTO dto) {
        if (userid == null)
            throw new AppException("错误的人员编号");
        userService.unUseUser(userid, dto.getUserInfo().getUserid(), timeService.getSysTimestamp());

        IUser user = organizationEntityService.getUserByUserId(userid);
        orgOpLogService.logUserOp(batchNo, dto.getUserInfo(), "06", user, "");
        TaEventPublisher.publishEvent(new EventSource(user, dto), "user_unuse");
    }

    public void unBatchUseUser(Long[] userids, ParamDTO dto) {
        Long batchNo = Long.valueOf(getStringSeq());
        for (Long userid : userids) {
            User user = getUser(userid);

            checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), user.getUserid(), "06", "02", "");
            unUseUser(batchNo, userid, dto);
        }
    }

    public void reUser(Long batchNo, Long userid, ParamDTO dto) {
        Org org = (Org) organizationEntityService.getDepartByUserId(userid);
        if ("1".equals(org.getEffective())) {
            throw new AppException("该用户所在组织已被禁用，不能启用该人员");
        }
        userService.reUseUser(userid, dto.getUserInfo().getUserid(), timeService.getSysTimestamp());

        IUser user = organizationEntityService.getUserByUserId(userid);
        orgOpLogService.logUserOp(batchNo, dto.getUserInfo(), "16", user, "");
        TaEventPublisher.publishEvent(new EventSource(user, dto), "user_use");
    }

    public void batchReUser(Long[] userids, ParamDTO dto) {
        Long batchNo = Long.valueOf(getStringSeq());
        for (Long userid : userids) {
            User user = getUser(userid);

            checkOrg(dto.getUserInfo().getUserid(), dto.getUserInfo().getNowPosition().getPositionid(), user.getUserid(), "16", "02", "");
            reUser(batchNo, userid, dto);
        }
    }

    private boolean checkUserPersonalPositionInCurrentUserPosition(Long userid) {
        return true;
    }

    @Deprecated
    public List<UserInfoVO> queryUsers(ParamDTO dto) {
        if (dto.isEmpty("orgid")) {
            dto.put("orgid", dto.getUserInfo().getNowPosition().getOrgid());
        } else if (!checkOrgInUserCurrentPositionOrg(dto)) {
            throw new AppException("非法组织");
        }

        StringBuffer sql = new StringBuffer(
                "select new com.yinhai.ta3.system.org.domain.UserInfoVO(u.userid,u.name,u.loginid,u.sex,u.tel,p.orgnamepath,p.positionname,p.positiontype) from Org o, User u ,UserPosition up, Position p where 1=1");
        if (dto.isNotEmpty("name"))
            sql.append(" and u.name=:name ");
        sql.append(" and o.orgid=:orgid ").append(" and p.orgidpath like o.orgidpath||'%'").append(" and p.effective=").append("0")
                .append(" and p.positionid = up.id.taposition.positionid").append(" and up.id.tauser.userid = u.userid")
                .append(" order by u.loginid,p.orgnamepath");

        Query createQuery = hibernateDao.createQuery(sql.toString(), new Object[0]);
        createQuery.setLong("orgid", dto.getAsLong("orgid").longValue());

        if (dto.isNotEmpty("name")) {
            createQuery.setString("name", dto.getAsString("name"));
        }

        return createQuery.list();
    }

    public PageBean queryUsersInfo(ParamDTO dto, String gdid, int start, int limit) {
        User user = (User) dto.toDomainObject(getEntityClassName(User.class));

        if (dto.isEmpty("orgid"))
            dto.put("orgid", Org.ORG_ROOT_ID);
        user.setUserid(dto.getUserInfo().getUserid());
        PageBean pg = null;

        if ("true".equals(dto.getAsString("isShowSubOrg"))) {
            pg = userService.queryUsers(user, dto.getAsLong("orgid"), dto.getUserInfo().getNowPosition().getPositionid(), true, start, limit);
        } else {
            pg = userService.queryUsers(user, dto.getAsLong("orgid"), dto.getUserInfo().getNowPosition().getPositionid(), false, start, limit);
        }
        pg.setList(buildUserInfo(pg.getList()));
        pg.setGridId(gdid);
        return pg;
    }

    private List buildUserInfo(List list) {
        List l = new ArrayList();
        String username = SysConfig.getSysConfig(User.class.getName(), User.class.getName());
        if (ValidateUtil.isEmpty(list)) {
            return l;
        }
        for (Object o : list) {
            Field[] pField = null;
            Field[] sField = null;
            if (User.class.getName().equals(username)) {
                pField = o.getClass().getDeclaredFields();
            } else {
                pField = o.getClass().getSuperclass().getDeclaredFields();
                sField = o.getClass().getDeclaredFields();
            }
            Map map = new HashMap();
            Arrays.stream(pField).forEach(field -> {
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), o.getClass());
                    Object invoke = pd.getReadMethod().invoke(o);
                    if (invoke != null) {
                        if ("directorgid".equals(field.getName())) {
                            String orgnamepath = (String) hibernateDao.createQuery("select o.orgnamepath from " + getEntityClassName(Org.class) + " o where o.orgid=?", invoke).uniqueResult();
                            map.put("orgnamepath", orgnamepath);
                        } else {
                            map.put(field.getName(), invoke.toString());
                        }
                    }
                } catch (Exception e) {
                }
            });

            if (!ValidateUtil.isEmpty(sField)) {
                Arrays.stream(sField).forEach(field -> {
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor(field.getName(), o.getClass());
                        Object invoke = pd.getReadMethod().invoke(o);
                        if (invoke != null) {
                            map.put(field.getName(), invoke.toString());
                        }
                    } catch (Exception e) {
                    }
                });
            }

            l.add(map);
        }

        return l;
    }

    public User getUser(Long userid) {
        return (User) organizationEntityService.getUserByUserId(userid);
    }

    public User getUser(String loginid) {
        return (User) organizationEntityService.getUserByLoginId(loginid);
    }

    public boolean checkSameUserId(ParamDTO dto) {
        String loginId = dto.getAsString("loginid");
        Query users = hibernateDao
                .createQuery("from " + getEntityClassName(User.class) + " u where u.loginid=:loginid and (u.destory is null or u.destory=:destory)")
                .setParameter("loginid", loginId).setString("destory", "1");

        if (users.list().size() > 0) {
            return true;
        }
        return false;
    }

    public List<Object[]> queryUserPositions(ParamDTO dto) {
        return null;
    }

    public List<Object[]> queryUserPerrmission(ParamDTO dto) {
        return null;
    }

    public boolean checkOrgInUserCurrentPositionOrg(ParamDTO dto) {
        Long orgidNow = dto.getUserInfo().getNowPosition().getOrgid();

        StringBuffer hql = new StringBuffer("select count(so) from Org o, Org so where 1=1");
        hql.append(" and o.orgid=? ").append(" and so.orgid=? ").append(" and so.effective=? ").append(" and so.orgidpath like o.orgidpath||'%'");

        List<Object[]> list = hibernateDao.createQuery(hql.toString(), orgidNow, dto.getAsLong("orgid"), "0").list();
        if (list.size() > 0)
            return true;
        return false;
    }

    public List<Position> getCurrentUserCanDistributionUserPersitions(ParamDTO dto) {
        if (dto.isEmpty("userid"))
            throw new AppException("用户无效");
        if (ValidateUtil.isEmpty(dto.getUserInfo())) {
            throw new AppException("无登陆用户");
        }

        List<UserPosition> ups = hibernateDao.createQuery("from UserPosition up where up.id.tauser.userid = ?", dto.getAsLong("userid")).list();

        dto.put("orgidpath", dto.getUserInfo().getNowPosition().getOrgidpath());
        List<PositionInfoVO> cpp = getCurrentUserCanDistributionPositionsByOrg(dto);

        List<Position> ps = new LinkedList<>();
        cpp.forEach(pi -> {
            ups.forEach(up ->{
                if (pi.getPositionid().equals(up.getId().getTaposition().getPositionid()))
                    ps.add(up.getId().getTaposition());
            });
        });

        return ps;
    }

    public List<PositionInfoVO> getCurrentUserCanDistributionPositionsByOrg(ParamDTO dto) {
        return null;
    }

    public List<PositionInfoVO> getCurrentUserCanDistributionUserPositionsByOrg(ParamDTO dto) {
        if (ValidateUtil.isEmpty(dto.getUserInfo()))
            throw new AppException("无登陆用户");
        if (ValidateUtil.isEmpty(dto.getAsString("orgidpath"))) {
            throw new AppException("组织编号为空");
        }
        List<Position> cup = getCurrentUserCanDistributionUserPersitions(dto);
        List<PositionInfoVO> cupbo = getCurrentUserCanDistributionPositionsByOrg(dto);
        List<PositionInfoVO> re = new LinkedList<>();
        cupbo.forEach(positionInfoVO -> {
            cup.forEach(up ->{
                if (positionInfoVO.getPositionid().equals(up.getPositionid()))
                    re.add(positionInfoVO);
            });
        });
        cupbo.removeAll(re);
        return cupbo;
    }

    public List<UserPosition> createUserPositionRefrences(ParamDTO dto) {
        if (ValidateUtil.isEmpty(dto.getAsLong("userid")))
            throw new AppException("用户编号错误");
        if (ValidateUtil.isEmpty(dto.get("positions"))) {
            throw new AppException("组织不存在");
        }
        List<Key> prositions = (List) dto.get("positions");
        for (Key key : prositions) {
            ParamDTO pdto = new ParamDTO();
            pdto.append("userid", dto.getAsLong("userid"));
            pdto.append("positionid", key.getAsLong("positionid"));
            createUserPositionRefrence(pdto);
        }
        return null;
    }

    public UserPosition removeUserPosition(ParamDTO dto) {
        Long userid = dto.getAsLong("userid");
        Long positionid = dto.getAsLong("positionid");
        if (ValidateUtil.isEmpty(userid))
            throw new AppException("用户编号错误");
        if (ValidateUtil.isEmpty(positionid)) {
            throw new AppException("组织不存在");
        }
        UserPosition up = new UserPosition();
        UserPositionId upid = new UserPositionId();
        User user = new User();
        Position ps = new Position();
        user.setUserid(userid);
        ps.setPositionid(positionid);
        upid.setTaposition(ps);
        upid.setTauser(user);
        up.setId(upid);
        hibernateDao.delete(up);

        return up;
    }

    public List<UserPerrmissionVO> queryUserPerrmission(Long userid) {
        StringBuffer sbf = new StringBuffer();
        sbf.append("select distinct new com.yinhai.ta3.system.org.domain.UserPerrmissionVO(m.menuname,m.url, m.resourcetype, pa.usepermission, pa.repermission, pa.reauthrity, p.positionname, u1.name, pa.createtime, p.orgnamepath,pa.auditstate,m.menuid)")
                .append(" from ").append(getEntityClassName(User.class)).append(" u,").append(getEntityClassName(User.class))
                .append(" u1,UserPosition up,").append(getEntityClassName(Position.class)).append(" p,").append("PositionAuthrity pa,")
                .append(getEntityClassName(Menu.class)).append(" m").append(" where 1=1 and u.userid=?").append(" and u.createuser=u1.userid")
                .append(" and u.userid=up.id.tauser.userid").append(" and up.id.taposition.positionid=p.positionid").append(" and p.effective=?")
                .append(" and (p.validtime is null or p.validtime >=?)").append(" and p.positionid=pa.id.taposition.positionid")
                .append(" and pa.id.tamenu.menuid=m.menuid").append(" and m.effective=?").append(" order by p.positionname");

        return hibernateDao.createQuery(sbf.toString(),userid, "0", timeService.getSysDate(), "0").list();
    }

    public List<Position> queryUserPersionalPostions(Long userid) {
        return hibernateDao.createQuery("select p from " + getEntityClassName(Position.class)
                                + " p, UserPosition up where up.id.taposition.positionid=p.positionid and up.id.tauser.userid=? and p.positiontype=? and p.effective=?",
                        userid, "2", "0").list();
    }

    public List<Position> queryUserPostions(Long userid) {
        return hibernateDao.createQuery("select p from  " + getEntityClassName(Position.class)
                        + " p, UserPosition up where up.id.taposition.positionid=p.positionid and up.id.tauser.userid=? and p.effective=?",
                userid, "0").list();
    }

    public void resetPassword(ParamDTO dto) {
        List users = (List) dto.get("users");
        Long batchNo = getLongSeq();
        for (int i = 0; i < users.size(); i++) {
            Long userid = ((Key) users.get(i)).getAsLong("userid");
            if (userid == null || ValidateUtil.isEmpty(dto.getAsString("newPassword"))) {
                continue;
            }
            User u = getUser(userid);
            String loginid = u.getLoginid();
            String encodePassword = md5PasswordEncoder.encodePassword(dto.getAsString("newPassword"), loginid);
            u.setPassword(encodePassword);
            if ("true".equals(dto.getAsString("firstFlag"))) {
                u.setPwdlastmodifydate(getSysTimestamp());
            } else {
                u.setPwdlastmodifydate(null);
            }
            hibernateDao.update(u);

            orgOpLogService.logUserOp(batchNo, dto.getUserInfo(), "07", u, "");
        }
    }

    public void batchChangeUserOrg(Long[] userids, ParamDTO dto) {
        for (int i = 0; i < userids.length; i++) {
            List<IPosition> list = organizationEntityService.getPerPositionsByUserId(userids[i]);
            for (IPosition position : list) {
                positionMgService.unUsePosition(position.getPositionid(), dto, "0");
            }
            User user = getUser(userids[i]);
            Position position = null;

            IPosition createPosition = dto.getUserInfo().getNowPosition();
            position = new Position();
            position.setPositionname(user.getName());
            position.setPositiontype("2");
            position.setCreateuser(user.getCreateuser());
            position.setCreatetime(user.getCreatetime());
            position.setEffective("0");
            position.setCreatepositionid(createPosition.getPositionid());
            positionService.createPosition(position, dto.getAsLong("orgid"));

            UserPosition up = new UserPosition();
            UserPositionId upid = new UserPositionId();
            up.setMainposition("1");
            up.setCreateuser(user.getCreateuser());
            up.setCreatetime(user.getCreatetime());
            upid.setTaposition(position);
            upid.setTauser(user);
            up.setId(upid);
            hibernateDao.save(up);
        }
    }

    public void updateDirectAndAffiliatedOrgs(Long orgid, List<Key> ids, Long userid, IUser userInfo) {
        if (ValidateUtil.isEmpty(orgid)) {
            throw new AppException("直属组织不能为空");
        }
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("人员id为空,不能分配组织");
        }
        User u = getUser(userid);

        checkOrg(userInfo.getUserid(), userInfo.getNowPosition().getPositionid(), u.getDirectorgid(), "02", "01", "更改组织，无权操作该人员所在的直属组织");

        if (!orgid.equals(u.getDirectorgid())) {
            checkOrg(userInfo.getUserid(), userInfo.getNowPosition().getPositionid(), orgid, "02", "01", "更改直属组织，无权操作该组织");

            List<UserPosition> ups = hibernateDao.createQuery(
                    "select up from UserPosition up," + getEntityClassName(Position.class)
                            + " p where up.id.taposition.positionid=p.positionid and up.id.tauser.userid=? and p.taorg.orgid=? and p.positiontype=?",
                    userid, u.getDirectorgid(), "2").list();
            if (ups != null) {
                Long positionid = ((UserPosition) ups.get(0)).getId().getTaposition().getPositionid();
                hibernateDao.createQuery("delete from UserPosition up where up.id.taposition.positionid=? and up.id.tauser.userid=?",
                        new Object[]{positionid, ((UserPosition) ups.get(0)).getId().getTauser().getUserid()}).executeUpdate();
                List<PositionAuthrity> list = hibernateDao.createQuery("from PositionAuthrity pa where pa.id.taposition.positionid=?",
                        new Object[]{positionid}).list();
                for (PositionAuthrity pa : list) {
                    grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(), positionid, userInfo
                            .getUserid(), timeService.getSysTimestamp(), "0", "6"));
                }

                hibernateDao.createQuery("update " + getEntityClassName(Org.class) + " o set o.orgmanager=null where o.orgmanager=?",
                        new Object[]{positionid}).executeUpdate();
                hibernateDao.createQuery("delete from ManagerMg mm where mm.id.positionid=?", new Object[]{positionid}).executeUpdate();
                hibernateDao.createQuery("delete from OrgMg om where om.id.positionid=?", new Object[]{positionid}).executeUpdate();
                hibernateDao
                        .createQuery("delete from " + getEntityClassName(Position.class) + " p where p.positionid=?", new Object[]{positionid})
                        .executeUpdate();
                Position position = null;
                try {
                    position = (Position) Class.forName(getEntityClassName(Position.class.getName())).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                position.setPositionname(u.getName());
                position.setPositiontype("2");
                position.setCreateuser(u.getCreateuser());
                position.setCreatetime(u.getCreatetime());
                position.setEffective("0");
                position.setCreatepositionid(userInfo.getNowPosition().getPositionid());

                Position position2 = positionService.createPosition(position, orgid);

                UserPosition up = new UserPosition();
                UserPositionId upid = new UserPositionId();

                List upList = hibernateDao.createQuery("from UserPosition up where up.id.tauser.userid=? and up.mainposition=?", userid, "1").list();
                if (upList != null) {
                    up.setMainposition("0");
                } else {
                    up.setMainposition("1");
                }
                up.setCreatetime(u.getCreatetime());
                up.setCreateuser(u.getUserid());
                upid.setTaposition(position2);
                upid.setTauser(u);
                up.setId(upid);
                hibernateDao.save(up);
                u.setDirectorgid(orgid);
                hibernateDao.update(u);
            }
        }

        if (!ValidateUtil.isEmpty(ids)) {
            for (Key key : ids) {
                checkOrg(userInfo.getUserid(), userInfo.getNowPosition().getPositionid(), key.getAsLong("id"), "02", "01", "更改附属组织，无权操作该组织");
                if ("true".equals(key.getAsString("checked"))) {
                    Position position = new Position();
                    position.setPositionname(u.getName());
                    position.setPositiontype("2");
                    position.setCreateuser(u.getCreateuser());
                    position.setCreatetime(u.getCreatetime());
                    position.setEffective("0");
                    position.setCreatepositionid(userInfo.getNowPosition().getPositionid());

                    Position position2 = positionService.createPosition(position, key.getAsLong("id"));

                    UserPosition up = new UserPosition();
                    UserPositionId upid = new UserPositionId();
                    up.setMainposition("0");
                    up.setCreatetime(u.getCreatetime());
                    up.setCreateuser(u.getUserid());
                    upid.setTaposition(position2);
                    upid.setTauser(u);
                    up.setId(upid);
                    hibernateDao.save(up);
                } else if ("false".equals(key.getAsString("checked"))) {
                    List<UserPosition> ups = hibernateDao.createQuery("select up from UserPosition up," + getEntityClassName(Position.class)
                                            + " p where up.id.taposition.positionid=p.positionid and up.id.tauser.userid=? and p.taorg.orgid=? and p.positiontype=?",
                                    userid, key.getAsLong("id"), "2").list();
                    if (ups != null) {
                        Long positionid = ((UserPosition) ups.get(0)).getId().getTaposition().getPositionid();
                        hibernateDao.createQuery("delete from UserPosition up where up.id.taposition.positionid=? and up.id.tauser.userid=?",
                                positionid, ((UserPosition) ups.get(0)).getId().getTauser().getUserid()).executeUpdate();
                        List<PositionAuthrity> list = hibernateDao.createQuery("from PositionAuthrity pa where pa.id.taposition.positionid=?", positionid).list();
                        for (PositionAuthrity pa : list) {
                            grantService.permissionChangeUniteFunction(new PermissionInfoVO(pa.getId().getTamenu().getMenuid(), positionid, userInfo
                                    .getUserid(), timeService.getSysTimestamp(), "0", "6"));
                        }

                        hibernateDao.createQuery("delete from ManagerMg mm where mm.id.positionid=?", positionid).executeUpdate();
                        hibernateDao.createQuery("delete from OrgMg om where om.id.positionid=?", positionid).executeUpdate();
                        hibernateDao.createQuery("delete from " + getEntityClassName(Position.class) + " p where p.positionid=?", positionid).executeUpdate();
                    }
                }
            }
            List upList = hibernateDao.createQuery("from UserPosition up where up.id.tauser.userid=? and up.mainposition=?", userid, "1").list();
            if ((upList == null) || (upList.size() <= 0)) {
                IPosition position = organizationEntityService.getPositionByUserId(userid);
                hibernateDao.createQuery("update UserPosition up set up.mainposition=? where up.id.tauser.userid=? and up.id.taposition.positionid=?", "1", userid, position.getPositionid()).executeUpdate();
            }
        }

        Map map = new HashMap();
        map.put("orgid", orgid);
        map.put("orgids", ids);
        map.put("userid", userid);
        TaEventPublisher.publishEvent(new EventSource(map), "org_changeOrg");
    }

    public PageBean queryUserOpLogs(String gridId, ParamDTO dto) {
        Long userid = dto.getAsLong("userid");
        if (ValidateUtil.isEmpty(userid)) {
            throw new AppException("用户为空，不能进行日志查询");
        }
        StringBuffer sb = new StringBuffer();
        sb.append("from OrgOpLog ool where ool.opuser=:userid and ool.ispermission is null ");
        if (!ValidateUtil.isEmpty(dto.get("logStartTime"))) {
            sb.append(" and ool.optime >=:logStartTime");
        }
        if (!ValidateUtil.isEmpty(dto.get("logEndTime"))) {
            sb.append(" and ool.optime <=:logEndTime");
        }
        sb.append(" order by ool.batchno");
        Integer skipResults = Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
        Integer maxResults = Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
        PageBean pb = new PageBean();
        pb.setStart(skipResults);
        pb.setLimit(maxResults);
        Query query = hibernateDao.createQuery(sb.toString()).setFirstResult(skipResults.intValue())
                .setMaxResults(maxResults.intValue());
        Finder finder = Finder.create(sb.toString());
        String countHql = finder.getRowCountHql();
        Query queryCount = hibernateDao.createQuery(countHql);
        query.setLong("userid", userid.longValue());
        queryCount.setLong("userid", userid.longValue());
        if (!ValidateUtil.isEmpty(dto.get("logStartTime"))) {
            query.setTimestamp("logStartTime", dto.getAsTimestamp("logStartTime"));
            queryCount.setDate("logStartTime", dto.getAsTimestamp("logStartTime"));
        }
        if (!ValidateUtil.isEmpty(dto.get("logEndTime"))) {
            query.setTimestamp("logEndTime", dto.getAsTimestamp("logEndTime"));
            queryCount.setDate("logEndTime", dto.getAsTimestamp("logEndTime"));
        }
        List<OrgOpLog> list = query.list();
        List<OrgLogInfoVO> listvo = new ArrayList<>();
        for (OrgOpLog log : list) {
            OrgLogInfoVO vo = new OrgLogInfoVO();
            vo.setBatchno(log.getBatchno());
            vo.setOpsubjekt(log.getOpsubjekt());
            vo.setChangcontent(log.getChangcontent());

            vo.setOpbody(log.getOpbody());
            vo.setOptime(log.getOptime());
            vo.setOptype(log.getOptype());
            User opUser = (User) hibernateDao.getSession().get(getEntityClassName(User.class), log.getOpuser());
            vo.setOpusername(opUser.getName());
            Position opPosition = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), log.getOpposition());
            vo.setOppositionname(opPosition.getOrgnamepath() + "/" + opPosition.getPositionname());
            if ("01".equals(log.getOpbody())) {
                if ("18".equals(log.getOptype())) {
                    vo.setChangcontent(log.getChangcontent());
                } else {
                    Org o = (Org) hibernateDao.getSession().get(getEntityClassName(Org.class), log.getOpsubjekt());
                    if (ValidateUtil.isEmpty(o)) {
                        vo.setOpsubjektname(log.getChangcontent());
                    } else {
                        vo.setOpsubjektname(o.getOrgnamepath());
                    }
                }
            } else if ("04".equals(log.getOpbody())) {
                Menu m = (Menu) hibernateDao.getSession().get(getEntityClassName(Menu.class), log.getOpsubjekt());
                if (!ValidateUtil.isEmpty(m)) {

                    vo.setOpsubjektname(m.getMenunamepath());
                }
            } else if ("03".equals(log.getOpbody())) {
                Position p = (Position) hibernateDao.getSession().get(getEntityClassName(Position.class), log.getOpsubjekt());
                if (ValidateUtil.isEmpty(p)) {
                    vo.setOpsubjektname(log.getChangcontent());
                } else {
                    vo.setOpsubjektname(p.getOrgnamepath() + "/" + p.getPositionname());
                }
            } else if ("02".equals(log.getOpbody())) {
                User u = (User) hibernateDao.getSession().get(getEntityClassName(User.class), log.getOpsubjekt());
                if (ValidateUtil.isEmpty(u)) {
                    vo.setOpsubjektname(log.getChangcontent());
                } else {
                    vo.setOpsubjektname(u.getName());
                }
            }
            listvo.add(vo);
        }
        Long total = (Long) queryCount.uniqueResult();
        pb.setTotal(Integer.valueOf(total.intValue()));
        pb.setList(listvo);
        return pb;
    }

    public void unLockUser(Long userid, IUser opUser) {
        User u = getUser(userid);

        checkOrg(opUser.getUserid(), opUser.getNowPosition().getPositionid(), u.getUserid(), "05", "02", "解锁用户，无权操作该用户�?��组织");
        if (u.getPasswordfaultnum().intValue() >= SysConfig.getSysConfigToInteger("passwordMaxFaultNumber", 3).intValue()) {
            u.setPasswordfaultnum(Integer.valueOf(0));
        }
        u.setIslock("0");
        hibernateDao.update(u);
    }

    public List<AppCode> queryDataField(Long userid, Long menuid) {
        return organizationEntityService.queryYab139ByUserIdAndMenuId(userid, menuid);
    }

    public void setHibernateDao(SimpleDao hibernateDao) {
        this.hibernateDao = hibernateDao;
    }

    public SimpleDao getHibernateDao() {
        return hibernateDao;
    }

    public void setTimeService(ITimeService timeService) {
        this.timeService = timeService;
    }

    public void setPositionMgService(IPositionMgService positionMgService) {
        this.positionMgService = positionMgService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
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

    public Md5PasswordEncoder getMd5PasswordEncoder() {
        return md5PasswordEncoder;
    }

    public void setMd5PasswordEncoder(Md5PasswordEncoder md5PasswordEncoder) {
        this.md5PasswordEncoder = md5PasswordEncoder;
    }

    public void setOrganizationEntityService(OrganizationEntityService organizationEntityService) {
        this.organizationEntityService = organizationEntityService;
    }

}
