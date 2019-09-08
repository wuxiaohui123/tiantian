<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>日志</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar">
		<ta:pageloading/>
		<ta:box cols="3" id="box1">
			<ta:date id="startDate" key="开始时间" showSelectPanel="true"></ta:date>
			<ta:date id="endDate" key="结束时间" showSelectPanel="true"></ta:date>
			<ta:buttonLayout align="left">
				<ta:button key="查询" onClick="fnQuery()"></ta:button>
			</ta:buttonLayout>
		</ta:box>
		<ta:panel key="非法操作日志" fit="true" hasBorder="false" titleAlign="left">
			<ta:datagrid id="illegalOptGrid"  fit="true" haveSn="true"  columnFilter="true" forceFitColumns="true">
				<%-- <ta:datagridItem key="批次号" id="batchno" width="100"></ta:datagridItem> --%>
				<ta:datagridItem key="操作主体类型" id="opbody" collection="opobjtype" width="100"></ta:datagridItem>
				<ta:datagridItem key="操作主体" id="opsubjektname"  width="200" showDetailed="true"></ta:datagridItem>
				<ta:datagridItem key="操作类型" id="optype" collection="optype" width="150"></ta:datagridItem>
				<%-- <ta:datagridItem key="影响主体类型" id="influencebodytype" collection="opobjtype" width="100"></ta:datagridItem>
				<ta:datagridItem key="影响主体" id="influencebodyname"  width="200" showDetailed="true"></ta:datagridItem> --%>
				<ta:datagridItem key="变更内容" id="changcontent" showDetailed="true" width="400"></ta:datagridItem>
				<ta:datagridItem key="操作人" id="opusername" width="100"></ta:datagridItem>
				<ta:datagridItem key="操作岗位" id="oppositionname" width="200" showDetailed="true"></ta:datagridItem>
				<ta:datagridItem key="操作时间" id="optime" width="200" sortable="true" dataType="dateTime"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnQuery(){
		Base.submit("box1","illegalOperationLogAction!queryIllegalOperationLog.do");
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>