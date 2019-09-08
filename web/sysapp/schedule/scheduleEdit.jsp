<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
	<ta:panel cols="1" id="editForm"  layout="column" withButtonBar="true" padding="3px">
		<ta:text key="Job名称" id="jobName" required="true" />
		<ta:text key="地址和端口" id="jobGroup" required="true" />
		<%-- <ta:text key="Job类名" id="jobClass" required="true" /> --%>
		<ta:text key="Job描述" id="jobDesc" maxLength="125"/>
		<ta:selectInput key="TRIGGER类型" id="triggerType" value="2" onSelect="fnSelectCbk" required="true" data="[{'name':'SimpleTrigger','id':'1'},{'name':'CronTrigger','id':'2'}]"/>
		<ta:text key="服务ID" id="triggerGroup" required="true" />
		<ta:date key="定时开始时间" id="startTime" showSelectPanel="true" datetime="true"  required="true"/>
		<ta:date key="定时结束时间" id="endTime" showSelectPanel="true" datetime="true"  />
		<ta:number key="重复执行次数(-1不限)" id="repeatCount" toolTip="不限次数:-1" labelWidth="150" required="true" alignLeft="true" display="false"/>
		<ta:number key="间隔时间(秒)" id="repeatInterval" required="true" alignLeft="true" display="false"/>
		<ta:text key="Cron表达式" id="cronExpression" required="true" />
		<ta:selectInput key="发生异常是否暂停" id="isPause" data="[{id:'0',name:'不暂停'},{id:'1',name:'要暂停'},{id:'2',name:'仅当通讯异常时暂停'},{id:'3',name:'仅当业务异常时暂停'}]"/>
		<ta:textarea key="Job变量" id="jobData" readOnly="true" height="60px"/>
		<%--<ta:button id="cronGenBtn" key="表达式生成器[D]" hotKey="D" icon="icon-edit" onClick="opneCronWin()" columnWidth="80px" display="false"/> --%>
		<ta:panelButtonBar align="right">
			<ta:button key="创建[S]" hotKey="S" onClick="if(Base.validateForm('editForm'))fnSaveJob()" isok="true" />
			<ta:button key="关闭[X]" hotKey="X" onClick="Base.closeWindow('jobWin')" />
		</ta:panelButtonBar>
	</ta:panel>
<script type="text/javascript">
	function fnSelectCbk(o) {
		if(o == "SimpleTrigger") {
			Base.hideObj("cronExpression");
			Base.showObj("repeatInterval,repeatCount");
		} else {
			Base.showObj("cronExpression");
			Base.hideObj("repeatCount,repeatInterval");
		}
	}
	function fnSaveJob() {
		 Base.submit("editForm", "<%=basePath%>scheduler/schedulerMgAction!addJob.do", null, null, false, function(o){
		 	if(o.success){
		 		Base.alert("任务创建成功！", "success");
		 		Base.closeWindow("jobWin");
			 } else {
		 		Base.alert("任务创建失败！", "error");
			 }
		 });
	}
	function opneCronWin() {
		Base.openWindow("win_1", "CronExpression生成器", "schedulerMgAction!cron.do", null, 600, 400, null, fnGetJobs);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>