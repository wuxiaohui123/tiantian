<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.webframework.security.filter.SecurityInterceptorFilter"%>
<%@tag import="java.util.Set"%>
<%@tag import="java.util.Random"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="com.yinhai.webframework.session.UserSession"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%--@doc--%>
<%@tag description='提交交互' display-name='submit' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='true/false,设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='true/false,设置是否显示，默认为显示:true' name='display' type='java.lang.String' %>
<%@attribute description='组件id' name='id' type='java.lang.String' %>
<%@attribute description='组件的label标签' name='key' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='热键，如果只输入一个英文字母默认是atl+字母的组合，还可以输入ctrl+s这样的值' name='hotKey' type='java.lang.String' %>
<%@attribute description='图标样式class名称,如:icon-add,可以到icon.css查询' name='icon' type='java.lang.String' %>
<%@attribute description='true/false,是否自动校验，默认true' name='isValidate' type='java.lang.String' %>
<%@attribute description='需要提交的组件id，可以是panel,fieldset,div,box,tabs,tab,form,datagrid以及输入元素的id。多个可以用逗号隔开。但是多个id里面不能有包含与被包含的关系' name='submitIds' type='java.lang.String' %>
<%@attribute description='action的地址' name='url' type='java.lang.String' required="true"%>
<%@attribute description='提交前的调用方法，必须返回true或false，如果返回false就不在继续调用' name='onSubmit' type='java.lang.String' %>
<%@attribute description='Function,业务成功（返回数据有success=true的数据的时候调用该方法),传入的是方法的定义，比如:successCallBack="fnMyCallBack"。注意不能写成:successCallBack="fnMyCallBack()"' name='successCallBack' type='java.lang.String' %>
<%@attribute description='Function,业务成功（返回数据有success=false的数据的时候调用该方法),传入的是方法的定义，比如:failureCallBack="fnMyCallBack"。注意不能写成:failureCallBack="fnMyCallBack()"' name='failureCallBack' type='java.lang.String' %>
<%@attribute description='手动传入js变量参数' name='parameter' type='java.lang.String' %>
<%@attribute description='设置是否为paneltoolbar中使用的按钮' name='asToolBarItem' type='java.lang.String' %>
<%@attribute description="设置提交时显示蒙层的对象id。可以panel，fieldset等。如果设置为'body'，就整个页面显示蒙层。" name='showMask' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='true/false,默认为false，设置是否同步提交。主要用途，表单提交后要刷新整个页面或跳转到其他页面的时候以及需要使用文件上传功能的时候使用。如果设置为true相当于调用Base.submitForm' name='isSyncSubmit' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%@attribute description='' name='span' type='java.lang.String' %>
<%@attribute description='按钮样式，是否为确认类型，比如保存，更新等操作，默认false，只适用于163风格' name='isok' type='java.lang.String' %>
<%@attribute description='是否提交空字段,true/false，true表示提交空字段，false表示采用全局配置' name='isIncludeNullFields' type='java.lang.String' %>
<%@attribute description='是否在新样式按钮上显示图标，默认false，不显示' name='isShowIcon' type='java.lang.String' %>
<%--@doc--%>
<%
 /* 对形如id="test.xxAction.save"的按钮做出权限判断，如果没有/test/xxAction!save.do的操作权限，则不输出 */
 		String outputting = "";
		UserSession _us = UserSession.getUserSession(request);
		if(_us != null && _us.getUser() != null){
		    if (id != null && id.indexOf(".") > -1) {
		   	String url = id.replace(".", "/");
		   	url = "/" + url.substring(0, url.lastIndexOf("/")) + "!"
		   			+ url.substring(url.lastIndexOf("/") + 1) + ".do";
		   	Set<String> perviewSet  = (Set<String>) request.getSession().getAttribute(SecurityInterceptorFilter.USER_PERVIEW_FLAG);
		   	if(perviewSet != null && perviewSet.contains(url)){
		   		outputting = "true";
		   	} else {
		   		if ("developer".equals(_us.getUser().getLoginId()) || "super".equals(_us.getUser().getLoginId())) {
		   			outputting = "true";
		   		}
		   	}
		   } else {
			   outputting = "true";
		   }
		}else{
			outputting = "true";
		}
        
        if (null != hotKey) {
        	if(hotKey.length()==1)hotKey = "Alt+"+hotKey;
        }
                
 
        if ("false".equals(isValidate)) {
            isValidate = "false";
        }else{
        	isValidate = "true";
        }
        if (null != onSubmit) {
        }else{
        	onSubmit = "null";
        }
        if (null != submitIds) {
            submitIds = "'"+submitIds+"'";
        }else{
        	submitIds = "null";
        }
        
        if (null != url) {
        	url = "'"+url+"'";
        }else{
        	url = "null";
        }
        
        if (null != successCallBack) {
        }else{
        	 successCallBack = "null";
        }
        
        if (null != failureCallBack) {
        }else{
        	failureCallBack= "null";
        }
        
        if (null != parameter) {
        }else{
        	parameter = "null";
        }
        if("false".equals(display)|| "none".equals(display)){
        	if(this.cssStyle ==null){
        		this.cssStyle = "display:none;";
        	}else{
        		this.cssStyle += ";display:none;";
        	}
        }
        boolean isGroup = false;
		if(null != getParent()){
	    	 String st = getParent().getClass().toString();
	    	 if(null != st && st.length()>0){
	    	   isGroup = st.contains("buttonGroup");
	    	 }
		}
		 if(isGroup){
			if(cssClass==null){
        		cssClass = "sexybutton_163";
        	}else{
        		cssClass = "sexybutton_163 "+cssClass;
        	}
        	if(this.cssStyle ==null){
        		this.cssStyle = "float:left;";
        	}else{
        		this.cssStyle += ";float:left;";
        	}
        }else if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
        	if(cssClass==null){
        		cssClass = "sexybutton_163";
        	}else{
        		cssClass = "sexybutton_163 "+cssClass;
        	}
        }else{
        	if(cssClass==null){
        		cssClass = "sexybutton";
        	}else{
        		cssClass = "sexybutton "+cssClass;
        	}
        }
       
        if("true".equals(asToolBarItem)){
        	cssClass +=" toolbarbt";
        }
        if ((this.id == null || this.id.length() == 0)) {
			Random RANDOM = new Random();
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tasubmit_" + String.valueOf(nextInt);
		}     
        
