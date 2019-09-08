<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="java.util.Random"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.Map"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%--@doc--%>
<%@tag description='下拉框选择数据后，显示在指定位置，可多选，可删除，类似下拉框功能' display-name="selectData" %>
<%@attribute description='组件唯一id，为后台like查询字段' name='id' type='java.lang.String'  required="true"%>
<%@attribute description='设置该容器的CSS中class样式，例如 cssClass="edit-icon"' name='cssClass' type='java.lang.String' %>
<%@attribute description='设置该容器的CSS中style样式，例如 cssStyle="font-size:12px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='标题值' name='key' type='java.lang.String' %>
<%@attribute description='查询url，返回一个形如[{‘orgid’：‘1’，‘orgname’：‘银海软件’}，{‘orgid’：‘2’，‘orgname’：‘企业创新中心’}]的列表' name='url' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='true/false,设置是否必输，默认false，设置后代小红星' name='required' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String' %>
<%@attribute description='显示在页面上的值，该值通过查询url返回的列表中获取，比如defaultName="orgname' name='defaultName' type='java.lang.String'  required="true"%>
<%@attribute description='submitid的数据来源，比如defaultId="orgid' name='defaultId' type='java.lang.String' required="true"%>
<%@attribute description='true/false，是否能多选，默认false，表示不能多选' name='multiple' type='java.lang.String' %>
<%@attribute description='输入多少个字符时开始查询，默认两个字符，如需修改，请将该值设置成大于0的数字，例如：inputQueryNum=‘3’' name='inputQueryNum' type='java.lang.Integer' %>
<%@attribute description='实际提交的值，如果可以多选，则提交的是一个以defaultId组成的以“,”号隔开的字符串，例如submitid=‘managers’，后台dto中可取出其值'  name='submitid' type='java.lang.String'  required="true"%>
<%@attribute description='鼠标移入item时，显示的内容字段'  name='titleId' type='java.lang.String'%>
<%@attribute description='提示内容'  name='tipContent' type='java.lang.String'%>

<%--@doc--%>
<%
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
if(inputQueryNum != null){
		
}else{
	inputQueryNum = 2;
}
if(tipContent == null) {
	tipContent = "输入后实时查询";
}

 %>
<%@include file="../columnhead.tag" %>
<div id="selectData_<%=id %>"
<% if(cssClass!=null){%>
class="<%=cssClass%>" 
<%}%>
<% if(cssStyle!=null){%>
style="position:relative;height:28px;padding:3px 0;;<%=cssStyle%>" 
<%}else{%>
style="position:relative;height:28px;padding:3px 0;" 
<%
}
 %>
<% if(columnWidth!=null){%>
columnWidth="${columnWidth}" 
<%}%>
<% if(span!=null){%>
span="${span}" 
<%}%>
>
<% if(key!=null && !"".equals(key.trim())){%>
	<label 
class="selectData_label" 
	<% if(labelStyle!=null){%>
style="<%=labelStyle%>" 
	<%}%>
> 
<% if(required!=null){%>
<span style="color:red">*</span>
<%}%>
${key}<%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))) {%>：<%} %>
	</label> 
	<%} %>
<div class="selectData_C"
<% if(labelWidth!=null){%>
	style="margin-left:${labelWidth}px" 
	<%}else if(null == key || "".equals(key.trim())) {%>
	style="margin-left:0px;"
	<%}%>
 >
 <input id="<%=submitid%>" name="dto['<%=submitid%>']" style="display:none;"/>
 <input 
<% if(id!=null){%>
id="<%=id %>" 
<%}%>
class="selectData_input"
<%if(tipContent!=null){ %>
value="<%=tipContent%>"
<%} %>
/>

