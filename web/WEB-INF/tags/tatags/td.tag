<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.util.ValidateUtil"%>
<%@tag import="org.apache.commons.lang3.StringUtils"%>
<%@tag import="java.util.Iterator"%>
<%@tag import="com.yinhai.sysframework.codetable.domain.AppCode"%>
<%@tag import="java.util.List"%>
<%@tag import="com.yinhai.webframework.session.UserSession"%>
<%@tag import="java.util.Map"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="com.yinhai.sysframework.codetable.service.CodeTableLocator"%>

<%--@doc--%>
<%@tag description='用于输出td标签' display-name='td' %>
<%@attribute description='组件id匹配field' name='id' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式class，如:cssClass="no-padding"' name='cssClass' type='java.lang.String' %>
<%@attribute description='给该组件添加自定义样式，如:cssStyle="padding-top:10px"' name='cssStyle' type='java.lang.String' %>
<%@attribute description='指定组件标签' name='key' type='java.lang.String' %>
<%@attribute description='定义单元格跨行数' name='rowspan' type='java.lang.String' %>
<%@attribute description='定义单元格跨列数' name='colspan' type='java.lang.String' %>
<%@attribute description='指定内容的垂直对齐方式' name='valign' type='java.lang.String' %>
<%@attribute description='指定内容对齐方式' name='align' type='java.lang.String' %>
<%@attribute description='指定td中显示的内容' name='content' type='java.lang.String' %>
<%@attribute description='指定码表转换' name='collection' type='java.lang.String' %>
<%@attribute description='指定td的宽度' name='width' type='java.lang.String' %>
<%@attribute description='指定td的高度' name='height' type='java.lang.String' %>
<%@attribute description='指定td显示的最大字符长度' name='maxLength' type='java.lang.String' %>
<%--@doc--%>
<%

		if ((this.id == null || this.id.length() == 0)) {
			jspContext.setAttribute("id", "");
			jspContext.setAttribute("content", "");
		}
		if (this.id != null && !this.id.equals("")) {
			jspContext.setAttribute("id", this.id);
			if (this.content != null && !this.content.equals("")) {
				jspContext.setAttribute("content", jspContext.getAttribute("content"));
				jspContext.setAttribute("fullContent",content);
		} else {
				ResultBean resultBean = (ResultBean)TagUtil.getResultBean();
				if (resultBean != null) {
					Map<String, Object> map = resultBean.getFieldData();
					if (map != null) {
						Object obj = map.get(this.id);
						if (obj != null) {
							if (collection != null && !"".equals(collection)) {
								String orgId = null;
								UserSession userSession = UserSession.getUserSession(request);
								if (userSession != null && userSession.getUser() != null)
									orgId = userSession.getUser().getOrgId();
								List<AppCode> codeList = CodeTableLocator.getInstance().getCodeList(collection, orgId);
								boolean  flage=false;
								String fullContent = "";
								for (Iterator<AppCode> i = codeList.iterator(); i.hasNext();) {
									AppCode app = i.next();
									if (app.getCodeValue().equals(obj.toString())) {
										fullContent=app.getCodeDESC();
										jspContext.setAttribute("content",getFinalString(fullContent));
										jspContext.setAttribute("fullContent",fullContent);
										flage=true;
									}
								}
								
								if(!flage){
									fullContent=obj.toString();
									jspContext.setAttribute("content", getFinalString(fullContent));
									jspContext.setAttribute("fullContent",fullContent);
								}
									

							} else {
								String fullContent=obj.toString();
								jspContext.setAttribute("content", getFinalString(fullContent));
								jspContext.setAttribute("fullContent", fullContent);
							}

						} else {
							jspContext.setAttribute("content", "");
						}
					}
				}
			}
			
			
		}
 %>
<%!
	public  String  getFinalString(String s){
	JspContext tdJspContext = getJspContext();
		if(this.maxLength != null && !this.maxLength.equals("")){
			if(StringUtils.isNumeric(maxLength)){
				Integer length= Integer.parseInt(maxLength);
				s=textCut(s,length,"...");
				tdJspContext.setAttribute("maxLength",tdJspContext.getAttribute("maxLength"));
			}
		}
		return s;
	}
	
	public  String textCut(String s, int len, String append) {
		if (s == null) {
			return null;
		}
		int slen = s.length();
		if (slen <= len) {
			return s;
		}
		// 最大计数（如果全是英文）
		int maxCount = len * 2;
		int count = 0;
		int i = 0;
		for (; count < maxCount && i < slen; i++) {
			if (s.codePointAt(i) < 256) {
				count++;
			} else {
				count += 2;
			}
		}
		if (i < slen) {
			if (count > maxCount) {
				i--;
			}
			if (!StringUtils.isBlank(append)) {
				if (s.codePointAt(i - 1) < 256) {
					i -= 2;
				} else {
					i--;
				}
				return s.substring(0, i) + append;
			} else {
				return s.substring(0, i);
			}
		} else {
			return s;
		}
	}
 %>
<td    
<% if( !ValidateUtil.isEmpty(align) ){%>
   align="${align}" 
<%}%>
<% if( !ValidateUtil.isEmpty(valign) ){%>
   valign="${valign}"
<%}%>
<% if(  !ValidateUtil.isEmpty(rowspan) ){%>
  rowspan="${rowspan}" 
<%}%>
<% if(  !ValidateUtil.isEmpty(colspan) ){%>
  colspan="${colspan}"  
<%}%>
 <% if( !ValidateUtil.isEmpty(cssStyle) ){%>
       style="${cssStyle}"  
  <%}%>
  <% if( !ValidateUtil.isEmpty(height) ){%>
             height="${height}" 
       <%}else{%>
             height="23" 
  <%}%>
<% if( !ValidateUtil.isEmpty(id) ){%>
       id="${id}" 
       <% if( !ValidateUtil.isEmpty(width) ){%>
            width="${width}" 
       <%}else{%>
             width="120" 
       <%}%>
    <%}else{%>
    <% if( !ValidateUtil.isEmpty(width) ){%>
            width="${width}" 
       <%}else{%>
             width="80" 
       <%}%>
 <%}%>
<% if( !ValidateUtil.isEmpty(id) ){%>
   <% if( !ValidateUtil.isEmpty(cssClass) ){%>
        class="table-td-cell ${cssClass}" 
      <%}else{%>
        class="table-td-cell"  
  <%}%>
 <%}else{%>
     <% if( !ValidateUtil.isEmpty(cssClass) ){%>
       class="table-td-cell table-td-cell-label   ${cssClass}" 
      <%}else{%>
       class="table-td-cell table-td-cell-label "  
    <%}%>
<%}%>
<% if(  !ValidateUtil.isEmpty(maxLength) ){%>
       maxLength="${maxLength}"  
     <% if( !ValidateUtil.isEmpty(jspContext.getAttribute("fullContent")) ){%>
        title="${fullContent}"  
     <%}%>
<%}%>
>
<jsp:doBody />
		<div style="clear:both"></div>
<% if( !ValidateUtil.isEmpty(id) ){%>
    <% if( !ValidateUtil.isEmpty(jspContext.getAttribute("content") ) ){%>
      ${content}  
    <%}%>
    <%}else{%>
     <% if( !ValidateUtil.isEmpty(key) ){%>
      ${key}
    <%}%>
<%}%>
</td>