%>

<%if(outputting !=null && "true".equals(outputting)){%>
<button id="<%=id%>" 
class="<%=cssClass%>"  
type="button" 
<%if(hotKey !=null){ %>
 hotKey="<%=hotKey%>" 
<%} %>
<%if(cssStyle !=null){ %>
 style="<%=cssStyle%>"
<%} %>
<%if(disabled !=null){ %>
  disabled="<%=disabled%>" 
<%} %>
<%if(toolTip !=null){ %>
  title="<%=toolTip%>"
<%} %>
> 
<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE)) || isGroup){ %>
<span class="button_span <% if (isok != null && "true".equals(isok)){%>isok<%} %>" 
>
<%if(icon != null){
	if(isShowIcon != null && "true".equals(isShowIcon)) {%>
	<span style="height:16px;width:16px;padding-left: 0px;float: left;margin-top: 6px;" class="${icon}"></span>
<%}}%>
      <% if (key != null){%>
        ${key}
      <%}else{%>
	 &nbsp;	
	  <%}%>
</span>
<%}else{%>
<span>
		<span>
		
			<% if (icon != null){%>
			<span style="height:16px;width:16px;padding-left: 0px;float: left;margin-top: 3px;" class="${icon}"></span>
			<span >
				<%}%>
				<%if(key !=null){ %>
					 <%=key%> 
				<%}%>	
				<%if(icon !=null){ %>
					</span>
				<%} %>		
		</span>
	</span>
<%} %>
</button> 
<script type="text/javascript">
	$(function(){
		$("#<%=id%>").click(function(){
			<% if(showMask !=null){ %>
				Base.showMask('<%=showMask %>'); 
			<%} %>
			<%if(isSyncSubmit !=null && "true".equals(isSyncSubmit)){ %>
				Base.submitForm(<%=submitIds%>,<%=onSubmit %>,<%=isValidate%>,<%=url %>); 
			<%} else{%>
				Base.submit.call(this,<%=submitIds%>,<%=url %>,<%=parameter%>,<%=onSubmit %>,<%=isValidate%>,<%=successCallBack %>,<%=failureCallBack%>,<%=isIncludeNullFields%>);
			<% }%>
		});
	});
</script>
<% }%>