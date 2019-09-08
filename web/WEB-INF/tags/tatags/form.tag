<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='类似于表单form元素,form标签禁止嵌套使用,一个页面可以有多个form标签' display-name='form' %>
<%@attribute name='id'   description='组件id页面唯一'  type='java.lang.String' %>
<%@attribute name='cols' description='当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认值为1,表示分为一列'  type='java.lang.String' %>
<%@attribute name='columnWidth' description='设置父容器layout为column布局的时候自定义占用容器行宽度百分比，可设置值为0-1之间的小数，如:0.1则表示占该行的1/10' type='java.lang.String' %>
<%@attribute name='cssClass' description='给该组件添加自定义样式class，如:cssClass="no-padding"'  type='java.lang.String' %>
<%@attribute name='cssStyle' description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"表示容器顶部向内占用10个像素' type='java.lang.String' %>
<%@attribute name='layout' description='设置该容器对子组件的布局类型，有column/border，column为按列布局，border按中西南北中布局，默认为column布局并分为一列，如要制定列数设置cols属性,当设置layout属性为border布局时，需要用到layoutCfg属性来配置相关布局参数' type='java.lang.String' %>
<%@attribute name='layoutCfg' description='当设置layout为border布局的时候布局的参数配置，如:layoutCfg="{leftWidth:200,topHeight:90,rightWidth:200,bottomHeight:100}"分别代表东西南北的宽度高度' type='java.lang.String' %>
<%@attribute name='span' description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' type='java.lang.String' %>
<%@attribute name='fit' description='是否自动适应剩余高度,如果设置为true，那么该组件的所有父辈组件都要设置fit为true或height为固定值。&lt;/br&gt;该组件兄弟组件间只能有一个设置fit=true。&lt;/br&gt;如果兄弟组件在后面且可见，那么需要设置heightDiff高度补差' type='java.lang.String' %>
<%@attribute name='heightDiff' description='true/false ,当fit设置为true的时候组件底部高度补差，后同级后面的组件留下一定高度，如:heightDiff="100",不需要加px' type='java.lang.String' %>
<%@attribute name='enctype' description='设置form提交的类型，如果是文件上传必须设置为multipart/form-data，一般不设置' type='java.lang.String' %>
<%@attribute name='action' description='form提交的地址，一般不需要设置' type='java.lang.String' %>
<%@attribute name='methord' description='当需要使用表单提交方法时使用，如文件上传，可以设置post或get方式' type='java.lang.String' %>
<%--@doc--%>
<%
if (null == methord) {
	methord = "POST";
}
if ((id == null || id.length() == 0)) {
	Random random = new Random();
	int nextInt = random.nextInt();
	nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
	id = "taform_" + String.valueOf(nextInt);
}
%>
<%@include file="../columnhead.tag" %>
<form method="post"
  id="<%=id %>"
<% if (action != null){%>
  action="${action}"
<%}%>
<% if (enctype != null){%>
  enctype="${enctype}"
<%}%>
<% if (cols != null){%>
  cols="${cols}"
<%}%>
<% if (span != null){%>
  span="${span}"
<%}%>
<% if (fit != null){%>
  fit="${fit}"
<%}%>
<% if (heightDiff != null){%>
  heightDiff="${heightDiff}"
<%}%>
<% if (layoutCfg != null){%>
  layoutCfg="${layoutCfg}"
<%}%>
<% if (cssStyle != null){%>
  style="${cssStyle}"
<%}%>
<% if (cssClass != null){%>
  class="${cssClass}"
<%}%>
<% if (columnWidth != null){%>
  columnWidth="${columnWidth}"
<%}%>	
<% if (methord != null){%>
  method="<%=methord %>"
<%}%>      
>
<jsp:doBody/>
<div style="clear:both"></div>
</form>		
<%@include file="../columnfoot.tag" %>	