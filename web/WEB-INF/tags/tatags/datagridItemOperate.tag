<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%--@doc--%>
<%@tag description='datagrid列操作按钮区域' display-name='datagridItem' %>
<%@attribute name="showAll" description='是否显示' required="true"%>
<%@attribute name="id" description='列id' required="true"%>
<%@attribute name="name" description='列名'%>
<%--@doc--%>
<%--media --%>
var c_${id} = {};
c_${id}.id = "${id}";
c_${id}.name = "${name}";
c_${id}.operate = true;
c_${id}.width = Number(50);
if (o.defaultEvent == null) 
	o.defaultEvent = [];
var id_operate_${id}_handler_show = 'showGridItemOperate';
var id_operate_${id}_handler_hide = 'hideGridItemOperate';
if ('${showAll}' == 'true'){
	id_operate_${id}_handler_show = 'showGridItemOperateAll';
	id_operate_${id}_handler_hide = 'hideGridItemOperateAll';
}

//o.defaultEvent.push({
//	id : '${id}',
//	name: 'onMouseEnter',
//	handler : id_operate_${id}_handler_show
//});
//o.defaultEvent.push({
//	id : '${id}',
//	name: 'onMouseLeave',
//	handler : id_operate_${id}_handler_hide
//});
c.push(c_${id});
<jsp:doBody/>