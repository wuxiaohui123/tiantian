<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>人员修改</title>
		<%@ include file="/ta/inc.jsp"%>
		<style type="">
				 #searchbox{
				 	background: url("../indexue/indexue_default/images/search.png") no-repeat scroll 0 0 transparent !important;
					padding: 0 0 0 16px;
					width:100px;
					margin:2px 2px 0px 5px;
					color:#CCC;
				 } 
		</style>
	</head>
	<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:240}" style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:form id="userForm" fit="true">
		<ta:panel id="userInfo" hasBorder="false" expanded="false" fit="true" bodyStyle="padding:10px 20px 10px 10px;" withButtonBar="true">
				<ta:text id="userid" key="用户编号" readOnly="true" span="2" display="false"/>
				<ta:text id="loginid" key="登录帐号" required="true" readOnly="true" span="2" maxLength="10"/>
				<ta:text id="name" key="姓名" required="true"/>
<!-- 				<ta:radiogroup key="性别" collection="SEX" id="sex" cols="2"/> -->
				<ta:selectInput key="性别" collection="SEX" id="sex" filterOrg="false"/>
				<ta:text id="tel" key="移动电话" maxLength="11" validType="mobile"/>
			<%-- <ta:panel key="所属组织" fit="true" withToolBar="true" cssStyle="display:none">
				<ta:panelToolBar cssStyle="height:auto;padding:0px">
					<ta:box cols="3">
						<ta:button asToolBarItem="true" key="添加" columnWidth="0.15" icon="icon-adduser" onClick="fnAddOrgToOrgList()"/>
						<ta:button asToolBarItem="true" key="删除" columnWidth="0.15" icon="icon-remove"  onClick="fnDelOrgFromOrgList()"/>
					</ta:box>
				</ta:panelToolBar>
				<ta:textarea id="orgs" display="none" readOnly="true"/>
				<ta:datagrid id="orgidList" fit="true" haveSn="true" forceFitColumns="true" selectType="checkbox">
					<ta:datagridItem id="orgname" key="组织名称" />
					<ta:datagridItem id="orgnamepath" key="组织路径" width="240" showDetailed="true"/>
					<ta:datagridItem id="orgtype" key="组织类型" collection="ORGTYPE" sortable="true" width="70"/>
				</ta:datagrid>
			</ta:panel> --%>
			<ta:panelButtonBar>
				<ta:submit id="updateUserBtn" key="保存[S]" icon="icon-add1" hotKey="S" isok="true" submitIds="userInfo" url="orgUserMgAction!editUser.do" successCallBack="function(){parent.Base.msgTopTip('修改用户成功');parent.Base.closeWindow('win');}"/>
				<ta:button id="closeWinBtn" key="关闭[X]" icon="icon-no"  hotKey="X" onClick="parent.Base.closeWindow('win');" />
			</ta:panelButtonBar>
		</ta:panel>
		</ta:form>
	</body>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	Base.focus("name");
})
/*添加org*/
function fnAddOrgToOrgList() {
	var data = Base.getGridData("orgidList");
	var node = $.fn.zTree.getZTreeObj("w_orgTree").getNodeByParam("id", Base.getValue("w_orgid"), null);
	if (node != null && node.id != null) {
		for (var i = 0; i < data.length; i ++) {
			if (node.id == data[i].orgid) {Base.msgTopTip("<div style='width: 200px;margin: auto 0;text-align: center;line-height: 100px;font-size: 20px;'>已添加</div>", 2200, 200, 100);return;}
		}
		var org = {};
		org.orgname = node.name;
		org.orgid = node.id;
		org.orgtype = node.orgtype;
		org.orgnamepath = node.orgnamepath;
		data.push(org);
		Base._setGridData("orgidList", data);
		var json = Ta.util.obj2string(data);
		$("#orgs").val(json);
	}
}
function fnDelOrgFromOrgList() {
	Base.deleteGridSelectedRows("orgidList");
	var data = Base.getGridData("orgidList");
	var json = Ta.util.obj2string(data);
	$("#orgs").val(json);
}
function fnSaveSuccCb() {
	if (confirm("保存成功，是否继续新增人员？")) {
		Base.resetForm("userForm");
		Base.focus("name");
	} else {
		Base.closeWindow('win');
	}
}
function fnselecttree(treeId, treeNode) {
	//是否有效
	if (treeNode.effective == 1) return false;
	//是否有管理权限
	if (!treeNode.admin) return false;
	//直属组织必须是部门或者机构
	if (treeNode.orgtype != "<%=orgType_org%>" && treeNode.orgtype != "<%=orgType_depart%>"){
		Base.alert("直属组织只能是部门或者机构，不能为组","warn");
		return false;
	}
	return true;
}
</script>
<%@ include file="/ta/incfooter.jsp"%>