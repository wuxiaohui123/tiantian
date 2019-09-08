<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:10px;margin:0px">
     <ta:pageloading />
     <ta:form id="form1" fit="true" cssStyle="overflow:none;" >
	  <ta:panel key="OracleJob列表" fit="true" id="OracleJob" hasBorder="true" expanded="false" withToolBar="true">
	   <ta:panelToolBar align="left">
		<ta:submit key="刷新[R]" hotKey="R"  id="jobList" asToolBarItem="true" icon="icon-search" url="oracleJobAction!queryJob.do" submitIds="form1"/>
		<ta:button key="新建任务[N]"  hotKey="N"   asToolBarItem="true" icon="icon-adduser" onClick="toCreateJob();"/>
		<ta:submit key="删除选择[D]" hotKey="D"   asToolBarItem="true" icon="icon-cancel" submitIds="oracleJobList" onSubmit="deletevalidation" url="oracleJobAction!deleteJobs.do" successCallBack="callBack"/>
		<ta:submit key="暂停选择[P]" hotKey="P"   asToolBarItem="true" icon="icon-remove" submitIds="oracleJobList" onSubmit="pausevalidation" url="oracleJobAction!pauseJobs.do" successCallBack="callBack"/>
	   </ta:panelToolBar>
	   <ta:datagrid id="oracleJobList" forceFitColumns="true"  haveSn="true" selectType="checkbox"  fit="true" columnFilter="true"  >
	   	<ta:datagridItem key="过程信息" id="viewProcInfo" align="center" click="viewJobMsgs"  width="80" icon="icon-search"/>
		<ta:datagridItem key="编辑任务" id="editTask" align="center" click="toEditJob" width="80" icon="icon-setting"/>
		<ta:datagridItem key="继续执行" id="continueTask" align="center" click="toContinueJob" width="80" icon="icon-reload"/>
		<ta:datagridItem key="任务编号" id="myjobid" align="center" width="80" sortable="true"/>
		<ta:datagridItem key="OracleJob编号" id="jobid" align="center" width="120" sortable="true"/>
		<ta:datagridItem key="任务名称" id="jobname" align="center" width="150" sortable="true"/>
		<ta:datagridItem key="执行过程" id="what" align="center" width="150" sortable="true"/>
		<ta:datagridItem key="运行情况" id="broken" align="center" width="80" sortable="true" formatter="fnformatter"/>
		<ta:datagridItem key="执行人员" id="userid" align="center" width="100" sortable="true" collection="YAE092"/>
		<ta:datagridItem key="开始定时时间" id="starttime" align="center" sortable="true" width="150"/>
		<ta:datagridItem key="间隔时间" id="interval" align="center" sortable="true" width="150" formatter="format"/>
		<ta:datagridItem key="上次执行时间" id="last_date" align="center" sortable="true" width="150"/>
		<ta:datagridItem key="下次执行时间" id="next_date" align="center" sortable="true" width="150"/>
	   	<ta:gridToolPaging pageSize="13" showCount="true" url="oracleJobAction!queryJob.do" submitIds="form1"/>
	  </ta:datagrid>
	 </ta:panel>
	</ta:form>
   </body>
</html>
<script>
//渲染回调函数
 function format(row, cell, value, columnDef, dataContext){
	if(value == "null" || value == null){
		return "无";
	}else{
		return value;
	}
 }
 function fnformatter(row, cell, value, columnDef, dataContext){
		if(value=="运行中"){
		    return "<div style='color:green;font-weight:bolder;'>" + value + "</div>";
		}else if(value=="暂停"){
		    return "<div style='color:orange;font-weight:bolder;'>" + value + "</div>";
		}else if(value=="已删除"){
		    return "<div style='color:red;font-weight:bolder;'>" + value + "</div>";
		}	
	}
	
 function deletevalidation(){
	var jsonData=Base.getGridSelectedRows('oracleJobList');
	if((jsonData==""||jsonData==null)){
		return Base.alert("请在需要删除的任务前打√！", "warn"), false;
		}
	for (var i = 0; i < jsonData.length; i++) {
		if (jsonData[i].broken != '暂停') {
			return Base.alert("暂停的任务才能删除！", "warn"), false; 
		}
	}
	return confirm('您确定要删除吗？');
 }
 function pausevalidation(){
		var jsonData=Base.getGridSelectedRows('oracleJobList');
		if((jsonData==""||jsonData==null)){
		return Base.alert("请在需要暂停的任务前打√！", "warn"), false;
		}
		for (var i = 0; i < jsonData.length; i++) {
			if (jsonData[i].broken != '运行中') {
				return Base.alert("运行中的任务才能暂停！", "warn"), false; 
			}
		}
	return confirm('您确定要暂停吗？');
 }
 function viewJobMsgs(o){
	 var broken=o.broken;
		if(broken=='已删除'){
			return Base.alert("已删除的任务不能查看过程信息！", "warn"), false; 
			}
	var name=o.jobid;
	var url="oracleJobAction!showDetailJob.do?dto.jobgroup=DEFAULT&dto.name="+name;
	Base.openWindow('JobMsgsWin',"浏览详细信息",url,null,600,370,null,null,true);
 }
 function toContinueJob(o){
	var broken=o.broken;
	if(broken!='暂停'){
		return Base.alert("暂停的任务才能够继续执行！", "warn"), false; 
		}
	var url="oracleJobAction!toContinueJob.do";
	var jobid=o.jobid;
    var jobname=o.jobname;
	var what=o.what;
	var next_date=o.starttime;
	var interval=o.interval;
	url+="?dto['jobid']="+jobid+"&dto['jobname']="+jobname+"&dto['what']="+URLencode(what)+"&dto['next_date']="+next_date+"&dto['interval']="+URLencode(interval);
	Base.openWindow('continueJobWin',"继续执行OracleJob任务",url,null,570,380,null,null,true);
 }
 function toCreateJob(){
	Base.openWindow('createJobWin',"创建OracleJob任务","oracleJobAction!toCreateJob.do",null,570,370,null,null,true);
 }
 function toEditJob(o){
	var broken=o.broken;
		if(broken=='已删除'){
			return Base.alert("已删除的任务不能编辑！", "warn"), false; 
			}
	var url="oracleJobAction!toEditJob.do";
	var jobid=o.jobid;
	var jobname=o.jobname;
	var what=o.what;
	var next_date=o.starttime;
	var interval=o.interval;
	url+="?dto['jobid']="+jobid+"&dto['jobname']="+jobname+"&dto['what']="+URLencode(what)+"&dto['next_date']="+next_date+"&dto['interval']="+URLencode(interval);
	Base.openWindow('editJobWin',"编辑OracleJob任务",url,null,570,370,null,null,true);
 }
 function callBack(){
	Base.submit("form1", "oracleJobAction!queryJob.do",false,false,false);
 }
 /***
  * 对 特殊字符进行重新编码
  * **/
 function URLencode(sStr){
     return sStr.replace(/\+/g, '%2B');
   }
$(document).ready(function () {
	$("body").taLayout();
	Base.submit("form1", "oracleJobAction!queryJob.do",false,false,false);
});
</script>
<%@ include file="/ta/incfooter.jsp"%>
