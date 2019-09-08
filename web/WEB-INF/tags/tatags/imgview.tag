<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%-- @doc --%>
<%@tag description='图片展示' display-name='imgview' %>
<%@attribute description='组件id' name='id' type='java.lang.String' %>
<%@attribute description='图片路径' name='src' type='java.lang.String' %>
<%@attribute description='图片长度' name='width' type='java.lang.String' %>
<%@attribute description='图片宽度' name='height' type='java.lang.String' %>
<%@attribute description='图片名称' name='alt' type='java.lang.String' %>
<%-- @doc --%>
<%
	String span =null;
	String columnWidth = null;
%>
<%@include file="../columnhead.tag" %>
<div 
<% if( null !=width){%>
width="${width}" 
<%}%>
class="zoom" 
>
<div 
class="zoom_pic"
>
<img 
<% if( null !=id){%>
id="${id}" 
<%}%>
<% if( null !=alt){%>
alt="${alt}" 
<%}%>
class="z_pic" 
<% if( null !=height){%>
height="${height}" 
<%}%>
 <% if( null !=width){%>
width="${width}" 
<%}%>
/>	
</div>
<div 
class="zoom_name" 
>
<% if( null !=alt){%>
${alt}
<%}%>
</div>
</div>
<%@include file="../columnfoot.tag" %>
<script>
<% if( null !=src){%>
	$("#${id}").attr("src",Base.globvar.contextPath+"/${src}");
<%}%>
$(".z_pic").xzy_zoom(600,450,5,200);  
</script>