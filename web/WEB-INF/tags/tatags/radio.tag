<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"  %>
<%@tag import="javax.servlet.jsp.tagext.JspTag" %>
<%@tag import="java.util.Random" %>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean" %>
<%@tag import="com.yinhai.sysframework.util.TagUtil" %>
<%--@doc--%>
<%@tag description='Radio' display-name='radio' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top：10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='设置组件id' name='id' type='java.lang.String' %>
<%@attribute description='组件的label标签' name='key' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='是否被选中' name='checked' type='java.lang.String' %>
<%@attribute description='是否可用' name='disabled' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='设置是否显示，默认为显示：true' name='display' type='java.lang.String' %>
<%@attribute description='onClick' name='onClick' type='java.lang.String' %>
<%@attribute description='设置是否必输' name='required' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列' name='span' type='java.lang.String' %>
<%@attribute description='true/false,设置radio只读' name='readonly' type='java.lang.String' %>
<%@attribute description='鼠标移过提示文本' name='toolTip' type='java.lang.String' %>
<%@attribute description='组件页面初始化的时候的默认值' name='value' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本' name='bpopTipMsg' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定宽度，默认自适应大小' name='bpopTipWidth' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的固定高度，默认自适应大小' name='bpopTipHeight' type='java.lang.String' %>
<%@attribute description='鼠标移过输入对象pop提示文本框的默认位置{top,left,right,bottom}，默认top' name='bpopTipPosition' type='java.lang.String' %>
<%@attribute description='' name='columnWidth' type='java.lang.String' %>
<%--@doc--%>
<%
		ResultBean resultBean = (ResultBean) TagUtil.getResultBean();
		//查找radioGroup是否有值，如果有值，查看当前radio是否等于当前radio的值
		Object obj = jspContext.getAttribute("_radiogroup_obj",PageContext.REQUEST_SCOPE);
		JspTag taRadioGroup = TagUtil.getTa3ParentTag(getParent());
		String radioGroupId = null;
		if(taRadioGroup != null && !"".equals(taRadioGroup) && taRadioGroup.equals(obj)){
			radioGroupId = (String)jspContext.getAttribute("_radiogroup_id",PageContext.REQUEST_SCOPE);
			String cgValue = null;
			if(resultBean != null){
				Object v = resultBean.getFieldData()==null?null:resultBean.getFieldData().get(radioGroupId);
	    		if(v !=null && !"".equals(v)){
	    			cgValue = v+"";
	    		}
			}
	    	//查找request
	    	//if(value != null && !"".equals(value) && radioGroupId != null){
	    	//	cgValue = radioGroupId;
	    	//}
	       	//查找session
	    	if(value != null && !"".equals(value) && request.getSession().getAttribute("_radiogroup_id") != null){
	    		cgValue = (String)request.getSession().getAttribute("_radiogroup_id");
	    	}
			if(cgValue!=null && cgValue.equals(this.value)){
				checked = "checked";
			}
		}	
			//如果是直接给当前radio的id赋值的情况下
	    	//优先 查找resultBean
			Boolean _value = false;
	    	if(this.id!=null){
	    		/**
	    		 * radiogroup的id存在，radio的id也存在；优先采用radiogroupId
	    		 */
			  if(resultBean != null){
			   		Object v = resultBean.getFieldData()==null?null:resultBean.getFieldData().get(radioGroupId);
			   		if(v ==null || "".equals(v)){
			   			v = resultBean.getFieldData()==null?null:resultBean.getFieldData().get(this.id);
			   		}
			   		
			   		if(v !=null && !"".equals(v)){
			   			v = v+"";
			   			if(v.equals(this.value)){
			   				_value = true;
			   			}
			   		}
				 }
				 //查找request
				 if(value != null && !"".equals(value) && request.getAttribute(this.id) != null){
				 	 String _v = request.getAttribute(this.id).toString();
				 	 if(_v.equals(this.value)) {
				 	 	_value = true;
				 	 }
				 }
				 //查找session
				 if(value != null && !"".equals(value) && request.getSession().getAttribute(this.id) != null){
				 	 String _v = request.getSession().getAttribute(this.id).toString();
				 	 if(_v.equals(this.value)) {
				 	 	_value = true;
				 	 }
				 }             	
	    	}
	    	//如果resultBean，request，session设置了有该id的值，改组件就会被勾选
	        if(_value){
	        	checked = "checked";
	        }
			
	        if ((this.id == null || this.id.length() == 0)) {
				Random RANDOM = new Random();
				int nextInt = RANDOM.nextInt();
				nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
						.abs(nextInt);
				id = "taradio_" + String.valueOf(nextInt);
			}   
			
	        if("false".equals(display) || "none".equals(display)){
	        	if(this.cssStyle ==null){
	        		this.cssStyle = "display:none;";
	        	}else{
	        		this.cssStyle += ";display:none;";
	        	}
	        }
	        
	        if(null == name){
	        	if(radioGroupId==null)
	        		name = "dto['"+id+"']";
	        	else
	        		name = "dto['"+radioGroupId+"']";
	        }   
    	   
%>

<%@include file="../columnhead.tag" %>
<div 
id="<%=id %>_radioDiv" 
	<% if( cssStyle != null ){ %>
style="white-space:nowrap;${cssStyle}" 
	<%}else{ %>
style="white-space:nowrap;" 
	<%}%>
	<% if( cssClass != null ){ %>
class="fielddiv ta_pw_radio ta-radio-uncheck ${cssClass}" 
	<%}else{ %>
class="fielddiv ta_pw_radio ta-radio-uncheck" 
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
>
<input 
	<% if( id != null ){ %>
id="<%=id%>" 
	<%}%>
	<% if( readonly != null && "true".equals(readonly)){%>
readOnly="${readonly}" 
	<%}%>
	<% if( disabled != null && "true".equals(disabled)){%> 
disabled="${disabled}" 
	<%}%>
type="radio" 
	<% if( value != null ){ %>
value="${value}" 
	<%}%>
name="<%=name %>" 
	<% if( "true".equals(checked) || "checked".equals(checked)){ %>
checked="<%=checked %>" 
	<%}%>
style="display:none;" 			
>
<label 
	<% if( labelStyle != null ){ %>
style="${labelStyle}" 
	<%}%>	
>
	<% if( key != null ){ %>
${key}
	<%}%>
</label>
</div>
<%@include file="../columnfoot.tag" %>