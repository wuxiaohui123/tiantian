<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%-- @doc --%>
<%@tag description='panel的工具栏在panel体内上方显示，只能在panel容器内使用' display-name='panelToolBar' %>
<%@attribute description='设置按钮位置（left,center,right,默认left）' name='align' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"表示容器顶部向内占用10个像素' name='cssStyle' type='java.lang.String' %>
<%-- @doc --%>
<%
		Map taPanel = null;
		Object obj = jspContext.getAttribute("taPanel_object",PageContext.REQUEST_SCOPE);
		JspTag tag = getParent();
		if(obj != null && obj.equals(tag)){
			taPanel = (HashMap)jspContext.getAttribute("taPanel",PageContext.REQUEST_SCOPE);
		}
		if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
			if(cssClass==null){
				this.cssClass = "panel-toolbar panel-toolbar-163";
			}else{
				this.cssClass = "panel-toolbar panel-toolbar-163"+this.cssClass;
			}
		}else{
			if(cssClass==null){
				this.cssClass = "panel-toolbar";
			}else{
				this.cssClass = "panel-toolbar "+this.cssClass;
			}			
		}
		if(taPanel != null && taPanel.get("key")  == null){
			this.cssClass = this.cssClass+" panelnotitle";
		}
		if("center".equals(align) || "left".equals(align) || "right".equals(align)){
			this.cssClass += " "+align;
		}
		if(taPanel !=null && "false".equals(taPanel.get("hasBorder"))){
			this.cssClass += " panelnoborder";
		}
%>
<div 
	<% if(cssClass !=null){ %>
	 class="<%=cssClass %>"	
	<%} %>
	<% if(null !=cssStyle) {%>
	 style="${cssStyle}"	 
	<%}%>
> 

<jsp:doBody />
</div> 

<div  
<% if(taPanel != null){%> 
	 	<% if(null != taPanel.get("withButtonBar") && "true".equals(taPanel.get("withButtonBar"))) {%>
	 	
		 	<% if(null !=taPanel.get("bodyStyle")) {%>
			 style="<%= taPanel.get("bodyStyle") %>"
			<%}%>
			class="panel-body panel-width-buttonpanel 
			<% if(("false").equals(taPanel.get("hasBorder"))) {%>
			panelnoborder 
			<%}%>
			<% if(null !=taPanel.get("bodyClass")) {%>
			  <%= taPanel.get("bodyClass") %>
			<%}%>					
			" 
			<% if(null !=taPanel.get("layout")) {%>
			 layout="<%=taPanel.get("layout")%>" 
				<% if(null !=taPanel.get("cols")) {%>
			 	cols="<%=taPanel.get("cols")%>" 
				<%}%>			 
				<% if(null !=taPanel.get("layoutCfg")) {%>
			 	layoutCfg="<%=taPanel.get("layoutCfg")%>" 
				<%}%>	
			<%}%>
		<%}else{ %>
			<% if(null !=taPanel.get("bodyStyle") ){%>
			 style="<%=taPanel.get("bodyStyle")%>" 
			<%}%>
			class="panel-body 
			<% if(("false").equals(taPanel.get("hasBorder")) ){%>
			 panelnoborder 
			<%}%>			
			<% if(null !=taPanel.get("bodyClass") ){%>
			   <%=taPanel.get("bodyClass")%> 
			<%}%>
			" 
			<% if(null !=taPanel.get("layout") ){%>
			 layout="<%=taPanel.get("layout")%>" 
				<% if(null !=taPanel.get("cols") ){%>
			 	cols="<%=taPanel.get("cols")%>" 
				<%}%>			 
				<% if(null !=taPanel.get("layoutCfg") ){%>
			 	layoutCfg="<%=taPanel.get("layoutCfg")%>" 
				<%}%>	
			<%}%>			
		<%}%>
<%}else{ %>
   class="panel-body"
<%}%>
>