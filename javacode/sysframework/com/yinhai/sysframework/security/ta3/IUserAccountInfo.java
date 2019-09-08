package com.yinhai.sysframework.security.ta3;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import com.yinhai.sysframework.iorg.IUser;

public interface IUserAccountInfo extends Serializable {

    Collection<IRoleAuthrity> getRoles();

    String getPassword();

    String getLoginId();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isEnabled();

    IUser getUser();

    boolean isAccountFirstLogin();

    Date lastModifyDate();

    int getPasswordfaultnum();

    Date getCreateUserDate();
}
