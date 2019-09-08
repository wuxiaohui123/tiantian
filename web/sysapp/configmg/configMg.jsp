<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>系统参数</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar">
		<ta:pageloading/>
		<ta:box fit="true"  id="systemConfig">
			<ta:fieldset key="系统参数查询，要修改请到config.properties文件中进行修改" cols="3" >
				<ta:text id="codeTableViewName" key="码表视图" value="aa10a1" readOnly="true" required="true" labelWidth="130"></ta:text>
				<ta:text id="codelevelTableViewName" key="层级码表视图" labelWidth="130" readOnly="true" ></ta:text>
				<ta:text id="companyName" key="companyName" labelWidth="130" readOnly="true" ></ta:text>
				<ta:number id="passwordMaxFaultNumber" key="密码输入错误锁定次数" readOnly="true"   required="true" precision="0" labelWidth="130" textHelp="<=0表示不启用此规则"></ta:number>
				<ta:number id="passwordUsefulLife" key="密码有效期(天)"  readOnly="true"  required="true" precision="0" labelWidth="130" textHelp="<=0表示不启用此规则"></ta:number>
				<ta:selectInput id="checkCodeLevel" required="true" key="验证码难度等级"  readOnly="true"  labelWidth="130" data="[{'id':1,'name':'非常简单'},{'id':2,'name':'简单'},{'id':3,'name':'中等'},{'id':4,'name':'困难'},{'id':5,'name':'非常困难'},{'id':6,'name':'极难'}]"></ta:selectInput>
				<ta:number id="pageSize" key="分页组件缺省记录数"  required="true" labelWidth="130" readOnly="true" ></ta:number>
				<ta:selectInput id="curSyspathId" key="当前登录系统" labelWidth="130"  collection="configsys" readOnly="true" ></ta:selectInput>
				<%-- <ta:selectInput id="dragMenuDock" key="首页菜单拖动" labelWidth="130" data="[{'id':'true','name':'能'},{'id':'false','name':'不能'}]" textHelp="当菜单能拖动时，打开或者关闭菜单时需要双击"></ta:selectInput> --%>
				<ta:text id="clustersIp" key="集群ip" labelWidth="130"  readOnly="true"  textHelp="没有集群，不需要填写；如有集群，请以逗号','隔开，例如：111.120.110.201,222.101.201.105"></ta:text>
				<ta:text id="UploadOrgUserVO" key="组织人员导入扩展"  readOnly="true"  labelWidth="130"  textHelp="比如com.yinhai.ta3.common.domain.MyUploadOrgUserVO"></ta:text>
				<ta:text id="com.yinhai.ta3.system.org.domain.Org" key="组织扩展"  readOnly="true"  labelWidth="130"  textHelp="比如com.yinhai.ta3.common.domain.MyOrg"></ta:text>
				<ta:text id="com.yinhai.ta3.system.org.domain.Position" key="岗位扩展"  readOnly="true"   labelWidth="130"  textHelp="比如com.yinhai.ta3.common.domain.MyPosition"></ta:text>
				<ta:text id="com.yinhai.ta3.system.org.domain.User" key="用户扩展" readOnly="true"   labelWidth="130"  textHelp="比如com.yinhai.ta3.common.domain.MyUser"></ta:text>
				<ta:text id="com.yinhai.ta3.system.sysapp.domain.Menu" key="菜单扩展" readOnly="true"   labelWidth="130"  textHelp="比如com.yinhai.ta3.common.domain.MyMenu"></ta:text>
				<ta:radiogroup cols="2" id="startCodeTable" key="缓存码表" required="true" labelWidth="130">
					<ta:radio key="缓存" value="true" readonly="true"></ta:radio>
					<ta:radio key="不缓存" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="neworold" key="使用localStorage" required="true" labelWidth="130"  >
					<ta:radio key="是" value="true"  readonly="true"></ta:radio>
					<ta:radio key="否" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="passwordFirstlogin" key="首次登陆修改密码" required="true" labelWidth="130" >
					<ta:radio key="修改" value="true"  readonly="true"></ta:radio>
					<ta:radio key="不修改" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="developMode" key="开发者模式" required="true" labelWidth="130" >
					<ta:radio key="是" value="true"  readonly="true"></ta:radio>
					<ta:radio key="否" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="useCheckCode" key="验证码" required="true" labelWidth="130"  >
					<ta:radio key="使用" value="true"  readonly="true"></ta:radio>
					<ta:radio key="不使用" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="lazyLoadOrgTree" key="加载组织机构树" required="true" labelWidth="130" >
					<ta:radio key="异步" value="true" readonly="true" ></ta:radio>
					<ta:radio key="同步" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="checkAuthorityByUser" key="安全验证" required="true" labelWidth="130" >
					<ta:radio key="用户" value="true"  readonly="true"></ta:radio>
					<ta:radio key="岗位" value="false" readonly="true"></ta:radio>
				</ta:radiogroup>
				<ta:radiogroup cols="2" id="isPortal" key="门户平台"  labelWidth="130"  >
					<ta:radio key="是" value="true"  readonly="true"></ta:radio>
					<ta:radio key="否" value="false"  readonly="true"></ta:radio>
				</ta:radiogroup>
				<%-- <ta:buttonLayout span="3">
					<ta:button icon="icon-add1" key="保存" isok="true" onClick="fnSaveConfig()"></ta:button>
				</ta:buttonLayout> --%>
			</ta:fieldset>				
		</ta:box>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>