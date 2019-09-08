<%@tag pageEncoding="UTF-8"  trimDirectiveWhitespaces="true" %>
<%@tag import="com.yinhai.sysframework.util.StringUtil"%>
<%@tag import="com.alibaba.fastjson.JSONObject"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="java.lang.reflect.Array"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%@tag import="com.alibaba.fastjson.JSONArray" %>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory" %>
<%@tag import="com.yinhai.webframework.session.UserSession" %>
<%@tag import="com.yinhai.sysframework.codetable.service.CodeTableLocator" %>

<%--@doc--%>
<%@tag description='多个radio的容器' display-name='radiogroup' %>
<%@attribute description='当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认不设置为1,表示分为一列' name='cols' type='java.lang.String' %>
<%@attribute description='组件id页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='设置组件布局方式,只能设置为column' name='layout' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String' %>
<%@attribute description='组件的label标签,不支持html标签' name='key' type='java.lang.String' %>
<%@attribute description='true/false,设置是否必输，默认false，设置后代小红星' name='required' type='java.lang.String' %>
<%@attribute description='label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='设置是否显示，默认为显示:true' name='display' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='设置组件class样式' name='cssClass' type='java.lang.String' %>
<%@attribute description='设置组件style样式' name='cssStyle' type='java.lang.String' %>
<%@attribute description='根据码表自动生成radio，例如:radio="aac004"' name='collection' type='java.lang.String' %>
<%@attribute description='true/false,是否过滤org（分中心），默认true' name='filterOrg' type='java.lang.String' %>
<%@attribute description='在页面构建radio,例如:data="[{"id":"1","name":"男"},{"id":"2","name":"女"}]"' name='data' type='java.lang.String' %>
<%@attribute description='' name='columnWidth' type='java.lang.String' %>
<%--@doc--%>
<%
	//通过码表构建radio
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
	
	if (labelStyle != null) {
		String style = "";
		if (labelWidth != null) {
			style = "width:" + labelWidth + "px";
		}
		style = labelStyle + ";" + style;
		labelStyle = style;
	} else {
		if (labelWidth != null) {
			labelStyle = "width:" + labelWidth + "px";
		}
	}
	if (cssClass != null) {
		cssClass = "fielddiv fieldgroupdiv radiogroup " + cssClass;
	} else {
		cssClass = "fielddiv fieldgroupdiv radiogroup";
	}
	if ("false".equals(display) || "none".equals(display)) {
		if (this.cssStyle == null) {
			this.cssStyle = "display:none;";
		} else {
			this.cssStyle += ";display:none;";
		}
	}
	
	if ((this.id == null || this.id.length() == 0)) {
		Random RANDOM = new Random();
		int nextInt = RANDOM.nextInt();
		nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE
				: Math.abs(nextInt);
		id = "taradiogroup_" + String.valueOf(nextInt);
	}
	
	jspContext.setAttribute("_radiogroup_id",id,PageContext.REQUEST_SCOPE);
	jspContext.setAttribute("_radiogroup_obj",this,PageContext.REQUEST_SCOPE);
	
	
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
<% if( toolTip != null ){ %>
	title="${toolTip}" 
<%}%>	
<% if( required != null ){ %>
	required="${required}" 
<%}%>	
<% if( cssStyle != null ){ %>
	style="<%=cssStyle%>" 
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
    	title="鼠标单击取消选择">
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
			style="margin-left:${labelWidth}px;"
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
	        <div style="white-space:nowrap;" class="fielddiv ta_pw_radio ta-radio-uncheck">
				<input id="radio_<%=sid %>_<%=id%>" type="radio" value="<%=sid %>" name="dto['<%=id %>']" style="display:none">
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