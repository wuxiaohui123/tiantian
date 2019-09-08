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
		
		<ta:panel fit="true" span="3" hasBorder="true" withToolBar="true" key="系统路径">
			<ta:panelToolBar>
				<ta:button key="新增" asToolBarItem="true" icon="icon-add1" onClick="fnAdd()"></ta:button>
			</ta:panelToolBar>
			<ta:datagrid id="ssoGrid" fit="true" haveSn="true"  forceFitColumns="true" >
				<ta:datagridItem id="id" key="系统id" ></ta:datagridItem>
				<ta:datagridItem id="name" key="系统名称" ></ta:datagridItem>
				<ta:datagridItem id="url" key="系统地址前缀" width="300"></ta:datagridItem>
				<ta:datagridItem id="py" key="拼音过滤"></ta:datagridItem>
				<ta:datagridItemOperate showAll="false" id="a" name="操作">
					<ta:datagridItemOperateMenu name="编辑" icon="a" click="fnEdit"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="删除" icon="a" click="fnRemove"></ta:datagridItemOperateMenu>
				</ta:datagridItemOperate>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnEdit(data,e){
		Base.openWindow("syspath","系统路径配置","configSysPathAction!toSaveUpdateSyspath.do",{"saveUpdate":"update","id":data.id},400,300,null,function(){
			Base.submit("","configSysPathAction!querySyspath.do");
		},true);
	}
	function fnRemove(data,e){
		if(data.cursystem == "0"){
			Base.confirm("该系统为当前系统，是否删除？",function(yes){
				if(yes){
					Base.submit("","configSysPathAction!removeSyspath.do",{"dto['id']":data.id},null,null,function(){
						Base.submit("","configSysPathAction!querySyspath.do");
						Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
						Base.focus("curSyspathId");
					});
				}
			});
		}else{
			Base.confirm("是否移除该系统，移除后将不再显示该系统下的菜单？",function(yes){
				if(yes){
					Base.submit("","configSysPathAction!removeSyspath.do",{"dto['id']":data.id},null,null,function(){
						Base.submit("","configSysPathAction!querySyspath.do");
						Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
					});
				}
			});
		}
	}
	function fnAdd(){
		Base.openWindow("syspath","系统路径配置","configSysPathAction!toSaveUpdateSyspath.do",{"saveUpdate":"save"},400,300,null,function(){
			Base.submit("","configSysPathAction!querySyspath.do");
		},true);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>