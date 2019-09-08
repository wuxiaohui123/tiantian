<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>可管理的组织范围</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading />
<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true" bodyStyle="overflow:auto;" withButtonBar="true">
	<ta:tree id="orgMgTree" checkable="true" fontCss="fnSetFont" chkboxType="{'Y':'s','N':'s'}"/>
	<ta:text id="positionid" display="false"/> 
	<ta:panelButtonBar align="right">
		<ta:button id="saveScopeOpBtn" key="保存[S]" hotKey="s" icon="icon-add1" isok="true" onClick="fnSaveOp('adminUserMgAction!saveOrgMgScope.do')"/>
		<ta:button id="closeOpBtn" key="关闭[X]" hotKey="x" icon="icon-no" onClick="parent.Base.closeWindow('mgScope');"/>
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
function fnSaveOp(url) {
debugger;
	var obj = Base.getObj("orgMgTree");
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
		Base.submit("panel1", url, {"ids":str},null,null,function(){ 
			parent.Base.msgTopTip("<div style='width:180px;margin:0 auto;font-size:14px;text-align:center;'>分配部门管理成功</div>");
			parent.refreshGrid3();
			parent.Base.closeWindow('mgScope');
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>