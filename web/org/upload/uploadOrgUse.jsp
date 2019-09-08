<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>批量导入</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
<body class="no-scrollbar" style="padding:0px;margin:0px;">
	<ta:pageloading/>
	<ta:box fit="true"  id="box1">
		<ta:textarea id="uploadSm" key="批量导入说明" height="120"  labelWidth="120" readOnly="true"></ta:textarea>
		<ta:buttonLayout>
			<ta:fileupload key="导入组织和人员" url="uploadOrgUserAction!detachUploadOrgAndUser.do" id="upload" icon="icon-setting"></ta:fileupload>
			<ta:fileupload key="导入组织" url="uploadOrgUserAction!detachUploadOrg.do" id="uploadOrg" icon="icon-organization"></ta:fileupload>
			<ta:fileupload key="导入人员" url="uploadOrgUserAction!detachUploadUser.do" id="uploadUser" icon="icon-adduser"></ta:fileupload>
		</ta:buttonLayout>
	</ta:box>
	
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	$("#uploadSm").css({"resize":"none","fontSize":"14px","lineHeight":"20px"});
	$("#uploadSm").parent().css({"border":"0px","boxShadow":"none"});
});
</script>
<%@ include file="/ta/incfooter.jsp"%>