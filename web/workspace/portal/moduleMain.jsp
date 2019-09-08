<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>模块管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>

	<body class="no-scrollbar">
		<ta:pageloading />
		<ta:button id="btn_query" key="查询" cssStyle="width:70px"
			icon="icon-search" onClick="fnQueryModuleList()" isok="true" />
		<ta:button id="btn_add" key="新增" icon="icon-add1"
			cssStyle="width:70px" onClick="fnAddItem()" />
		<ta:form id="moduleMainForm" fit="true" cssStyle="margin-top:10px;">
			<ta:panel id="queryResult" key="查询结果" bodyStyle="height:400px;">
				<ta:datagrid id="moduleList" fit="true" haveSn="true">
					<ta:datagridItem width="150px" id="moduleid" key="模块编号" />
					<ta:datagridItem width="350px" id="modulename" key="模块名称" />
					<ta:datagridItem width="450px" id="moduleurl" key="模块url" />
					<ta:datagridItem width="100px" id="moduleheight" key="模块高度" />
					<ta:datagridItem width="60px" id="edit" key="编辑" icon="icon-edit"
						click="fnEditItem" />
					<ta:datagridItem width="60px" id="delete" key="删除" icon="icon-no"
						click="fnDeleteItem" />
				</ta:datagrid>
			</ta:panel>
		</ta:form>
	</body>

</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
	function fnAddItem(){
		Base.openWindow("addWin","新增模块","<%=basePath%>sysapp/moduleMainAction!toAdd.do",null,500,270,null,fnQueryModuleList,true);
	}
	function fnQueryModuleList(){
		Base.submit("moduleMainForm","<%=basePath%>sysapp/moduleMainAction!query.do");
	}
	function fnDeleteItem(o){
		if(window.confirm("确定删除 ["+o.modulename+"] 模块？"))
			Base.submit("moduleMainForm","<%=basePath%>sysapp/moduleMainAction!doUpdate.do",{"dto['isDelete']":1,"dto['moduleId']":o.moduleid},null,null,fnQueryModuleList);
	}
	function fnEditItem(o){
		Base.openWindow("editWin","修改模块","<%=basePath%>sysapp/moduleMainAction!toUpdate.do",{"dto['moduleId']":o.moduleid},500,270,null,fnQueryModuleList,true);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>