<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:panel id="passInfo" hasBorder="false" expanded="false" fit="true" bodyStyle="padding:10px 10px 0px 0px;" withButtonBar="true">
	<ta:text id="newPassword" key="登录密码" required="true" maxLength="15" type="password"/>
	<ta:text id="rpassword" key="确认密码" required="true" maxLength="15" type="password" validType="compare(this.value, ['=', 'newPassword'])"/>
	<ta:panelButtonBar>
		<ta:button id="saveUserBtn" key="保存[S]" icon="icon-add1" hotKey="S" isok="true" onClick="fnSavePass();"/>
		<ta:button id="closeWinBtn" key="关闭[X]" icon="icon-no"  hotKey="X" onClick="Base.closeWindow('passWin');"/>
	</ta:panelButtonBar>
</ta:panel>
<script>
$(function () {
	Base.focus("newPassword");
});
function fnSavePass() {
	if (Base.getValue("newPassword") == "")
		Base.alert("新密码不能为空！", "warn");
	else if (Base.getValue("newPassword") == Base.getValue("rpassword"))
		Base.submit("passInfo,userGd", "orgUserMgAction!resetPassword.do", null, null, true, function(){Base.closeWindow("passWin");});
	else
		Base.alert("两次输入的密码不一致，请检查！", "warn");
}
</script>
<%@ include file="/ta/incfooter.jsp"%>