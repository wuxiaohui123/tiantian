<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="java.util.Enumeration"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%--@doc--%>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='设置是否显示，默认为显示:true' name='display' type='java.lang.String' %>
<%@attribute description='组件id' name='id' required='true' type='java.lang.String' %>
<%@attribute description='组件的label标签' name='key' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='onBlur' name='onBlur' type='java.lang.String' %>
<%@attribute description='onChange' name='onChange' type='java.lang.String' %>
<%@attribute description='onClick' name='onClick' type='java.lang.String' %>
<%@attribute description='onFocus' name='onFocus' type='java.lang.String' %>
<%@attribute description='onKeydown' name='onKeydown' type='java.lang.String' %>
<%@attribute description='设置是否必输' name='required' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String' %>
<%@attribute description='label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='最大字符数' name='maxLength' type='java.lang.String' %>
<%@attribute description='最小字符数' name='minLength' type='java.lang.String' %>
<%@attribute description='true/false,设置为只读，默认为false' name='readOnly' type='java.lang.String' %>
<%@attribute description='设置textarea的高度,例如:height="200px"' name='height' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%@tag description='富文本框' display-name='textarea' %>
<%@attribute description='onmouseover' name='onmouseover' type='java.lang.String' %>
<%@attribute description='onmouseout' name='onmouseout' type='java.lang.String' %>
<%--@doc--%>
<%
		final Random RANDOM = new Random();

		if (labelStyle != null){
			String style = "";
			if (labelWidth != null){
				style = "width:" + labelWidth + "px";
			}
			style = labelStyle + ";" + style;
			labelStyle = style;
			//addParameter("labelStyle", this.labelStyle);
			jspContext.setAttribute("labelStyle", labelStyle);
		}
		else{
			if (labelWidth != null){
				//addParameter("labelStyle", "width:" + findString(labelWidth) + "px");
				labelStyle = "width:" + labelWidth + "px";
			    jspContext.setAttribute("labelStyle", labelStyle);
			}
		}
		if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
			if (null != cssClass){
				cssClass = "fielddiv fielddiv_163" + cssClass;
				//addParameter("cssClass", cssClass);
				 jspContext.setAttribute("cssClass",cssClass);
			}else{
				//addParameter("cssClass", "fielddiv");
				cssClass="fielddiv fielddiv_163";
				jspContext.setAttribute("cssClass",cssClass);
			}
		}else{
			if (null != cssClass){
				cssClass = "fielddiv " + cssClass;
				//addParameter("cssClass", cssClass);
				 jspContext.setAttribute("cssClass",cssClass);
			}else{
				//addParameter("cssClass", "fielddiv");
				cssClass="fielddiv";
				jspContext.setAttribute("cssClass",cssClass);
			}
		}
		if ("false".equals(display) || "none".equals(display)){
			if (cssStyle == null){
				cssStyle = "display:none;";
			}
			else{
				cssStyle += ";display:none;";
			}
			jspContext.setAttribute("cssStyle",cssStyle);
		}
		
		String tcssStyle = "";
		
		if (height != null) {
			String tmpw = height;
			if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
				if(tmpw.endsWith("px")){
					tmpw = tmpw.substring(0,tmpw.length()-2);
				}
				tcssStyle = "height:" + (Integer.valueOf(tmpw)+12) + "px;";
				jspContext.setAttribute("height",tmpw+"px");
			}else{
				jspContext.setAttribute("height",tmpw);
				tcssStyle = "height:"+(tmpw.endsWith("px")?tmpw:(tmpw+"px"))+";";
			}
			if(cssStyle!=null){
				cssStyle = tcssStyle +cssStyle;
			}else{
				cssStyle = tcssStyle;
			}
