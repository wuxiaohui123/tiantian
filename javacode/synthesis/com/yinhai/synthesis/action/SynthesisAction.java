package com.yinhai.synthesis.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;

import com.yinhai.synthesis.service.SynthesisService;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.webframework.BaseAction;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Namespace("/process/synthesis")
@Action(value = "synthesisAction")
public class SynthesisAction extends BaseAction {

	private SynthesisService synthesisService = (SynthesisService) super.getService("synthesisService");
	
	protected final String linkName = "@whpt";
	protected final String bz = "1";// 是否做进业务系统标志 1是
	protected final String id = "JY12GX01300";// 项目编号
	protected final String name = "基板";// 项目名称
	protected final String departId = "01";// 部门编号
	protected final String departname = "技术部";// 部门名称2
	protected final String zdjjr = "软件维护";// 部门名称

	/**
	 * 对树形下拉列表进行赋值
	 * 
	 * @param selectInputId
	 *            jsp页面的ID
	 * @param aaa100
	 *            代码类型
	 * @param yab003
	 *            经办机构
	 * @throws Exception
	 */
	public void setSelectInputLevelDataByList(String selectInputId, String aaa100, String yab003) throws Exception {
		selectInputLevelDataByList(selectInputId, synthesisService.getSelectInputLevelDataByList(aaa100, null, yab003));
	}
	
	public void setSelectInputLevelDataByCache(String selectInputId, String aaa100, String yab003) throws Exception {
		selectInputLevelDataByList(selectInputId, synthesisService.getSelectInputLevelDataListByCache(aaa100, null, yab003));

	}
	/**
	 * 对树形下拉列表进行赋值
	 * 
	 * @param selectInputId
	 * @param aaa100
	 * @param aaa102
	 *            使用like "aaa102%"
	 * @param yab003
	 * @throws Exception
	 */
	public void setSelectInputLevelDataByList(String selectInputId, String aaa100, String aaa102, String yab003) throws Exception {
		selectInputLevelDataByList(selectInputId, synthesisService.getSelectInputLevelDataByList(aaa100, aaa102, yab003));
	}

	public void setTipsMessage(String aaz002) throws AppException {
		setMsg(synthesisService.getTipsMessage(aaz002), SUCCESS);
	}

	/**
	 * 
	 * @param workbenchUrl
	 * @param request
	 * @return
	 * @throws AppException
	 * @description
	 * @version 1.0
	 * @author
	 * @update
	 */
	public Map getPaymentInfo(String aaz002) throws AppException {
		Map map = new HashMap();
		Map paymentInfo = (Map) super.getDao().queryForObject("paymentBaseInfo.getAc74Info2View", aaz002);
		if (!ValidateUtil.isEmpty(paymentInfo) && !"11".equals(paymentInfo.get("yad169").toString().trim())) {
			paymentInfo = (Map) super.getDao().queryForObject("paymentBaseInfo.getAe31Info2View", aaz002);
		}
		map.putAll(paymentInfo);
		return map;
	}

	public String getSelectJSON(List list, String id, String name) {
		if (ValidateUtil.isEmpty(list)) {
			return "[]";
		}
		StringBuffer sb = new StringBuffer("");
		Map mp = null;
		for (int j = 0; j < list.size(); j++) {
			mp = (Map) list.get(j);
			sb.append("{'id':'").append(mp.get(id)).append("',");
			sb.append("'name':'").append(mp.get(name)).append("'},");
		}
		String str = null;
		if (list.size() > 0) {
			str = "[" + sb.substring(0, sb.lastIndexOf(",")) + "]";
		}
		return str;
	}

	public List getStringArray(String arg0) {
		return synthesisService.getStringArray(arg0);
	}

	public String getSequence(String arg0, String arg1) {
		return synthesisService.getSequence(arg0, arg1);
	}
}
