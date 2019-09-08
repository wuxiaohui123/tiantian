package com.yinhai.ta3.system.sysapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import com.yinhai.sysframework.config.IConfigSyspath;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.config.service.IConfigService;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.ta3.redis.annotation.CacheMethod;
import com.yinhai.ta3.redis.annotation.LapseMethod;
import com.yinhai.ta3.system.org.dao.PositionDao;
import com.yinhai.ta3.system.org.dao.UserDao;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.sysapp.dao.MenuDao;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@SuppressWarnings("unchecked")
public class PermissionService implements IPermissionService {

    private SimpleDao hibernateDao;
    private MenuDao menuDao;
    private UserDao userDao;
    private PositionDao positionDao;
    private ITimeService timeService;

    @CacheMethod(expires = 28800)
    public List<IMenu> getUserPermissionMenus(Long userId) {
        Assert.notNull(userId, "userId不能为空");

        Date curdate = timeService.getSysDate();
        List<IPosition> pList = positionDao.getUserEffectivePosition(userId, curdate);
        for (IPosition p : pList) {
            if (isAdministrator(p)) {
                return menuDao.getEffectiveMenus();
            }
        }
        return menuDao.getUserPermissionMenus(userId, curdate);
    }

    @LapseMethod(name = "getUserPermissionMenus")
    public void clearUserPermissionMenusCache(Long userid) {
    }

    @CacheMethod(expires = 28800)
    public List<IMenu> getUserPermissionMenus(Long userId, Long positionId) {
        Assert.notNull(userId, "userId不能为空");
        Assert.notNull(positionId, "positionId不能为空");

        Date curdate = timeService.getSysDate();
        List<IPosition> pList = positionDao.getUserEffectivePosition(userId, curdate);
        for (IPosition p : pList) {
            if (isAdministrator(p)) {
                return menuDao.getEffectiveMenus();
            }
        }
        return menuDao.getUserPermissionMenus(userId, curdate, positionId);
    }

    @LapseMethod(name = "getUserPermissionMenus")
    public void clearUserPermissionMenusCache(Long userid, Long positionId) {
    }

    public boolean isAdministrator(IPosition p) {
        Assert.notNull(p, "岗位为空");
        return IPosition.ADMIN_POSITIONID.equals(p.getPositionid());
    }

    @CacheMethod(expires = 28800)
    public Set<String> getUserPermissionUrl(Long userId) {
        Assert.notNull(userId, "UserId为空");

        IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
        List<IConfigSyspath> syslist = configService.getConfigSysPaths();
        String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
        boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);

        List<IPosition> pList = positionDao.getUserEffectivePosition(userId, timeService.getSysDate());
        for (IPosition p : pList) {
            if (isAdministrator(p)) {
                List<String> find = new ArrayList<String>();

                if (isPortal) {
                    find = hibernateDao.find("select distinct '/'||c.url from " + getMenuName() + " c where c.effective=?", new Object[]{"0"});
                } else if ((syslist != null) && (syslist.size() > 1)) {
                    find = hibernateDao.find("select distinct '/'||c.url from " + getMenuName() + " c where c.effective=? and c.syspath=?", new Object[]{"0", curSyspathId});
                } else {
                    find = hibernateDao.find("select distinct '/'||c.url from " + getMenuName() + " c where c.effective=?", new Object[]{"0"});
                }
                Set<String> set = new HashSet<String>();
                for (String s : find) {
                    set.add(StringUtil.delUrlParam(s));
                }
                return set;
            }
        }
        Set<String> effectiveUrls = menuDao.getEffectiveUrls(userId, timeService.getSysDate());
        Set<String> setret = new HashSet<String>();
        for (String url : effectiveUrls) {
            setret.add(StringUtil.delUrlParam(url));
        }
        return setret;
    }

    @LapseMethod(name = "getUserPermissionUrl")
    public void clearUserPermissionUrlCache(Long userid) {
    }

    @CacheMethod(expires = 28800)
    public Set<String> getUserPermissionUrl(Long userId, Long positionId) {
        Assert.notNull(userId);

        IConfigService configService = (IConfigService) ServiceLocator.getService("configService");
        List<IConfigSyspath> syslist = configService.getConfigSysPaths();
        String curSyspathId = SysConfig.getSysConfig("curSyspathId", "sysmg");
        boolean isPortal = SysConfig.getSysconfigToBoolean("isPortal", false);

        List<IPosition> pList = positionDao.getUserEffectivePosition(userId, timeService.getSysDate());
        for (IPosition p : pList) {
            if (isAdministrator(p)) {
                List<String> find = new ArrayList<String>();
                Set<String> set = new HashSet<String>();
                if (isPortal) {
                    find = hibernateDao.find("select distinct '/'||c.url from " + getMenuName() + " c where c.effective=?", new Object[]{"0"});
                } else if ((syslist != null) && (syslist.size() > 1)) {
                    find = hibernateDao.find("select distinct '/'||c.url from " + getMenuName() + " c where c.effective=? and c.syspath=?", new Object[]{"0", curSyspathId});
                } else {
                    find = hibernateDao.find("select distinct '/'||c.url from " + getMenuName() + " c where c.effective=?", new Object[]{"0"});
                }
                for (String s : find) {
                    set.add(StringUtil.delUrlParam(s));
                }
                return set;
            }
        }
        Set<String> effectiveUrls = menuDao.getEffectiveUrls(userId, positionId, timeService.getSysDate());
        Set<String> setret = new HashSet<String>();
        for (String url : effectiveUrls) {
            setret.add(StringUtil.delUrlParam(url));
        }
        return setret;
    }

    @LapseMethod(name = "getUserPermissionUrl")
    public void clearUserPermissionUrlCache(Long userid, Long positionId) {
    }

    @CacheMethod
    public List<IPosition> getPositionsByMenu(Long userid, Long menuid) {
        String positionClassName = SysConfig.getSysConfig(Position.class.getName(), Position.class.getName());
        StringBuilder hql = new StringBuilder();
        hql.append("select c from PositionAuthrity b," + positionClassName + " c,UserPosition d").append(" where").append(" d.id.tauser.userid=?")
                .append(" and b.id.tamenu.menuid=?").append(" and b.usepermission=?").append(" and d.id.taposition.positionid = c.positionid")
                .append(" and c.positionid = b.id.taposition.positionid").append(" and c.effective=?")
                .append(" and (c.validtime is null or c.validtime >=?)");

        return hibernateDao.createQuery(hql.toString(), new Object[]{userid, menuid, "1", "0", timeService.getSysDate()}).list();
    }

    @LapseMethod(name = "getPositionsByMenu")
    public void clearPositionsByMenuCache(Long userid, Long menuid) {
    }

    @CacheMethod
    public List<IPosition> getUserEffectivePositions(Long userid) {
        List<IPosition> list = positionDao.getUserEffectivePosition(userid, timeService.getSysDate());
        return list;
    }

    @LapseMethod(name = "getUserEffectivePositions")
    public void clearUserEffectivePositionsCache(Long userid) {
    }

    public void setMenuDao(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    public void setTimeService(ITimeService timeService) {
        this.timeService = timeService;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setPositionDao(PositionDao positionDao) {
        this.positionDao = positionDao;
    }

    public void setHibernateDao(SimpleDao hibernateDao) {
        this.hibernateDao = hibernateDao;
    }

    private String getMenuName() {
        return SysConfig.getSysConfig(Menu.class.getName(), Menu.class.getName());
    }
}
