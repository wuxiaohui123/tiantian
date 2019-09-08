<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>

<%@tag description='列表ListView' display-name='listView'%>
<%@attribute name="id" type="java.lang.String" description="列表id" required="true"%>
<%@attribute name="width" type="java.lang.String" description="列表宽度，值可为 xx px,xx %， 默认为520px"%>
<%@attribute name="height" type="java.lang.String" description="列表高度，值可为 xx px,xx %， 默认为520px"%>
<%@attribute name="itemHeight" type="java.lang.String" description="列表数据行高度，值可为 xx px,xx %， 默认为35px"%>
<%@attribute name="hasSearch" type="java.lang.String" description="是否包含搜索框，值为 true/false，默认为 true"%>
<%@attribute name="isAutoSearch" type="java.lang.String" description="是否自动搜索，即但搜索输入框的字符有变化时自动筛选，建议数据量小于1000时开启，值为 true/false，默认为 true"%>
<%@attribute name="searchPlaceholder" type="java.lang.String" description="搜索框提示文字，默认为 ‘请输入关键字搜索’"%>
<%@attribute name="searchKey" type="java.lang.String" description="自动搜索字段，默认为 name，多个字段用逗号分隔，列如：id,name,icon"%>
<%@attribute name="isItemIcon" type="java.lang.String" description="每相数据是否包含图标，值为 true/false，默认为 true"%>
<%@attribute name="itemIcon" type="java.lang.String" description="设置全局图标ICON，默认为 icon" %>
<%@attribute name="displayName" type="java.lang.String" description="显示字段名称，默认为 name" %>
<%@attribute name="itemDisplayTemplate" type="java.lang.String" description="显示内容模板，默认为{name}，例如：{name}（{id}）" %>
<%@attribute name="itemClickBgColor" type="java.lang.String" description="单项数据点击时呈现的背景色，默认为 #6c95c9" %>
<%@attribute name="list" type="java.lang.String" description="json,手工传入列表数据，例如:[{'id':'xxx','name':'xxx','icon':'xx'},{},{},.....]"%>
<%@attribute name="isAsync" type="java.lang.String" description="是否异步获取数据，默认为 false"%>
<%@attribute name="asyncUrl" type="java.lang.String" description="异步获取数据地址，当isAsync为ture时生效，默认为 ''"%>
<%@attribute name="isPage" type="java.lang.String" description="是否分页，默认为 true"%>
<%@attribute name="pageSize" type="java.lang.String" description="分页大小，即一页显示数据条数，默认为 20"%>
<%@attribute name="showSummary" type="java.lang.String" description="是否显示分页摘要信息，默认为 true"%>
<%@attribute name="summaryTemplate" type="java.lang.String" description="分页摘要信息信息模板，即显示分页条数信息和页数信息，可以为‘Displaying {startRow}-{endRow} of {totalRow} results’，也可以为 ‘第{currentPage}页/共{totalPage}页’，默认为 ‘Displaying {startRow}-{endRow} of {totalRow} results’"%>
<%@attribute name="showEditBtn" type="java.lang.String" description="是否显示编辑按钮，默认为 false"%>
<%@attribute name="editUrl" type="java.lang.String" description="编辑提交地址，当showEditBtn为ture时生效，默认为 ''"%>
<%@attribute name="showDeleteBtn" type="java.lang.String" description="是否显示删除按钮，默认为 false"%>
<%@attribute name="deleteUrl" type="java.lang.String" description="删除提交地址，当showDeleteBtn为ture时生效，默认为 ''"%>
<%@attribute name="itemClick" type="java.lang.String" description="单击事件，传函数定义(不加括号)，默认传入参数该对象如itemClick='fnClick',再在javascript中定义函数function fnClick(obj)" %>
<%@attribute name="itemDbClick" type="java.lang.String" description="双击事件，传函数定义(不加括号)，默认传入参数该对象如itemDbClick='fnDbClick',再在javascript中定义函数function fnDbClick(obj)" %>
<%
	String data = "";
	if (list == null) {
		ResultBean resultBean = (ResultBean) TagUtil.getResultBean();
		if (resultBean != null) {
			Object val = resultBean.getFieldData() == null? null : resultBean.getFieldData().get(id);
			// 屏蔽原始字符串
			if (val != null) {
				data= val.toString();
			}
		}
	}
