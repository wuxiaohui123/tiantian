<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>代码表查看</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:10px;">
		<ta:pageloading/>
		<ta:fieldset cols="5" id="appCodePanel" span="2">
			<ta:text id="codeType" key="代码类别" />
			<ta:text id="codeTypeDESC" key="类别名称" />
			<ta:text id="codeValue" key="代码值" />
			<ta:text id="codeDESC" key="代码名称" />
			<ta:buttonLayout align="center">
				<ta:submit key="查询[Q]" hotKey="Q" icon="icon-search" submitIds="appCodePanel" url="appCodeMainAction!query.do" isok="true"/>
			<%--<ta:button key="重新加载页面码表[R]" hotKey="Q" icon="icon-search" onClick="fnRefresh();" />--%>
			</ta:buttonLayout>
		</ta:fieldset>
		<ta:panel  withToolBar="true" fit="true" key="码表列表" cssStyle="margin-top:10px;">
			<ta:panelToolBar align="left">
				<ta:button  key="新增" icon="icon-add" onClick="fnAdd()" asToolBarItem="true"></ta:button>
			</ta:panelToolBar>
			<ta:datagrid id="appCodeList" haveSn="true"  columnFilter="true" fit="true" forceFitColumns="true">
				<ta:datagridItem id="codeType" key="代码类别" sortable="true" width="100"></ta:datagridItem>
				<ta:datagridItem id="codeTypeDESC" key="类别名称" sortable="true" width="100"></ta:datagridItem>
				<ta:datagridItem id="codeValue" key="代码值" sortable="true" width="100"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="代码名称" sortable="true" width="100"></ta:datagridItem>
				<ta:datagridItemOperate showAll="false" id="a" name="操作">
					<ta:datagridItemOperateMenu name="删除缓存" icon="a" click="fnClearCache"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="编辑" icon="a" click="fnEdit"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="删除" icon="a" click="fnRemove"></ta:datagridItemOperateMenu>
				</ta:datagridItemOperate>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	
	function fnClearCache(rowdata){
		var param = {};
		param["dto['codeType']"] = rowdata.codeType;
		param["dto['codeValue']"] = rowdata.codeValue;
		param["dto['orgId']"] = rowdata.orgId;
		Base.confirm("该操作是执行码表在内存中的缓存清楚以保证下次请求能够与数据库同步，请确认依据配置好集群server地址。",function(v){
			if(v)Base.submit("","appCodeMainAction!clearcache.do",param);
		});
	}
	function fnEdit(rowdata){
		var param = {};
		param["dto['codeType']"] = encodeURI(rowdata.codeType);
		param["dto['codeTypeDESC']"] = encodeURI(rowdata.codeTypeDESC);
		param["dto['codeValue']"] = encodeURI(rowdata.codeValue);
		param["dto['codeDESC']"] = encodeURI(rowdata.codeDESC);
		param["dto['orgId']"] = rowdata.orgId;
		Base.openWindow("edit","码表修改","appCodeMainAction!edit.do",param,"200","250",null,function(){
			Base.submit("appCodePanel","appCodeMainAction!query.do");
		});
		
	}
	function fnAdd(){
		Base.openWindow("edit","码表新增","appCodeMainAction!edit.do",{},"300","250",function(){
			Base.setEnable("t_codeType,t_codeTypeDESC,t_codeValue");
			Base.showObj("saveBtn");
			Base.hideObj("editBtn");
		},function(){
			Base.submit("appCodePanel","appCodeMainAction!query.do");
		});
	}
	function fnRemove(rowdata){
		Base.confirm("确定删除该码表？",function(yes){
			if(yes){
				Base.submit("","appCodeMainAction!remove.do",{"dto['codeType']":rowdata.codeType,"dto['codeValue']":rowdata.codeValue,"dto['orgId']":rowdata.orgId},null,null,
						function(){
					Base.submit("appCodePanel","appCodeMainAction!query.do");
				});
			}
		})
	}
	/**
	 * 重新加载页面码表
	*/
	function fnRefresh(){
		$.ajax({
		           cache: true,
		           type: "POST",
		           url: "appCodeMainAction!pushLocalCache.do",
		           dataType : "json",
		           data:"version=0",
		           async: true,
		           error: function(request) {
		               Base.alert("error:"+request);
		           },
		           success: function(data) {
			           if(JSON.stringify(data) != "{}"){
			           		localStorage.clear();
			          		for(var i in data){
								localStorage.setItem(i,data[i]);
							}
			           }
				       $.ajax({
				           cache: true,
				           type: "POST",
				           url: "appCodeMainAction!pushLocalCache.do",
				           dataType : "json",
				           data:"version="+localStorage.getItem("VERSION"),
				           async: true,
				           error: function(request) {
				               alert("error:"+request);
				           },
				           success: function(data) {
					           if(JSON.stringify(data) != "{}"){
					          		for(var i in data){
										localStorage.setItem(i,data[i]);
									}
					           }
					           Base.msgTopTip("<div class='msgTopTip'>加载成功</div>");
				           }
		    			});
		           }
		    });
	}
$(document).ready(function () {
	$("body").taLayout();
});
</script>
<%@ include file="/ta/incfooter.jsp"%>