<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>图标管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:10px;">
		<ta:pageloading/>
		<ta:panel fit="true" id="p1" key="图片信息" expanded="false" heightDiff="10" cssStyle="margin:0px;">
			<ta:datagrid id="picGrid" columnFilter="true" fit="true" haveSn="true" snWidth="50">
				<ta:datagridItem id="pic" key="图片预览" formatter="getPic" dataAlign="center" width="70"/>
				<ta:datagridItem id="picclass" key="CSS类名" width="210"/>
				<ta:datagridItem id="picname" key="文件名称" width="240"/>
				<ta:datagridItem id="picaddress" key="访问路径" width="545"/>
				<ta:datagridItem id="picinx" key="定位" hiddenColumn="true"/>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		Base.submit('picGrid','<%=basePath%>sysapp/iconsQueryAction!queryIconAll.do');
	});
	//格式化图片
	function getPic(row, cell, value, columnDef, dataContext){
		if(dataContext.picname.indexOf("gif")  != -1){
			return "<img class='"+dataContext.picclass+"' style='width:16px;height:16px;margin-top:3px;'/>";
		}
		if(dataContext.picclass.indexOf("ztree") != -1){
			return "<img style='width:16px;height:16px;margin-top:3px;background:url(../"+dataContext.picaddress+") "+dataContext.picinx+" no-repeat !important;'/>";
		}
		if(dataContext.picinx){
			return "<img class='"+dataContext.picclass+"' style='width:16px;height:16px;margin-top:3px;'/>";
		}
		if(dataContext.picinx==""){
			return "<img src='"+dataContext.picaddress +"' width='16px' height='16px' style='margin-top:3px'/>";	
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>