<%-- @doc --%>
<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag description='页面加载显示' display-name='pageloading'%>

<%@attribute description='提示信息' name='value' type='java.lang.String' %>
<%-- @doc --%>
<div id="pageloading">
	<div class="pageloading" ></div>
	<div class="pageloading-text">
<%if(value!=null){%>
	${value}
<% }else{%>
	加载中。。。
<%}%>
	</div>
</div>