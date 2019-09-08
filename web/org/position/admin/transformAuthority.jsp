<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<ta:text id="positionid" display="false"/>
<ta:panel fit="true" hasBorder="false" withButtonBar="true">
	<ta:datagrid fit="true" id="userGrid" haveSn="true" selectType="radio">
		<ta:datagridItem id="positionid" key="岗位id" hiddenColumn="true" asKey="true"></ta:datagridItem>
		<ta:datagridItem id="loginid" key="登录号"></ta:datagridItem>
		<ta:datagridItem id="name" key="姓名"></ta:datagridItem>
		<ta:datagridItem id="sex" key="性别" collection="sex"></ta:datagridItem>
		<ta:datagridItem id="orgnamepath" key="所属组织路径" width="300"></ta:datagridItem>
	</ta:datagrid>
	<ta:panelButtonBar align="center">
		<ta:button key="确定" onClick="fnSave()" isok="true" icon="icon-add1"/>
		<ta:button key="关闭" icon="icon-no" onClick="Base.closeWindow('transformWin')"/>
	</ta:panelButtonBar>
</ta:panel>
<script type="text/javascript">
function fnSave(){
	var sdata = Base.getGridSelectedRows("userGrid");
	if(sdata && sdata.length == 1){
		Base.submit("positionid,userGrid","adminUserMgAction!transformAuthority.do",null,false,false,function(){
			Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>转移权限成功</div>");
			Base.closeWindow("transformWin");
			Base.submit("","adminUserMgAction!queryAdminMgUsers.do");
		});
	}else{
		Base.alert("请选择后再进行转移权限操作","warn");
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>