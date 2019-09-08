<%@tag pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.StringUtil"%>
<%@tag import="com.alibaba.fastjson.JSONObject"%>
<%@tag import="java.util.Random"%>
<%@tag import="java.lang.reflect.Array"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%@tag import="com.alibaba.fastjson.JSONArray" %>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory" %>
<%@tag import="com.yinhai.webframework.session.UserSession" %>
<%@tag import="com.yinhai.sysframework.codetable.service.CodeTableLocator" %>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='用于包围checkbox的容器' display-name='checkboxgroup' %>
<%@attribute description='设置组件id,页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='设置组件布局方式,只能设置为column' name='layout' type='java.lang.String' %>
<%@attribute description='对checkboxGroup设置标题,不支持html格式' name='key' type='java.lang.String' %>
<%@attribute description='当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认不设置为1' name='cols' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='true/false,设置是否必选,默认为:false' name='required' type='java.lang.String' %>
<%@attribute description='设置最大选择数,例如:maxSelect="2",表示最多只能选择两个checkbox' name='maxSelect' type='java.lang.String' %>
<%@attribute description='设置最小选择数,例如:minSelect="2",表示最少要选择两个checkbox' name='minSelect' type='java.lang.String' %>
<%@attribute description='设置label的宽度' name='labelWidth' type='java.lang.String' %>
<%@attribute description='设置label的样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='true/false,是否可以见,默认:true' name='display' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='根据码表自动生成checkbox，例如:collection="aac004"' name='collection' type='java.lang.String' %>
<%@attribute description='true/false,是否过滤org（分中心），默认true' name='filterOrg' type='java.lang.String' %>
<%@attribute description='在页面构建checkbox,例如:data="[{"id":"1","name":"成都"},{"id":"2","name":"重庆"}]"' name='data' type='java.lang.String' %>
<%--@doc--%>
<%
	JSONArray json2bean = null;
	if(null != data && !"".equals(data)){
		json2bean = JSonFactory.json2bean(data,JSONArray.class);
		data = json2bean.toJSONString();
	}else if (null != collection && !"".equals(collection)) {
		collection = collection.toString();
		String orgId = null;
		UserSession userSession = UserSession.getUserSession(request);
        if(userSession!=null)
        	orgId = UserSession.getUserSession(request).getUser().getOrgId();
        if(filterOrg != null && "false".equals(filterOrg)){
        	orgId = "9999";
        }
    	String str = CodeTableLocator.getInstance().getCodeListJson(collection, orgId);
		if(!"".equals(str) && null != str){
			json2bean = JSonFactory.json2bean(str,JSONArray.class);
			data = json2bean.toJSONString();
		}
	}
	
	String validType = null;
	
	if (maxSelect != null || minSelect !=null) {
		String min = "0",max = "999";
		if(maxSelect != null)max = maxSelect;
		if(minSelect != null)min = minSelect;
		validType = "checkboxgroup["+min+","+max+"]";
	}
	
	if (labelStyle != null) {
		String style = "";
		if (labelWidth != null) {
			style = "width:"+labelWidth+"px";
		}
		style = labelStyle+";"+style;
		labelStyle = style;
	}else{
		if (labelWidth != null) {
			labelStyle =  "width:"+ labelWidth +"px";
		}
	}
	if(cssClass !=null){
		cssClass = "fielddiv fieldgroupdiv checkboxgroup "+ cssClass;
	}else{
		cssClass = "fielddiv fieldgroupdiv checkboxgroup";
	}
	if("false".equals(display) || "none".equals(display)){
		if(this.cssStyle ==null){
			this.cssStyle = "display:none;";
		}else{
			this.cssStyle += ";display:none;";
		}
	}
    jspContext.setAttribute("_checkboxgroup_id",id,PageContext.REQUEST_SCOPE);
	if ((this.id == null || this.id.length() == 0)) {
		Random RANDOM = new Random();
		int nextInt = RANDOM.nextInt();
		nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
				.abs(nextInt);
		id = "tacheckboxgroup_" + String.valueOf(nextInt);
	}
	
	jspContext.setAttribute("_checkboxgroup_obj",this,PageContext.REQUEST_SCOPE);
	String columnWidth = null;	

	Integer col = (StringUtil.isEmpty(cols)?Integer.valueOf("1"):Integer.valueOf(cols));
	String w = Math.floor((100/col.doubleValue()) * 100) / 100 + "%";
%>

<%@include file="../columnhead.tag" %>

<div
	<% if( cssClass != null ){ %> 
class="<%=cssClass %>" 
	<%}%>	
	<% if( id != null ){ %>
id="<%=id %>" 
	<%}%>
	<% if( span != null ){ %>
span="${span}" 
	<%}%>	
	<% if( required != null ){ %>
required="${required}" 
	<%}%>	
	<% if( validType != null ){ %>
validType="<%= validType %>" 
	<%}%>
	<% if( toolTip != null ){ %>
title="${toolTip}" 
	<%}%>
	<% if( cssStyle != null ){ %>
style="<%=cssStyle %>" 
	<%}%>	
	<% if( key != null ){ %>
	
	<%}else{ %>
		<% if( cols != null ){ %>
layout="column" cols="${cols}"
		<%}%>		
	<%}%>	
> 
	<% if( key != null ){ %>
		<label class="fieldLabel" 
		<% if( labelStyle != null ){ %>
style="<%=labelStyle%>"  
		<%}%>
title="鼠标单击全选或取消">
<% if(required!=null){%>
<span style="color:red">*</span>
<%}%> 
${key}：
		</label>
		<div class="fielddiv2" layout="column" 
		<% if( cols != null ){ %>
			cols="${cols}" 
		<%}%>
		<% if( labelWidth != null ){ %>
			style="margin-left:${labelWidth}px" 
		<%}%>		
		> 
	<%}%>
	
<jsp:doBody />

	<% if( json2bean != null ){ 
		for(int i = 0; i <json2bean.size(); i++){
			JSONObject obj = json2bean.getJSONObject(i);
			String sid = obj.getString("id");
			String sname = obj.getString("name");
	%>
		<div class='ez-fl ez-negmx' style='width:<%=w %>;'>
	        <div style="white-space:nowrap;" class="fielddiv fielddiv_163 ta_pw_chkbox ta-chk-uncheck">
				<input id="check_<%=sid %>_<%=id%>" type="checkbox" value="<%=sid %>" name="dto['<%=id %>']" style="display:none">
				<label>
				<%=sname %>
				</label>
			</div>
		</div>
      	<% }%>
    <%}%>
	<% if( key != null ){ %>
		</div>
	<%}%>
<div style="clear:both"></div>
</div>	

<%@include file="../columnfoot.tag" %>