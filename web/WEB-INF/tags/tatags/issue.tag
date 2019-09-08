<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%@tag import="java.util.Random" %>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean" %>
<%@tag import="com.yinhai.sysframework.util.TagUtil" %>
<%-- @doc --%>
<%@tag description='期号选择框' display-name='issue' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"表示容器顶部向内占用10个像素' name='cssStyle' type='java.lang.String' %>
<%@attribute description='true/false,设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台，默认为false' name='disabled' type='java.lang.String' %>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='true/false,设置是否显示，默认为显示:true' name='display' type='java.lang.String' %>
<%@attribute description='组件id页面唯一' name='id' required='true' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准，如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='true/false,设置是否必输，默认false，设置后代小红星' name='required' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String' %>
<%@attribute description='true/false,设置为只读，默认为true只读' name='readOnly' type='java.lang.String' %>
<%@attribute description='组件的label标签,不支持html标签' name='key' type='java.lang.String' %>
<%@attribute description='设置该容器所占父亲容器column布局中当前位置的百分比，该值在0-1之间，例如 columnWidth="0.03"' name='columnWidth' type='java.lang.String' %>
<%@attribute description='单独设置input元素的css样式,例如:cssInput="font-size:20px;color:red"' name='cssInput' type='java.lang.String' %>
<%@attribute description='label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String' %>
<%@attribute description='鼠标经过提示信息' name='toolTip' type='java.lang.String' %>
<%@attribute description='onBlur事件，当失去焦点时调用，此处填写函数调用如onBlur="fnBlur(this)"' name='onBlur' type='java.lang.String' %>
<%@attribute description='onChange事件，当onChange值改变时调用，此处填写函数调用如onChange="fnChange(this)"' name='onChange' type='java.lang.String' %>
<%@attribute description='onClick事件，当单击控件时调用，此处填写函数调用如onClick="fnClick(this)"' name='onClick' type='java.lang.String' %>
<%@attribute description='onFocus事件，当控件获取焦点时，此处填写函数调用如onClick="fnFocus(this)"' name='onFocus' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%-- @doc --%>
<% 
		if (labelStyle != null) {
			String style = "";
			if (labelWidth != null) {
				style = "width:"+ labelWidth + "px";
			}
			style = labelStyle + ";" + style;
			labelStyle = style;
		} else {
			if (labelWidth != null) {
				labelStyle = "width:" + labelWidth + "px";
			}
		}

if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
	if(null != cssClass){
	   	cssClass = "fielddiv fielddiv_163 "+cssClass;
	}else{
	    cssClass = "fielddiv fielddiv_163";
	}
}else{
	if(null != cssClass){
	   	cssClass = "fielddiv "+cssClass;
	}else{
	    cssClass = "fielddiv";
	}
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
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "taissue_" + String.valueOf(nextInt);
		}
		if (name == null || "".equals(name)) {
			name = "dto['" + id + "']";
		}
		// 优先 查找resultBean
		ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
		if (resultBean != null) {
			Object v = resultBean.getFieldData() == null ? null : resultBean.getFieldData().get(id);
			if (v != null && !"".equals(v)) {
				this.value = v.toString();
			}
		}
		// 查找request
		if (value != null && !"".equals(value)&& request.getAttribute(this.id) != null) {
			value = request.getAttribute(this.id).toString();
		}
		// 查找session
		if (value != null && !"".equals(value)
				&& request.getSession().getAttribute(this.id) != null) {
			value = request.getSession().getAttribute(this.id).toString();
		}
		
		String inputStyle="textinput issuefield";
		if(readOnly != null && !"false".equals(readOnly)){
        	inputStyle +=" readonly";
        }
        if(disabled !=null && !"false".equals(disabled)){
        	inputStyle +=" disabled";
        }
        String validType = "issue";
%>
<%@include file="../columnhead.tag" %>
<div 
<% if(cssClass != null ){ %>
class="<%=cssClass %>" 
<%}%>
<% if(cssStyle != null ){ %>
style="<%=cssStyle %>" 
<%}%>
<% if(columnWidth != null ){ %>
columnWidth="${columnWidth}" 
<%}%>
<% if(span != null ){ %>
span="${span}" 
<%}%>
<% if(toolTip != null ){ %>
title="${toolTip}" 
<%}%>
>
<% if(key != null && !"".equals(key.trim())){ %>
	<label 
	 for="<%=id%>" 
class="fieldLabel"
	<% if(labelStyle != null ){ %>
style="<%=labelStyle%>" 
	<%}%>
>
<% if( required != null && "true".equals(required)){%>
<span style="color:red">*</span>
<%}%>
${key}<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>：<%}%>
	</label>
	<%}%>
	<%
	if(inputStyle.contains("readonly") || inputStyle.contains("disabled")) {%>
	<div class="fielddiv2 readonly"
	<%}else{ %>
	<div class="fielddiv2"
	<%} %>
	<% if(labelWidth != null ){ %>
	style="margin-left:${labelWidth}px"
	<%}else if(null == key || "".equals(key.trim())) {%>
	style="margin-left:0px;" 
	<%}%>
>
		<input type="text" 
maxlength = "6"		
<% if(id != null ){ %>
id="<%=id %>" 
<%}%>
<% if(name != null ){ %>
name="<%=name %>" 
<%}%>
<% if(value != null ){ %>
value="<%=value %>" 
<%}%>
<% if(readOnly != null && "true".equals(readOnly)){%>
readOnly="${readonly}" 
<%}%>
<% if( disabled != null && "true".equals(disabled)){%>
disabled="${disabled}" 
<%}%>
<% if(required != null && "true".equals(required)){%>
required="${required}" 
<%}%>
<% if(validType != null ){ %>
validType="<%=validType %>" 
<%}%>
<% if(onClick != null ){ %>
onClick="${onClick}" 
<%}%>
<% if(onChange != null ){ %>
onChange="${onChange}" 
<%}%>
<% if(onBlur != null ){ %>
onBlur="${onBlur}" 
<%}%>
<% if(onFocus != null ){ %>
onFocus="${onFocus}" 
<%}%>
class="Wdate <%=inputStyle %>" 
<% if(cssInput != null ){ %>
style="${cssInput}" 
<%}%>
/>

	<div id="<%=id%>_issueDiv" class="issue_div">
	</div>
</div>
</div>
<%@include file="../columnfoot.tag" %>
<script>
Ta.core.TaUICreater.addUI( 
	function(){
		  var options = {
		       	txtWidth:100,
				txtHeight:20,
				<% if( disabled != null && "true".equals(disabled)){%>
				disabled : "${disabled}", 
				<%}%>
				<% if(readOnly != null && "true".equals(readOnly)){%>
				readOnly : "${readonly}", 
				<%}%>
				txtId:"<%=id%>",
				txtName:"<%=name%>"		
		   	};
		   var issue = new taissue($("#<%=id%>_issueDiv"),options);
		   
 }); 
$(function(){
	$("body").bind("mousedown", 
		function(event){
			if (!(event.target.id == "<%=id%>_issueDiv" || $(event.target).parents("#<%=id%>_issueDiv").length > 0)) {
				$("#<%=id%>_months").hide();
				$("#<%=id%>_issueDiv").hide();
			}
	});
});
</script>