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
		<div  style="color:red;">注:你在表格中所勾选的岗位将"失去"这些被勾选的资源的使用权限.</div>
</ta:fieldset>
<ta:panel id="panel1" hasBorder="true" fit="true" cols="1" bodyClass="linecolor"  bodyStyle="overflow:auto;margin:0px 10px 10px 10px;background:white;" withButtonBar="true">
	<ta:tree id="opTree" checkable="true" chkboxType="{'Y':'s','N':''}" onCheck="fnTreeChg1" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	<ta:text id="recyclePermissionsPositionids" display="false"/>
	<ta:text id="positionType" display="false"/>
	<ta:panelButtonBar>
		<ta:button id="personalrecyclePermissionsOpBtn" key="保存[S]" hotKey="s" icon="icon-add1" isok="true"  onClick="fnRecyclePermissionsOp('positionUserMgAction!recyclePermissions.do')" cssStyle="top:-7px;"/>
		<ta:button id="recyclePermissionsOpBtn" key="保存[S]" hotKey="s" icon="icon-add1" isok="true"  onClick="fnRecyclePermissionsOp('positionUserMgAction!recyclePermissions.do')" cssStyle="top:-7px;"/>
		<ta:button id="closeOpBtn" key="关闭[X]" hotKey="x" icon="icon-no" onClick="parent.Base.closeWindow('opWin');" cssStyle="top:-7px;"/>
	</ta:panelButtonBar>
</ta:panel>
</body>
</html> 
<script>
$(document).ready(function() {
		$("body").taLayout();
}) 
function fnTreeChg1(event, treeId, treeNode) {
	var opTree = Base.getObj("opTree");
	var ptreeNode = opTree.getNodeByParam("id", treeNode.pId);
	var pcheckbox = $("#" + treeId + "_" + treeNode.pId + "_check");
	if(ptreeNode){
		if (!treeNode.checked && ptreeNode.checked)
			pcheckbox.click();
	}
}
function fnSetFont(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return {color:"red"};
}
function fnBfChk(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return Base.alert("该功能无需安全认证，所有用户都有权限，即便修改也无效。"), false;
	return true;
}

function fnRecyclePermissionsOp(url){
	var obj = Base.getObj("opTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	if (len == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var checkedNodes = obj.getCheckedNodes(true);
	var str = "";
	for (var i = 0; i < len; i++) {
		nodes[i].checkedOld = nodes[i].checked;
	}
	for(var i = 0 ; i < checkedNodes.length; i++){
		var node = checkedNodes[i];
		if(node.isyab003 != null){
			var pnode = node.getParentNode();
			str += "{\"permissionid\":\"" + node.id + "\",\"isyab003\":\""+node.isyab003+"\",\"menuid\":\""+pnode.id+"\"},";
		}else{
			str += "{\"permissionid\":\"" + node.id + "\"},";
		}
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("panel1", url, {"ids":str,"positionids":Base.getValue("recyclePermissionsPositionids")},null,null,function(){
			parent.Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>批量回收使用权限成功</div>");
			parent.Base.closeWindow('opWin');
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>