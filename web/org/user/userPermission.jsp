<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:text id="p_userid" display="false"></ta:text>
	<ta:panel hasBorder="false" id="userPositionPanel" fit="true">
		<ta:datagrid id="userPermission" fit="true" haveSn="true" forceFitColumns="true">
			<ta:datagridItem id="menuname" key="名称"/>
			<ta:datagridItem id="usepermission" width="60" key="使用权限" collection="SEX" collectionData="[{id:0,name:'无'},{id:1,name:'有'}]"/>
			<ta:datagridItem id="repermission" width="60" key="授权权限" collectionData="[{id:0,name:'无'},{id:1,name:'有'}]"/>
			<ta:datagridItem id="reauthrity" width="60" key="再授权权限" collectionData="[{id:0,name:'无'},{id:1,name:'有'}]"/>
			<ta:datagridItem id="auditstate" width="60" key="审核状态"  collection="auditstate"/>
			<ta:datagridItem id="orgnamepath" key="组织路径" showDetailed="true"/>
			<ta:datagridItem id="positionname" key="所属岗位" showDetailed="true"/>
			<ta:datagridItem id="name" width="80" key="创建人"/>
			<ta:datagridItem id="createtime" key="创建时间" showDetailed="true"/>
			<ta:datagridItem id="btnShowYab139" key="查看数据区" icon="icon-search" click="fnShowYab139"/>
		</ta:datagrid>
	</ta:panel>
<script>
function fnShowYab139(data,e){
	Base.openWindow("yab139win","数据区","userMgAction!queryDataField.do",{"userid":Base.getValue("p_userid"),"menuid":data.menuid},"30%","80%");
}
</script>
<%@ include file="/ta/incfooter.jsp"%>