<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<%@page import="com.yinhai.sysframework.config.SysConfig"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>人员管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:0px;margin:0px">
	<ta:pageloading/>
		<ta:box fit="true"  layout="border" layoutCfg="{leftWidth:250}">
			<ta:box position="left" key="查询" cssClass="left-themes-color"  cssStyle="padding-top:20px;">
				<ta:form id="userQuery" fit="true"  cssStyle="overflow-y:auto;padding:0  16px;overflow-x:hidden;">
					<ta:selectTree selectTreeBeforeClick="fnBeforeClick" fontCss="fnFontCss"  labelWidth="80"  cssStyle="width:200px"
						nameKey="orgname" idKey="orgid" parentKey="porgid" url="userMgAction!webQueryAsyncOrgTree.do" asyncParam="['orgid']" key="部门" targetDESC="orgname"  treeId="orgTree" targetId="orgid" />
					<ta:radiogroup  id="isShowSubOrg" key="子部门人员"  labelWidth="80" cols="2">
						<ta:radio key="显示" value="true" checked="true" />
						<ta:radio key="不显示" value="false"/>
					</ta:radiogroup>
					<ta:text id="name"  key="用户姓名" labelWidth="80"/>
					<ta:text id="loginid" key="登录账户" textHelp="支持多loginid查询,以逗号隔开,例如:developer,test_01,test_02" labelWidth="80"/>
					<ta:radiogroup key="有效标志" id="effective" cols="3"  labelWidth="80">
						<ta:radio  key="有效" value="0"/>
						<ta:radio  key="无效" cssStyle="color:gray;text-decoration: line-through;" value="1"/>
						<ta:radio  key="不限" value="-1" checked="checked"/>
					</ta:radiogroup>
					<ta:radiogroup key="性别" id="sex"  cols="3" labelWidth="80">
						<ta:radio  key="男" value="1"/>
						<ta:radio  key="女" value="2"/>
						<ta:radio  key="不限" value="-1" checked="checked"/>
					</ta:radiogroup>
					<ta:radiogroup key="锁定标志" id="islock"  cols="3" labelWidth="80">
						<ta:radio  key="锁定" cssStyle="color:gray;text-decoration: line-through;" value="1"/>
						<ta:radio  key="未锁" value="0"/>
						<ta:radio  key="不限" value="-1" checked="checked"/>
					</ta:radiogroup>
					<ta:buttonLayout cssStyle="border-top:1px solid #c6c6c6;">
						<ta:button key="查询" isok="true" icon="xui-icon-query" onClick="fnQueryUsers()"></ta:button>
						<ta:button key="重置" icon="xui-icon-reset" onClick="Base.resetForm('userQuery')"></ta:button>
					</ta:buttonLayout>
				</ta:form>
			</ta:box>
			<ta:panel position="center"  id="userPanel"  expanded="false" fit="true">
					<ta:buttonGroup align="left" cssClass="buttonlayout-bc" cssStyle="height:35px;padding-top: 10px;">
						<ta:button  id="addBtn" key="新增[A]" isok="true" hotKey="A" icon="icon-adduser" onClick="addUser()"/>
						<ta:buttonGroupSeparate/>
						<ta:button  id="deleteBtn" key="删除[R]" hotKey="R" icon="xui-icon-delete" onClick="delUser()" />
						<%-- <ta:button asToolBarItem="true" id="batchadd" key="批量导入[B]" hotKey="B" icon="icon-adduser" onClick="addUser()"/> --%>
						<ta:button  id="seteffective" key="启用[D]" hotKey="D" icon="icon-ok" disabled="true" onClick="fnReUser()"/>
						<ta:button  id="uneffective" key="禁用[E]" hotKey="E" icon="xui-icon-delete2" disabled="true" onClick="fnDisUser()"/>
						<ta:button id="lockBtn" key="解锁[L]" hotKey="L" icon="icon-yhpurview" disabled="true" onClick="fnUnLock()"/>
						<ta:button  disabled="true" id="changeOrgBtn" key="更改组织" hotKey="C" icon="icon-organization" onClick="fnBatchPositionSet()" />
						<ta:buttonGroupSeparate/>
						<ta:button  id="resetPassBtn" key="密码重置[P]" hotKey="P" disabled="true" onClick="fnResetPass()"/>
					</ta:buttonGroup>
				<ta:text id="departId" display="false" />
				<ta:datagrid onSelectChange="fnSlctChg" onChecked="fnOnSelect" id="userGd" fit="true"   selectType="checkbox" haveSn="true" forceFitColumns="true" border="true">
					<ta:datagridItem id="userid" asKey="true" key="userid" hiddenColumn="true"/>
					<ta:datagridItem id="name" key="姓名" showDetailed="true" width="150" formatter="fnNameFormatter">
					</ta:datagridItem>
					<ta:datagridItem id="orgnamepath" key="所属部门" showDetailed="true" width="150"/>
					<ta:datagridItem id="loginid" sortable="true" key="登录账号" width="150" showDetailed="true"/>
					<ta:datagridItem id="sex" key="性别" width="150" collection="SEX" />
					<ta:datagridItem id="tel" key="移动电话" width="150" align="center" dataAlign="center" ></ta:datagridItem>
					<ta:datagridItem id="effective" key="可用标志" width="150px" hiddenColumn="true" asKey="true"/>
					<ta:datagridItem id="islock" key="锁定标志" width="150px" hiddenColumn="true" asKey="true"/>
