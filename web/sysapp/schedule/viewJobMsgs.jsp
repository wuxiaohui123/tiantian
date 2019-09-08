<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar">
	 <ta:pageloading />
	 <ta:form id="form2" fit="true" cssStyle="overflow:auto;">
      <ta:text id="group" display="false"/>
	  <ta:text id="name" display="false"/>
	  <ta:text id="jobgroup" display="false"/>
	
	  <ta:panel key="成功信息" cssStyle="font-size:13px;" height="200px" hasBorder="true" expanded="false">
		<ta:datagrid  id="jobSuccessMsgList" haveSn="true" selectType="checkbox" forceFitColumns="true" fit="true">
		    <ta:datagridItem key="执行开始时间" id="execstarttime" />
			<ta:datagridItem key="执行结束时间" id="execendtime" />
			<ta:datagridItem key="成功消息" id="successmsg" />
			 <ta:dataGridToolPaging pageSize="6" showCount="true"  url="oracleJobAction!queryDetailJobMsgs.do?dto.flag=1" submitIds="form2">
			    <ta:gridToolButton key="导出全部记录为Excel"  id="excel_1"   icon="icon-save"   onClick="exportAllExcel_1();" />
				<ta:gridToolButton key="导出选定行为Excel"  id="sel_excel_1"  icon="icon-save"  onClick="exportSelectRowsExcel('jobSuccessMsgList', 'oracleJobAction!exportSelectRowsToExcel.do');" />
			 </ta:dataGridToolPaging>
		</ta:datagrid>
	  </ta:panel> 
	  
	  <ta:panel key="出错信息" cssStyle="font-size:13px;" height="200px" hasBorder="true" expanded="false"> 
		<ta:datagrid  id="jobErrorMsgList" haveSn="true" selectType="checkbox" forceFitColumns="true"  fit="true">
		    <ta:datagridItem key="执行开始时间" id="execstarttime" />
			<ta:datagridItem key="执行结束时间" id="execendtime"   />
			<ta:datagridItem key="出错消息" id="errormsg"   />	
			 <ta:dataGridToolPaging  pageSize="6" showCount="true"  url="oracleJobAction!queryDetailJobMsgs.do?dto.flag=2"  submitIds="form2">
			    <ta:gridToolButton key="导出全部记录为Excel"  id="excel_2"   icon="icon-save" onClick="exportAllExcel_2();" />
				<ta:gridToolButton key="导出选定行为Excel"  id="sel_excel_2"  icon="icon-save" onClick="exportSelectRowsExcel('jobErrorMsgList', 'oracleJobAction!exportSelectRowsToExcel.do');" />
			 </ta:dataGridToolPaging>
		</ta:datagrid>
	  </ta:panel> 
	
	</ta:form>
</body>
</html>
<script type="text/javascript">
//导出所有的查询记录为excel
 function exportAllExcel_1(){
	var datagrid=Base.getGridData("jobSuccessMsgList");
	if(datagrid.length==0){
		return Base.alert('没有日志！','warn'), false;
		}
	var name=$("#name").val();
	location.href="oracleJobAction!exportExcel.do?dto.flag=1&dto.name="+name;
 }
 function exportAllExcel_2(){
	var datagrid=Base.getGridData("jobErrorMsgList");
	if(datagrid.length==0){
		return Base.alert('没有日志！','warn'), false;
		}
	var name=$("#name").val();
	location.href="oracleJobAction!exportExcel.do?dto.flag=2&dto.name="+name;
 }
//导出选中行数据为excel
 function	exportSelectRowsExcel(submitIds,url)
 {
   var  ids=Base.getGridSelectedRows(submitIds) ;
     if(ids== ""){
		return Base.alert('请选择需要导出的行数据','warn'), false;
	 }
	 else
	 {
	     toQuery(submitIds,url);
	 }
 }   
 //通用查询方法
   function  toQuery(submitIds,url)
   {
	   var flag="";
	   if(submitIds=="jobSuccessMsgList"){
           flag="1"
	   }
	   if(submitIds=="jobErrorMsgList"){
		   flag="2"
	   }
       submitIds = submitIds?submitIds:"";
	   var aids = submitIds.split(',');
	 	//根据ids拼接传递的条件字符串
	 	var  queryStr="";
		var  datagridids = [];
		if(aids){
			for(var i=0;i<aids.length;i++){
				if(aids[i]==null || aids[i]=='')continue;
				var obj = Base.getObj(aids[i]);
				var $obj = $(obj);
				if(obj && obj.cmptype!='datagrid' && ($obj.hasClass('panel') || $obj.hasClass('grid') || obj.cmptype=='flexbox' ||
				  (obj.tagName &&　(obj.tagName=='FORM' ||obj.tagName=='FIELDSET' || obj.tagName=='DIV'|| obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || obj.tagName=='SELECT') ) )){
					if(obj.cmptype=='flexbox')obj = $("#"+aids[i]);//下拉框
				  	
					for(var j=0;j<aids.length;j++){//对ids进行校验，不能父子嵌套
						if(aids[j]==null || aids[j]=='')continue;
						var obj2 = Base.getObj(aids[j]);
						if(obj2.cmptype=='flexbox')obj2 = $("#"+aids[j]);
						if(i != j && obj2.cmptype!='datagrid'){//找到其他对象
							
							if($(obj).has($(obj2)).length>0){
								alert(aids[j]+"对象在"+aids[i]+"对象里面，指定提交的元素id不能有包含与被包含关系");
								return false;
							}
							if($(obj2).has($(obj)).length>0){
								alert(aids[i]+"对象在"+aids[j]+"对象里面，指定提交的元素id不能有包含与被包含关系");
								return false;
							}
						}
					}
					if(queryStr=="")
						queryStr += $("#"+aids[i]).taserialize();
					else
						queryStr += "&"+$("#"+aids[i]).taserialize();
				}
				else if(obj.cmptype=='datagrid'){
					datagridids.push(new String(aids[i]));
					if(queryStr=="")
						queryStr += $("#"+aids[i]).taserialize();
					else
						queryStr += "&"+$("#"+aids[i]).taserialize();	
					aids[i]=null;//.splice(i,1);//删除当前id
				}
				else{
					alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
					return false;
				}
			}
		}
	    //传递表格 selected 数据
		for(var i=0;i<datagridids.length;i++){
			var p = {};
			p["gridInfo['"+datagridids[i]+"_selected']"] = Ta.util.obj2string(Base.getGridSelectedRows(datagridids[i]));
			if(queryStr==""){
				queryStr = jQuery.param(p);
			}else{
				queryStr += "&"+jQuery.param(p);
			}
		}
		//访问Action并提交参数
		 location.href=url+"?dto.flag="+flag+"&"+queryStr;
		 
   }
$(document).ready(function () {
	$("body").taLayout();
	Base.submit("form2", "oracleJobAction!queryDetailJobMsgs.do?dto.flag=0",false,false,false);
});
</script>
<%@ include file="/ta/incfooter.jsp"%>
