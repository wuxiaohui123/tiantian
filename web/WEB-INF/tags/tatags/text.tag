<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='文本框' display-name="text" %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top：10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='设置是否显示，默认为显示：true' name='display' type='java.lang.String' %>
<%@attribute description='组件id页面唯一' name='id' required='true' type='java.lang.String' %>
<%@attribute description='当required属性为true时，设置默认错误提示信息' name='toolTip' type='java.lang.String' %>
<%@attribute description='组件的label标签,不支持html标签' name='key' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准，如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='onBlur事件，当失去焦点时调用，此处填写函数调用如onBlur="fnBlur(this)"' name='onBlur' type='java.lang.String' %>
<%@attribute description='onChange事件，当onChange值改变时调用，此处填写函数调用如onChange="fnChange(this)"' name='onChange' type='java.lang.String' %>
<%@attribute description='onClick事件，当单击控件时调用，此处填写函数调用如onClick="fnClick(this)"' name='onClick' type='java.lang.String' %>
<%@attribute description='点击放大镜,在该点击事件前发生，例如:popWinBeforeClick="fnPopWinBeforeClick",在js中,function fnPopWinBeforeClick(){return true},一定要有返回值,且只有返回true时才执行点击事件' name='popWinBeforeClick' type='java.lang.String' %>
<%@attribute description='onFocus事件，当控件获取焦点时，此处填写函数调用如onFocus="fnFocus(this)"' name='onFocus' type='java.lang.String' %>
<%@attribute description='onKeydown，当按下键盘是调用，此处填写函数调用如onKeydown="fnKeydown(this)"' name='onKeydown' type='java.lang.String' %>
<%@attribute description='true/false,设置是否必输，默认false，设置后代小红星' name='required' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String' %>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String' %>
<%@attribute description='验证类型，如:url，email，ip，integer，number，idcard，mobile，issue，chinese，zipcode，compare' name='validType' type='java.lang.String' %>
<%@attribute description='自定义验证,validType属性必须设置成self才会生效,validFunction为回调函数，例如validFunction=“fnSelfValidate”，在js中必须返回一个形如：{"message":"只能为数字","result":false}的json对象，message表示验证失败提示信息，result表示是否通过验证' name='validFunction' type='java.lang.String' %>
<%@attribute description='label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='最大字符数' name='maxLength' type='java.lang.String' %>
<%@attribute description='最小字符数' name='minLength' type='java.lang.String' %>
<%@attribute description='true/false,设置为只读，默认为false' name='readOnly' type='java.lang.String' %>
<%@attribute description='可以设置input元素的类型，如：password，text，file等，默认text' name='type' type='java.lang.String' %>
<%@attribute description='单独设置input元素的css样式,例如:cssInput="font-size:20px;color:red"' name='cssInput' type='java.lang.String' %>
<%@attribute description='设置是否显示放大镜按钮' name='popWin' type='java.lang.String' %>
<%@attribute description='点击放大镜按钮后弹出win的url' name='popWinUrl' type='java.lang.String' %>
<%@attribute description='点击放大镜按钮后弹出win的url传递参数，此方式为get方式传参' name='popParam' type='java.lang.String' %>
<%@attribute description='点击放大镜按钮后弹出win的时传递的控件id（非容器id）,以逗号分开，此方式为get方式传参' name='popSubmitIds' type='java.lang.String' %>
<%@attribute description='点击放大镜按钮后弹出win的方式，有parent，top，self，默认self，例如:popWinType="top"，此时弹出框将以top方式弹出' name='popWinType' type='java.lang.String' %>
<%@attribute description='点击放大镜按钮后弹出win的宽度,例如:popWinWidth="800"' name='popWinWidth' type='java.lang.String' %>
<%@attribute description='点击放大镜按钮后弹出win的高度,例如:popWinHeight="500"' name='popWinHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%@attribute description='是否显示软键盘' name='softkeyboard' type='java.lang.String' %>
<%@attribute description='显示输入框提示图标，内容自定义。例如textHelp="默认显示在左下角"' name='textHelp' type='java.lang.String' %>
<%@attribute description='textHelp宽度，默认200。例如textHelpWidth="200"' name='textHelpWidth' type='java.lang.String' %>
<%@attribute description='textHelp位置{topLeft,topRight,bottomLeft,bottomRight}，默认bottomLeft。例如textHelpPosition="bottomRight"' name='textHelpPosition' type='java.lang.String' %>
<%@attribute description='提示文字' name='placeholder' type='java.lang.String' %>
<%@attribute description='在输入框尾部显示一个可以按的图标按钮' name='clickIcon' type='java.lang.String' %>
<%@attribute description='在输入框尾部按钮的按钮事件' name='clickIconFn' type='java.lang.String' %>
<%@attribute description='鼠标移动到尾部按钮上给得提示信息' name='clickIconTitle' type='java.lang.String' %>

