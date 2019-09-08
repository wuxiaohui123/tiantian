<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.config.SysConfig"%>
<%@tag import="java.util.Locale"%>
<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="java.util.List"%>
<%@tag import="com.yinhai.sysframework.codetable.domain.AppCode"%>
<%@tag import="java.util.Iterator"%>
<%@tag import="com.yinhai.webframework.session.UserSession"%>
<%@tag import="com.yinhai.sysframework.codetable.service.CodeTableLocator"%>
<%@tag import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%--@doc--%>
<%@tag description='单元格编辑器' display-name='datagridEditor'%>
<%@attribute description='设置编辑器类型,selectInput,text,date,dateTime,issue,number,bool,textArea。当设置成selectInput时，需要再设置collection属性' name='type'
	type='java.lang.String'%>
<%@attribute description='编辑器转码类型' name='collection' type='java.lang.String'%>
<%@attribute description='true/false,默认false。当type为date,dateTime,issue时，可设置是否弹出日期面板' name='showSelectPanel' type='java.lang.String'%>
<%@attribute description='编辑器自定义初始化数据,例如:data="[{’id‘:1,‘name’:‘aa’,’py‘:1},{’id‘:2,‘name’:‘bb’,‘py’:2}]"' name='data' type='java.lang.String'%>
<%@attribute description='editorData,编辑框中为下拉框时，为下拉框数据，参见下拉框data' name='editorData' type='java.lang.String'%>
<%@attribute description='max，最大值,当你输入的大于此值时，自动变为此最大值' name='max' type='java.lang.String'%>
<%@attribute description='min，最小值，当你输入的小于此值时，自动变为此最小值' name='min' type='java.lang.String'%>
<%@attribute description='precition，小数点位数，允许输入的小数位数' name='precition' type='java.lang.String'%>
<%@attribute
	description='change事件,默认传入参数data，value,分别表示该行数据，修改后的值.例如:onChange="fnChange",在javascript中，function fnChange(data,value) {var aac003 = data.aac003;alert(value)}'
	name='onChange' type='java.lang.String'%>
<%@attribute description='键盘事件,默认传入参数e,事件。例如:onKeydown="fnOnKeydown",在javascript中，function fnOnKeydown(e){alert(e.keyCode)}' name='onKeydown'
	type='java.lang.String'%>
<%@attribute description='键盘事件,默认传入参数e,事件。例如:onFocus="fnonFocus",在javascript中，function fnonFocus(e){}' name='onFocus' type='java.lang.String'%>
<%@attribute description='键盘事件,默认传入参数e,事件。例如:onKeyup="fnonKeyup",在javascript中，function fnonKeyup(e){}' name='onKeyup' type='java.lang.String'%>
<%@attribute description='true/false,默认false，获取选中行时，是否获取该列数据' name='gridItemfn' type='java.lang.String'%>
<%@attribute description='true/false,默认false，获取选中行时，是否获取该列数据' name='gridDatafn' type='java.lang.String'%>
<%@attribute description='true/false,默认false，获取选中行时，是否获取该列数据' name='gridOptionfn' type='java.lang.String'%>
<%@attribute description='验证类型，如:url，email，ip，integer，idcard，mobile，chinese，zipcode' name='validType' type='java.lang.String'%>
<%@attribute
	description='自定义验证,validType属性必须设置成self才会生效,validFunction为回调函数，例如validFunction=“fnSelfValidate”，在js中必须返回一个形如：{"message":"只能为数字","result":false}的json对象，message表示验证失败提示信息，result表示是否通过验证'
	name='validFunction' type='java.lang.String'%>
<%@attribute description='true/false,设置是否必输，默认false' name='required' type='java.lang.String'%>
<%@attribute description='是否启用延迟加载,true/false.默认为false' name='lazy' type='java.lang.String'%>

<%--@doc--%>

<%
	try {
		PropertyDescriptor pd = new PropertyDescriptor("id", getParent().getClass());
		String gridItem = (String) pd.getReadMethod().invoke(getParent());
		if (gridItem != null) {
			jspContext.setAttribute("gridItem", gridItem);
		}
	} catch (Exception e) {
		jspContext.setAttribute("gridItem", "");
	}
	boolean flagConfig = SysConfig.getSysconfigToBoolean("neworold");
	boolean lazyBoolean = false;
	if (lazy != null && !"".equals(lazy)) {
		lazyBoolean = Boolean.parseBoolean(lazy);
	}
	boolean flag = flagConfig && lazyBoolean;
%>

