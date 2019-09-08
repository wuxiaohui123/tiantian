<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
  <head>
    <title>新增自由报表</title>
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
				<ta:selectInput id="raqtype" key="报表类型" collection="RAQTYPE" value="3" readOnly="true" cssClass="textCss" required="true" columnWidth="0.84"/>
				<ta:box columnWidth="0.16">
				  <ta:checkbox key="是否公用" value="com" bpopTipMsg="勾选后该报表对所有机构均可见" id="yab109" labelStyle="line-height:30px" checked="true"/>
				</ta:box>
			   </ta:box>
  				<ta:text id="raqid" key="报表ID" display="false" readOnly="true" cssClass="textCss"/>
	  			<ta:text id="raqpid" key="父报表ID" display="false" readOnly="true" cssClass="textCss"/>
				<ta:box id="file_id" cssClass="textCss">
					<ta:text id="theFile" key="资源文件" type="file" name="theFile"  required="true"/>
				</ta:box>
				<ta:box id="raqfileBox" cols="2" cssClass="textCss" cssStyle="display:none;" >
					<ta:text id="selectedfile" key="已选择报表文件" readOnly="true" columnWidth="0.75" />
					<ta:button id="reSelectBtn" icon="xui-icon-tableRefresh" columnWidth="0.25" onClick="fnReSelect()" key="重新选择" />
	  			</ta:box>
				<ta:text id="raqname" key="报表名称" required="true" />
				<ta:text id="raqdatasource" key="数据源"  required="true" bpopTipMsg="需要在reportConfig.xml里配置"/>
<%--					<ta:number id="scaleexp" key="缩放比例" precision="2" alignLeft="true" bpopTipMsg="如设置为1.5，即在界面中放大报表1.5倍" bpopTipPosition="left"/>--%>
				<ta:panel id="paramPanel" key="已配置参数列表"  expanded="false" height="187px" cssStyle="margin-top:5px"  bodyStyle="margin:0px">
					<ta:datagrid id="paramGrid"  enableColumnMove="false" snWidth="30" forceFitColumns="true"  haveSn="true"  fit="true" selectType="checked">
						<ta:datagridItem click="fnDelRow" icon="icon-table_delete" key="删除" width="45" align="center"/>
						<ta:datagridItem id="paramKey" key="参数名" width="170">
							<ta:datagridEditor type="text"/>
						</ta:datagridItem>
						<ta:datagridItem id="paramValue" key="参数值" width="200">
							<ta:datagridEditor type="text"/>
						</ta:datagridItem>
						<ta:dataGridToolPaging url="" showExcel="false" showButton="false"  showDetails="false" showCount="false" >
			                <ta:gridToolButton  id="addBtn" key="新增" icon="icon-table_add" onClick="fnAddRow('paramGrid')"/>
			                <ta:gridToolButton id="qrBtn" key="确认" icon="icon-table_save" onClick="fnSaveRows('paramGrid')"/>
			            </ta:dataGridToolPaging>
					</ta:datagrid>
				</ta:panel>
				<ta:box cssStyle="visibility:hidden;height:1px" ></ta:box>
			</ta:box>
			<ta:buttonLayout align="center"  cssStyle="background-color: #FAFAFA;border-top: 1px solid #CCC;height:35px">
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
	
	var saveParamFlag = false;
	<%--保存参数--%>
	function fnSaveRows(submitIds){
		//Base.refreshGrid(submitIds);
		var rows=Base.getGridData(submitIds);
		var obj = Base.getObj(submitIds);
		var newJson = [];
		for(var i = 0 ; i < rows.length ; i++){
			var row = rows[i];
			if(!(row.paramValue) || row.paramKey == ''){
				saveParamFlag = false;
				return Base.alert('请输入参数名！','warn'),false;
			}
			if(!(row.paramKey) || row.paramValue == ''){
				saveParamFlag = false;
				return Base.alert('请输入参数值！','warn'),false;
			}
			newJson.push(row);
		}
		saveParamFlag = true;
		Base._setGridData(submitIds, newJson);
	}
	<%--保存按钮事件--%>
	function fnSaveAdd(){
		var raqpid = Base.getValue("raqpid");
		var raqtype = Base.getValue("raqtype");
		var yab109 = Base.getValue("yab109");
		if(raqtype=="2"&&raqpid == ""){
			return Base.alert("没有选择父报表，请选择父报表再点击[添加子报表]！","warn"),false;
		}
		var param={};
		var theFile = $("#theFile").val();
		var paramDatas = Base.getGridData("paramGrid");
		if(paramDatas && paramDatas.length > 0){
			if(!saveParamFlag){
				return Base.alert('请确认参数列表！','warn'),false;
			}
			param["dto['raqparam']"] = Ta.util.obj2string(paramDatas);
		}
		if(Base.validateForm("form1")){
			param["dto['filename']"]=theFile;
			if(null != yab109){
			  param["dto['yab109']"]=yab109;
			}
			else{
			  param["dto['yab109']"]="";
			}
			Base.submitForm('form1', null, false, "runQianReportFileMgAction!addZyBB.do",param);
		}
	}
	
	<%--编辑按钮事件--%>
	function fnSaveUpdate(){
		var param={};
		var theFile = $("#theFile").val();
		var paramDatas = Base.getGridData("paramGrid");
		if(paramDatas && paramDatas.length > 0){
			if(!saveParamFlag){
				return Base.alert('请确认参数列表！','warn'),false;
			}
			param["dto['raqparam']"] = Ta.util.obj2string(paramDatas);
		}
		if(Base.validateForm("form1")){
		    if(null != yab109){
			  param["dto['yab109']"]=yab109;
			}
			else{
			  param["dto['yab109']"]="";
			}
			param["dto['filename']"]=theFile;
			Base.submitForm('form1', null, false, "runQianReportFileMgAction!editZyBB.do",param);
		}
	}
	
    $(document).ready(function () {
		$("body").taLayout();
		$("div.textCss").css({"margin-bottom":"10px","margin-top":"10px"});
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>