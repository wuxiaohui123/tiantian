package com.yinhai.sysframework.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagAdapter;

import com.yinhai.sysframework.app.domain.jsonmodel.IGetResultBean;
import com.yinhai.sysframework.app.domain.jsonmodel.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.util.Assert;

import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

public class TagUtil {

	static final String STACK = "_TA_STACK";

	public static ResultBean getResultBean(ValueStack stack) {
		Assert.notNull(stack, "stack参数为空");
		CompoundRoot root = stack.getRoot();
		for (Object object: root) {
			if (object instanceof IGetResultBean){
				return ((IGetResultBean) object).getResultBean();
			}
		}
		return (ResultBean) stack.findValue("resultBean");
	}

	public static ResultBean getResultBean() {
		HttpServletRequest request = ServletActionContext.getRequest();
		return (ResultBean) request.getAttribute(STACK);
	}

	public static JspTag getTa3ParentTag(JspTag tag) {
		if (tag instanceof SimpleTagSupport)
			return tag;
		if (tag instanceof TagAdapter)
			return ((TagAdapter) tag).getAdaptee();
		if (tag instanceof Tag) {
			JspTag jspTag = ((Tag) tag).getParent();
			if (jspTag != null)
				return getTa3ParentTag(jspTag);
			return tag;
		}
		return tag;
	}

	public static void columnlayout(JspTag parent, JspContext jspContext, String span, String columnWidth, String fit) {
		if (parent == null) {
			return;
		}
		String pdLayout = null;
		String pdCols = null;
		parent = getTa3ParentTag(parent);
		try {
			PropertyDescriptor pd1 = new PropertyDescriptor("layout", parent.getClass());
			Method method1 = pd1.getReadMethod();
			pdLayout = (String) method1.invoke(parent, new Object[0]);
			PropertyDescriptor pd2 = new PropertyDescriptor("cols", parent.getClass());
			Method method2 = pd2.getReadMethod();
			pdCols = (String) method2.invoke(parent, new Object[0]);
		} catch (Exception e) {
		}
		String layout = pdLayout;
		if (("column".equals(layout)) || (layout == null)) {
			Double cols = StringUtil.isEmpty(pdCols) ? Double.valueOf("1") : Double.valueOf(pdCols);
			if (cols > 1.0D) {
				Double spanDouble = StringUtil.isEmpty(span) ? Double.valueOf("1") : Double.valueOf(span);
				if (spanDouble > cols) {
					spanDouble = cols;
				}
				double w = Math.floor(100.0D / cols * 100.0D) / 100.0D;
				String _w = Math.floor(spanDouble * w * 100.0D) / 100.0D + "%";

				String cw = columnWidth;
				if (cw != null && !"0".equals(cw)) {
					if (cw.indexOf('%') > 1) {
						_w = cw.substring(0, cw.length() - 1) + "%";
					} else if (cw.indexOf("px") > 1) {
						_w = cw;
					} else if (Double.valueOf(cw) < 1.0D) {
						_w = Math.floor(Double.valueOf(cw) * 100.0D * 100.0D) / 100.0D + "%";
					} else {
						_w = cw;
					}
				}
				jspContext.setAttribute("doColumnLayout", _w + ";");
			}
			if ("true".equals(fit)) {
				jspContext.setAttribute("doFitLayout", "true");
			}
		}
	}

	public static String replaceXSSTagValue(String value) {
		value = StringUtils.replace(value, "<", "&lt;");
		value = StringUtils.replace(value, ">", "&gt;");
		value = StringUtils.replace(value, "\"", "&quot;");
		return value;
	}

	public static String createHtmlTag(JspContext jspContext, String html, boolean isend, String... params) {
		String space = " ";
		StringBuffer htmlBuffer = new StringBuffer();
		if (isend)
			htmlBuffer.append("</");
		else
			htmlBuffer.append("<");
		htmlBuffer.append(html);
		for (String param : params) {
			if (param != null && !"".equals(param)) {
				String jParam = (String) jspContext.getAttribute(param);
				if (null != jParam) {
					htmlBuffer.append(space);
					htmlBuffer.append(param);
					htmlBuffer.append("='");
					htmlBuffer.append(jParam);
					htmlBuffer.append("'");
				}
			}
		}
		htmlBuffer.append(">");
		return htmlBuffer.toString();
	}
}
