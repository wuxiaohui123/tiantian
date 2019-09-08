package com.yinhai.sysframework.security.ta3;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yinhai.sysframework.util.*;
import nl.bitwalker.useragentutils.UserAgent;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yinhai.sysframework.cache.ehcache.service.ServerAddressService;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.security.IPermissionService;
import com.yinhai.sysframework.security.OnlineSessionInfo;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.time.ITimeService;

@SuppressWarnings({"deprecation", "resource"})
public class UserLoginHelper implements IUserLogin {

    private static Logger log = LogManager.getLogger(UserLoginHelper.class);

    public static final String LOGIN_SESSIONID = "login_sessionid";

    public static final String LOGIN_USERID = "login_userid";

    public static final String LOGIN_CLIENTIP = "login_clientip";
    public static final String LOGIN_SERVERIP = "login_serverip";
    public static final String LOGIN_SESSIONTIME = "login_sessiontime";
    public static final String LOGIN_END_ACCESS_SESSIONTIME = "login_end_access_sessiontime";
    IPermissionService permissionService;
    ILoadUserAccountInfo loadUserAccountInfo;
    Md5PasswordEncoder md5PasswordEncoder;
    ITimeService timeService;
    IFailLoginCheckUser failLoginCheckUser;
    ServerAddressService serverAddressService;

    public void setFailLoginCheckUser(IFailLoginCheckUser failLoginCheckUser) {
        this.failLoginCheckUser = failLoginCheckUser;
    }

    public void loginCheck(IUserAccountInfo userAccount, String password) throws AppException {
        if (userAccount != null) {
            String loginid = userAccount.getLoginId();
            if (!userAccount.isAccountNonLocked())
                throw new AppException("帐户被锁定,不能登录", "j_username");
            if (!userAccount.isEnabled())
                throw new AppException("帐户被禁用,不能登录", "j_username");
            if ((SysConfig.getSysConfigToInteger("passwordMaxFaultNumber", 3).intValue() > 0)
                    && (userAccount.getPasswordfaultnum() >= SysConfig.getSysConfigToInteger("passwordMaxFaultNumber", 3).intValue())) {

                failLoginCheckUser.setUserLocked(userAccount.getUser().getUserid());

                throw new AppException("密码输入错误次数大于" + SysConfig.getSysConfig("passwordMaxFaultNumber") + "次，账号被锁定", "j_username");
            }

            if (!md5PasswordEncoder.isPasswordValid(userAccount.getPassword(), password, loginid)) {
                int passwordfaultnum = userAccount.getPasswordfaultnum();
                failLoginCheckUser.updateUserFaultNum(userAccount.getUser().getUserid(), passwordfaultnum + 1);

                throw new AppException("密码不正确,不能登录", "j_username");
            }
            if ((SysConfig.getSysconfigToBoolean("passwordFirstlogin")) && (userAccount.isAccountFirstLogin())) {
                throw new AppException("您第一次登录系统，请修改密码后再进行登录", "j_username");
            }
            if (SysConfig.getSysConfigToInteger("passwordUsefulLife", 30).intValue() > 0) {
                Date validDate = null;
                if (ValidateUtil.isEmpty(userAccount.lastModifyDate())) {
                    validDate = userAccount.getCreateUserDate();
                } else {
                    validDate = userAccount.lastModifyDate();
                }
                if (DateUtil.computeDateOnly(timeService.getSysDate(), validDate, true) > SysConfig.getSysConfigToInteger("passwordUsefulLife", 30)
                        .intValue()) {

                    throw new AppException("超过有效日期:" + SysConfig.getSysConfig("passwordUsefulLife") + "天,请修改密码后登录", "j_username");
                }
            }

            if (userAccount.getPasswordfaultnum() > 0) {
                failLoginCheckUser.updateUserFaultNum(userAccount.getUser().getUserid(), 0);
            }
        } else {
            throw new AppException("用户不存在，不能登录", "j_username");
        }
    }

    public void regesitUserAccount(IUserAccountInfo userAccount, HttpServletRequest request) {
        List<IPosition> positions = loadUserEffectivePositions(userAccount.getUser().getUserid());
        if (ValidateUtil.isEmpty(positions)) {
            throw new AppException("该人员所有岗位均被禁用，不能登录");
        }
        TaSessionManager sm = (TaSessionManager) ServiceLocator.getService("taSessionManager");
        if (!TaSecurityStrategy.isAllowRepeatedlyLogin()) {
            sm.invalidUsersession(userAccount.getLoginId(), request.getSession().getId());
        }

        HttpSession session = request.getSession(true);
        OnlineSessionInfo createOnlineSessionInfo = createOnlineSessionInfo(userAccount, request);
        session.setAttribute(IConstants.USERINFO, userAccount);
        session.setAttribute(IConstants.USER_PERVIEW_FLAG, loadPermissionUrls(userAccount.getUser()));
        session.setAttribute(IConstants.USER_PERVIEW_MENUS_FLAG, loadPermissionMenus(userAccount.getUser()));
        session.setAttribute(IConstants.USER_EFFECTIVE_POSITIONS, positions);

        sm.registerNewSession(session, createOnlineSessionInfo);
        if (log.isDebugEnabled()) {
            log.info("注册登录用户id:" + userAccount.getUser().getUserid());
            log.info("注册登录用户名称:" + userAccount.getUser().getName());
            log.info("注册登录用户账号:" + userAccount.getUser().getLoginid());
        }
        ParamDTO eventDto = new ParamDTO();
        eventDto.put(LOGIN_SESSIONID, session.getId());
        eventDto.put(LOGIN_USERID, userAccount.getUser().getUserid());
        eventDto.put("login_name", userAccount.getUser().getName());
        eventDto.put(LOGIN_CLIENTIP, createOnlineSessionInfo.getClientIp());
        eventDto.put("login_resource", getRequestAddress(request));
        eventDto.put(LOGIN_SERVERIP, createOnlineSessionInfo.getUseRealServer());
        eventDto.put(LOGIN_SESSIONTIME, new Timestamp(session.getLastAccessedTime()));
        TaEventPublisher.publishEvent(new EventSource(request, eventDto), "log_online");
    }

