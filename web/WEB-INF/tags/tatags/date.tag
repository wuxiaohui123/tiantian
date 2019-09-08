<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="com.yinhai.sysframework.util.StringUtil"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="java.util.Date"%>
<%@tag import="java.util.Calendar"%>
<%@tag import="java.text.SimpleDateFormat"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.persistence.PageBean"%>
<%@tag import="java.util.Map"%>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="java.util.Enumeration"%>
<%--@doc--%>
<%@tag description='日期或日期时间框' display-name='date' %>
<%@attribute description='组件id，页面唯一' name='id' required='true' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top：10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='组件的label标签' name='key' type='java.lang.String' %>
<%@attribute description='当该组件被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='设置label的宽度' name='labelWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto[‘id’]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String' %>
<%@attribute description='设置是否必输' name='required' type='java.lang.String' %>
<%@attribute description='设置是否显示，默认为显示：true' name='display' type='java.lang.String' %>
<%@attribute description='是否显示时分秒' name='datetime' type='java.lang.String' %>
<%@attribute description='设置是否只读' name='readOnly' type='java.lang.String' %>
<%@attribute description='设置日期最大值' name='max' type='java.lang.String' %>
<%@attribute description='设置日期最小值' name='min' type='java.lang.String' %>
<%@attribute description='设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='日期框点击事件，例如:onClick="fnClick()"' name='onClick' type='java.lang.String' %>
<%@attribute description='日期框change事件' name='onChange' type='java.lang.String' %>
<%@attribute description='onFocus' name='onFocus' type='java.lang.String' %>
<%@attribute description='onBlur' name='onBlur' type='java.lang.String' %>
<%@attribute description='true/false。默认false,设置是否显示选择面板' name='showSelectPanel' type='java.lang.String' %>
<%@attribute description='年月日期格式，true/false。YYYY-MM' name='dateMonth' type='java.lang.String' %>
<%@attribute description='年日期格式，true/false。YYYY' name='dateYear' type='java.lang.String' %>
<%@attribute description='期号格式,true/false。YYYYMM' name='issue' type='java.lang.String' %>
<%@attribute description='单独设置input元素的css样式,例如:cssInput="font-size:20px;color:red"' name='cssInput' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%@attribute description='是否聚焦时显示选择面板，默认为true' name='isFocusShowPanel' type='java.lang.String' %>
<%@attribute description='left/right,验证时间,left表示不能大于当前时间，right表示不能小于当前时间' name='validNowTime' type='java.lang.String' %>
<%@attribute description='label样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='显示毫秒' name='dateFulltime' type='java.lang.String' %>
<%@attribute description='显示输入框提示图标，内容自定义。例如textHelp="默认显示在左下角"' name='textHelp' type='java.lang.String' %>
<%@attribute description='textHelp宽度，默认200。例如textHelpWidth="200"' name='textHelpWidth' type='java.lang.String' %>
<%@attribute description='textHelp位置{topLeft,topRight,bottomLeft,bottomRight}，默认bottomLeft。例如textHelpPosition="bottomRight"' name='textHelpPosition' type='java.lang.String' %>
<%--@doc--%>
<%
String labelStyle = null;
if (labelStyle != null) {
	String style = "";
	if (labelWidth != null) {
		style = "width:"+ labelWidth +"px";
	}
	style = labelStyle + ";" + style;
	this.labelStyle = style;
	jspContext.setAttribute("labelStyle", this.labelStyle);
}else{
	if (labelWidth != null) {
		labelStyle =  "width:" + labelWidth +"px";
		jspContext.setAttribute("labelStyle", "width:" + labelWidth +"px");
	}
}
if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
	if(null != cssClass){
		this.cssClass = "fielddiv fielddiv_163 "+ cssClass;
		jspContext.setAttribute("cssClass", cssClass);
	}else{
		this.cssClass = "fielddiv fielddiv_163";
		jspContext.setAttribute("cssClass", "fielddiv fielddiv_163");
	}
}else{
	if(null != cssClass){
		this.cssClass = "fielddiv "+ cssClass;
		jspContext.setAttribute("cssClass", cssClass);
	}else{
		this.cssClass = "fielddiv";
		jspContext.setAttribute("cssClass", "fielddiv");
	}
}

if("false".equals(display) || "none".equals(display)){
	if(this.cssStyle ==null){
		this.cssStyle = "display:none;";
	}else{
		this.cssStyle += ";display:none;";
	}
}

