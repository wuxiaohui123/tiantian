<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<ta:panel id="opPanel"  fit="true"  withButtonBar="true" hasBorder="false" cols="2">
	<ta:text id="userid" display="false"></ta:text>
	<ta:text id="w_orgid_1" display="false"></ta:text>
	<ta:box span="2">
		<ta:selectTree fontCss="fnSetFont" url="userMgAction!webQueryAsyncOrgTree.do" required="true" 
		selectTreeBeforeClick="fnselecttree" key="直属部门" nameKey="orgname" asyncParam="['orgid']" 
		idKey="orgid" parentKey="porgid" targetDESC="w_orgname"
		treeId="w_orgTree" targetId="w_orgid" />
	</ta:box>
	<ta:box span="2" fit="true">
		<ta:text id="w1_orgid" key="目标部门Id"  display="false"/>
		<ta:text id="w1_orgname" key="附属组织" onClick="showMenu();" readOnly="true" />
		<ta:box id ="menuContent" height="250px"  cssStyle="background:white;display:none;width:220px;left:100px;border:1px solid #99BBE8;overflow:auto;position:absolute;z-index:1000;">
			<ta:panel fit="true" withButtonBar="true" hasBorder="false" bodyStyle="overflow:auto;">
				<ta:tree id="w1_orgTree" nameKey="orgname" childKey="orgid" parentKey="porgid" checkable="true" async="true" asyncParam="['orgid']"  fontCss="fnSetFont"
				asyncUrl="userMgAction!webQueryAsyncOrgTree.do" beforeCheck="fnBeforeCheck" onCheck="onCheck"  chkboxType="{'Y':'','N':''}" />
				<ta:panelButtonBar align="center" >
					<ta:button key="清除"  id="menuBtn" onClick="fnFsRemove()"></ta:button>
					<ta:button key="关闭" icon="icon-no"  id="menuBtn1" onClick="hideMenu()"></ta:button>
				</ta:panelButtonBar>
			</ta:panel>
		</ta:box>
	</ta:box>
	<ta:panelButtonBar>
		<ta:button key="保存[S]" icon="icon-add1" hotKey="S" isok="true" onClick="fnChangePosition()" />
		<ta:button key="关闭[X]" hotKey="X"  onClick="Base.closeWindow('win')"/>
	</ta:panelButtonBar>
</ta:panel>
<script type="text/javascript">
function fnChangePosition(){
	var obj = Base.getObj("w1_orgTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	var flag = true;
	if (len == 0) {
		flag = false;
	}
	var str = "";
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
	Base.submit("opPanel", "userMgAction!webSaveBatchOrg.do", {"ids":str},null,null,function(){
		Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>组织更改成功!</div>");
		Base.closeWindow("win");
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
			Base.alert("直属部门已经是该部门，附属部门不能选择为直属部门","warn");
			return false;
		}
	}else{
		Base.alert("请先选择直属部门","warn");
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