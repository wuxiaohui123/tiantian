<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>岗位</title>
		<%@ include file="/ta/inc.jsp"%>
		
	</head>
	<body id="body1" class="no-scrollbar">
		<ta:pageloading/>
		
		<ta:box fit="true" >
			<ta:text id="positionid" display="false"/>
			<ta:text id="positionname" display="false"/>
			<ta:tableView width="100%" id="table1" key="您还可以进行如下操作">
				<ta:tr>
					<ta:td cssStyle="text-align:left">1.继续新增岗位</ta:td>
					<ta:td cssStyle="text-align:center"><ta:button id="add" isok="true"  key="新增岗位" onClick="fnAdd()"/></ta:td>
				</ta:tr>
				<ta:tr>
					<ta:td cssStyle="text-align:left">2.设置人员</ta:td>
					<ta:td cssStyle="text-align:center"><ta:button id="opUser"  key="设置人员" onClick="fnAssignUser()"/></ta:td>
				</ta:tr>
				<ta:tr>
					<ta:td cssStyle="text-align:left">3.授予使用权限</ta:td>
					<ta:td cssStyle="text-align:center"><ta:button id="usePer"  key="使用权限" onClick="fnUsePermission()"/></ta:td>
				</ta:tr>
			</ta:tableView>
			<ta:buttonLayout>
				<ta:button id="removeBt"  key="关闭" icon="icon-no" onClick="parent.Base.closeWindow('operWin');"/>
			</ta:buttonLayout>
		</ta:box>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnAdd(){
		parent.Base.closeWindow("operWin");
	}
	function fnAssignUser(){
		parent.Base.openWindow("assignUser",Base.getValue("positionname") + "->人员选择","<%=basePath%>org/position/positionMgAction!toAssignUser.do",{"dto['positionid']":Base.getValue("positionid")},"90%","90%",null,null,true);
	}
	function fnUsePermission(){
		parent.Base.openWindow("opWin", Base.getValue("positionname") + "->功能使用权限", "<%=basePath%>org/position/grantMgAction!toFuncOpPurview.do", {"dto['positionid']":Base.getValue("positionid"),"dto['positionType']":1}, "35%", "80%");
	}
// 	function fnRePermission(data,e) {
// 		if (o.type != "02") return Base.alert("非管理员岗位不能授授权权限。"), false;
// 		parent.Base.openWindow("grantingWin", Base.getValue("positionname") + "->功能授权权限", "<%=basePath%>org/position/grantMgAction!toFuncGrantingPurview.do", {"dto['positionid']":Base.getValue("positionid"),"dto['positionType']":1}, "70%", "80%");
// 	}
</script>
<%@ include file="/ta/incfooter.jsp"%>