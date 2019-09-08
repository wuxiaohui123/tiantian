<%@page import="com.yinhai.sysframework.service.AppManager"%>
<%@page import="com.yinhai.sysframework.exception.IllegalInputAppException"%>
<%@page import="com.yinhai.sysframework.exception.PrcException"%>
<%@page import="com.yinhai.sysframework.exception.AppException"%>
<%@page import="com.yinhai.sysframework.app.domain.jsonmodel.OperationBean"%>
<%@page import="com.opensymphony.xwork2.util.ValueStack"%>
<%@page import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="org.apache.struts2.views.jsp.TagUtils"%>
<%@page import="java.util.List"%>
<script type="text/javascript">
_noClose = false;
$(function(){
	$(document).keydown(function(e) {
		// 屏蔽回退键盘
		var target = e.srcElement || e.target;
		if ((e.keyCode == 8) && target && ((target.type != "text"
				&& target.type != "textarea" && !$(target).attr("contenteditable")
				&& target.type != "password") ||target.readOnly || target.disabled)) {
			e.keyCode = 0;
			e.cancelBubble = true;
			e.returnValue = false;

			// e.stopPropagation works in Firefox.
			if (e.stopPropagation) {
				e.stopPropagation();
				e.preventDefault();
			}
		}
		var isie = (document.all) ? true : false;
		var key;
		var srcobj;
		if (isie) {
			key = e.keyCode;
			srcobj = e.srcElement;
		} else {
			key = e.which;
			srcobj = e.target;
		}
		if (srcobj == null)srcobj = e.target;
		if (key == null) key = e.which;
		if (key == 13 && srcobj.type != 'button' && srcobj.type != 'submit'
				&& srcobj.type != 'reset' && srcobj.type != 'textarea'
				&& srcobj.type != '') {
			var el = Base._getNextFormField(srcobj.id);
			if(el){
				Base.focus(el.id);
				//时间组件已默认选中,如果再在这里选中,会出现闪动问题,20130628,liys修改
				if($(el).hasClass("Wdate")){
				}else if (el.type == "text")
					el.select();
			}
			return false;
		}

	}).click(function(event){
		if(!_noClose){
			try{
				top._fnCloseMenu();
			}	
			catch(e){
				sendMsgToFrame("function","_fnCloseMenu");
			}	
		}
	});
	function sendMsgToFrame(type, msg, args){
        try {
            var o = {};
            o.type = type;
            o.msg = msg;
            o.args = args || [];
			o = Ta.util.obj2string(o);
            window.top.postMessage(o, "*");
        } 
        catch (e) {
      }
   }
   	setTimeout(function(){
<%
ValueStack stack = TagUtils.getStack(pageContext);
ResultBean rb = (ResultBean)stack.findValue("resultBean");
if(rb != null){//出书action对界面的控制的内容，通过脚本执行来控制
	if(rb.getOperation() != null){
		List<OperationBean> ___list = rb.getOperation();
		for(int i=0;i<___list.size();i++){
			OperationBean op = ___list.get(i);
			String optype = op.getType();
			if("readonly".equals(optype)){
				out.println("Base.setReadOnly("+OperationBean.arrayTojsArray(op.getIds())+");");
			}else if("enable".equals(optype)){
				out.println("Base.setEnable("+OperationBean.arrayTojsArray(op.getIds())+");");
			}else if("disabled".equals(optype)){
				out.println("Base.setDisabled("+OperationBean.arrayTojsArray(op.getIds())+");");
			}else if("select_tab".equals(optype)){	
				//out.println("Base.selectTab('"+op.getIds()+"');");
				String[] ids = op.getIds();
				for(int j = 0;j<ids.length;j++){
					out.println("Base.activeTab('"+ids[j]+"');");
				}
				//out.println("Base.activeTab('"+op.getIds()+"');");
				//out.println("Base.activeTab('"+op.getIds()[0]+"');");
			}else if("hide".equals(optype)){				
				out.println("Base.hideObj("+OperationBean.arrayTojsArray(op.getIds())+");");
			}else if("show".equals(optype)){	
				out.println("Base.showObj("+OperationBean.arrayTojsArray(op.getIds())+");");
			}else if("unvisible".equals(optype)){
				out.println("Base.hideObj("+OperationBean.arrayTojsArray(op.getIds())+",true);");
			}else if("resetForm".equals(optype)){
				out.println("Base.resetForm('"+op.getIds()[0]+"');");
			}else if("required".equals(optype)){
				out.println("Base.setRequired("+OperationBean.arrayTojsArray(op.getIds())+");");
			}else if("disrequired".equals(optype)){
				out.println("Base.setDisRequired("+OperationBean.arrayTojsArray(op.getIds())+");");
			}
		}
	}
	//有msg
	if(rb.getMsg()!=null){
		out.println("var focus = null;");
		if(rb.getFocus()!=null){
			out.println("focus = function(_fieldId){return function(){Base.focus(_fieldId,100);}}('"+rb.getFocus()+"');");
		}
		if(rb.getMsgType() != null){
			out.println("Base.alert('"+rb.getMsg()+"','"+rb.getMsgType()+"',focus)");
		}else{
			out.println("Base.alert('"+rb.getMsg()+"',null,focus)");
		}
	}
	//没有msg，但是有focus
	if(rb.getMsg()==null && rb.getFocus()!=null){
		out.println("Base.focus('"+rb.getFocus()+"',50);");
	}
	//有topMsg
// 	if(rb.getTopTipMsg() != null){
// 		TopMsg t = rb.getTopTipMsg();
// 		out.println("Base.msgTopTip('"+t.getTopMsg()+"',"+t.getTime()+","+t.getWidth()+","+t.getHeight()+")");
// 	}else if(rb.getTopMsg() != null && rb.getTopTipMsg() == null){
// 		out.println("Base.msgTopTip('"+rb.getTopMsg()+"')");
// 	}
}
%>
	$("#pageloading").remove();
},1);
	<%
//对同步提交异常的处理
Exception e = (Exception)request.getAttribute("exception");
String errmsg = (String)request.getAttribute("exceptionStack");
if(errmsg!=null){
errmsg = errmsg.replaceAll( "<", "&lt;")
.replaceAll( ">", "&gt;")
.replaceAll( " ", "&nbsp;").replaceAll( "\n", "").replaceAll( "\r", "");
}
if(errmsg != null && !"".equals(errmsg)){
	if("true".equals(AppManager.getSysConfig("developMode"))){
		System.out.println("\n"+(String)request.getAttribute("exceptionStack"));
	}
	String msg  ="",errors = "";
	if(e instanceof AppException){
		AppException ae = (AppException)e;
		msg = ae.getMessage();
		if(ae.getFieldName()!=null){
			errors = ",\"'validateErrors'\":{\""+ae.getFieldName()+"\" :\""+msg+"\"}";
		}
	}else if(e instanceof PrcException){
		msg =  ((PrcException)e).getShortMsg(); 
	}else if(e instanceof IllegalInputAppException){
		IllegalInputAppException ia = (IllegalInputAppException)e;
		List<AppException> list2 = ia.getExceptions();
		errors = ",\"validateErrors\":{";
		for(int i=0;list2!=null && i<list2.size();i++){
			AppException ae = list2.get(i);
			if(i==0){
				errors += (ae.getFieldName()==null?" ":"\""+ae.getFieldName()+"\"") + ":\""+ae.getMessage()+"\"";
			}else{
				errors += ","+(ae.getFieldName()==null?" ":"\""+ae.getFieldName()+"\"") + ":\""+ae.getMessage()+"\"";	
			}
		}
		errors += "}";
	}else{//非业务异常
		errmsg = errmsg.replaceAll( "<", "&lt;")
		.replaceAll( ">", "&gt;")
		.replaceAll( " ", "&nbsp;")
		.replaceAll( "\n", "").replaceAll( "\r", "");
		String tmpmsg="";
		if(null != e.getCause()){
			tmpmsg=e.getCause().getCause().getMessage().replaceAll( "<", "&lt;")
			.replaceAll( ">", "&gt;")
			.replaceAll( " ", "&nbsp;")
			.replaceAll( "\n", "").replaceAll( "\r", "");
		}else{
			tmpmsg = e.toString();
		}
%>
	Base._dealdata({'success':false,'msg':'<%=tmpmsg%>','errorDetail':'<%=errmsg%>'});
<%
	}
	if(!"".equals(msg)){//说明是业务异常
		msg = msg.replaceAll( "<", "&lt;")
		.replaceAll( ">", "&gt;")
		.replaceAll( " ", "&nbsp;")
		.replaceAll( "\n", "").replaceAll( "\r", "");
	    System.out.println("____++++++++++++");
		out.print("Base._dealdata({'success':false,'msg':'"+msg+"'"+errors+",'errorDetail':'"+errmsg+"'}); " + 
	    "var a = $(document.body).find('.window>.window-body').attr('id');"+
		"if (a != undefined) Base.closeWindow(a);"); 
	}
}//if(errmsg != null && !"".equals(errmsg)){
%>

});
<%if ("true".equals(request.getAttribute("_removeButtonClick"))) {
%>
	//$("input[type=button]").unbind("click");
	$(document).ready(function () {
		
		<% 
		Enumeration en = request.getAttributeNames();
		String enStr = "";
		String selStr = "";
		while (en.hasMoreElements()) {
				if (en.nextElement() !=null && (enStr = (String)en.nextElement()).indexOf("_sel") != -1) {
					selStr = request.getAttribute(enStr).toString();
					enStr = enStr.substring(0, enStr.indexOf("_sel"));
					
					%>
					Base.getObj("<%=enStr%>").setCheckedRows('<%=selStr%>');
					<%
				}
			}
		%>
		$("button").attr("disabled","true");
		$("input").attr("disabled","true");
	})
	//$("button").attr("disable","true");
	//.remove();
<%--	.each(function(a){--%>
<%--		$(a).remove();--%>
<%--	});--%>
<%
}
%>
</script>