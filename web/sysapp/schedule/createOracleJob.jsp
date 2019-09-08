<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body>
		<ta:pageloading />
<ta:form id="form2" fit="true">
     <ta:text id="flag" display="false"/>
	<ta:fieldset  key="任务信息(明细见下方说明)" cols="2">
	   <ta:text  id="myjobid"   key="任务编号"       display="none" />
	   <ta:text  id="jobid"     key="OracleJob编号" display="none" />
	   <ta:text  id="jobname"   key="任务名称"       required="true" span="2"/>
	   <ta:text  id="next_date" key="开始执行时间"    required="true" span="2" maxLength="19"/>
	   <ta:text  id="interval"  key="间隔时间"   required="true" span="2"/>
	   <ta:textarea  id="what"  key="执行过程"  required="true" span="2"  cssStyle="height:100px"/>
	</ta:fieldset >
	<ta:buttonLayout align="center" >
		<ta:submit id="create"  key="创建[C]" url="oracleJobAction!createJob.do" hotKey="C" icon="icon-adduser"  submitIds="form2" successCallBack="callBack" display="false" onSubmit="function(){return confirm('确认要执行此操作吗？');}"/>
		<ta:submit id="edit"    key="更改[E]" url="oracleJobAction!editJob.do" hotKey="E" icon="icon-setting"  submitIds="form2" successCallBack="callBack" display="false" onSubmit="function(){return confirm('确认要执行此操作吗？');}"/>
		<ta:button id="resetbt" key="重置[R]" hotKey="r" icon="icon-remove" onClick="Base.resetForm('form2');" />
	</ta:buttonLayout>
	<ta:panel key="说明" hasBorder="true" cssStyle="font-size:12px" expanded="false">
		 <p>1、执行过程：可以是sql语句，也可以是存储过程。后面要带";"号。多个存储过程可以用";"隔开，最后一个一定要带上";"。</p>
		 <p> &nbsp&nbsp⑴、过程变量拼成字符串的时候要加 ' 符号，如果是数字类型的就不要加 ' 符号。</p>
		 <p> &nbsp&nbsp⑵、时间类型的就拼成to_date('"+time+"','yyyy-MM-dd HH:mm:ss')，这里特别要注意的时候必须是已经格式化成yyyy-MM-dd HH:mm:ss类型的时间串</p>
		 <p> &nbsp&nbsp⑶、日期类型的就拼成to_date('"+date+"','yyyy-MM-dd')，这里特别要注意的是：date必须是已经格式化成yyyy-MM-dd类型的日期串</p>
		 <p>2、开始执行时间：可以是例如：2012-03-30 10:30:00 这种格式，也可以是oracle的一个时间表达式，比如：trunc(sysdate, 'HH24')+1/24 代表系统时间1点钟开始执行；sysdate+1/1440代表系统当前时间+1分钟执行</p>
		 <p>3、间隔时间：是oracle的一个时间表达式的字符串，例如：</p>
		 <p> &nbsp&nbsp⑴、每隔一分钟执行一次: sysdate+1/1440</p>
		 <p> &nbsp&nbsp⑵、每天早上6点执行一次(每天执行一次): TRUNC(sysdate)+1+6/24</p>
		 <p> &nbsp&nbsp⑶、每周一凌晨一点执行一次(每周执行一次): </p>
		 <p> &nbsp&nbsp&nbsp&nbsp TRUNC(next_day(sysdate,'星期一'))+1/24</p>
		 <p> &nbsp&nbsp⑷、每月1日凌晨一点执行一次(每月执行一次): </p>
		 <p> &nbsp&nbsp&nbsp&nbsp TRUNC(LAST_DAY(sysdate))+1+1/24</p>
		 <p> &nbsp&nbsp⑸、每年1月1日和7月1日执行一次(半年执行一次): </p>
		 <p> &nbsp&nbsp&nbsp&nbsp ADD_MONTHS(TRUNC(sysdate,'YYYY'),6)+1/24</p>
		 <p> &nbsp&nbsp⑹、每年1月1日执行一次(每年执行一次): </p>
		 <p> &nbsp&nbsp&nbsp&nbsp ADD_MONTHS(TRUNC(sysdate,'YYYY'),12)+1/24</p>
	</ta:panel>
	<ta:panel key="下面是任务执行策略的几个例子：" padding="3">
	<ta:fieldset  key="每周执行一次的job"  cssStyle="font-size:12px">
	   <p>（周一早上10：00）,trunc(sysdate, 'd')是将时间截断到本周周日（本周第一天）早上0点</p>	
       <p>&nbsp&nbsp开始执行时间 ：trunc(sysdate, 'd') + 8 + 10/24</p>
	   <p>&nbsp&nbsp间隔时间：trunc(sysdate, 'd') + 8 + 10/24</p>
	</ta:fieldset>
	<ta:fieldset  key="半点执行的job" cssStyle="font-size:12px">	
       <p>&nbsp&nbsp开始执行时间 ：trunc(sysdate, 'HH24') + FLOOR(TO_NUMBER(TO_CHAR(sysdate, 'MI'))/30)/48+1/48</p>
	   <p>&nbsp&nbsp间隔时间：trunc(sysdate, ''HH24'') + 3/48</p>
	</ta:fieldset>
	</ta:panel>
</ta:form>
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	     if($("#flag").val()=="1"){
				$("#create").css("display","inline");
// 				Base.showObj("myjobid");
		}
		if($("#flag").val()=="2"){
				$("#edit").css("display","inline");
				Base.showObj("jobid");
				Base.setReadOnly("jobid");
		}
});

function callBack(){
	parent.callBack();
}
</script>
<%@ include file="/ta/incfooter.jsp"%>