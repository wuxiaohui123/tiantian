<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%-- @doc --%>
<%@tag description='数字输入框' display-name="number"%>
<%@attribute name="id"  type="java.lang.String" rtexprvalue="true" required="true" description="组件id页面唯一"%>
<%@attribute name="key" type="java.lang.String" rtexprvalue="true" description="组件的label标签,不支持html标签 "%>
<%@attribute name="name" type="java.lang.String" rtexprvalue="true" description="组件的name属性，一般可以不设置，系统会根据id自动生成类似dto['id']这样的名称，如果你自己设置的了name属性，那么将以您设置的为准，如果你没有以dto方式设置，后台将不能通过dto来获取数据"%>
<%@attribute name="span" type="java.lang.String" rtexprvalue="true" description="当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span='2'表示跨两列 "%>
<%@attribute name="columnWidth" type="java.lang.String" rtexprvalue="true" description="设置layout为column布局的时候自定义占用容器行宽度百分比，可设置值为0-1之间的小数，如:0.1则表示占该行的1/10"%>
<%@attribute name="cssClass" type="java.lang.String" rtexprvalue="true" description="给该组件添加自定义样式class，如:cssClass='no-padding'"%>
<%@attribute name="cssStyle" type="java.lang.String" rtexprvalue="true" description="给该组件添加自定义样式，如:cssStyle='padding-top:10px'表示容器顶部向内占用10个像素"%>
<%@attribute name="onClick" type="java.lang.String" rtexprvalue="true" description="onClick事件，当单击控件时调用，此处填写函数调用如onClick='fnClick(this)'"%>
<%@attribute name="onChange" type="java.lang.String" rtexprvalue="true" description="onChange事件，当onChange值改变时调用，此处填写函数调用如onChange='fnChange(this)'"%>
<%@attribute name="onFocus" type="java.lang.String" rtexprvalue="true" description="onFocus事件，当控件获取焦点时，此处填写函数调用如onFocus='fnFocus(this)'"%>
<%@attribute name="onBlur" type="java.lang.String" rtexprvalue="true" description="onBlur事件，当失去焦点时调用，此处填写函数调用如onBlur='fnBlur(this)'"%>
<%@attribute name="onKeydown" type="java.lang.String" rtexprvalue="true" description="onKeydown，当按下键盘是调用，此处填写函数调用如onKeydown='fnKeydown(this)'"%>
<%@attribute name="disabled" type="java.lang.String" rtexprvalue="true" description="true/false,设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台，默认为false"%>
<%@attribute name="readOnly" type="java.lang.String" rtexprvalue="true" description="true/false,设置为只读，默认为false"%>
<%@attribute name="display" type="java.lang.String" rtexprvalue="true" description="true/false,设置是否显示，默认为显示:true"%>
<%@attribute name="required" type="java.lang.String" rtexprvalue="true" description="true/false,设置是否必输，默认false，设置后代小红星"%>
<%@attribute name="labelWidth" type="java.lang.String" rtexprvalue="true" description="label及key占的宽度，如labelWidth='120'"%>
<%@attribute name="labelStyle" type="java.lang.String" rtexprvalue="true" description="label自定义样式"%>
<%@attribute name="cssInput" type="java.lang.String"  rtexprvalue="true" description="单独设置input元素的css样式,例如:cssInput='font-size:20px;color:red'"%>
<%@attribute name="value" type="java.lang.String" rtexprvalue="true" description="组件页面初始化的时候的默认值"%>
<%@attribute name="toolTip" type="java.lang.String" rtexprvalue="true" description="鼠标移过提示文本"%>
<%@attribute name="precision" type="java.lang.String" rtexprvalue="true" description="小数位数，如precision='2',默认小数位为0"%>
<%@attribute name="asAamount" type="java.lang.String" rtexprvalue="true" description="true/false,设置数字显示为金额，每三位用逗号隔开。默认为false"%>
<%@attribute name="amountPre" type="java.lang.String" rtexprvalue="true" description="String,当asAamount设置为true的时候可以设置金额前面追加的符号，比如￥或$等"%>
<%@attribute name="max" type="java.lang.String" rtexprvalue="true" description="最大值"%>
<%@attribute name="min" type="java.lang.String" rtexprvalue="true" description="最小值 "%>
<%@attribute name="numberRound" type="java.lang.String" rtexprvalue="true" description="true/false,当numberRound设置为true时表示四舍五入,false表示不四舍五入,默认true"%>
<%@attribute name="alignLeft" type="java.lang.String" rtexprvalue="true" description="true/false,设置数字是否居左显示。默认为false"%>
<%@attribute name="bpopTipMsg" type="java.lang.String" rtexprvalue="true" description="鼠标移过输入对象pop提示文本 "%>
<%@attribute name="bpopTipWidth" type="java.lang.String" rtexprvalue="true" description="鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小"%>
<%@attribute name="bpopTipHeight" type="java.lang.String" rtexprvalue="true" description="鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小"%>
<%@attribute name="bpopTipPosition" type="java.lang.String" rtexprvalue="true" description="鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top"%>
<%@attribute name="softkeyboard" type="java.lang.String" rtexprvalue="true" description="是否显示软键盘"%>
<%@attribute description='显示输入框提示图标，内容自定义。例如textHelp="默认显示在左下角"' name='textHelp' type='java.lang.String' %>
<%@attribute description='textHelp宽度，默认200。例如textHelpWidth="200"' name='textHelpWidth' type='java.lang.String' %>
<%@attribute description='textHelp位置{topLeft,topRight,bottomLeft,bottomRight}，默认bottomLeft。例如textHelpPosition="bottomRight"' name='textHelpPosition' type='java.lang.String' %>
<%@attribute description='提示文字' name='placeholder' type='java.lang.String' %>
<%-- @doc --%>
<%
  String validType = null;
  String inputStyle = "textinput numberfield";
  if ((id == null || id.length() == 0)) {
     Random random = new Random();
	 int nextInt = random.nextInt();
	 nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
	 id = "tanumber_" + String.valueOf(nextInt);
  } 
  if(alignLeft != null){
     inputStyle += " alignleft";
  }
  if(asAamount != null){
     inputStyle += " amountfield";
  }
  if(readOnly != null && !"false".equals(readOnly)){
     inputStyle +=" readonly";
  }
  if(disabled !=null && !"false".equals(disabled)){
     inputStyle +=" disabled";
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
  if(null != max && null != min){
	 validType = "number['"+ min+"','"+max+"']";
  }
  else if(null == max && null != min){
	 validType = "number["+ min+",'']";
  }
  else if(null != max && null == min){
	 validType = "number['',"+ max+"]";
  }else{
	 validType = "number";
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
  if("false".equals(display) || "none".equals(display)){
      if(cssStyle ==null){
    		cssStyle = "display:none;";
      }else{
    		cssStyle += ";display:none;";
      }
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
<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>
<div class="fielddiv fielddiv_163 ${cssClass}" 
<%}else{ %>
<div class="fielddiv ${cssClass}" 
<%} %>
<% if (cssStyle != null){%>
  style="<%=cssStyle %>" 
<%}%>
<% if (columnWidth != null){%>
  columnWidth="${columnWidth}" 
<%}%>
<% if (span != null){%>
  span="${span}" 
<%}%>
<% if (toolTip != null){%>
  title="${toolTip}"
<%}%>
> 
<% if (key != null && !"".equals(key.trim())){%>
<label  for="<%=id %>"  class="fieldLabel" 
<% if (labelStyle != null){%>
  style="<%=labelStyle %>"
<%}%>
> 
	<%if("true".equals(required)){%>
        <span style="color:red">*</span>
	<%}%>
${key}<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>：<%}%>
</label> 
<%} %>
<%if(inputStyle.contains("readonly") || inputStyle.contains("disabled")){ %>
<div class="fielddiv2 readonly"
<%}else{ %>
<div class="fielddiv2"
<%} %> 
<% if (labelWidth != null){%>
  style="margin-left:${labelWidth}px"
  <%}else if(null == key || "".equals(key.trim())) {%>
   style="margin-left:0px;"
<%}%>
>
<% if (softkeyboard != null){%>
<span class="softkeyboard" onclick="fn_softkeyboard_${id}()"></span>
<script>
function fn_softkeyboard_<%=id %>(){
    new KeyBoard.SoftKeyBoard("<%=id %>");
};
</script>
<%}%>
<input type="text" id="<%=id %>"
<% if (numberRound != null){%> 
  numberRound="${numberRound}" 
<%}%>
  name="<%=name %>"
<% if (value != null){%>   
  value="<%=value %>"
<%}%>  
<% if(placeholder!=null){%>
placeholder="<%=placeholder %>" 
<%}%>
<% if (readOnly != null){%>
  readOnly="${readOnly}"
<%}%>
<% if (disabled != null){%>
  disabled="${disabled}"
<%}%>
<% if ("true".equals(required)){%>
  required="${required}"
<%}%>
  class="<%=inputStyle %>"
  validType="<%=validType %>"
<% if (max != null){%>
  max="${max}" 
<%}%>
<% if (min != null){%>
  min="${min}"
<%}%>
<% if (onClick != null){%>
  onClick="${onClick}"
<%}%>
<% if (onChange != null){%>
  onChange="${onChange}"
<%}%>
<% if (onKeydown != null){%>
  onKeydown="${onKeydown}"
<%}%>
<% if (onBlur != null){%>
  onBlur="${onBlur}"
<%}%>
<% if (onFocus != null){%>
  onFocus="${onFocus}"
<%}%>
<% if (precision != null){%>
  precision="${precision}"
<%}%>
<% if (amountPre != null){%>
  amountPre="${amountPre}"
<%}%>
<% if (cssInput != null){%>
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