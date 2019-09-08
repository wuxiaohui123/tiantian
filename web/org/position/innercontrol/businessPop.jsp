<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>业务人员查询</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
<body class="no-scrollbar" style="padding:0px;margin:0px;">
	<ta:pageloading/>
	<ta:box fit="true">
		<ta:fieldset key="" id="field1"  cols="3">
			<ta:selectTree selectTreeBeforeClick="fnBeforeClick" fontCss="fnFontCss"  labelWidth="80"  cssStyle="width:200px"
					nameKey="orgname" idKey="orgid" parentKey="porgid" url="innerControlAction!webQueryAsyncOrgTree.do" asyncParam="['orgid']" key="部门" targetDESC="orgname"  treeId="orgTree" targetId="orgid" />
			<ta:radiogroup  id="isShowSubOrg" key="子部门业务人员"  labelWidth="120" cols="2">
				<ta:radio key="显示" value="true" />
				<ta:radio key="不显示" value="false" checked="true" />
			</ta:radiogroup>
			<ta:buttonLayout>
				<ta:button key="查询" onClick="fnClick()"></ta:button>
			</ta:buttonLayout>
		</ta:fieldset>
		<ta:panel fit="true" key="业务人员列表(双击选择业务人员)" hasBorder="false">
			<ta:datagrid id="businessGrid" fit="true" haveSn="true"  columnFilter="true" onRowDBClick="fnRowClick">
				<ta:datagridItem id="positionid" key="业务人员id" hiddenColumn="true"/>
				<ta:datagridItem id="orgnamepath" key="组织路径" width="400" showDetailed="true"/>
				<ta:datagridItem id="positionname" key="业务人员姓名" width="150" showDetailed="true"/>
			</ta:datagrid>
		</ta:panel>
	</ta:box>
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
});
function fnBeforeClick(treeId, treeNode){
	if(treeNode.effective == 1){
		Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能进行查询</div>");
		return false;
	}
	if(treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
		return false;
	}else{
		return true;
	}
}
function fnFontCss(treeId,treeNode){
	if(treeNode.effective == 1 && !treeNode.admin){
		return {'text-decoration':'line-through','color': 'red'};
	}else if(treeNode.effective == 1 && treeNode.admin){
		return {'text-decoration':'line-through'};
	}else if(treeNode.effective != 1 && !treeNode.admin){
		return {'color': 'red'};
	}else{
		return {};
	}
}
function fnClick(){
	Base.submit("field1","innerControlAction!queryBusinessByOrgId.do");
}
function fnRowClick(e,data){
	parent.Base.setValue("businessPop",data.positionname);
	parent.Base.setValue("businessPositionId",data.positionid);
	parent.Base.closeWindow("w_businessPop");
}
</script>
<%@ include file="/ta/incfooter.jsp"%>