<%--			    <ta:datagridItem id="editor" key="修改用户" click="fnToEditUser" width="80" align="center" icon="icon-edit" ></ta:datagridItem>
 				    <ta:datagridItem id="showposition" key="拥有岗位" click="fnQueryPosition" width="80" align="center" dataAlign="center" icon="icon-organization"></ta:datagridItem>
					<ta:datagridItem id="showperrmisstion" key="拥有权限" width="80" align="center" dataAlign="center" click="fnshowPermiss" icon="icon-help"></ta:datagridItem>
					<ta:datagridItem id="showlog" key="操作日志" width="80" align="center" dataAlign="center" icon="icon-ok" click="fnShowOpLog"></ta:datagridItem>
 --%>					
 					<ta:datagridItemOperate showAll="false" id="opt" name="操作选项" >
 						<ta:datagridItemOperateMenu name="编辑" icon="a" click="fnToEditUser"/>
						<ta:datagridItemOperateMenu name="已分配岗位" icon="a" click="fnQueryPosition"/>
						<ta:datagridItemOperateMenu name="已分配权限" icon="a" click="fnshowPermiss"/>
						<ta:datagridItemOperateMenu name="权限操作日志" icon="a" click="fnShowOpLog"/>
					</ta:datagridItemOperate>
					<ta:dataGridToolPaging url="userMgAction!webQueryUsers.do" submitIds="userQuery,userGd"  pageSize="200"/>
				</ta:datagrid>
			</ta:panel>
		</ta:box>
	</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	fnQueryUsers();
});
<%-- 表格formatter 用于区别 effective，islock，positiontype 显示不同的颜色 --%>
function fnNameFormatter(row, cell, value, columnDef, dataContext) {
	if (dataContext.effective == 1) {
		return "<span style='color:red;text-decoration:line-through;';>" + value + "</span>";
	}
	if (dataContext.islock == 1) {
		return "<span style='color:yellow;text-decoration:line-through;'>" + value + "</span>";
	}
	if (dataContext.positiontype == 1) {
		return "<span style='color:red'>" + value + "</span>";
	}
	return value;
}
<%-- 弹出用于权限查看的窗口 --%>
function fnshowPermiss(data, e) {
	Base.openWindow("win", data.name + "权限视图", "userMgAction!webToUserPermiss.do", {"dto['userid']":data.userid}, "90%", "90%", null);
}
<%-- 弹出用于岗位查看的窗口 iframe--%>
function fnQueryPosition(data, e) {
	Base.openWindow("win", data.name + "拥有岗位", "userMgAction!webQueryUserPosition.do",{"dto['userid']": data.userid}, "60%", "60%", null,null,true);
}
function fnShowOpLog(data,e){
	Base.openWindow("win", data.name + "-->操作日志", "userMgAction!webToUserOpLog.do", {"dto['userid']":data.userid}, "90%", "90%");
}
<%-- 弹出用于 人员修改的界面--%>
function fnToEditUser(data, e){
	Base.openWindow("win", data.name + "信息修改", "userMgAction!webToEdit.do", {"dto['userid']":data.userid}, 450, "80%", null, function(){
		fnQueryUsers();
	}, true);
}