<%
	if (flag) {
%>
<%-- 使用新版的localStorage缓存 --%>
<%
	if (null != type) {
%>
var data; var collection = "";
<%
	if (this.data == null && collection != null && !"".equals(collection)) {
				collection = collection.toUpperCase(Locale.ENGLISH);
%>
collection = "<%=collection%>"; data = eval(localStorage.getItem(collection));
<%
	}
%>

<%
	if (data != null) {
%>
data =
<%=data%>;
<%
	}
%>
c_${gridItem}.editor = ${type}; o.editable = true;
<%
	if (null != collection) {
%>
var data_${gridItem} = data; c_${gridItem}.editordata = data_${gridItem}; c_${gridItem}.formatter = SelectInputFormatter; c_${gridItem}.collection =
null;
<%
	}
%>
<%
	if (null != data) {
%>
var data_${gridItem} = data; c_${gridItem}.editordata = data_${gridItem}; c_${gridItem}.formatter = SelectInputFormatter; c_${gridItem}.collection =
null;
<%
	}
%>

<%
	if (null != gridDatafn) {
%>
c_${gridItem}.gridDatafn = ${gridDatafn};
<%
	}
%>
<%
	if (null != gridItemfn) {
%>
c_${gridItem}.gridItemfn = ${gridItemfn};
<%
	}
%>

<%
	if (null != gridOptionfn) {
%>
c_${gridItem}.gridOptionfn = ${gridOptionfn};
<%
	}
%>

<%
	if (null != onChange) {
%>
c_${gridItem}.onChange = ${onChange};
<%
	}
%>
<%
	if (null != onKeydown) {
%>
c_${gridItem}.onKeydown = ${onKeydown};
<%
	}
%>
<%
	if (null != onKeyup) {
%>
c_${gridItem}.onKeyup = ${onKeyup};
<%
	}
%>
<%
	if (null != onFocus) {
%>
c_${gridItem}.onFocus = ${onFocus};
<%
	}
%>
<%
	if (null != max) {
%>
c_${gridItem}.max = ${max};
<%
	}
%>
<%
	if (null != min) {
%>
c_${gridItem}.min = ${min};
<%
	}
%>
<%
	if (null != precition) {
%>
c_${gridItem}.precition = ${precition};
<%
	}
%>
<%
	if (null != validType) {
%>
c_${gridItem}.validType = "${validType}";
<%
	}
%>
<%
	if (null != validType) {
%>
c_${gridItem}.validType = "${validType}";
<%
	}
%>
<%
	if (null != validFunction) {
%>
c_${gridItem}.validFunction = "${validFunction}";
<%
	}
%>
<%
	if (null != required) {
%>
c_${gridItem}.required = "${required}";
<%
	}
%>
<%
	if (null != showSelectPanel) {
%>
c_${gridItem}.showSelectPanel = ${showSelectPanel};
<%
	}
%>
<%
	}
%>

<%
	} else {
%>

<%--使用老版的缓存,不使用localStorage --%>
<%
	if (this.data == null && collection != null && !"".equals(collection)) {
			String orgId = null;

			UserSession userSession = UserSession.getUserSession(request);
			if (userSession != null && userSession.getUser() != null)
				orgId = userSession.getUser().getOrgId();

			List<AppCode> codeList = CodeTableLocator.getInstance().getCodeList(collection, orgId);
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Iterator<AppCode> i = codeList.iterator(); i.hasNext();) {
				AppCode app = i.next();
				sb.append("{\"id\":\"" + JSonFactory.getJson(app.getCodeValue()) + "\",");
				sb.append("\"name\":\"" + JSonFactory.getJson(app.getCodeDESC()) + "\",");
				sb.append("\"py\":\"" + JSonFactory.getJson(app.getPy()));
				if (i.hasNext()) {
					sb.append("\"},");
				} else
					sb.append("\"}");
			}
			sb.append("]");
			jspContext.setAttribute("data", sb.toString());
		}
		if (data != null) {
			jspContext.setAttribute("data", data);
		}
%>
<%
	if (null != type) {
%>
c_${gridItem}.editor = ${type}; o.editable = true;
<%
	if (null != collection) {
%>
var data_${gridItem} = ${data}; c_${gridItem}.editordata = data_${gridItem}; c_${gridItem}.formatter = SelectInputFormatter; c_${gridItem}.collection
= null;
<%
	}
%>
<%
	if (null != data) {
%>
var data_${gridItem} = ${data}; c_${gridItem}.editordata = data_${gridItem}; c_${gridItem}.formatter = SelectInputFormatter; c_${gridItem}.collection
= null;
<%
	}
%>

<%
	if (null != gridDatafn) {
%>
c_${gridItem}.gridDatafn = ${gridDatafn};
<%
	}
%>
<%
	if (null != gridItemfn) {
%>
c_${gridItem}.gridItemfn = ${gridItemfn};
<%
	}
%>

<%
	if (null != gridOptionfn) {
%>
c_${gridItem}.gridOptionfn = ${gridOptionfn};
<%
	}
%>

<%
	if (null != onChange) {
%>
c_${gridItem}.onChange = ${onChange};
<%
	}
%>
<%
	if (null != onKeydown) {
%>
c_${gridItem}.onKeydown = ${onKeydown};
<%
	}
%>
<%
	if (null != onKeyup) {
%>
c_${gridItem}.onKeyup = ${onKeyup};
<%
	}
%>
<%
	if (null != onFocus) {
%>
c_${gridItem}.onFocus = ${onFocus};
<%
	}
%>
<%
	if (null != max) {
%>
c_${gridItem}.max = ${max};
<%
	}
%>
<%
	if (null != min) {
%>
c_${gridItem}.min = ${min};
<%
	}
%>
<%
	if (null != precition) {
%>
c_${gridItem}.precition = ${precition};
<%
	}
%>
<%
	if (null != validType) {
%>
c_${gridItem}.validType = "${validType}";
<%
	}
%>
<%
	if (null != validFunction) {
%>
c_${gridItem}.validFunction = "${validFunction}";
<%
	}
%>
<%
	if (null != required) {
%>
c_${gridItem}.required = "${required}";
<%
	}
%>
<%
	if (null != showSelectPanel) {
%>
c_${gridItem}.showSelectPanel = ${showSelectPanel};
<%
	}
%>
<%
	}
%>
<%
	}
%>



