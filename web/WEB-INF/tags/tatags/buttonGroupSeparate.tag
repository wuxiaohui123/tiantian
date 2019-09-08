<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%--@doc--%>
<%@tag description='buttonGroupSeparate,只能存放于buttonGroup中，用于隔开group中的按钮' display-name="buttonGroupSeparate" %>
<%@attribute description='设置组件id，页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%--@doc--%>
<%
if(cssClass == null){
	cssClass = "buttonGroupSeparate";
}else{
	cssClass = "buttonGroupSeparate" + cssClass;
}
jspContext.setAttribute("cssClass",cssClass);
%>
<div 
<%if(id != null){ %>
id="${id}" 
<%} %>
class="${cssClass}"
<% if (cssStyle != null){%>
  style="${cssStyle}"	 
<%}%>
>
</div>