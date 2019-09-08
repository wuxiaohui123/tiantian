package com.yinhai.sysframework.print;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ExcelInfo implements Serializable {

	private static final String EXCELFILEPATH = "";
	private static final String EXCELFILENAME = "ExcelFileName";
	private String sourceFileName = "";
	private String tartgetFileName = "";
	private List cellList = new ArrayList();
	private List copySheetList = new ArrayList();

	public static final String CELLSPIT = "_";

	public ExcelInfo() {
	}

	public ExcelInfo(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public static String buidCellName(int page, String column, String row) {
		return String.valueOf(column) + "_" + String.valueOf(row) + "_" + String.valueOf(page);
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

	public String getTartgetFileName() {
		return tartgetFileName;
	}

	public void setTartgetFileName(String tartgetFileName) {
		this.tartgetFileName = tartgetFileName;
	}

	public List getCopySheetList() {
		return copySheetList;
	}

	public void setCopySheetList(List copySheetList) {
		this.copySheetList = copySheetList;
	}

	public void addCopySheetList(CopySheet copySheet) {
		copySheetList.add(copySheet);
	}

	public void addMapValue(Map map) {
		String key = "";
		Object val = null;
		for (Iterator<Map.Entry<String, Object>> i = map.keySet().iterator(); i.hasNext();) {
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
}
