package com.yinhai.sysframework.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.commons.beanutils.BeanUtils;

import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.util.ValidateUtil;

public class ExcelPrintDomain implements Serializable {

	private List domains = new ArrayList();

	private List lista = new ArrayList();

	private List listb = new ArrayList();

	private List listc = new ArrayList();

	private List listd = new ArrayList();

	private List liste = new ArrayList();

	private Object domaina;

	private Object domainb;

	private Object domainc;

	private Object domaind;

	private Object domaine;

	private ServletContext context;

	private int pageNo;

	private String title;

	private String paramA;

	private String paramB;

	private String paramC;

	private String paramD;

	private String paramE;

	private String yab003;

	private int isBeginPage = 0;

	private int isEndPage = 0;

	public ExcelPrintDomain() {
	}

	public ExcelPrintDomain(ServletContext context) {
		this.context = context;
	}

	public void addDomain(Object obj) {
		domains.add(obj);
	}

	public void addLista(Object obj) {
		lista.add(obj);
	}

	public void addListb(Object obj) {
		listb.add(obj);
	}

	public void addListc(Object obj) {
		listc.add(obj);
	}

	public void addListd(Object obj) {
		listd.add(obj);
	}

	public void addListe(Object obj) {
		liste.add(obj);
	}

	public List getDomains() {
		return domains;
	}

