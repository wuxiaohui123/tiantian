<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>岗位管理</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:400}"
	style="padding:0px;margin:0px">
	<ta:pageloading />
	<ta:box position="left" key="部门" cssStyle="overflow:auto;">
		<ta:tree id="deptForRoleScope" async="true" childKey="orgid" nameKey="orgname" parentKey="porgid"
			asyncUrl="moduleMainAction!webQueryAsyncOrgTree.do" asyncParam="['orgid']"
			onClick="fnQuery" />
	</ta:box>
	<ta:panel position="center" key="岗位信息" id="userPanel" hasBorder="false" expanded="false" fit="true">
		<ta:text id="departId" display="false" />
		<ta:datagrid id="positionGrid" haveSn="true" fit="true"
			 onSelectChange="fnSlctChg">
			<ta:datagridItem id="positionname" key="岗位名称" width="350" sortable="true" />
			<ta:datagridItem id="positiontype" key="类型" collection="POSITIONTYPE" width="350"
				sortable="true" />
			<ta:datagridItem id="selectModule" key="模块选择" 
				width="120"  click="fnSelectModule" icon="icon-application_form_add" />
		</ta:datagrid>
	</ta:panel>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
		var treeObj = Base.getObj("deptForRoleScope");
		var treeNode = treeObj.getNodeByTId("deptForRoleScope_1");
		treeObj.selectNode(treeNode); // 选中第一个节点
		treeObj.expandNode(treeNode); // 展开第一个节点
		Base.setValue("departId", treeNode.id);
		var param = {};
		param["dto['orgid']"] = treeNode.orgid;
		Base.submit("positionGrid,departId", "moduleMainAction!getAllPositions.do",param);
	});
	function fnQuery(event, treeId, treeNode) {
		Base.submit("departId", "moduleMainAction!getAllPositions.do",{"dto['orgid']":treeNode.orgid});
	}
	function fnSlctChg(o) {
		if (o.length == 1) {
			Base.setEnable(["editBtn","delBtn"]);
		} else if (o.length > 1) {
			Base.setEnable(["delBtn"]);
			Base.setDisabled("editBtn");
		} else {
			Base.setDisabled(["editBtn","delBtn"]);
		}
	}
	function fnSelectModule(o){
		Base.openWindow("grantWin","模块授权","<%=basePath%>sysapp/moduleMainAction!toGrantWin.do",{"dto['roleId']":o.positionid},500,400,null,null,true);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>