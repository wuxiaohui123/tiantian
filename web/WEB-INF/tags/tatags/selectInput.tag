<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.IConstants"%>
<%@tag import="com.yinhai.sysframework.service.AppManager"%>
<%@tag import="java.util.Locale"%>
<%@tag import="com.yinhai.sysframework.codetable.service.CodeTableLocator"%>
<%@tag import="com.alibaba.fastjson.JSONArray"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.webframework.session.UserSession"%>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%--@doc--%>
<%@tag description='下拉框' display-name='selectInput'%>
<%@attribute description='设置父容器layout为column布局的时候自定义占用容器行宽度百分比，可设置值为0-1之间的小数，如:0.1则表示占该行的1/10' name='columnWidth' type='java.lang.String'%>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String'%>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String'%>
<%@attribute description='true/false,设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台。默认false' name='disabled' type='java.lang.String'%>
<%@attribute description='true/false,设置是否显示，默认为显示:true' name='display' type='java.lang.String'%>
<%@attribute description='组件id页面唯一' name='id' required='true' type='java.lang.String'%>
<%@attribute description='组件的label标签,不支持html标签' name='key' type='java.lang.String'%>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准，如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name'
	type='java.lang.String'%>
<%@attribute description='选择选项时触发，传函数定义，默认传入参数’描述‘数据，和’真实’数据，如onSelect="fnSelect",再在javascript中定义函数function fnSelect(key, value)' name='onSelect'
	type='java.lang.String'%>
<%@attribute description='true/false,设置是否必输，默认false，设置后代小红星' name='required' type='java.lang.String'%>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String'%>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String'%>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String'%>
<%@attribute description='设置码表的类型，如SEX' name='collection' type='java.lang.String'%>
<%@attribute description='设置层级下拉框码表的类型，如SEX' name='collectionTree' type='java.lang.String'%>
<%@attribute description='true/false,设置是否允许输入列表以外的数据，输入后key及value均为输入值，默认false' name='allowInputOtherText' type='java.lang.String'%>
<%@attribute description='设置过滤这些数据不显示，例如:filter="1,2,3"表示key为1,2,3的数据项不显示' name='filter' type='java.lang.String'%>
<%@attribute description='true/false,对设置的filter数据是否进行反向过滤，默认false' name='reverseFilter' type='java.lang.String'%>
<%@attribute description='数值，设置最大可见下拉列表条数，大于此条数则显示滚动条' name='maxVisibleRows' type='java.lang.String'%>
<%@attribute description='true/false,是否自动分页,当下拉项大于200的时候，默认true' name='paging' type='java.lang.String'%>
<%@attribute description='true/false,设置只读,默认false' name='readOnly' type='java.lang.String'%>
<%@attribute description='数值，label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String'%>
<%@attribute description='label自定义样式，如labelStyle="font-size:12px"' name='labelStyle' type='java.lang.String'%>
<%@attribute description='true/false,是否过滤org（分中心），默认true' name='filterOrg' type='java.lang.String'%>
<%@attribute description='json,手工传入下拉数据，例如:[{"id":"xxx","name":"xxx","py":"xx"},{},{}]' name='data' type='java.lang.String'%>
<%@attribute description='true/false,是否服务端转码，默认false，一般不设置' name='serverCvtCode' type='java.lang.String'%>
<%@attribute description='true/false,是否显示Key值，默认false，设置为true时，下拉列中将显示为key:value的样式' name='showKey' type='java.lang.String'%>
<%@attribute description='设置’描述‘来自于数据中的哪个字段，默认为name' name='displayValue' type='java.lang.String'%>
<%@attribute description='设置’key‘来自于数据中的哪个字段，默认为id' name='hiddenValue' type='java.lang.String'%>
<%@attribute description='设置’拼音过滤‘来自于数据中的哪个字段，默认为py' name='pyFilter' type='java.lang.String'%>
<%@attribute description='值改变事件，传函数定义(不加括号)，默认传入参数’描述‘数据，和’真实’数据，如onChange="fnChange",再在javascript中定义函数function fnChange(value, key)' name='onChange'
	type='java.lang.String'%>
<%@attribute description='获取焦点事件' name='onFocus' type='java.lang.String'%>
<%@attribute description='true/false,当为true时,表示默认选择第一项,显示在文本框中;默认false,表示不选择.当有value属性时,以value属性为准.此属性必须设置collection或者data属性' name='selectFirstValue'
	type='java.lang.String'%>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String'%>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String'%>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String'%>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String'%>
