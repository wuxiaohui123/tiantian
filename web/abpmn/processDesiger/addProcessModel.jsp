<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>添加流程模型</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
	<ta:pageloading />
	<ta:form id="from1" fit="true">
	   <ta:fieldset id="panel1" >
		   <ta:text id="modelName" key="模型名称" labelWidth="60" required="true"/>
		   <ta:text id="modelKey" key="模型KEY" labelWidth="60" required="true"/>
		   <ta:textarea id="modelDesc" key="模型描述" height="95px" labelWidth="60"/>
	   </ta:fieldset>
	   <ta:buttonLayout align="right">
	      <ta:button id="btnSave" key="创建" isok="true" onClick="fnSaveModel();"/>
	      <ta:button id="btnCancle" key="关闭" onClick="parent.Base.closeWindow('addWin');"/>
	   </ta:buttonLayout>
	</ta:form>
</body>
<script type="text/javascript">
$(document).ready(function() {
	$("body").taLayout();
});
function fnSaveModel(){
	var paramter = {"dto['pName']":Base.getValue("modelName"),
			        "dto['pKey']":Base.getValue("modelKey"),
			        "dto['pDesc']":Base.getValue("modelDesc")};
	Base.submit("from1","processModelAction!saveProcessModel.do",paramter,null,true,function(data){
		var width = top.window.innerWidth;
		var heigth = top.window.innerHeight;
		top.layer.open({
		    type: 2,
		    title: ["BMPN2.0流程设计器","background-color:#62cae4;font-size:15px;"],
		    fix: true,
		    area: [width + "px", heigth + "px"],
		    content: "<%=basePath%>abpmn/processEditor/modeler.html?modelId="+data.fieldData.modelId,
		    cancel: function(index){
		    	top.layer.close(index);
		    	parent.Base.closeWindow("addWin");
		    }
		});
	});
}
</script>
</html>
<%@ include file="/ta/incfooter.jsp"%>
