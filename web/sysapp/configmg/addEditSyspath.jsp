<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>系统路径管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:10px;">
		<ta:pageloading/>
<ta:panel fit="true" id="ssoinfo"  withButtonBar="true" hasBorder="false" bodyStyle="padding:0px 20px 0px 0px">
	<ta:text id="id" key="系统id" required="true" readOnly="true" labelWidth="110"></ta:text>
	<ta:text id="name" key="系统名称" required="true" labelWidth="110"></ta:text>
	<ta:text id="sysipaddress" key="ip地址" required="true" validType="ip" textHelp="系统访问ip地址，例如：127.0.0.1或者112.118.188.111等" labelWidth="110"></ta:text>
	<ta:number id="sysport" key="端口" required="true" min="1" textHelp="系统访问端口，例如:8080,7001等" alignLeft="true" labelWidth="110"></ta:number>
	<ta:text id="contextroot" key="系统上下文路径" required="true" labelWidth="110" textHelp="系统上下文访问路径，例如：ta3,sibpm等"></ta:text>
	<ta:radiogroup id="iscur" key="当前系统" cols="2" required="true" labelWidth="110">
		<ta:radio key="是" value="0" readonly="true"></ta:radio>
		<ta:radio key="否" value="1" readonly="true"></ta:radio>
	</ta:radiogroup>
	<ta:panelButtonBar>
		<ta:button icon="icon-add1" isok="true" key="保存" id="btnSave" onClick="fnSysSave(1)"></ta:button>
		<ta:button icon="icon-add1" isok="true" key="保存" id="btnUpdate" onClick="fnSysSave(2)" display="false"></ta:button>
		<ta:button icon="icon-no" key="关闭" onClick="parent.Base.closeWindow('syspath')"></ta:button>
	</ta:panelButtonBar>
</ta:panel>
	</body>
</html>
<script>
$(document).ready(function () {
	$("body").taLayout();
});
	function fnSysSave(flag){
		if(flag == 1){
			Base.submit("ssoinfo","configSysPathAction!saveUpdateSyspath.do",{"dto['curSyspathId']":Base.getValue("curSyspathId")},null,null,function(){
				Base.setRequired("curSyspathId");
				parent.Base.closeWindow("syspath");
				parent.Base.msgTopTip("<div class='msgTopTip'>新增系统成功</div>");
			});
		}else{
			Base.submit("ssoinfo","configSysPathAction!saveUpdateSyspath.do",{"dto['curSyspathId']":Base.getValue("curSyspathId")},null,null,function(){
				Base.setRequired("curSyspathId");
				parent.Base.closeWindow("syspath");
				parent.Base.msgTopTip("<div class='msgTopTip'>修改系统成功</div>");
			});
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>