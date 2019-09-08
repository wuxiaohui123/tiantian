<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>添加流程用户</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:190,isLeftCollapse:false}">
	<ta:pageloading />
	<ta:box id="myfieldset" key="查询" cssStyle="padding:10px" position="left">
	    <ta:text id="groupId" key="角色ID" display="false"></ta:text>
		<ta:text id="name" key="姓名" labelWidth="60"/>
		<ta:selectInput id="sex" key="性别" collection="SEX" labelWidth="60"/>
		<ta:text id="tel" key="联系电话" labelWidth="60"/>
		<ta:buttonLayout id="mybtlayout" align="right">
			<ta:button key="查询" icon="icon-search" isShowIcon="true" onClick="toQueryUser();" isok="true"/>
			<ta:button key="重置" icon="icon-reload" isShowIcon="true"/>
		</ta:buttonLayout>
	</ta:box>
	<ta:box position="center">
	<ta:panel id="mypanel" fit="true" withButtonBar="true" hasBorder="false">
		<ta:datagrid id="userGrid" fit="true"  selectType="checkbox" forceFitColumns="true">
			<ta:datagridItem id="useid" key="USERID" hiddenColumn="true"/>
			<ta:datagridItem id="name" key="姓名" width="80"/>
			<ta:datagridItem id="sex" key="性别" collection="SEX" width="50"/>
			<ta:datagridItem id="orgname" key="部门" width="120"/>
			<ta:datagridItem id="email" key="Email" width="120"/>
			<ta:datagridItem id="tel" key="联系电话" width="120"/>
			<ta:dataGridToolPaging url="processRoleAction!queryOrgUsers.do" submitIds="myfieldset" showExcel="false"/>
		</ta:datagrid>
		<ta:panelButtonBar>
			<ta:button key="确定" icon="icon-ok" isShowIcon="true" isok="true" onClick="fnAddRoleUser()"/>
		</ta:panelButtonBar>
	</ta:panel>	
	</ta:box>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
		toQueryUser();
	});
	function toQueryUser(){
		Base.submit("myfieldset","processRoleAction!queryOrgUsers.do");
	}
	function fnAddRoleUser(){
		Base.submit("groupId,userGrid","processRoleAction!addRoleUserByGroup.do",null,null,false,function(){
			parent.Base.closeWindow("win");
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
