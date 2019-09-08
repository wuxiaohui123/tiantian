<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="javax.servlet.jsp.tagext.JspTag"%>
<%@tag import="java.util.Enumeration"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%--@doc--%>
<%@tag description='数字微调框' display-name='spinner' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"表示容器顶部向内占用10个像素' name='cssStyle' type='java.lang.String' %>
<%@attribute description='true/false,设置页面初始化的时候改组件不可用，同时表单提交时不会传值到后台，默认为false' name='disabled' type='java.lang.String' %>
<%@attribute description='label自定义样式' name='labelStyle' type='java.lang.String' %>
<%@attribute description='true/false,设置是否显示，默认为显示:true' name='display' type='java.lang.String' %>
<%@attribute description='组件id页面唯一' name='id' required='true' type='java.lang.String' %>
<%@attribute description='组件的name属性，一般可以不设置，系统会根据id自动生成类似dto["id"]这样的名称，如果你自己设置的了name属性，那么将以您设置的为准，如果你没有以dto方式设置，后台将不能通过dto来获取数据' name='name' type='java.lang.String' %>
<%@attribute description='true/false,设置是否必输，默认false，设置后代小红星' name='required' type='java.lang.String' %>
<%@attribute description='当该容器被父容器作为column方式布局的时候，设置span表明当前组件需要占用多少列，如span=‘2’表示跨两列' name='span' type='java.lang.String' %>
<%@attribute description='最大值' name='max' type='java.lang.String' %>
<%@attribute description='最小值' name='min' type='java.lang.String' %>
<%@attribute description='true/false,设置为只读，默认为true只读' name='readOnly' type='java.lang.String' %>
<%@attribute description='组件的label标签,不支持html标签' name='key' type='java.lang.String' %>
<%@attribute description='label占的宽度，如labelWidth="120"' name='labelWidth' type='java.lang.String' %>
<%@attribute description='初始化大小值' name='defValue' type='java.lang.String' %>
<%@attribute description='整个控件不可以使用' name='notUse' type='java.lang.String' %>
<%@attribute description='每次添加和减少的值' name='addValue' type='java.lang.String' %>
<%@attribute description='每次添加和减少的值value' name='value' type='java.lang.String' %>
<%@attribute description='columnWidth' name='columnWidth' type='java.lang.String' %>
<%@attribute description='validType' name='validType' type='java.lang.String' %>
<%@attribute description='readonly' name='readonly' type='java.lang.String' %>
<%--@doc--%>
<%
final Random RANDOM = new Random();
if (labelStyle != null) {
			String style = "";
			if (labelWidth != null) {
				style = "width:" + labelWidth + "px";
			}
			style = labelStyle + ";" + style;
			labelStyle = style;
			//addParameter("labelStyle", labelStyle);
			jspContext.setAttribute("labelStyle", labelStyle);
		} else {
			if (labelWidth != null) {
				//addParameter("labelStyle", "width:" + findString(labelWidth)+ "px");
				labelStyle="width:" + labelWidth+ "px";
				jspContext.setAttribute("labelStyle", "width:" + labelWidth+ "px");		
			}
		}

		if (null != cssClass) {
			cssClass = "fielddiv "+ cssClass;
			//addParameter("cssClass", cssClass);
			jspContext.setAttribute("cssClass", cssClass);		
		} else {
			//addParameter("cssClass", "fielddiv");
			cssClass="fielddiv";
			jspContext.setAttribute("cssClass", cssClass);	
			//System.out.println(jspContext.getAttribute("cssClass"));	
		}
		if ("false".equals(display) || "none".equals(display)) {
			if (cssStyle == null) {
				cssStyle = "display:none;";
			} else {
				cssStyle += ";display:none;";
			}
		}
		if (null != max && null != min) {
			//addParameter("validType", "number['" + findString(min) + "','"+ findString(max) + "']");
			validType="number['" + min + "','"+ max+ "']";
			jspContext.setAttribute("validType", validType);
		} else if (null == max && null != min) {
			//addParameter("validType", "number[" + findString(min) + ",'']");
			//addParameter("min", findString(min));
			jspContext.setAttribute("validType","number[" + min + ",'']");
			jspContext.setAttribute("min",min);
		} else if (null != max && null == min) {
			//addParameter("validType", "number[''," + findString(max) + "]");
			validType="number[''," + max + "]";
			jspContext.setAttribute("validType","number[''," + max + "]");
			//addParameter("max", findString(max));
			jspContext.setAttribute("max",max);
		} else {
			//addParameter("validType", "number");
			validType="number";
			jspContext.setAttribute("validType", "number");
		}
		if (null != defValue && null != min) {
			if (Integer.parseInt(defValue) < Integer
					.parseInt(min)) {
				//addParameter("defValue", findString(min));
				jspContext.setAttribute("defValue", min);
			} else {
				//addParameter("defValue", findString(defValue));
				jspContext.setAttribute("defValue", defValue);
			}

		}
		
		if ((id == null || id.length() == 0)) {

			int nextInt = RANDOM.nextInt();
			nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math
					.abs(nextInt);
			id = "taspinner_" + String.valueOf(nextInt);
			//addParameter("id", id);
			jspContext.setAttribute("id", id);
		}
		if (name == null || "".equals(name)) {
			name = "dto['" + id + "']";
			//addParameter("name", name);
			jspContext.setAttribute("name", name);
		}
		// 优先 查找resultBean
		ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
		if (resultBean != null) {
			Object v = resultBean.getFieldData() == null ? null : resultBean
					.getFieldData().get(id);
			if (v != null && !"".equals(v)) {
				value = v.toString();
			}
		}
		// 查找request
		if (value!= null && !"".equals(value)
				&& request.getAttribute(id) != null) {
			value = request.getAttribute(id).toString();
		}
		// 查找session
		if (value != null && !"".equals(value)
				&& request.getSession().getAttribute(id) != null) {
			value = request.getSession().getAttribute(id).toString();
		}
 %>