if(null != validNowTime && "left".equals(validNowTime)){
	 Calendar calendar = Calendar.getInstance();
	 Date date= calendar.getTime();
	 SimpleDateFormat sf = null;
	 if("true".equals(datetime)){
		 sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	 max = sf.format(date);
		}else if("true".equals(dateFulltime)){
			sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
			max = sf.format(date);
		}else if("true".equals(dateMonth)){
			sf=new SimpleDateFormat("yyyy-MM"); 
			max = sf.format(date);
		}
		else if("true".equals(issue)){
			sf=new SimpleDateFormat("yyyyMM"); 
			max = sf.format(date);
		}
		else if("true".equals(dateYear)){
			sf=new SimpleDateFormat("yyyy"); 
			max = sf.format(date);
		}
		else{
			sf=new SimpleDateFormat("yyyy-MM-dd"); 
			max = sf.format(date);
		}
}
if(null != validNowTime && "right".equals(validNowTime)){
	 Calendar calendar = Calendar.getInstance();
	 Date date = calendar.getTime();
	 SimpleDateFormat sf = null;
	if("true".equals(datetime)){
		 sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	 min = sf.format(date);
	}else if("true".equals(dateFulltime)){
		sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		min = sf.format(date);
	}else if("true".equals(dateMonth)){
		sf=new SimpleDateFormat("yyyy-MM"); 
   	 	min = sf.format(date);
	}else if("true".equals(dateYear)){
		sf=new SimpleDateFormat("yyyy"); 
   	 	min = sf.format(date);
	}else if ("true".equals(issue)) {
		sf=new SimpleDateFormat("yyyyMM"); 
		min = sf.format(date);
	}
	else{
		sf=new SimpleDateFormat("yyyy-MM-dd"); 
		min = sf.format(date);
	}
}
if(validNowTime!=null){
	jspContext.setAttribute("nowTime", validNowTime);
}
/*这里对validType作处理，如果有maxLength，则在validType中加上validType="length[max,min]"
 * 如果已经有了validType，则以指定的validType为准
 */ 
if(null != max && null != min)
{
	if("true".equals(datetime)){
		jspContext.setAttribute("validType","datetime['" + min + "','"+ max + "']");
	}
	else if("true".equals(dateFulltime)){
		jspContext.setAttribute("validType","dateFulltime['"+ min+"','"+max+"']");
	} 
	else if("true".equals(dateMonth)){
		jspContext.setAttribute("validType","dateMonth['"+ min+"','"+max+"']");
	} 
	else if("true".equals(dateYear)){
		jspContext.setAttribute("validType","dateYear['"+ min+"','"+max+"']");
	} 
	else if ("true".equals(issue)) {
		jspContext.setAttribute("validType","issue['"+ min+"','"+max+"']");
	}
	else 
		jspContext.setAttribute("validType","date['"+ min+"','"+max+"']");
}
else if(null == max && null != min)
{
	if("true".equals(datetime)){
		jspContext.setAttribute("validType","datetime['"+ min+"','']");
	}else if("true".equals(dateFulltime)){
		jspContext.setAttribute("validType","dateFulltime['"+ min+"','']");
	}else if("true".equals(dateMonth)){
		jspContext.setAttribute("validType","dateMonth['"+ min+"','']");
	}else if("true".equals(dateYear)){
		jspContext.setAttribute("validType","dateYear['"+ min+"','']");
	}else if ("true".equals(issue)) {
		jspContext.setAttribute("validType","issue['"+ min+"','']");
	}
	else
		jspContext.setAttribute("validType","date['"+ min+"','']");
}
else if(null != max && null == min)
{
	if("true".equals(datetime)){
		jspContext.setAttribute("validType","datetime['','"+max+"']");
	}else if("true".equals(dateFulltime)){
		jspContext.setAttribute("validType","dateFulltime['','"+max+"']");
	}else if("true".equals(dateMonth)){
		jspContext.setAttribute("validType","dateMonth['','"+max+"']");
	}
	else if("true".equals(dateYear)){
		jspContext.setAttribute("validType","dateYear['','"+max+"']");
	}
	else if("true".equals(issue)){
		jspContext.setAttribute("validType","issue['','"+max +"']");
	}
	else
		jspContext.setAttribute("validType","date['','"+max +"']");
}else{
	if("true".equals(datetime)){
		jspContext.setAttribute("validType","datetime");
	}else if("true".equals(dateFulltime)){
		jspContext.setAttribute("validType","dateFulltime");
	}else if("true".equals(dateMonth)){
		jspContext.setAttribute("validType","dateMonth");
	}else if("true".equals(dateYear)){
		jspContext.setAttribute("validType","dateYear");
	}else if("true".equals(issue)){
		jspContext.setAttribute("validType","issue");
	}
	else
		jspContext.setAttribute("validType","date");
}
if ((this.id == null || this.id.length() == 0)) {

	int nextInt = new Random().nextInt();
	nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
			.abs(nextInt);
	this.id = "tadate_" + String.valueOf(nextInt);
	jspContext.setAttribute("id", this.id);
} 

