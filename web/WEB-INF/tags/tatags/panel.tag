<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Iterator"%>
<%@tag import="java.util.Enumeration"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.alibaba.fastjson.JSONArray"%>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@tag import="com.alibaba.fastjson.JSONObject"%>
<%-- @doc --%>
<%@tag description='panel面板' display-name='panel' %>
<%@attribute name="id" type="java.lang.String" description="容器组件在页面上的唯一id"%>
<%@attribute name="cols" type="java.lang.String" description="当该容器对子组件布局layout=column的时候，可以设置cols参数表面将容器分成多少列，默认不设置为1"%>
<%@attribute name="columnWidth" type="java.lang.String" description="设置layout为column布局的时候自定义占用宽度百分比，可设置值为0-1之间的小数，如:0.1"%>
<%@attribute name="cssStyle" type="java.lang.String" description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"'%>
<%@attribute name="cssClass" type="java.lang.String" description='给该组件添加自定义样式class，如:cssClass="no-padding"'%>
<%@attribute name="key" type="java.lang.String" description="组件的label标签"%>
<%@attribute name="layout" type="java.lang.String" description="设置该容器对子组件的布局类型，有column/border，默认为column，cols=1"%>
<%@attribute name="layoutCfg" type="java.lang.String" description='json,设置layout为border布局的时候布局的参数配置，如:layoutCfg=\"{leftWidth:200,topHeight:90,rightWidth:200,bottomHeight:100}\"'%>
<%@attribute name="position" type="java.lang.String" description="top/left/center/right/bottom,设置父亲容器layout为border布局的时候该组件所在位置"%>
<%@attribute name="span" type="java.lang.String" description='数字，当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列'%>
<%@attribute name="icon" type="java.lang.String" description='标题图标class名称,如:icon-add,可以到icon.css查询'%>
<%@attribute name="expanded" type="java.lang.String" description='true/false ,是否展开，默认为false'%>
<%@attribute name="withToolBar" type="java.lang.String" description='true/false ,是否带有toolbar。默认false'%>
<%@attribute name="withButtonBar" type="java.lang.String" description='true/false ,是否带有ButtonBar。默认false'%>
<%@attribute name="bodyClass" type="java.lang.String" description='添加表格体部分的样式class'%>
<%@attribute name="bodyStyle" type="java.lang.String" description='添加表格体部分的样式style'%>
<%@attribute name="hasBorder" type="java.lang.String" description='true/false ,是否有外边框。默认true'%>
<%@attribute name="width" type="java.lang.String" description='自定义panel的宽度，如:width="100px"'%>
<%@attribute name="height" type="java.lang.String" description='自定义panel的高度，这个高度是除开标题的高度，如:height="100px"'%>
<%@attribute name="fit" type="java.lang.String" description='true/false,是否自动适应剩余高度,如果设置为true，那么该组件的所有父辈组件都要设置fit为true或height为固定值。&gt;/br&lt;该组件兄弟组件间只能有一个设置fit=true。&gt;/br&lt;如果兄弟组件在后面且可见，那么需要设置heightDiff高度补差'%>
<%@attribute name="heightDiff" type="java.lang.String" description='当fit设置为true的时候组件底部高度补差，后同级后面的组件留下一定高度，如:heightDiff="100",不需要加px'%>
<%@attribute name="padding" type="java.lang.String" description='当panel内部的组件和边框靠的太近可以通过设置该属性来增加一定的内边距，如:3或3px 2px 3px 2px(上、右、下、左)'%>
<%@attribute name="scalable" type="java.lang.String" description='true/false ,是否可缩放，默认为false'%>
<%@attribute name="minHeight" type="java.lang.String" description='true/false ,是否可缩放，默认为true'%>
<%@attribute name="titleAlign" type="java.lang.String" description='left/center/right,panel标题的位置，默认center'%>
<%@attribute name="headerButton" type="java.lang.String" description='在panel上面右边存在按钮,如[{"id":"button1","name":"提交","click":"fnClick()"}]'%>
<%-- @doc --%>
<%		
		if(scalable != null && "true".equals(scalable) ){
			scalable = "true";
		}else{
			scalable = "false";
		}
		if(expanded != null && "true".equals(expanded) ){
			expanded = "true";
		}else{
			expanded = "false";
		}
		if (withToolBar != null) {
			if("true".equals(withToolBar)){
				bodyClass="panelhastoolbar ";
			}
		}
		if(key==null && !"true".equals(withToolBar) ){
			if(bodyClass==null)
				bodyClass =  " panelnotitle";
			else
				bodyClass =  bodyClass+" panelnotitle";
		}	
		if(layout==null){//处理panel在存在panelToolBar的情况下设置cols无效的问题
			layout="column";
		}
		/*
		 * 这里对width和height进行处理，如果width或height有值，则在style中加上width和height
		 */
		String tcssStyle = "";
		
		if (width != null) {
			String tmpw = width;
			tcssStyle = "width:"+(tmpw.endsWith("px")?tmpw:(tmpw+"px"))+";";
			if(cssStyle!=null){
				cssStyle = tcssStyle +cssStyle;
			}else{
				cssStyle = tcssStyle;
			}
		}
		if (height != null) {
			String tmph = height;
			tcssStyle += "height:"+(tmph.endsWith("px")?tmph:(tmph+"px"))+";";
		}
		if (bodyStyle != null) {
			bodyStyle = bodyStyle+";"+tcssStyle;

		}else{
			bodyStyle = tcssStyle;
		}
		if(padding!=null){
			if(bodyStyle!=null)
				bodyStyle =bodyStyle+";padding:"+padding+(padding.indexOf("px")>0?";":"px;");
			else
				bodyStyle = padding+";";
		}
		if(cssClass ==null){
			cssClass = "panel";
		}else{
			cssClass =  "panel "+cssClass;
		}
		if(hasBorder !=null){
			cssClass += " panelnomargin";
		}
		if ((this.id == null || this.id.length() == 0)) {
			Random RANDOM = new Random();
		
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tapanel_" + String.valueOf(nextInt);
		}
		
		Map panelMap = new HashMap();
		
		panelMap.put("cols", cols);
		panelMap.put("key", key);
		panelMap.put("columnWidth", columnWidth);
		panelMap.put("layout", layout);
		panelMap.put("layoutCfg", layoutCfg);
		panelMap.put("position", position);
		panelMap.put("span", span);
		panelMap.put("icon", icon);
		panelMap.put("expanded", expanded);
		panelMap.put("withToolBar", withToolBar);
		panelMap.put("withButtonBar", withButtonBar);
		panelMap.put("bodyClass", bodyClass);
		panelMap.put("bodyStyle", bodyStyle);
		panelMap.put("hasBorder", hasBorder);
		panelMap.put("width", width);
		panelMap.put("height", height);
		panelMap.put("fit", fit);
		panelMap.put("heightDiff", heightDiff);
		panelMap.put("padding", padding);
		panelMap.put("scalable", scalable);
		jspContext.setAttribute("taPanel", panelMap, PageContext.REQUEST_SCOPE);
		jspContext.setAttribute("taPanel_object", this, PageContext.REQUEST_SCOPE);
 %>
