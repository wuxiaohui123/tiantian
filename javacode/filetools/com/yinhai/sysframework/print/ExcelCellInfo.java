package com.yinhai.sysframework.print;

import java.io.Serializable;

import jxl.write.WritableCellFormat;

public class ExcelCellInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int sheetNum;
	private int colNum;
	private int rowNum;
	private Object value;
	private String cellFormat;
	private transient WritableCellFormat wcellFormat;
	private boolean insertRow;
	private boolean insertColumn;
	private boolean deleteRow;
	private boolean deleteColumn;
	private boolean showCodeDesc;

	public ExcelCellInfo() {
	}

	public ExcelCellInfo(int sheetNum, String scolNum, String srowNum, Object value, String cellFormat) {
		this.sheetNum = sheetNum;
		colNum = (cervertExcelTodecimalist(scolNum) - 1);
		rowNum = (Integer.parseInt(srowNum) - 1);
		this.value = value;
		this.cellFormat = cellFormat;
	}

	public ExcelCellInfo(int sheetNum, int colNum, int rowNum, Object value, String cellFormat) {
		this.sheetNum = sheetNum;
		this.colNum = colNum;
		this.rowNum = rowNum;
		this.value = value;
		this.cellFormat = cellFormat;
	}

	public ExcelCellInfo(int sheetNum, String scolNum, String srowNum, Object value, String cellFormat,
			boolean insertRow, boolean insertColumn) {
		this.sheetNum = sheetNum;
		colNum = (cervertExcelTodecimalist(scolNum) - 1);
		rowNum = (Integer.parseInt(srowNum) - 1);
		this.value = value;
		this.cellFormat = cellFormat;
		this.insertRow = insertRow;
		this.insertColumn = insertColumn;
	}

	public ExcelCellInfo(int sheetNum, int colNum, int rowNum, Object value, String cellFormat, boolean insertRow,
			boolean insertColumn) {
		this.sheetNum = sheetNum;
		this.colNum = colNum;
		this.rowNum = rowNum;
		this.value = value;
		this.cellFormat = cellFormat;
		this.insertRow = insertRow;
		this.insertColumn = insertColumn;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getCellFormat() {
		return cellFormat;
	}

	public void setCellFormat(String cellFormat) {
		this.cellFormat = cellFormat;
	}

	public int getColNum() {
		return colNum;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
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

	public int getSheetNum() {
		return sheetNum;
	}

	public void setSheetNum(int sheetNum) {
		this.sheetNum = sheetNum;
	}

	public boolean isDeleteColumn() {
		return deleteColumn;
	}

	public boolean isDeleteRow() {
		return deleteRow;
	}

	public boolean isInsertColumn() {
		return insertColumn;
	}

	public boolean isInsertRow() {
		return insertRow;
	}

	public void setDeleteColumn(boolean deleteColumn) {
		this.deleteColumn = deleteColumn;
	}

	public void setDeleteRow(boolean deleteRow) {
		this.deleteRow = deleteRow;
	}

	public void setInsertColumn(boolean insertColumn) {
		this.insertColumn = insertColumn;
	}

	public void setInsertRow(boolean insertRow) {
		this.insertRow = insertRow;
	}

	public WritableCellFormat getWcellFormat() {
		return wcellFormat;
	}

	public void setWcellFormat(WritableCellFormat wcellFormat) {
		this.wcellFormat = wcellFormat;
	}

	public boolean isShowCodeDesc() {
		return showCodeDesc;
	}

	public void setShowCodeDesc(boolean showCodeDesc) {
		this.showCodeDesc = showCodeDesc;
	}
}
