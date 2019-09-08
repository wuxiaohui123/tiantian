<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="java.util.Random"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.Map"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%--@doc--%>
<%@tag description='Query基本查询条件组标签,该标签只能作为Query标签的子标签使用,当存在additionfield标签时,该标签必须指定' display-name="basicfield" %>
<%@attribute name='id' description='容器组件在页面上的唯一id'  type='java.lang.String' %>
<%@attribute name='cssClass' description='设置该容器的CSS中class样式，例如 cssClass="edit-icon"'  type='java.lang.String' %>
<%@attribute name='cssStyle' description='设置该容器的CSS中style样式，例如 cssStyle="font-size:12px"'  type='java.lang.String' %>
<%@attribute name='key' description='标题值' type='java.lang.String' %>
<%@attribute name='cols' description='设置该容器划分的列数，例如 cols="2"' type='java.lang.String' %>
<%@attribute name="layout"  type="java.lang.String" description='column/border,设置该容器的布局类型，例如 layout="column"'%>
<%@attribute name='toolButtonsColumnWidth' description='设置工具按钮所占宽度的百分比,例如toolButtonsColumnWidth="0.2"' type='java.lang.String' %>
<%--@doc--%>
<%
Object obj = jspContext.getAttribute("_query_object",PageContext.REQUEST_SCOPE);
JspTag parent = TagUtil.getTa3ParentTag(getParent());
Map m = new HashMap();
if(null != obj && obj.equals(parent)){
  m = (HashMap)jspContext.getAttribute("_query_map",PageContext.REQUEST_SCOPE);
}
if ((id == null || id.length() == 0)) {
  Random random = new Random();
  int nextInt = random.nextInt();
  nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
  id = "tabasicfield_" + String.valueOf(nextInt);
}
PropertyDescriptor pd = new PropertyDescriptor("cols", parent.getClass());
Method method = pd.getReadMethod();
String st = (String)method.invoke(parent);
if (st != null && cols == null) {
	cols = st;
	jspContext.setAttribute("cols", cols);
}
%>
<div id="<%=id %>" 
	class="grid  basicfield 
<% if (cssClass != null){%>
   ${cssClass} 	 
<%}%>
" 
layout="column" 
   <% if (key != null){%>
title="${key}" 	 
   <%}%>      
   <% if (cols != null){%>
   cols="${cols}"  
   <%}else{%>
   cols="4" 	 
<%}%>
   style="height:auto;padding-left:5px;padding-right:5px;padding-top:4px;padding-bottom:0px;
   <% if (cssStyle != null){%>
${cssStyle}" 
   <%}%>
   "    
 <% if (toolButtonsColumnWidth != null){%>
  toolButtonsColumnWidth="${toolButtonsColumnWidth}" 
 <%}%>
>
<jsp:doBody/>
<div id="<%=id %>_ez-fl" class="ez-fl ez-negmx" style="">
<div   class="grid basicfield-tool"  style="height:34px;">
<div  style="float: left;margin-left:15px;height:26px;min-width:185px;margin-top:2px;">
<button id="<%=id %>_query" class="sexybutton" type="button" >
	<span>
		<span>
			<span style="height:16px;width:16px;padding-left: 0px;float: left;margin-top: 3px;" class="xui-icon-query"></span>
			<span>查询</span>
		</span>
	</span>
</button>

<button id="<%=id %>_reset" class="sexybutton" type="button">
	<span>
		<span>
			<span style="height:16px;width:16px;padding-left: 0px;float: left;margin-top: 3px;" class="xui-icon-reset"></span>
			<span>重置</span>
		</span>
	</span>
</button>
<button id="<%=id %>_toggle" class="sexybutton" type="button">
	<span>
		<span>
			<span style="height:16px;width:16px;padding-left: 0px;float: left;margin-top: 3px;" class="xui-icon-slidedown nokey"></span>
			<span >&nbsp;</span>
		</span>
	</span>
</button>
</div>
</div>
</div>
<script>
$(function(){
var pWidth = $("#<%=id %>").width();
$("#<%=id %>_ez-fl").width(pWidth * <%=toolButtonsColumnWidth%>);
 Ta.Query("<%=m.get("id")%>",{
        		queryBtnId:"<%=id %>_query",
        		resetBtnId:"<%=id %>_reset",
        		toggleBtnId:"<%=id %>_toggle",
        		isAutoQuery:<%= m.get("queryMethod") != null?false:true%>,
        		queryMethod:<%= m.get("queryMethod")%>,
        		queryHotKey:<%if(null != m.get("queryHotKey")){%>"<%=m.get("queryHotKey")%>"<%}else{%>null<%}%>,
        		resetHotKey:<%if(null != m.get("resetHotKey")){%>"<%=m.get("resetHotKey")%>"<%}else{%>null<%}%>,
        		resetGridIds:<%if(null != m.get("resetGridIds")){%>"<%=m.get("resetGridIds")%>"<%}else{%>null<%}%>,
        		resetCallback:<%= m.get("resetCallback")%>,
        		targetGrid:<%if(null != m.get("targetGrid")){%>"<%=m.get("targetGrid")%>"<%}else{%>null<%}%>,
        		url:"<%= m.get("url")%>",
        		otherParam:<%= m.get("otherParam")%>,
        		validator:<%= m.get("validator")%>,
        		autoValidate:<%= m.get("autoValidate") != null?m.get("autoValidate"):true%>,
        		successCallback:<%= m.get("successCallback")%>,
        		failureCallback:<%= m.get("failureCallback")%>
 });
  });
</script>
<div style="clear:both"></div>
</div>