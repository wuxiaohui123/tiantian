<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.config.SysConfig"%>
<%@tag import="java.util.Locale"%>
<%@tag import="java.lang.reflect.Method"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="com.yinhai.webframework.session.UserSession"%>
<%@tag import="com.yinhai.sysframework.codetable.service.CodeTableLocator"%>
<%@tag import="java.util.Random"%>

<%--@doc--%>
<%@tag description='datagrid列信息' display-name='datagridItem' %>
<%@attribute description='组件id匹配field' name='id' type='java.lang.String' %>
<%@attribute description='数据绑定field,默认与id相同' name='field' type='java.lang.String' %>
<%@attribute description='列宽度' name='width' type='java.lang.String' %>
<%@attribute description='是否支持排序' name='sortable' type='java.lang.String' %>
<%@attribute description='left/center/right,列标题对齐方式，默认left' name='align' type='java.lang.String' %>
<%@attribute description='列渲染回调函数,回掉函数默认传参function(row, cell, value, columnDef, dataContext),参数的意思分别是行号，列号，值，该列属性信息，当前行数据，自定义函数不要写括号，例如:formatter="fnFormatter",在javascript中，function fnFormatter(row, cell, value, columnDef, dataContext){var columnName = columnDef.name,aac003 = dataContext.aac003;return value;}' name='formatter' type='java.lang.String' %>
<%@attribute description='设置当前列为显示图标' name='icon' type='java.lang.String' %>
<%@attribute description='设置是否显示合计列的文字，设置为false时，全部不显示' name='tatalsTextShow' type='java.lang.String' %>
<%@attribute description='设置本列是否为后台转码' name='serverCvtCode' type='java.lang.String' %>
<%@attribute description='列点击事件,当单击此列时发生，默认传入参数data,e,分别为点击该单元格所在的行数据及事件，例如:click="fnClick",然后在javascript中，function fnClick(data,e){var row = data.row,cell = data.cell,aac003 = data.aac003}' name='click' type='java.lang.String' %>
<%@attribute description='设置列的码表转换' name='collection' type='java.lang.String' %>
<%@attribute description='设置列是否显示，默认为显示' name='hiddenColumn' type='java.lang.String' %>
<%@attribute description='设置列的数据类型:string,number,date,dateTime，默认String' name='dataType' type='java.lang.String' %>
<%@attribute description='列标题' name='key' type='java.lang.String' %>
<%@attribute description='left/center/right，数据对齐方式，默认left' name='dataAlign' type='java.lang.String' %>
<%@attribute description='设置统计方式，有avg(平均值)，sum(总和)，max(最大值)，min(最小值)' name='totals' type='java.lang.String' %>
<%@attribute description='是否当鼠标指上单元格时，在指针右下角显示此单元格的全部内容。常用于单元格内容过多，导致单元格无法完全显示全部信息的情况' name='showDetailed' type='java.lang.String' %>
<%@attribute description='手动设置Collection的值，当存在collection及collectionData时，优先选择collectionData' name='collectionData' type='java.lang.String' %>
<%@attribute description='true/false,默认false，获取选中行时，是否获取该列数据' name='asKey' type='java.lang.String' %>
<%@attribute description='表头背景颜色,例如headerBackgroundColor="#123456"' name='headerBackgroundColor' type='java.lang.String' %>
<%@attribute description='格式化统计信息,必须返回一个值,默认有个value参数,表示统计值.例如:totalsFormatter="fnTotalsFormatter",在js中,function fnTotalsFormatter(value){return value};' name='totalsFormatter' type='java.lang.String' %>
<%@attribute description='列宽,以最大显示字数显示,以中文为准.例如:maxChart="10",表示最多显示10个中文的宽度' name='maxChart' type='java.lang.String' %>
<%@attribute description='统计行数据对齐方式,left/center/right.例如:totalsAlign="left",默认left' name='totalsAlign' type='java.lang.String' %>
<%@attribute description='mediadata' name='mediaData' type='java.lang.String' %>
<%@attribute description='是否根据内容宽度来确定列宽度,默认每个英文默认8px,中文16px,该属性填写表格列最大宽度' name='perqWidthWithData' type='java.lang.String' %>
<%@attribute description='是否启用延迟加载,true/false.默认为false' name='lazy' type='java.lang.String' %>

