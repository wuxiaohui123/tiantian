<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.persistence.PageBean"%>
<%@tag import="java.util.Map"%>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="java.util.Enumeration"%>

<%--@doc--%>
<%@tag description='datagrid组件' display-name='datagrid' %>
<%@attribute description='表格id，页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='行高,设置每条datagridItem的高度，例如:rowHeight="30"' name='rowHeight' type='java.lang.String' %>
<%@attribute description='默认列宽度,默认80' name='defaultColumnWidth' type='java.lang.String' %>
<%@attribute description='true/false,设置是否显示列过滤条，默认false，不显示' name='columnFilter' type='java.lang.String' %>
<%@attribute description='true/false:设置表格自动充满父容器，一般来说都设置true' name='fit' type='java.lang.String' %>
<%@attribute description='表格行单击事件，填写函数定义不加括号，默认传参event,data, 分别是事件，整行数据(data)，数据包含行号row，列号cell，以及当前表格对象grid，及当前行数据。如onRowClick=’clickfn‘ 然后在javascript代码中写function clickfn(e,rowdata){var row = rowdata.row,cell = rowdata.cell,aac003 = rowdata.aac003,grid = rowdata.grid}' name='onRowClick' type='java.lang.String' %>
<%@attribute description='表格行双击事件，填写函数定义不加括号,默认传参event,data, 分别是事件，整行数据(data)，数据包含行号row，列号cell，以及当前表格对象grid，及当前行数据。如onRowDBClick=‘clickfn’ 然后在javascript代码中写function clickfn(e,rowdata){var row = rowdata.row,cell = rowdata.cell,aac003 = rowdata.aac003,grid = rowdata.grid}' name='onRowDBClick' type='java.lang.String' %>
<%@attribute description='直接指定表格数据格式为json格式对象组，例如data="[{‘a’:‘1111’,‘b’:’32‘},{’a‘:’3234‘,’b‘:’311‘}]"' name='data' type='java.lang.String' %>
<%@attribute description='布局底部补足,例如:heightDiff="100"' name='heightDiff' type='java.lang.String' %>
<%@attribute description='true/false，设置表格列宽是否等长且占据整个表宽度' name='forceFitColumns' type='java.lang.String' %>
<%@attribute description='checkbox/radio，设置表格的选择方式' name='selectType' type='java.lang.String' %>
<%@attribute description='设置表格的选中改变事件，默认传参rowsData，n 分别是所有选择数据(数组)，选择的行数.此事件在你点击选择行的时候发生。例如:onSelectChange="fnSelectChange",然后在javascript中，function fnSelectChange(rowsData,n){alert(n)}' name='onSelectChange' type='java.lang.String' %>
<%@attribute description='是否显示行序列号，默认false不显示' name='haveSn' type='java.lang.String' %>
<%@attribute description='true/false，是否双击修改，默认false双击不修改，配合datagridEditor使用' name='dblClickEdit' type='java.lang.String' %>
<%@attribute description='true/false，是否允许列拖动,默认为false，不允许拖动' name='enableColumnMove' type='java.lang.String' %>
<%@attribute description='高度' name='height' type='java.lang.String' %>
<%@attribute description='通过id进行分组,此时该datagrid必须设置haveSn属性，否则groupingBy无效' name='groupingBy' type='java.lang.String' %>
<%@attribute description='true/false，是否后台转码，默认false。当设置为true且在列上定义了collection时，传送过来的数据就已经是通过转码后的值，不再是码值。例如:当有性别字段时，此属性设置为true且collection也进行了相应的设置，那么在获取性别字段时就已经是男或者女，而不再是0或者1' name='serverCvtCode' type='java.lang.String' %>
<%@attribute description='点击行选择时回调函数，参数为整行数据,及事件' name='onRowSelect' type='java.lang.String' %>
<%@attribute description='行渲染的回调函数填写函数定义（不加括号），参数为整行数据，返回值为color值如#FFFFFF或者red，如果返回值为false不处理，例如rowColorfn="fnColor"在javascript中定义函数function fnColor(data){if (data.id == 1) return ‘#ffffff’;}' name='rowColorfn' type='java.lang.String' %>
<%@attribute description='点击表格checkbox调用事件传入rowdata,表示整行数据' name='onChecked' type='java.lang.String' %>
<%@attribute description='设置序号宽度' name='snWidth' type='java.lang.String' %>
<%@attribute description='复杂表头，传入Table的 id' name='htmlHeadId' type='java.lang.String' %>
<%@attribute description='默认选择行,传入一个json对象或者function,例如:defaultRows="[{’aac004’:‘1’,‘aac005’:‘2’}]"或者defaultRows="getDefaultRows",然后在js里,function getDefaultRows(){return [{‘aac001‘:’1’}]}' name='defaultRows' type='java.lang.String' %>
<%@attribute description='true/false,默认true，单双击后是否添加单元格样式，false表示不添加' name='clickActiveStyle' type='java.lang.String' %>
<%@attribute description='是否显示边框，默认false' name='border' type='java.lang.String' %>
<%@attribute description='mediaColumn,是否使用easyUI表格的行展开功能' name='mediaColumn' type='java.lang.String' %>
<%@attribute description='点击加号时回掉函数，传入整合数据' name='onMediaColumnExpand' type='java.lang.String' %>
<%@attribute description='表头所占行数,例如headerColumnsRows="2"' name='headerColumnsRows' type='java.lang.String' %>
<%@attribute description='表格宽度' name='width' type='java.lang.String' %>
<%@attribute description='设置columnwidth' name='columnWidth' type='java.lang.String' %>
<%@attribute description='设置span' name='span' type='java.lang.String' %>
<%@attribute description='设置mediaData' name='mediaData' type='java.lang.String' %>
<%--@doc--%>

