<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random"  %>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='悬浮框容器' display-name='boxComponent' %>
<%@attribute name="id"  type="java.lang.String" description="容器组件在页面上的唯一id" required="true"%>
<%@attribute name="cssClass"  type="java.lang.String" description="设置该容器的CSS中class样式，例如 cssClass='edit-icon'"%>
<%@attribute name="cssStyle"  type="java.lang.String" description="设置该容器的CSS中style样式，例如 cssStyle='font-size:12px'" %>
<%@attribute name="span"  type="java.lang.String" description="设置该容器在父亲容器column布局中所跨列数，例如 span='2'"%>
<%@attribute name="key"  type="java.lang.String" description="当box的父亲容器layout为border时指定它的标题值"%>
<%@attribute name="cols"  type="java.lang.String" description="设置该容器在父亲容器中所跨行数，例如 cols=2"%>
<%@attribute name="height"  type="java.lang.String" description='自定义box的高度，如:height="100px"'%>
<%@attribute name="width"  type="java.lang.String" description='自定义box的宽度，如:width="100px"'%>
<%@attribute name="columnWidth"  type="java.lang.String" description=''%>
<%@attribute name="arrowPosition"  type="java.lang.String" description='悬浮框箭头朝向，默认为horizontal，vertical表示垂直自适应，horizontal水平自适应'%>
<%--@doc--%>
<%
		if(height!=null){
			if(height.endsWith("%")){
			}else if(height.endsWith("px")){
				  cssStyle="height:"+height+";"+cssStyle;
			}else{
				float  heightVal=Float.parseFloat(height);
				if(0<heightVal  && heightVal<1){
				}else{
					cssStyle="height:"+height+"px;"+cssStyle;
				}
			}
		}
		if(width!=null){
			if(width.endsWith("%")){
			}else if(width.endsWith("px")){
				  cssStyle="width:"+width+";"+cssStyle;
			}else{
				float  widthVal=Float.parseFloat(width);
				if(0<widthVal  && widthVal<1){
				}else{
					cssStyle="width:"+width+"px;"+cssStyle;
				}
			}
		}
		if(arrowPosition == null) {
			arrowPosition="horizontal";
		}
		if ((this.id == null || this.id.length() == 0)) {
			Random RANDOM = new Random();
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tagrid_" + String.valueOf(nextInt);
		}
%>

<%@include file="../columnhead.tag" %>
<div id="<%=id%>"
	class="boxComponent"
	_position="<%=arrowPosition%>"
>
<b class="boxComponent_b"></b>
<div class="boxComponent_1 ${cssClass}"
	<%if(cssStyle !=null) {%>
	style="<%=cssStyle%>"
	<%} %> 
	<%if(span !=null) {%>
	span="${span}"
	<%} %> 
	<%if(cols !=null) {%>
	cols="${cols}"
	<%} %> 
>
<jsp:doBody />
<div style="clear:both"></div>
</div>
<div class="boxComponent_close" title="关闭"></div>
</div>
<script>
$(function(){
	$(".boxComponent_close").click(function(){
		$(this).parent().hide();
	});
	$(document).mousedown(function(e){
		var target = e.target || e.srcElement; 
		if(!$(target).parents().is(".boxComponent")){
			$(".boxComponent").hide();
		}
	});
});

</script>
<%@include file="../columnfoot.tag" %>