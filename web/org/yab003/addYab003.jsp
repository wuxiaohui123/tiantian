<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>经办机构管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" >
		<ta:pageloading/>
		<ta:panel id="addPanel" fit="true" withButtonBar="true" hasBorder="false" bodyStyle="padding:10px 10px 0px 0px;">
			<ta:text id="treeNodeId" display="false"></ta:text>
			<ta:text id="t_codeType"  key="代码类别" value="YAB003" readOnly="true" required="true"/>
			<ta:text id="t_codeTypeDESC"  key="类别名称" value="经办机构" readOnly="true" required="true"/>
			<ta:text id="t_codeValue"  key="代码值"   required="true" maxLength="6"/>
			<ta:text id="t_codeDESC"  key="经办机构名称"  required="true"/>
			<ta:text id="treeId"  key="treeId" display="false"/>
			<ta:text id="treeNode"  key="treeNode" display="false"/>
			<ta:text id="treeNodeName"  key="treeNode" display="false"/>
			<ta:panelButtonBar align="right">
				<ta:button id="confirmAdd" key="确认" isok="true" onClick="confirmAdd()"></ta:button>
				<ta:button id="cancel" key="取消" onClick="cancelAdd()"></ta:button>
			</ta:panelButtonBar>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	
	function confirmAdd(treeId, treeNode){
		Base.submit("addPanel","agenciesMgAction!addOrEditTreeNode.do",{"dto['yab003']":Base.getValue("treeNodeId"),"dto['insertApp']":"1"},null,null,function(){
			/* Base.submit("","agenciesMgAction!saveYab003.do",{"dto['yab003']":Base.getValue("treeNodeId"),"dto['codeType']":Base.getValue("t_codeType"),"dto['codeTypeDESC']":Base.getValue("t_codeTypeDESC"),"dto['codeValue']":Base.getValue("t_codeValue"),"dto['codeDESC']":Base.getValue("t_codeDESC")},null,null,function(data){
			},function(){
				Base.alert("执行失败!!!");
			}); */
		 	var treeObj  = parent.Base.getObj("yab003Tree");
			var parentId = treeObj.getNodeByParam("id",Base.getValue("treeNodeId"));
			var nodeValue = Base.getValue("t_codeDESC");
			var childNode = {name:nodeValue};
			treeObj.addNodes(parentId,childNode,false);
			parent.Base.msgTopTip("新增成功");
			closeWindow(); 
		});	
	}
	
	function cancelAdd(){
		closeWindow();
	}
	
	function closeWindow(){
		parent.Base.closeWindow("addWindow");
	}
	
	function fnClearCache(rowdata){
		var param = {};
		param["dto['codeType']"] = rowdata.codeType;
		param["dto['codeValue']"] = rowdata.codeValue;
		param["dto['orgId']"] = rowdata.orgId;
		Base.submit("","agenciesMgAction!clearcache.do",param);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>