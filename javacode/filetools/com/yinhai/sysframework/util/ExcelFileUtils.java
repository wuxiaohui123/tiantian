package com.yinhai.sysframework.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.fastjson.JSONArray;
import com.yinhai.sysframework.Reflect;
import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.exception.IllegalInputAppException;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.ibatis.CountStatementUtil;
import com.yinhai.sysframework.persistence.ibatis.IDao;
import com.yinhai.sysframework.print.ColumnInfo;
import com.yinhai.sysframework.print.ExcelCellInfo;
import com.yinhai.sysframework.print.SaveAsInfo;
import com.yinhai.sysframework.util.json.JSonFactory;

public class ExcelFileUtils {
	/**
	 * 行高系数
	 */
	private static final Double ROW_HEGIHT_CV = 15.625;
	/**
	 * 列宽系数
	 */
	private static final Double COL_HEGIHT_CV = 35.7;

	// --------------excel导入 BINGEN-----------------------------------//
	public static String[] SheetCellsToStringArray(Cell[] cells) {
		String[] resultArray = new String[cells.length];
		for (int i = 0; i < cells.length; i++) {
			resultArray[i] = cells[i].getContents();
		}
		return resultArray;
	}

	public static List<Map<String, String>> getSheetToMapList(Sheet sheet, Integer model, Map<String, Object> datamap, Integer rowStart,Integer rowEnd) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		rowEnd = rowEnd != null ? rowEnd : sheet.getRows();
		rowEnd = rowEnd > sheet.getRows() ? sheet.getRows() : rowEnd;
		for (int i = rowStart; i < rowEnd; i++) {
			map = new LinkedHashMap<String, String>();
			Cell[] cells = sheet.getRow(i);
			for (String key : datamap.keySet()) {
				int j = Integer.valueOf((String) datamap.get(key)) - 1;
				map.put(key, cells[j].getContents().trim() + "");
			}
			list.add(map);
		}
		return list;
	}

	public static List getExcelInputStream2ObjectList(InputStream is, String fieldNames, String domainClassName, boolean hasTitle) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List<Object> retList = new ArrayList<Object>();
		String[] fieldName = fieldNames.split(",");
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			Workbook book = Workbook.getWorkbook(is, workbookSettings);

			Sheet sheet = book.getSheet(0);

			if (hasTitle) {
				String cresult = "";
				Object tmpObj = null;
				for (int nn = 1; nn < sheet.getRows(); nn++) {
					String[] values = new String[fieldName.length];

					for (int mm = 0; mm < fieldName.length; mm++) {
						Cell cell1 = sheet.getCell(mm, nn);
						cresult = cell1.getContents();
						values[mm] = cresult.trim();
					}
					try {
						tmpObj = ReflectUtil.generatorObjectFromArray(fieldName, values, domainClassName);
						retList.add(tmpObj);
					} catch (Exception e) {
						e.printStackTrace();
						inputExp.addException(new AppException("在第" + (nn + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}
			} else {
				String cresult = "";
				Object tmpObj = null;
				for (int nn = 0; nn < sheet.getRows(); nn++) {
					String[] values = new String[fieldName.length];
					for (int mm = 0; mm < fieldName.length; mm++) {
						Cell cell1 = sheet.getCell(mm, nn);
						cresult = cell1.getContents();
						values[mm] = cresult.trim();
					}
					try {
						tmpObj = ReflectUtil.generatorObjectFromArray(fieldName, values, domainClassName);
						retList.add(tmpObj);
					} catch (Exception e) {
						e.printStackTrace();
						inputExp.addException(new AppException("在第" + (nn + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}

		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}

		return retList;
	}

	public static void dealExcelInputStream(InputStream is, int fieldCount, boolean hasTitle, ExcelReadRowHandler excelReadRowHandler)
			throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			Workbook book = Workbook.getWorkbook(is, workbookSettings);

			Sheet sheet = book.getSheet(0);

			if (hasTitle) {
				String cresult = "";
				for (int nn = 1; nn < sheet.getRows(); nn++) {
					String[] values = new String[fieldCount];

					for (int mm = 0; mm < fieldCount; mm++) {
						Cell cell1 = sheet.getCell(mm, nn);
						cresult = cell1.getContents();
						values[mm] = cresult.trim();
					}
					try {
						excelReadRowHandler.handerRow(values);
					} catch (Exception e) {
						inputExp.addException(new AppException("在第" + (nn + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}
			} else {
				String cresult = "";
				for (int nn = 0; nn < sheet.getRows(); nn++) {
					String[] values = new String[fieldCount];
					for (int mm = 0; mm < fieldCount; mm++) {
						Cell cell1 = sheet.getCell(mm, nn);
						cresult = cell1.getContents();
						values[mm] = cresult.trim();
					}
					try {
						excelReadRowHandler.handerRow(values);
					} catch (Exception e) {
						inputExp.addException(new AppException("在第" + (nn + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}
		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}
	}

	public static List getExcelInputStream2ObjectListWithParam(InputStream is, String fieldNames, String domainClassName, int rowStart, int rowEnd,
			int cellStart, int cellEnd) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List retList = new ArrayList();
		String[] fieldName = fieldNames.split(",");
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			Workbook book = Workbook.getWorkbook(is, workbookSettings);

			Sheet sheet = book.getSheet(0);

			rowStart = rowStart > 0 ? rowStart : 0;
			rowEnd = rowEnd > 0 ? rowEnd : 0;
			cellStart = cellStart > 0 ? cellStart : 0;
			cellEnd = cellEnd > 0 ? cellEnd : 0;
			String cresult = "";
			Object tmpObj = null;
			for (int nn = rowStart; nn < sheet.getRows() - rowEnd; nn++) {
				String[] values = new String[fieldName.length];

				for (int mm = cellStart; mm < fieldName.length + cellStart; mm++) {
					Cell cell1 = sheet.getCell(mm, nn);
					cresult = cell1.getContents();

					values[(mm - cellStart)] = cresult.trim();
				}
				try {
					tmpObj = ReflectUtil.generatorObjectFromArray(fieldName, values, domainClassName);
					retList.add(tmpObj);
				} catch (Exception e) {
					e.printStackTrace();
					inputExp.addException(new AppException("在第" + (nn + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}

		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}

		return retList;
	}

	public static ByteArrayOutputStream saveAsDomainListToExcelFile(HttpServletRequest request, HttpServletResponse response, SaveAsInfo saveAsInfo)
			throws IOException, WriteException {
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("ISO-8859-1");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ColumnInfo columnInfo = null;
		Object obj = new Object();
		Object value = null;
		String javaType = "";

		Label label = null;
		jxl.write.Number numberCell = null;
		DateTime dateCell = null;

		Formula formulaCell = null;
		Cell cell = null;

		WritableFont FONT = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				Colour.BLACK);

		WritableCellFormat LABEL_FORMAT = new WritableCellFormat(FONT);

		LABEL_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		DateFormat dtf = new DateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df = new DateFormat("yyyy-MM-dd");

		WritableCellFormat DATETIME_FORMAT = new WritableCellFormat(FONT, dtf);
		DATETIME_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);
		WritableCellFormat DATE_FORMAT = new WritableCellFormat(FONT, df);
		DATE_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		NumberFormat nf_long = new NumberFormat("#");
		NumberFormat nf_float = new NumberFormat("0.00");
		NumberFormat nf_China = new NumberFormat("￥#,##0.00");
		NumberFormat nf_science = new NumberFormat("#,##0.00");

		WritableCellFormat LONG_FORMAT = new WritableCellFormat(FONT, nf_long);
		LONG_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat FLOAT_FORMAT = new WritableCellFormat(FONT, nf_float);
		FLOAT_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat CHINA_FORMAT = new WritableCellFormat(FONT, nf_China);
		CHINA_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat SCIENCE_FORMAT = new WritableCellFormat(FONT, nf_science);
		SCIENCE_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		if (saveAsInfo.getStartPage() < 0) {
			saveAsInfo.setStartPage(0);
		}
		if (saveAsInfo.getStartRow() < 0) {
			saveAsInfo.setStartRow(0);
		}
		if (saveAsInfo.getStartColumn() < 0) {
			saveAsInfo.setStartColumn(0);
		}

		WritableWorkbook wwb = null;

		int pageCount = 1;
		int exPageSize = 65000;
		if (saveAsInfo.getDomainList().size() % exPageSize > 0) {
			pageCount = saveAsInfo.getDomainList().size() / exPageSize + 1;
		} else {
			pageCount = saveAsInfo.getDomainList().size() / exPageSize;
		}
		try {
			wwb = Workbook.createWorkbook(bos, workbookSettings);

			WritableSheet ws = null;
			Map mobj = null;
			Object tobj = null;
			String stype = "";
			Object cellValue = null;
			for (int sp = 0; sp < pageCount; sp++) {
				ws = wwb.createSheet("第" + (sp + 1) + "页", sp);

				ws.setProtected(saveAsInfo.isProtected());

				if ((saveAsInfo.isViewSubject()) && (saveAsInfo.getStartRow() > 0)) {
					ws.insertRow(saveAsInfo.getStartRow() - 1);
					int endColumnCount = saveAsInfo.getColumnList().size() - 1;
					ws.mergeCells(0, 0, endColumnCount, 0);
					WritableFont font1 = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
					WritableCellFormat format1 = new WritableCellFormat(font1);
					format1.setAlignment(Alignment.CENTRE);
					ws.addCell(new Label(0, 0, saveAsInfo.getSubjectStr(), format1));
				}

				if (saveAsInfo.isViewTitle()) {
					if ((saveAsInfo.getStartRow() >= 0) && (saveAsInfo.isAutoInsert())) {
						ws.insertRow(saveAsInfo.getStartRow());
					}

					for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
						int i_row = 0;
						int i_colummn = 0;
						if (saveAsInfo.getStartRow() >= 0) {
							i_row = saveAsInfo.getStartRow();
						} else {
							i_row = 0;
						}

						if (saveAsInfo.getStartColumn() >= 0) {
							i_colummn = saveAsInfo.getStartColumn() + i;
						} else {
							i_colummn = i;
						}

						columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
						label = new Label(i_colummn, i_row, columnInfo.getTitlecomment(), LABEL_FORMAT);
						ws.addCell(label);
					}
				}

				Reflect reflect = new Reflect(saveAsInfo.getDomainClass());

				int i = 0;
				for (int kk = sp * exPageSize; (saveAsInfo.getDomainList() != null) && (kk < (sp + 1) * exPageSize)
						&& (kk < saveAsInfo.getDomainList().size()); kk++) {
					int i_row = 0;
					int i_colummn = 0;
					if (saveAsInfo.getStartRow() >= 0) {
						if (saveAsInfo.isViewTitle()) {
							if (saveAsInfo.isAutoInsert()) {
								ws.insertRow(saveAsInfo.getStartRow() + i + 1);
							}
							i_row = saveAsInfo.getStartRow() + i;
						} else {
							if (saveAsInfo.isAutoInsert()) {
								ws.insertRow(saveAsInfo.getStartRow() + i);
							}
							i_row = saveAsInfo.getStartRow() + i - 1;
						}
					} else if (saveAsInfo.isViewTitle()) {
						i_row = i - 1;
					} else {
						i_row = i;
					}

					obj = saveAsInfo.getDomainList().get(kk);
					for (int j = 0; j < saveAsInfo.getColumnList().size(); j++) {
						if (saveAsInfo.getStartColumn() >= 0) {
							i_colummn = saveAsInfo.getStartColumn() + j;
						} else {
							i_colummn = j;
						}

						columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(j);

						if ((obj instanceof Map)) {
							mobj = (Map) obj;
							tobj = mobj.get(columnInfo.getFieldName());
							if (tobj != null) {
								value = tobj.toString();
							} else {
								value = "";
							}
						} else {
							value = reflect.getObjFieldValue(obj, columnInfo.getFieldName());
						}

						if (value == null) {
							value = "";
						}

						if (!saveAsInfo.isShowCode()) {
							if ((!value.equals(""))
									&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase().indexOf("string") > -1) || (reflect
											.getFieldType(columnInfo.getFieldName()).indexOf("Long") > -1))) {

								value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
							}
						} else if ((!columnInfo.isShowCode())
								&& (!value.equals(""))
								&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase().indexOf("string") > -1) || (reflect.getFieldType(
										columnInfo.getFieldName()).indexOf("Long") > -1))) {

							value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
						}

						if ((obj instanceof Map)) {
							stype = value.getClass().toString();
							if (stype.lastIndexOf(".") > 0) {
								javaType = stype.substring(stype.lastIndexOf("."));
							} else {
								javaType = stype;
							}
						} else {
							javaType = reflect.getFieldType(columnInfo.getFieldName());
						}

						if (!value.equals("")) {
							if (columnInfo.getFormat() == null) {
								if ((javaType.toLowerCase().indexOf("date") > -1) || (javaType.toLowerCase().indexOf("time") > -1)) {
									dateCell = new DateTime(i_colummn, i_row + 1, (Date) value, DATE_FORMAT);
									ws.addCell(dateCell);
								} else if ((javaType.toLowerCase().indexOf("int") > -1) || (javaType.toLowerCase().indexOf("long") > -1)
										|| (javaType.toLowerCase().indexOf("short") > -1) || (javaType.toLowerCase().indexOf("byte") > -1)
										|| (javaType.toLowerCase().indexOf("double") > -1) || (javaType.toLowerCase().indexOf("float") > -1)
										|| (javaType.toLowerCase().indexOf("big") > -1)) {

									if (value.toString().indexOf(".") > -1) {
										numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value.toString()), FLOAT_FORMAT);
									} else {
										numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value.toString()), LONG_FORMAT);
									}
									ws.addCell(numberCell);
								} else {
									label = new Label(i_colummn, i_row + 1, value.toString(), LABEL_FORMAT);
									ws.addCell(label);
								}

							} else if (columnInfo.getFormat().equals("DATETIME")) {
								dateCell = new DateTime(i_colummn, i_row + 1, (Date) value, DATETIME_FORMAT);
								ws.addCell(dateCell);
							} else if (columnInfo.getFormat().equals("DATE")) {
								dateCell = new DateTime(i_colummn, i_row + 1, (Date) value, DATE_FORMAT);
								ws.addCell(dateCell);
							} else if (columnInfo.getFormat().equals("LONG")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value.toString()), LONG_FORMAT);
								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("FLOAT")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value.toString()), FLOAT_FORMAT);
								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("CHINA")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value.toString()), CHINA_FORMAT);
								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("SCIENCE")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value.toString()), SCIENCE_FORMAT);
								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("NUMBER_FORMULA")) {
								formulaCell = new Formula(i_colummn, i_row + 1, String.valueOf(value), FLOAT_FORMAT);
								ws.addCell(formulaCell);
							} else if (columnInfo.getFormat().equals("STRING_FORMULA")) {
								formulaCell = new Formula(i_colummn, i_row + 1, String.valueOf(value), LABEL_FORMAT);
								ws.addCell(formulaCell);
							} else if (columnInfo.getFormat().equals("DATE_FORMULA")) {
								formulaCell = new Formula(i_colummn, i_row + 1, String.valueOf(value), DATE_FORMAT);
								ws.addCell(formulaCell);
							} else {
								label = new Label(i_colummn, i_row + 1, String.valueOf(value), LABEL_FORMAT);
								ws.addCell(label);
							}
						}
					}
					i++;
				}

				ExcelCellInfo excelCellInfo = null;
				for (int i1 = 0; (saveAsInfo.getCellList() != null) && (i1 < saveAsInfo.getCellList().size()); i1++) {
					excelCellInfo = (ExcelCellInfo) saveAsInfo.getCellList().get(i1);
					int sheetnum = excelCellInfo.getSheetNum();
					int col = excelCellInfo.getColNum();
					int row = excelCellInfo.getRowNum();
					cellValue = excelCellInfo.getValue();

					if (wwb.getNumberOfSheets() <= sheetnum) {
						ws = wwb.createSheet("new", sheetnum);
					} else {
						try {
							ws = wwb.getSheet(sheetnum);
						} catch (IndexOutOfBoundsException ex1) {
						}
					}

					if (excelCellInfo.isInsertRow()) {
						ws.insertRow(excelCellInfo.getRowNum());
					}

					if (excelCellInfo.isInsertColumn()) {
						ws.insertColumn(excelCellInfo.getColNum());
					}

					if (excelCellInfo.getCellFormat() == null) {
						cell = ws.getCell(col, row);

						if (cell == null) {
							label = new Label(col, row, String.valueOf(cellValue), LABEL_FORMAT);
							ws.addCell(label);
						} else if ((cell.getType().equals(CellType.EMPTY)) || (cell.getType().equals(CellType.ERROR))
								|| (cell.getType().equals(CellType.FORMULA_ERROR))) {

							label = new Label(col, row, String.valueOf(cellValue), LABEL_FORMAT);

							ws.addCell(label);
						} else if (cell.getType().equals(CellType.DATE)) {
							((DateTime) cell).setDate((Date) cellValue);
						} else if (cell.getType().equals(CellType.NUMBER)) {
							((jxl.write.Number) cell).setValue(cellValue == null ? 0.0D : ((Double) cellValue).doubleValue());
						} else if (cell.getType().equals(CellType.LABEL)) {
							((Label) cell).setString(String.valueOf(cellValue));
						} else if ((cell.getType() == CellType.NUMBER_FORMULA) || (cell.getType() == CellType.STRING_FORMULA)
								|| (cell.getType() == CellType.DATE_FORMULA) || (cell.getType() == CellType.BOOLEAN_FORMULA)) {

							CellFormat fcf = ((Formula) cell).getCellFormat();
							formulaCell = new Formula(col, row, String.valueOf(cellValue), fcf);
							ws.addCell(formulaCell);
						} else {
							label = new Label(col, row, String.valueOf(cellValue), LABEL_FORMAT);
							ws.addCell(label);
						}
					} else if (excelCellInfo.getCellFormat().equals("DATETIME")) {
						dateCell = new DateTime(col, row, (Date) cellValue, DATETIME_FORMAT);
						ws.addCell(dateCell);
					} else if (excelCellInfo.getCellFormat().equals("DATE")) {
						dateCell = new DateTime(col, row, (Date) cellValue, DATE_FORMAT);
						ws.addCell(dateCell);
					} else if (excelCellInfo.getCellFormat().equals("LONG")) {
						numberCell = new jxl.write.Number(col, row, ((Double) cellValue).doubleValue(), LONG_FORMAT);
						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("FLOAT")) {
						numberCell = new jxl.write.Number(col, row, ((Double) cellValue).doubleValue(), FLOAT_FORMAT);
						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("CHINA")) {
						numberCell = new jxl.write.Number(col, row, ((Double) cellValue).doubleValue(), CHINA_FORMAT);
						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("SCIENCE")) {
						numberCell = new jxl.write.Number(col, row, ((Double) cellValue).doubleValue(), SCIENCE_FORMAT);
						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("NUMBER_FORMULA")) {
						formulaCell = new Formula(col, row, String.valueOf(cellValue), FLOAT_FORMAT);
						ws.addCell(formulaCell);
					} else if (excelCellInfo.getCellFormat().equals("STRING_FORMULA")) {
						formulaCell = new Formula(col, row, String.valueOf(cellValue), LABEL_FORMAT);
						ws.addCell(formulaCell);
					} else if (excelCellInfo.getCellFormat().equals("DATE_FORMULA")) {
						formulaCell = new Formula(col, row, String.valueOf(cellValue), DATE_FORMAT);
						ws.addCell(formulaCell);
					} else {
						label = new Label(col, row, String.valueOf(cellValue), LABEL_FORMAT);
						ws.addCell(label);
					}
				}
			}

			for (int tt = 0; tt < saveAsInfo.getColumnList().size(); tt++) {
				ColumnInfo column = (ColumnInfo) saveAsInfo.getColumnList().get(tt);
				ws.setColumnView(tt, column.getWidth());
			}
		} catch (WriteException ex) {
			ex.printStackTrace();
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			wwb.write();
			wwb.close();
		}
		return bos;
	}

	public static ByteArrayOutputStream saveAsResultSetToExcelFile(HttpServletRequest request, HttpServletResponse response, SaveAsInfo saveAsInfo)
			throws IOException, WriteException {
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("ISO-8859-1");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ColumnInfo columnInfo = null;
		Object value = null;
		String strValue = "";
		String javaType = "";

		InputStream srcIs = null;
		Label label = null;
		jxl.write.Number numberCell = null;
		DateTime dateCell = null;

		Formula formulaCell = null;
		Cell cell = null;

		WritableFont FONT = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				Colour.BLACK);

		WritableCellFormat LABEL_FORMAT = new WritableCellFormat(FONT);

		LABEL_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		DateFormat dtf = new DateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df = new DateFormat("yyyy-MM-dd");

		WritableCellFormat DATETIME_FORMAT = new WritableCellFormat(FONT, dtf);
		DATETIME_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);
		WritableCellFormat DATE_FORMAT = new WritableCellFormat(FONT, df);
		DATE_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		NumberFormat nf_long = new NumberFormat("#");
		NumberFormat nf_float = new NumberFormat("0.00");
		NumberFormat nf_China = new NumberFormat("￥#,##0.00");
		NumberFormat nf_science = new NumberFormat("#,##0.00");

		WritableCellFormat LONG_FORMAT = new WritableCellFormat(FONT, nf_long);
		LONG_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat FLOAT_FORMAT = new WritableCellFormat(FONT, nf_float);
		FLOAT_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat CHINA_FORMAT = new WritableCellFormat(FONT, nf_China);
		CHINA_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat SCIENCE_FORMAT = new WritableCellFormat(FONT, nf_science);
		SCIENCE_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		if (saveAsInfo.getStartPage() < 0) {
			saveAsInfo.setStartPage(0);
		}
		if (saveAsInfo.getStartRow() < 0) {
			saveAsInfo.setStartRow(0);
		}
		if (saveAsInfo.getStartColumn() < 0) {
			saveAsInfo.setStartColumn(0);
		}

		Workbook sourcewb = null;

		WritableWorkbook wwb = null;
		try {
			wwb = Workbook.createWorkbook(bos, workbookSettings);

			WritableSheet ws = null;

			ResultSet rs = saveAsInfo.getRs();

			Reflect reflect = new Reflect(saveAsInfo.getDomainClass());

			int rr = -1;
			int pageCount = 0;
			while (rs.next()) {
				rr += 1;
				int exPageMax = 65000;
				if (rr % exPageMax == 0) {
					pageCount += 1;
					rr = 1;
					ws = wwb.createSheet("第" + pageCount + "页", pageCount - 1);

					ws.setProtected(saveAsInfo.isProtected());

					if ((saveAsInfo.isViewSubject()) && (saveAsInfo.getStartRow() > 0)) {
						ws.insertRow(saveAsInfo.getStartRow() - 1);
						int endColumnCount = saveAsInfo.getColumnList().size() - 1;
						ws.mergeCells(0, 0, endColumnCount, 0);
						WritableFont font1 = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
						WritableCellFormat format1 = new WritableCellFormat(font1);
						format1.setAlignment(Alignment.CENTRE);
						ws.addCell(new Label(0, 0, saveAsInfo.getSubjectStr(), format1));
					}

					if (saveAsInfo.isViewTitle()) {
						if ((saveAsInfo.getStartRow() >= 0) && (saveAsInfo.isAutoInsert())) {
							ws.insertRow(saveAsInfo.getStartRow());
						}

						for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
							int i_row = 0;
							int i_colummn = 0;
							if (saveAsInfo.getStartRow() >= 0) {
								i_row = saveAsInfo.getStartRow();
							} else {
								i_row = 0;
							}

							if (saveAsInfo.getStartColumn() >= 0) {
								i_colummn = saveAsInfo.getStartColumn() + i;
							} else {
								i_colummn = i;
							}

							columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
							label = new Label(i_colummn, i_row, columnInfo.getTitlecomment(), LABEL_FORMAT);
							ws.addCell(label);
						}
					}
				}
				int i_row = 0;
				int i_colummn = 0;
				if (saveAsInfo.getStartRow() >= 0) {
					if (saveAsInfo.isViewTitle()) {
						if (saveAsInfo.isAutoInsert()) {
							ws.insertRow(saveAsInfo.getStartRow() + rr + 1);
						}
						i_row = saveAsInfo.getStartRow() + rr;
					} else {
						if (saveAsInfo.isAutoInsert()) {
							ws.insertRow(saveAsInfo.getStartRow() + rr);
						}
						i_row = saveAsInfo.getStartRow() + rr - 1;
					}
				} else {
					i_row = 1;
				}

				for (int j = 0; j < saveAsInfo.getColumnList().size(); j++) {
					if (saveAsInfo.getStartColumn() >= 0) {
						i_colummn = saveAsInfo.getStartColumn() + j;
					} else {
						i_colummn = j;
					}

					columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(j);

					value = rs.getObject(columnInfo.getFieldName());
					if (value == null) {
						value = "";
					}

					if ((!columnInfo.isShowCode())
							&& (!value.equals(""))
							&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase().indexOf("string") > -1) || (reflect.getFieldType(
									columnInfo.getFieldName()).indexOf("Long") > -1))) {

						value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
					}

					javaType = reflect.getFieldType(columnInfo.getFieldName());
					if (!value.equals("")) {
						if (columnInfo.getFormat() == null) {
							if ((javaType.toLowerCase().indexOf("date") > -1) || (javaType.toLowerCase().indexOf("time") > -1)) {
								dateCell = new DateTime(i_colummn, i_row, (Date) value, DATE_FORMAT);
								ws.addCell(dateCell);
							} else if ((javaType.toLowerCase().indexOf("int") > -1) || (javaType.toLowerCase().indexOf("long") > -1)
									|| (javaType.toLowerCase().indexOf("short") > -1) || (javaType.toLowerCase().indexOf("byte") > -1)
									|| (javaType.toLowerCase().indexOf("double") > -1) || (javaType.toLowerCase().indexOf("float") > -1)
									|| (javaType.toLowerCase().indexOf("big") > -1)) {

								if (value.toString().indexOf(".") > -1) {
									numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), FLOAT_FORMAT);
								} else {
									numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), LONG_FORMAT);
								}
								ws.addCell(numberCell);
							} else {
								label = new Label(i_colummn, i_row, value.toString(), LABEL_FORMAT);
								ws.addCell(label);
							}

						} else if (columnInfo.getFormat().equals("DATETIME")) {
							dateCell = new DateTime(i_colummn, i_row, (Date) value, DATETIME_FORMAT);
							ws.addCell(dateCell);
						} else if (columnInfo.getFormat().equals("DATE")) {
							dateCell = new DateTime(i_colummn, i_row, (Date) value, DATE_FORMAT);
							ws.addCell(dateCell);
						} else if (columnInfo.getFormat().equals("LONG")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), LONG_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("FLOAT")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), FLOAT_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("CHINA")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), CHINA_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("SCIENCE")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), SCIENCE_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("NUMBER_FORMULA")) {
							formulaCell = new Formula(i_colummn, i_row, String.valueOf(value), FLOAT_FORMAT);
							ws.addCell(formulaCell);
						} else if (columnInfo.getFormat().equals("STRING_FORMULA")) {
							formulaCell = new Formula(i_colummn, i_row, String.valueOf(value), LABEL_FORMAT);
							ws.addCell(formulaCell);
						} else if (columnInfo.getFormat().equals("DATE_FORMULA")) {
							formulaCell = new Formula(i_colummn, i_row, String.valueOf(value), DATE_FORMAT);
							ws.addCell(formulaCell);
						} else {
							label = new Label(i_colummn, i_row, String.valueOf(value), LABEL_FORMAT);
							ws.addCell(label);
						}

					}
				}
			}
		} catch (WriteException ex) {
			ex.printStackTrace();
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			wwb.write();
			wwb.close();
		}
		return bos;
	}

	public static FileInputStream saveAsResultSetToExcelFile2(HttpServletRequest request, HttpServletResponse response, SaveAsInfo saveAsInfo)
			throws IOException, WriteException {
		File f = new File(System.getProperty("java.io.tmpdir"));
		File ftemp = File.createTempFile("xlsletter" + Math.random(), ".xls", f);
		FileOutputStream fos = new FileOutputStream(ftemp);

		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("ISO-8859-1");

		ColumnInfo columnInfo = null;
		Object value = null;
		String strValue = "";
		String javaType = "";

		InputStream srcIs = null;
		Label label = null;
		jxl.write.Number numberCell = null;
		DateTime dateCell = null;

		Formula formulaCell = null;
		Cell cell = null;

		WritableFont FONT = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				Colour.BLACK);

		WritableCellFormat LABEL_FORMAT = new WritableCellFormat(FONT);

		LABEL_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		DateFormat dtf = new DateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df = new DateFormat("yyyy-MM-dd");

		WritableCellFormat DATETIME_FORMAT = new WritableCellFormat(FONT, dtf);
		DATETIME_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);
		WritableCellFormat DATE_FORMAT = new WritableCellFormat(FONT, df);
		DATE_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		NumberFormat nf_long = new NumberFormat("#");
		NumberFormat nf_float = new NumberFormat("0.00");
		NumberFormat nf_China = new NumberFormat("￥#,##0.00");
		NumberFormat nf_science = new NumberFormat("#,##0.00");

		WritableCellFormat LONG_FORMAT = new WritableCellFormat(FONT, nf_long);
		LONG_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat FLOAT_FORMAT = new WritableCellFormat(FONT, nf_float);
		FLOAT_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat CHINA_FORMAT = new WritableCellFormat(FONT, nf_China);
		CHINA_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		WritableCellFormat SCIENCE_FORMAT = new WritableCellFormat(FONT, nf_science);
		SCIENCE_FORMAT.setBorder(Border.ALL, BorderLineStyle.THIN);

		if (saveAsInfo.getStartPage() < 0) {
			saveAsInfo.setStartPage(0);
		}
		if (saveAsInfo.getStartRow() < 0) {
			saveAsInfo.setStartRow(0);
		}
		if (saveAsInfo.getStartColumn() < 0) {
			saveAsInfo.setStartColumn(0);
		}

		WritableWorkbook wwb = null;

		try {
			wwb = Workbook.createWorkbook(fos, workbookSettings);

			WritableSheet ws = null;

			ResultSet rs = saveAsInfo.getRs();

			Reflect reflect = new Reflect(saveAsInfo.getDomainClass());

			int rr = -1;
			int pageCount = 0;
			while (rs.next()) {
				rr += 1;
				int exPageMax = 65000;
				if (rr % exPageMax == 0) {
					pageCount += 1;
					rr = 1;
					ws = wwb.createSheet("第" + pageCount + "页", pageCount - 1);

					ws.setProtected(saveAsInfo.isProtected());

					if ((saveAsInfo.isViewSubject()) && (saveAsInfo.getStartRow() > 0)) {
						ws.insertRow(saveAsInfo.getStartRow() - 1);
						int endColumnCount = saveAsInfo.getColumnList().size() - 1;
						ws.mergeCells(0, 0, endColumnCount, 0);
						WritableFont font1 = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);
						WritableCellFormat format1 = new WritableCellFormat(font1);
						format1.setAlignment(Alignment.CENTRE);
						ws.addCell(new Label(0, 0, saveAsInfo.getSubjectStr(), format1));
					}

					if (saveAsInfo.isViewTitle()) {
						if ((saveAsInfo.getStartRow() >= 0) && (saveAsInfo.isAutoInsert())) {
							ws.insertRow(saveAsInfo.getStartRow());
						}

						for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
							int i_row = 0;
							int i_colummn = 0;
							if (saveAsInfo.getStartRow() >= 0) {
								i_row = saveAsInfo.getStartRow();
							} else {
								i_row = 0;
							}

							if (saveAsInfo.getStartColumn() >= 0) {
								i_colummn = saveAsInfo.getStartColumn() + i;
							} else {
								i_colummn = i;
							}

							columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
							label = new Label(i_colummn, i_row, columnInfo.getTitlecomment(), LABEL_FORMAT);
							ws.addCell(label);
						}
					}
				}
				int i_row = 0;
				int i_colummn = 0;
				if (saveAsInfo.getStartRow() >= 0) {
					if (saveAsInfo.isViewTitle()) {
						if (saveAsInfo.isAutoInsert()) {
							ws.insertRow(saveAsInfo.getStartRow() + rr + 1);
						}
						i_row = saveAsInfo.getStartRow() + rr;
					} else {
						if (saveAsInfo.isAutoInsert()) {
							ws.insertRow(saveAsInfo.getStartRow() + rr);
						}
						i_row = saveAsInfo.getStartRow() + rr - 1;
					}
				} else {
					i_row = 1;
				}

				for (int j = 0; j < saveAsInfo.getColumnList().size(); j++) {
					if (saveAsInfo.getStartColumn() >= 0) {
						i_colummn = saveAsInfo.getStartColumn() + j;
					} else {
						i_colummn = j;
					}

					columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(j);

					value = rs.getObject(columnInfo.getFieldName());
					if (value == null) {
						value = "";
					}

					if ((!columnInfo.isShowCode())
							&& (!value.equals(""))
							&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase().indexOf("string") > -1) || (reflect.getFieldType(
									columnInfo.getFieldName()).indexOf("Long") > -1))) {

						value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
					}

					javaType = reflect.getFieldType(columnInfo.getFieldName());
					if (!value.equals("")) {
						if (columnInfo.getFormat() == null) {
							if ((javaType.toLowerCase().indexOf("date") > -1) || (javaType.toLowerCase().indexOf("time") > -1)) {
								dateCell = new DateTime(i_colummn, i_row, (Date) value, DATE_FORMAT);
								ws.addCell(dateCell);
							} else if ((javaType.toLowerCase().indexOf("int") > -1) || (javaType.toLowerCase().indexOf("long") > -1)
									|| (javaType.toLowerCase().indexOf("short") > -1) || (javaType.toLowerCase().indexOf("byte") > -1)
									|| (javaType.toLowerCase().indexOf("double") > -1) || (javaType.toLowerCase().indexOf("float") > -1)
									|| (javaType.toLowerCase().indexOf("big") > -1)) {

								if (value.toString().indexOf(".") > -1) {
									numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), FLOAT_FORMAT);
								} else {
									numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), LONG_FORMAT);
								}
								ws.addCell(numberCell);
							} else {
								label = new Label(i_colummn, i_row, value.toString(), LABEL_FORMAT);
								ws.addCell(label);
							}

						} else if (columnInfo.getFormat().equals("DATETIME")) {
							dateCell = new DateTime(i_colummn, i_row, (Date) value, DATETIME_FORMAT);
							ws.addCell(dateCell);
						} else if (columnInfo.getFormat().equals("DATE")) {
							dateCell = new DateTime(i_colummn, i_row, (Date) value, DATE_FORMAT);
							ws.addCell(dateCell);
						} else if (columnInfo.getFormat().equals("LONG")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), LONG_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("FLOAT")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), FLOAT_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("CHINA")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), CHINA_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("SCIENCE")) {
							numberCell = new jxl.write.Number(i_colummn, i_row, Double.parseDouble(value.toString()), SCIENCE_FORMAT);
							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("NUMBER_FORMULA")) {
							formulaCell = new Formula(i_colummn, i_row, String.valueOf(value), FLOAT_FORMAT);
							ws.addCell(formulaCell);
						} else if (columnInfo.getFormat().equals("STRING_FORMULA")) {
							formulaCell = new Formula(i_colummn, i_row, String.valueOf(value), LABEL_FORMAT);
							ws.addCell(formulaCell);
						} else if (columnInfo.getFormat().equals("DATE_FORMULA")) {
							formulaCell = new Formula(i_colummn, i_row, String.valueOf(value), DATE_FORMAT);
							ws.addCell(formulaCell);
						} else {
							label = new Label(i_colummn, i_row, String.valueOf(value), LABEL_FORMAT);
							ws.addCell(label);
						}
					}
				}
			}
		} catch (WriteException ex) {
			ex.printStackTrace();
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			wwb.write();
			wwb.close();
			FileInputStream fis = new FileInputStream(ftemp);
			ftemp.deleteOnExit();
			return fis;
		}
	}

	public static String getCodeDesc(HttpServletRequest request, String codeType, String codeValue) {
		Vector vector = (Vector) request.getSession().getServletContext().getAttribute(codeType);
		String code = "";
		if ((vector == null) || (vector.size() == 0)) {
			return codeValue;
		}

		for (int i = 0; i < vector.size(); i++) {
			code = ((AppCode) vector.get(i)).getCodeValue();
			if ((code != null) && (code.equals(codeValue))) {
				return ((AppCode) vector.get(i)).getCodeDESC();
			}
		}

		return codeValue;
	}

	private static String getTempFileName() {
		String tempDir = System.getProperty("java.io.tmpdir");
		String tempFileName = String.valueOf(Math.random()).substring(2, 8) + ".pdf";
		tempFileName = tempDir + tempFileName;
		while (new File(tempFileName).exists()) {
			tempFileName = String.valueOf(Math.random()).substring(2, 8) + ".pdf";
			tempFileName = tempDir + tempFileName;
		}
		return tempFileName;
	}

	private static List<Map> getWorkbookSheetList(InputStream is) {
		List<Map> list = new ArrayList<Map>();
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			Workbook book = Workbook.getWorkbook(is, workbookSettings);
			Sheet[] sheets = book.getSheets();
			for (Sheet sheet : sheets) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("SheetId", sheet.getColumns());
				m.put("sheetName", sheet.getName());
				list.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// --------------excel导入 END-----------------------------------//

	// --------------excel导出 BINGEN-----------------------------------//
	public static HSSFWorkbook getGridDataToWorkbook(HttpServletRequest request, ParamDTO dto, String parameter) {
		JSONArray json2bean = (JSONArray) JSonFactory.json2bean(parameter.toString(), JSONArray.class);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");
		String isHasTableTitle = request.getParameter("Set0");
		String isHasColumnHead = request.getParameter("Set1");
		String isHasCreateInfo = request.getParameter("Set2");
		int startRow = 0;// sheet开始行号
		if ("1".equals(isHasTableTitle)) {// 是否包含表标题
			String title = request.getParameter("tableTitle");// 表格标题
			int colNum = ((JSONArray) json2bean.get(0)).size() - 1;// 列数
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colNum));// 合并单元格
			HSSFRow eTitle = sheet.createRow(0);
			eTitle.setHeightInPoints(35.625f);
			HSSFCell TitleCell = eTitle.createCell(0);
			HSSFCellStyle cellTStyle = getTableTitleStyle(workbook, TitleCell);
			TitleCell.setCellStyle(cellTStyle);
			TitleCell.setCellValue(new HSSFRichTextString(title));
			startRow++;
		}
		if ("1".equals(isHasCreateInfo)) {// 是否添加创建信息
			IUser user = dto.getUserInfo();
			String informaction = "informaction// 用户名：" + user.getName() + " 岗位：" + user.getNowPosition().getPositionname();
			informaction += " 创建时间：" + DateUtil.getCurDateTime();
			int colNum = ((JSONArray) json2bean.get(0)).size() - 1;// 列数
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, colNum));// 合并单元格
			HSSFRow createInfoRow = sheet.createRow(1);
			createInfoRow.setHeightInPoints((short) 16);
			HSSFCell creInfoCell = createInfoRow.createCell(0);
			HSSFCellStyle cellInfoTStyle = getCreateInfoStyle(workbook);
			creInfoCell.setCellStyle(cellInfoTStyle);
			creInfoCell.setCellValue(new HSSFRichTextString(informaction));
			startRow++;
		}
		ArrayList<Object> list = null;
		for (int i = 0; i < json2bean.size(); i++) {
			HSSFRow row = sheet.createRow(i + startRow);
			list = new ArrayList<Object>((JSONArray) json2bean.get(i));
			if ("1".equals(isHasColumnHead) && i == 0) {
				row.setHeightInPoints(30);
				HSSFCellStyle headStyle = getColumnHeadStyle(workbook);
				setColumnHeadAndStyle(sheet, list, row, headStyle);
			} else {
				row.setHeightInPoints(25);
				HSSFCellStyle dataStyle = getDataRowStyle(workbook);
				setDataRowAndStyle(sheet, list, row, dataStyle);
			}
		}
		return workbook;
	}

	public static HSSFCellStyle getCreateInfoStyle(HSSFWorkbook workbook) {
		// 样式
		HSSFCellStyle cellInfoTStyle = workbook.createCellStyle();
		cellInfoTStyle.setBorderLeft(BorderStyle.THIN);
		cellInfoTStyle.setBorderTop(BorderStyle.THIN);
		cellInfoTStyle.setBorderRight(BorderStyle.THIN);
		cellInfoTStyle.setBorderBottom(BorderStyle.THIN);
		cellInfoTStyle.setAlignment(HorizontalAlignment.RIGHT);// 水平靠右
		// 字体
		HSSFFont cellInfoFont = workbook.createFont();
		cellInfoFont.setFontName("微软雅黑");
		cellInfoFont.setFontHeightInPoints((short) 10);// 10像素
		cellInfoTStyle.setFont(cellInfoFont);
		return cellInfoTStyle;
	}

	public static HSSFCellStyle getTableTitleStyle(HSSFWorkbook workbook, HSSFCell TitleCell) {
		// 样式
		HSSFCellStyle cellTStyle = workbook.createCellStyle();
		cellTStyle.setBorderLeft(BorderStyle.THIN);
		cellTStyle.setBorderTop(BorderStyle.THIN);
		cellTStyle.setBorderRight(BorderStyle.THIN);
		cellTStyle.setBorderBottom(BorderStyle.THIN);

		cellTStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
		cellTStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
		// 字体
		HSSFFont cellFont = workbook.createFont();
		cellFont.setFontName("微软雅黑");
		cellFont.setFontHeightInPoints((short) 16);// 16px
		cellFont.setBold(true);// 加粗
		cellTStyle.setFont(cellFont);
		// 设置自动换行
		cellTStyle.setWrapText(false);
		return cellTStyle;

	}

	public static void setDataRowAndStyle(HSSFSheet sheet, ArrayList<Object> list, HSSFRow row, HSSFCellStyle dataStyle) {
		for (int j = 0; j < list.size(); j++) {
			// 设置列宽
			sheet.setColumnWidth(j, 140 * COL_HEGIHT_CV.intValue());
			HSSFCell cell = row.createCell(j);
			cell.setCellStyle(dataStyle);
			HSSFRichTextString text = new HSSFRichTextString(list.get(j).toString());
			cell.setCellValue(text);
		}
	}

	public static void setColumnHeadAndStyle(HSSFSheet sheet, ArrayList<Object> list, HSSFRow row, HSSFCellStyle headStyle) {
		for (int j = 0; j < list.size(); j++) {
			// 设置列宽
			sheet.setColumnWidth(j, 140 * COL_HEGIHT_CV.intValue());
			HSSFCell cell = row.createCell(j);
			cell.setCellStyle(headStyle);
			HSSFRichTextString text = new HSSFRichTextString(list.get(j).toString());
			cell.setCellValue(text);
		}
	}

	public static HSSFCellStyle getDataRowStyle(HSSFWorkbook workbook) {
		HSSFCellStyle dataStyle = workbook.createCellStyle();
		// 数据边框
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderTop(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);
		dataStyle.setBorderBottom(BorderStyle.THIN);
		// 数据边框颜色
		dataStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		dataStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		dataStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		dataStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		// 数据对齐方式
		dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直对齐方式
		dataStyle.setAlignment(HorizontalAlignment.CENTER);// 水平对齐方式
		return dataStyle;
	}

	/**
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getColumnHeadStyle(HSSFWorkbook workbook) {
		// 表头样式
		HSSFCellStyle Headstyle = workbook.createCellStyle();
		Headstyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直对齐方式
		Headstyle.setAlignment(HorizontalAlignment.CENTER);// 水平对齐方式
		// 表头字体
		HSSFFont HeadFont = workbook.createFont();
		HeadFont.setFontName("微软雅黑");
		HeadFont.setFontHeightInPoints((short) 13);
		HeadFont.setBold(true);
		HSSFPalette headPalette = workbook.getCustomPalette();
		headPalette.setColorAtIndex((short) 15, (byte) 255, (byte) 255, (byte) 255);
		HeadFont.setColor((short) 15);
		Headstyle.setFont(HeadFont);
		// 表头边框
		Headstyle.setBorderLeft(BorderStyle.MEDIUM);
		Headstyle.setBorderTop(BorderStyle.MEDIUM);
		Headstyle.setBorderRight(BorderStyle.MEDIUM);
		Headstyle.setBorderBottom(BorderStyle.MEDIUM);
		// 表头边框颜色
		Headstyle.setLeftBorderColor(HSSFColor.BLACK.index);
		Headstyle.setTopBorderColor(HSSFColor.BLACK.index);
		Headstyle.setRightBorderColor(HSSFColor.BLACK.index);
		Headstyle.setBottomBorderColor(HSSFColor.BLACK.index);
		// 表头背景颜色
		Headstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);// 纯色填充
		headPalette.setColorAtIndex((short) 16, (byte) 91, (byte) 155, (byte) 213);
		Headstyle.setFillForegroundColor((short) 16);
		return Headstyle;
	}

	public static HSSFWorkbook getGridAllDataToWorkbook(ParamDTO dto, IDao dao) throws Exception {
		String gridHead = dto.getAsString("_gridHead_");
		if (("".equals(gridHead.trim())) || (gridHead == null)) {
			throw new AppException("没有表头");
		}
		JSONArray json2bean = (JSONArray) JSonFactory.json2bean(gridHead.toString(), JSONArray.class);

		List headName = (List) json2bean.get(0);

		List headId = (List) json2bean.get(1);

		List sqlstatment = (List) json2bean.get(2);

		List resultType = (List) json2bean.get(3);

		List collection = (List) json2bean.get(4);

		byte[] decryptData = DESCoderUtil.decryptBASE64((String) sqlstatment.get(0));
		String sqlStatementName = new String(DESCoderUtil.decrypt(decryptData, "reYj6fIsWGE="));

		// List list = getDao().queryForList(sqlStatementName, dto);
		int count = (int) CountStatementUtil.autoGetTotalCount(sqlStatementName, dto, dao);
		List list = dao.queryForPage(sqlStatementName, dto, 0, count);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");
		int startRow = 0;// sheet开始行号
		Integer isHasTableTitle = dto.getAsInteger("Set0");
		Integer isHasColumnHead = dto.getAsInteger("Set1");
		Integer isHasCreateInfo = dto.getAsInteger("Set2");
		if (isHasTableTitle == 1) {// 是否包含表标题
			String title = dto.getAsString("tableTitle");// 表格标题
			int colNum = headName.size() - 1;// 列数
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colNum));// 合并单元格
			HSSFRow eTitle = sheet.createRow(0);
			eTitle.setHeightInPoints(35.625f);
			HSSFCell TitleCell = eTitle.createCell(0);
			HSSFCellStyle cellTStyle = getTableTitleStyle(workbook, TitleCell);
			TitleCell.setCellStyle(cellTStyle);
			TitleCell.setCellValue(new HSSFRichTextString(title));
			startRow++;
		}
		if (isHasCreateInfo == 1) {// 是否添加创建信息
			IUser user = dto.getUserInfo();
			String informaction = "informaction// 用户名：" + user.getName() + " 岗位：" + user.getNowPosition().getPositionname();
			informaction += " 创建时间：" + DateUtil.getCurDateTime();
			int colNum = headName.size() - 1;// 列数
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, colNum));// 合并单元格
			HSSFRow createInfoRow = sheet.createRow(1);
			createInfoRow.setHeightInPoints((short) 16);
			HSSFCell creInfoCell = createInfoRow.createCell(0);
			HSSFCellStyle cellInfoTStyle = getCreateInfoStyle(workbook);
			creInfoCell.setCellStyle(cellInfoTStyle);
			creInfoCell.setCellValue(new HSSFRichTextString(informaction));
			startRow++;
		}
		if (isHasColumnHead == 1) {
			HSSFRow row = sheet.createRow(2);
			row.setHeightInPoints(30);
			HSSFCellStyle headStyle = getColumnHeadStyle(workbook);
			setColumnHeadAndStyle(sheet, new ArrayList(headName), row, headStyle);
			startRow++;
		}
		Class c = Class.forName((String) resultType.get(0));
		Object obj = c.newInstance();
		Map map = new HashMap();
		BaseDomain baseDomain = null;
		String yab003 = "9999";
		String desc = "";
		HSSFCellStyle dataStyle = getDataRowStyle(workbook);
		for (int i = 0; i < list.size(); i++) {
			HSSFRow row = sheet.createRow(i + startRow);
			obj = list.get(i);
			if ((obj instanceof BaseDomain)) {
				baseDomain = (BaseDomain) obj;
				map = baseDomain.toMap();
			}
			if ((obj instanceof Map)) {
				map = (Map) obj;
			}
			IUser user = dto.getUserInfo();
			if (user != null) {
				yab003 = user.getYab003();
			}

			for (int j = 0; j < headName.size(); j++) {
				HSSFCell cell = row.createCell(j);
				cell.setCellStyle(dataStyle);
				HSSFRichTextString text = null;
				if (("".equals(map.get(headId.get(j)))) || (map.get(headId.get(j)) == null)) {
					text = new HSSFRichTextString("");
				} else if (collection.size() > 0) {
					for (int k = 0; k < collection.size(); k++) {
						if (headId.get(j).equals(collection.get(k))) {
							desc = CodeTableLocator.getCodeDesc((String) headId.get(j), map.get(headId.get(j)).toString(), yab003);
							text = new HSSFRichTextString(desc);
							break;
						}
						if (k == collection.size() - 1) {
							text = new HSSFRichTextString(map.get(headId.get(j)).toString());
						}
					}
				} else {
					text = new HSSFRichTextString(map.get(headId.get(j)).toString());
				}
				cell.setCellValue(text);
			}
		}
		return workbook;
	}
	// --------------excel导出 END-----------------------------------//
}
