<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
	Object typeString = request.getAttribute("submitType");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
	<head>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding-top:0px;">
			<ta:buttonLayout align="left">
				<ta:button key="新增任务[N]" hotKey="N" onClick="addJob()" isok="true"/>
				<ta:button key="刷新任务列表[R]"  hotKey="R" onClick="fnGetJobs();" />
			</ta:buttonLayout>
			<ta:panel key="JOB列表" id="panel" fit="true">
				<ta:datagrid id="jobList" fit="true" selectType="checkbox">
					<ta:datagridItem key="Job名称" id="jobname" width="100px"/>
					<ta:datagridItem key="指定的应用地址" id="jobgroup" width="140px" showDetailed="true" sortable="true"/>
					<ta:datagridItem key="Job描述" id="jobdesc" width="120px" showDetailed="true"/>
					<%-- <ta:datagridItem key="Job类" id="jobclass" width="150px" showDetailed="true"/>--%>
					<ta:datagridItem key="服务ID" id="trigroup" width="100px" showDetailed="true"/>
					<ta:datagridItem key="状态" id="state" width="70px" sortable="true"/>
					<ta:datagridItem key="开始时间" id="st" width="145px" sortable="true"/>
					<ta:datagridItem key="结束时间" id="et" width="145px" sortable="true"/>
					<ta:datagridItem key="上次运行时间" id="pt" width="145px" sortable="true"/>
					<ta:datagridItem key="下次运行时间" id="nt" width="145px" sortable="true"/>
					<ta:datagridItem key="暂停" icon="icon-remove" click="pauseJob" width="38px"/>
					<ta:datagridItem key="启动" icon="icon-ok" click="resumeJob"  width="38px"/>
					<ta:datagridItem key="删除" icon="icon-cancel" click="stopJob" width="38px"/>
				</ta:datagrid>
			</ta:panel>
	</body>
</html>

<script type="text/javascript">
<!--
	$(document).ready(function () {
	    $('body').taLayout();
	});
	function fnGetJobs(){
		 Base.submit(null, 'schedulerMgAction!getJobs.do', null, false, false);
	}
	function addJob(){
		Base.openWindow("jobWin", "新增任务", "schedulerMgAction!toAdd.do", null, 400, 430);
	}
	function stopJob(o){
		if(confirm("确定删除该任务？删除将不可恢复。\n\n[确定]删除，[取消]不删除。"))
			 Base.submit(null, 'schedulerMgAction!stopJob.do', {'name':o.triname,'group':o.trigroup});
	}
	function pauseJob(o){
		if (o.state != "WAITING" && o.state != "ACQUIRED")
			return alert("任务未处于待命或运行状态中，不能执行暂停操作！"),false;
		Base.submit(null, 'schedulerMgAction!pauseJob.do', {'name':o.triname,'group':o.trigroup});
	}
	function resumeJob(o){
		if (o.state == "WAITING" || o.state == "ACQUIRED")
			return alert("任务已处于待命或运行状态，不能执行启动操作！"),false;
		Base.submit(null, 'schedulerMgAction!resumeJob.do', {'name':o.triname,'group':o.trigroup});
	}
//-->
</script>
<%@ include file="/ta/incfooter.jsp"%>