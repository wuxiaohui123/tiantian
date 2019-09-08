<%@tag pageEncoding="UTF-8"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="java.util.Random"%>
<%--@doc--%>
<%@tag description='Query附加条件标签,该标签只能作为Query标签的子标签使用,当有该标签出现时,Query标签的工具栏会出现一个展开/收回按钮,用来控制该标签内所有组件的显示/隐藏' display-name="addtionfield" %>
<%@attribute name='id' description='容器组件在页面上的唯一id'  type='java.lang.String' %>
<%@attribute name='key' description='标题值' type='java.lang.String' %>
<%@attribute name='cols' description='当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认值为1,表示分为一列'  type='java.lang.String' %>
<%@attribute name='cssClass' description='给该组件添加自定义样式class，如:cssClass="no-padding"'  type='java.lang.String' %>
<%@attribute name='cssStyle' description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"表示容器顶部向内占用10个像素' type='java.lang.String' %>
<%@attribute name='layout' description='设置该容器对子组件的布局类型，有column/border，column为按列布局，border按中西南北中布局，默认为column布局并分为一列，如要制定列数设置cols属性,当设置layout属性为border布局时，需要用到layoutCfg属性来配置相关布局参数' type='java.lang.String' %>


<%--@doc--%>
<%
if ((id == null || id.length() == 0)) {
	Random random = new Random();
    int nextInt = random.nextInt();
	nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
	this.id = "taaddtionfield_" + String.valueOf(nextInt);
}
JspTag parent = TagUtil.getTa3ParentTag(getParent());
PropertyDescriptor pd = new PropertyDescriptor("cols", parent.getClass());
Method method = pd.getReadMethod();
String st = (String)method.invoke(parent);
if (st != null && cols == null) {
	cols = st;
	jspContext.setAttribute("cols", cols);
}
%>

<div id="<%=id %>" 
	class="grid  addtionfield 
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
style="display: none;height:auto;padding-left:5px;padding-right:5px;padding-top:0px;padding-bottom:0px; 
   <%if(cssStyle != null){%>
${cssStyle}" 
<%}%>
   " 
> 
<jsp:doBody/> 
<div style="clear:both"></div>
</div>