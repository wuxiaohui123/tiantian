<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>在线用户情况</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" style="padding:10px;">
	<ta:pageloading />
	<ta:tabs id="userLogonInfo" fit="true" headPlain="true" hasBorder="true">
		<ta:tab id="userLoginLog" key="当前在线用户信息" cssStyle="margin:5px;">
			<ta:fieldset id="userLoginLogParam" key="查询条件" cols="4">
				<ta:text id="name" key="操作员姓名" />
				<ta:text id="clientip" key="客户端IP地址" />
				<ta:text id="serverip" key="服务器地址" />
				<ta:buttonLayout>
					<ta:submit key="查询" icon="icon-search" isShowIcon="true" submitIds="userLoginLogParam" url="userLoginLogAction!queryTaOnlineLog.do" />
				</ta:buttonLayout>
			</ta:fieldset>
			<ta:panel key="用户信息" fit="true">
				<ta:datagrid id="userLoginLogList" haveSn="true" forceFitColumns="true" columnFilter="true" fit="true">
					<ta:datagridItem id="name" key="姓名" width="80" showDetailed="true" />
					<ta:datagridItem id="username" key="用户名" width="80" showDetailed="true" />
					<ta:datagridItem id="telphone" key="联系电话" width="80" showDetailed="true" />
					<ta:datagridItem id="logintime" key="登录时间" dataType="dateTime" showDetailed="true" width="100" />
					<ta:datagridItem id="sessionid" key="SESSION" showDetailed="true" />
					<ta:datagridItem id="clientip" key="客户端IP" showDetailed="true" />
					<ta:datagridItem id="serverip" key="服务器主机及端口" showDetailed="true" />
					<ta:datagridItem id="curresource" key="客户端访问地址" width="180" showDetailed="true" />
				</ta:datagrid>
			</ta:panel>
		</ta:tab>
		<ta:tab id="userHisLogQuery" key="用户登录历史信息查询" cssStyle="margin:5px;">
			<ta:fieldset id="hisQueryParam" key="查询条件" cols="4">
				<ta:text id="q_name" key="操作员姓名" />
				<ta:text id="q_clientip" key="客户端IP地址" />
				<ta:text id="q_serverip" key="服务器地址" />
				<ta:buttonLayout>
					<ta:submit key="查询" icon="icon-search" isShowIcon="true" submitIds="hisQueryParam" url="userLoginLogAction!queryTaUserLoginHisLog.do" />
				</ta:buttonLayout>
			</ta:fieldset>
			<ta:fieldset cols="2">
				<ta:text id="logoncount" key="累计登录次数合计" readOnly="true" labelWidth="140" />
				<ta:text id="onlinetimeSum" key="累计上线时间合计" readOnly="true" labelWidth="140" />
			</ta:fieldset>
			<ta:panel key="用户登录历史信息" fit="true">
				<ta:datagrid id="hisUserLogList" haveSn="true" columnFilter="true" fit="true">
					<ta:datagridItem id="name" key="姓名" width="90" />
					<ta:datagridItem id="logintime" key="登录时间" width="140" />
					<ta:datagridItem id="logouttime" key="下线时间" width="140" />
					<ta:datagridItem id="onlinetime" key="在线时长（秒）" width="120" />
					<ta:datagridItem id="serverip" key="服务器主机及端口" width="220" />
					<ta:datagridItem id="username" key="用户名" width="100" />
					<ta:datagridItem id="telphone" key="联系电话" width="80" />
					<ta:datagridItem id="clientip" key="客户端主机" width="180" />
				</ta:datagrid>
			</ta:panel>
		</ta:tab>
	</ta:tabs>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>