<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>管理类功能权限</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading /> 
<ta:panel id="addpanel" fit="true" cols="1" bodyStyle="overflow:auto;" withButtonBar="true" hasBorder="false">
	<ta:tree id="adminTree" checkable="true" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	<ta:text id="adminPositionid" display="false"/> 
	<ta:text id="positionType" display="false"/>
	<ta:panelButtonBar align="right">
		<ta:button id="saveBtn" key="保存[S]" hotKey="S"  icon="icon-add1" isok="true" onClick="fnSaveOp('adminUserMgAction!saveAdminUsePermission.do')"/>
		<ta:button id="closeOpBtn" key="关闭[X]" hotKey="X" icon="icon-no" onClick="parent.Base.closeWindow('adminUseWin');"/>
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
function fnSaveOp(url) {
	var obj = Base.getObj("adminTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	if (len == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var str = "";
	for (var i = 0; i < len; i++) {
		str += "{\"id\":\"" + nodes[i].id + "\",\"checked\":" + nodes[i].checked + "},";
		nodes[i].checkedOld = nodes[i].checked;
	} 
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("addpanel", url, {"ids":str,"dto['positionid']":Base.getValue("adminPositionid")},null,null,function(){
			parent.Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:14px;text-align:center;'>授权成功!</div>");
			parent.refreshGrid1();
			parent.Base.closeWindow("adminUseWin");
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>