    public void doUserLoginWithoutCheck(String loginId, HttpServletRequest request) {
        regesitUserAccount(loadUserAccountInfo(loginId, request), request);
    }

    public IUserAccountInfo loadUserAccountInfo(String loginId, HttpServletRequest request) {
        return loadUserAccountInfo.loadUser(loginId, request);
    }

    protected List<IMenu> loadPermissionMenus(IUser user) {
        return permissionService.getUserPermissionMenus(user.getUserid());
    }

    protected Set<String> loadPermissionUrls(IUser user) {
        return permissionService.getUserPermissionUrl(user.getUserid());
    }

    protected List<IPosition> loadUserEffectivePositions(Long userid) {
        return permissionService.getUserEffectivePositions(userid);
    }

    protected HashMap<String, Object> createMigratedAttributeMap(HttpSession session) {
        return null;
    }

    protected OnlineSessionInfo createOnlineSessionInfo(IUserAccountInfo account, HttpServletRequest request) {
        OnlineSessionInfo osi = new OnlineSessionInfo();
        osi.setLoginId(account.getLoginId());
        osi.setSession(request.getSession());
        ServletContext servletContext = request.getSession().getServletContext();

        getUserRealServer(servletContext);
        osi.setUseRealServer((String) servletContext.getAttribute("USE_REAL_SERVER"));

        osi.setClientIp(WebUtil.getClientIp(request));
        osi.setClientPort(String.valueOf(request.getLocalPort()));
        UserAgent userAgent = new UserAgent(request.getHeader("User-Agent"));
        osi.setClientExplorer(userAgent.getBrowser().getName());
        osi.setClientSystem(userAgent.getOperatingSystem().getName());
        return osi;
    }


    protected void getUserRealServer(ServletContext servletContext) {
        HttpClient httpclient = new DefaultHttpClient();
        if (servletContext.getAttribute("USE_REAL_SERVER") == null) {
            List<String> addresses = serverAddressService.getAllUsefulServerAddress();
            httpclient.getParams().setParameter("http.socket.timeout", 100);
            httpclient.getParams().setParameter("http.connection.timeout", 100);

            httpclient.getParams().setParameter("http.connection-manager.timeout", 100);

            if ((ValidateUtil.isEmpty(addresses)) && (log.isInfoEnabled())) {
                log.info("提示：没有配置集群的server地址，无法获取当前用户访问的server与端口，请在[集群server地址配置]中配置,如果您在开发环境中可以无需理会本提示");
            }

            for (String address : addresses) {
                HttpGet httpget = new HttpGet();
                try {
                    httpget.setURI(new URI(address + "/ta/getIPPort.jsp"));
                    httpclient.execute(httpget);

                } catch (Exception e) {
                } finally {
                    httpget.releaseConnection();
                }
            }
        }
    }

    protected String getRequestAddress(HttpServletRequest request) {
        if (null == request || request.getRequestURL() == null) {
            return "";
        }
        String str = request.getRequestURL().toString();
        if (str.endsWith("logon.do")) {
            str = str.substring(0, str.indexOf("logon.do") - 1);
        }
        return str;
    }

    protected String getServerAndHost(HttpServletRequest request) {
        if (null == request) {
            return "";
        }
        StringBuffer localStringBuffer = new StringBuffer();
        try {
            localStringBuffer.append(InetAddress.getLocalHost().getHostName());
            localStringBuffer.append("/");
            localStringBuffer.append(InetAddress.getLocalHost().getHostAddress());
            localStringBuffer.append(":");
            localStringBuffer.append(request.getServerPort());
        } catch (UnknownHostException localUnknownHostException) {
            localUnknownHostException.printStackTrace();
        }
        return localStringBuffer.toString();
    }

    public void setPermissionService(IPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setLoadUserAccountInfo(ILoadUserAccountInfo loadUserAccountInfo) {
        this.loadUserAccountInfo = loadUserAccountInfo;
    }

    public IPermissionService getPermissionService() {
        return permissionService;
    }

    public ILoadUserAccountInfo getLoadUserAccountInfo() {
        return loadUserAccountInfo;
    }

    public Md5PasswordEncoder getMd5PasswordEncoder() {
        return md5PasswordEncoder;
    }

    public void setMd5PasswordEncoder(Md5PasswordEncoder md5PasswordEncoder) {
        this.md5PasswordEncoder = md5PasswordEncoder;
    }

    public void setTimeService(ITimeService timeService) {
        this.timeService = timeService;
    }

    public void setServerAddressService(ServerAddressService serverAddressService) {
        this.serverAddressService = serverAddressService;
    }

}
