package com.yinhai.sysframework.security;

import java.util.List;
import java.util.Set;

import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.ta3.redis.annotation.LapseMethod;

public interface IPermissionService {

    String SERVICEKEY = "permissionService";

    List<IMenu> getUserPermissionMenus(Long userId);

    List<IMenu> getUserPermissionMenus(Long userId, Long positionId);

    Set<String> getUserPermissionUrl(Long userId);

    Set<String> getUserPermissionUrl(Long userId, Long positionId);

    boolean isAdministrator(IPosition p);

    List<IPosition> getPositionsByMenu(Long userid, Long menuid);

    List<IPosition> getUserEffectivePositions(Long userid);

    @LapseMethod(name = "getUserPermissionMenus")
    void clearUserPermissionMenusCache(Long userid);

    void clearUserPermissionMenusCache(Long userid, Long positionId);

    void clearUserPermissionUrlCache(Long userid);

    void clearUserPermissionUrlCache(Long userid, Long positionId);

    void clearPositionsByMenuCache(Long userid, Long menuid);

    void clearUserEffectivePositionsCache(Long userid);
}
