package com.yinhai.webframework;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yinhai.sysframework.codetable.domain.AppCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.ibatis.sqlmap.client.event.RowHandler;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.app.domain.jsonmodel.IGetResultBean;
import com.yinhai.sysframework.app.domain.jsonmodel.OperationBean;
import com.yinhai.sysframework.app.domain.jsonmodel.ResultBean;
import com.yinhai.sysframework.app.domain.jsonmodel.TopMsg;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.dao.hibernate.SimpleDao;
import com.yinhai.sysframework.dto.DTO;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.exception.SysLevelException;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.persistence.ibatis.ICountStatement;
import com.yinhai.sysframework.persistence.ibatis.IDao;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ParentPackage("ta-default")
public class BaseAction extends ActionSupport implements Action, IGetResultBean {

	private static Logger logger = LogManager.getLogger(BaseAction.class);

	private static final long serialVersionUID = 2863769505963567954L;

	public static final String BUSINESSID = "___businessId";

	public static final String JSON = "tojson";

	public static final String FILE = "tofile";
	protected boolean hasgetdto = false;

	protected HttpServletRequest request = ServletActionContext.getRequest();

	protected HttpServletResponse response = ServletActionContext.getResponse();

	private ParamDTO dto = new ParamDTO();

	private ParamDTO gridInfo = new ParamDTO();

	private ResultBean resultBean = new ResultBean();

	private ParamDTO ovDto = new ParamDTO();

	public ParamDTO getOvDto() {
		return ovDto;
	}

	public void setOvDto(ParamDTO ovDto) {
		this.ovDto = ovDto;
	}

	protected ResultBean setMsg(String msg) {
		resultBean.setMsg(msg);
		return resultBean;
	}

	protected ResultBean setMsg(String msg, String msgType) {
		resultBean.setMsg(msg);
		resultBean.setMsgType(msgType);
		return resultBean;
	}

	protected ResultBean setTopMsg(String topMsg) {
		resultBean.setTopMsg(topMsg);
		return resultBean;
	}

	protected ResultBean setTopMsg(String msg, int time, int width, int height) {
		resultBean.setTopTipMsg(new TopMsg(msg, time, width, height));
		return resultBean;
	}

	protected ResultBean setSuccess(boolean success) {
		resultBean.setSuccess(success);
		return resultBean;
	}

	protected ResultBean setData(Map<String, Object> data, boolean clear) {
		if (data != null) {
			Map<String, String> map = (HashMap) getSessionResource("__selectinput_flag_map_");
			if ((map != null) && (map.size() > 0)) {
				Map<String, Object> tmpmap = new HashMap();
				for (Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = (Map.Entry) it.next();
					String id = (String) entry.getKey();
					Object value = entry.getValue();
					if ((id != null) && (!"".equals(id)) && (value != null) && (!"".equals(value)) && (map.get(id) != null)) {
						String desc = getCodeDesc((String) map.get(id), String.valueOf(value), getDto().getUserInfo().getYab003());
						if ((desc != null) && (!"".equals(desc))) {
							tmpmap.put(id + "_desc", desc);
						}
					}
				}
				data.putAll(tmpmap);
			}
			resultBean.setData(data, clear);
		}
		return resultBean;
	}

	protected ResultBean setData(String id, Object value) {
		if ((id != null) && (!"".equals(id)) && (value != null) && (!"".equals(value))) {
			Map<String, String> map = (HashMap) getSessionResource("__selectinput_flag_map_");
			if ((map != null) && (map.get(id) != null)) {
				String desc = getCodeDesc((String) map.get(id), String.valueOf(value), getDto().getUserInfo().getYab003());
				if ((desc != null) && (!"".equals(desc))) {
					resultBean.addData(id + "_desc", desc);
				}
			}
		}
		return resultBean.addData(id, value);
	}

	protected ResultBean setInvalidField(String id, String message) {
		return resultBean.addInvalidField(id, message);
	}