<%--@doc--%>
<% 
if ((id == null || id.length() == 0)) {
	Random random = new Random();
	int nextInt = random.nextInt();
	nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
	id = "tatext_" + String.valueOf(nextInt);
}
if(name == null || "".equals(name)){
   	name = "dto['"+id+"']";
}
ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
if(resultBean != null){
	Object v =  resultBean.getFieldData()==null?null:resultBean.getFieldData().get(id);
	if(v !=null && !"".equals(v)){
	   value= v.toString();
	}
}
//查找request
if(value != null && !"".equals(value) && request.getAttribute(id) != null){
	value = request.getAttribute(id).toString();
}
//查找session
if(value != null && !"".equals(value) && request.getSession().getAttribute(id) != null){
	value = request.getSession().getAttribute(id).toString();
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
		labelStyle = "width:"+labelWidth+"px";
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

if("false".equals(display) || "none".equals(display)){
   	if(cssStyle ==null){
   		cssStyle = "display:none;";
   	}else{
   		cssStyle += ";display:none;";
   	}
}
if(validType == null || "length".equals(validType))
{
	if(null != maxLength && null != minLength)
	{
		validType="length["+ minLength+","+maxLength+"]";
	}
	else if(null == maxLength && null != minLength)
	{
		validType="length["+ minLength+",9999]";
	}
	else if(null != maxLength && null == minLength)
	{
		validType="length[0,"+maxLength+"]";
	}
}
String inputStyle="textinput";
if(readOnly != null && !"false".equals(readOnly)){
	inputStyle +=" readonly";
}
if(disabled !=null && !"false".equals(disabled)){
	inputStyle +=" disabled";
}
if(type == null){
	type = "text";
}

if(value != null && !"".equals(value)){
	value = TagUtil.replaceXSSTagValue(value);
}

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
<% if(cssClass!=null){%>
class="<%=cssClass%>" 
<%}%>
<% if(cssStyle!=null){%>
style="<%=cssStyle%>" 
<%}%>
<% if(columnWidth!=null){%>
columnWidth="${columnWidth}" 
<%}%>
<% if(span!=null){%>
span="${span}" 
<%}%>
> 
<% if(key!=null && !"".equals(key.trim())){%>
	<label 
	 for="<%=id %>" 
class="fieldLabel" 
	<% if (null != labelStyle) {%>
style="<%=labelStyle%>" 
	<%} %>
> 
<% if("true".equals(required)){%>
<span style="color:red">*</span>
<%}%>
${key}<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>：<%} %>
	</label> 
	<%} %>
	<%
	if(readOnly != null && !"false".equals(readOnly)){
	%>
	<div class="fielddiv2 readonly" 
	<%
	}else if(disabled !=null && !"false".equals(disabled)){%>
	<div class="fielddiv2 disabled"
	<%}else{ %>
	<div class="fielddiv2" 
	<%} %>
	<% if (null != labelWidth) {%>
		style="margin-left:${labelWidth}px" 
	<%}else if(null == key || "".equals(key.trim())) {%>
		style="margin-left:0px;"
	<%} %>
