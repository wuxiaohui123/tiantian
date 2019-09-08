<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>一键生成主从表</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body>
		<ta:pageloading/>
		<ta:box id="box" fit="true" minWidth="900">
		<ta:panel id="panel1" key="缓存分类" height="200">
			<ta:datagrid id="cacheList" haveSn="true" selectType="checkbox" fit="true">
				<ta:datagridItem id="name" key="cacheName" width="300"/>
				<ta:datagridItem id="size" key="mem_size" width="100"/>
				<ta:datagridItem id="avgSearchTime" key="avgSearchTime" width="100"/>
				<ta:datagridItem id="avgGetTime" key="avgGetTime" width="100"/>
				<ta:datagridItem id="e" key="查看内容" icon="icon-edit" click="fnEdit"/>
			</ta:datagrid>
		</ta:panel> 
		<ta:panel id="panel2" key="缓存内容" fit="true" >
			<ta:datagrid id="cacheElementList" haveSn="true" fit="true" columnFilter="true">
				<ta:datagridItem id="e" key="清除缓存" icon="icon-remove" click="fnRemove"/>
				<ta:datagridItem id="key" key="key" showDetailed="true" width="400" sortable="true"/>
				<ta:datagridItem id="value" key="value" showDetailed="true" width="400"/>
			</ta:datagrid>
		</ta:panel>		
	</ta:box>
	</body>
</html>
<script type="text/javascript">
	function fnEdit(rowdata){
		Base.submit("","cacheMgAction!query.do",{"dto['cacheName']":rowdata.name});
	}
	function fnRemove(rowdata) {
		Base.confirm('如果是集群环境下将通知其他服务器删除该缓存，请确定集群地址配置正确。<br>确定删除？',function(c){
			if(c){
				Base.submit("","cacheMgAction!delete.do",{"dto['key']":rowdata.key,"dto['cacheName']":rowdata.cacheName},null,null,function(){
					Base.deleteGridRow("cacheElementList",rowdata.row);
				});
			}
		});
		
	}
	
	$(document).ready(function () {
		$("body").taLayout();
		

	});
</script>
<%@ include file="/ta/incfooter.jsp"%>