	protected ResultBean setList(String gridId, PageBean pageBean) throws Exception {
		pageBean.setGridId(gridId);
		pageBean.setList(codeDisplay(gridId, pageBean.getList()));
		return resultBean.addList(gridId, pageBean);
	}

	protected ResultBean setList(String gridId, List list) throws Exception {
		return resultBean.addList(gridId, codeDisplay(gridId, list));
	}

	protected ResultBean setReadOnly(String ids) {
		return resultBean.addOperation(new OperationBean("readonly", ids));
	}

	protected ResultBean setEnable(String ids) {
		return resultBean.addOperation(new OperationBean("enable", ids));
	}

	protected ResultBean setDisabled(String ids) {
		return resultBean.addOperation(new OperationBean("disabled", ids));
	}

	protected ResultBean setActiveTab(String tabId) {
		return resultBean.addOperation(new OperationBean("select_tab", tabId));
	}

	protected ResultBean setHideObj(String ids) {
		return resultBean.addOperation(new OperationBean("hide", ids));
	}

	protected ResultBean setUnVisibleObj(String ids) {
		return resultBean.addOperation(new OperationBean("unvisible", ids));
	}

	protected ResultBean setShowObj(String ids) {
		return resultBean.addOperation(new OperationBean("show", ids));
	}

	protected ResultBean resetForm(String formid) {
		return resultBean.addOperation(new OperationBean("resetform", formid));
	}

	protected ResultBean setFocus(String id) {
		resultBean.setFocus(id);
		return resultBean;
	}

	protected ResultBean setRequired(String ids) {
		return resultBean.addOperation(new OperationBean("required", ids));
	}

	protected ResultBean setDisRequired(String ids) {
		return resultBean.addOperation(new OperationBean("disrequired", ids));
	}

	public ParamDTO getDto() {
		if (!hasgetdto) {
			dto.setUserInfo(WebUtil.getUserInfo(request));
			dto.setGridInfo(gridInfo);
			dto.setOvDto(ovDto);
			hasgetdto = true;
		}
		return dto;
	}

	public void setDto(ParamDTO dto) {
		this.dto = dto;
	}

	private Integer getStart(String gridId) {
		return Integer.valueOf(dto.getStart(gridId) == null ? 0 : dto.getStart(gridId).intValue());
	}

