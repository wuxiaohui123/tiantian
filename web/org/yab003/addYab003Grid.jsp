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
		<ta:box fit="true" heightDiff="38" id="box1">
			<ta:text id="yab003" display="false"></ta:text>
			<ta:datagrid id="yab003ChildGrid" fit="true" haveSn="true" selectType="checkbox" columnFilter="true" >
				<ta:datagridItem id="codeValue" key="经办机构码值" width="100"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="经办机构名称" width="200"></ta:datagridItem>
			</ta:datagrid>
			<ta:buttonLayout align="right" cssStyle="padding-top:5px;">
				<ta:button key="保存" isok="true" onClick="fnSave()"></ta:button>
				<ta:button key="取消" onClick="fnCancle()"></ta:button>
			</ta:buttonLayout>
		</ta:box>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnSave(){
		var selectDatas = Base.getGridSelectedRows("yab003ChildGrid");
		if(selectDatas && selectDatas.length > 0) {
			Base.submit("box1,yab003ChildGrid","agenciesMgAction!addOrEditTreeNode.do",{},null,null,function(){
				var treeObj  = parent.Base.getObj("yab003Tree"); 
				var parentNode = treeObj.getNodeByParam("id",Base.getValue("yab003")); 
				var childrenNodes = [];
				for(var i = 0 ; i < selectDatas.length ; i++) {
					childrenNodes.push({"id":selectDatas[i].codeValue,"name":selectDatas[i].codeDESC});
				}
				treeObj.addNodes(parentNode,childrenNodes,false); 
				parent.Base.closeWindow("addWindow");
			});
		}else {
			Base.alert("请至少勾选一条经办机构数据后再保存","warn");
		}
	}
	function fnCancle(){
		parent.Base.closeWindow("addWindow");
	} 
</script>
<%@ include file="/ta/incfooter.jsp"%>