<%@include file="../columnhead.tag" %>
<div
	id="<%=id %>"
	<%if(cssStyle !=null){ %>
	style="<%=cssStyle %>"
	<%} %>
	class="<%=cssClass %>"
	<%if(span !=null){ %>
	span="<%=span %>"
	<%} %>
	<%if(fit !=null){ %>
	fit="<%=fit %>"
	<%} %>
	<%if(position !=null){ %>
	position="<%=position %>"
	<%} %>
	<%if(columnWidth !=null){ %>
	columnWidth="<%=columnWidth %>"
	<%} %>
	<%if(heightDiff !=null){ %>
	heightDiff="<%=heightDiff %>"
	<%} %>
	<%if(minHeight !=null){ %>
		minHeight="<%=minHeight%>"
	<%} %>
> 
	<%if(!ValidateUtil.isEmpty(key)) {%>
		<div class="panel-header ui-corner-top 
		<%if(!ValidateUtil.isEmpty(icon)) {%>
			panel-with-icon	
		<%} %>	 
		<%if(!ValidateUtil.isEmpty(hasBorder)) {%>
			panelnoborder 	
		<%} %>
			">  
		<%if(!ValidateUtil.isEmpty(icon)) {%>
			<div class="panel-icon ${icon}"></div> 
		<%} %>
			<div class="panel-title <%if(titleAlign != null){%>${titleAlign}<%}else{%>left<%}%>"> 
				 ${key} 	
			</div>
		<%if(!ValidateUtil.isEmpty(headerButton)){ %>
			<div style="position: absolute;right:20px;top:-2px;">
			<% 
				JSONArray array = JSonFactory.json2bean(headerButton, JSONArray.class);
				
				for(int i =0;i<array.size();i++){
					JSONObject obj = (JSONObject) array.get(i);
				%>
					<button id="<%=obj.get("id") %>" class="sexybutton_163" onclick="<%=obj.get("click")%>" style="height:22px;margin:0px;"> 
						<span class="button_span isok" style="height: 18px;line-height: 18px;font-size: 10px; margin:0 5px;"><%=obj.get("name") %></span>
					</button>
				<%
				}
			%>
			</div>
		<%} %>
			<%if("false".equals(scalable) && "false".equals(expanded)){%>
			
			<%}else{%>
				<div class="panel-tool">
			<%if("false".equals(scalable)){} else{%>
				<div class="panel-tool-max"  title="放大或缩小"></div>
			<% }%>
			<%if("false".equals(expanded)){} else{%>
				<div class="panel-tool-collapse" title="收缩或展开"></div>
			<% }%>
				</div>
			<%} %>
		</div>
	<%} %>
		
	<%if("true".equals(withToolBar)){} else{%>
		<%if("true".equals(withButtonBar)) {%>
		 	<div 
			 	<%if(!ValidateUtil.isEmpty(bodyStyle)){ %>
				 style="<%=bodyStyle %>"
			 	<%} %> 
				class="panel-body panel-width-buttonpanel 
				<%if(!ValidateUtil.isEmpty(bodyClass)){ %>
				  <%=bodyClass %>
			 	<%} %> 
			 	<%if("false".equals(hasBorder)){ %>
				  panelnoborder 
			 	<%} %> 
				" 
				<%if(!ValidateUtil.isEmpty(layout)){ %>
					layout="<%=layout %>" 
					<%if(!ValidateUtil.isEmpty(cols)){ %>
						cols="${cols}"
			 		<%} %>  
			 		<%if(!ValidateUtil.isEmpty(layoutCfg)){ %>
						layoutCfg="${layoutCfg}" 
			 		<%} %> 
			 	<%} %> 		
			> 
		<%}else{ %>
			<div 
				style="<%=bodyStyle %>" 
				class="panel-body 
			<%if("false".equals(hasBorder)) {%>
				panelnoborder 
			<%} %>
			<%if("false".equals(bodyClass)) {%>
				<%=bodyClass %>
			<%} %>" 
			<%if(!ValidateUtil.isEmpty(layout)) {%>
				layout="<%=layout %>" 
				<%if(!ValidateUtil.isEmpty(cols)) {%>
			 		cols="${cols}"
				<%} %>
				<%if(!ValidateUtil.isEmpty(layoutCfg)) {%>
			 		layoutCfg="${layoutCfg}"
				<%} %>
			<%} %>
			> 
		<%} %>
		
	<%} %>
<jsp:doBody/> 	
<div style="clear:both"></div>
<%if("true".equals(withButtonBar)){}else{ %>
</div>
<%} %>
	
</div>
<%@include file="../columnfoot.tag" %>