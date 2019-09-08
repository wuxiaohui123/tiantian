<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random"%>
<%@tag import="java.util.Set"%>
<%--@doc--%>
<%@tag description='selectButtonItem,下拉按钮selectButton中的按钮' display-name='selectButtonItem' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='设置组件id，页面唯一' name='id' type='java.lang.String'%>
<%@attribute description='设置标题，不支持html格式文本' name='key' type='java.lang.String' required="true"%>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='单击事件，例如:onClick="fnOnClick()",在javascript中，function fnOnClick(){alert(111)}' name='onClick' type='java.lang.String' %>
<%--@doc--%>
<%
final Random RANDOM = new Random();
        if ((this.id == null || this.id.length() == 0)) {

			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "taSelectButtonItem_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
		}
 %>
<div id="${id}" 
<% if (cssClass != null){%>
class="${cssClass} select_button_item" 
<%}else{%>
class="select_button_item"
<%}%>
<% if (cssStyle != null){%>
style="${cssStyle}" 
<%}%>
<% if (onClick != null){%>
onclick="${onClick}" 
<%}%>
<% if (toolTip != null){%>
title="${toolTip}" 
<%}%>
>
${key }
</div>
<script>
	$(function(){
		$("div.select_button_item").bind("click",function(){
			$(this).parent().parent().hide(); 
		})
	})
</script>