<%@attribute description='是否自动计算下拉选项扩展方向，默认为true' name='isAutoExtend' type='java.lang.String'%>
<%@attribute description='是否聚焦时显示选择面板，默认为true' name='isFocusShowPanel' type='java.lang.String'%>
<%@attribute description='过滤，以某些字符串开头的字段，不显示，比如:filterStartChar="a,b",表示以a或者b开始的字符串不显示' name='filterStartChar' type='java.lang.String'%>
<%@attribute description='过滤，低于几个字符串的不显示，比如:fiterValueLengthMin="3",表示低于3个字符串不显示' name='fiterValueLengthMin' type='java.lang.String'%>
<%@attribute description='过滤，高于几个字符串的不显示，比如:fiterValueLengthMax="5",表示高于5个字符串不显示' name='fiterValueLengthMax' type='java.lang.String'%>
<%@attribute description='下拉框所占宽度百分比，例如:widthPercentage="150%",默认100%' name='widthPercentage' type='java.lang.String'%>
<%@attribute description='true/false,是否显示层级关系，默认false不显示，设置成ture表示显示，只有true的时候isMustLeaf,minLevel,maxLevel才生效' name='islevel' type='java.lang.String'%>
<%@attribute description='true/false,是否必须选择子节点，默认false，设置成true表示必须选择，必须设置islevel="true"' name='isMustLeaf' type='java.lang.String'%>
<%@attribute description='number，选择数据的最低层次，必须设置islevel="true"' name='minLevel' type='java.lang.String'%>
<%@attribute description='number，选择数据的最高层次，必须设置islevel="true"' name='maxLevel' type='java.lang.String'%>
<%@attribute description='是否显示刷新码表按钮' name='showRefresh' type='java.lang.String'%>
<%@attribute description='设置selectInput的显示值,key为显示key值,all显示"key:value"的值,默认为显示value值' name='showValue' type='java.lang.String'%>
<%@attribute description='显示输入框提示图标，内容自定义。例如textHelp="默认显示在左下角"' name='textHelp' type='java.lang.String'%>
<%@attribute description='textHelp宽度，默认200。例如textHelpWidth="200"' name='textHelpWidth' type='java.lang.String'%>
<%@attribute description='textHelp位置{topLeft,topRight,bottomLeft,bottomRight}，默认bottomLeft。例如textHelpPosition="bottomRight"' name='textHelpPosition'
	type='java.lang.String'%>
<%@attribute description='是否启用延迟加载,true/false.默认为false' name='lazy' type='java.lang.String'%>
<%@attribute description='提示文字' name='placeholder' type='java.lang.String'%>
<%@attribute description='首数据项' name='clearData' type='java.lang.String'%>
<%@attribute description='设置下拉面板宽度，如100px.默认和输入框一样长' name='selectPanelWidth' type='java.lang.String'%>
<%--@doc--%>

<%
	String basePath = request.getContextPath();
	boolean flagConfig = Boolean.valueOf(AppManager.getSysConfig("neworold"));
	boolean lazyBoolean = false;
	if(lazy != null && !"".equals(lazy)){
		lazyBoolean = Boolean.parseBoolean(lazy);
	}
	boolean flag = flagConfig && lazyBoolean;
%>

