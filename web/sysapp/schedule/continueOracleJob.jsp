<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<%@ include file="/ta/inc.jsp"%>
		<link rel="stylesheet" type="text/css" href="<%=basePath %>ta/resource/themes/base/extentcss/jquery.bubblepopup.v2.3.1.css" />
	<script src="<%=basePath%>ta/resource/external/jquery.bubblepopup.v2.3.1.min.js" type="text/javascript"></script>
	</head>
	<body class="no-scrollbar">
     <ta:pageloading />
   <ta:form   id="form2" fit="true" cssStyle="overflow:auto;" >
	<ta:fieldset  key="任务信息" id="fs1" cols="2"> 
	   <ta:text  id="jobid" key="任务编号"  required="true"  readOnly="true"/>
	   <ta:text  id="interval"  key="间隔时间" readOnly="true" span="2"/>
	  <ta:text  id="jobname"  key="任务名称"  readOnly="true" span="2"/>
	   <ta:textarea  id="what"  key="执行过程" readOnly="true" span="2" cssStyle="width:100%;height:100px"/>
	</ta:fieldset>
	<ta:buttonLayout align="center"> 
	  <ta:text  id="next_date"  key="开始执行时间" required="true" />
	  <ta:box><span   style="color:red;font-size:11px;padding-top: 7px;">
						 提示：请填入重新开始执行时间，填入后将按此时间继续执行当前任务
	  </span>
	  </ta:box>
	  <ta:submit  key="执行[S]" url="oracleJobAction!continueJob.do" hotKey="S" icon="icon-reload"  submitIds="form2" successCallBack="callBack" onSubmit="function(){return confirm('确认要执行此操作吗？');}"/>
	  <ta:button  key="重置[R]" hotKey="r" icon="icon-remove" onClick="Base.resetForm('form2');" />
	</ta:buttonLayout>
   </ta:form>
   </body>
</html>
<script>
function callBack(){
	parent.callBack();
}
$(document).ready(function () {
	$("body").taLayout();
});
</script>
<%@ include file="/ta/incfooter.jsp"%>