	private Integer getLimit(String gridId) {
		return Integer.valueOf(dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId).intValue());
	}

	protected PageBean queryForPage(String gridId, String sqlId, Object obj, boolean needTotal) {
		PageBean p = null;

		Integer start = getStart(gridId);
		Integer limit = getLimit(gridId);
		if (needTotal) {
			p = getDao().queryForPageWithCount(gridId, sqlId, obj, getDto());
		} else {
			p = new PageBean(getDao().queryForPage(sqlId, obj, start.intValue(), limit.intValue()));
			p.setStart(start);
			p.setLimit(limit);
		}

		return p;
	}

	protected boolean queryForRowHandler(final String gridId, String sqlId, Object obj, boolean needTotal) throws Exception {
		IDao dao = (IDao) getService("dao");
		String total = "0";
		if (needTotal) {
			ICountStatement cs = ServiceLocator.getService("countStatement", ICountStatement.class);
			total = cs.autoGetTotalCount(sqlId, obj, dao) + "";
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=UTF-8");
		final PrintWriter writer = response.getWriter();
		writer.write("{\"success\":true,\"lists\":{\"" + gridId + "\":{\"total\":" + total);
		Integer limit = getLimit(gridId);
		Integer start = getStart(gridId);
		if ((limit != null) && (limit.intValue() > 0)) {
			writer.write(",\"start\":" + start + ",\"limit\":" + limit + ",\"list\":[");
			dao.queryWithRowHandler(sqlId, obj, new RowHandler() {
				private int i;

				public void handleRow(Object paramAnonymousObject) {
					String str = (String) getSessionResource("___stop_query");
					if ((str != null) && (!"".equals(str))) {
						throw new AppException("ͣ停止查询");
					}
					Object localObject = paramAnonymousObject;
					Key localKey = BaseAction.this.getGridDisplayCode(gridId);
					if ((localKey != null) && (!localKey.isEmpty())) {
						ArrayList localArrayList = new ArrayList();
						localArrayList.add(paramAnonymousObject);
						try {
							localObject = codeDisplay(gridId, localArrayList).get(0);
						} catch (Exception localException) {
						}
					}
					if (i > 0) {
						writer.write(",");
					}
					writer.write(JSonFactory.bean2json(localObject));
					i += 1;
				}
			}, start.intValue(), limit.intValue());
		} else {
			writer.write(",\"list\":[");
			dao.queryWithRowHandler(sqlId, obj, new RowHandler() {
				private int i = 0;

				public void handleRow(Object paramAnonymousObject) {
					String str = (String) getSessionResource("___stop_query");
					if ((str != null) && (!"".equals(str))) {
						throw new AppException("ͣ停止查询");
					}
					Object localObject = paramAnonymousObject;
					Key localKey = BaseAction.this.getGridDisplayCode(gridId);
					if ((localKey != null) && (!localKey.isEmpty())) {
						ArrayList localArrayList = new ArrayList();
						localArrayList.add(paramAnonymousObject);
						try {
							localObject = codeDisplay(gridId, localArrayList).get(0);
						} catch (Exception localException) {
						}
					}
					if (i > 0) {
						writer.write(",");
					}
					writer.write(JSonFactory.bean2json(localObject));
					i += 1;
				}
			});
		}
		writer.write("]}}}");
		writer.flush();

		return true;
	}

	public List<DTO> jsonStrToDTOList(String jsonstr) {
		List list = (List) JSonFactory.json2bean(jsonstr, ArrayList.class);
		List<DTO> retlist = new ArrayList();
		if ((list != null) && (list.size() > 0)) {
			if (!(list.get(0) instanceof Map)) {
				throw new SysLevelException("json格式字符串反序列化类型不支持" + jsonstr);
			}
			for (int i = 0; i < list.size(); i++) {
				DTO dto = new ParamDTO();
				dto.putAll((Map) list.get(i));
				retlist.add(dto);
			}
		}
		return retlist;
	}

	public List<Key> jsonStrToList(String jsonstr) {
		List list = (List) JSonFactory.json2bean(jsonstr, ArrayList.class);
		List<Key> retlist = new ArrayList();
		if ((list != null) && (list.size() > 0)) {
			if (!(list.get(0) instanceof Map)) {
				logger.debug("json格式字符串反序列化失败");
				throw new SysLevelException("json格式字符串反序列化类型不支持" + jsonstr);
			}
			for (int i = 0; i < list.size(); i++) {
				retlist.add(new Key((Map) list.get(i)));
			}
		}
		return retlist;
	}

	public List<Key> getJsonParamAsList(String paramName) {
		String ids = request.getParameter(paramName);
		if (StringUtil.isEmpty(ids)) {
			return new ArrayList();
		}
		return jsonStrToList(ids);
	}

	protected void writeJsonToClient(Object obj) throws Exception {
		if (obj == null)
			return;
		String str = "";
		if (obj instanceof PageBean) {
			PageBean pbt = (PageBean) obj;
			pbt.setList(codeDisplay(pbt.getGridId(), pbt.getList()));
			str = JSonFactory.bean2json(pbt);
		} else if (obj instanceof String || obj instanceof StringBuffer || obj instanceof StringBuilder) {
			str = obj.toString();
		} else {
			str = JSonFactory.bean2json(obj);
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(str);
		writer.flush();
		writer.close();
	}

	private Key getGridDisplayCode(String gridId) {
		Object obj = getGridInfo().get(gridId + "_displayCode");
		if (obj == null)
			return null;
		String str = "";
		if ((obj instanceof String[])) {
			String[] tmp = (String[]) obj;
			for (int i = 0; i < tmp.length; i++) {
				if (str == "") {
					str = tmp[i];
				} else {
					str = str + "^" + tmp[i];
				}
			}
		} else {
			str = (String) obj;
		}
		if (str == "")
			return null;
		return new Key(str);
	}

	protected void setGridDisplayCode(String gridId, String columnId, String collection) {
		Key key = getGridDisplayCode(gridId);
		if ((key != null) && (key.size() != 0)) {
			String cid;
			Iterator it;
			if (key.size() == 1) {
				cid = "";
				for (it = key.keySet().iterator(); it.hasNext();) {
					cid = (String) it.next();
					if (cid.equals(columnId)) {
						gridInfo.put(gridId + "_displayCode", columnId + "`" + collection);
					} else
						gridInfo.put(gridId + "_displayCode", new String[] { columnId + "`" + collection, cid + "`" + key.getAsString(cid) });
				}
			} else {
				String[] str = key.getId().split("^");
				gridInfo.put(gridId + "_displayCode", str);
			}
		} else {
			gridInfo.put(gridId + "_displayCode", columnId + "`" + collection);
		}
	}

	protected List codeDisplay(String gridId, List list) throws Exception {
		if ((list == null) || ((list != null) && (list.size() == 0)) || (gridId == null) || (gridId == "")) {
			return list;
		}
		Key dis = getGridDisplayCode(gridId);
		if ((dis == null) || ((dis != null) && (dis.size() == 0))) {
			return list;
		}
		String yab003 = getDto().getUserInfo().getYab003();
		if ((yab003 == null) || ("".equals(yab003)))
			yab003 = null;
		for (int i = 0; i < list.size(); i++) {
			Object o = list.get(i);
			for (Iterator it = dis.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String collection = dis.getAsString(key);

				if (StringUtil.isEmpty(collection)) {
					collection = key;
				}

				Object vt = ReflectUtil.getPropertyValue(o, key);
				if ((vt != null) && (!"".equals(vt.toString()))) {
					String desc = CodeTableLocator.getCodeDesc(collection.toUpperCase(), vt.toString(), yab003);
					if ((o instanceof Map)) {
						((Map) o).put(key, desc);
					} else {
						ReflectUtil.getPropertyValue(o, key);
					}
				}
				list.set(i, o);
			}
		}

		return list;
	}

	protected List<Key> getSelected(String gridId) {
		String selected = gridInfo.getAsString(gridId + "_selected", null);
		if (StringUtil.isEmpty(selected)) {
			return new ArrayList();
		}
		return jsonStrToList(selected);
	}

	protected List<Key> getModified(String gridId) {
		String modified = gridInfo.getAsString(gridId + "_modified", null);
		if (StringUtil.isEmpty(modified)) {
			return new ArrayList();
		}
		return jsonStrToList(modified);
	}

	protected List<Key> getRemoved(String gridId) {
		String removed = gridInfo.getAsString(gridId + "_removed", null);
		if (StringUtil.isEmpty(removed)) {
			return new ArrayList();
		}
		return jsonStrToList(removed);
	}

	protected List<Key> getAdded(String gridId) {
		String added = gridInfo.getAsString(gridId + "_added", null);
		if (StringUtil.isEmpty(added)) {
			return new ArrayList();
		}
		return jsonStrToList(added);
	}

	protected Object getSessionResource(String key) {
		return request.getSession().getAttribute(key);
	}

	protected void putSessionResource(String key, Object obj) {
		request.getSession().setAttribute(key, obj);
	}

	protected void removeSessionResource(String key) {
		request.getSession().removeAttribute(key);
	}

	protected void writeSuccess() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write("{\"success\":true}");
		writer.flush();
	}

	protected void writeSuccess(String msg) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write("{\"success\":true,\"msg\":\"" + msg + "\"}");
		writer.flush();
	}

	protected void writeFailure() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write("{\"success\":false}");
		writer.flush();
	}

	protected void writeFailure(String msg) throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write("{\"success\":false,\"msg\":\"" + msg + "\"}");
		writer.flush();
	}

	protected IDao getDao() {
		return ServiceLocator.getService("dao", IDao.class);
	}

	protected SimpleDao getHibernateDao() {
		return ServiceLocator.getService("hibernateDao", SimpleDao.class);
	}

	protected Object getService(String name) {
		return ServiceLocator.getService(name);
	}

	protected <T> T getService(String name, Class<T> type) {
		return ServiceLocator.getService(name, type);
	}

	protected String getCodeDesc(String codeType, String codeValue, String orgId) {
		return CodeTableLocator.getCodeDesc(codeType, codeValue, orgId);
	}

	public List<AppCode> getCodeList(String codeType, String orgId) {
		return CodeTableLocator.getCodeList(codeType, orgId);
	}

	public String execute() throws Exception {
		return "success";
	}

	public ResultBean getResultBean() {
		return resultBean;
	}

	public void setResultBean(ResultBean resultBean) {
		this.resultBean = resultBean;
	}

	public ParamDTO getGridInfo() {
		return gridInfo;
	}

	public void setGridInfo(ParamDTO gridInfo) {
		this.gridInfo = gridInfo;
	}

	public ParamDTO reBuildDto(String strPer, ParamDTO dto) {
		ParamDTO reDto = new ParamDTO();
		Iterator<String> iterator = dto.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			if (string.indexOf(strPer) == -1) {
				reDto.put(string, dto.get(string));
			} else {
				int end = string.indexOf(strPer) + strPer.length();
				String string2 = string.substring(end);
				reDto.put(string2, dto.get(string));
			}
		}
		reDto.setGridInfo(dto.getGridInfo());
		reDto.setUserInfo(dto.getUserInfo());
		return reDto;
	}

	public ParamDTO removeDtoEndWith(String strEndWidth, ParamDTO dto) {
		ParamDTO reDto = dto;
		Iterator<String> iterator = dto.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			if (string.endsWith(strEndWidth))
				iterator.remove();
		}
		return reDto;
	}

	public Map reBuildMap(String strPer, DTO dto) {
		Map map = new HashMap();
		Iterator<String> iterator = dto.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			map.put(strPer + string, dto.get(string));
		}
		return map;
	}

	protected ResultBean setSelectInputList(String id, List list) {
		String value = JSonFactory.bean2json(list);
		if ((id != null) && (!"".equals(id)) && (value != null) && (!"".equals(value))) {
			Map<String, String> map = (HashMap) getSessionResource("__selectinput_flag_map_");
			if ((map != null) && (map.get(id) != null)) {
				String desc = getCodeDesc((String) map.get(id), String.valueOf(value), getDto().getUserInfo().getYab003());
				if ((desc != null) && (!"".equals(desc))) {
					resultBean.addData(id + "_desc", desc);
				}
			}
		}
		return resultBean.addData(id, value);
	}
	
	
	protected ResultBean setListView(String id,List list){
		String value = JSonFactory.bean2json(list);
		
		return resultBean.addData(id, value);
	}

	protected ResultBean selectInputLevelDataByList(String id, List list) {
		List list2 = new ArrayList();
		Map map = null;
		if (list != null && list.size() > 0) {
			for (Iterator i = list.iterator(); i.hasNext();) {
				Map app = (Map) i.next();
				map = new HashMap();
				map.put("id", app.get("codevalue"));
				map.put("name", app.get("codedesc"));
				map.put("level", app.get("levelvalue"));
				map.put("leaf", app.get("leaf"));
				map.put("py", StringUtil.getPYString(app.get("codedesc") + ""));
				list2.add(map);
			}
		}

		return setSelectInputList(id, list2);
	}
}
