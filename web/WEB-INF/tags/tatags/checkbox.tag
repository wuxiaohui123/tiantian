<%@tag pageEncoding="UTF-8" %>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%@tag import="java.util.Random" %>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean" %>
<%@tag import="com.yinhai.sysframework.util.TagUtil" %>
<%--@doc--%>
<%@tag description='checkbox组件' display-name="checkbox"%>
<%@attribute description='true/false,默认false，设置是否不可用' name='disabled' type='java.lang.String' %>
<%@attribute description='true/false,默认true，设置是否显示' name='display' type='java.lang.String' %>
<%@attribute description='设置组件style样式' name='cssStyle' type='java.lang.String' %>
<%@attribute description='设置组件css样式' name='cssClass' type='java.lang.String' %>
<%@attribute description='true/false,默认false，设置是否为只读' name='readOnly' type='java.lang.String' %>
<%@attribute description='设置组件id，页面唯一' name='id' type='java.lang.String' %>
<%@attribute description='设置标题，不支持html格式文本' name='key' required='true' type='java.lang.String'  rtexprvalue="true"%>
<%@attribute description='单击事件,比如onClick="fnOnClick()",在javascript中，function fnOnClick(){alert(111)}' name='onClick' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='true/false,设置是否被选中，默认为false' name='checked' type='java.lang.String' %>
<%@attribute description='设置label的样式，例如:labelStyle="font-size:16px"' name='labelStyle' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String' %>
<%@attribute description='设置是否必选' name='required' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%--@doc--%>
<%
			
			String columnWidth = null;
			ResultBean resultBean = (ResultBean) TagUtil.getResultBean();
			//查找CheckboxGroup是否有值，如果有值，查看当前checkbox的值是否在里面
			Object obj = jspContext.getAttribute("_checkboxgroup_obj",PageContext.REQUEST_SCOPE);
			JspTag taCheckboxGroup = TagUtil.getTa3ParentTag(getParent());
			
			String checkboxgroupid = null;
			if(taCheckboxGroup != null && !"".equals(taCheckboxGroup) && taCheckboxGroup.equals(obj)){
				checkboxgroupid = (String)jspContext.getAttribute("_checkboxgroup_id",PageContext.REQUEST_SCOPE);
				Object cgValue = null;
				if(resultBean != null){
			    	Object v = resultBean.getFieldData()==null?null:resultBean.getFieldData().get(checkboxgroupid);
			    	if(v !=null && !"".equals(v)){
			    		cgValue = v;
			    	}
				}
			    //查找request
			    //if(value != null && !"".equals(value) && checkboxgroupid != null){
			    //	cgValue = checkboxgroupid;
			    //}
			    //查找session
			    if(value != null && !"".equals(value) && request.getSession().getAttribute("_checkboxgroup_id") != null){
			    	cgValue = request.getSession().getAttribute("_checkboxgroup_id");
			    }
			    if(cgValue!=null){
			    	if(cgValue instanceof String){
			    		if(((String)cgValue).indexOf(value==null?" ":value)>0){
			    			checked = "checked";
			    		}
			    	}else if(cgValue instanceof String[]){
			    		String[] tmp = (String[])cgValue;
			    		
			    		for(int i=0;i<tmp.length;i++){
			    			if(tmp[i]==value){
			    				checked = "checked";
			    				break;
			    			}
			    		}
			    	}
			    }
			}    
			//如果是直接给当前checkbox的id赋值的情况下
	    	//优先 查找resultBean
			Boolean _value = false;
	    	if(id != null){
		    	if(resultBean != null){
		    		Object v =  resultBean.getFieldData()==null?null:resultBean.getFieldData().get(id);
		    		if(v !=null && !"".equals(v) && (v.toString()).equals(value)){
		    			_value = true;
		    		}
		    	}
		    	//查找request
		    	if(value != null && !"".equals(value) && request.getAttribute(id) != null){
		    		_value = (request.getAttribute(id)==null?false:true);
		    	}
		    	//查找session
		    	if(value != null && !"".equals(value) && request.getSession().getAttribute(id) != null){
		    		_value = (request.getSession().getAttribute(id)==null?false:true);
		    	}
		    	//如果resultBean，request，session设置了有该id的值，该组件就会被勾选
		        if(_value){
		        	checked = "checked";
		        }
	    	}
	    	
			if ((id == null || id.length() == 0)) {
				Random RANDOM = new Random();
				int nextInt = RANDOM.nextInt();
				nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
						.abs(nextInt);
				id = "tacheckbox_" + String.valueOf(nextInt);
			}
			if ("false".equals(display) || "none".equals(display)) {
				if (cssStyle == null) {
					cssStyle = "display:none;";
				} else {
					cssStyle += ";display:none;";
				}
			}
			
			if (name == null || "".equals(name)) {
				if (checkboxgroupid == null){
					name = "dto['" + id + "']";
				}else{
					name = "dto['" + checkboxgroupid + "']";
				}	
			}
			
%>

<%@include file="../columnhead.tag" %>
<div id="<%=id %>_chkboxDiv"
<% if( cssStyle != null ){ %>
style="white-space:nowrap;<%=cssStyle%>" 
<%}else{ %>
style="white-space:nowrap;" 
<%}%>
<% if( span != null ){ %>
span="${span}" 
<%}%>
<% if( onClick != null ){ %>
_onClick="${onClick}" 
<%}%>	
<% if( toolTip != null ){ %>
title="${toolTip}" 
<%}%>
<% if( cssClass != null ){ %>
class="fielddiv ta_pw_chkbox ta-chk-uncheck ${cssClass}" 
<%}else{%>
class="fielddiv ta_pw_chkbox ta-chk-uncheck"	
<%} %>
>
<input 
id="<%=id %>" 
type="checkbox" 
	<% if( value != null ){ %>
value="${value }" 
	<%}%>
name="<%=name %>" 
	<% if( "true".equals(checked) || "checked".equals(checked)){ %>
checked="<%=checked %>" 
	<%}%>
	<% if( readOnly != null ){ %>
readOnly="${readOnly}" 
	<%}%>
	<% if( disabled != null ){ %>
disabled="${disabled}" 
	<%}%>
style="display:none;"					
/> 
<label 
	<% if( labelStyle != null ){ %>
style="${labelStyle}" 
	<%}%>
class="ta-chk-mc"		
>
	<% if( key != null ){ %>
${key}
	<%}%>
</label>
</div>
<%@include file="../columnfoot.tag" %>