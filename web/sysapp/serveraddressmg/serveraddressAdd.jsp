<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:panel id="panel" bodyStyle="padding:5px" fit="true" hasBorder="false" withButtonBar="true">
		<ta:text id="address" key="应用地址"  required="true"/>
		<ta:selectInput id="canuse" key="是否启用" collection="YESORNO" value="0"/>
	<ta:panelButtonBar> 
		<ta:submit id="saveBtn" key="保存[S]" hotKey="s" icon="icon-save" submitIds="panel" url="serverAddressAction!save.do" successCallBack="function(){Base.closeWindow('userwin1');}"/>
		<ta:button key="关闭[C]" hotKey="alt+c" icon="icon-remove" onClick="Base.closeWindow('userwin1')"/>
	</ta:panelButtonBar>
</ta:panel>
<%@ include file="/ta/incfooter.jsp"%>