> 
<% if(popWin!=null && !"false".equals(popWin)){%>
<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>
<span class="popWin" onClick="fn_<%=id %>()"></span> 
<%}else{ %>
<span class="innerIcon popWin_163" onClick="fn_<%=id %>()"></span> 
<%} %>
<script>
	function fn_<%=id %>() { 
		<% if(popWinBeforeClick!=null){%>
			var result = ${popWinBeforeClick}();
			if(result == true || result == "true"){
			}else{
				return;
			}
		<%}%>
		var param = {}; 
		<% if(popParam!=null){%>
			param = ${popParam}; 
		<%}%> 
		var submitIds = ""; 
		<% if(popSubmitIds!=null){%>
			submitIds = "${popSubmitIds}";
		<%}%>
		<% if(key!=null){%>
			Base.openWindowWithSubmitIds
			("w_<%=id %>","${key}","${popWinUrl}",submitIds,param,
		<%}else{%>
			Base.openWindowWithSubmitIds
			("w_<%=id %>","","${popWinUrl}",submitIds,param,
		<%}%>
		<% if(popWinWidth!=null){%>"${popWinWidth}"<%}else{%>"800"<%}%>
		,
		<% if(popWinHeight!=null){%>"${popWinHeight}"<%}else{%>"500"<%}%>
		,null,null,true,null,null<% if(popWinType!=null){%>,"${popWinType}"<%}%>);
	}
	function <%=id %>SetBack(value) {
		if (value != undefined) {
			Base.setValue(<%=id %>, value);
		}
	}
</script>
<%} else {
	if (clickIcon != null) {
%>
	<span class="innerIcon ${clickIcon}" title="${clickIconTitle}" onClick="${clickIconFn}"></span> 
<%
	}
  }
%>
<% if(softkeyboard!=null){%>
<span class="softkeyboard" onclick="fn_softkeyboard_<%=id %>()"></span>
<script>
function fn_softkeyboard_<%=id %>(){
    new KeyBoard.SoftKeyBoard("<%=id %>");
}
</script>
<%}%>
		<input type="<%=type %>" 
<% if(id!=null){%>
id="<%=id %>" 
<%}%>
<%if (toolTip != null) {%>
toolTip = "${toolTip}"
<%} %>
<% if(name!=null){%>
name="<%=name %>" 
<%}%>
<% if(value!=null){%>
value="<%=value %>" 
<%}%>
<% if(placeholder!=null){%>
placeholder="<%=placeholder %>" 
<%}%>
<% if(readOnly!=null && !"false".equals(readOnly)){%>
readOnly="${readOnly}" 
<%}%>
<% if(disabled!=null && !"false".equals(disabled)){%>
disabled="${disabled}" 
<%}%>
<% if("true".equals(required)){%>
required="${required}" 
<%}%>
class="<%=inputStyle%>" 
<% if(validType!=null){%>
validType="<%=validType%>" 
<%}%>
<% if(validFunction!=null){%>
validFunction="<%=validFunction%>" 
<%}%>
<% if(maxLength!=null){%>
maxLength="${maxLength}" 
<%}%>
<% if(onClick!=null){%>
onClick="${onClick}" 
<%}%>
<% if(onChange!=null){%>
onChange="${onChange}" 
<%}%>
<% if(onKeydown!=null){%>
onKeydown="${onKeydown}" 
<%}%>
<% if(onBlur!=null){%>
onBlur="${onBlur}" 
<%}%>
<% if(onFocus!=null){%>
onFocus="${onFocus}" 
<%}%>
<% if(cssInput!=null){%>
style="${cssInput}" 
<%}%>
>
<%if(placeholder != null){ %>
	<script type="text/javascript">
		(function(factory){
			if (typeof require === 'function') {
				require(["api.forms"], factory);
			} else {
				factory(jQuery);
			}
		}(function($){
			var ele = document.getElementById('<%=id %>');
			Base.funPlaceholder(ele);
		}));
	</script>
<%} %>
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
	</div>
</div>
<%@include file="../columnfoot.tag" %>