<%
	if(flag){
%>
<%-- 使用新版的localStorage缓存 --%>
<%
	if(collection != null && !"".equals(collection)){
	collection = collection.toUpperCase(Locale.ENGLISH);
		}
	if (("true".equals(readOnly) || "true".equals(disabled))
			&& "true".equals(serverCvtCode)
			&& null != collection
			&& !"".equals(collection)) {
		UserSession us = UserSession.getUserSession(request);
		
		Map<String, String> map = (HashMap<String, String>) us.getCurrentBusiness().getSessionResource("__selectinput_flag_map_");
		if (map == null) {
			map = new HashMap<String, String>();
		}
		map.put(id, collection.toString());
		us.getCurrentBusiness().putSessionResource("__selectinput_flag_map_", map);
	}
	        if (labelStyle != null) {
		String style = "";
		if (labelWidth != null) {
			style = "width:" + labelWidth+ "px";
			jspContext.setAttribute("style", style);
		}
		style = labelStyle+ ";" + style;
		labelStyle = style;
		jspContext.setAttribute("labelStyle", labelStyle);
	} else {
		if (labelWidth != null) {
		    labelStyle="width:" + labelWidth+ "px";
	jspContext.setAttribute("labelStyle", labelStyle);
		}
	}
	if ("false".equals(display) || "none".equals(display)) {
		if (cssStyle == null) {
			cssStyle = "display:none;";
		} else {
			cssStyle += ";display:none;";
		}
		jspContext.setAttribute("cssStyle",cssStyle);
	}
	   if (null != islevel && "true".equals(islevel)) {
		if (null != isMustLeaf && "true".equals(isMustLeaf)) {
		jspContext.setAttribute("isMustLeaf",isMustLeaf);
		}
	}
	    final Random RANDOM = new Random();
	    if (data == null) {
	    	//优先 查找resultBean
	    	ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
	    	if(resultBean != null){
	    		Object val =  resultBean.getFieldData()==null?null:resultBean.getFieldData().get(id);
	    		Object v =  resultBean.getFieldData()==null?null:resultBean.getFieldData().get(resultBean._SEL_ +id);
	    		if(v !=null && !"".equals(v)){
	    			if (v.toString().indexOf("[") >= 0 && v.toString().indexOf("]") > 0) {
	    				data = v.toString();
	    				if(val != null && !"".equals(val) && val.toString().indexOf("[") < 0){
	    					value= val.toString();
	    				}
	    			}
	    		}
	    		if(val != null && !"".equals(val) && val.toString().indexOf("[") < 0){
	    			value= val.toString();
	    		}
	         }
	         // 查找request
	if ((value == null || "".equals(value))
				&& request.getAttribute(id) != null) {
			Object attribute = request.getAttribute(id);
			if (attribute instanceof List) {
				data = JSonFactory.bean2json(attribute);
			} else {
				value = request.getAttribute(id).toString();
			}
	         }
	        // 查找session
	if ((value == null || "".equals(value))
			&& request.getSession().getAttribute(id) != null) {
		Object attribute = request.getSession().getAttribute(id);
		if (attribute instanceof List) {
			data = JSonFactory.bean2json(attribute);
		} else {
			value = request.getSession().getAttribute(id)
					.toString();
	    }
		     }
	//
		}
		
	   if (data != null) {
		// 当data非空,value为空,selectFirstValue="true"时,设置默认值.
		if (ValidateUtil.isEmpty(value)) {
			if (selectFirstValue != null && "true".equals(selectFirstValue)) {
				JSONArray json2bean = JSonFactory.json2bean(
						data.toString(), JSONArray.class);
				if (json2bean.size() > 0) {
					Map map = (Map) json2bean.get(0);
					value = map.get("id").toString();
				}
			}
			ResultBean resultBean = (ResultBean) TagUtil
					.getResultBean();
			if (resultBean != null) {
				Object val = resultBean.getFieldData() == null ? null
						: resultBean.getFieldData().get(id);
				// 屏蔽原始字符串
				if (val != null && val.toString().indexOf("[") != 0) {
					value= val.toString();
				}
			}
		}
		}
	if ((id == null || id.length() == 0)) {
	int nextInt = RANDOM.nextInt();
		nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
				.abs(nextInt);
		id = "tatab_" + String.valueOf(nextInt);
		jspContext.setAttribute("id", id);
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
<%@include file="../columnhead.tag"%>
<div <% if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){%> class="fielddiv fielddiv_163" <%}else{ %>
	class="fielddiv" <%} %> <%if(span!=null && !"".equals(span)) {%> span="${span}" <%} %> <%if(columnWidth!=null && !"".equals(columnWidth)) {%>
	columnWidth="${columnWidth}" <%} %> <%if(cssClass!=null && !"".equals(cssClass)) {%> class="${cssClass}" <%} %>
	<%if(cssStyle!=null && !"".equals(cssStyle)) {%> style="${cssStyle}" <%} %> <%if(toolTip!=null && !"".equals(toolTip)) {%> title="${toolTip}" <%} %>>
	<%
		if (key != null && !"".equals(key.trim())) {
	%>
	<label for="${id}_desc" class="fieldLabel" <%if(labelStyle!=null && !"".equals(labelStyle)) {%> style="${labelStyle}" <%} %>> <%
 	if ("true".equals(required)) {
 %> <span style="color:red">*</span> <%
 	}
 %>${key}<%
 	if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
 %>：<%
 	}
 %></label>
	<%
		if("true".equals(readOnly) || "true".equals(disabled)){
	%>
	<div class="fielddiv2 readonly" <%}else{%>
		<div class="fielddiv2"
	<%}%>
	 <%if(labelWidth!=null && !"".equals(labelWidth)) {%>
			style="margin-left:${labelWidth}px" 
	<%}else if(null == key || "".equals(key.trim())) {%>
		style="margin-left:0px" 
	<%} %>
	>
			<div id="${id}_div" class="textinput selinput"></div></div>
	<%
		} else {
	%>
	<div id="${id}_div" class="textinput selinput"></div>
	<%
		}
	%>
	<%
		if(textHelp != null){
	%>
	<div class="textInfo">
		<%
			if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
		%>
		<div
			class="textInfo_content ffb_163
		<%}else{ %>
		<div class="textInfo_content ffb
		<%} %>
		 <%if(textHelpPosition != null)%>${textHelpPosition}" style="<%=textHelpStyle%>">${textHelp}</div>
		<script>
			var $textInfo = $("#<%=id%> + div.textInfo > div.textInfo_content"
			);
			var textInfoHeight=$textInfo.height()
			+ 45;
			<%if("topLeft".equals(textHelpPosition) || "topRight".equals(textHelpPosition)){ %>
			$textInfo.css("top","-"+textInfoHeight+"px");
			<%} %>
			</script>
		</div>
		<%
			}
		%>
	</div>
	<%@include file="../columnfoot.tag"%>
	<script type="text/javascript"> 
