<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head> 
<title>流程模型设计</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:350}" >
	<ta:pageloading />
	<ta:box id="mymodel" key="流程模型管理" position="left" cssStyle="padding:10px;">
	  <ta:panel id="mypanel" fit="true" hasBorder="false" heightDiff="20">
	    <%--  <ta:box cols="2">
	     	<ta:text id="processName" columnWidth="0.75" placeholder="可以输入模型ID，模型名称查询"/>
	     	<ta:buttonLayout columnWidth="0.25">
		     	<ta:button id="queryBtn" key="查询" isok="true" onClick="fnRefresh()"/>
	     	</ta:buttonLayout>
	     </ta:box> --%>
	     <ta:listView id="myProcess" width="100%" height="100%" hasSearch="true" itemDisplayTemplate="{modelName}（ID:{modelId}）" isAsync="true" asyncUrl="processModelAction!queryModel.do"
	     searchKey="modelName" itemIcon="icon-process" isPage="false" itemClickBgColor="#a3b39b" showDeleteBtn="true" deleteUrl="processModelAction!deleteProcessModel.do" itemClick="fnClick"/>
	  </ta:panel>
	</ta:box>
	<ta:box position="center" cssStyle="padding:10px">
	    <ta:toolbar id="btns">
	       <ta:toolbarItem id="btnAdd" key="新建模型" onClick="fnAddModel()"/>
	       <ta:toolbarItemSeperator></ta:toolbarItemSeperator>
	       <ta:toolbarItem id="btnEdit" key="编辑模型" onClick="fnEditModel()"/>
	       <ta:toolbarItemSeperator></ta:toolbarItemSeperator>
	       <ta:toolbarItem id="btnDeploy" key="部署模型" onClick="fnDeployModel()"/>
	    </ta:toolbar>
		<ta:panel id="processModelInfo" key="流程模型信息" expanded="true">
		   <ta:tableView id="processModelInfoTable">
		      <ta:tr>
		         <ta:td key="模型ID" width="50px"/><ta:td id="mId"/>
		         <ta:td key="模型名称" width="50px"/><ta:td id="mName"/>
		      </ta:tr>
		      <ta:tr>
		         <ta:td key="模型Key" width="50px"/><ta:td id="mKey"/>
		         <ta:td key="模型版本" width="50px"/><ta:td id="mVersion"/>
		      </ta:tr>
		      <ta:tr>
		         <ta:td key="创建时间" width="50px"/><ta:td id="mCreateTime"/>
		         <ta:td key="最后修改时间" width="50px"/><ta:td id="mLastUpdateTime"/>
		      </ta:tr>
		      <ta:tr><ta:td key="模型类型" width="50px"/><ta:td id="mType" colspan="3"/></ta:tr>
		      <ta:tr><ta:td key="元数据" width="50px"/><ta:td id="mMetaInfo" colspan="3"/></ta:tr>
		      <%-- <ta:tr><ta:td key="部署ID" width="50px"/><ta:td id="mDeployId"/>
		      <ta:td key="创建人" width="50px"/><ta:td id="mTenantId"/></ta:tr> --%>
		   </ta:tableView>
		</ta:panel>
		<ta:box id="processModelImage" fit="true">
		   <ta:panel id="panelImage" key="流程图" fit="true" cssStyle="padding-top:10px" 
		   headerButton="[{'id':'button1','name':'全屏查看','click':'fullScreenView()'}]">
		       <img id="processmodel"/>
		   </ta:panel>
		</ta:box>
	</ta:box>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
	//获取流程模型信息
	var modelId = "";
	function fnClick(model){
		$("#mId").find("div").html(model.modelId);
		$("#mName").find("div").html(model.modelName);
		$("#mKey").find("div").html(model.modelKey);
		$("#mVersion").find("div").html(model.modelVersion);
		$("#mType").find("div").html(model.modelType);
		$("#mCreateTime").find("div").html(model.modelCreateTime);
		$("#mLastUpdateTime").find("div").html(model.modelLastUpdateTime);
		$("#mMetaInfo").find("div").html(model.modelMetaInfo);
		/*$("#mDeployId").find("div").html(rowdata.modelDeployId);
		$("#mTenantId").find("div").html(rowdata.modelTenantId); */
		processModelImageQuery(model.modelId);
		modelId = model.modelId;
	}
	
	//查看流程模型图
	function processModelImageQuery(modelid){
		var pModelImg = document.getElementById("processmodel");
		if(modelid==""){
			pModelImg.src="";
			return;
		}
		Base.getJson("processModelAction!getProcessModelSvg.do",{"dto['modelId']":modelid},
			function(data){
				if(data.fieldData.imgData != ""){
					pModelImg.src="data:image/jpg;base64,"+data.fieldData.imgData;	
					pModelImg.width = $("#panelImage").find(".panel-body").width();
					pModelImg.height = $("#panelImage").find(".panel-body").height();
				}else{
					pModelImg.src="";
				}
		});
	}
	//添加流程模型
	function fnAddModel(){
	    Base.openWindow("addWin","新建模型","processModelAction!toAddProcessModel.do",null,"400","265",null,function(){
            Base.refreshListView("myProcess");
	    },true,"top:43.5px;left:316px;",{maximizable:false});
	}
	//编辑流程模型
	function fnEditModel(){
		if(modelId == ""){
			Base.alert("请选择模型！","error");
			return;
		}
		var width = top.window.innerWidth;
		var height = top.window.innerHeight;
		top.layer.open({
		    type: 2,
		    title: ["BMPN2.0流程设计器","background-color:#62cae4;font-size:15px;"],
		    fix: true,
		    area: [width + "px", height + "px"],
		    content: "<%=basePath%>abpmn/processEditor/modeler.html?modelId="+modelId,
		    cancel: function(index){
		    	if(confirm("确定要关闭么?")){
		    		Base.refreshListView("myProcess");
					var obj = {"modelId":"","modelName":"","modelKey":"","modelVersion":"","modelType":"","modelCreateTime":"","modelLastUpdateTime":"","modelMetaInfo":""};
					fnClick(obj);
					top.layer.close(index);
		    	}
		    	return false; 
		    }
		});
	}
	//部署流程模型
	function fnDeployModel(){
		Base.submit("","processModelAction!deployProcessModel.do",{"dto['modelId']":modelId});
	}
	//全屏查看
	function fullScreenView(){
		var pModelImg = document.getElementById("processmodel");
		var width = top.window.innerWidth;
		var heigth = top.window.innerHeight;
		if(pModelImg.src.startWith("data:image/jpg;base64,")){
			top.layer.open({
			    type: 1,
			    title: ["流程图","background-color:#62cae4;font-size:15px;"],
			    fix: true,
			    area: [width + "px", heigth + "px"],
			    content: "<img src='"+pModelImg.src+"' style='width:100%;height:100%;'/>",
			    cancel: function(index){
			    	top.layer.close(index);
			    }
			});
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
