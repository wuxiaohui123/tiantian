<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<html>
	<head>
	  <%@ include file="/ta/inc.jsp"%>
	</head>
  <body class="no-scrollbar" style="padding:10px 10px 0px 10px;margin:0px;">
  	<ta:form id="form1" fit="true" methord="post" enctype="multipart/form-data">
		<ta:fieldset id="queryfield"  cols="3" key="查询报表">
			<ta:text id="raqfilename" key="报表标识" labelWidth="70" cssStyle="width:195px" textHelp="asdf"/>
			<ta:selectInput id="raqtype"  key="报表类型" labelWidth="70" cssStyle="width:195px" collection="RAQTYPE" />
			<div style="padding-top:3px;">
				<ta:submit key="查询" icon="xui-icon-query"  submitIds="raqfilename,raqtype" url="runQianReportFileMgAction!queryBB.do"/>
				<ta:button key="重置" icon="xui-icon-reset"  onClick="Base.resetForm('form1');Base.clearGridData('rqbbGrid');Base.clearData('form1');"/>
			</div>
		</ta:fieldset>
		<ta:panel key="报表"  withToolBar="true" id="p1" fit="true" cssStyle="margin-top:10px">
			<ta:panelToolBar>
				<ta:button asToolBarItem="true" key="新增报表" icon="xui-icon-tableAdd" onClick="openBBadd()"/>
				<ta:toolbarItemSeperator />
				<ta:button asToolBarItem="true" key="删除报表" icon="xui-icon-tableDelete" onClick="fnDelRow()"/>
			</ta:panelToolBar>
			<ta:datagrid id="rqbbGrid" snWidth="30" columnFilter="true" selectType="checkbox" haveSn="true" fit="true" forceFitColumns="true"> 
				<ta:datagridItem id="editor" key="编辑报表" align="center" click="fnEditRow" width="60" icon="xui-icon-tableEdit"/>
				<ta:datagridItem id="downloadBtn" width="60" align="center" key="点击下载" icon="icon-download" click="fnDownload"/>
				<ta:datagridItem id="addSubBtn" width="60" formatter="fnBtnView" align="center" key="添加子表" icon="xui-icon-tableRowInsert" click="openZBBadd"/>
				<ta:datagridItem id="raqfilename" key="报表标识" sortable="true" align="center" showDetailed="true"  width="180"/>
				<ta:datagridItem id="raqname" key="报表名称" sortable="true"  align="center" showDetailed="true" width="180"/>
				<ta:datagridItem id="raqtype" key="报表类型" sortable="true"  align="center" dataAlign="center" width="80" collection="RAQTYPE"/>
				<ta:datagridItem id="uploador" key="上传人" sortable="true"  align="center" collection="YAE092"  width="80"/>
				<ta:datagridItem id="uploadtime" key="上传时间" sortable="true"  align="center" showDetailed="true" width="150" dataAlign="center"/>
				<ta:dataGridToolPaging url="runQianReportFileMgAction!queryBB.do"  showExcel="true" ></ta:dataGridToolPaging>
			</ta:datagrid>
		</ta:panel> 
  	</ta:form>
  </body>
</html>

<script type="text/javascript">
	$(document).ready(function () {
	    $('body').taLayout();
	    Base.submit(null,"runQianReportFileMgAction!queryBB.do");
	});
	function print(){
		var args = [];
		args[0]= "AAC001="+"123";
		args[1]= "YAB139="+"6521";
		Base.print(["1000001579"],null);
	}
	<%--添加子报表显示事件--%>
	function fnBtnView(row, cell, value, columnDef, dataContext){
		var type = dataContext.raqtype;
		if(type == "1"){
			return '<center><div title="单击新增子报表" class="xui-icon-tableRowInsert" style="cursor:pointer;height:22px;width:16px;margin-top:3px"></div></center>';
		}else{
			return '　';
		}
	}
	
	
	<%--下载报表--%>
	function fnDownload(o){
		Base.confirm("确认下载吗?",function(btn, callback,options) {
			if (btn) {	
				var url = "<%=basePath%>runqian/runQianReportFileMgAction!downloadBB.do?dto['raqfilename']="+ o.raqfilename;
				window.location.href = url;
			}
		});
	}
	
	<%-- 编辑报表--%>
	function fnEditRow(o) {
		var raqtype = o.raqtype;
		var param ={};
		param["dto['raqname']"] = o.raqname;
		param["dto['scaleexp']"] = o.scaleexp;
		param["dto['selectedfile']"] = o.raqfilename;
		param["dto['yab109']"] = o.yab109;
		if(raqtype == '2'){
			param["dto['subcell']"] = o.subcell;
			param["dto['subrow']"] = o.subrow;
			Base.openWindow("addWin", "编辑[<span style='color:green'>"+o.raqname+"</span>]子报表", "runQianReportFileMgAction!toViewAdd.do?dto.flag=u1&dto.raqfilename="+o.raqfilename+"&dto.raqpid="+o.raqpid+"&dto.raqtype="+o.raqtype, param , 500 , 258, null, function(){
				queryBB();
			}, true);
		}else if(raqtype == '3'){
			param["dto['scaleexp']"] = o.scaleexp;
			Base.openWindow("addWin", "编辑[<span style='color:green'>"+o.raqname+"</span>]自由报表", "runQianReportFileMgAction!toViewZyAdd.do?dto.flag=u&dto.raqfilename="+o.raqfilename+"&dto.raqtype="+o.raqtype, param , 500 , 470, null, function(){
				queryBB();
			}, true);
		}else{
			Base.openWindow("addWin", "编辑[<span style='color:green'>"+o.raqname+"</span>]报表", "runQianReportFileMgAction!toViewAdd.do?dto.flag=u2&dto.raqfilename="+o.raqfilename+"&dto.raqtype="+o.raqtype, param , 500 , 258, null, function(){
				queryBB();
			}, true);
		}
	}
<%--	新增报表--%>
	function openBBadd() {
		Base.openWindow("addWin", "新增报表", "runQianReportFileMgAction!toViewAdd.do?dto.flag=a2", null , 500 , 215, null, function(){
			queryBB();
		}, true);
	}
<%--	新增子报表--%>
	function openZBBadd(o) {
		var raqtype = o.raqtype;
		if(raqtype != "1"){
			return false;
		}
		var param = {};
		param["dto['raqfilename']"] = o.raqfilename;
		param["dto['raqname']"] = encodeURI(o.raqname);
		param["dto['flag']"] = "a1";
		Base.openWindow("addZBBWin", "新增[<span style='color:green'>"+o.raqname+"</span>]子报表", "runQianReportFileMgAction!toViewAdd.do", param , 500 , 250, null, function(){
			queryBB();
		}, true);
	}
<%--	查询报表--%>
	function queryBB() {
		Base.submit("raqfilename,raqtype","runQianReportFileMgAction!queryBB.do");
	}
<%--	删除报表--%>
	function fnDelRow(){
		var o = Base.getGridSelectedRows("rqbbGrid");
		if (!(o && o.length > 0)) return Base.alert("请先选择要删除的报表!", "warn"), false;  
 		Base.confirm("确认删除吗?",function(btn, callback) {
			if (btn) {	
				Base.submit("rqbbGrid","runQianReportFileMgAction!deleteBB.do",null,null,null,function(){
					queryBB();
				});
			}
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>