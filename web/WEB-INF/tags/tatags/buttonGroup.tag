<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='buttonGroup,按钮组合,只能放button,submit,selectButton,如果button或者submit，要与selectButton混用，则必须外层套上buttonGroup组件' display-name="buttonGroup" %>
<%@attribute description='center/left/right，默认center。设置容器内button的对齐方式，例如:align="center"' name='align' type='java.lang.String' %>
<%@attribute description='设置组件id，页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='数字，当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%--@doc--%>
<%
final Random RANDOM = new Random();
if(cssClass==null){
	cssClass = "buttonGroup";
}else{
	cssClass = "buttonGroup "+cssClass;
}
if("right".equals(align) || "left".equals(align)){
	cssClass += " "+align;
}else{
	cssClass += " center";
}
 jspContext.setAttribute("cssClass",cssClass);
if ((this.id == null || this.id.length() == 0)) {

	int nextInt = RANDOM.nextInt();
	nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
			.abs(nextInt);
	id = "taButtonGroup_" + String.valueOf(nextInt);
	jspContext.setAttribute("id", id);
}
%>
 <%@include file="../columnhead.tag" %>
<div 
id="${id}" 
class="${cssClass}"
<% if (cssStyle != null){%>
  style="${cssStyle}"	 
<%}%>
<% if (span != null){%>
  span="${span}"	 
<%}%>	
<% if (columnWidth != null){%>
  columnWidth="${columnWidth}"	 
<%}%>
>
<jsp:doBody/> 
<div style="clear:both"></div>
</div>
<script type="text/javascript">
	$(function(){
		var $children = $("#${id}").children(".sexybutton,.sexybutton_163,.select_button");
		if ($children.length < 1) return;
		var $first = $children.first();
		var $last = $children.last();
		if($first.is("div")){
			$(".sexybutton_163 .button_span",$first).css("borderRadius","3px 0px 0px 3px");
		}
		if($first.is(".sexybutton_163")){
			$(".button_span",$first).css("borderRadius","3px 0px 0px 3px");
		}
		if($last.is("div")){
			$(".sexybutton_163 .button_span",$last).css("borderRadius","0px 3px 3px 0px");
		}
		if($last.is(".sexybutton_163")){
			$(".button_span",$last).css("borderRadius","0px 3px 3px 0px");
		}
		var $buttonGroupSeparate = $("#${id}").children(".buttonGroupSeparate");
		if($buttonGroupSeparate && $buttonGroupSeparate.length > 0){
			var tempArr = $buttonGroupSeparate.prevUntil();
			if(tempArr && tempArr[0].tagName.toLowerCase() == "script"){
				$(".sexybutton_163 .button_span",tempArr[1]).css("borderTopRightRadius","3px").css("borderBottomRightRadius","3px");
			}
			//$(".sexybutton_163 .button_span",$buttonGroupSeparate.prev("div.select_button")).css("borderTopRightRadius","3px").css("borderBottomRightRadius","3px");
			$(".button_span",$buttonGroupSeparate.prev(".sexybutton_163")).css("borderTopRightRadius","3px").css("borderBottomRightRadius","3px");
			$(".sexybutton_163 .button_span",$buttonGroupSeparate.next("div")).css("borderTopLeftRadius","3px").css("borderBottomLeftRadius","3px");
			$(".button_span",$buttonGroupSeparate.next(".sexybutton_163")).css("borderTopLeftRadius","3px").css("borderBottomLeftRadius","3px");
		}
		var leftWidth = $last.position().left;
		var lastWidth = $last.outerWidth(true);
		var width = leftWidth + lastWidth;
		var divWidth = $("#${id}").width();
		if("left" == "${align}"){
		}else if("right" == "${align}"){
			$("#${id}").css("paddingLeft",(divWidth-width));
		}else{
			$("#${id}").css("paddingLeft",(divWidth-width)/2);
		}
	})
</script>
<%@include file="../columnfoot.tag" %>