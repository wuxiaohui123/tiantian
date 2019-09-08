<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%-- @doc --%>
<%@tag description='panel的按钮栏在panel体内下方显示，只能在panel容器内使用' display-name='panelButtonBar' %>
<%@attribute description='设置按钮位置（left,center,right,默认right）' name='align' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"表示容器顶部向内占用10个像素' name='cssStyle' type='java.lang.String' %>
<%-- @doc --%>
<%		
		String border = "false";
		try{
			JspTag parent = getParent();
			PropertyDescriptor pd1 = new PropertyDescriptor("hasBorder", parent.getClass());
			Method method1 = pd1.getReadMethod();
			border = (String)method1.invoke(parent);
		}catch(Exception e){
		}
		if(cssClass==null){
			this.cssClass = "panel-button";
		}else{
			this.cssClass = "panel-button "+this.cssClass;
		}
		if("center".equals(align) || "left".equals(align) || "right".equals(align)){
			this.cssClass += " "+align;
		}
		
 %><div style="clear:both"></div>
</div>

<div 
	<% if( cssClass != null){%>
	 class="<%=cssClass %>
	<%}%>
	<% if( "false".equals(border)){%>
	 panelnoborder	
	<%}%>	
	" 
	<% if( cssStyle != null){%>
	 style="<%=cssStyle%>" 	 
	<%}%>
> 
<jsp:doBody />
</div> 
