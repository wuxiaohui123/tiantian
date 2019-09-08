<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>模块管理</title>
		<%@ include file="/ta/inc.jsp"%>
		<%@ taglib prefix="s" uri="/struts-tags"%>
	</head>

	<body class="no-scrollbar">
		<ta:pageloading />
		<ta:form id="moduleEditForm" fit="true">
			<ta:text id="moduleId" display="false" />
			<ta:text required="true" cssStyle="margin-top:10px;" id="moduleName"
				key="模块名称" />
			<ta:text required="true" cssStyle="margin-top:10px;" id="moduleURL"
				key="模块URL" />
			<ta:text cssStyle="margin-top:10px;" id="moduleHeight" key="模块高度"
				validType="integer" />
			<ta:selectInput required="true" cssStyle="margin:10px 0;"
				id="moduleDefault" key="是否默认展示"
				data="[{'id':1,'name':'是'},{'id':0,'name':'否'}]" />
			<ta:buttonLayout align="center">
				<s:if test="#request.addFlag==true">
					<ta:button id="btn_save" key="保存" icon="icon-save"
						onClick="fnSaveModule()" isok="true" />
				</s:if>
				<s:else>
					<ta:button id="btn_update" key="保存" icon="icon-save"
						onClick="fnUpdateModule()" isok="true" />
				</s:else>
				<ta:button id="btn_reset" key="重置" icon="icon-reload"
					onClick="$('#moduleName,#moduleURL').val('')" />
			</ta:buttonLayout>
		</ta:form>
	</body>

</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
	function fnSaveModule() {
		Base.submit("moduleEditForm","<%=basePath%>sysapp/moduleMainAction!doAdd.do",null,null,null,function(){
			alert("新增成功！");
			$('#moduleName,#moduleURL').val('');
		});
	}
	function fnUpdateModule(){
		Base.submit("moduleEditForm","<%=basePath%>sysapp/moduleMainAction!doUpdate.do",null,null,null,function(){
			alert("修改成功！");
			parent.Base.closeWindow("editWin");
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>