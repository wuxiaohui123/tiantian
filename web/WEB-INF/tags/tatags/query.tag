<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.Random"%>

<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='query标签，必须包含basicfild子标签，可以包含addtionfield以及其他容器标签' display-name="query" %>
<%@attribute description='query组件id' name='id' required='true' type='java.lang.String' %>
<%@attribute description='设置该容器的CSS中class样式，例如 cssClass="edit-icon"' name='cssClass' type='java.lang.String' %>
<%@attribute description='设置该容器的CSS中style样式，例如 cssStyle="font-size:12px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='手动设置查询的function，设置该属性后，提交前将不再进行自动校验，只写方法名，不加括号，例如queryMethod="query"' name='queryMethod' type='java.lang.String' %>
<%@attribute description='提交的url' name='url' type='java.lang.String' %>
<%@attribute description='提交的其他参数，必须为json格式，例如otherParam="{"dto["pid"]":"123"}"' name='otherParam' type='java.lang.String' %>
<%@attribute description='提交的执行成功的回调function，只写方法名，不加括号，例如successCallback="doSucc"，回调函数的格式function doSucc(data){.....}' name='successCallback' type='java.lang.String' %>
<%@attribute description='提交的执行失败的回调function，只写方法名，不加括号，例如failureCallback="failureCallback"，回调函数的格式function doFail(data){.....}' name='failureCallback' type='java.lang.String' %>
<%@attribute description='提交前手动检查function，如果返回false将不再提交,必须返回true或false，只写方法名，不加括号，例如validator="test"  function  test(){ ...;return true;}' name='validator' type='java.lang.String' %>
<%@attribute description='默认false ，是否进行自动校验，如果校验失败将不再提交' name='autoValidate' type='java.lang.String' %>
<%@attribute description='需要进行分页查询的表格ID，用于提交表格分页信息，多个DataGrid ID使用逗号分隔' name='targetGrid' type='java.lang.String' %>
<%@attribute description='点击重置按钮时需要重置的dataGrid Ids ,以逗号分隔' name='resetGridIds' type='java.lang.String' %>
<%@attribute description='指定全局划分的列数，默认为4' name='cols' type='java.lang.String' %>
<%@attribute description='重置完成后的回调函数，只写方法名，不加括号,例如resetCallback="fnResetCallback"' name='resetCallback' type='java.lang.String' %>
<%@attribute description='查询的热键，如果只输入一个英文字母默认是atl+字母的组合，还可以输入ctrl+q这样的值' name='queryHotKey' type='java.lang.String' %>
<%@attribute description='重置的热键，如果只输入一个英文字母默认是atl+字母的组合，还可以输入ctrl+r这样的值' name='resetHotKey' type='java.lang.String' %>
<%--@doc--%>
<%
if ((id == null || id.length() == 0)) {
	Random random = new Random();
	int nextInt = random.nextInt();
	nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
	this.id = "taquery_" + String.valueOf(nextInt);
}
if (queryHotKey!=null) {
    if(queryHotKey.length()==1)queryHotKey = "Alt+"+queryHotKey;
}
if (resetHotKey!=null) {
    if(resetHotKey.length()==1)resetHotKey = "Alt+"+resetHotKey;
}
Map map = new HashMap();
map.put("queryMethod", queryMethod);
map.put("queryHotKey", queryHotKey);
map.put("resetHotKey", resetHotKey);
map.put("resetGridIds", resetGridIds);
map.put("resetCallback", resetCallback);
map.put("targetGrid", targetGrid);
map.put("url", url);
map.put("otherParam", otherParam);
map.put("validator", validator);
map.put("autoValidate", autoValidate);
map.put("successCallback", successCallback);
map.put("failureCallback", failureCallback);
map.put("id", id);
jspContext.setAttribute("_query_map",map,PageContext.REQUEST_SCOPE);
jspContext.setAttribute("_query_object",this,PageContext.REQUEST_SCOPE);
if (cols == null) {cols="4"; jspContext.setAttribute("cols", "4");};
%>
<form method="post" 
id="<%=id %>" 	      
<% if (cssStyle != null){%>
  style="${cssStyle}" 	 
<%}%>
  class="queryForm
<% if (cssClass != null){%>
  ${cssClass} 	 
<%}%>
" 
<% if (cols != null){%>
  cols="${cols}" 	
<%}else{%>
  cols="4" 
<%}%>
> 
<jsp:doBody/> 
</form>