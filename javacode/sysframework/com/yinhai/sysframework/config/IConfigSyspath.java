package com.yinhai.sysframework.config;

import java.io.Serializable;

public interface IConfigSyspath extends Serializable {

    String ISCURSYSTEM_YES = "0";
    String ISCURSYSTEM_NO = "1";

    String getId();

    String getUrl();

    String getName();
}
