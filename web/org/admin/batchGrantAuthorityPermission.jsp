<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>批量授予授权权限</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading />
	<ta:fieldset cssStyle="margin:10px;">
		<div  style="color:red;">注：这两种授权都是针对管理员，如甲乙两个管理员，甲给乙授了左边的权限，则乙可以给普通操作员授相应的操作权限，甲给乙授了右边的权限，则乙可以给管理员授相应的授权权限。</div>
	</ta:fieldset>
	<ta:panel id="gpanel" hasBorder="true" expanded="false" fit="true"  bodyStyle="overflow:auto;margin:0px 10px 10px 10px;" cols="2" layout="column" withButtonBar="true">
		<ta:panel  key="授权权限" hasBorder="true" expanded="false" bodyStyle="border-left:0;border-bottom:0;background:white">
			<ta:tree id="grantingTree1" checkable="true"  checkedKey="checked1" onCheck="fnTreeChg1" onExpand="fnSyncExpand" onCollapse="fnSyncCollapse" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
		</ta:panel>
		<ta:panel  key="再授权权限" hasBorder="true" expanded="false" bodyStyle="border-right:0;border-bottom:0;background:white" cssStyle="margin-left:10px;">
			<ta:tree id="grantingTree2" checkable="true" checkedKey="checked2" onCheck="fnTreeChg2"  onExpand="fnSyncExpand" onCollapse="fnSyncCollapse" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
		</ta:panel>
		<ta:text id="grantAuthorityPermissionsPositionids" display="false"></ta:text>
		<ta:panelButtonBar align="right">
			<ta:button id="saveRoleGrantingBtn" key="保存[S]" hotKey="S" icon="icon-add1" isok="true" onClick="fnSaveGranting('adminUserMgAction!grantAuthorityPermissions.do')" cssStyle="top:-7px;"/>
			<ta:button id="closeGrantingBtn" key="关闭[X]" hotKey="X" icon="icon-no" onClick="parent.Base.closeWindow('grantingWin');"  cssStyle="top:-7px;"/>
		</ta:panelButtonBar>
	</ta:panel>
</body>
</html> 
<script>
$(document).ready(function() {
		$("body").taLayout();
	}) 
	
function fnSetFont(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return {color:"red"};
}
function fnBfChk(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return Base.alert("该功能无需安全认证，所有用户都有权限，即便修改也无效。"), false;
	return true;
}
function fnSaveGranting(url) {
	var tree1 = Base.getObj("grantingTree1");
	var tree2 = Base.getObj("grantingTree2");
	var nodes1 = tree1.getChangeCheckedNodes();
	var nodes2 = tree2.getChangeCheckedNodes();
	var checkedNodes1 = tree1.getCheckedNodes(true);
	var checkedNodes2 = tree2.getCheckedNodes(true);
	var len1 = nodes1.length;
	var len2 = nodes2.length;
	if (len1 == 0 && len2 == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var str = "";
	for(var m = 0; m < checkedNodes1.length; m++){
		str += "{\"id\":\"" + checkedNodes1[m].id + "\",\"checked\":\"true\"},";
	}
	for(var n = 0; n < checkedNodes2.length; n++){
		str += "{\"id\":\"" + checkedNodes2[n].id + "\",\"checked\":\"true\",\"re\":\"1\"},";
	}
	for (var i = 0; i < len1; i++) {
		nodes1[i].checkedOld = nodes1[i].checked1;
	}
	for (var j = 0; j < len2; j++) {
		nodes2[j].checkedOld = nodes2[j].checked2;
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("gpanel", url, {"ids":str,"positionids":Base.getValue("grantAuthorityPermissionsPositionids")},null,null,function(){
			parent.Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:5px;text-align:center;'>批量授予权限成功</div>");
			parent.Base.closeWindow("grantingWin");
		});
	}
	
}
function fnSyncExpand(event, treeId, treeNode) {
	var treeObj;
	if (treeId == "grantingTree1") {
		treeObj = Base.getObj("grantingTree2");
	} else {
		treeObj = Base.getObj("grantingTree1");
	}
	treeObj.expandNode(treeObj.getNodeByParam("id", treeNode.id), true, false, false);
}
function fnSyncCollapse(event, treeId, treeNode) {
	var treeObj;
	if (treeId == "grantingTree1") {
		treeObj = Base.getObj("grantingTree2");
	} else {
		treeObj = Base.getObj("grantingTree1");
	}
	treeObj.expandNode(treeObj.getNodeByParam("id", treeNode.id), false, false, false);
}
function fnTreeChg1(event, treeId, treeNode) {
	var tree2 = Base.getObj("grantingTree2");
	var treeNode2 = tree2.getNodeByParam("id", treeNode.id);
	var checkbox2 = $("#" + treeNode2.tId + "_check");
	if (!treeNode.checked1 && treeNode2.checked2)
		checkbox2.click();
}
function fnTreeChg2(event, treeId, treeNode) {
	var tree1 = Base.getObj("grantingTree1");
	var treeNode1 = tree1.getNodeByParam("id", treeNode.id);
	var checkbox1 = $("#" + treeNode1.tId + "_check");
	if (treeNode.checked2 && !treeNode1.checked1)
		checkbox1.click();
	else if (treeNode.isParent && treeNode.checked2 && treeNode1.checked1) {
		var children = treeNode1.children;
		fnIterateNode(children);
	}
}
function fnIterateNode(children) {
	for (var i = 0; i < children.length; i++) {
		if (!children[i].checked1) {
			$("#" + children[i].tId + "_check").click();
		}
		if (children[i].isParent) fnIterateNode(children[i].children);
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>