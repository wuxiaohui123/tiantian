<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random" %>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='tab页，只能放在tabs容器内' display-name='tab' %>
<%@attribute description='当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认不设置为1' name='cols' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='组件id' name='id' type='java.lang.String' %>
<%@attribute description='组件的label标签' name='key' type='java.lang.String' %>
<%@attribute description='设置该容器对子组件的布局类型，有column/border，默认为column，cols=1' name='layout' type='java.lang.String' %>
<%@attribute description='设置layout为border布局的时候布局的参数配置，如:layoutCfg="{leftWidth:200,topHeight:90,rightWidth:200,bottomHeight:100}"' name='layoutCfg' type='java.lang.String' %>
<%@attribute description='true/false.该tab页是否有关闭小按钮，默认为false' name='closable' type='java.lang.String' %>
<%@attribute description='true/false.该tab页是否可用，默认为true' name='enable' type='java.lang.String' %>
<%@attribute description='tab页标题的图标class，如icon="icon-add"，可以到icon.css查看' name='icon' type='java.lang.String' %>
<%@attribute description='tab是否被选中' name='selected' type='java.lang.String' %>
<%--@doc--%>
<%
		if ((this.id == null || this.id.length() == 0)) {
			int nextInt = new Random().nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
			id = "tatab_" + String.valueOf(nextInt);
		}
 %>
 
<div 
id="<%=id %>" 
	<% if( key != null ){ %>
title="${key}" 
	<%}%>	
	<% if( closable != null ){ %>
closable="${closable}" 
	<%}%>
	<% if( cssStyle != null ){ %>
style="${cssStyle}" 
	<%}%>
	<% if( cssClass != null ){ %>
class="${cssClass}" 
	<%}%>
	<% if( icon != null ){ %>
icon="${icon}" 
	<%}%>
	<% if( selected != null ){ %>
selected="${selected}" 
	<%}%>
	<% if( enable != null ){ %>
enable="${enable}" 
	<%}%>
	<% if( layout != null ){ %>
layout="${layout}" 
	<%}%>
	<% if( layoutCfg != null ){ %>
layoutCfg="${layoutCfg}" 
	<%}%>
	<% if( cols != null ){ %>
cols="${cols}" 
	<%}%>
>		

<jsp:doBody />

</div>