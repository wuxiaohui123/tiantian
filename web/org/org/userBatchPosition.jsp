<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>YOURTITLE</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body style="margin:0px;padding:1px;" class="no-scrollbar" >
	<div id='pageloading'></div>
<ta:panel id="opPanel"  fit="true"  withButtonBar="true" hasBorder="false" cols="2" bodyStyle="padding:10px 10px 10px 10px;" cssStyle="overflow:auto">
	<ta:text id="userid" display="false"></ta:text>
	<ta:text id="w_orgid_1" display="false"></ta:text>
	<ta:box span="2">
		<ta:selectTree fontCss="fnSetFont" url="orgUserMgAction!webQueryAsyncOrgTree.do" required="true" 
		selectTreeBeforeClick="fnselecttree" key="直属组织" nameKey="orgname" asyncParam="['orgid']" 
		idKey="orgid" parentKey="porgid" targetDESC="w_orgname"
		treeId="w_orgTree" targetId="w_orgid" />
	</ta:box>
	<ta:box span="2" fit="true">
		<ta:text id="w1_orgid" key="目标部门Id"  display="false"/>
<!-- 		cssInput="background-color:#fff!important;"  -->
		<ta:text id="w1_orgname" key="附属组织" onClick="showMenu()" />
		<ta:box id ="menuContent" height="250px"  cssStyle="border-radius: 4px;box-shadow: 0 0 10px rgba(0,0,0,.5);background:white;display:none;width:274px;top:28px;left:100px;border:1px solid #99BBE8;overflow:auto;position:absolute;z-index:1000;">
			<ta:panel fit="true" withButtonBar="true" hasBorder="false" bodyStyle="overflow:auto;">
				<ta:tree id="w1_orgTree" nameKey="orgname" childKey="orgid" parentKey="porgid" checkable="true" async="true" asyncParam="['orgid']"  fontCss="fnSetFont"
				asyncUrl="orgUserMgAction!queryAsyncOrgTree.do" beforeCheck="fnBeforeCheck" onCheck="onCheck"  chkboxType="{'Y':'','N':''}" />
				<ta:panelButtonBar align="center" >
					<ta:button key="清除"  id="menuBtn" onClick="fnFsRemove()"></ta:button>
					<ta:button key="关闭" icon="icon-no"  id="menuBtn1" onClick="hideMenu()"></ta:button>
				</ta:panelButtonBar>
			</ta:panel>
		</ta:box>
	</ta:box>
	<ta:panelButtonBar>
		<ta:button key="保存[S]" icon="icon-add1" hotKey="S" isok="true" onClick="fnChangePosition()" />
		<ta:button key="关闭[X]" hotKey="X"  onClick="parent.Base.closeWindow('win')"/>
	</ta:panelButtonBar>
</ta:panel>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		
		document.getElementById("w1_orgname").readOnly = true;
	});
	//单人组织修改中的树
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
	
function fnChangePosition(){
	var obj = $.fn.zTree.getZTreeObj("w1_orgTree");
	var str = "";
	if(obj!=null||obj!=undefined){
		var nodes = obj.getChangeCheckedNodes();
		var len = nodes.length;
		var flag = true;
		if (len == 0) {
			flag = false;
		}
		for (var i = 0; i < len; i++) {
			str += "{\"id\":\"" + nodes[i].orgid + "\",\"checked\":" + nodes[i].checked + "},";
			nodes[i].checkedOld = nodes[i].checked;
		}
		if(!flag && Base.getValue("w_orgid_1") == Base.getValue("w_orgid")){
			Base.alert("没有改变，无需保存","warn");
			return;
		}
		if (str != "") {
			str = "[" + str.substr(0, str.length - 1) + "]";
		}
	}
	Base.submit("opPanel", "orgUserMgAction!saveBatchOrg.do", {"ids":str},null,null,function(){
		parent.Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>组织更改成功!</div>");
		parent.Base.closeWindow("win");
	});
}
function fnBeforeClick(treeId, treeNode){
	if (treeNode.admin != true) return false;
}

function fnFsRemove(){
	var zTree = $.fn.zTree.getZTreeObj("w1_orgTree");
	zTree.checkAllNodes(false);
	var cityObj = $("#w1_orgname");
	cityObj.attr("value", "");
	var targetDepartId1 = $("#w1_orgid");
	targetDepartId1.attr("value", "");
	Base.hideObj("menuContent");
}
function fnBeforeCheck(treeId, treeNode) {
	var w_orgid = Base.getValue("w_orgid");
	if(w_orgid && w_orgid != ""){
		if(treeNode.orgid == w_orgid){
			Base.alert("直属组织已经是该组织，附属组织不能选择为直属组织","warn");
			return false;
		}
	}else{
		Base.alert("请先选择直属组织","warn");
		return false;
	}
	if(!treeNode.admin){
		Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
		return false;
	}
	return true;
//		var zTree = $.fn.zTree.getZTreeObj("w1_orgTree");
//		zTree.checkNode(treeNode, !treeNode.checked, null, true);
//		return false;
}

function onCheck(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("w1_orgTree"),
	nodes = zTree.getCheckedNodes(true),
	v = "",hv = "";
	
	for (var i=0, l=nodes.length; i<l; i++) {
		v += nodes[i].orgname + ",";
		hv += nodes[i].orgid + ",";
	}
	if (v.length > 0 ) v = v.substring(0, v.length-1);
	if (hv.length > 0 ) hv = hv.substring(0, hv.length-1);
	var cityObj = $("#w1_orgname");
	cityObj.attr("value", v);
	var targetDepartId1 = $("#w1_orgid");
	targetDepartId1.attr("value", hv);
}
function showMenu() {
	$("#menuContent").slideDown("fast");

	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "w1_orgname" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
		hideMenu();
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>