<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:panel fit="true" withButtonBar="true" >
	<ta:datagrid fit="true" haveSn="true" selectType="radio" id="cloneGrid">
		<ta:datagridItem id="loginid" key="登录号" width="100"  align="center" dataAlign="center" formatter="fnFomatter"></ta:datagridItem>
		<ta:datagridItem id="positionname" key="姓名" width="100" align="center" dataAlign="center" showDetailed="true" sortable="true"></ta:datagridItem>
		<ta:datagridItem id="sex" key="性别" width="70" collection="sex"  align="center" dataAlign="center"></ta:datagridItem>
		<ta:datagridItem id="orgnamepath" key="所属部门" width="300" align="center" showDetailed="true" sortable="true"></ta:datagridItem>
		<%-- <ta:datagridItem id="username" key="创建人" width="100" align="center" dataAlign="center" sortable="true" showDetailed="true"></ta:datagridItem>
		<ta:datagridItem id="createtime" key="创建时间" width="150" align="center" dataAlign="center" dataType="date" sortable="true"></ta:datagridItem> --%>
		<ta:dataGridToolPaging url="personalPositionMgAction!queryPositions.do" submitIds="query" showExcel="false" pageSize="1000"></ta:dataGridToolPaging>
	</ta:datagrid>
	<ta:panelButtonBar align="center">
		<ta:button key="确定" icon="icon-add1" isok="true" onClick="fnCloneOk()" />
		<ta:button key="关闭" icon="icon-no" onClick="Base.closeWindow('cloneWin');" />
	</ta:panelButtonBar>
</ta:panel>

<script>
function fnCloneOk(){
	var o = Base.getGridSelectedRows("cloneGrid");
	if(o && o.length == 1){
		var sdata = Base.getGridSelectedRows("positionPersonalGrid");
		var str = "";
		for(var i = 0 ; i < sdata.length ; i ++){
			str += sdata[i].positionid + ",";
		}
		str = str.substring(0,str.length-1);
		Base.submit("","personalPositionMgAction!clonePermissions.do",{"dto['positionids']":str,"dto['positionid']":o[0].positionid},null,null,function(){
			Base.alert("复制权限成功","success");
			Base.closeWindow("cloneWin");
		})
	}else{
		Base.alert("请选择被复制使用权限的人员！","warn");
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>