if(name == null || "".equals(name)){
	this.name = "dto['"+this.id+"']";
	jspContext.setAttribute("name", this.name);
}

//优先 查找resultBean
ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
if(resultBean != null){
	Object v = resultBean.getFieldData()==null?null:resultBean.getFieldData().get(this.id);
	if(v !=null && !"".equals(v)){
		this.value= v.toString();
	}
}
//查找request
if(value != null && !"".equals(value) && request.getAttribute(this.id) != null){
	value = request.getAttribute(this.id).toString();
}
//查找session
if(value != null && !"".equals(value) && request.getSession().getAttribute(this.id) != null){
	value = request.getSession().getAttribute(this.id).toString();
}        	
if(value != null && !"".equals(value)){
	jspContext.setAttribute("value", this.value);
}        
if(value != null && !"".equals(value)){
	value = value.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
}
String inputStyle="textinput";
if(readOnly != null && !"false".equals(readOnly)){
	inputStyle +=" readonly";
}
if(disabled !=null && !"false".equals(disabled)){
	inputStyle +=" disabled";
}
if("true".equals(datetime)){
	inputStyle += " datetimefield";
}else if("true".equals(dateFulltime)){
	inputStyle += " dateFulltimefield";
}else if("true".equals(dateMonth)){
	inputStyle += " dateMonthfield";
}else if("true".equals(dateYear)){
	inputStyle += " dateYearfield";
}else if("true".equals(issue)){
	inputStyle += " issuefield";
}else{
	inputStyle += " datefield";
}
jspContext.setAttribute("inputStyle", inputStyle);
String textHelpStyle="";
if(textHelpWidth != null){
	String temp = "";
	if(textHelpWidth.endsWith("px")){
		temp = textHelpWidth.substring(0,textHelpWidth.length()-2);
	}else{
		temp = textHelpWidth;
	}
	if("bottomRight".equals(textHelpPosition) || "topRight".equals(textHelpPosition)){
		textHelpStyle += "width:"+temp+"px;";
	}else{
		textHelpStyle += "width:"+temp+"px;left:-"+(Integer.valueOf(temp)+10)+"px;";
	}
}else{
	if("bottomRight".equals(textHelpPosition) || "topRight".equals(textHelpPosition)){
		textHelpStyle += "width:200px;";
	}else{
		textHelpStyle += "width:200px;left:-210px;";
	}
}
%>

<%@include file="../columnhead.tag" %>

<div 
<% if (null != cssClass) {%>
class="${cssClass}" 
<%}%>
<% if (null != cssStyle) {%>
style="<%=cssStyle%>" 
<%}%>
<% if (null != columnWidth) {%>
columnWidth="${columnWidth}" 
<%}%>
<% if (null != span) {%>
span="${span}" 
<%}%>
<% if (null != toolTip) {%>
title="${toolTip}" 
<%}%>
>
<% if (null != key && !"".equals(key.trim())) {%>
	<label 
	 for="${id}" 
class="fieldLabel"
	<% if (null != labelStyle) {%>
style="${labelStyle}" 
	<%} %>
>
<% if (null != required && "true".equals(required)){%>
	<span style="color:red">*</span>
<% }%>
	${key}<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>：<%}%>
	</label>
	<%}%>	
	<%if(inputStyle.contains("readonly") || inputStyle.contains("disabled")){%>
	<div class="fielddiv2 readonly"
	<%}else{ %>
	<div class="fielddiv2"
	<%} %>
	<% if (null != labelWidth) {%>
		style="margin-left:${labelWidth}px" 
	<%}else if(null == key || "".equals(key.trim())) {%>
		style="margin-left:0px;"
	<%} %>
>

		<input type="text" 
