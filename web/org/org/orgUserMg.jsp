<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<html>
<head>
<title>组织及用户管理</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:270}" id="hintbody"
	style="padding:0px;margin:0px">
	<ta:pageloading/>
	<ta:box position="left" key="组织维护" cssStyle="overflow:auto;">
		<div style="backgound:#EEE;height:40px;border-bottom:1px solid #CCC;padding-top:3px;padding-left:10px;padding-right:10px"> 
	        		<!-- <input id="searchbox" class="searchbox" style="height:18px" type="text" value="搜索" /> -->
	        		<ta:text id="searchbox" cssClass="searchbox" value="输入后回车检索" labelWidth="1"></ta:text> 
			</div>
		<div class="grid" fit="true" style="overflow:auto;">
				<ta:tree id="orgTree" childKey="orgid" nameKey="orgname" parentKey="porgid" fontCss="treeEffective" showLine="true" async="false" 
					editable="true" onClick="fnClk" beforeEdit="fnToEdit" beforeRemove="fnBfRemove" onRemove="fnDelDept" onRightClick="fnOnRightClick"
					keepLeaf="true" editTitle="编辑当前组织" removeTitle="删除当前组织" addTitle="添加子组织" showAddBtn="true" onAdd="fnAddDept" 
					beforeDrop="fnBeforeDrop" onDrop="fnOnDrop" />
	       	</div>
		<div id="rm" style="width:150px;font-size:12px;">
			<div id="rm_add" class="btn-app">添加子组织</div>
			<div id="rm_modify">编辑当前组织</div>
			<div id="rm_del">删除当前组织</div>
		</div>
	</ta:box>
	<ta:box position="center">
		<ta:form id="userQuery" fit="true" cssStyle="padding:10px 10px 5px 10px;">
		
		<ta:fieldset cols="12" id="flt">
			<ta:text id="dept" key="组织" value="所有组织" labelWidth="30"  span="5" readOnly="true" clickIcon="xui-icon-close" clickIconTitle="点击还原为所有组织" clickIconFn="fnClear()" />
			<ta:buttonLayout>
				<ta:button key="清除" onClick="fnClear()" />
			</ta:buttonLayout>
			<ta:checkboxgroup id="isShowSubOrg" cssStyle="width:40px">
				<ta:checkbox key="子组织" value="1" checked="true" onClick="fnOnClick()"/>
			</ta:checkboxgroup>
			<ta:selectLableText span="3" labelWidth="60" keys="[{\"id\":\"name\",\"key\":\"姓名\"},{\"id\":\"loginid\",\"key\":\"账户\"}]" textHelp="姓名支持模糊查询;账户不支持模糊查询，但支持多个账户查询，以逗号隔开"/>
			<ta:buttonLayout span="3">
				<ta:button key="查询" isok="true" icon="xui-icon-query" onClick="fnQueryUsers()"></ta:button>
				<ta:button key="重置" icon="xui-icon-reset" onClick="fnReset()"></ta:button>
				<ta:button key="↓" id="showmore" onClick="showMore(this,'more')"/>
			</ta:buttonLayout>
			<ta:box span="12" cols="3" id="more" cssStyle="display:none">
				<ta:radiogroup key="性别" id="sex" cols="3" labelWidth="30">
					<ta:radio key="男" value="1" />
					<ta:radio key="女" value="2" />
					<ta:radio key="不限" value="-1" checked="checked" />
				</ta:radiogroup>
				<ta:radiogroup key="锁定标志" id="islock" cols="3" labelWidth="60">
					<ta:radio key="锁定"
						cssStyle="color:gray;text-decoration: line-through;" value="1" />
					<ta:radio key="未锁" value="0" />
					<ta:radio key="不限" value="-1" checked="checked" />
				</ta:radiogroup>
			</ta:box>
			<ta:text id="deptId" key="树的orgId" display="false"/>
			<ta:text id="effective" display="false" value="-1"/>
		</ta:fieldset>
		<ta:buttonGroup align="left" cssClass="buttonlayout-bc"
			cssStyle="height:35px;padding-top: 10px;margin-left:0px;" id="btngrp">
			<ta:button id="addBtn" key="新增[A]" isok="true" hotKey="A"
				icon="icon-adduser" onClick="addUser()" />
			<ta:buttonGroupSeparate />
			<ta:button id="deleteBtn" key="删除[R]" hotKey="R"
				icon="xui-icon-delete" onClick="delUser()" disabled="true"/>
			<%-- <ta:button asToolBarItem="true" id="batchadd" key="批量导入[B]" hotKey="B" icon="icon-adduser" onClick="addUser()"/> --%>
			<ta:button id="seteffective" key="启用[D]" hotKey="D" icon="icon-ok"
				disabled="true" onClick="fnReUser()" />
			<ta:button id="uneffective" key="禁用[E]" hotKey="E"
				icon="xui-icon-delete2" disabled="true" onClick="fnDisUser()" />
			<ta:button id="lockBtn" key="解锁[L]" hotKey="L" icon="icon-yhpurview"
				disabled="true" onClick="fnUnLock()" />
			<ta:button disabled="true" id="changeOrgBtn" key="更改组织" hotKey="C"
				icon="icon-organization" onClick="fnBatchPositionSet()" />
			<ta:buttonGroupSeparate />
			<ta:button id="resetPassBtn" key="密码重置[P]" hotKey="P" disabled="true"
				onClick="fnResetPass()" />
		</ta:buttonGroup>
		<ta:panel fit="true" key="人员列表" >
			<ta:datagrid id="userGd" enableColumnMove="true" onSelectChange="fnSlctChg" fit="true" selectType="checkbox" haveSn="true" >
				<ta:datagridItem id="userid" asKey="true" key="userid" hiddenColumn="true" />
				<ta:datagridItem id="name" key="姓名" showDetailed="true" align="center" dataAlign="center" formatter="fnNameFormatter"/>
				<ta:datagridItem id="loginid" sortable="true" key="登录账号" width="80" align="center" dataAlign="center" showDetailed="true" />
				<ta:datagridItem id="icon1" icon="icon-edit"  align="center" dataAlign="center" click="fnEdit" key="编辑" />
				<ta:datagridItem id="icon2" icon="icon-search" align="center" dataAlign="center" click="fnPermission" key="权限" />
				<ta:datagridItem id="icon3" icon="icon-find" align="center" dataAlign="center"  key="岗位"  click="fnShowPossition"/>
				<ta:datagridItem id="orgnamepath" key="所属组织" showDetailed="true" width="200"/>
				<ta:datagridItem id="sex" key="性别" align="center" dataAlign="center" collection="SEX" />
				<ta:datagridItem id="age" key="年龄" align="center" dataAlign="center"/>
				<ta:datagridItem id="birth" key="出生日期" align="center" dataAlign="center" width="100" dataType="date"/>
				<ta:datagridItem id="job" key="职位" align="center" collection="JOB"/>
				<ta:datagridItem id="tel" key="移动电话"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				<ta:datagridItem id="officetel" key="办公电话"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				<ta:datagridItem id="email" key="电子邮箱"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				<ta:datagridItem id="qq" key="QQ"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				<ta:datagridItem id="weixin" key="微信"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				<ta:datagridItem id="weibo" key="微博"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				<ta:datagridItem id="address" key="地址"  align="center" width="150" showDetailed="true" dataAlign="center"/>
				
				<ta:datagridItem id="effective" key="可用标志" width="150px" hiddenColumn="true" asKey="true" />
				<ta:datagridItem id="islock" key="锁定标志" width="150px" hiddenColumn="true" asKey="true" />
				<ta:dataGridToolPaging url="orgUserMgAction!queryUsers.do" selectExpButtons="1,2" submitIds="userQuery,userGd" pageSize="10"/>
			</ta:datagrid>
		</ta:panel>
		</ta:form>
	</ta:box>
	<ta:boxComponent height="200px" width="500px" id="b2" arrowPosition="vertical">
		<ta:datagrid id="find" fit="true" haveSn="true" forceFitColumns="true" onRowClick="fnRowClick" >
				<ta:datagridItem id="orgid" key="组织id" hiddenColumn="false"/>
				<ta:datagridItem id="orgname" key="岗位名称"  showDetailed="true" width="100"/>
				<ta:datagridItem id="orgnamepath" key="组织路径" showDetailed="true" width="300"/>
		</ta:datagrid>
	</ta:boxComponent>
	<ta:boxComponent height="200px" width="400px" id="b1" arrowPosition="vertical">
		<ta:datagrid id="positions" fit="true" haveSn="true" border="true">
				<ta:datagridItem id="positionname" key="岗位名称" formatter="fnNameFormatter" showDetailed="true" width="70"/>
				<ta:datagridItem id="orgnamepath" key="组织路径" showDetailed="true" width="200"/>
				<ta:datagridItem id="positiontype" key="岗位类型" width="80" collection="positiontype" showDetailed="true"/>
		</ta:datagrid>
	</ta:boxComponent>
	<ta:boxComponent id="editUser" height="290px" width="300px" arrowPosition="vertical">
		<ta:fieldset id="editfield" >
		   <ta:box id="bx1" cols="2">
			<ta:text id="p_userid" key="用户编号" labelWidth="60" span="2" readOnly="true" display="false"/>
			<ta:text id="p_loginid" key="登录号" labelWidth="60" span="2" readOnly="true" required="true" />
			<ta:text id="p_name" key="姓名" labelWidth="60" span="2" required="true" />
			<ta:selectInput id="p_sex" key="性别" labelWidth="60"  collection="sex"/>
			<ta:number id="p_age" key="年龄" labelWidth="60" alignLeft="true" max="200"/>
			<ta:selectInput id="p_job" key="职位" labelWidth="60" span="2" collection="JOB"/>
			<ta:text id="p_tel" key="移动电话" labelWidth="60" span="2" validType="mobile"/>
			<ta:text id="p_officetel" key="办公电话" labelWidth="60" span="2" maxLength="15"/>
			<ta:buttonGroup span="2" cssStyle="float: right;">
			 <ta:button id="more" key="更多..." onClick="fnMore()"/>
			</ta:buttonGroup>
		   </ta:box>
		   <ta:box id="bx2" cssStyle="display:none">
			<ta:date id="p_birth" key="出生日期" labelWidth="60" showSelectPanel="true"/>
			<ta:number id="p_qq" key="QQ" labelWidth="60" alignLeft="true" max="99999999999"/>
			<ta:text id="p_email" key="电子邮箱" labelWidth="60" validType="email"/>
			<ta:text id="p_weixin" key="微信" labelWidth="60" maxLength="50"/>
			<ta:text id="p_weibo" key="微博" labelWidth="60" maxLength="50"/>
			<ta:text id="p_address" key="地址"  labelWidth="60" maxLength="200"/>
			<ta:buttonGroup cssStyle="float: right;">
			 <ta:button id="back" key="返回" onClick="fnBack()"/>
			</ta:buttonGroup>
		   </ta:box>
		</ta:fieldset>
		<ta:buttonLayout align="right" cssStyle="margin-top:3px;padding-right:0px;">
			<ta:button key="保存" isok="true" onClick="fnEditUser()"/>
			<ta:button key="取消" onClick="fnCancleEditUser()"/>
		</ta:buttonLayout>
	</ta:boxComponent>
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	var $dept = $("#dept");
	$dept.removeClass("readonly");
	$dept.parent().removeClass("readonly");
	$dept.parent().css({"borderTop":"0px","borderLeft":"0px","borderRight":"0px","borderRadius":0});
	$("#searchbox").css('color','#CCC');
	fnQueryUsers();		
	var treeObj = $.fn.zTree.getZTreeObj("orgTree");
	$("#rm").menu(); // 构建右键菜单
	// 在树上绑定右键事件
	$("#orgTree").bind('contextmenu', function(e){
		$('#rm').menu('show', {left: e.pageX, top: e.pageY});
		return false;
	});
	//树判断
	$("#searchbox").focus(function(e){
 		if(this.value.trim()=='输入后回车检索'){
 			$(this).css('color','#000000');
 			this.value="";
 			e.stopPropagation();
		}
 	}).blur(function(e){
 		if(this.value.trim()==''){
 			$(this).css('color','#CCC');
 			this.value="输入后回车检索";
 			e.stopPropagation();
 		}
 	}).keydown(function(e){
 		if(e.keyCode==13){	
			fnQuery();
 		}
 	});
 	fnPageGuide(parent.currentBuinessId);
})
function fnMore(){
	Base.hideObj("bx1");
	Base.showObj("bx2");
}
function fnBack(){
	Base.hideObj("bx2");
	Base.showObj("bx1");
}
function fnPageGuide(currentBuinessId){
		    var data = [
		        {id:$("#hintbody"),
		    	message:"此功能可对组织与人员进行管理，增删查改组织或者人员。"
		        },
		        {id:$("#orgTree_1_span"),
		    	message:"1.鼠标移到组织树上的每一个节点，在该节点右边将产生一排可操作该组织的按钮；<br/>2.单击节点还可查询人员；<br/>3.右键单击可弹出右键菜单"
		        },
		        {id:$("#dept").prev(),
		        message:"可将该查询条件重置为“所有组织”"
			    },
			    {id:$(".selectLabelText_label").eq(0),
			   	message:"可在姓名和账号间切换查询条件"
			    },
			    {id:$("#showmore"),
			   	message:"单击可在下面展示更多查询条件"
			    },
			    {id:$("#btngrp"),
			   	message:"针对人员的操作，可根据表格中的勾选状况，变化可操作状态"
			    }
			]
			$("body").hintTip({
				replay 	: false,
				show 	: true, 
				cookname: currentBuinessId,
				data 	: data
			}); 
	}
	function fnEditUser(){
		Base.submit("editUser","orgUserMgAction!editUser.do",{},null,null,function(){
			Base.msgTopTip('修改用户成功');
			$("#editUser").hide();
			fnQueryUsers();
		})
	}
	function fnCancleEditUser(){
		$("#editUser").hide();
	}
