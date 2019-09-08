<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%--@doc--%>
<%@tag description='下拉树' display-name='selectTree' %>
<%@attribute name="required" description="true/false,设置是否必输，默认false，设置后代小红星" type="java.lang.String"%>
<%@attribute name="url" description="url地址，必填" type="java.lang.String" required="true"%>
<%@attribute name="treeId" description="树id，必填，这个id必须与后台request或者session或者application绑定" type="java.lang.String" required="true"%>
<%@attribute name="selectTreeCallback" description='自定义节点点击事件,例如selectTreeCallback="fnClickSelectTree",js中，function fnClickSelectTree(event, treeId, treeNode){alert(treeId)}。如果不写，默认调用框架方法，将值赋值给targetId和targetDESC指定的输入框' type="java.lang.String"%>
<%@attribute name="selectTreeBeforeClick" description="该事件在节点被点击后最先触发，如果返回 false，则不会触发onClick事件，函数定义示例:fnBeforeClick(treeId, treeNode)" type="java.lang.String"%>
<%@attribute name="data" description='设置节点数据，数组格式，例如data="[{id:1,pId:0,name:"中国"},{id:11,pId:1,name:"四川"}]"，但是优先会采用后台传回的数据' type="java.lang.String"%>
<%@attribute name="targetDESC" description="目标描述id，用于显示节点数据，必填" type="java.lang.String" required="true"%>
<%@attribute name="targetId" description="目标id，用于存放节点id，必填" type="java.lang.String" required="true"%>
<%@attribute name="key" description="下拉树key" type="java.lang.String"%>
<%@attribute name="cssClass" description='给该组件添加自定义样式class，如:cssClass=“no-padding"' type="java.lang.String"%>
<%@attribute name="cssStyle" description='给该组件添加自定义样式，如:cssStyle=”padding-top:10px"' type="java.lang.String"%>
<%@attribute name="height" description="下拉树高度" type="java.lang.String"%>
<%@attribute name="width" description="下拉树宽度，默认和输入框同宽" type="java.lang.String"%>
<%@attribute name="minLevel" description="只能选择的最小层级" type="java.lang.String"%>
<%@attribute name="maxLevel" description="只能选择的最大层级" type="java.lang.String"%>
<%@attribute name="labelWidth" description='label占的宽度，如labelWidth="120"' type="java.lang.String"%>
<%@attribute name="asyncParam" description='设置异步时提交的与节点数据相关的必需属性，数组格式，例如asyncParam="["id","checked"]"' type="java.lang.String"%>
<%@attribute name="tagetIdValue" description='隐藏输入框的值,例如:tagetIdValue="1"' type="java.lang.String"%>
<%@attribute name="tagetDESCValue" description='只读输入框的值,例如:tagetDESCValue="男"' type="java.lang.String"%>
<%@attribute name="nameKey" description='设置显示节点名称的属性名，默认为"name"' type="java.lang.String"%>
<%@attribute name="idKey" description='设置节点父子关系中子节点的标示属性，也是每个节点的唯一标示属性名称，默认为"id"' type="java.lang.String"%>
<%@attribute name="parentKey" description='设置节点父子关系中父节点的标示属性，默认为"pId"' type="java.lang.String"%>
<%@attribute name="fontCss" description='设置个性化文字样式，只针对显示的节点名称，json格式，如:{color:"#ff0011", background:"blue"}，也可设置为一个函数名称，该函数返回json格式的样式，函数定义示例:fnSetFont(treeId, treeNode)' type="java.lang.String"%>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String' %>
<%@attribute description='设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1' name='columnWidth' type='java.lang.String' %>
<%@attribute description='显示输入框提示图标，内容自定义。例如textHelp="默认显示在左下角"' name='textHelp' type='java.lang.String' %>
<%@attribute description='textHelp宽度，默认200。例如textHelpWidth="200"' name='textHelpWidth' type='java.lang.String' %>
<%@attribute description='textHelp位置{topLeft,topRight,bottomLeft,bottomRight}，默认bottomLeft。例如textHelpPosition="bottomRight"' name='textHelpPosition' type='java.lang.String' %>
<%--@doc--%>
<%
ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
if(resultBean != null){
	Object v = resultBean.getFieldData()==null?null:resultBean.getFieldData().get(treeId);
	if(v !=null && !"".equals(v)){
		String[] vs = v.toString().split(",");
		if(vs.length == 2){
			tagetIdValue = vs[0];
			tagetDESCValue = vs[1];
			jspContext.setAttribute("tagetIdValue", vs[0]);
			jspContext.setAttribute("tagetDESCValue", vs[1]);
		}
	}
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
<input type="text" id="<%=targetId %>" name="dto['<%=targetId %>']" class="textinput" style="display:none;" <%if(ValidateUtil.isNotEmpty(tagetIdValue)){%>value="<%=tagetIdValue %>"<%}%>>
<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>
<div class="fielddiv fielddiv_163">
<%}else{ %>
<div class="fielddiv">
<%} %>
<%if( ValidateUtil.isNotEmpty(key)){%>
	<label for="<%=targetDESC%>" class="fieldLabel"
	<%if (labelWidth != null && !"".equals(labelWidth)){%>
	style="width:<%=labelWidth%>px"
	<%}%>
	<% if(columnWidth!=null){%>
	columnWidth="<%=columnWidth %>" 
	<%}%>
	<% if(span!=null){%>
	span="<%=span %>" 
	<%}%>
	>
	<%if (ValidateUtil.isNotEmpty(required)){%>
<span style="color:red">*</span>
	<%}%>
<%=key%><%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>：<%} %>
	</label>
	<%}%>
	<div class="fielddiv2" 
	<%if (ValidateUtil.isNotEmpty(labelWidth)){%>
	style="margin-left:<%=labelWidth%>px"
	<%}else if(null == key || "".equals(key.trim())) {%>
	style="margin-left:0px;"
	<%} %>
	>
	<span class="innerIcon innerIcon_show" onclick="__fnShowSelectTree_<%=treeId%>()" title="点击展开下拉树" <%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>style="top:2px"<%} %>></span> 
	<span class="innerIcon innerIcon_delete" onclick="fn_${treeId}_remove()" title="清除" <%if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){ %>style="top:2px"<%} %>></span> 
		<input type="text" id="<%=targetDESC%>" 
		name="dto['<%=targetDESC%>']" readonly="true" 
		class="textinput" 
		 <%if (ValidateUtil.isNotEmpty(tagetDESCValue)){%>value="<%=tagetDESCValue%>"<%}%>
		 <%if (ValidateUtil.isNotEmpty(required)){%>required="<%=required%>"<%}%>>
		 <%if(textHelp != null){ %>
		<div class="textInfo">
			<div class="textInfo_content ffb <%if(textHelpPosition != null)%>${textHelpPosition}" style="<%=textHelpStyle%>">${textHelp}</div>
			<script>
				var $textInfo = $("#<%=targetDESC%> + div.textInfo > div.textInfo_content");
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
<script>
(function(factory){
	if (typeof require === 'function') {
		require(["jquery", "selectTree"], factory);
	} else {
		factory(jQuery);
	}
}(function($){


$(document).ready(function () {
		var options = {};
		options.url = "<%=url%>";
		<%if(ValidateUtil.isNotEmpty(selectTreeCallback)){%>
		options.selectTreeCallback = <%=selectTreeCallback%>;
		<%}else{%>
		options.selectTreeCallback = __fnSlctTargetDept_<%=treeId%>;
		<%}%>
		<%if (ValidateUtil.isNotEmpty(selectTreeBeforeClick)){%>
		options.selectTreeBeforeClick = <%=selectTreeBeforeClick%>;
		<%}else{%>
		options.selectTreeBeforeClick = __selectTreeBeforeClick_<%=treeId%>;
		<%}%>
		<%if(ValidateUtil.isNotEmpty(cssStyle)){%>
		options.cssStyle = "<%=cssStyle%>";
		<%}%>
		<%if (ValidateUtil.isNotEmpty(cssClass)){%>
		options.cssClass = "<%=cssClass%>";
		<%}%>
		<%if (ValidateUtil.isNotEmpty(height)){%>
		options.height = '<%=height%>';
		<%}%>
		<%if (ValidateUtil.isNotEmpty(width)){%>
		options.width = '<%=width%>';
		<%}%>
		<%if (ValidateUtil.isNotEmpty(nameKey)){%>
		options.nameKey = '<%=nameKey%>';
		<%}%>
		<%if (ValidateUtil.isNotEmpty(idKey)){%>
		options.idKey = '<%=idKey%>';
		<%}%>
		<%if (ValidateUtil.isNotEmpty(parentKey)){%>
		options.parentKey = '<%=parentKey%>';
		<%}%>
		<%if (ValidateUtil.isNotEmpty(fontCss)){%>
		options.fontCss = <%=fontCss%>;
		<%}%>
		<%if (ValidateUtil.isNotEmpty(asyncParam)){%>
		options.asyncParam = <%=asyncParam%>;
		<%}%>
<%
	Object req = request.getAttribute(treeId);
	String reqs = null;
	if(req != null && !"".equals(req))reqs = req.toString();
	String sessions = (String)session.getAttribute(treeId);
	String app = (String)application.getAttribute(treeId);
%>
var req = <%=reqs%>;
var sessions = <%=sessions%>;
var app = <%=app%>;
var nodesData = null;
if(req){
	nodesData = req;
}else if(sessions){
	nodesData = sessions;
}else if(app){
	nodesData = app;
}
<% if(ValidateUtil.isNotEmpty(data)){%>
else if(<%=data%>){
	nodesData = <%=data%>;
} 
<% }%>
	options.nodesData = nodesData;
	new SelectTree("dropdownTreeBackground_<%=treeId%>",'<%=targetDESC%>','<%=treeId%>',options);
});
//requirejs
}));
$(function(){
	$(document).bind("mousedown", 
	function(event){
		if (!(event.target.id == "dropdownTreeBackground_<%=treeId%>" || $(event.target).parents("#dropdownTreeBackground_<%=treeId%>").length > 0)) {
			__fnHideSelectTree_<%=treeId%>();
		}
	});
});

//隐藏树
function __fnHideSelectTree_<%=treeId%>(){
	$("#dropdownTreeBackground_<%=treeId%>").fadeOut(100);
}
//显示树
function __fnShowSelectTree_<%=treeId%>() {
	var p = $("#<%=targetDESC%>").parent().offset();
	var w = $("#<%=targetDESC%>").parent().width()+16;
	if("<%=cssStyle%>" != null && "<%=cssStyle%>".indexOf("width") >= 0){
		$("#dropdownTreeBackground_<%=treeId%>").css({"top":p.top+28,"left":p.left});
	}else if("<%=width%>" != "null"){
		$("#dropdownTreeBackground_<%=treeId%>").css({"top":p.top+28,"left":p.left});
	}else{
		$("#dropdownTreeBackground_<%=treeId%>").css({"top":p.top+28,"left":p.left,"width":w});
	}
	$("#dropdownTreeBackground_<%=treeId%>").slideDown(100);
}
//获取节点设置值
function __fnSlctTargetDept_<%=treeId%>(event, treeId, treeNode) {
	<%if (ValidateUtil.isNotEmpty(idKey)){%>
		Base.setValue("<%=targetId%>", treeNode.<%=idKey%>);
	<%}else{%>
		Base.setValue("<%=targetId%>", treeNode.id);
	<%}%>
	<%if (ValidateUtil.isNotEmpty(nameKey)){%>
		Base.setValue("<%=targetDESC%>", treeNode.<%=nameKey%>);
	<%}else{%>
		Base.setValue("<%=targetDESC%>", treeNode.name);
	<%}%>
	__fnHideSelectTree_<%=treeId%>();
}
function __selectTreeBeforeClick_<%=treeId%>(treeId,treeNode){
	<%if(ValidateUtil.isNotEmpty(minLevel)){%>
	if(parseInt(treeNode.level)+1 < <%=minLevel%>){
		alert('当前选择层级为'+parseInt(treeNode.level+1)+',必须选择大于等于层级为'+<%=minLevel%>+'的节点数据');
		Base.setValue('<%=targetId%>','');
		Base.setValue('<%=targetDESC%>','');
		return false;
	}
	<%}%>
	<%if(ValidateUtil.isNotEmpty(maxLevel)){%>
	if(parseInt(treeNode.level) >= <%=maxLevel%>){
		alert('当前选择层级为'+parseInt(treeNode.level+1)+',必须选择小于等于层级为'+<%=maxLevel%>+'的节点数据');
		Base.setValue('<%=targetId%>','');
		Base.setValue('<%=targetDESC%>','');
		return false;
	}
	<%}%>
	return true;
}
function fn_${treeId}_remove(){
	Base.setValue('<%=targetId%>','');
	Base.setValue('<%=targetDESC%>','');
}
</script>