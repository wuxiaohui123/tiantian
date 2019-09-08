<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="java.util.Random"%>

<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='用于输出table标签' display-name='tableView' %>
<%@attribute description='指定table的ID' name='id' required='true' type='java.lang.String' %>
<%@attribute description='指定是否有外部边框，默认为true' name='hasOuterBorder' type='java.lang.String' %>
<%@attribute description='给该组件外部容器添加自定义样式，如:outerCssStyle="padding-top:10px"' name='outerCssStyle' type='java.lang.String' %>
<%@attribute description='给该组件外部容器添加自定义样式class，如:outerCssClass="no-padding"' name='outerCssClass' type='java.lang.String' %>
<%@attribute description='给该table添加自定义样式class，如:tableCssClass="no-padding"' name='tableCssClass' type='java.lang.String' %>
<%@attribute description='给该table添加自定义样式，如:tableCssStyle="padding-top:10px"' name='tableCssStyle' type='java.lang.String' %>
<%@attribute description='指定table的标题值' name='key' type='java.lang.String' %>
<%@attribute description='指定表格单元格之间的空隙，默认值为0' name='cellspacing' type='java.lang.String' %>
<%@attribute description='指定单元边沿与单元内容之间的空间，默认值为0' name='cellpadding' type='java.lang.String' %>
<%@attribute description='定义table边框的宽度' name='border' type='java.lang.String' %>
<%@attribute description='定义table的宽度' name='width' type='java.lang.String' %>
<%--@doc--%>
<% 
		if ((this.id == null || this.id.length() == 0)) {
			Random RANDOM = new Random();
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			this.id = "tatableview_" + String.valueOf(nextInt);
		}
%>
<div  style="<% if(!ValidateUtil.isEmpty(width)) {%>width:${width}px;<%}%><% if(!ValidateUtil.isEmpty(outerCssStyle)){%>${outerCssStyle}<%}%>"
<%  if(!ValidateUtil.isEmpty(outerCssClass)){%>
      
       <% if("true".equals(hasOuterBorder)) {%>
          class="tableView-default ${outerCssClass}"  
      <%}else{%>
          class="tableView-no-border   ${outerCssClass}"    
      <%}%>
   <%}else{%>
           <%   if("true".equals(hasOuterBorder)) {%>
            class="tableView-default"    
          <%}else{%>
            class="tableView-no-border"  
          <%}%>
<%}%>
>
<table   width="100%"  
<%  if(!ValidateUtil.isEmpty(tableCssStyle)) {%>
     style="table-layout:fixeud;border-collapse:collapse;border-color:#ededed;${tableCssStyle}" 
    <%}else{%>
     style="table-layout:fixeud;border-collapse:collapse;border-color:#ededed;"  
 <%}%>
 <%  if(!ValidateUtil.isEmpty(tableCssClass)) {%>
     class="${tableCssClass}"   
 <%}%>
<%  if(!ValidateUtil.isEmpty(id)) {%>
    id="<%=id %>" 
<%}%>
<%  if(!ValidateUtil.isEmpty(cellspacing)) {%>
   cellspacing="${cellspacing}" 
<%}else{%>
   cellspacing="0"  
<%}%>

<% if(!ValidateUtil.isEmpty(cellpadding)) {%>
   cellpadding="${cellpadding}" 
<%}else{%>
   cellpadding="0"  
<%}%>
<%  if(!ValidateUtil.isEmpty(border)) {%>
   border="${border}" 
<%}else{%>
   border="0"  
<%}%>
>
<%   if(!ValidateUtil.isEmpty(key)) {%>
  <caption    class="tableView-title" >${key}</caption>
<%}%>

<jsp:doBody />
</table>
</div>