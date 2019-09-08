<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="java.util.Enumeration"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@attribute description='热键，如果只输入一个英文字母默认是atl+字母的组合，还可以输入ctrl+s这样的值' name='hotKey' type='java.lang.String' %>
<%@attribute description='设置按钮文本' name='key' type='java.lang.String' %>
<%@attribute description='设置按钮图标:例如icon="icon-edit"' name='icon' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='设置组件id' name='id' type='java.lang.String' %>
<%@attribute description='设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台' name='disabled' type='java.lang.String' %>
<%@attribute description='单击事件' name='onClick' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top：10px' name='cssStyle' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String'%>
<%@attribute description='设置是否显示，默认为显示：true' name='display' type='java.lang.String'%>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String'%>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String'%>
<%@tag description='工具栏元素' display-name='toolbarItem' %>
<%--@doc--%>

<%
  final Random RANDOM = new Random();
if (null != hotKey) {
        	if(hotKey.length()==1)
        	hotKey = "Alt+"+hotKey;
            jspContext.setAttribute("hotKey", hotKey);
        }
                
        if("false".equals(display)|| "none".equals(display)){
        	if(cssStyle ==null){
        		cssStyle = "display:none;";
        	}else{
        		cssStyle += ";display:none;";
        	}
        }
        jspContext.setAttribute("cssStyle", cssStyle);
        if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
        	if(cssClass==null){
        		cssClass = "sexybutton_163 toolbarbt";
        	}else{
        		cssClass = "sexybutton_163 toolbarbt"+cssClass;
        	}
        }else{
        	if(cssClass==null){
        		cssClass = "sexybutton toolbarbt";
        	}else{
        		cssClass = "sexybutton toolbarbt"+cssClass;
        	}
        }
        jspContext.setAttribute("cssClass", cssClass);
       if ((id == null || id.length() == 0)) {

			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tatoolbarItem_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
		} 



%>


<%@include file="../columnhead.tag" %>
<button id="${id}" 
class="${cssClass}" 
type="button" 
<%if( hotKey!=null){ %>
 hotKey="${hotKey}" 
<%}%>
<%if( cssStyle!=null){ %>
 style="${cssStyle}" 
<%}%>
<%if( onClick!=null){ %>
 onClick="${onClick}" 
<%}%>
<%if( disabled!=null){ %>
 disabled="${disabled}" 
<%}%>
<%if( toolTip!=null){ %>
title="${toolTip}" 
<%}%>
<%if( columnWidth!=null){ %>
columnWidth="${columnWidth}" 
<%}%>
>
<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>
<span class="button_span " 
>
      <% if (key != null){%>
        ${key}
      <%}else{%>
	 &nbsp;	
	  <%}%>
</span>
<%}else{ %>
<span>
		<span>	
		    <% if (icon != null){%>
			<span style="height:16px;width:14px;padding-left: 0px;float: left;margin-top: 3px;" class="${icon}"></span>
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
<%@include file="../columnfoot.tag" %>