<% if (null != id) {%>
	id="${id}" 
<%}%>
<% if (null != name) {%>
	name="${name}" 
<%}%>
<% if (null != value) {%>
	value="${value}" 
<%}%>
<% if (null != readOnly){%>
readOnly="${readOnly}" 
<%}%>
<% if (null != disabled){%>
disabled="${disabled}" 
<%}%>
<% if (null != required){%>
required="${required}" 
<%}%>
class="Wdate ${inputStyle} " 
<% if (ValidateUtil.isNotEmpty((String)jspContext.getAttribute("validType"))) {%>
validType="${validType}" 
<%}%>
<% if (null != dateMonth){%>
maxlength= "7"
<% } else if ( null != dateYear){%>
maxlength="4" 
<% } else if (null != issue){%>
maxlength="6" 
<% } else if (null != datetime){%>
maxlength="19" 
<% } else if (null != dateFulltime){%>
maxlength="23" 
<%} else { %>
maxlength="10" 
<%}%>
<% if (null != validNowTime) {%>
validNowTime="${validNowTime}"
<%}%>
<% if (null != onClick) {%>
onClick="${onClick}" 
<%}%>
<% if (null != onChange) {%>
onChange="${onChange}" 
<%}%>
<% if (null != onFocus) {%>
onFocus="${onFocus}" 
<%}%>
<% if (null != onBlur) {%>
onBlur="${onBlur}" 
<%}%>
<% if (null != cssInput) {%>
style="${cssInput}" 
<%}%>
>
<%if(textHelp != null){ %>
<div class="textInfo">
	<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>
	<div class="textInfo_content ffb_163
	<%}else{ %>
	<div class="textInfo_content ffb
	<%} %>
	 <%if(textHelpPosition != null)%>${textHelpPosition}" style="<%=textHelpStyle%>">${textHelp}</div>
	<script>
		var $textInfo = $("#<%=id%> + div.textInfo > div.textInfo_content");
		var textInfoHeight = $textInfo.height() + 45;
		<%if("topLeft".equals(textHelpPosition) || "topRight".equals(textHelpPosition)){ %>
		$textInfo.css("top","-"+textInfoHeight+"px");
		<%} %>
	</script>
</div>
<%}%>
<% if (null != readOnly){%>
<div class="shadingWdate"></div>
<%}%>
	</div>
</div>
<%@include file="../columnfoot.tag" %>
<% if (null != showSelectPanel){%>
<script type="text/javascript">
(function(factory){
	if (typeof require === 'function') {
		require(["jquery", "TaUIManager", "datetimeMask"], factory);
	} else {
		factory(jQuery);
	}
}(function($){

Ta.core.TaUICreater.addUI(
		function(){
	var dateObj = document.getElementById('${id}');
	var $topObj = $dp;
// 	var $topObj = $(window.top.document).find("div[lang='zh-cn']:has(iframe)");
	/* dateObj.onkeyup = function(){
		if (window.event.keyCode > 47){
			$dp.cal.init();
		}else if (!($(document.getElementById('${id}')).hasClass('readonly'))&&window.event.keyCode > 36&& window.event.keyCode<41){
			$topObj.css('display','block');
		}else{
			$topObj.css('display','none');
		}
	}; */
	
		dateObj.onfocus = 
			function(){
				if($(document.getElementById('${id}')).hasClass('readonly')){
					$dp.css('display','none');
// 					$(window.top.document).find("div[lang='zh-cn']:has(iframe)").css('display','none');
				}else{
				var position = {top:0,left:0};
				<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>
					position.top=6;
					position.left=-9;
				<%}%>
					WdatePicker({
				position:position,//liys,新皮肤修改后，日期控件的位置调整
				 isShowWeek:false
				 ,el:'${id}'
				<% if (null != datetime){%>
				,dateFmt:'yyyy-MM-dd HH:mm:ss'
				<% }else if (null != dateMonth){%>
				,dateFmt:'yyyy-MM'
				<% } else if (null != dateYear){%>
				,dateFmt:'yyyy'
				<% } else if (null != issue){%>
				,dateFmt:'yyyyMM'
				<% } else { %>
				,dateFmt:'yyyy-MM-dd'
				<%}%>
				<% if (null != min) {%>
				,minDate:'${min}'
				<%}%>
				<% if (null != max) {%>
				,maxDate:'${max}'
				<%}%>});
				if($topObj){
					<% if (null != isFocusShowPanel&&"false".equals(isFocusShowPanel)){%>
						$topObj.hide();
					<%}%> 
				}
				
			}
	}
	});
//requirejs
}));
</script>
<%}%>