//			addParameter("cssStyle", findString(cssStyle));
            jspContext.setAttribute("cssStyle",cssStyle);
		}

		if ((id == null || id.length() == 0)){

			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);
			id = "tatext_" + String.valueOf(nextInt);
			//addParameter("id", this.id);
			jspContext.setAttribute("id",id);
		}
		if (name == null || "".equals(name)){
			name = "dto['" + id + "']";
			//addParameter("name", this.name);
			jspContext.setAttribute("name",name);
		}

		// 优先 查找resultBean
		ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
		if (resultBean != null){
			Object v = resultBean.getFieldData() == null ? null : resultBean.getFieldData().get(id);
			if (v != null && !"".equals(v)){
				value = v.toString();
			}
		}
		// 查找request
		if (value != null && !"".equals(value) && request.getAttribute(id) != null){
			value = request.getAttribute(id).toString();
		}
		// 查找session
		if (value != null && !"".equals(value) && request.getSession().getAttribute(id) != null){
			value = request.getSession().getAttribute(id).toString();
		}
		if (value != null && !"".equals(value)){
			//addParameter("value",findString( this.value));
			 jspContext.setAttribute("value",value);
		}
		
		if(value != null && !"".equals(value)){
			value = TagUtil.replaceXSSTagValue(value);
		}
		String inputStyle = "textinput";
		if (readOnly != null && !"false".equals(readOnly)){
			inputStyle += " readonly";
		}
		if (disabled != null && !"false".equals(disabled)){
			inputStyle += " disabled";
		}
		//addParameter("inputStyle", inputStyle);
 jspContext.setAttribute("inputStyle",inputStyle);

 %>



<%@include file="../columnhead.tag" %>
<div 
<%if( cssClass!=null){%>
class="${cssClass}" 
<%}%>
<%if( cssStyle!=null){%>
style="${cssStyle}" 
<%}%>
<%if( columnWidth!=null){%>
columnWidth="${columnWidth}" 
<%}%>
<%if( span!=null){%>
span="${span}" 
<%}%>
<%if( toolTip!=null){%>
title="${toolTip}" 
<%}%>
>
<%if( key!=null && !"".equals(key.trim())){%>
	<label 
	 for="${id}" 
class="fieldLabel"
	<%if( labelStyle!=null){%>
style="${labelStyle}" 
	<%}%>
>
<%if("true".equals(required)){%>
<span style="color:red">*</span>
<%}%>
${key}<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>：<%}%>
	</label>
	<%}%>	
	<div class="fielddiv2"
	<%if( labelWidth!=null){%>
	style="margin-left:${labelWidth}px;height:${height}" 
	<%}else  if(null == key || "".equals(key.trim())) {%>
	style="margin-left:0px;height:${height}" 
	<%}else {%>
	style="height:${height}" 
	<%} %>
>

		<textarea 
<%if( id!=null){%>
id="${id}" 
<%}%>
<%if( height!=null){%>
style="height:${height}" 
<%}%>
<%if( name!=null){%>
name="${name}" 
<%}%>
<%if( readOnly!=null&& readOnly.equals("true") ){%>
readOnly="${readOnly}" 
<%}%>
<%if( disabled!=null&& disabled.equals("true")){%>
disabled="${disabled}" 
<%}%>
<%if("true".equals(required)){%>
required="${required}" 
<%}%>
class="${inputStyle}"
<%if( maxLength!=null){%>
maxLength="${maxLength}" 
validType="length[0,${maxLength}]" 
<%}%>
<%if( onClick!=null){%>
onClick="${onClick}" 
<%}%>
<%if( onChange!=null){%>
onChange="${onChange}" 
<%}%>
<%if( onKeydown!=null){%>
onKeydown="${onKeydown}" 
<%}%>
<%if( onBlur!=null){%>
onBlur="${onBlur}" 
<%}%>
<%if( onFocus!=null){%>
onFocus="${onFocus}" 
<%}%>
<%if( onmouseover!=null){%>
onmouseover="${onmouseover}" 
<%}%>
<%if( onmouseout!=null){%>
onmouseout="${onmouseout}" 
<%}%>
>
<%if( value!=null){%>${value}<%}%></textarea>
	</div>
</div>
<%@include file="../columnfoot.tag" %>