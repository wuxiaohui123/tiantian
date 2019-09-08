package com.yinhai.sysframework.print;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SaveAsInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String EXCELFILEPATH = "";
	private static final String EXCELFILENAME = "ExcelFileName";
	private Class domainClass;
	private List columnList = new ArrayList();
	private List domainList = new ArrayList();
	private String sourceFileName;
	private String fileName;
	private int startPage;
	private int startRow;
	private String startStrRow;
	private int startColumn;
	private String startStrColumn;
	private boolean viewTitle;
	private boolean isShowCode = true;
	private boolean isViewSubject = false;
	private boolean isProtected = false;

	private String subjectStr;

	private boolean autoInsert;
	private List cellList = new ArrayList();

	private transient ResultSet rs;

	private transient Log log = LogFactory.getLog(getClass());

	public SaveAsInfo() {
	}

	public SaveAsInfo(Class domainClass) {
		this.domainClass = domainClass;
	}

	public Class getDomainClass() {
		return domainClass;
	}

	public void setDomainClass(Class domainClass) {
		this.domainClass = domainClass;
	}

	public List getColumnList() {
		return columnList;
	}

	public void setColumnList(List columnList) {
		this.columnList = columnList;
	}

	public void addColumnList(ColumnInfo columnInfo) {
		columnList.add(columnInfo);
	}

	public List getDomainList() {
		return domainList;
	}

	public void setDomainList(List domainList) {
		this.domainList = domainList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		if (sourceFileName != null) {
			if ((sourceFileName.startsWith("/")) || (sourceFileName.indexOf(":") > -1)) {
				this.sourceFileName = sourceFileName;
			} else {
				this.sourceFileName = ("" + sourceFileName);
			}
		} else {
			this.sourceFileName = null;
		}
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}

	public String getStartStrColumn() {
		return startStrColumn;
	}

	public void setStartStrColumn(String startStrColumn) {
		this.startStrColumn = startStrColumn;
		startColumn = (cervertExcelTodecimalist(startStrColumn) - 1);
	}

	public String getStartStrRow() {
		return startStrRow;
	}

	public void setStartStrRow(String startStrRow) {
		this.startStrRow = startStrRow;
		startRow = (Integer.parseInt(startStrRow) - 1);
	}

	public boolean isViewTitle() {
		return viewTitle;
	}

	public void setViewTitle(boolean viewTitle) {
		this.viewTitle = viewTitle;
	}

	public List getCellList() {
		return cellList;
	}

	public void setCellList(List cellList) {
		this.cellList = cellList;
	}

	public void addCell(ExcelCellInfo excelCell) {
		cellList.add(excelCell);
	}

	public void addMapValue(Map map) {
		String key = "";
		Object val = null;
		for (Iterator<Map.Entry<String, Object>> i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, Object> entry = (Map.Entry) i.next();
			key = (String) entry.getKey();
			val = entry.getValue();
			if ("ExcelFileName".equals(key)) {
				setSourceFileName(isNull(String.valueOf(val), ""));
			} else {
				String[] keyArray = parseStr(key);
				if ((keyArray != null) && (keyArray.length == 3)) {

					String value = isNull(String.valueOf(val), "");

					int page = getPage(key);
					String column = getColumn(key);
					String row = getRow(key);
					ExcelCellInfo excelCellInfo = new ExcelCellInfo(page, column, row, value, null);

					addCell(excelCellInfo);
				}
			}
		}
	}

	private int getPage(String excelKey) {
		String[] strArray = parseStr(excelKey);
		if (strArray == null) {
			return -1;
		}
		if (strArray.length > 2) {
			return Integer.parseInt(strArray[2]);
		}
		return -1;
	}

	private String getColumn(String excelKey) {
		String[] strArray = parseStr(excelKey);
		if (strArray == null) {
			return null;
		}
		if (strArray.length > 0) {
			return strArray[0];
		}
		return null;
	}

	private String getRow(String excelKey) {
		String[] strArray = parseStr(excelKey);
		if (strArray == null) {
			return null;
		}
		if (strArray.length > 1) {
			return strArray[1];
		}
		return null;
	}

	private String[] parseStr(String str) {
		if (str == null) {
			return null;
		}
		str = str.toUpperCase();
		List list = new ArrayList();

		StringTokenizer st = new StringTokenizer(str, "_");

		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}

		return (String[]) list.toArray(new String[0]);
	}

	private String isNull(String text, String defaultStr) {
		if (text == null) {
			return defaultStr;
		}
		return text;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int cervertExcelTodecimalist(String value) {
		if ((value == null) || (value.length() == 0)) {
			return 1;
		}
		String columnNumber = value.toUpperCase();
		int len = columnNumber.length();
		int num = 0;
		for (int i = 0; i < len; i++) {
			char c = columnNumber.charAt(i);
			int A = 65;
			num += (c - A + 1) * (int) Math.pow(26.0D, len - i - 1);
		}

		return num;
	}

	public boolean isAutoInsert() {
		return autoInsert;
	}

	public void setAutoInsert(boolean autoInsert) {
		this.autoInsert = autoInsert;
	}

	public boolean isShowCode() {
		return isShowCode;
	}

	public void setShowCode(boolean isShowCode) {
		this.isShowCode = isShowCode;
	}

	public boolean isViewSubject() {
		return isViewSubject;
	}

	public void setViewSubject(boolean isViewSubject) {
		this.isViewSubject = isViewSubject;
	}

	public String getSubjectStr() {
		return subjectStr;
	}

	public void setSubjectStr(String subjectStr) {
		this.subjectStr = subjectStr;
		isViewSubject = true;
		if (startRow == 0) {
			startRow = 1;
		}
	}

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}
}