<%-- media --%>
<%
	if (mediaColumn != null){
%>
<%@include file="../columnhead.tag" %>
<table id="${id}" singleSelect="false"  fit="true" remoteSort="false"
    <%if (null != haveSn) { %>
		data-options="rownumbers:true,${mediaData}"
	<%} %>
>
<thead>  
<tr>  
	<%if ("checkbox".equals(selectType)){%>
<th data-options="field:'ck',checkbox:true"></th>
<%} %>		
<jsp:doBody/>
</tr>  
</thead>  
</table>  
<%@include file="../columnfoot.tag" %>
<script type="text/javascript">  
$(function(){  
   $('#${id}').datagrid({
       //view: detailview,
      detailFormatter:function(index,row){
          return '<div id="${id}-ddv-' + index + '" style="width:100%;overflow:hidden;background-color:#eee;padding:5px 0"></div>';  
      },
      onBeforeLoading : function(index, row){
          $("#${id}_mediaColumn").hide().appendTo($("body"));
      },
      onExpandRow: function(index,row){
          var $_${id} = $('#${id}');
          $(".datagrid-row-collapse").each(function(){
            		//$(this).trigger('click',this);
          });
          var $_${id}_mediaColumn = $("#${id}_mediaColumn");
          $_${id}_mediaColumn.show().appendTo($('#${id}-ddv-'+index).empty())
	      $_${id}.datagrid('fixDetailRowHeight',index);  
          $_${id}.datagrid('unselectAll');
          $_${id}.datagrid('selectRow',index);
          <%if (null != onMediaColumnExpand) {%>
		       ${onMediaColumnExpand}(row);
		  <%}%>
      },
      onCollapseRow :function(index,row){
           //$("#${id}_mediaColumn").hide().appendTo($("body"));
      }
   });  
});  
</script>
<%  } else {%>
<%
	String cssStyle = "";
	String cssClass = "";
	String serverCutCode = serverCvtCode;
	if(width != null){
		if(cssStyle != null){
			cssStyle += ";"+(width.endsWith("px")?("width:"+width):(width.endsWith("%")?("width:"+width):("width:"+width+"px")));
		}else{
			cssStyle = (width.endsWith("px")?("width:"+width):(width.endsWith("%")?("width:"+width):("width:"+width+"px")));
		}
	}
	
	if(height != null){
		if(cssStyle != null){
			cssStyle += ";"+(height.endsWith("px")?("height:"+height):(height.endsWith("%")?("height:"+height):("height:"+height+"px")));
		}else{
			cssStyle = (height.endsWith("px")?("height:"+height):(height.endsWith("%")?("height:"+height):("height:"+height+"px")));
		}
	}else if(height==null && fit==null){
		if(cssStyle != null){
			cssStyle += ";height:150px";
		}else{
			cssStyle = "height:150px";
		}
	}
	if (fit != null) {
		if (cssStyle != null)
			cssStyle += "height:100%;";
		else 
			cssStyle = "height:100%;";
	}
	
	//优先 查找resultBean
	ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
	if(resultBean != null){
		Map<String, PageBean> map = resultBean.getLists();
		if(map !=null){
			com.yinhai.sysframework.persistence.PageBean pageBean = map.get(this.id);
			data = JSonFactory.bean2json(pageBean);
		}
	}
	//查找request
	if((data == null || "".equals(data)) && request.getAttribute(this.id) != null){
		Object obj = request.getAttribute(this.id);
		if(obj instanceof String || obj instanceof StringBuffer || obj instanceof StringBuilder){
			data = obj.toString();
		}else{
			data =  JSonFactory.bean2json(obj);
		}
	}
	//查找session
	if((data == null || !"".equals(data)) && request.getSession().getAttribute(this.id) != null){
		Object obj = request.getSession().getAttribute(this.id);
		if(obj instanceof String || obj instanceof StringBuffer || obj instanceof StringBuilder){
			data = obj.toString();
		}else{
			data =  JSonFactory.bean2json(obj);
		}
	}        	
    if(data != null && !"".equals(data)){
    	jspContext.setAttribute("data", data);
    } else {
    	jspContext.setAttribute("data", "{}");
    }
	
	if ((this.id == null || this.id.length() == 0)) {

		int nextInt = new Random().nextInt();
		nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
				.abs(nextInt);
		this.id = "tadatagrid_" + String.valueOf(nextInt);
		jspContext.setAttribute("id", this.id);
	}
	jspContext.setAttribute("_grid_", id, PageContext.REQUEST_SCOPE);
	
	jspContext.setAttribute("_grid_obj", this, PageContext.REQUEST_SCOPE);
%>

<%@include file="../columnhead.tag" %>

<div id="${id}" 
<%if (cssStyle != null) {%>
   style="<%=cssStyle%>"
<%} %>
<%if (cssClass != null) {%>
    class="datagrid ta-datagrid slick-grid-container ui-widget ${cssClass}"
<%} else {%>
    class="datagrid ta-datagrid slick-grid-container ui-widget"
<%} %>
<%if (columnWidth != null) {%>
    columnWidth="columnWidth"	 
<%} %>
<% if(null != span){%>
    span="${span}"	 
<%} %>
<% if(null != fit){%>
    fit="${fit}"	 
<%} %>
<% if(null != heightDiff){%>
    heightDiff="${heightDiff}"	 
<%} %>
></div>
<script type="text/javascript">	
(function(factory){
	if (typeof require === 'function') {
		//_allR.push("grid.core");
		require(["jquery", "TaUIManager", "grid.core"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
Ta.core.TaUICreater.addUI(function(){
	var grid_${id};
	var c = [];
	var o = {};
	var h = [];
	o.collectionsDataArrayObject = {};
	var serverCvtCode=false;
	var serverCvtCode_column=false;
	<% if(null !=border){%>
		o.border="${border}";	 
	<%} %>
	<% if(null !=serverCvtCode){%>
		serverCvtCode=${serverCvtCode};	 
	<%} %>
	<jsp:doBody/>
	<% if (null != rowHeight) {%>
		o.rowHeight = ${rowHeight};
	<% }%>
	<% if (null != defaultColumnWidth) {%>
		o.defaultColumnWidth = ${defaultColumnWidth};
	<% }%>
	<% if (null != dblClickEdit) {%>
		o.autoEdit = !${dblClickEdit};
	<% }%>
	<% if (null != htmlHeadId) {%>
		o.htmlHeadId = "${htmlHeadId}";
	<% }%>
	<% if (null != enableColumnMove) {%>
		o.enableColumnReorder = ${enableColumnMove};
	<% }%>
	<% if (null != rowColorfn) {%>
		o.rowColorfn = ${rowColorfn};
	<% }%>
	<% if (null != columnFilter) {%>
		o.columnFilter = ${columnFilter};
		o.showHeaderRow = true;
	<% }%>
	<% if (null != forceFitColumns) {%>
		o.forceFitColumns = "${forceFitColumns}";
	<% }%>
	<% if (null != selectType) {%>
		o.selectType = "${selectType}";
	<% }%>
	<% if (null != haveSn) {%>
		o.haveSn = "${haveSn}";
	<% }%>
	<% if (null != serverCutCode) {%>
		o.serverCutCode = ${serverCutCode};	 
	<% }%>
	<% if (null != onSelectChange) {%>
		o.onSelectChange = ${onSelectChange};
	<% }%>
	<% if (null != groupingBy) {%>
		o.groupingBy = "${groupingBy}";
	<% }%>
	<% if (null != onChecked) {%>
		o.onChecked = ${onChecked};
	<% }%>
	<% if (null != onRowSelect) {%>
		o.onRowSelect = ${onRowSelect};
	<% }%>
	<% if (null != snWidth) {%>
		o.snWidth = ${snWidth};
	<% }%>
	<% if (null != defaultRows) {%>
		o.defaultRows = ${defaultRows};
	<% }%>
	<% if (null != headerColumnsRows) {%>
		o.headerColumnsRows = ${headerColumnsRows};
	<% }%>
	<% if (null != clickActiveStyle) {%>
		o.clickActiveStyle = ${clickActiveStyle};
	<% }%>
	   var data = ${data};
	   grid_${id} = new Slick.Grid("#${id}", data, c, o);
	<% if (null != onRowClick) {%>
	   grid_${id}.onClick.subscribe(${onRowClick});
	<% }%>
	<% if (null != onRowDBClick) {%>
	   grid_${id}.onDblClick.subscribe(${onRowDBClick});
	<% }%>
	grid_${id}.setColumnHidden(h);
	Ta.core.TaUIManager.register("${id}",grid_${id});
   });
}));
</script>
<%@include file="../columnfoot.tag" %>
<%}%>