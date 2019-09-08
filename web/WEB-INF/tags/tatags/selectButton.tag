<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="java.util.Random"%>
<%@tag import="java.util.Set"%>
<%@tag import="com.yinhai.webframework.security.filter.SecurityInterceptorFilter"%>
<%@tag import="com.yinhai.webframework.session.UserSession"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='selectButton按钮,下拉按钮，只能放selectButtonItem' display-name='selectButton' %>
<%@attribute description='数字，当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='热键，如果只输入一个英文字母默认是alt+字母的组合，还可以输入ctrl+s这样的值' name='hotKey' type='java.lang.String' %>
<%@attribute description='设置按钮图标:例如icon="icon-edit"' name='icon' type='java.lang.String' %>
<%@attribute description='true/false,设置button为toolbar按钮样式，默认为false' name='asToolBarItem' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='设置是否显示，默认为显示:true' name='display' type='java.lang.String' %>
<%@attribute description='设置组件id，页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='设置标题，不支持html格式文本' name='key' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='按钮样式，是否为确认类型，比如保存，更新等操作，默认false' name='isok' type='java.lang.String' %>
<%--@doc--%>
<%
final Random RANDOM = new Random();
		UserSession _us = UserSession.getUserSession(request);
		if(_us != null && _us.getUser() != null){
	         if (id != null && id.indexOf(".") > -1) {
	        	String url = id.replace(".", "/");
	        	url = "/" + url.substring(0, url.lastIndexOf("/")) + "!"
	        			+ url.substring(url.lastIndexOf("/") + 1) + ".do";
	        	Set<String> perviewSet  = (Set<String>) request.getSession().getAttribute(SecurityInterceptorFilter.USER_PERVIEW_FLAG);
	        	if(perviewSet != null && perviewSet.contains(url)){
	        		jspContext.setAttribute("outputting", "true");
	        	} else {
	        		if ("developer".equals(_us.getUser().getLoginId()) || "super".equals(_us.getUser().getLoginId())) {
	        			jspContext.setAttribute("outputting", "true");
	        		}
	        	}
	        } else {
	        	jspContext.setAttribute("outputting", "true");
	        }
		}else{
			jspContext.setAttribute("outputting", "true");
		}
        if (null != hotKey) {
        	if(hotKey.length()==1)hotKey = "Alt+"+hotKey;
            jspContext.setAttribute("hotKey", hotKey);
        }
        
        if("false".equals(display)|| "none".equals(display)){
        	if(this.cssStyle ==null){
        		this.cssStyle = "display:none;";
        	}else{
        		this.cssStyle += ";display:none;";
        	}
        	jspContext.setAttribute("cssStyle", cssStyle);
        }
    	if(cssClass==null){
    		cssClass = "select_button";
    	}else{
    		cssClass = "select_button "+cssClass;
    	}
        if("true".equals(asToolBarItem)){
        	cssClass +=" toolbarbt";
        }
        jspContext.setAttribute("cssClass",cssClass);
        if ((this.id == null || this.id.length() == 0)) {

			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "taSelectButton_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
		}
 %>
 <%@include file="../columnhead.tag" %>
<%if("true".equals(jspContext.getAttribute("outputting"))){ %>
<div class="${cssClass}" style="float:left;">
<button id="${id}" 
class="sexybutton_163" 
<% if (hotKey != null){%>
hotKey="${hotKey}" 
<%}%>
<% if (cssStyle != null){%>
style="${cssStyle}" 
<%}%>
type="button"
onClick="fnButtonGroupClick(this)" 
<% if (disabled != null && "true".equals(disabled)){%>
disabled="${disabled}"
<%}%>
<% if (columnWidth != null){%>
columnWidth="${columnWidth}"
<%}%>
<% if (toolTip != null){%>
title="${toolTip}" 
<%}%>>
<% if (icon != null){%>
<span class="button_icon ${icon} "></span>
<%} %>
<span class="button_span select_span <% if (isok != null && "true".equals(isok)){%>isok<%} %>" <% if (icon != null){%>
style="padding-left:25px;"
<%} %>
>
      <% if (key != null){%>
        ${key}
      <%}else{%>
	 &nbsp;	
	  <%}%>
</span>
<span class="select_button_arrow"></span>
</button>
<div class="select_content ffb_163">
	<div class="content">
	<jsp:doBody/> 	
	</div>
</div>
</div>
<script type="text/javascript">
	function fnButtonGroupClick(o){
		var c = $(o).next("div.select_content");
		if(c.css("display") == "none"){
			$("div.select_content").hide();
			if($(document).height() - c.parent().offset().top > c.outerHeight(true)){
				c.show();
			}else{
				c.css("top",-(c.outerHeight(true))).show();
			}
		}else{
			$("div.select_content").hide();
		}
		
	}
	$(function(){
		$(document).bind("mousedown.selectButton", function(event){
			var $b = $(event.target).parents(".sexybutton_163");
			var $c = $(event.target).parents(".select_content");
			if ($b && $b.length > 0) {
				
			}else if($c && $c.length > 0){
				
			}else{
				$("div.select_content").hide();
			}
		});
	})
</script>
<%}%>
<%@include file="../columnfoot.tag" %>