//点击显示子组织触发事件
function fnOnClick(){
	fnQueryUsers();
}
//查询表格行点击事件
function fnRowClick(e,data){
	var treeObj=$.fn.zTree.getZTreeObj("orgTree");
	var node=treeObj.getNodeByParam("orgid",data.orgid,null);
 	treeObj.selectNode(node,false);
	$("#b2").hide();
 	fnClk(e, "orgTree", node);
}
//树的搜索按钮点击事件
function fnQuery(){
	var treeObj = $.fn.zTree.getZTreeObj("orgTree");
	treeObj.cancelSelectedNode();	
	if ($("#searchbox").val().trim() != "") {
 		var menuTree = Ta.core.TaUIManager.getCmp('orgTree');
 		var nodes = menuTree.getNodesByParamFuzzy("py", $("#searchbox").val());
 		if(nodes.length == 0)
 			nodes = menuTree.getNodesByParamFuzzy("orgname", $("#searchbox").val());
 		if(nodes.length == 0){
 			Base.setValue("dept","所有组织");
			Base.setValue("deptId","");
			Base.clearGridData("userGd");
			treeObj.cancelSelectedNode();
			return;
 		} 			
 		if (nodes.length>0) {
 			if(nodes.length==1){
	 			menuTree.selectNode(nodes[0]);
 			}else{
				var target=Base.getObj("searchbox");
				Base.showBoxComponent("b2",target)
				var data=[];
				for(var i=0;i<nodes.length;i++){
					var row={};
					row.orgname=nodes[i].orgname;
					row.orgnamepath=nodes[i].orgnamepath;
					row.orgid=nodes[i].orgid;
					data.push(row);
				}
				Base._setGridData("find",data);
// 				var grid=Base.getObj("find");
// 				grid.setSelectedRows([ 0 ]);
 			}
 		}
 	}else{
 		fnClear();
 	}
 	var sn=treeObj.getSelectedNodes();
 	if(sn!=null||sn!=undefined){
	 	fnClk(event,"orgTree",sn[0]);
 	}
}
//清除组织信息
function fnClear(){
	Base.setValue("dept","所有组织");
	Base.setValue("deptId","");
	fnQueryUsers();
	var treeObj = $.fn.zTree.getZTreeObj("orgTree");
	treeObj.cancelSelectedNode();
}
//删除人员
function delUser() {
	var o = Base.getGridSelectedRows("userGd");
	if(o && o.length > 0){
		Base.confirm("确实要删除这些人员吗？", function(yes){
			if(yes){
				Base.submit("userGd", "orgUserMgAction!deleteUsers.do", null, false, false, function(){
					Base.deleteGridSelectedRows('userGd');
					Base.msgTopTip("<div class='msgTopTip'>删除人员成功</div>");	
				});
			}
		});
	}else{
		Base.alert("请先选择人员后再进行删除","warn");
	}
}
<%-- 新增人员 --%>
function addUser() {
	Base.openWindow("win", "人员新增", "orgUserMgAction!toAddUser.do", null, 450, 440,null,fnQueryUsers,true);
}
//新增人员中树的节点点击事件
function fnSelectTreeCallback(event, treeId, treeNode){
	Base.setValue("w_orgid",treeNode.orgid);
	Base.setValue("w_orgname",treeNode.orgnamepath);
	__fnHideSelectTree_w_orgTree();
}
//单人组织修改 
function fnBatchPositionSet() {
	var selectRows = Base.getGridSelectedRows("userGd");
	if(selectRows && selectRows.length == 1){
		Base.openWindow("win", "更改组织", "orgUserMgAction!toUpdateOrg.do", {"dto['userid']":selectRows[0].userid}, 400, 400,function(data){
			$("#w_orgname").parent().parent().css("margin-top","1px");
		},function(){
			fnQueryUsers();
		},true);
	}
} 
//单人组织修改中的树
function fnselecttree(treeId, treeNode) {
	//直属组织必须是组织或者机构
	if (treeNode.orgtype != "<%=orgType_org%>" && treeNode.orgtype != "<%=orgType_depart%>"){
		Base.msgTopTip("<div class='msgTopTip'>直属组织只能是组织或者机构，不能为组</div>");
		return false;
	}
	//是否有效
	if (treeNode.effective == 1) return false;
	//是否有管理权限
	if (!treeNode.admin) {
		Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
		return false;	
	}
	var w_tree = $.fn.zTree.getZTreeObj("w_orgTree");
	var nodes = w_tree.getCheckedNodes(true);
	if(nodes){
		for(var i  = 0 ; i < nodes.length; i++){
			if(treeNode.orgid == nodes[i].orgid){
				Base.msgTopTip("附属组织已经有该组织，直属组织不能与附属组织相同",4000);
				return false;
			}
		}
	}
	return true;
}
//单人更改设置选中后的样式
function fnSetFont(treeId, treeNode){
	//if (treeNode.effective == 1 && treeNode.admin == true)return [{'color': 'red'},{'text-decoration':'line-through'}]
	if (treeNode.effective == 1) return {'text-decoration':'line-through'};
	if (!treeNode.admin) return {'color': 'red'};
	if (treeNode.orgtype == "<%=orgType_team%>"){
		return {"cursor":"not-allowed"};
	}
	return {};
}
//解锁单个用户
function fnUnLock(){
	var d = Base.getGridSelectedRows("userGd");
	if(d && d.length == 1){
		Base.submit("", "orgUserMgAction!unLockUser.do", {"dto['userid']":d[0].userid},null,null, fnQueryUsers);
	}
}
//批量禁用用户
function fnDisUser(){
	var o = Base.getGridSelectedRows("userGd");
	if (confirm(o.length > 1 ? "确实要禁用这些人员吗？" : "确实要禁用这个人员吗？"))
		Base.submit("userGd", "orgUserMgAction!unEffectiveUsers.do", null,null,null, function(){
			fnQueryUsers();
			Base.msgTopTip("禁用成功");
		});
}
//批量启用用户
function fnReUser(){
	Base.submit("userGd", "orgUserMgAction!effectiveUsers.do", null,null,null, function(){
		fnQueryUsers();
		Base.msgTopTip("启用成功"); 
	});
}
//密码重置
function fnResetPass() {
	var o = Base.getGridSelectedRows("userGd");
	Base.openWindow("passWin", "密码重置", "orgUserMgAction!toRestPass.do", {"userid":o[0].userid}, 280, 175);
}
//表格选中行改变事件，控制页面按钮
function fnSlctChg(o) {
	var data=Base.getGridSelectedRows("userGd");
	if(data!=null&&data!=""&&data!=undefined){
		Base.setEnable("deleteBtn");
	}else{
		Base.setDisabled("deleteBtn");
	}
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
		Base.setDisabled(["editBtn","changeOrgBtn","resetPassBtn"]);
	} else {
		Base.setDisabled(["changeOrgBtn","resetPassBtn","seteffective","uneffective","resetPassBtn","changeOrgBtn","lockBtn"]);
	}
}
<%-- 组织样式 --%>
function treeEffective(treeId, treeNode){
	if(treeNode.effective == "1" && !treeNode.admin){
		return {'text-decoration':'line-through','color': 'red'};
	}else if(treeNode.effective == "1" && treeNode.admin){
		return {'text-decoration':'line-through'};
	}else if(treeNode.effective != "1" && !treeNode.admin){
		return {'color': 'red'};
	}else{
		return {'text-decoration':'none'};
	}
}
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
// 右键点击树节点事件
function fnOnRightClick(event, treeId, treeNode) {
	var treeObj = $.fn.zTree.getZTreeObj("orgTree");
	$("#rm_add").unbind("click").bind("click", function(){
		fnAddDept(event, treeId, treeNode);
	});
	$("#rm_modify").unbind("click").bind("click", function(){
		fnToEdit(treeId, treeNode);
	});
	$("#rm_del").unbind("click").bind("click", function(){
		if(fnBfRemove(treeId, treeNode)){		
			fnDelDept(event, treeId, treeNode);
		}
	});
	if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
		treeObj.cancelSelectedNode();
	} else if (treeNode) {
		treeObj.selectNode(treeNode);
	}
}
//点击重置按钮触发
function fnReset(){
	Base.setValue("dept","所有组织");
	Base.setValue("deptId",null);
	Base.setValue("sex","-1");
	Base.setValue("islock","-1");
	Base.setValue("isShowSubOrg","1");
	Base.setValue("name","");
	Base.setValue("loginid","");
}
//点击查询按钮触发
function fnQueryUsers(){
	var isShow=false;
	if($("#more").css("display")=="block"){
		isShow=true;
	}else{
		isShow=false;
	}
	Base.submit("userGd,userQuery", "orgUserMgAction!queryUsers.do",{"dto['isShow']":isShow});
}
// 单击查询人员数据
function fnClk(e, treeId, treeNode) {
	if(treeNode!=undefined&&treeNode!=null){
	var str=treeNode.orgnamepath;
	var strs=str.split("/");
	var newStr="";
	for(var i=0;i<strs.length;i++){
		if(i!=strs.length-1){
			newStr+=strs[i]+"->";
		}else{
			newStr+=strs[i];
		}
	}
	Base.setValue("dept",newStr);
	Base.setValue("deptId",treeNode.orgid);
	fnQueryUsers();
	}
}
// 添加子组织
function fnAddDept(event, treeId, treeNode) {
	if (treeNode.effective == 1){
		Base.msgTopTip("<div class='msgTopTip'>该组织已经禁用：无法添加子组织</div>");
	} else if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>该组织没有操作权限：无法添加子组织</div>");
	} else {
		var param={"dto['porgname']":treeNode.orgname,
					"dto['orglevel']":treeNode.orglevel,
					"dto['isleaf']":treeNode.isleaf,
					"dto['orgnamepath']":treeNode.orgnamepath,
					"dto['effective']":0,
					"dto['porgid']":treeNode.orgid,
					"dto['yab003']":treeNode.yab003,
					"dto['yab139']":treeNode.yab139};
		Base.openWindow("newWin","新增组织","orgUserMgAction!toAddOrg.do",param,500,450,null,null,true);
	}
}
// 新增组织成功后的回调函数
function fnSaveSuccessCb(data) {
	var treeObj = $.fn.zTree.getZTreeObj("orgTree");
	var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("porgid"));
	node.isParent = true;
	treeObj.updateNode(node);
	 treeObj.addNodes(node, data.fieldData.childOrg,false );
	if (confirm("新增组织成功，是否继续新增？")) {
		fnAddDept(null, "orgTree", $.fn.zTree.getZTreeObj("orgTree").getNodeByParam("orgid", Base.getValue("porgid")));
	} 
}
// 点击编辑按钮编辑组织信息
function fnToEdit(treeId, treeNode) {
	if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你没有该组织的操作权限：无法修改</div>");
		return false;
	} 
	var porgname="";
	if(treeNode.getParentNode()){
		porgname=treeNode.getParentNode().orgname;
	}
	var param={"dto['orgid']":treeNode.orgid,"dto['porgname']":porgname};
	Base.openWindow("newWin","修改组织","orgUserMgAction!toEditOrg.do",param,500,450,null,null,true);
	return false;
}
// 判断和提示能否删除组织
function fnBfRemove(treeId, treeNode) {
	if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你没有操作该组织权限：无法删除该组织</div>");
		return false;
	} 
	if (!treeNode.porgid || treeNode.porgid == "") {
		return Base.alert("不能删除顶级组织！", "warn"), false;
	} else if (treeNode.isParent) {
		return confirm("删除该组织会把下面的子组织一并删除,并且会删除组织相关的一切内容，确实要删除吗？");
	} else {
		return confirm("确实要删除该组织吗，删除组织会删除组织相关的一切内容，确认删除吗？");
	}
}
// 提交删除操作
function fnDelDept(event, treeId, treeNode) {
	if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>该组织没有操作权限：无法删除</div>");
		return false;
	} 
    Base.submit(null, "orgUserMgAction!deleteOrg.do", {"dto['orgid']":treeNode.orgid}, false, false,
   		function(){
   			Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
  			var treeObj = $.fn.zTree.getZTreeObj("orgTree");
   			var parentNode=treeNode.getParentNode();
   			if(parentNode.children.length==0){
	   			treeObj.removeChildNodes(parentNode); 
   			}
			treeObj.removeNode(treeNode);
			//删除组织后查询
			fnReset();
			fnQueryUsers();
   		}
    ); 
}
// 判断和提示能否拖拽调整组织排序
function fnBeforeDrop(treeId, treeNodes, targetNode, moveType) {
	var treeNode = treeNodes[0];
	if (treeNode.porgid != targetNode.porgid) {
		return Base.alert("非同级组织间不支持排序！"), false;
	} else if (moveType == "inner") {
		return Base.alert("不支持改变组织级次！"), false;
	}
	return confirm("是否保存对组织排序的修改？");
}
// 拖拽调整组织排序
function fnOnDrop(event, treeId, treeNodes, targetNode, moveType) {
	Base.setDisabled("update,save");
	var pNode = treeNodes[0].getParentNode();
	var sortid = [];
	for (var i = 0; i < pNode.children.length; i ++) {
		sortid.push({orgid: pNode.children[i].orgid});
	}
	Base.submit(null, "orgUserMgAction!sortOrg.do", {sortorgids:Ta.util.obj2string(sortid)} , false, false);
}
//点击编辑按钮，弹出人员编辑框
function fnEdit(data,e){
	fnBack();
	var target=event.srcElement?event.srcElement:event.target;
	var classname=$(target).attr('class');
	if(classname!="icon-edit"){
		return;
	}	
	Base.showBoxComponent("editUser",target);
	Base.submit("","orgUserMgAction!toEditUser.do",{"dto['userid']":data.userid})
	/* Base.openWindow("win", "["+data.name + "] 信息修改", "orgUserMgAction!toEditUser.do", {"dto['userid']":data.userid}, 300, 250, null, function(){
		fnQueryUsers();
	}, true); */
}
//点击表格中权限列
function fnPermission(data,e){
	Base.openWindow("win", data.name + "的权限列表", "orgUserMgAction!queryUserPermission.do", {"dto['userid']":data.userid}, "90%", "90%", null,null,true);
}
//显示岗位表格
function fnShowPossition(data,e){
	var target=event.srcElement?event.srcElement:event.target;
	var classname=$(target).attr('class');
	if(classname!="icon-find"){
		return;
	}	
	Base.showBoxComponent("b1",target);
	Base.submit("","orgUserMgAction!queryUserPosition.do",{"dto['userid']": data.userid});
}
function showMore(o,id){
	var $con = $('#'+id);
	if($con.is(':visible')){
		$con.hide();
		$(o).find('span').text('↓');
	}else{
		$con.show();
		$(o).find('span').text('↑');
		
	}
	$(window).resize();
}
</script>
<%@ include file="/ta/incfooter.jsp"%>