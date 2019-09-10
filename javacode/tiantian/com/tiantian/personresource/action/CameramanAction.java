package com.tiantian.personresource.action;

import com.yinhai.webframework.BaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

/****
 * 摄影师管理
 * @author wuxiaohui
 */
@Namespace("/resource")
@Action(value = "cameramanAction", results = {@Result(location = "/tiantian/personresource/cameraman.jsp")})
public class CameramanAction extends BaseAction {

    @Override
    public String execute() throws Exception {

        

        return super.execute();
    }
}
