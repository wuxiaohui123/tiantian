<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<form method="post" id="editForm" style="padding:3px;">
	<ta:box cols="1" layout="column">
		<ta:text key="Job名称" id="jobName" required="true" readOnly="true"/>
		<ta:text key="地址和端口" id="jobGroup" required="true" readOnly="true"/>
		<%-- <ta:text key="Job类名" id="jobClass" required="true" /> --%>
		<ta:text key="Job描述" id="jobDesc" maxLength="125" readOnly="true"/>
		<ta:selectInput key="TRIGGER类型" id="triggerType" value="2" onSelect="fnSelectCbk" required="true" data="[{'name':'SimpleTrigger','id':'1'},{'name':'CronTrigger','id':'2'}]" readOnly="true"/>
		<ta:text key="服务ID" id="triggerGroup" required="true" readOnly="true"/>
		<ta:date key="定时开始时间" id="startTime" showSelectPanel="true" datetime="true" required="true" readOnly="true"/>
		<ta:date key="定时结束时间" id="endTime" showSelectPanel="true" datetime="true" readOnly="true" />
		<ta:number key="重复执行次数(-1不限)" id="repeatCount" toolTip="不限次数:-1" labelWidth="150" required="true" alignLeft="true" display="false" readOnly="true"/>
		<ta:number key="间隔时间(秒)" id="repeatInterval" required="true" alignLeft="true" display="false" readOnly="true"/>
		<ta:text key="CronTrigger表达式" id="cronExpression" labelWidth="150" columnWidth="400px" required="true" readOnly="true"/>
		<ta:text id="isSuccess" display="false"/>
		<ta:text id="log_id" display="false"/>
		<ta:buttonLayout align="center" span="2">
			<ta:button id="resumeBtn" key="恢复任务[S]" hotKey="S" icon="icon-reload" onClick="fnResumeJob()" />
			<ta:button id="newBtn" key="以新任务恢复[N]" hotKey="N" icon="icon-setting" onClick="fnNewJob()" />
			<ta:button id="closeBtn" key="关闭[X]" hotKey="X" icon="icon-no" onClick="Base.closeWindow('win')" />
		</ta:buttonLayout>
	</ta:box>
</form>
<script type="text/javascript">
$(document).ready(function () {
    var v = Base.getValue("triggerType");
    if(v == "SimpleTrigger") {
		Base.hideObj("cronExpression");
		Base.showObj("repeatInterval,repeatCount");
	} else {
		Base.showObj("cronExpression");
		Base.hideObj("repeatCount,repeatInterval");
	}
    if ("1" == Base.getValue("isSuccess")) {
    	Base.setDisabled("resumeBtn,newBtn");
    }
});
function fnSelectCbk(o) {
	if(o == "SimpleTrigger") {
		Base.hideObj("cronExpression");
		Base.showObj("repeatInterval,repeatCount");
	} else {
		Base.showObj("cronExpression");
		Base.hideObj("repeatCount,repeatInterval");
	}
}
function fnResumeJob() {
	Base.submit("editForm", "jobLogAction!resumeJob.do", null, null, false);
}
function fnNewJob() {
	Base.prompt("新的Cron表达式：", function(yes, value) {
		if (yes) {
			Base.submit("editForm", "jobLogAction!newJob.do", {cronExpression:value}, null, false);
		}
	});
}
</script>
<%@ include file="/ta/incfooter.jsp"%>
