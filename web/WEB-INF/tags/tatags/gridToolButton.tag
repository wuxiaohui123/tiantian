<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Enumeration"%>
<%--@doc--%>
<%@tag description='datagrid分页条上的按钮组件' display-name='gridToolButton' %>
<%@attribute description='设置快捷键' name='hotKey' type='java.lang.String' %>
<%@attribute description='设置按钮图标' name='icon' type='java.lang.String' %>
<%@attribute description='设置组件id' name='id' type='java.lang.String' %>
<%@attribute description='设置标题，不支持html格式文本' name='key' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='单击事件' name='onClick' type='java.lang.String' %>
<%--@doc--%>

var buttonStr = "";
buttonStr += '<button id="${id}" class="sexybutton toolbarbt ${cssClass}" type="button" <% if( null != hotKey) {%> hotKey="${hotKey}" <% }%><% if( null != cssStyle) {%> style="${cssStyle}" <% }%>';
<% if( null != onClick) {%>
buttonStr += " onClick=\"${onClick}\"";
<% }%>
buttonStr += '>';
buttonStr += '	<span>';
buttonStr += '		<span>';
				<% if( null != icon) {%>
buttonStr += '			<span style="height:16px;width:16px;padding-left: 0px;float: left;margin-top: 3px;" class="${icon}"></span>';
				<% }%>
				<% if( null != key){ %>
buttonStr += '				<span> ${key}';
				<% }%>	
			<% if( null != icon) {%>			
buttonStr += '			</span>';
			<% }%>
buttonStr += '		</span>';
buttonStr += '	</span>';
buttonStr += '</button>';
o.pagingOptions.buttons.push($(buttonStr));