<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>一键生成主从表</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar"  style="padding:10px;">
		<ta:pageloading/>
		<ta:form id="form1">
			<ta:fieldset id="fset1" cols="4">
				<ta:text id="q_address" key="地址"/>
				<ta:buttonLayout align="left">
					<ta:submit id="query" key="查询[Q]" isok="true" hotKey="q" icon="icon-search" submitIds="form1" url="serverAddressAction!query.do"/>
					<ta:button id="resetbt"  key="重置[C]" hotKey="C" icon="icon-remove" onClick="Base.resetForm('form1');Base.clearGridData('MainGrid');Base.clearGridData('FromGrid');"/>
				</ta:buttonLayout>
			</ta:fieldset>
		</ta:form>
		<ta:panel id="MainPanel" key="主表信息" withToolBar="true" fit="true" cssStyle="margin-top:10px;">
			<ta:panelToolBar>
				<ta:button id="mainAdd" key="新增[A]" hotKey="a" icon="icon-add" asToolBarItem="true" onClick="fnAdd();" />
				<ta:submit id="mainDelete" key="删 除[D]" hotKey="d" icon="icon-cancel" onSubmit="function(){return confirm('确认删除');}" asToolBarItem="true" submitIds="MainGrid" url="serverAddressAction!delete.do" successCallBack="function(){Base.deleteGridSelectedRows('MainGrid');}"/>
			</ta:panelToolBar>
			<ta:datagrid id="MainGrid" fit="true" selectType="checkbox" forceFitColumns="true" haveSn="true" columnFilter="true" dblClickEdit="true">
				<ta:datagridItem id="address" key="地址" sortable="true" width="700"/>
				<ta:datagridItem id="canuse" key="是否启用" collection="YESORNO"/>
				<ta:datagridItemOperate showAll="false" id="a" name="操作">
					<ta:datagridItemOperateMenu name="编辑" icon="a" click="fnEdit"></ta:datagridItemOperateMenu>
				</ta:datagridItemOperate>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	
	function fnAdd(){
		Base.openWindow('userwin1',"新增","serverAddressAction!toadd.do",null,500,200);
	}
	function fnEdit(rowdata){
		var param = {};
		param["dto['address']"]=rowdata.address;
		Base.openWindow('userwin',"编辑","serverAddressAction!tomodify.do?r="+Math.random(),param,500,200);
	}
	
	$(document).ready(function () {
		$("body").taLayout();
		

	});
</script>
<%@ include file="/ta/incfooter.jsp"%>