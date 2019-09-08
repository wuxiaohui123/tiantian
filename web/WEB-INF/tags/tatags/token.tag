<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.WebUtil"%>
<%@tag import="com.yinhai.webframework.MySubmitKeyHelper"%>

<%@tag description='防重复提交' display-name='button' %>
<%@attribute description='keyid' name='id' type='java.lang.String' %>

<%
String setSubmitKey = MySubmitKeyHelper.setSubmitKey(id + "_" + MySubmitKeyHelper.TA_SUBMITKEY);
%>

<input name="${id}_<%=MySubmitKeyHelper.TA_SUBMITKEY %>" type="hidden" value="<%=setSubmitKey %>"/>
<input id="${id}_tokenflag" name="<%=MySubmitKeyHelper.TA_SUBMITKEYTOKENID %>" type="hidden" value="${id}_<%=MySubmitKeyHelper.TA_SUBMITKEY %>"/>