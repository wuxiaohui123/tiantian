<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>流程引擎配置</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" style="padding:10px">
	<ta:pageloading />
	<ta:tabs fit="true" headPlain="true">
		<ta:tab key="流程基本设置" icon="icon-setting">
			<ta:buttonLayout align="left">
				<ta:button id="btnSave1" key="保存设置" icon="icon-save" isok="true" isShowIcon="true" />
			</ta:buttonLayout>
			<ta:fieldset id="fst1" key="流程信息" cols="5" cssStyle="margin:5px">
				<ta:selectInput id="processName" key="流程名称" span="2" onSelect="selectedProcess" required="true"/>
				<ta:text id="processVersion" key="流程版本" span="1" disabled="true"/>
				<ta:text id="processKey" key="流程Key" span="2" disabled="true"/>
				<div style="margin-left:120px;color:chocolate;">您还可输入 500 个字符（限500 个字符之内）</div>
				<ta:textarea id="processDesc" key="流程描述" span="5" height="50" maxLength="500"/>
			</ta:fieldset>
			<ta:box cols="3">
				<ta:fieldset key="设置业务分类" cssStyle="margin-left:5px">
					<ta:selectTree treeId="businessType" key="业务分类" targetId="id" targetDESC="name" height="200" minLevel="2" asyncParam="['id']" url="abpmnConfigAction!getAsyncData.do"  required="true"/>
					<ta:selectInput id="businessDeail" key="处理方式" collection="ABPMN_IS_FLAG"/>
					<div style="color:red;">注：当场办结设置为‘是’，那么此流程进行业务受理时，将会直接加载到经办界面</div>
				</ta:fieldset>
			</ta:box>
		</ta:tab>
		<%-- <ta:tab key="管理权限管理" icon="icon-yhpurview">
			<ta:buttonLayout align="left">
				<ta:button key="保存设置" icon="icon-save" isok="true" isShowIcon="true" />
			</ta:buttonLayout>
			<ta:fieldset key="办理公司" cols="3" cssStyle="margin-left:5px;margin-right:5px;">
				<ta:selectInput id="busOrg" key="业务公司" />
			</ta:fieldset>
			<ta:box cols="3" cssStyle="margin:5px;" fit="true">
				<ta:panel id="yxPanel" key="可选流程" fit="true" columnWidth="0.475">
					<ta:datagrid id="yxGrid" fit="true" forceFitColumns="true" columnFilter="true">
						<ta:datagridItem id="procName" key="流程名称" />
						<ta:datagridItem id="procVersion" key="流程版本" />
						<ta:datagridItem id="procType" key="流程分类" />
					</ta:datagrid>
				</ta:panel>
				<ta:box columnWidth="0.05" fit="true" cssStyle="padding-top:150px">
					<div class="fielddiv">
						<ta:button icon="addBtn" isShowIcon="true" asToolBarItem="true" />
					</div>
					<div class="fielddiv">
						<ta:button icon="addAllBtn" isShowIcon="true" asToolBarItem="true" />
					</div>
					<div class="fielddiv">
						<ta:button icon="delBtn" isShowIcon="true" asToolBarItem="true" />
					</div>
					<div class="fielddiv">
						<ta:button icon="delAllBtn" isShowIcon="true" asToolBarItem="true" />
					</div>
				</ta:box>
				<ta:panel id="kxPanel" key="已选流程" fit="true" columnWidth="0.475">
					<ta:datagrid id="kxGrid" fit="true" forceFitColumns="true" columnFilter="true">
						<ta:datagridItem id="procName" key="流程名称" />
						<ta:datagridItem id="procVersion" key="流程版本" />
						<ta:datagridItem id="procType" key="流程分类" />
					</ta:datagrid>
				</ta:panel>
			</ta:box>
		</ta:tab> --%>
		<ta:tab key="办理期限设置" icon="icon-date">
		 	<ta:fieldset id="fst3" key="流程信息" cols="5" cssStyle="margin:5px">
				<ta:selectInput id="processName_3" key="流程名称" span="2" onSelect="selectedProcess" required="true"/>
				<ta:text id="processVersion_3" key="流程版本" span="1" disabled="true"/>
				<ta:text id="processKey_3" key="流程Key" span="2" disabled="true"/>
				<ta:textarea id="processDesc_3" key="流程描述" span="5" height="50" maxLength="500"/>
			</ta:fieldset>
		</ta:tab>
	</ta:tabs>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
	function selectedProcess(key,value){
		Base.getJson("abpmnConfigAction!queryProcessDefinition.do",{"dto['processDefId']":value},function(data){
			if(data.fieldData.processDef != null){
				var processObj = data.fieldData.processDef;
				Base.setValue("processVersion",processObj.version);
				Base.setValue("processKey",processObj.key);
				Base.setValue("processDesc",processObj.desc);
			}
		});
		Base.getJson("abpmnConfigAction!getProcessFlowNodes.do",{"dto['processDefId']":value},function(data){});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