<%-- 页面按钮控制 --%>
// addBtn		新增人员
// batchadd     批量新增
// changeOrgBtn 更改部门
// resetPassBtn 密码重置
// seteffective 设置有效
// uneffective  设置无效
function fnSlctChg(o) {
	//如果批量选择中有被禁用的用户，则不能 密码重置，修改和禁用
	for (var i = 0; i < o.length; i ++) {
		if (o[i].effective == 1)
			Base.setDisabled(["changeOrgBtn"]);
	}
	if (o.length == 1) {
		if (o[0].effective == 1) {
			Base.setEnable("seteffective");
			Base.setDisabled("uneffective");
		} else {
			Base.setDisabled("seteffective");
			Base.setEnable("uneffective");
		}
		if(o[0].islock == 1){
			Base.setEnable("lockBtn");
		}else{
			Base.setDisabled("lockBtn");
		}
		Base.setEnable(["editBtn","delBtn","resetPassBtn","changeOrgBtn"]);
	} else if (o.length > 1) {
		Base.setEnable(["delBtn","resetPassBtn"]);
		Base.setDisabled(["editBtn","changeOrgBtn"]);
	} else {
		Base.setDisabled(["changeOrgBtn","resetPassBtn","seteffective","uneffective","resetPassBtn","changeOrgBtn","lockBtn"]);
	}
}
<%-- 查询用户信息 --%>
function fnQueryUsers() {
	Base.submit("userQuery,userGd", "userMgAction!webQueryUsers.do");
}
function fnDisUser(){
	var o = Base.getGridSelectedRows("userGd");
	if (confirm(o.length > 1 ? "确实要禁用这些人员吗？" : "确实要禁用这个人员吗？"))
		Base.submit("userGd", "userMgAction!webUnEffectiveUsers.do", null,null,null, function(){
			fnQueryUsers();
			Base.msgTopTip("禁用成功");
		});
}
function fnReUser(){
	Base.submit("userGd", "userMgAction!webEffectiveUsers.do", null,null,null, function(){
		fnQueryUsers();
		Base.msgTopTip("启用成功"); 
	});
}
//解锁
function fnUnLock(){
	var d = Base.getGridSelectedRows("userGd");
	if(d && d.length == 1){
		Base.submit("", "userMgAction!webUnLockUser.do", {"dto['userid']":d[0].userid},null,null, fnQueryUsers);
	}
}
<%-- 批量组织修改 --%>
function fnBatchPositionSet() {
	var selectRows = Base.getGridSelectedRows("userGd");
	if(selectRows && selectRows.length == 1){
// 		var substr = Ta.util.obj2string(selectRows);
// 		Base.openWindow("win", "更改组织", "userMgAction!webToBatchPosition.do", {"users":substr}, "40%", "80%");
		Base.openWindow("win", "更改组织", "userMgAction!webToUpdateOrg.do", {"dto['userid']":selectRows[0].userid}, "30%", "70%",function(data){
			$("#w_orgname").parent().parent().css("margin-top","1px");
		},function(){
			fnQueryUsers();
		});
	}else{
		Base.alert("只能选择一个用户！","warn");
	}
}
function fnOnSelect(o,e){
// 	if (o.effective == 1) return false;
// 	else return true;
	return true;
}
<%-- 新增人员 --%>
function addUser() {
	Base.openWindow("win", "人员新增", "userMgAction!webToAddUser.do", null, 450, "80%",null,fnQueryUsers);
}
function fnResetPass() {
	var o = Base.getGridSelectedRows("userGd");
	Base.openWindow("passWin", "密码重置", "userMgAction!webToRestPass.do", {"userid":o[0].userid}, 280, 160);
}
function delUser() {
	var o = Base.getGridSelectedRows("userGd");
	if(o && o.length > 0){
		Base.confirm("确实要删除这些人员吗？", function(yes){
			if(yes){
				Base.submit("userGd", "userMgAction!webDeleteUsers.do", null, false, false, function(){
					Base.deleteGridSelectedRows('userGd');
					Base.msgTopTip("<div class='msgTopTip'>删除人员成功</div>");	
				});
			}
		});
	}else{
		Base.alert("请先选择人员后再进行删除","warn");
	}
}
function fnBeforeClick(treeId, treeNode){
	if(treeNode.effective == 1){
		Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能进行查询</div>");
		return false;
	}
	if(treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
		return false;
	}else{
		return true;
	}
}
function fnFontCss(treeId,treeNode){
	if(treeNode.effective == 1 && !treeNode.admin){
		return {'text-decoration':'line-through','color': 'red'};
	}else if(treeNode.effective == 1 && treeNode.admin){
		return {'text-decoration':'line-through'};
	}else if(treeNode.effective != 1 && !treeNode.admin){
		return {'color': 'red'};
	}else{
		return {};
	}
}
function fnSetFont(treeId, treeNode){
	//if (treeNode.effective == 1 && treeNode.admin == true)return [{'color': 'red'},{'text-decoration':'line-through'}]
	if (treeNode.effective == 1) return {'text-decoration':'line-through'};
	if (!treeNode.admin) return {'color': 'red'};
	if (treeNode.orgtype == "<%=orgType_team%>"){
		return {"cursor":"not-allowed"};
	}
	return {};
}
function fnselecttree(treeId, treeNode) {
	//直属组织必须是部门或者机构
	if (treeNode.orgtype != "<%=orgType_org%>" && treeNode.orgtype != "<%=orgType_depart%>"){
		Base.msgTopTip("<div class='msgTopTip'>直属组织只能是部门或者机构，不能为组</div>");
		return false;
	}
	//是否有效
	if (treeNode.effective == 1) return false;
	//是否有管理权限
	if (!treeNode.admin) {
		Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
		return false;	
	}
	var w_tree = Base.getObj("w1_orgTree");
	var nodes = w_tree.getCheckedNodes(true);
	if(nodes){
		for(var i  = 0 ; i < nodes.length; i++){
			if(treeNode.orgid == nodes[i].orgid){
				Base.msgTopTip("附属部门已经有该部门，直属部门不能与附属部门相同",4000);
				return false;
			}
		}
	}
	return true;
}
function fnSelectTreeCallback(event, treeId, treeNode){
	Base.setValue("w_orgid",treeNode.orgid);
	Base.setValue("w_orgname",treeNode.orgnamepath);
	__fnHideSelectTree_w_orgTree();
}
</script>
<%@ include file="/ta/incfooter.jsp"%>