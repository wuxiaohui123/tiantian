package com.yinhai.sysframework.app.domain.jsonmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.CollectionUtils;
import com.yinhai.sysframework.util.json.JSonFactory;
@SuppressWarnings({"rawtypes","unchecked"})
public class ResultBean implements Serializable {

	private static final long serialVersionUID = 5830857194872732536L;
	public static final String _SEL_ = "_sel_";
	protected boolean success = true;

	protected String msg;

	protected String msgType;

	protected Map<String, String> validateErrors;

	protected Map<String, Object> fieldData;

	protected Map<String, PageBean> lists;

	public List<OperationBean> operation;

	protected String focus;

	protected String topMsg;

	protected TopMsg topTipMsg;

	public ResultBean() {
	}

	public ResultBean(boolean success) {
		this.success = success;
	}

	public ResultBean(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}

	public ResultBean(boolean success, HashMap<String, Object> data) {
		this.success = success;
		fieldData = data;
	}

	public ResultBean(boolean success, String msg, HashMap<String, Object> data) {
		this.success = success;
		this.msg = msg;
		fieldData = data;
	}

	public ResultBean(boolean success, String msg, HashMap<String, Object> data, HashMap<String, PageBean> lists) {
		this.success = success;
		this.msg = msg;
		fieldData = data;
		this.lists = lists;
	}

	public ResultBean(boolean success, String msg, String msgType, HashMap<String, Object> data,
			HashMap<String, PageBean> lists) {
		this.success = success;
		this.msg = msg;
		fieldData = data;
		this.lists = lists;
		this.msgType = msgType;
	}

	public ResultBean setData(Map<String, Object> data, boolean clear) {
		if (fieldData == null)
			fieldData = new HashMap<String, Object>();
		if (clear) {
			fieldData.clear();
		}
		fieldData.putAll(data);
		return this;
	}

	public ResultBean addData(String key, Object value) {
		if (fieldData == null)
			fieldData = new HashMap<String, Object>();
		if (null != value) {
			if ((value instanceof List)) {
				fieldData.put(key, value);
				value = JSonFactory.bean2json(value);
				fieldData.put(_SEL_ + key, value);
			} else if (((value instanceof String)) && (value.toString().contains("["))
					&& (value.toString().indexOf("]") > 0)) {
				fieldData.put(_SEL_ + key, value);
				fieldData.put(key, value);
			} else {
				fieldData.put(key, value);
			}
		}
		return this;
	}

	public ResultBean addInvalidField(String key, String message) {
		if (validateErrors == null)
			validateErrors = new HashMap<String, String>();
		validateErrors.put(key, message);
		return this;
	}

	public ResultBean addList(String key, PageBean pageBean) {
		if (lists == null)
			lists = new HashMap<String, PageBean>();
		lists.put(key, pageBean);
		return this;
	}

	public ResultBean addList(String key, List<Map<String, PageBean>> list) {
		if (lists == null)
			lists = new HashMap<String, PageBean>();
		PageBean pageBean = new PageBean(list);
		pageBean.setGridId(key);
		lists.put(key, pageBean);
		return this;
	}

	public ResultBean addOperation(OperationBean operationBean) {
		if (operation == null) {
			operation = new ArrayList<OperationBean>();
		}
		operation.add(operationBean);
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, PageBean> getLists() {
		return lists;
	}

	void clearValues() {
		fieldData = null;
		lists = null;
		operation = null;
		success = false;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public TopMsg getTopTipMsg() {
		return topTipMsg;
	}

	public void setTopTipMsg(TopMsg topTipMsg) {
		this.topTipMsg = topTipMsg;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public Map<String, String> getValidateErrors() {
		return validateErrors;
	}

	public void setValidateErrors(Map<String, String> validateErrors) {
		this.validateErrors = validateErrors;
	}

	public Map<String, Object> getFieldData() {
		return fieldData;
	}

	public void setFieldData(HashMap<String, Object> fieldData) {
		this.fieldData = fieldData;
	}

	public List<OperationBean> getOperation() {
		return operation;
	}

	public void setOperation(ArrayList<OperationBean> operation) {
		this.operation = operation;
	}

	public void setLists(HashMap<String, PageBean> lists) {
		this.lists = lists;
	}

	public String getTopMsg() {
		return topMsg;
	}

	public void setTopMsg(String topMsg) {
		this.topMsg = topMsg;
	}

	
	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"success\":").append(isSuccess());

		if ((msgType != null) && (!"".equals(msgType)) && (msg != null)) {
			sb.append(", \"msgBox\" : {");
			sb.append("\"msg\":\"").append(JSonFactory.toJson(msg)).append("\"");
			sb.append(",\"msgType\":\"").append(JSonFactory.toJson(msgType)).append("\"");
			sb.append("}");
		} else if (msg != null) {
			sb.append(",\"msg\":\"").append(JSonFactory.toJson(msg)).append("\"");
		}
		if ((topMsg != null) && (!"".equals(topMsg))) {
			sb.append(",\"topMsg\":\"").append(topMsg).append("\"");
		}
		if (topTipMsg != null) {
			sb.append(",\"topTipMsg\":").append(JSonFactory.bean2json(topTipMsg));
		}
		if ((focus != null) && (!"".equals(focus))) {
			sb.append(",\"focus\":\"").append(focus).append("\"");
		}
		if ((fieldData != null) && (fieldData.size() > 0)) {
			sb.append(",\"fieldData\":").append(CollectionUtils.mapToJson(fieldData));
		}
		if ((validateErrors != null) && (validateErrors.size() > 0)) {
			sb.append(",\"validateErrors\":").append(CollectionUtils.mapToJson(validateErrors));
		}
		if ((operation != null) && (operation.size() > 0)) {
			sb.append(",\"operation\":[");
			for (int i = 0; i < operation.size(); i++) {
				if (i > 0)
					sb.append(",");
				sb.append(((OperationBean) operation.get(i)).toJson());
			}
			sb.append("]");
		}
		if ((lists != null) && (lists.size() > 0)) {
			sb.append(",\"lists\":{");
			Iterator<Map.Entry<String, PageBean>> iterator = lists.entrySet().iterator();
			boolean noFirst = false;
			while (iterator.hasNext()) {
				Map.Entry<String, PageBean> next = (Map.Entry) iterator.next();
				if (noFirst)
					sb.append(",");
				sb.append("\"").append((String) next.getKey()).append("\":")
						.append(((PageBean) next.getValue()).toJson());
				noFirst = true;
			}
			sb.append("}");
		}
		sb.append("}");
		return sb.toString();
	}
}