<%--@doc--%>
<%
	
	boolean flagConfig = SysConfig.getSysconfigToBoolean("neworold");
	boolean lazyBoolean = false;
	if(lazy != null && !"".equals(lazy)){
		lazyBoolean = Boolean.parseBoolean(lazy);
	}
	boolean flag = flagConfig && lazyBoolean;
	if(flag){%>
		<%-- 使用新版的localStorage缓存 --%>
		<%--media --%>
		<%
			PropertyDescriptor pd = new PropertyDescriptor("mediaColumn", TagUtil.getTa3ParentTag(getParent()).getClass());
			Method method = pd.getReadMethod();
			String st = (String)method.invoke(TagUtil.getTa3ParentTag(getParent()));
			if ("true".equals(st)){
		%>
		            <th
						<% if (null != sortable){%>
						 sortable="true"
						<%} %> field="${id}"<% if (null != width){%>width="${width}" <%} %>
							<% if (null != hiddenColumn){%>
								hidden="true" 
							<%} 
						if (null != mediaData){%>
							data-options = ${mediaData} 
						<%} %>
					>
		           		${key}
		            </th> 
		<%}else { %>
		
		<%
		if (field != null) {
			field = field;
		} else if (id != null) {
			field = id;
		}
		if(collection != null){
			collection = "\""+collection.toUpperCase(Locale.ENGLISH)+"\"";
		}
		if (ValidateUtil.isEmpty(id)) {
			int nextInt = new Random().nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			this.id = "tadatagriditem_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
		}
		String _grid_ = (String)jspContext.getAttribute("_grid_", PageContext.REQUEST_SCOPE);
		if (_grid_ != null){
			jspContext.setAttribute("grid", id, PageContext.REQUEST_SCOPE);
		}
		
		if (totals != null) {
			jspContext.setAttribute("totals", totals);
			if (totalsAlign != null) {
				jspContext.setAttribute("totalsAlign", totalsAlign);
			}
		}
			
		%>
		var c_${id} = {};
		var collectionData;
		var collection;
		c_${id}.id = "${id}";
		c_${id}.name = "${key}";
		<% if( null != field) {%>
		c_${id}.field = "<%=field %>";
		<% }%>
		<% if( null != hiddenColumn) {%>
			h.push(c_${id}.id);
		<% }%>
		<% if( null != width) {%>
			var tempWidth = "${width}".replace("px","");
			c_${id}.width = Number(tempWidth);
		<% }%>
		<% if( null != maxChart) {%>
			var tempWidth = ${maxChart};
			c_${id}.width = (Number(tempWidth)+2) * 13;
			
		<% }%>
		<% if( null != sortable) {%>
		c_${id}.sortable = ${sortable};
		<% }%>
		<% if( null != headerBackgroundColor) {%>
		c_${id}.headerBackgroundColor = "${headerBackgroundColor}";
		<% }%>
		<% if( null != icon) {%>
		c_${id}.icon = "${icon}";
		<% }%>
		<% if( null != formatter) {%>
		c_${id}.formatter = ${formatter};
		<% }%>
		<% 
		//通过服务器获取前台转码collectionData
		//TODO if(collection != null && !"".equals(collection)&&grid != null && ( serverCvtCode == null || serverCvtCode =="false" )&&(grid.getServerCutCode() == null ||grid.getServerCutCode().equals("false"))){
		if(ValidateUtil.isNotEmpty(collection) && _grid_ != null && ( serverCvtCode == null || serverCvtCode =="false" )){
			if (collectionData == null) {
		%>		
				collection = <%=collection%>;
				collectionData = "collectionData";//此处只做判断,实际取值在下面;
				
		<%} else {%>
		   <%}
		} else {  
		 	if (collectionData != null) {
		 		collection = collectionData;
				%>		
				collection = "collectionData";
			<%} 
		}
		%>
		
			 <%if(collection != null) {%>
				<% if( null != serverCvtCode) {%>
					serverCvtCode_column = ${serverCvtCode};
				<% }%>
				if (serverCvtCode || serverCvtCode_column) {
					c_${id}.collection = collection;
				}else {
					c_${id}.formatter = function(row, cell, value, columnDef, dataContext) {
						collectionData = "<%=collectionData %>";
						<% 
							//通过服务器获取前台转码collectionData
							//TODO if(collection != null && !"".equals(collection)&&grid != null && ( serverCvtCode == null || serverCvtCode =="false" )&&(grid.getServerCutCode() == null ||grid.getServerCutCode().equals("false"))){
							if(ValidateUtil.isNotEmpty(collection) && _grid_ != null && ( serverCvtCode == null || serverCvtCode =="false" )){
								if (collectionData == null) {
							%>		
									collection = <%=collection%>;
									collectionData = localStorage.getItem(<%=collection%>);
									
							<%} else {%>
							   <%}
							} else {  
							 	if (collectionData != null) {
							 		collection = collectionData;
									%>		
									collection = "collectionData";
								<%} 
							}
							%>
							var reData = value;
							if(collectionData != null && collectionData != "null") {
								var data = eval(collectionData);
								data.column = "${id}";
								o.collectionsDataArrayObject.${id} = data;
								for (var i = 0; i < data.length; i ++) {
									if (data[i].id == value) {
										reData = data[i].name;
									}
								}
							}
							<% if( null != formatter) {%>
								reData = ${formatter}(row, cell, reData, columnDef, dataContext);
							<% }%>
								return reData? reData: "";
					}
				}
			<% }%>
		
		<% if( null != click) {%>
		c_${id}.click  = ${click};
		<% }%>
		<% if( null != dataType) {%>
		c_${id}.dataType  = "${dataType}";
		<% }%>
		<% if( null != align) {%>
		c_${id}.align  = "${align}";
		<% }%>
		<% if( null != dataAlign) {%>
		c_${id}.dataAlign  = "${dataAlign}";
		<% }%>
		<% if( null != showDetailed) {%>
		c_${id}.showDetailed  = ${showDetailed};
		<% }%>
		<% if( null != asKey) {%>
		c_${id}.propertyKey  = ${asKey};
		<% }%>
		<% if( null != totalsFormatter) {%>
		c_${id}.totalsFormatter  = ${totalsFormatter};
		<% }%>
		<% if( null != totalsAlign) {%>
		c_${id}.totalsAlign  = "${totalsAlign}";
		<% }%>
		<% if( null != totals) {%>
			o.groupingBy = "_onlyTotals";
			c_${id}.totals = "${totals}"; 
			c_${id}.groupTotalsFormatter  = function(totals, columnDef) {
					var text = "";
					<% if( null != tatalsTextShow) {%>
						var tatalsTextShow = ${tatalsTextShow};
					<% } else { %>
						var tatalsTextShow = true;
					<% }%>
					if (tatalsTextShow == true) {
						if ("${totals}" == "avg") { 
							text = "平均: ";
						}  else if ("${totals}" == "sum") { 
							text = "合计: ";
						}  else if ("${totals}" == "max") {
							text = "最大: ";
						}  else if ("${totals}" == "min")  { 
							text = "最小: ";
						}
					}
					return text + totals.${totals}[columnDef.field] ;
			};
		<% }%>
		<jsp:doBody/>
		c.push(c_${id});
		<%}%>
	<% }else{%> 
		<%--使用老版的缓存,不使用localStorage --%>
		<%--media --%>
		<%
			PropertyDescriptor pd = new PropertyDescriptor("mediaColumn", TagUtil.getTa3ParentTag(getParent()).getClass());
			Method method = pd.getReadMethod();
			String st = (String)method.invoke(TagUtil.getTa3ParentTag(getParent()));
			if ("true".equals(st)){
		%>
		            <th
						<% if (null != sortable){%>
						 sortable="true"
						<%} %> field="${id}"<% if (null != width){%>width="${width}" <%} %>
							<% if (null != hiddenColumn){%>
								hidden="true" 
							<%} 
						if (null != mediaData){%>
							data-options = ${mediaData} 
						<%} %>
					>
		           		${key}
		            </th> 
		<%}else { %>
		
		<%
		if (field != null) {
			field = field;
		} else if (id != null) {
			field = id;
		}
		if (ValidateUtil.isEmpty(id)) {
			int nextInt = new Random().nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			this.id = "tadatagriditem_" + String.valueOf(nextInt);
			jspContext.setAttribute("id", id);
		}
		String _grid_ = (String)jspContext.getAttribute("_grid_", PageContext.REQUEST_SCOPE);
		if (_grid_ != null){
			jspContext.setAttribute("grid", id, PageContext.REQUEST_SCOPE);
		}
		//通过服务器获取前台转码collectionData
		//TODO if(collection != null && !"".equals(collection)&&grid != null && ( serverCvtCode == null || serverCvtCode =="false" )&&(grid.getServerCutCode() == null ||grid.getServerCutCode().equals("false"))){
		if(ValidateUtil.isNotEmpty(collection) && _grid_ != null && ( serverCvtCode == null || serverCvtCode =="false" )){
			if (collectionData == null) {
				String orgId = null;
		        
		        UserSession userSession = UserSession.getUserSession(request);
		        if(userSession!=null && userSession.getUser() != null)
		        	orgId = userSession.getUser().getOrgId();
		      
		//	        if("false".equals(filterOrg)){
		//	        	orgId = null;
		//	        }
		    	collectionData = CodeTableLocator.getInstance().getCodeListJson(collection, orgId);
		        jspContext.setAttribute("collectionData" ,collectionData);
			} else {
		    	jspContext.setAttribute("collectionData" ,collectionData);
		    }
		} else {
			if (collectionData != null) {
				collection = collectionData;		
				jspContext.setAttribute("collection" , "collectionData");
				jspContext.setAttribute("collectionData" ,collectionData);
			}
		}
		if (totals != null) {
			jspContext.setAttribute("totals", totals);
			if (totalsAlign != null) {
				jspContext.setAttribute("totalsAlign", totalsAlign);
			}
		}
		// 	Object obj = jspContext.getAttribute("_grid_obj", PageContext.REQUEST_SCOPE);
		// 	PropertyDescriptor pd = new PropertyDescriptor("data", TagUtil.getTa3ParentTag(getParent()).getClass());
		// 	Method method = pd.getReadMethod();
		// 	String st = (String)method.invoke(TagUtil.getTa3ParentTag(getParent()));
			
		%>
		var c_${id} = {};
		c_${id}.id = "${id}";
		c_${id}.name = "${key}";
		<% if( null != field) {%>
		c_${id}.field = "<%=field %>";
		<% }%>
		<% if( null != hiddenColumn) {%>
			h.push(c_${id}.id);
		<% }%>
		<% if( null != width) {%>
			var tempWidth = "${width}".replace("px","");
			c_${id}.width = Number(tempWidth);
		<% }%>
		<% if( null != maxChart) {%>
			var tempWidth = ${maxChart};
			c_${id}.width = (Number(tempWidth)+2) * 13;
			
		<% }%>
		<%if (null != perqWidthWithData){%>
		c_${id}.perqWidthWithData = ${perqWidthWithData};
		o.perqWidthWithData = true;	
		<%}%>
		<% if( null != sortable) {%>
		c_${id}.sortable = ${sortable};
		<% }%>
		<% if( null != headerBackgroundColor) {%>
		c_${id}.headerBackgroundColor = "${headerBackgroundColor}";
		<% }%>
		<% if( null != icon) {%>
		c_${id}.icon = "${icon}";
		<% }%>
		<% if( null != formatter) {%>
		c_${id}.formatter = ${formatter};
		<% }%>
			<% if( null != collection) {%>
				<% if( null != serverCvtCode) {%>
					serverCvtCode_column = ${serverCvtCode};
				<% }%>
				if (serverCvtCode || serverCvtCode_column) {
					c_${id}.collection = "${collection}";
				}else {
					c_${id}.formatter = function(row, cell, value, columnDef, dataContext) {
							var reData = value;
							<% if( null != collectionData) {%>
								var data = <%=collectionData%>;
								data.column = "${id}";
								o.collectionsDataArrayObject.${id} = data;
								for (var i = 0; i < data.length; i ++) {
									if (data[i].id == value) {
										reData = data[i].name;
									}
								}
							<% }%>
							<% if( null != formatter) {%>
								reData = ${formatter}(row, cell, reData, columnDef, dataContext);
							<% }%>
								return reData? reData: "";
					}
				}
			<% }%>
		
		<% if( null != click) {%>
		c_${id}.click  = ${click};
		<% }%>
		<% if( null != dataType) {%>
		c_${id}.dataType  = "${dataType}";
		<% }%>
		<% if( null != align) {%>
		c_${id}.align  = "${align}";
		<% }%>
		<% if( null != dataAlign) {%>
		c_${id}.dataAlign  = "${dataAlign}";
		<% }%>
		<% if( null != showDetailed) {%>
		c_${id}.showDetailed  = ${showDetailed};
		<% }%>
		<% if( null != asKey) {%>
		c_${id}.propertyKey  = ${asKey};
		<% }%>
		<% if( null != totalsFormatter) {%>
		c_${id}.totalsFormatter  = ${totalsFormatter};
		<% }%>
		<% if( null != totalsAlign) {%>
		c_${id}.totalsAlign  = "${totalsAlign}";
		<% }%>
		<% if( null != totals) {%>
			o.groupingBy = "_onlyTotals";
			c_${id}.totals = "${totals}"; 
			c_${id}.groupTotalsFormatter  = function(totals, columnDef) {
					var text = "";
					<% if( null != tatalsTextShow) {%>
						var tatalsTextShow = ${tatalsTextShow};
					<% } else { %>
						var tatalsTextShow = true;
					<% }%>
					if (tatalsTextShow == true) {
						if ("${totals}" == "avg") { 
							text = "平均: ";
						}  else if ("${totals}" == "sum") { 
							text = "合计: ";
						}  else if ("${totals}" == "max") {
							text = "最大: ";
						}  else if ("${totals}" == "min")  { 
							text = "最小: ";
						}
					}
					return text + totals.${totals}[columnDef.field] ;
			};
		<% }%>
		<jsp:doBody/>
		c.push(c_${id});
		<%}%>
	<%}
	
%>

