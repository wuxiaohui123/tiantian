<%@page import="java.math.BigDecimal"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tld/runqianReport4.tld" prefix="report" %>
<%@ page import="java.util.*"%>
<%@ page import="com.runqian.report4.usermodel.*"%>
<%@ page import="com.runqian.report4.view.ParamsPool"%>
<%@ page import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<%@ include file="/ta/inc.jsp"%>
	<link type="text/css" href="<%=basePath %>runqian/reportJsp/css/style.css" rel="stylesheet"/>
</head>
<body>
	<%  
	    StringBuffer param=new StringBuffer();
		Enumeration paramNames = request.getParameterNames();
		if(paramNames!=null){
			while(paramNames.hasMoreElements()){
				String paramName = (String) paramNames.nextElement();
				String paramValue=request.getParameter(paramName);
				if(paramValue!=null){
					//把参数拼成name=value;name2=value2;.....的形式
					param.append(paramName).append("=").append(paramValue).append(";");
				}
			}
		}
		StringBuffer param_action = (StringBuffer)request.getAttribute("param_action");
		param.append(param_action);
		request.setAttribute("raqarg", param.toString());
        
	%>
	<!-- 1.主，2.子，0参数 -->
	<%
	List praqList = (List)request.getAttribute("praqs");
	if (praqList != null && praqList.size()>0) {
	    Map m = (Map)praqList.get(0);
	    String pName = m.get("raqname").toString();
	    request.setAttribute("paramName", pName);
	%>
		<ta:fieldset key="查询条件" cssStyle="padding:10px">
			<c:forEach var="raq" items="${praqs}" begin="0" end="0">
				<report:param name='${raq.raqname}'
						srcType="defineBean"
						beanName='${raq.raqfilename}'
						resultPage="/runqian/queryReportAction.do"
						needSubmit="no"/>
			</c:forEach>
			<ta:box cols="2">
				<ta:box columnWidth="0.6">
					<button id="query" class="sexybutton" style="float:right;" type="button"  onclick="javascript:_submit( ${paramName} )" >
						<span>
							<span>
								<span class="xui-icon-query">
										 查询
								</span>
							</span>
						</span>
				    </button>
				</ta:box>
			    <ta:button key="重置" columnWidth="0.4" cssStyle="margin-left:2%" onClick="fnReset()" />
			</ta:box>
		</ta:fieldset>
	<% } %>
			<!-- tab主报表 -->
			<ta:tabs  fit="true" headPlain="true" onSelect="fnSelect">
					<ta:tab id="${raqfilename}" key="${raqname }" cssStyle="overflow:auto">
				      <input id="${raqfilename}_input" type="hidden" value="no"/>
					  <table width="100%" height="100%">
					   <tr align="center">
					      <td align="center">
					          <report:html name="${raqname}" 
										saveAsName="${raqname}"
										beanName="${name}" 
										srcType="defineBean" 
										generateParamForm="no"
										needPageMark="yes" 
										scale="${scaleexp}"
										needSaveAsExcel="${needsaveasexcel}"
										needSaveAsPdf="${needsaveaspdf}"
										needSaveAsWord="${needsaveasword}"
										needSaveAsText="${needsaveastext}"
										needPrint="${needprint}"
										needPivot="no" 
									    exceptionPage="/runqian/reportJsp/myError2.jsp"
										appletJarName="runqian/runqianReport4Applet.jar,runqian/dmGraphApplet.jar"
										params= "${raqarg}"
					            />
					      </td>
					   </tr>
					</table>
				   </ta:tab>
				   <c:if test="${!empty mraqs}">
						<c:forEach var="raq" items="${mraqs}" begin="0">
							<ta:tab id="${raq.tabid}" key="${raq.tabname}">
						      <input id="${raq.tabid}_input" type="hidden" value="yes"/>
							  <iframe id="${raq.tabid}_if" style="margin:0px;padding:0px;" frameBorder="0" width="100%" height="100%"></iframe>
						   </ta:tab>
						</c:forEach>
				   </c:if>
			</ta:tabs>
		
<%
    List mList = (List)request.getAttribute("mraqs");
	String jsonInfo = JSonFactory.bean2json(mList);
%>
</body>
</html>
<script language="javascript">
	$(document).ready(function() {
		$("body").taLayout();
		var a = $("form[name='"+'${raqname}'+"_turnPageForm']").attr("action", Base.globvar.contextPath+"/runqian/queryReportAction.do?t_i_m_e="+ new Date().getTime());
	});
	
	function fnSelect(tabId){
	   var json = '<%=jsonInfo%>';
	   var param = '${raqarg}';
	   var data = eval("("+json+")");
	   var flag = $("#"+tabId+"_input").val();
	   if(flag == "yes"){
	     $("#"+tabId+"_input").val("no");
	     for(var k in data){
	        if(data[k]["tabid"]==tabId){
	           var reportInfo = data[k]["json"];
	            $("#"+tabId+"_if").attr("src",Base.globvar.contextPath+"/runqian/showReportAction.do?raq="+tabId+"&raqInfo="+reportInfo+"&param="+param);
	        }else{
	          continue;
	        }
	     }
	   }
	};
	
	function fnReset(){
		$("legend:contains('查询条件') + div").find("input").each(function(){
			  var inputVal = $(this).val();
			  if(inputVal!="/runqian/queryReportAction.do"){
				 $(this).val("");
			  }
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
