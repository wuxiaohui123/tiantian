<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.beans.PropertyDescriptor"%>

<%--@doc--%>
<%@tag description='datagrid列操作按钮区域' display-name='datagridItem' %>
<%@attribute name="icon" description='图标' required="true"%>
<%@attribute name="name" description='菜单名称' required="true"%>
<%@attribute name="click" description='点击事件' required="true"%>
<%--@doc--%>
<%--media --%>
<%

try {
	PropertyDescriptor pd = new PropertyDescriptor("id", getParent().getClass());
	String gridItem = (String)pd.getReadMethod().invoke(getParent());
	if (gridItem != null){
		jspContext.setAttribute("gridItem", gridItem);
	}
} catch (Exception e){
	jspContext.setAttribute("gridItem", "");
}
%>
if (c_${gridItem}.operateMenus == null)c_${gridItem}.operateMenus = [];
c_${gridItem}.operateMenus.push({name:'${name}', click:${click}})