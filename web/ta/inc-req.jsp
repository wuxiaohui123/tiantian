<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.sysframework.util.IConstants"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>
<link rel="stylesheet" type="text/css" href="${basePath}ta/resource/themes/base/ta-icon-all.css" />
<link rel="stylesheet" type="text/css" href="${basePath}ta/resource/themes/base/ta-item-all.css" />
<%@include file="/ta/inc-theme.jsp"  %>
<link rel="stylesheet" type="text/css" href="${basePath}ta/resource/themes/base/zTreeStyle/zTreeStyle.css" />
<link rel="stylesheet" type="text/css" href="${basePath}ta/resource/themes/base/zTreeStyle/zTreeIcons.css" />
<script>
var Base = {globvar:{}};
Base.globvar.contextPath = "<%=request.getContextPath()%>";
Base.globvar.basePath = "<%=basePath%>";
Base.globvar.developMode = <%=AppManager.getSysConfig("developMode")%>;
Base.globvar.pageSize = <%=AppManager.getSysConfig("pageSize")%>;
Base.globvar.cols_360 = <%=AppManager.getSysConfig("cols_360")%>;
Base.globvar.columnsWidthsOverView = <%=AppManager.getSysConfig("columnsWidthsOverView")%>;
Base.globvar.indexStyle="<%=AppManager.getSysConfig("indexstyle")%>"=="mini"?"default":"<%=AppManager.getSysConfig("indexstyle")%>";
</script>
<script src="${basePath}ta/resource/external/plugin/ta-core-all/require.js" type="text/javascript"></script>
<script src="${basePath}ta/resource/external/plugin/ta-core-all/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="${basePath}ta/resource/external/plugin/main.js" type="text/javascript"></script>
<script type="text/javascript">
//<!--
//针对现有body.ready改造为requirejs所能使用的
$.fn.ready_t = $.fn.ready;
$.fn.ready = function(fn){
	$.fn.ready_t(
		function(){
			require(['domReady'], function (domReady) {
				  domReady(function () {
				    require(["taLayout","hint-tip"], function(){
						fn();
					});
				  });
			});
		}
	);
};
//-->
</script>
<%@ include file="/appinc.jsp" %>
