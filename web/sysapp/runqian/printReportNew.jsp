<%@page import="com.opensymphony.xwork2.Action"%>
<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/runqianReport4.tld" prefix="report"%>
<%@ page import="java.util.*"%>
<%@ page import="com.runqian.report4.usermodel.*"%>
<%@ page import="com.runqian.report4.cache.*"%>
<%@ page import="com.runqian.report4.view.ParamsPool"%>
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ include file="/ta/inc.jsp"%>
<link type="text/css"
	href="<%=basePath%>runqian/reportJsp/css/style.css" rel="stylesheet" />
</head>
<body>
	<iframe id="report1_printIFrame" name="report1_printIFrame" width="100"
		height="100" style="position:absolute;left:-100px;top:-100px"></iframe>
	<%
		String report = (String) request.getAttribute("raqfilename");
		String actionType = (String) request.getAttribute("action");
		String paramString = request.getParameter("paramString");
		
		//导出需要的参数
		String paged = request.getParameter("paged");
		String formula = request.getParameter("formula");
		String excelFormat = request.getParameter("excelFormat");
		String expStyle = request.getParameter("expStyle");
		String saveAsName = request.getParameter("saveAsName");
		String ratio = request.getParameter("ratio");
		String columns = request.getParameter("columns");
		String savePrintSetup = request.getParameter("savePrintSetup");
		String serverPagedPrint = request.getParameter("serverPagedPrint");
		String printerName = request.getParameter("printerName");
		String needSelectPrinter = request.getParameter("needSelectPrinter");
		
		IReport rd = (IReport) request.getAttribute(report);
		//下面代码不能删除，否则打印和导出功能全部失效
		CacheManager manager = CacheManager.getInstance(); //获取系统缓存管理器
		ReportEntry re = manager.cacheReportDefine(report, rd);
		request.setAttribute(report, rd);
	%>


</body>
</html>
<script language="javascript">
	var path = "<%=path %>";
	var report = "<%=report %>";
	$(document).ready(function() {
		var action = "<%=actionType %>";
		var paged = "<%=paged %>";
		var formula = "<%=formula %>";
		var excelFormat = "<%=excelFormat %>";
		var expStyle = "<%=expStyle %>";
		var saveAsName = "<%=saveAsName %>";
		var ratio = "<%=ratio %>";
		var columns = "<%=columns %>";
		var savePrintSetup = "<%=savePrintSetup %>";
		var serverPagedPrint = "<%=serverPagedPrint %>";
		var needSelectPrinter = "<%=needSelectPrinter %>";
		var printerName = "<%=printerName %>";
		var paramString = "<%=paramString %>";
		var url = null;
		if(printerName == "no"){
		   url = "&needSelectPrinter="+needSelectPrinter;
		}else{
		   url = "&needSelectPrinter="+needSelectPrinter+"&printerName="+printerName;
		}
		switch(action){
			//预览打印
			case "2":
				$("#report1_printIFrame").attr("src",path + "/reportServlet?action=2&name=report1&reportFileName=" + report + "&" +
						"srcType=reportBean&savePrintSetup="+savePrintSetup+"&appletJarName=runqian/runqianReport4Applet.jar%2Crunqian/dmGraphApplet.jar&"+
						"serverPagedPrint="+serverPagedPrint+"&mirror=no&columns="+columns+"&paramString=" + paramString);
			break;
			//直接打印	
			case "30":
				$("#report1_printIFrame").attr("src",path + "/reportServlet?action=30&name=report1&reportFileName=" + report + "&" +
						"srcType=reportBean&appletJarName=runqian/runqianReport4Applet.jar%2Crunqian/dmGraphApplet.jar&"+
						"serverPagedPrint="+serverPagedPrint+url+"&mirror=no&columns="+columns+"&paramString=" + paramString);
			break;	
			//导出excel	
			case "3":
				$("#report1_printIFrame").attr("src",path + "/reportServlet?action=3&name=report1&file=" + report + "&" +
				        "&pageStyle="+paged+"&ratio="+ratio+"&saveAsName="+saveAsName+"&formula="+formula+
						"&srcType=reportBean&columns="+columns+"&excelFormat="+excelFormat);
			break;	
			//导出pdf	
			case "6":
				$("#report1_printIFrame").attr("src",path + "/reportServlet?action=6&name=report1&file=" + report + "&saveAsName="+saveAsName+
						"&srcType=reportBean&columns="+columns+"&expStyle="+expStyle+"&paged="+paged+"&paramString=" + paramString);
			break;	
			//导出word	
			case "7":	
				$("#report1_printIFrame").attr("src",path + "/reportServlet?action=7&name=report1&file=" + report +"&saveAsName="+saveAsName+
						"&srcType=reportBean&columns="+columns+"&paramString=" + paramString);
			break;	
			//导出text	
			case "18":
				$("#report1_printIFrame").attr("src",path + "/reportServlet?action=18&name=report1&file=" + report + "&saveAsName="+saveAsName+
						"&srcType=reportBean&paramString=" + paramString);
			break;
		}
	});
	
	/**
	 *打印完成后回调，清除该报表缓存，保证数据实时性
	 */
	function runqian_printOver(){
		$.post(path + "/runqian/queryReportAction!printCallBack.do",
				{"report" : report}
		);
	}
</script>
