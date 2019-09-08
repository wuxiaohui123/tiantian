<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:fieldset cols="3" id="opLogField" key="查询" cssStyle="margin-bottom:5px;">
		<ta:text id="w_userid" display="false"></ta:text>
		<ta:date id="logStartTime" key="开始时间" datetime="true" showSelectPanel="true"></ta:date>
		<ta:date id="logEndTime" key="结束时间" datetime="true" showSelectPanel="true"></ta:date>
		<ta:buttonLayout align="left">
			<ta:button key="查询" isok="true" onClick="fnClick()"  icon="xui-icon-query"></ta:button>
		</ta:buttonLayout>
	</ta:fieldset>
	<ta:panel hasBorder="false" id="userOpLogPanel" fit="true">
		<ta:datagrid id="userOpLog"  fit="true" haveSn="true" forceFitColumns="true">
			<ta:datagridItem key="批次号" id="batchno" width="100"></ta:datagridItem>
			<ta:datagridItem key="操作主体类型" id="opbody" collection="opobjtype" width="100"></ta:datagridItem>
			<ta:datagridItem key="操作主体" id="opsubjektname"  width="200" showDetailed="true"></ta:datagridItem>
			<ta:datagridItem key="操作类型" id="optype" collection="optype" width="150"></ta:datagridItem>
			<%-- <ta:datagridItem key="影响主体类型" id="influencebodytype" collection="opobjtype" width="100"></ta:datagridItem>
			<ta:datagridItem key="影响主体" id="influencebodyname"  width="200" showDetailed="true"></ta:datagridItem> --%>
			<ta:datagridItem key="变更内容" id="changcontent" showDetailed="true" width="400"></ta:datagridItem>
			<%-- <ta:datagridItem key="操作人" id="opusername" width="100"></ta:datagridItem> --%>
			<ta:datagridItem key="操作岗位" id="oppositionname" width="200" showDetailed="true"></ta:datagridItem>
			<ta:datagridItem key="操作时间" id="optime" width="200" sortable="true" dataType="dateTime"></ta:datagridItem>
			<ta:dataGridToolPaging url="userMgAction!webQueryOpLogs.do" pageSize="500" submitIds="opLogField" showExcel="false"></ta:dataGridToolPaging>
		</ta:datagrid>
	</ta:panel>
<script>
function fnClick(){
	Base.submit("opLogField","userMgAction!webQueryOpLogs.do");
}
</script>
<%@ include file="/ta/incfooter.jsp"%>