	public void setDomains(List domains) {
		this.domains = domains;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public List getLista() {
		return lista;
	}

	public void setLista(List lista) {
		this.lista = lista;
	}

	public List getListb() {
		return listb;
	}

	public void setListb(List listb) {
		this.listb = listb;
	}

	public List getListc() {
		return listc;
	}

	public void setListc(List listc) {
		this.listc = listc;
	}

	public List getListd() {
		return listd;
	}

	public void setListd(List listd) {
		this.listd = listd;
	}

	public List getListe() {
		return liste;
	}

	public void setListe(List liste) {
		this.liste = liste;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDateYear(Date date) {
		int iy = date.getYear();
		String retV = "" + iy;
		return retV;
	}

	public String getDateMonth(Date date) {
		int im = date.getMonth();
		String retV = "" + im;
		return retV;
	}

	public String getDateDay(Date date) {
		int id = date.getDay();
		String retV = "" + id;
		return retV;
	}

	public String getCodeDesc(String codeType, String codeValue) {
		if (context == null)
			return codeValue;
		if ((codeValue == null) || (codeValue.length() == 0))
			return "";
		List vector = (List) context.getAttribute(codeType);
		String code = "";
		if ((vector == null) || (vector.size() == 0))
			return codeValue;
		Object oOrgId = null;
		String codeFilter = "";
		String orgId = "";
		for (int j = 0; j < vector.size(); j++) {
			AppCode appCode = (AppCode) vector.get(j);
			code = appCode.getCodeValue();
			if (!ValidateUtil.isEmpty(yab003)) {
				try {
					oOrgId = BeanUtils.getProperty(appCode, "orgId");
					codeFilter = appCode.getCodeValue();
					orgId = (String) oOrgId;
					if (((orgId == null) || (orgId.equalsIgnoreCase("9999"))) && (codeFilter != null)
							&& (codeFilter.equalsIgnoreCase(codeValue.toString()))) {
						return appCode.getCodeDESC();
					}
					if ((codeFilter != null) && (orgId.equalsIgnoreCase(String.valueOf(yab003)))
							&& (codeValue.equalsIgnoreCase(codeFilter))) {
						return appCode.getCodeDESC();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if ((code != null) && (code.equals(codeValue))) {
				return ((AppCode) vector.get(j)).getCodeDESC();
			}
		}

		return codeValue;
	}

	public String getCodeDesc(String codeType, Long codeValue) {
		if (context == null)
			return codeValue.toString();
		Vector vector = (Vector) context.getAttribute(codeType);
		String code = "";
		if ((vector == null) || (vector.size() == 0))
			return codeValue.toString();
		Object oOrgId = null;
		String codeFilter = "";
		String orgId = "";
		for (int j = 0; j < vector.size(); j++) {
			AppCode appCode = (AppCode) vector.get(j);
			code = appCode.getCodeValue();
			if (!ValidateUtil.isEmpty(yab003)) {
				try {
					oOrgId = BeanUtils.getProperty(appCode, "orgId");
					codeFilter = appCode.getCodeValue();
					orgId = (String) oOrgId;
					if (((orgId == null) || (orgId.equalsIgnoreCase("9999"))) && (codeFilter != null)
							&& (codeFilter.equalsIgnoreCase(codeValue.toString()))) {
						return appCode.getCodeDESC();
					}
					if ((codeFilter != null) && (orgId.equalsIgnoreCase(String.valueOf(yab003)))
							&& (codeFilter.equalsIgnoreCase(codeValue.toString()))) {
						return appCode.getCodeDESC();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if ((code != null) && (code.equalsIgnoreCase(codeValue.toString()))) {
				return ((AppCode) vector.get(j)).getCodeDESC();
			}
		}
		return codeValue.toString();
	}

	public Object getDomaina() {
		return domaina;
	}

	public void setDomaina(Object domaina) {
		this.domaina = domaina;
	}

	public Object getDomainb() {
		return domainb;
	}

	public void setDomainb(Object domainb) {
		this.domainb = domainb;
	}

	public Object getDomainc() {
		return domainc;
	}

	public void setDomainc(Object domainc) {
		this.domainc = domainc;
	}

	public Object getDomaind() {
		return domaind;
	}

	public void setDomaind(Object domaind) {
		this.domaind = domaind;
	}

	public Object getDomaine() {
		return domaine;
	}

	public void setDomaine(Object domaine) {
		this.domaine = domaine;
	}

	public String lowerToUpperOfMoney(double je) {
		String money = "";
		String num = "零壹贰叁肆伍陆柒捌玖";
		String[] unit = { "元", "拾", "佰", "仟", "万", "拾万", "佰万", "仟万", "亿", "拾亿", "佰亿", "仟亿" };
		String s = String.valueOf(je);
		int a = s.indexOf("+");
		int e = s.indexOf("E");

		if (e != -1) {
			int index = 0;
			if (a == -1) {
				index = Integer.parseInt(s.substring(e + 1));
			} else
				index = Integer.parseInt(s.substring(a + 1));
			String sub1 = s.substring(0, e);
			int dot = sub1.indexOf(".");

			if (dot == -1) {
				for (int i = 1; i <= index; i++) {
					s = sub1 + "0";
				}
			} else {
				String sub11 = sub1.substring(0, dot);
				String sub12 = sub1.substring(dot + 1);
				if (index >= sub12.length()) {
					int j = index - sub12.length();
					for (int i = 1; i <= j; i++) {
						sub12 = sub12 + "0";
					}
				} else {
					sub12 = sub12.substring(0, index) + "." + sub12.substring(index);
				}

				s = sub11 + sub12;
			}
		}
		int sdot = s.indexOf(".");
		String beforeDot = "";
		String afterDot = "";

		if (sdot != -1) {
			beforeDot = s.substring(0, sdot);
			afterDot = s.substring(sdot + 1);
		} else {
			beforeDot = s;
		}
		int bl = beforeDot.length();
		boolean zero = false;
		int z = 0;

		int j = 0;
		for (int i = bl - 1; j <= bl - 1; i--) {
			int number = Integer.parseInt(String.valueOf(beforeDot.charAt(j)));
			if (number == 0) {
				zero = true;
				z++;
			} else {
				zero = false;
				z = 0;
			}
			if ((zero) && (z == 1)) {
				money = money +  "零";
			} else if (!zero) {
				money = money + num.substring(number, number + 1) + unit[i];
			}
			j++;
		}

		String ss = "";
		String moneySub1 = null;
		String moneySub2 = null;
		for (int i = 1; i <= 2; i++) {
			if (i == 1) {
				ss ="万";
			} else
				ss = "亿";
			int last = money.lastIndexOf(ss);
			if (last != -1) {
				moneySub1 = money.substring(0, last);
				moneySub2 = money.substring(last, money.length());
				int last2 = moneySub1.indexOf(ss);
				while (last2 != -1) {
					moneySub1 = moneySub1.substring(0, last2) + moneySub1.substring(last2 + 1, moneySub1.length());

					last2 = moneySub1.indexOf(ss);
				}
				money = moneySub1 + moneySub2;
			}
		}

		int yuan = money.indexOf("元");

		if (yuan == -1) {
			int zi = money.lastIndexOf("零");

			if (zi == money.length() - 1) {
				money = money.substring(0, money.length() - 1) + "元";
			}
		}

		if (!afterDot.equals("")) {
			int al = afterDot.length();
			if (al > 2) {
				afterDot = afterDot.substring(0, 2);
				al = afterDot.length();
			}

			if ((!afterDot.equals("0")) && (!afterDot.equals("00"))) {
				for (int i = 0; i < al; i++) {
					int number = Integer.parseInt(String.valueOf(afterDot.charAt(i)));

					if ((number != 0) && (i == 0)) {
						money = money + num.substring(number, number + 1) + "角";
					} else if ((number != 0) && (i == 1)) {
						money = money + num.substring(number, number + 1) + "分";
					} else if ((number == 0) && (i == 0)) {
						money = money + "零";
					}
				}
			}
		}

		if ((money.indexOf("角") == -1) && (money.indexOf("分") == -1))
			money = money + "整";
		return money;
	}

	public String lowerToUpperOfMoney(int je) {
		String money = "";
		String num = "零壹贰叁肆伍陆柒捌玖";
		String[] unit = { "元", "拾", "佰", "仟", "万", "拾万", "佰万", "仟万", "亿", "拾亿", "佰亿", "仟亿" };

		String s = String.valueOf(je);
		int a = s.indexOf("+");
		int e = s.indexOf("E");

		if (e != -1) {
			int index = 0;
			if (a == -1) {
				index = Integer.parseInt(s.substring(e + 1));
			} else
				index = Integer.parseInt(s.substring(a + 1));
			String sub1 = s.substring(0, e);
			int dot = sub1.indexOf(".");

			if (dot == -1) {
				for (int i = 1; i <= index; i++) {
					s = sub1 + "0";
				}
			} else {
				String sub11 = sub1.substring(0, dot);
				String sub12 = sub1.substring(dot + 1);
				if (index >= sub12.length()) {
					int j = index - sub12.length();
					for (int i = 1; i <= j; i++) {
						sub12 = sub12 + "0";
					}
				} else {
					sub12 = sub12.substring(0, index) + "." + sub12.substring(index);
				}

				s = sub11 + sub12;
			}
		}
		int sdot = s.indexOf(".");
		String beforeDot = "";
		String afterDot = "";

		if (sdot != -1) {
			beforeDot = s.substring(0, sdot);
			afterDot = s.substring(sdot + 1);
		} else {
			beforeDot = s;
		}
		int bl = beforeDot.length();
		boolean zero = false;
		int z = 0;

		int j = 0;
		for (int i = bl - 1; j <= bl - 1; i--) {
			int number = Integer.parseInt(String.valueOf(beforeDot.charAt(j)));
			if (number == 0) {
				zero = true;
				z++;
			} else {
				zero = false;
				z = 0;
			}
			if ((zero) && (z == 1)) {
				money = money + "零";
			} else if (!zero) {
				money = money + num.substring(number, number + 1) + unit[i];
			}
			j++;
		}

		String ss = "";
		String moneySub1 = null;
		String moneySub2 = null;
		for (int i = 1; i <= 2; i++) {
			if (i == 1) {
				ss = "万";
			} else
				ss = "亿";
			int last = money.lastIndexOf(ss);
			if (last != -1) {
				moneySub1 = money.substring(0, last);
				moneySub2 = money.substring(last, money.length());
				int last2 = moneySub1.indexOf(ss);
				while (last2 != -1) {
					moneySub1 = moneySub1.substring(0, last2) + moneySub1.substring(last2 + 1, moneySub1.length());

					last2 = moneySub1.indexOf(ss);
				}
				money = moneySub1 + moneySub2;
			}
		}

		int yuan = money.indexOf("元");

		if (yuan == -1) {
			int zi = money.lastIndexOf("零");

			if (zi == money.length() - 1) {
				money = money.substring(0, money.length() - 1) + "元";
			}
		}

		if (!afterDot.equals("")) {
			int al = afterDot.length();
			if (al > 2) {
				afterDot = afterDot.substring(0, 2);
				al = afterDot.length();
			}

			if ((!afterDot.equals("0")) && (!afterDot.equals("00"))) {
				for (int i = 0; i < al; i++) {
					int number = Integer.parseInt(String.valueOf(afterDot.charAt(i)));

					if ((number != 0) && (i == 0)) {
						money = money + num.substring(number, number + 1) + "角";
					} else if ((number != 0) && (i == 1)) {
						money = money + num.substring(number, number + 1) + "分";
					} else if ((number == 0) && (i == 0)) {
						money = money + "零";
					}
				}
			}
		}

		if ((money.indexOf("角") == -1) && (money.indexOf("分") == -1))
			money = money + "整";
		return money;
	}

	public int getIsBeginPage() {
		return isBeginPage;
	}

	public void setIsBeginPage(int isBeginPage) {
		this.isBeginPage = isBeginPage;
	}

	public int getIsEndPage() {
		return isEndPage;
	}

	public void setIsEndPage(int isEndPage) {
		this.isEndPage = isEndPage;
	}

	public String getParamA() {
		return paramA;
	}

	public void setParamA(String paramA) {
		this.paramA = paramA;
	}

	public String getParamB() {
		return paramB;
	}

	public void setParamB(String paramB) {
		this.paramB = paramB;
	}

	public String getParamC() {
		return paramC;
	}

	public void setParamC(String paramC) {
		this.paramC = paramC;
	}

	public String getParamD() {
		return paramD;
	}

	public void setParamD(String paramD) {
		this.paramD = paramD;
	}

	public String getParamE() {
		return paramE;
	}

	public void setParamE(String paramE) {
		this.paramE = paramE;
	}

	public String getYab003() {
		return yab003;
	}

	public void setYab003(String yab003) {
		this.yab003 = yab003;
	}
}
