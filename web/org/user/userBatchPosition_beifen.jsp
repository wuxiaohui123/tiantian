<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:panel id="opPanel" hasBorder="false" expanded="false" fit="true" bodyStyle="overflow:auto;" withButtonBar="true">
	<ta:tree id="positionsTree" beforeClick="fnBeforeClick" parentKey="porgid" childKey="orgid" nameKey="orgname" checkable="true" checkStyle="radio" checkRadioType="all"/>
	<ta:text id="wp_orgid" display="none"/>
	<ta:panelButtonBar>
		<ta:button key="保存[S]" hotKey="S" icon="icon-add1" onClick="fnChangePosition()" />
		<ta:button key="关闭[X]" hotKey="X" icon="icon-no" onClick="Base.closeWindow('win')"/>
	</ta:panelButtonBar>
</ta:panel>
<script type="text/javascript">
function fnChangePosition(){
	var selectRows = Base.getGridSelectedRows("userGd");
	var substr = Ta.util.obj2string(selectRows);
	var node = Base.getObj("positionsTree").getCheckedNodes(true);
	Base.setValue("wp_orgid", node[0].orgid);
	Base.submit("wp_orgid","userMgAction!webSaveBatchPosition.do", {"users":substr}, false, false, 
		function(){
			Base.alert("成功", "保存成功", function(){
				fnQueryUsers();
				Base.closeWindow('win');
			})
		}
	)
}
function fnBeforeClick(treeId, treeNode){
	if (treeNode.admin != true) return false;
}
</script>
<%@ include file="/ta/incfooter.jsp"%>