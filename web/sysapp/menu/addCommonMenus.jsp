<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>常用菜单</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar">
		<ta:pageloading/>
<ta:box fit="true">
	<ta:box cssStyle="overflow:auto;" fit="true" heightDiff="40">
		<ta:tree id="commonMenuTree" checkable="true"  nameKey="menuname" childKey="menuid" parentKey="pmenuid" chkboxType="{'Y':'s', 'N':'ps'}"/>
	</ta:box>
	<ta:buttonLayout>
		<ta:button id="saveScopeOpBtn" key="保存"  isok="true" onClick="fnSaveOp('/sysapp/commonMenuAction!saveCommonMenus.do')" />
		<ta:button id="closeOpBtn" key="关闭"  onClick="parent.Base.closeWindow('addWin');" />
	</ta:buttonLayout>
</ta:box>
	</body>
</html>
<script>
$(document).ready(function () {
	$("body").taLayout();
});
function sendMsgToFrame(type, msg, args){
		    try {
		        var o = {};
		        o.type = type;
		        o.msg = msg;
		        o.args = args;
				o = Ta.util.obj2string(o);
		        window.top.postMessage(o, "*");
		    } 
		    catch (e) {
		  }
		}
function fnSaveOp(url) {
	var obj = Base.getObj("commonMenuTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	if (len == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var str = "";
	for (var i = 0; i < len; i++) {
		var node = nodes[i];
		str += "{\"menuId\":\"" + node.menuid + "\",\"checked\":\"" + node.checked + "\"},";
		node.checkedOld = node.checked;
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("", Base.globvar.basePath + url, {"commonmenus":str},null,null,function(){
			parent.Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>添加常用菜单成功!</div>");
			parent.Base.closeWindow("addWin");
			sendMsgToFrame("function", "getCommonMenu");
		});
	}
}

</script>
<%@ include file="/ta/incfooter.jsp"%>