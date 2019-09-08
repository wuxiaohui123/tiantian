<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='用于输出tr标签' display-name='tr' %>
<%@attribute description='指定tr元素的id' name='id' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='指定表格行的内容对齐方式' name='align' type='java.lang.String' %>
<%@attribute description='指定表格行中内容的垂直对齐方式' name='valign' type='java.lang.String' %>
<%--@doc--%>
<%
		if((this.id == null || this.id.length() == 0)){
		Random RANDOM = new Random();
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			this.id = "tr_" + String.valueOf(nextInt);
		}
 %>

<tr   
<% if(!ValidateUtil.isEmpty(id)){%>
    id="<%=id %> " 
<%}%>
<% if(!ValidateUtil.isEmpty(align)){%>
   align="${align}"  
<%}%>
<% if(!ValidateUtil.isEmpty(valign)){%>
   valign="${valign}"; 
<%}%>
<% if(!ValidateUtil.isEmpty(cssStyle)) {%>
    style="${cssStyle}"    
<%}%>
<% if(!ValidateUtil.isEmpty(cssClass)) {%>
    class="${cssClass} "
<%}%>
>
<jsp:doBody />
		<div style="clear:both"></div>
</tr>