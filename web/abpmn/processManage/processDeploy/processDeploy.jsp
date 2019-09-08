<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>流程文件管理</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
	<ta:pageloading />
	<ta:form id="form1" methord="post" enctype="multipart/form-data" fit="true" cssStyle="padding:10px;">
		<ta:fieldset id="fset1" key="查询条件">
		    <ta:box id="box1" cols="4">
		    	<ta:text id="processKey" key="流程Key" labelWidth="70"/>
		    	<ta:text id="processName" key="流程名称" labelWidth="70"/>
		    	<ta:number id="processVers" key="流程版本" labelWidth="70" alignLeft="true"/>
		    	<ta:buttonLayout align="left" columnWidth="0.2">
		    		<ta:button id="queryBtn" key="查询" isok="true" onClick="toQuery()"/>
		    		<ta:button id="uploadBtn" key="↓" onClick="fnShow(this)"/>
		    		<ta:button id="F1Btn" key="查看帮助" icon="xui-icon-help" onClick="toHelpView()"/>
		    	</ta:buttonLayout>
		    </ta:box>
		    <ta:box id="box2" cols="8" cssStyle="display:none;">
			    <ta:text id="theFile" type="file" span="4"/>
				<ta:checkbox id="openAtOnce" key="立即启用"/>
				<ta:buttonLayout align="left">
					<ta:button id="saveBtn" key="上传文件" icon="icon-upload"/>
				</ta:buttonLayout>
		    </ta:box>
		</ta:fieldset>
		<ta:panel id="panel1" key="部署流程" fit="true" cssStyle="padding-top:10px;">
		 <ta:datagrid id="processdefineGrid" fit="true" forceFitColumns="true" haveSn="true" columnFilter="true" enableColumnMove="true">
		    <ta:datagridItem id="processid" key="流程ID" align="center" hiddenColumn="true"></ta:datagridItem>
		    <ta:datagridItem id="download" key="点击下载" icon="icon-download" width="50" align="center" click="downloadProcessModel"></ta:datagridItem>
		    <ta:datagridItem id="view" key="流程图" icon="icon-organization" width="50" align="center" click="viewProcessImage"></ta:datagridItem>
		    <ta:datagridItem id="processname" key="流程名称" align="center"></ta:datagridItem>
		    <ta:datagridItem id="processkey" key="流程Key值" align="center"></ta:datagridItem> 
		    <ta:datagridItem id="processversion" key="版本" align="center"></ta:datagridItem>
		    <ta:datagridItem id="processdesc" key="流程描述" align="center" showDetailed="true"></ta:datagridItem>
		    <ta:datagridItem id="processdeployid" key="流程部署ID" align="center" hiddenColumn="true"></ta:datagridItem>
		    <ta:datagridItem id="filename" key="文件名称" align="center"></ta:datagridItem> 
		    <ta:datagridItem id="uploador" key="上传人/部署人" align="center"></ta:datagridItem> 
		    <ta:datagridItem id="uploadtime" key="上传时间/部署时间" align="center" width="100" dataType="date"></ta:datagridItem> 
		    <ta:datagridItem id="delete" key="点击删除" icon="icon-no" width="65" align="center" click="deleteProcessDefinition"></ta:datagridItem>
		 </ta:datagrid>
		</ta:panel>
	</ta:form>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
		toQuery();
	});
	function toHelpView(){
		var helptitle="<span style='color:blue'>帮助信息--流程定义文件(如果此界面显示不正常，请使用最新版本的谷歌浏览器)</span>";
		Base.openWindow("helpView", helptitle, null, null, "650", "400",function(){},function(){},false);
	}
	function toQuery(){
		Base.submit("form1", "processDeployAction!queryProcessDefinition.do",null, null, false);
	}
	function fnShow(obj){
		var box = $(Base.getObj("box2"));
		if(box.css("display") == "none"){
			box.show();
			$(obj).find("span")[0].innerHTML = "↑";
		}else{
			box.hide();
			$(obj).find("span")[0].innerHTML = "↓";
		}
	}
	//删除流程定义
	function deleteProcessDefinition(data, event){
		Base.confirm("确定要删除【流程定义Id:"+data.processid+"】的所有相关信息？",function(yes){
			if(yes){
				var parmater = {"dto['deploymentId']":data.processdeployid};
				Base.submit("", "processDeployAction!deleteProcessDefinition.do",parmater, null, false, function(){
					toQuery();
				});
			}
		});
		
	}
	//下载流程定义
	function downloadProcessModel(data, event){
		var parmater = "deploymentId=" + data.processdeployid + "&filename="+data.filename;
		console.log(parmater);
		location.href = top.window.baseGlobvar.basePath + "abpmn/processDeployAction!downloadProcessModel.do?" + parmater;
	}
	//查看流程图
	function viewProcessImage(data, event){
		Base.openWindow("viewer",data.processname + "--流程图",null,"","99%","99%",function(){
			var parmater = {"dto['deploymentId']" : data.processdeployid};
			Base.getJson("processDeployAction!viewProcessImage.do",parmater,function(data){
				if(data.fieldData.image != ""){
					var myProWin = $(Base.getObj("viewer"));
					var width = myProWin.width();
					var height = myProWin.height();
					var proImage = "<img src='data:image/png;base64," +data.fieldData.image + "' style='width:"+width+";height:"+height+"'/>";
					myProWin.append(proImage);
				}
			});
		},function(){
			
		},false);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>