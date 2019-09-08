<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='用于存放按钮的容器' display-name='buttonLayout' %>
<%@attribute description='center/left/right，默认center。设置容器内button的对齐方式，例如:align="center"' name='align' type='java.lang.String' %>
<%@attribute description='设置组件id，页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='数字，当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%--@doc--%>
<%
  if ((id == null || id.length() == 0)) {
		Random random = new Random();
	    int nextInt = random.nextInt();
		nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
				.abs(nextInt);
		id = "tabuttonlayout_" + String.valueOf(nextInt);
  }
  if(cssClass==null){
			cssClass = "button-panel";
		}else{
			cssClass = "button-panel "+cssClass;
		}
		if("right".equals(align) || "left".equals(align)){
			cssClass += " "+align;
		}else{
			cssClass += " center";
  }
%>

<%@include file="../columnhead.tag" %>

<div	
<% if (id != null){%>
  id="<%=id %>"	
<%}%>
<% if (cssClass != null){%>
  class="<%=cssClass %>"	
<%}%>
<% if (cssStyle != null){%>
  style="${cssStyle}"	 
<%}%>
<% if (span != null){%>
  span="${span}"	 
<%}%>	
<% if (columnWidth != null){%>
  columnWidth="${columnWidth}"	 
<%}%>
>
<jsp:doBody/>
</div>
<%@include file="../columnfoot.tag" %>