<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>系统异常信息</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" >
		<ta:pageloading/>
		<ta:form id="form" cssStyle="padding:10px;">
			<ta:fieldset id="fset" cols="5">
				<div style="float:left;height: 34px;line-height: 34px;">起止时间：</div>
				<ta:date id="begin"  datetime="true" showSelectPanel="true"></ta:date>
				<div style="float:left;height: 34px;line-height: 34px;">-</div>
				<ta:date id="end"  datetime="true" showSelectPanel="true"></ta:date>
				<ta:buttonLayout align="left">
					<ta:submit id="query" key="查询[Q]" isok="true" hotKey="q" icon="icon-search" submitIds="form" url="serverExeceptionLogAction!query.do"/>
					<ta:button id="resetbt"  key="重置" icon="icon-remove" onClick="Base.resetForm('form');Base.clearGridData('MainGrid');Base.clearGridData('from');"/>
				</ta:buttonLayout>
			</ta:fieldset>
		</ta:form>
			<ta:panel id="MainPanel" key="主表信息"  fit="true" cssStyle="margin:0px 10px 10px 10px;">
				<ta:datagrid id="MainGrid" fit="true" selectType="checkbox" forceFitColumns="true" haveSn="true" columnFilter="true" dblClickEdit="true">
					<ta:datagridItem id="type" key="exception类型"  sortable="true"/>
					<ta:datagridItem id="ipaddress" key="服务器ip地址"   sortable="true">
						<ta:datagridEditor type="text">
						</ta:datagridEditor>
					</ta:datagridItem>
					<ta:datagridItem id="clientip" key="客户端ip地址"  sortable="true"/>
					<ta:datagridItem id="menuid" key="菜单id"  sortable="true">
					
					</ta:datagridItem>
					<ta:datagridItem id="menuname" key="菜单名称"  sortable="true"/>
					<ta:datagridItem id="useragent" key="客户端环境"  sortable="true">
					<ta:datagridEditor type="text">
						</ta:datagridEditor>
					</ta:datagridItem>
					<ta:datagridItem id="time" key="报错时间"  dataType="dateTime" sortable="true"/>
					<ta:datagridItemOperate showAll="false" id="opt" name="操作选项" >
 						<ta:datagridItemOperateMenu name="删除" icon="a" click="fnDelete"/>
 						<ta:datagridItemOperateMenu name="报错详细内容" icon="a" click="fnClick"/>
					</ta:datagridItemOperate>
					<ta:dataGridToolPaging url="serverExeceptionLogAction!queryLinmit.do" submitIds="form" showExcel="true"  pageSize="10"/>
				</ta:datagrid>
			</ta:panel>
	</body>
</html>
<script type="text/javascript">
	
	function fnClick(data,e){
		var param = {};
		param["dto['id']"]=data.id;
		Base.openWindow('detailwin',"详细信息","serverExeceptionLogAction!goDetail.do",param,900,500,null,null,true);
 	}
	function fnDelete(data,e){
		var param = {};
		param["dto['id']"]=data.id;
		Base.confirm("确认删除?",function(yes){
				if(yes){
					Base.submit('form',"serverExeceptionLogAction!delete.do",param,
						function(){
							return true;
						},false,
						function(data){
							Base.alert("sucess","success");
						},
						function(data){
							Base.alert("error","error");
						}
					);
				}
			}
		);
		
 	}
 	/** 查询*/
 	function fnQuery() {
		Base.submit("form,MainGrid", "serverExeceptionLogAction!query.do");
	}
	
	$(document).ready(function () {
		$("body").taLayout();
		fnQuery();
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>