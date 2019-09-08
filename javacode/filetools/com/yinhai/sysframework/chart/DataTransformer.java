package com.yinhai.sysframework.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.VO;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

public class DataTransformer {

	public static String listToChartHorizontal(List lst, String szNameField, String szDataField) {
		if ((null == lst) || (0 == lst.size()))
			return "[]";
		List list = new ArrayList();
		Map map = null;
		String[] a = szDataField.split("[,;]");
		Object o = null;
		Map m = null;
		Object[] os = null;

		for (int i = 0; i < lst.size(); i++) {
			o = lst.get(i);
			if ((o instanceof VO)) {
				m = ((VO) o).toMap();
			} else if ((o instanceof Map))
				m = (Map) o;
			else {
				ReflectUtil.copyObjectToMap(o, m = new HashMap());
			}
			map = new HashMap();
			map.put("name", m.get(szNameField));

			os = new Object[a.length];
			for (int j = 0; j < a.length; j++) {
				os[j] = m.get(a[j]);
			}
			map.put("data", os);
			list.add(map);
		}
		return JSonFactory.bean2json(list);
	}

	public static String listToChartHorizontal(List lst, String szDataField) {
		return listToChartHorizontal(lst, "name", szDataField);
	}

	public static String listToChartVertical(List lst, String szNameField, String szDataField) {
		if ((null == lst) || (0 == lst.size()) || (null == szDataField) || (null == szNameField))
			return "[]";
		List r = new ArrayList();

		String szLstKey = null;
		Map m1 = null;

		Map mLst = new HashMap();

		int i = 0;
		for (int j = lst.size(); i < j; i++) {
			Map m = null;

			Object o = lst.get(i);
			if ((o instanceof VO)) {
				m = ((VO) o).toMap();
			} else if ((o instanceof Map))
				m = (Map) o;
			else {
				ReflectUtil.copyObjectToMap(o, m = new HashMap());
			}
			String szStr = (String) m.get(szNameField);
			List lstT = (List) mLst.get(szStr);

			if (!szStr.equals(szLstKey)) {
				r.add(m1 = new HashMap());

				m1.put("name", szStr);
				szLstKey = szStr;
			}
			if (null == lstT) {
				m1.put("data", lstT = new ArrayList());
				mLst.put(szStr, lstT);
			}
			lstT.add(m.get(szDataField));
		}

		return JSonFactory.bean2json(r.toArray());
	}

	public static String listToArrayString(List<Map> lst, String szDataField) {
		if ((null == lst) || (0 == lst.size()) || (null == szDataField))
			return "[]";
		List r = new ArrayList();

		int i = 0;
		for (int j = lst.size(); i < j; i++) {
			r.add(((Map) lst.get(i)).get(szDataField));
		}

		return JSonFactory.bean2json(r.toArray());
	}

	public static String listToPieString(List lst, String szNameField, String szDataField) {
		if ((null == lst) || (0 == lst.size()) || (null == szDataField))
			return "[]";
		List<Object[]> r = new ArrayList();

		Map m = null;

		int i = 0;
		for (int j = lst.size(); i < j; i++) {
			Object o = lst.get(i);
			if ((o instanceof VO)) {
				m = ((VO) o).toMap();
			} else if ((o instanceof Map))
				m = (Map) o;
			else
				ReflectUtil.copyObjectToMap(o, m = new HashMap());
			r.add(new Object[] { m.get(szNameField), m.get(szDataField) });
		}
		return JSonFactory.bean2json(r);
	}

	public static String pieDonut(List lst) {
		if ((null == lst) || (0 == lst.size()))
			return "[]";
		return JSonFactory.bean2json(lst);
	}

	private void test_lineBasic() {
	}

	public static void main(String[] args) {
		new DataTransformer().test_lineBasic();
	}
}
