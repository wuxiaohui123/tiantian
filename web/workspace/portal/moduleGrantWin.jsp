<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>模块管理授权</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>

	<body class="no-scrollbar">
		<ta:pageloading />
		<ta:form id="moduleMainForm" fit="true">
			<ta:text id="roleId" display="false" />
			<ta:datagrid id="moduleList" height="320" selectType="checkbox">
				<ta:datagridItem width="80" id="moduleid" key="模块编号" />
				<ta:datagridItem width="135" id="modulename" key="模块名称" />
				<ta:datagridItem width="230px" id="moduleurl" key="模块URL" />
			</ta:datagrid>
			<ta:buttonLayout align="center">
				<ta:button key="保存" onClick="fnSaveGrant()" isok="true" />
				<ta:button key="关闭" onClick="parent.Base.closeWindow('grantWin');" />
			</ta:buttonLayout>
		</ta:form>
	</body>

</html>
<script type="text/javascript">
	var selectedItems = '${selectedItems}';
	selectedItems = eval(selectedItems);
	//console.log(selectedItems)
	$(document).ready(function() {
		$("body").taLayout();
		Base.setSelectRowsByData("moduleList",selectedItems);
	});
	function fnSaveGrant(){
		Base.submit("moduleList","<%=basePath%>sysapp/moduleMainAction!saveGrant.do",{"dto['roleId']":Base.getValue("roleId")},null,null,function(){
			alert("授权成功！");
			parent.Base.closeWindow("grantWin");
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>