%>
<div id="${id}_listview"></div>

<script type="text/javascript">
(function(factory){
	if (typeof require === 'function') {
		require(["jquery", "TaUIManager", "listView","api.listview"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	Ta.core.TaUICreater.addUI(function(){
		var o = {};
		<%if (width != null && !"".equals(width)) { %>
			 o.width = "${width}";
		<%}%>
		<%if (height != null && !"".equals(height)) {%>
		 	 o.height = "${height}";
		<%}%>
		<%if (itemHeight != null && !"".equals(itemHeight)) {%>
		     o.itemHeight ="${itemHeight}";
        <%}%>
	   <%if (hasSearch != null && !"".equals(hasSearch)) {%>
			o.hasSearch =${hasSearch};
	   <%}%>
	   <%if (isAutoSearch != null && !"".equals(isAutoSearch)) {%>
			o.isAutoSearch =${isAutoSearch};
	   <%}%>
	   <%if (searchKey != null && !"".equals(searchKey)) {%>
		o.searchKey = "${searchKey}";
	   <%}%>
	   <%if (isItemIcon != null && !"".equals(isItemIcon)) {%>
			o.isItemIcon =${isItemIcon};
	   <%}%>
	   <%if (itemIcon != null && !"".equals(itemIcon)) {%>
		    o.itemIcon = "${itemIcon}";
	   <%}%>
	   <%if (displayName != null && !"".equals(displayName)) {%>
		o.itemDisplayName ="${displayName}";
       <%}%>
       <%if (itemDisplayTemplate != null && !"".equals(itemDisplayTemplate)) {%>
	      o.itemDisplayTemplate ="${itemDisplayTemplate}";
       <%}%>
	   <%if (itemClickBgColor != null && !"".equals(itemClickBgColor)) {%>
	   		o.itemClickBgColor = "${itemClickBgColor}";
	   <%}%>
	   <%if (isAsync != null && !"".equals(isAsync)) {%>
		    o.isAsync =${isAsync};
       <%}%>
       <%if (asyncUrl != null && !"".equals(asyncUrl)) {%>
   		    o.asyncUrl = "${asyncUrl}";
	   <%}%>
	   var page = {};
	   <%if (isPage != null && !"".equals(isPage)) {%>
	        page.enable =${isPage};
       <%}%>
       <%if (pageSize != null && !"".equals(pageSize)) {%>
            page.size =${pageSize};
       <%}%>
       <%if (showSummary != null && !"".equals(showSummary)) {%>
            page.showSummary =${showSummary};
       <%}%>
       <%if (summaryTemplate != null && !"".equals(summaryTemplate)) {%>
            page.summaryTemplate = "${summaryTemplate}";
       <%}%>
       o.paging = page;
       var itemOperationObj = {};
       <%if (showEditBtn != null && !"".equals(showEditBtn)) {%>
            itemOperationObj.showEditBtn =${showEditBtn};
       <%}%>
       <%if (showDeleteBtn != null && !"".equals(showDeleteBtn)) {%>
            itemOperationObj.showDeleteBtn =${showDeleteBtn};
       <%}%>
       <%if (editUrl != null && !"".equals(editUrl)) {%>
             itemOperationObj.editUrl = "${editUrl}";
       <%}%>
       <%if (deleteUrl != null && !"".equals(deleteUrl)) {%>
             itemOperationObj.deleteUrl = "${deleteUrl}";
       <%}%>
       
       o.itemOperation = itemOperationObj;
	   <%if (itemClick != null && !"".equals(itemClick)) {%>
			o.itemClick = ${itemClick};
	   <%}%>
	   <%if (itemDbClick != null && !"".equals(itemDbClick)) {%>
			o.itemDbClick = ${itemDbClick};
	   <%}%>
	   <%if (list != null && !"".equals(list)) {%>
			o.list = ${list};
	   <%} else {%>
			o.list = <%=data %>
	   <%} %>
	   $("#${id}_listview").ListView(o); 
	});
}));
</script>