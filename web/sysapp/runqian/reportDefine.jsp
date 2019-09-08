<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%@ taglib uri="/WEB-INF/tld/runqianReport4.tld" prefix="report" %>
<%@ page import="java.util.*"%>
<%@ page import="com.runqian.report4.usermodel.*"%>
<%@ page import="com.runqian.report4.model.ReportDefine"%>
<%@ page import="com.runqian.report4.view.ParamsPool"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>列表对话框</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body>
<%
   String name = (String)request.getAttribute("name");
   Map reportInfo = (Map)request.getAttribute("reportInfo");
   String raqname = (String)reportInfo.get("raqname");
   String needsaveasexcel = (String)reportInfo.get("needsaveasexcel");
   String needsaveaspdf = (String)reportInfo.get("needsaveaspdf");
   String needsaveasword = (String)reportInfo.get("needsaveasword");
   String needsaveastext = (String)reportInfo.get("needsaveastext");
   String needprint = (String)reportInfo.get("needprint");
   String scaleexp = (String)reportInfo.get("scaleexp");
   String param = (String)reportInfo.get("param");
 %>
	<ta:pageloading />
	<table width="100%">
	   <tr align="center">
	      <td align="center">
	          <report:html name="<%=raqname %>" 
						saveAsName="<%=raqname %>"
						beanName="<%=name %>" 
						srcType="defineBean" 
						generateParamForm="no"
						needPageMark="yes" 
						scale="<%=scaleexp %>"
						needSaveAsExcel="<%=needsaveasexcel %>"
						needSaveAsPdf="<%=needsaveaspdf %>"
						needSaveAsWord="<%=needsaveasword %>"
						needSaveAsText="<%=needsaveastext %>"
						needPrint="<%=needprint %>"
						needPivot="no" 
					    exceptionPage="/runqian/reportJsp/myError2.jsp"
						appletJarName="runqian/runqianReport4Applet.jar,runqian/dmGraphApplet.jar"
						params= "<%=param %>"
	/>
	      </td>
	   </tr>
	</table>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
		var a = $("form[name='<%=raqname %>_turnPageForm']").attr("action", Base.globvar.contextPath+"/runqian/showReportAction.do?t_i_m_e="+ new Date().getTime());
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>
