<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>列表对话框</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
	<ta:pageloading />
	<ta:fieldset id="condition" cols="4">
		<ta:text id="job_name" key="任务名称" />
		<ta:text id="address" key="服务器地址" />
		<ta:text id="service_id" key="服务ID" />
		<ta:selectInput id="success" key="成功标志" data="[{id:'1',name:'成功'},{id:'0',name:'失败'}]"/>
		<ta:date id="start_time" key="起止时间从" showSelectPanel="true" datetime="true"/>
		<ta:date id="end_time" key="到" showSelectPanel="true" datetime="true"/>
	</ta:fieldset>
	<ta:buttonLayout>
		<ta:submit key="查询[Q]" hotKey="Q" url="jobLogAction!query.do" submitIds="condition"/>
		<ta:submit key="清空成功记录[X]" hotKey="X" url="jobLogAction!clearSuccLog.do" />
	</ta:buttonLayout>
	<ta:panel key="任务执行记录录" fit="true">
		<ta:datagrid id="logs" fit="true" haveSn="true" forceFitColumns="true">
			<ta:datagridItem id="job_name" key="任务名称" />
			<ta:datagridItem id="address" key="服务器地址" width="140px"/>
			<ta:datagridItem id="service_id" key="服务ID" width="140px"/>
			<ta:datagridItem id="success" key="执行结果" formatter="fnHandleFlag"/>
			<ta:datagridItem id="fired_time" key="发生时间" width="145px"/>
			<ta:datagridItem id="log_msg" key="日志消息" width="160px"/>
			<ta:datagridItem key="检查" icon="icon-search" click="fnCheck" width="38px"/>
			<ta:dataGridToolPaging url="jobLogAction!query.do" submitIds="logs,condition" pageSize="100" showCount="false"/>
		</ta:datagrid>
	</ta:panel>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
	$("body").taLayout();
});
function fnHandleFlag(row, cell, value) {
	if (value == "1") return "成功";
	if (value == "0") return "失败";
}
function fnCheck(o){
	Base.openWindow("win", "任务详情", "jobLogAction!toCheck.do", {log_id:o.log_id,success:o.success,job_name:o.job_name,job_group:o.address,trigger_group:o.service_id}, 600, 380);
}
</script>
<%@ include file="/ta/incfooter.jsp"%>
