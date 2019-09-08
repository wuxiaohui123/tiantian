<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.sysframework.util.IConstants"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<meta http-equiv="X-UA-Compatible" content="IE=8; IE=9; IE=10" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/base/ta-icon-all.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/base/ta-item-all.css" />
<%@include file="/ta/inc-theme.jsp"  %>
<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/base/zTreeStyle/zTreeStyle.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/base/zTreeStyle/zTreeIcons.css" />
<script src="<%=basePath%>ta/resource/external/plugin/ta-core-all/jquery-1.11.0.min.js" type="text/javascript"></script>

<script src="<%=basePath%>ta/resource/external/plugin/ta-core-all/TaJsUtil.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-core-all/TaUIManager.js" type="text/javascript"></script> 
<script>
var Base = {};
Base.globvar = {};
Base.globvar.contextPath = "<%=request.getContextPath()%>";
Base.globvar.basePath = "<%=basePath%>";
Base.globvar.developMode = <%=AppManager.getSysConfig("developMode")%>;
Base.globvar.pageSize = <%=AppManager.getSysConfig("pageSize")%>;
Base.globvar.cols_360 = <%=AppManager.getSysConfig("cols_360")%>;
Base.globvar.columnsWidthsOverView = <%=AppManager.getSysConfig("columnsWidthsOverView")%>;
Base.globvar.indexStyle="<%=AppManager.getSysConfig("indexstyle")%>"=="mini"?"default":"<%=AppManager.getSysConfig("indexstyle")%>";
var allRequire = [];
</script>
<script src="<%=basePath%>ta/resource/external/plugin/ta3.all.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-datepicker-all/WdatePicker.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-softkeyboard-all/softkeyboard.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.core.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.excheck.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.exedit.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.exhide.min.js" type="text/javascript"></script> 

<%@ include file="/appinc.jsp" %>