<%@include file="../columnhead.tag" %>
<div 
<%if(cssClass!=null){%>
class="${cssClass}" 
<%}%>
<%if(cssStyle!=null){%>
style="${cssStyle}" 
<%}%>
<%if(columnWidth!=null){%>
 columnWidth="${columnWidth}" 
<%}%>
<%if(span!=null){%>
span="${span}" 
<%}%>
> 
<%if(key!=null){%>
<label 
	 for="${id}" 
class="fieldLabel" 
	<%if(labelStyle!=null){%>
style="${labelStyle}" 
	<%}%>
> 
<%if(required!=null&& required.equals("true"))%>
<span style="color:red">*</span>
<%}if(key!=null){%>

${key} 
	</label> 
<%}%>
<div class="fielddiv2" 
	<%if(labelWidth!=null){%>
	style="margin-left:${labelWidth}px" 
	<%}%>
	id="${id}_divtop" 
> 

<script type="text/javascript">
Ta.core.TaUICreater.addUI( 
function(){
    var options = {
        <%if(max!=null){%>
       	maxValue: ${max},
       	<%}%>
       	<%if(min!=null){%>
       	minValue: ${min},
       	<%}%>
       	<%if(defValue!=null){%>
       	defValue: ${defValue},
       	<%}%>
       	<%if(addValue!=null){%>
		addValue: ${addValue},
		<%}%>
       	txtWidth:100,
		txtHeight:20,
		<%if(readonly!=null){%>
		readOnly : ${readonly},
		<%}%>
		txtId:"${id}",
		<%if(notUse!=null){%>
		notUse: "${notUse}",
		<%}%>
		txtName:"${name}"		
   	};
   var spinner = new taspinner($("#"+"${id}_divtop"),options);
   Ta.core.TaUIManager.register("${id}",spinner);
 }); 
</script>	
</div>
</div>
<%@include file="../columnfoot.tag" %>