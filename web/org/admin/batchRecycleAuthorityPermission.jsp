<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>批量回收授权权限</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading />
<ta:fieldset cssStyle="margin:10px;">
	<div  style="color:red;">注：这两种授权都是针对管理员，如甲乙两个管理员<br/>(1)甲收回了乙左边的权限,则乙右边的权限也跟着被回收;乙不再有授权权限和相对应的再授权权限;<br/>(2)甲收回了乙右边的权限,则左边的权限不受影响;乙不再具有相应的再授权权限<br/>(3)勾选表示回收</div>
</ta:fieldset>
<ta:panel id="panel1" hasBorder="true" expanded="false" fit="true"  bodyStyle="overflow:auto;margin:0px 10px 10px 10px;" cols="2" withButtonBar="true">
	<ta:panel  key="授权权限" hasBorder="true" expanded="false"  bodyStyle="border-bottom:0px;border-left:0px;background:white">
		<ta:tree id="grantingTree1" checkable="true" chkboxType="{'Y':'s','N':''}" checkedKey="checked1" onCheck="fnTreeChg1" onExpand="fnSyncExpand" onCollapse="fnSyncCollapse" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	</ta:panel>
	<ta:panel  key="再授权权限" hasBorder="true" expanded="false" cssStyle="margin-left:10px;" bodyStyle="border-bottom:0px;border-right:0px;background:white">
		<ta:tree id="grantingTree2" checkable="true" chkboxType="{'Y':'s','N':''}" checkedKey="checked2" onCheck="fnTreeChg2" onExpand="fnSyncExpand" onCollapse="fnSyncCollapse" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	</ta:panel>
	<ta:text id="recycleAuthorityPermissionsPositionids" display="false"></ta:text>
	<ta:panelButtonBar align="right">
		<ta:button id="saveRoleGrantingBtn" key="保存[S]" hotKey="S" icon="icon-add1" isok="true" onClick="fnSaveGranting('adminUserMgAction!recycleAuthorityPermissions.do')" cssStyle="top:-7px;"/>
		<ta:button id="closeGrantingBtn" key="关闭[X]" hotKey="X" icon="icon-no" onClick="parent.Base.closeWindow('grantingWin');" cssStyle="top:-7px;"/>
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
		str += "{\"id\":\"" + checkedNodes1[m].id + "\",\"checked\":\"true\",\"permission\":\"1\"},";
	}
	for(var n = 0; n < checkedNodes2.length; n++){
		str += "{\"id\":\"" + checkedNodes2[n].id + "\",\"checked\":\"true\",\"authrity\":\"1\"},";
	}
	for (var i = 0; i < len1; i++) {
		nodes1[i].checkedOld = nodes1[i].checked1;
	}
	for (var j = 0; j < len2; j++) {
		nodes2[j].checkedOld = nodes2[j].checked2;
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("panel1", url, {"ids":str,"positionids":Base.getValue("recycleAuthorityPermissionsPositionids")},null,null,function(){
			parent.Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:5px;text-align:center;'>批量回收权限成功</div>");
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
	var tree1 = Base.getObj(treeId);
	var ptree1Node =  tree1.getNodeByParam("id", treeNode.pId);
	var p1checkbox = $("#" + treeId + "_" + treeNode.pId + "_check");
	var tree2 = Base.getObj("grantingTree2");
	var treeNode2 = tree2.getNodeByParam("id", treeNode.id);
	var checkbox2 = $("#" + treeNode2.tId + "_check");
	if(treeNode2){
		if (treeNode.checked1 && !treeNode2.checked2)
			checkbox2.click();
	}
	if(ptree1Node){
		if (!treeNode.checked1 && ptree1Node.checked1)
			p1checkbox.click();
	}
	
}
function fnTreeChg2(event, treeId, treeNode) {
	var tree1 = Base.getObj("grantingTree1");
	var treeNode1 = tree1.getNodeByParam("id", treeNode.id);
	var checkbox1 = $("#" + treeNode1.tId + "_check");
	var tree2 = Base.getObj(treeId);
	var ptree2Node =  tree2.getNodeByParam("id", treeNode.pId);
	var p2checkbox = $("#" + treeId + "_" + treeNode.pId + "_check");
	if(ptree2Node){
		if (!treeNode.checked2 && ptree2Node.checked2)
			p2checkbox.click();
	}
	if(treeNode1){
		if (!treeNode.checked2 && treeNode1.checked1)
			checkbox1.click();
// 		else if (treeNode.isParent && treeNode.checked2 && treeNode1.checked1) {
// 			var children = treeNode1.children;
// 			fnIterateNode(children);
// 		}
	}
}
// function fnIterateNode(children) {
// 	for (var i = 0; i < children.length; i++) {
// 		if (!children[i].checked1) {
// 			$("#" + children[i].tId + "_check").click();
// 		}
// 		if (children[i].isParent) fnIterateNode(children[i].children);
// 	}
// }
</script>
<%@ include file="/ta/incfooter.jsp"%>