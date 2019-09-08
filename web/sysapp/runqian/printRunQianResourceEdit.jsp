<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
  <head>
    <title>报表资源新增</title>
    <%@ include file="/ta/inc.jsp"%>
    <style>
    .textCss{
    margin-top:5px;
    margin-bottom:5px;
    }
    </style>
  </head>
  	<body style="margin:0px;padding:0px;overflow:hidden;">
  		<ta:form id="form1" fit="true" methord="post" enctype="multipart/form-data">
  			<ta:text id="flag" display="false" />
  			<ta:box id="add"  heightDiff="0" cssStyle="padding:10px">
  			   <ta:box id="pxsBox" cols="2">
				<ta:selectInput id="raqtype" key="报表类型" collection="RAQTYPE" filter="3,2" required="true" columnWidth="0.84" cssClass="textCss"/>
				<ta:box columnWidth="0.16">
				  <ta:checkbox key="是否公用" value="com" bpopTipMsg="勾选后该报表对所有机构均可见" id="yab109" labelStyle="line-height:30px" checked="true"/>
				</ta:box>
			   </ta:box>
  				<ta:text id="raqfilename" key="报表标识" display="false" readOnly="true" cssClass="textCss"/>
	  			<ta:text id="parentraqfilename" key="父报表标识" display="false" readOnly="true" cssClass="textCss"/>
				<ta:box id="file_id" cssClass="textCss">
					<ta:text id="theFile" key="资源文件" type="file" name="theFile" onChange="fnChangeFile(this)" required="true"/>
				</ta:box>
				<ta:box id="raqfileBox" cols="2" cssClass="textCss" cssStyle="display:none;" >
					<ta:text id="selectedfile" key="已选择报表文件" readOnly="true" columnWidth="0.75" />
					<ta:button id="reSelectBtn" icon="xui-icon-tableRefresh" columnWidth="0.25" onClick="fnReSelect()" key="重新选择" />
	  			</ta:box>
				<ta:text id="raqname" key="报表名称" cssClass="textCss" required="true" />
				<ta:number id="scaleexp" alignLeft="true" key="缩放比例" display="false" bpopTipMsg="如设置为1.5，即在界面中放大报表1.5倍"  precision="2"/>
				<ta:box cols="2" >
					<ta:number id="subrow" key="行(父报表位置)" display="false" disabled="true"/>
					<ta:number id="subcell" key="列(父报表位置)" display="false" disabled="true"/>
				</ta:box>
			</ta:box>
			<ta:buttonLayout align="center" cssStyle="background-color: #FAFAFA;border-top: 1px solid #CCC;margin-top:7px;height:45px">
					<ta:button id="saveBtn" key="保存" icon="icon-table_save" onClick="fnSaveAdd()"/>
					<ta:button id="updateBtn" display="false" key="保存" icon="icon-table_save" onClick="fnSaveUpdate()"/>
			</ta:buttonLayout>
  		</ta:form>
    </body>
 </html>
<script type="text/javascript">
	<%--重新选择报表--%>
	function fnReSelect(){
		Base.showObj("file_id");
		Base.hideObj("raqfileBox");
	}
	<%--添加自定义参数--%>
	function fnAddRow(submitIds){
		Base.addGridRow(submitIds,{});
		var obj = Base.getObj(submitIds);
		obj.gotoCell(0,2);
	}
	<%--删除参数--%>
	function fnDelRow(data){
		Base.deleteGridRow('paramGrid',data.row);
	}
	<%--文件选择--%>
	function fnChangeFile(data){
		if(data.value != undefined && data.value != ""){
			var raqname = $(data).val();
			raqname = raqname.substring(raqname.lastIndexOf('\\')+1,raqname.indexOf('.raq'));
			Base.setValue('raqname',raqname);
		}
	}
	
	<%--保存参数--%>
	function fnSaveRows(submitIds){
		//Base.refreshGrid(submitIds);
		var rows=Base.getGridData(submitIds);
		var obj = Base.getObj(submitIds);
		var newJson = [];
		for(var i = 0 ; i < rows.length ; i++){
			var row = rows[i];
			if(!(row.subcell) || row.subrow == ''){
				return Base.alert('请输入参数名！','warn'),false;
			}
			if(!(row.subrow) || row.subcell == ''){
				return Base.alert('请输入参数值！','warn'),false;
			}
			newJson.push(row);
		}
		Base._setGridData(submitIds, newJson);
	}
	<%--保存按钮事件--%>
	function fnSaveAdd(){
		var raqpid = Base.getValue("parentraqfilename");
		var raqtype = Base.getValue("raqtype");
		var yab109 = Base.getValue("yab109");
		if(raqtype=="2"&&raqpid == ""){
			return Base.alert("没有选择父报表，请选择父报表再点击[添加子报表]！","warn"),false;
		}
		var theFile = $("#theFile").val();
		if(Base.validateForm("form1")){
			var param={};
			param["dto['filename']"]=theFile;
			if(null != yab109){
			  param["dto['yab109']"]=yab109;
			}
			else{
			  param["dto['yab109']"]="";
			}
			Base.submitForm('form1', null, false, "runQianReportFileMgAction!addBB.do",param);
		}
	}
	
	<%--编辑按钮事件--%>
	function fnSaveUpdate(){
		var theFile = $("#theFile").val();
		var selectedfile = Base.getValue("selectedfile");
		if(Base.validateForm("form1")){
			var param={};
			param["dto['filename']"]=theFile;
			param["dto['selectedfile']"]=selectedfile;
			Base.submitForm('form1', null, false, "runQianReportFileMgAction!editBB.do",param);
		}
	}
	
    $(document).ready(function () {
		$("body").taLayout();
		$("div.textCss").css({"margin-bottom":"10px","margin-top":"10px"});
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>