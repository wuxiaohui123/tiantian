package com.yinhai.sysframework.persistence;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.BaseVO;
import com.yinhai.sysframework.util.CollectionUtils;
import com.yinhai.sysframework.util.json.JSonFactory;

@SuppressWarnings("rawtypes")
public class PageBean implements Serializable {

	private static final long serialVersionUID = 4797865277662075470L;
	private String gridId;
	protected Integer start;
	protected Integer limit;
	protected Integer total;
	protected List list;

	public PageBean() {
	}

	public PageBean(List list) {
		this.list = list;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"gridId\":\"").append(getGridId()).append("\",").append("\"start\":")
				.append(getStart()).append(",").append("\"limit\":").append(getLimit()).append(",")
				.append("\"total\":").append(getTotal()).append(",").append("\"list\":");

		if (list != null) {
			if (list.size() > 8000) {
				sb.append(JSonFactory.bean2json(list));
			} else {
				sb.append("[");
				Object obj = null;
				for (int i = 0; i < list.size(); i++) {
					obj = list.get(i);
					if (i > 0)
						sb.append(",\n");
					if ((obj instanceof BaseVO)) {
						sb.append(((BaseVO) obj).toJson());
					} else if ((obj instanceof Map)) {
						sb.append(CollectionUtils.mapToJson((Map) obj));
					} else {
						sb.append(JSonFactory.bean2json(obj));
					}
				}

				sb.append("]");
			}
		} else {
			sb.append("[]");
		}
		sb.append("}");
		return sb.toString();
	}
}
