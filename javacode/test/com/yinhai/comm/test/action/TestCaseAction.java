package com.yinhai.comm.test.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.webframework.BaseAction;
import com.yinhai.synthesis.util.Constant;

@Namespace("/process/TestCase")
@Action(value="testCaseAction",results={
		@Result(name="success",location=Constant.XMMC+"/demo/demo.jsp")})
public class TestCaseAction extends BaseAction{

	@Override
	public String execute() throws Exception {
		
		return SUCCESS;
	}
	public String toQuery() throws Exception{
		PageBean list = getDao().queryForPageWithCount("dgEMP", "emp.queryData", getDto(), getDto());
		setList("dgEMP", list);
		return JSON;
	}
}