<div class="selectData_xiala_C" ></div>
</div>
</div>
<%@include file="../columnfoot.tag" %>
<script>
	$(function(){
		$("#<%=id %>")
		.bind("focus",function(event){
			if(this.value.trim()=="<%=tipContent%>") {
				$(this).css('color','#000000');
 				this.value="";
 				event.stopPropagation();
			}
		}).bind("keyup",function(event){
			if(this.value != null && $.trim(this.value) != "" && $.trim(this.value).length >=<%=inputQueryNum%>){
				<%
					if(url != null && !"".equals(url)) {
				%>
				Base.getJson("<%=url%>",{"dto['<%=id%>']":$("#<%=id%>").val()},function(data){
					if(data != null && data.length > 0) {
						$("#selectData_<%=id %> div.selectData_xiala_C").empty();
						for(var i = 0; i < data.length; i++){
							$("#selectData_<%=id %> div.selectData_xiala_C").append("<div title='"+data[i].<%=titleId%>+"'  _id='"+data[i].<%=defaultId%>+"'>"+data[i].<%=defaultName%>+"</div>");
						}
						$("#<%=id%>").next(".selectData_xiala_C").show(); 
					}
				})
				<%}%>
			}
		});
		$("#selectData_<%=id%> div.selectData_xiala_C").delegate("div","mouseover",function(){
			$(this).addClass("selectData_xiala_hover");
		}).delegate("div","mouseout",function(){
			$(this).removeClass("selectData_xiala_hover");
		}).delegate("div","click",function(){
			<%
			if(multiple !=null && "true".equals(multiple)){
			%>
			var $last = $(this).parent().siblings(".selectData_leirong:last");
			if($last != null && $last != undefined && $last.length != 0){
				$(this).parent().siblings(".selectData_leirong:last").after("<div class='selectData_leirong' _id='leirong_"+$(this).attr('_id')+"'><strong  style='margin-right:18px;'>"+$(this).html()+"</strong><a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_<%=id%>(this)'>x</a></div>");
			}else{
				$(this).parent().parent().prepend("<div class='selectData_leirong'  _id='leirong_"+$(this).attr('_id')+"'><strong style='margin-right:18px;'>"+$(this).html()+"</strong> <a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_<%=id%>(this)'>x</a></div>");
			}
			var val = $("#<%=submitid %>").val();
			if(val == null || val == ""){
				val = $(this).attr("_id");
			}else{
				val += ","+$(this).attr("_id");
			}
			$("#<%=submitid %>").val(val);
			<%
			}else{
			%>
			$("#selectData_<%=id%> div.selectData_leirong").remove();
			$(this).parent().parent().prepend("<div class='selectData_leirong'><strong style='margin-right:18px;'>"+$(this).html()+"</strong> <a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_<%=id%>(this)'>x</a></div>");
			$("#<%=submitid %>").val($(this).attr("_id"));
			<%
			}
			%>
			$("#<%=id %>").val("");
			$(this).parent().hide();
			var $leirong = $("#selectData_<%=id%> div.selectData_leirong"),leirongWidth=0; 
			if($leirong && $leirong.length > 0) {
				for(var i = 0 ; i < $leirong.length ; i++) {
					leirongWidth += $($leirong[i]).outerWidth(true);
				}
			}
			$("#<%=id %>").css("left",leirongWidth); 
			
		});
	});
function fnRemoveSelectData_<%=id%>(o) {
	<%
	if(multiple !=null && "true".equals(multiple)){
	%>
	var id = $(o).parent().attr("_id").substring(8);
	var val = $("#<%=submitid%>").val();
	if(id == val){
		$("#<%=submitid%>").val("");
	}else{
		var num = val.indexOf(id);
		if(num == 0) {
			$("#<%=submitid%>").val(val.substring(String(id).length+1));
		}else{
			var preval = val.substring(0,num-1);
			var nextval = val.substring(num+String(id).length);
			$("#<%=submitid%>").val(preval+nextval);
		}
	}
	<%
	}else{
	%>
	$("#<%=submitid%>").val("");
	<%
	}
	%>
	$(o).parent().remove();
	var $leirong = $("#selectData_<%=id%> div.selectData_leirong"),leirongWidth=0; 
	if($leirong && $leirong.length > 0) {
		for(var i = 0 ; i < $leirong.length ; i++) {
			leirongWidth += $($leirong[i]).outerWidth(true);
		}
	}
	$("#<%=id %>").css("left",leirongWidth);
	
}
</script>