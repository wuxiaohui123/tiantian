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
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/cookie.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/draggable.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/event.drag-2.2.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/hotkeys.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/json-2.3.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/sortable.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/ta.jquery.ext.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-bubblepopup-all/bubblepopup.v2.3.1.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/panel.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/query.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/selectpanel.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/tauipanel.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/tauitabs.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-dialog-all/dialog.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-dialog-all/window.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-dialog-all/windowmessage.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/datetimeMask.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/issue.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/moneyInput.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/numberBox.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/numberSpinner.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/selectGrid.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/selectGrid_temp.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/selectInput.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-form-all/selectTree.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.base.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.checkbox.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.core.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.dataview.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.editors.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.group.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.pager.erpexcel.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.pager.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.radioselect.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-grid-all/grid.rowselect.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/autoPercentHeight.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/border.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/fit.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/resizable.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/taLayout.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/autoPercentHeight.js" type="text/javascript"></script>
<script src="<%=basePath%>ta/resource/external/plugin/ta-menu-all/menu.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-menu-all/tamenu.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-password-all/passwordCheck.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-softkeyboard-all/softkeyboard.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tools-all/GB2312.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.core.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.excheck.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.exedit.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tree-all/ztree.exhide.min.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-validate-all/validateBox.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-tools-all/hint-tip.js" type="text/javascript"></script> 
<!-- <script src="<%=basePath%>ta/resource/external/plugin/ta-helptip-all/jquery.timers-1.2.js" type="text/javascript"></script>  -->
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.datagrid.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.fieldset.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.panel.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.print.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.selectinput.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.tabs.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.tree.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.window.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.forms.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.taajax.js" type="text/javascript"></script> 
<script src="<%=basePath%>ta/resource/external/plugin/ta-datepicker-all/WdatePicker.js" type="text/javascript"></script> 

<%@ include file="/appinc.jsp" %>
