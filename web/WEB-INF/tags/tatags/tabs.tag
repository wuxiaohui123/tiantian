<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random" %>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='tab页的容器' display-name='tabs' %>

<%@attribute description='当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认不设置为1' name='cols' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='组件id' name='id' type='java.lang.String' %>
<%@attribute description='设置layout为border布局的时候布局的参数配置，如:layoutCfg="{leftWidth:200,topHeight:90,rightWidth:200,bottomHeight:100}"' name='layoutCfg' type='java.lang.String' %>
<%@attribute description='top/left/center/right/bottom，设置父亲容器layout为border布局的时候该组件所在位置' name='position' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='true/false，设置tabs的头部是否无背景，默认false' name='headPlain' type='java.lang.String' %>
<%@attribute description='是否自动适应剩余高度,如果设置为true，那么该组件的所有父辈组件都要设置fit为true或height为固定值。&lt;/br&gt;该组件兄弟组件间只能有一个设置fit=true。&lt;/br&gt;如果兄弟组件在后面且可见，那么需要设置heightDiff高度补差' name='fit' type='java.lang.String' %>
<%@attribute description='true/false ,是否有外边框，默认true,有外边框' name='hasBorder' type='java.lang.String' %>
<%@attribute description='true/false ,当fit设置为true的时候组件底部高度补差，后同级后面的组件留下一定高度，如:heightDiff="100",不需要加px' name='heightDiff' type='java.lang.String' %>
<%@attribute description='Function，当tab被选择的时候触发的事件,传入方法定义（不加括号）,默认传参选中的那个tab的id，如:onSelect="fnSelect"，在javascript中定义函数function fnSelect(tabid){}' name='onSelect' type='java.lang.String' %>
<%@attribute description='Function，当tab被关闭的时候触发的事件,传入方法定义（不加括号）,默认传参选中的那个tab的id，如:onClose="fnClose"，在javascript中定义函数function fnClose(tabid){}' name='onClose' type='java.lang.String' %>
<%@attribute description='指定tabs的宽度，如width="300"' name='width' type='java.lang.String' %>
<%@attribute description='指定tabs的高度,如height="300"' name='height' type='java.lang.String' %>
<%--@doc--%>
<%
		if ("false".equals(hasBorder)) {
			if(cssClass != null){
				cssClass += " tabsnomargin";
			}else{
				cssClass = "tabsnomargin";
			}
		}
		
		/*
		 * 这里对width和height进行处理，如果width或height有值，则在style中加上width和height
		 */
		String tcssStyle = "";
		if(cssStyle != null){
			tcssStyle = cssStyle;
		}
		
		if (width != null) {
			tcssStyle = "width:"+width+"px;"+tcssStyle;
		}
		
		if (height != null) {
			tcssStyle = "height:"+height+"px;"+tcssStyle;
		}
		
		if(tcssStyle.trim().length()>0)
		{
			cssStyle = tcssStyle;
		}				
		if ((this.id == null || this.id.length() == 0)) {
			Random RANDOM = new Random();
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tatabs_" + String.valueOf(nextInt);
		}
 %>
<%@include file="../columnhead.tag" %>
<div layout="tabs"  	
	<% if( id != null ){ %>
id="<%=id %>" 
	<%}%>
	<% if( fit != null ){ %>
fit="${fit}" 
	<%}%>
	<% if( hasBorder != null ){ %>
border="${hasBorder}" 
	<%}%>
	<% if( cols != null ){ %>
cols="${cols}" 
	<%}%>
	<% if( layoutCfg != null ){ %>
layoutCfg="${layoutCfg}" 
	<%}%>
	<% if( span != null ){ %>
span="${span}" 
	<%}%>
   <% if( columnWidth != null ){ %>
columnWidth="${columnWidth}" 	 
   <%}%>	
	<% if( cssStyle != null ){ %>
style="<%=cssStyle%>" 
	<%}%>
	<% if( cssClass != null ){ %>
class="<%=cssClass %>" 
	<%}%>
	<% if( heightDiff != null ){ %>
heightDiff="${heightDiff}" 
	<%}%>
	<% if( headPlain != null ){ %>
plain="${headPlain}" 
	<%}%>
	<% if( width != null ){ %>
width="${width}" 
	<%}%>
	<% if( height != null ){ %>
height="${height}" 
	<%}%>
	<% if( onSelect != null ){ %>
onSelect="${onSelect}" 
	<%}%>
	<% if( onClose != null ){ %>
onClose="${onClose}" 
	<%}%>
>				

<jsp:doBody />

</div>
<%@include file="../columnfoot.tag" %>