(function(factory){
	if (typeof require === 'function') {
		require(["jquery", "TaUIManager", "selectInput"], factory);
	} else {
		factory(jQuery);
	}
}(function($){	

	Ta.core.TaUICreater.addUI( 
	function(){ 
	var option = {}; 
	var data = {};
	var localCache;
	var value;
	<%if(data == null && ((collection != null && !"".equals(collection)) 
							|| (collectionTree != null && !"".equals(collectionTree)))) {
		collection = "\""+collection+"\"";%>
		
		localCache = localStorage.getItem(<%=collection%>);
		if(localCache){
			data = eval(localCache);
		}
	
	<%}else{%>
		data = <%=data%>;
	<%}%>
	if(data.length > 0){
		<%if (ValidateUtil.isEmpty(value)) {%>
			<%if (selectFirstValue != null && "true".equals(selectFirstValue)) {%>
					var obj = data[0];
					value = obj.id;
			<%ResultBean resultBean = (ResultBean) TagUtil
							.getResultBean();
					if (resultBean != null) {
						Object val = resultBean.getFieldData() == null ? null
								: resultBean.getFieldData().get(id);
						// 屏蔽原始字符串
						if (val != null && val.toString().indexOf("[") != 0) {
							value= val.toString();
						}
					}
			}%>
		<%}%>
	}
				<%if(id!=null && !"".equals(id)) {%>
	option.divId="${id}"; 
				<%}%>
				<%if(name!=null && !"".equals(name)) {%>
	option.name="${name}"; 
				<%}%>
				<%if(selectPanelWidth!=null && !"".equals(selectPanelWidth)) {%>
	option.selectPanelWidth="${selectPanelWidth}"; 
				<%}%>
				<%if(name==null||"".equals(name)) {%>
	option.name="dto['${id}']"; 
				<%}%>
				<%if(value!=null && !"".equals(value)) {%>
	option.value="<%=value%>"; 
				<%}else{%>
					if(value){
						option.value = value;
					}
				<%}%>
				<%if(collection!=null && !"".equals(collection)) {%>
	option.collection="${collection}"; 
				<%}%>
				<%if(collectionTree!=null && !"".equals(collectionTree)) {%>
	option.collectionTree="${collectionTree}"; 
				<%}%>
				<%if(allowInputOtherText!=null && !"".equals(allowInputOtherText)) {%>
	option.allowInputOtherText=${allowInputOtherText}; 
				<%}%>
				<%if("true".equals(required)) {%>
	option.required="${required}"; 
				<%}%>
				<%if(placeholder!=null && !"".equals(placeholder)) {%>
	option.placeholder="${placeholder}"; 
				<%}%>
				<%if(clearData!=null && !"".equals(clearData)) {%>
	option.clearData="${clearData}"; 
				<%}%>
				<%if(showRefresh!=null && !"".equals(showRefresh)) {%>
	option.showRefresh="${showRefresh}"; 
				<%}%>
				<%if(filter!=null && !"".equals(filter)) {%>
	option.filter="${filter}"; 
				<%}%>
				<%if(filterOrg!=null && !"".equals(filterOrg)) {%>
	option.filterOrg="${filterOrg}"; 
				<%}%>
				<%if(reverseFilter!=null && !"".equals(reverseFilter)) {%>
	option.reverseFilter=${reverseFilter}; 
				<%}%>
				<%if(maxVisibleRows!=null && !"".equals(maxVisibleRows)) {%>
	option.maxVisibleRows="${maxVisibleRows}"; 
				<%}%>
				<%if(disabled!=null && !"".equals(disabled)) {%>
	option.disabled="${disabled}"; 
				<%}%>
				<%if(readOnly!=null && !"".equals(readOnly)) {%>
	option.readonly="${readOnly}"; 
				<%}%>
				<%if(showKey!=null && !"".equals(showKey)) {%>
	option.showKey=${showKey}; 
				<%}%>
				<%if(showValue !=null && !"".equals(showValue)) {%>
	option.showValue="${showValue}";
				<%}%>
			<%if(onSelect!=null && !"".equals(onSelect)) {%>
	option.onSelect=${onSelect} ;
	<%}%>
		<%if(onChange!=null && !"".equals(onChange)) {%>
	option.onChange=${onChange} ;
	<%}%>	
	<%if(onFocus!=null && !"".equals(onFocus)) {%>
	option.onFocus=${onFocus} ;
	<%}%>
	<%if(displayValue!=null && !"".equals(displayValue)) {%>
	option.displayValue="${displayValue}"; 
	option.resultTemplate="{${displayValue}}";
	<%}%>
	<%if(hiddenValue!=null && !"".equals(hiddenValue)) {%>
	option.hiddenValue="${hiddenValue}"; 
	<%}%>
	<%if(pyFilter!=null && !"".equals(pyFilter)) {%>
	option.pyFilter="${pyFilter}"; 
	<%}%>
	<%if(isAutoExtend!=null && !"".equals(isAutoExtend)) {%>
	option.isAutoExtend=${isAutoExtend}; 
	<%}%>
	<%if(selectFirstValue!=null && !"".equals(selectFirstValue)) {%>
	option.selectFirstValue="${selectFirstValue}"; 
	<%}%>
	<%if(isFocusShowPanel!=null && !"".equals(isFocusShowPanel)) {%>
	option.isFocusShowPanel=${isFocusShowPanel}; 
	<%}%>
	<%if(widthPercentage!=null && !"".equals(widthPercentage)) {%>
	option.widthPercentage="${widthPercentage}"; 
	<%}%>	
	<%if(isMustLeaf!=null && !"".equals(isMustLeaf)) {%>
	option.isMustLeaf="${isMustLeaf}"; 
	<%}%>
	<%if(minLevel!=null && !"".equals(minLevel)) {%>
	option.minLevel="${minLevel}"; 
	<%}%>
	<%if(maxLevel!=null && !"".equals(maxLevel)) {%>
	option.maxLevel="${maxLevel}"; 
	<%}%>
	<%if(islevel!=null && !"".equals(islevel)) {%>
	option.islevel="${islevel}"; 
	<%}%>
	<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){%>
	option.selectClass="ffb-select_163";
	option.itemHeight=27;
	<%}%>
	if(data == null || data == "null"){
		data = eval("[]");
	}
	
	var slectInput_${id} = $("#${id}_div").flexbox(data, option); 
				<%if(value!=null && !"".equals(value)) {%>
					slectInput_${id}[0].setValue("<%=value%>");
				<%}%>
				<%if(filter!=null && !"".equals(filter)) {%>
	slectInput_${id}[0].setDisableSelect("${filter}"); 
				<%}%>
				<%if(filterStartChar!=null && !"".equals(filterStartChar)) {%>
	slectInput_${id}[0].setfilterStartChar("${filterStartChar}"); 
				<%}%>
				<%if(fiterValueLengthMin!=null && !"".equals(fiterValueLengthMin)) {%>
	slectInput_${id}[0].fiterValueLengthMin("${fiterValueLengthMin}"); 
				<%}%>
				<%if(fiterValueLengthMax!=null && !"".equals(fiterValueLengthMax)) {%>
	slectInput_${id}[0].fiterValueLengthMax("${fiterValueLengthMax}"); 
				<%}%>
	Ta.core.TaUIManager.register("${id}",slectInput_${id}[0]); 
			}); 
//requirejsend		
}));
	</script>
	<%
		}else{
	%>
	<%--使用老版的缓存,不使用localStorage --%>
	<%
		if (("true".equals(readOnly) || "true".equals(disabled))
				&& "true".equals(serverCvtCode)
				&& null != collection
				&& !"".equals(collection)) {
			UserSession us = UserSession.getUserSession(request);
			
			Map<String, String> map = (HashMap<String, String>) us.getCurrentBusiness().getSessionResource("__selectinput_flag_map_");
			if (map == null) {
				map = new HashMap();
			}
			map.put(id, collection.toString());
			us.getCurrentBusiness().putSessionResource("__selectinput_flag_map_", map);
		}
		        if (labelStyle != null) {
			String style = "";
			if (labelWidth != null) {
				style = "width:" + labelWidth+ "px";
				jspContext.setAttribute("style", style);
			}
			style = labelStyle+ ";" + style;
			labelStyle = style;
			jspContext.setAttribute("labelStyle", labelStyle);
		} else {
			if (labelWidth != null) {
			    labelStyle="width:" + labelWidth+ "px";
		jspContext.setAttribute("labelStyle", labelStyle);
			}
		}
		if ("false".equals(display) || "none".equals(display)) {
			if (cssStyle == null) {
				cssStyle = "display:none;";
			} else {
				cssStyle += ";display:none;";
			}
			jspContext.setAttribute("cssStyle",cssStyle);
		}
		   if (null != islevel && "true".equals(islevel)) {
			if (null != isMustLeaf && "true".equals(isMustLeaf)) {
			jspContext.setAttribute("isMustLeaf",isMustLeaf);
			}
		}
		    final Random RANDOM = new Random();
		    if (data == null) {
		    	//优先 查找resultBean
		    	ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
		    	if(resultBean != null){
		    		Object val =  resultBean.getFieldData()==null?null:resultBean.getFieldData().get(id);
		    		Object v =  resultBean.getFieldData()==null?null:resultBean.getFieldData().get(resultBean._SEL_ +id);
		    		if(v !=null && !"".equals(v)){
		    			if (v.toString().indexOf("[") >= 0 && v.toString().indexOf("]") > 0) {
		    				data = v.toString();
		    				if(val != null && !"".equals(val) && val.toString().indexOf("[") < 0){
		    					value= val.toString();
		    				}
		    			}
		    		}
		    		if(val != null && !"".equals(val) && val.toString().indexOf("[") < 0){
		    			value= val.toString();
		    		}
		         }
		         // 查找request
		if ((value == null || "".equals(value))
					&& request.getAttribute(id) != null) {
				Object attribute = request.getAttribute(id);
				if (attribute instanceof List) {
					data = JSonFactory.bean2json(attribute);
				} else {
					value = request.getAttribute(id).toString();
				}
		         }
		        // 查找session
		if ((value == null || "".equals(value))
				&& request.getSession().getAttribute(id) != null) {
			Object attribute = request.getSession().getAttribute(id);
			if (attribute instanceof List) {
				data = JSonFactory.bean2json(attribute);
			} else {
				value = request.getSession().getAttribute(id)
						.toString();
		    }
			     }
		//
		if (data == null
					&& ((collection != null && !""
							.equals(collection)) || (collectionTree != null && !""
							.equals(collectionTree)))) {
				String orgId = null;
				UserSession userSession = UserSession.getUserSession(request);
				if (userSession!=null && userSession.getUser() != null) {
					orgId = userSession.getUser().getOrgId();
				}
				if ("false".equals(filterOrg)) {
					orgId = null;
				}
				StringBuilder sb = new StringBuilder();
				if (("true".equals(readOnly) || "true".equals(disabled))
						&& "true".equals(serverCvtCode)
						&& collection != null
						&& !"".equals(collection)) {
					if (value != null && !"".equals(value)) {
						String desc = CodeTableLocator.getInstance().getCodeDesc(collection,
								value, orgId);
						sb.append("[");
						sb.append("{\"id\":\"").append(value).append("\",");
						sb.append("\"name\":\"").append(desc).append("\"}");
						sb.append("]");
					}
				} else {
					if (collectionTree != null && !"".equals(collectionTree)) {
						sb.append(CodeTableLocator.getInstance()
								.getCodeLevelListJson(collectionTree, orgId));
					} else
						sb.append(CodeTableLocator.getInstance()
								.getCodeListJson(collection, orgId));
				}
				
				data=sb.toString();
			}
			    //
		    }
		    if (data != null) {
			// 当data非空,value为空,selectFirstValue="true"时,设置默认值.
			if (ValidateUtil.isEmpty(value)) {
				if (selectFirstValue != null && "true".equals(selectFirstValue) && !"[]".equals(data)) {
					JSONArray json2bean = JSonFactory.json2bean(
							data.toString(), JSONArray.class);
					if (json2bean.size() > 0) {
						Map map = (Map) json2bean.get(0);
						value = map.get("id").toString();
					}
				}
				ResultBean resultBean = (ResultBean) TagUtil
						.getResultBean();
				if (resultBean != null) {
					Object val = resultBean.getFieldData() == null ? null
							: resultBean.getFieldData().get(id);
					// 屏蔽原始字符串
					if (val != null && val.toString().indexOf("[") != 0) {
						value= val.toString();
					}
				}
			}
		}
		
		
		if ((id == null || id.length() == 0)) {
			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "tatab_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
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
	<%@include file="../columnhead.tag"%>
	<div <% if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){%> class="fielddiv fielddiv_163" <%}else{ %>
		class="fielddiv" <%} %> <%if(span!=null && !"".equals(span)) {%> span="${span}" <%} %> <%if(columnWidth!=null && !"".equals(columnWidth)) {%>
		columnWidth="${columnWidth}" <%} %> <%if(cssClass!=null && !"".equals(cssClass)) {%> class="${cssClass}" <%} %>
		<%if(cssStyle!=null && !"".equals(cssStyle)) {%> style="${cssStyle}" <%} %> <%if(toolTip!=null && !"".equals(toolTip)) {%> title="${toolTip}" <%} %>>
		<%
			if (key != null && !"".equals(key.trim())) {
		%>
		<label for="${id}_desc" class="fieldLabel" <%if(labelStyle!=null && !"".equals(labelStyle)) {%> style="${labelStyle}" <%} %>> <%
 	if ("true".equals(required)) {
 %> <span style="color:red">*</span> <%
 	}
 %>${key}<%
 	if(!!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
 %>：<%
 	}
 %></label>
		<%
			if("true".equals(readOnly) || "true".equals(disabled)){
		%>
		<div class="fielddiv2 readonly" <%}else{%>
			<div class="fielddiv2"
	<%}%>
	 <%if(labelWidth!=null && !"".equals(labelWidth)) {%>
			style="margin-left:${labelWidth}px"
		 <%}else if(null == key || "".equals(key.trim())){%>
		 style="margin-left:0px"
		 <%} %>
		 >
			<div id="${id}_div" class="textinput selinput"></div></div>
		<%
			} else {
			if("true".equals(readOnly) || "true".equals(disabled)){
		%>
		<div class="fielddiv2 readonly" <%}else{%>
			<div class="fielddiv2"
	        <%}%>
	        <%if(labelWidth!=null && !"".equals(labelWidth)) {%>
			   style="margin-left:${labelWidth}px"
		 <%}else if(null == key || "".equals(key.trim())){%>
		 style="margin-left:0px"
		 <%} %>
		 >
		        <div id="${id}_div" class="textinput selinput"></div></div>
		<%
			}
		%>
		<%
			if(textHelp != null){
		%>
		<div class="textInfo">
			<%
				if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){
			%>
			<div
				class="textInfo_content ffb_163
		<%}else{ %>
		<div class="textInfo_content ffb
		<%} %>
		 <%if(textHelpPosition != null)%>${textHelpPosition}" style="<%=textHelpStyle%>">${textHelp}</div>
		<script>
			var $textInfo = $("#<%=id%> + div.textInfo > div.textInfo_content"
				);
			var textInfoHeight=$textInfo.height()
				+ 45;
			<%if("topLeft".equals(textHelpPosition) || "topRight".equals(textHelpPosition)){ %>
			$textInfo.css("top","-"+textInfoHeight+"px");
			<%} %>
				</script>
			</div>
			<%
				}
			%>
		</div>
		<%@include file="../columnfoot.tag"%>
		<script type="text/javascript"> 
