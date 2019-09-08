<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>添加流程角色</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
	<ta:pageloading />
	<ta:panel id="pgroup" fit="true" hasBorder="false" withButtonBar="true" padding="10px">
		<ta:text id="groupname" key="角色名称" labelWidth="70" required="true"/>
		<ta:selectInput id="grouptype" key="角色类型" labelWidth="70"/>
		<ta:panelButtonBar>
			<ta:button key="确定" icon="icon-ok" isShowIcon="true" onClick="fnAddGroup()" isok="true"/>
			<ta:button key="放弃" icon="icon-cancel" isShowIcon="true" onClick="parent.Base.closeWindow('win');"/>
		</ta:panelButtonBar>
	</ta:panel>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
	function fnAddGroup(){
		Base.submit("pgroup","processRoleAction!addGroup.do",null,null,true,function(){
			parent.Base.closeWindow("win");
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
