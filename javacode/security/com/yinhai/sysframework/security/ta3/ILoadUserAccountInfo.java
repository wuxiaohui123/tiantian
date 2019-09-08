package com.yinhai.sysframework.security.ta3;

import javax.servlet.http.HttpServletRequest;

public interface ILoadUserAccountInfo {

    String SERVICEKEY = "loadUserAccountInfo";

    IUserAccountInfo loadUser(String loginId, HttpServletRequest request);
}