(function(factory){
	if (typeof require === 'function') {
		require(["jquery", "TaUIManager", "selectInput"], factory);
	} else {
		factory(jQuery);
	}
}(function($){	
	Ta.core.TaUICreater.addUI( 
	function(){ 
	var option = {}; 
	var data = []; 
				<%if(data!=null && !"".equals(data)) {%>
	data=<%=data%>; 
				<%}%>
				<%if(id!=null && !"".equals(id)) {%>
	option.divId="${id}"; 
				<%}%>
				<%if(name!=null && !"".equals(name)) {%>
	option.name="${name}"; 
				<%}%>
				<%if(name==null||"".equals(name)) {%>
	option.name="dto['${id}']"; 
				<%}%>
				<%if(value!=null && !"".equals(value)) {%>
	option.value="<%=value%>"; 
				<%}%>
				<%if(placeholder!=null && !"".equals(placeholder)) {%>
	option.placeholder="${placeholder}"; 
				<%}%>
				<%if(clearData!=null && !"".equals(clearData)) {%>
	option.clearData="${clearData}"; 
				<%}%>
				<%if(collection!=null && !"".equals(collection)) {%>
	option.collection="${collection}"; 
				<%}%>
				<%if(collectionTree!=null && !"".equals(collectionTree)) {%>
	option.collectionTree="${collectionTree}"; 
				<%}%>
				<%if(allowInputOtherText!=null && !"".equals(allowInputOtherText)) {%>
	option.allowInputOtherText=${allowInputOtherText}; 
				<%}%>
				<%if("true".equals(required)) {%>
	option.required="${required}"; 
				<%}%>
				<%if(showRefresh!=null && !"".equals(showRefresh)) {%>
	option.showRefresh="${showRefresh}"; 
				<%}%>
				<%if(filter!=null && !"".equals(filter)) {%>
	option.filter="${filter}"; 
				<%}%>
				<%if(filterOrg!=null && !"".equals(filterOrg)) {%>
	option.filterOrg="${filterOrg}"; 
				<%}%>
				<%if(reverseFilter!=null && !"".equals(reverseFilter)) {%>
	option.reverseFilter=${reverseFilter}; 
				<%}%>
				<%if(maxVisibleRows!=null && !"".equals(maxVisibleRows)) {%>
	option.maxVisibleRows="${maxVisibleRows}"; 
				<%}%>
				<%if(disabled!=null && !"".equals(disabled)) {%>
	option.disabled="${disabled}"; 
				<%}%>
				<%if(readOnly!=null && !"".equals(readOnly)) {%>
	option.readonly="${readOnly}"; 
				<%}%>
				<%if(showKey!=null && !"".equals(showKey)) {%>
	option.showKey=${showKey}; 
				<%}%>
				<%if(selectPanelWidth!=null && !"".equals(selectPanelWidth)) {%>
	option.selectPanelWidth="${selectPanelWidth}"; 
				<%}%>
			<%if(onSelect!=null && !"".equals(onSelect)) {%>
	option.onSelect=${onSelect} ;
	<%}%>
		<%if(onChange!=null && !"".equals(onChange)) {%>
	option.onChange=${onChange} ;
	<%}%>	
	<%if(onFocus!=null && !"".equals(onFocus)) {%>
	option.onFocus=${onFocus} ;
	<%}%>
	<%if(displayValue!=null && !"".equals(displayValue)) {%>
	option.displayValue="${displayValue}"; 
	option.resultTemplate="{${displayValue}}";
	<%}%>
	<%if(hiddenValue!=null && !"".equals(hiddenValue)) {%>
	option.hiddenValue="${hiddenValue}"; 
	<%}%>
	<%if(pyFilter!=null && !"".equals(pyFilter)) {%>
	option.pyFilter="${pyFilter}"; 
	<%}%>
	<%if(isAutoExtend!=null && !"".equals(isAutoExtend)) {%>
	option.isAutoExtend=${isAutoExtend}; 
	<%}%>
	<%if(selectFirstValue!=null && !"".equals(selectFirstValue)) {%>
	option.selectFirstValue="${selectFirstValue}"; 
	<%}%>
	<%if(isFocusShowPanel!=null && !"".equals(isFocusShowPanel)) {%>
	option.isFocusShowPanel=${isFocusShowPanel}; 
	<%}%>
	<%if(widthPercentage!=null && !"".equals(widthPercentage)) {%>
	option.widthPercentage="${widthPercentage}"; 
	<%}%>	
	<%if(isMustLeaf!=null && !"".equals(isMustLeaf)) {%>
	option.isMustLeaf="${isMustLeaf}"; 
	<%}%>
	<%if(minLevel!=null && !"".equals(minLevel)) {%>
	option.minLevel="${minLevel}"; 
	<%}%>
	<%if(maxLevel!=null && !"".equals(maxLevel)) {%>
	option.maxLevel="${maxLevel}"; 
	<%}%>
	<%if(islevel!=null && !"".equals(islevel)) {%>
	option.islevel="${islevel}"; 
	<%}%>
	<%if(!IConstants.INDEX_STYLE_CLASSIC.equals(AppManager.getSysConfig(IConstants.INDEX_STYLE))){%>
	option.selectClass="ffb-select_163";
	option.itemHeight=27;
	<%}%>
	if(data == null || data =="null"){
		data = eval("[]");
	}
	var slectInput_${id} = $("#${id}_div").flexbox(data, option); 
		<%if(value!=null && !"".equals(value)) {%>
					slectInput_${id}[0].setValue("<%=value%>
			");
		<%}%>
			
		<%if(filter!=null && !"".equals(filter)) {%>
			slectInput_${id}[0]
											.setDisableSelect("${filter}");
		<%}%>
			
		<%if(filterStartChar!=null && !"".equals(filterStartChar)) {%>
			slectInput_${id}[0]
											.setfilterStartChar("${filterStartChar}");
		<%}%>
			
		<%if(fiterValueLengthMin!=null && !"".equals(fiterValueLengthMin)) {%>
			slectInput_${id}[0]
											.fiterValueLengthMin("${fiterValueLengthMin}");
		<%}%>
			
		<%if(fiterValueLengthMax!=null && !"".equals(fiterValueLengthMax)) {%>
			slectInput_${id}[0]
											.fiterValueLengthMax("${fiterValueLengthMax}");
		<%}%>
			Ta.core.TaUIManager.register("${id}",slectInput_${id}[0]);
								});
						//requirejsend		
					}));
</script>

		<%
			}
		%>
		<jsp:doBody />