<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="java.util.Enumeration"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@attribute description='容器组件在页面上的唯一id' name='id' type='java.lang.String' %>
<%@attribute description='是否显示toolbar' name='display' type='java.lang.String' %>
<%@attribute description='cssClass' name='cssClass' type='java.lang.String' %>
<%@tag description='工具栏' display-name='toolbar' %>
<%--@doc--%>
<%
  final Random RANDOM = new Random();
		if ((id == null || id.length() == 0)) {

			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tatoolbar_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
		}
%>

<div id="${id}" 
class="<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>toolbar<%}else{ %>toolbar toolbar_163<%} %>"
  <%if(display!=null&&display.equals("false")){ %>
  style="display:none;"	 
  <%} %>
>
<center><table cellpadding="0" cellspacing="0"><tr>
<td>
<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>
<div class="toolbarleft"></div>
<%} %>
<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>
<div class="toolbarcenter">
<%}else{ %>
<div class="toolbarcenter toolbarcenter_163">
<%} %>
<jsp:doBody/> 
<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>
</div><div class="toolbarright"></div>
<%} %>
</td></tr></table></center>
</div>
<script>
	(function(){
		var $toolbardiv = $("#${id}");
		$toolbardiv.css('marginTop',(-parseInt($toolbardiv.parent().css('paddingTop')))+'px').css('width',$('body').width());
		var a = $("#${id} center table").eq(0).width();
		$("#${id} center").css('left',($toolbardiv.width()-a)/2).css('z-index',2000).css('position','fixed');
		$(window).resize(function(){
		 	$("#${id} center").css('left',($('body').width()-a)/2);
		});
	})();
</script>