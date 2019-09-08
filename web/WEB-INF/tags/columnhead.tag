<%
	if ("true".equals((String)jspContext.getAttribute("fit")))
		TagUtil.columnlayout(TagUtil.getTa3ParentTag(getParent()), jspContext, span, columnWidth, "true");
	else 
		TagUtil.columnlayout(TagUtil.getTa3ParentTag(getParent()), jspContext, span, columnWidth, "");
		
%>
<%if (jspContext.getAttribute("doColumnLayout") !=null) {%>
<div class='ez-fl ez-negmx'
<%if (jspContext.getAttribute("doFitLayout") !=null) {%>
 fit="true"
<%} %>
 style='width:${doColumnLayout}'>
<%} %>