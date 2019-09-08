 package com.yinhai.sysframework.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.yinhai.sysframework.Reflect;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.exception.IllegalInputAppException;
import com.yinhai.sysframework.print.ColumnInfo;
import com.yinhai.sysframework.print.ExcelCellInfo;
import com.yinhai.sysframework.print.SaveAsInfo;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.Resources;

public class FilePrintUtils {

	public static final String GBK_CODE = "GBK";

	public static String getTxtInputStreamToString(InputStream is) {
		StringBuilder txt = new StringBuilder();
		try {
			int i = is.read();
			while (i != -1) {
				txt.append((char) i);
				i = is.read();
			}
			is.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return txt.toString();
	}

	public static String getTxtInputStreamToStringByLine(InputStream is) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));

		int linenum = 0;
		try {
			String line = br.readLine();

			while (line != null) {
				linenum += 1;
				sb.append(line).append(" \n");
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static byte[] getInputStreamToByte(InputStream tis) {
		InputStream is = tis;
		byte[] tmp = new byte['?'];
		byte[] data = null;
		int len = 0;
		try {
			int sz;
			while ((sz = is.read(tmp)) != -1) {
				if (data == null) {
					len = sz;
					data = tmp;

				} else {
					int nlen = len + sz;
					byte[] narr = new byte[nlen];
					System.arraycopy(data, 0, narr, 0, len);
					System.arraycopy(tmp, 0, narr, len, sz);
					data = narr;
					len = nlen;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (len != data.length) {
			byte[] narr = new byte[len];

			System.arraycopy(data, 0, narr, 0, len);
			data = narr;
		}

		return data;
	}

	public static List getXMLInputStreamToObjList(InputStream is, String className) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List retList = new ArrayList();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(is);
			Element root = doc.getRootElement();

			List elements = root.getChildren();
			for (int kk = 0; kk < elements.size(); kk++) {
				Element tele = (Element) elements.get(kk);
				Map retMap = new HashMap();
				List plist = tele.getChildren();
				String tname = "";
				String tvalue = "";
				for (int yy = 0; yy < plist.size(); yy++) {
					Element pele = (Element) plist.get(yy);
					tname = pele.getName();
					tvalue = pele.getText();
					retMap.put(tname, tvalue);
				}
				Object obj = null;
				try {
					obj = ReflectUtil.newInstance(className);
				} catch (Exception e) {
					e.printStackTrace();
					inputExp.addException(new AppException("在第" + kk + 1 + "数据项格式不符合要求，请检查！" + e.getMessage()));
				}

				ReflectUtil.copyMapToObject(retMap, obj, true);
				retList.add(obj);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		} catch (IOException e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}

		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}
		return retList;
	}

	public static List getTxtInputStreamToObjectList(InputStream is, String fieldNames, String domainClassName)
			throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List retList = new ArrayList();
		String[] fieldName = fieldNames.split(",");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));

		int linenum = 0;
		try {
			String line = br.readLine();

			while (line != null) {
				linenum += 1;
				if (line.trim().length() > 0) {
					String[] valuest = line.split("\\t");
					String[] values = new String[valuest.length];
					if ((null != values) && (values.length > 0)) {
						for (int kk = 0; kk < values.length; kk++) {
							values[kk] = valuest[kk].trim();
						}
					}
					try {
						Object tmpObj = ReflectUtil.generatorObjectFromArray(fieldName, values, domainClassName);

						retList.add(tmpObj);
					} catch (Exception e) {
						e.printStackTrace();
						inputExp.addException(new AppException("在第" + linenum + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}

		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}

		return retList;
	}

	public static List getSpeTxtInputStreamToObjectList(InputStream is, String fieldNames, String fieldLongs,
			String domainClassName, boolean isTrim) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List retList = new ArrayList();
		String[] fieldName = fieldNames.split(",");
		String[] fieldLong = fieldLongs.split(",");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));

		int linenum = 0;
		try {
			String line = br.readLine();

			while (line != null) {
				linenum += 1;
				if (line.trim().length() > 0) {
					String[] values = new String[fieldName.length];
					String tfname = "";
					String tvalue = "";
					for (int kk = 0; kk < fieldName.length; kk++) {
						tfname = fieldName[kk];
						int tflong = Integer.valueOf(fieldLong[kk]).intValue();
						tvalue = line.substring(0, tflong);
						if (isTrim) {
							tvalue = tvalue.trim();
						}
						values[kk] = tvalue;
						if (kk < fieldName.length - 1) {
							line = line.substring(tflong + 1);
						}
					}
					try {
						Object tmpObj = ReflectUtil.generatorObjectFromArray(fieldName, values, domainClassName);

						retList.add(tmpObj);
					} catch (Exception e) {
						e.printStackTrace();
						inputExp.addException(new AppException("在第" + linenum + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}

		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}

		return retList;
	}

	public static List getSpeTxtByteInputStreamToObjectList(InputStream is, String fieldNames, String fieldLongs,
			String domainClassName, boolean isTrim) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List retList = new ArrayList();
		String[] fieldName = fieldNames.split(",");
		String[] fieldLong = fieldLongs.split(",");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));

		int linenum = 0;
		try {
			String line = br.readLine();

			while (line != null) {
				linenum += 1;
				if (line.trim().length() > 0) {
					String[] values = new String[fieldName.length];
					String tfname = "";
					String tvalue = "";
					for (int kk = 0; kk < fieldName.length; kk++) {
						tfname = fieldName[kk];
						byte[] data = line.getBytes("GBK");
						int tflong = Integer.valueOf(fieldLong[kk]).intValue();
						tvalue = new String(data, 0, tflong);
						if (isTrim) {
							tvalue = tvalue.trim();
						}
						values[kk] = tvalue;
						if (kk < fieldName.length - 1) {
							line = new String(data, tflong + 1, data.length - tflong - 1, "GBK");
						}
					}
					try {
						Object tmpObj = ReflectUtil.generatorObjectFromArray(fieldName, values, domainClassName);

						retList.add(tmpObj);
					} catch (Exception e) {
						e.printStackTrace();
						inputExp.addException(new AppException("在第" + linenum + "行数据格式不符合要求，请检查！" + e.getMessage()));
					}
				}

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			inputExp.addException(new AppException("数据导入出错！"));
		}

		if (inputExp.getExceptions().size() > 0) {
			throw inputExp;
		}

		return retList;
	}

	public static ByteArrayOutputStream saveAsDomainListToExcelFile(HttpServletRequest request,
			HttpServletResponse response, SaveAsInfo saveAsInfo) throws IOException, WriteException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ColumnInfo columnInfo = null;
		Object obj = new Object();
		Object value = null;
		String strValue = "";
		String javaType = "";

		InputStream srcIs = null;
		Label label = null;
		jxl.write.Number numberCell = null;
		DateTime dateCell = null;

		Formula formulaCell = null;
		Cell cell = null;

		WritableFont FONT = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

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

		int pageCount = 1;
		int exPageSize = 65000;
		if (saveAsInfo.getDomainList().size() % exPageSize > 0) {
			pageCount = saveAsInfo.getDomainList().size() / exPageSize + 1;
		} else {
			pageCount = saveAsInfo.getDomainList().size() / exPageSize;
		}
		try {
			if ((saveAsInfo.getSourceFileName() != null) && (saveAsInfo.getSourceFileName().length() > 1)) {
				srcIs = Resources.getResourceAsStream(saveAsInfo.getSourceFileName());

				sourcewb = Workbook.getWorkbook(srcIs);
				wwb = Workbook.createWorkbook(bos, sourcewb);
			} else {
				wwb = Workbook.createWorkbook(bos);
			}

			WritableSheet ws = null;
			Map mobj = null;
			Object tobj = null;
			String stype = "";
			Object cellValue = null;
			for (int sp = 0; sp < pageCount; sp++) {
				ws = wwb.createSheet("第" + (sp + 1) + "页", sp);

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
				for (int kk = sp * exPageSize;

				(saveAsInfo.getDomainList() != null) && (kk < (sp + 1) * exPageSize)
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
									&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase()
											.indexOf("string") > -1) || (reflect
											.getFieldType(columnInfo.getFieldName()).indexOf("Long") > -1))) {

								value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
							}

						} else if ((!columnInfo.isShowCode())
								&& (!value.equals(""))
								&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase().indexOf("string") > -1) || (reflect
										.getFieldType(columnInfo.getFieldName()).indexOf("Long") > -1))) {

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
								if ((javaType.toLowerCase().indexOf("date") > -1)
										|| (javaType.toLowerCase().indexOf("time") > -1)) {
									dateCell = new DateTime(i_colummn, i_row + 1, (Date) value, DATE_FORMAT);

									ws.addCell(dateCell);
								} else if ((javaType.toLowerCase().indexOf("int") > -1)
										|| (javaType.toLowerCase().indexOf("long") > -1)
										|| (javaType.toLowerCase().indexOf("short") > -1)
										|| (javaType.toLowerCase().indexOf("byte") > -1)
										|| (javaType.toLowerCase().indexOf("double") > -1)
										|| (javaType.toLowerCase().indexOf("float") > -1)
										|| (javaType.toLowerCase().indexOf("big") > -1)) {

									if (value.toString().indexOf(".") > -1) {
										numberCell = new jxl.write.Number(i_colummn, i_row + 1,
												Double.parseDouble(value.toString()), FLOAT_FORMAT);

									} else {

										numberCell = new jxl.write.Number(i_colummn, i_row + 1,
												Double.parseDouble(value.toString()), LONG_FORMAT);
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
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value
										.toString()), LONG_FORMAT);

								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("FLOAT")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value
										.toString()), FLOAT_FORMAT);

								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("CHINA")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value
										.toString()), CHINA_FORMAT);

								ws.addCell(numberCell);
							} else if (columnInfo.getFormat().equals("SCIENCE")) {
								numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value
										.toString()), SCIENCE_FORMAT);

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

				for (int i1 = 0; (saveAsInfo.getCellList() != null) && (i1 < saveAsInfo.getCellList().size()); i1++) {
					ExcelCellInfo excelCellInfo = null;

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
							((jxl.write.Number) cell).setValue(cellValue == null ? 0.0D : ((Number) cellValue)
									.doubleValue());

						} else if (cell.getType().equals(CellType.LABEL)) {
							((Label) cell).setString(String.valueOf(cellValue));
						} else if ((cell.getType() == CellType.NUMBER_FORMULA)
								|| (cell.getType() == CellType.STRING_FORMULA)
								|| (cell.getType() == CellType.DATE_FORMULA)
								|| (cell.getType() == CellType.BOOLEAN_FORMULA)) {

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
						numberCell = new jxl.write.Number(col, row, ((Number) cellValue).doubleValue(), LONG_FORMAT);

						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("FLOAT")) {
						numberCell = new jxl.write.Number(col, row, ((Number) cellValue).doubleValue(), FLOAT_FORMAT);

						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("CHINA")) {
						numberCell = new jxl.write.Number(col, row, ((Number) cellValue).doubleValue(), CHINA_FORMAT);

						ws.addCell(numberCell);
					} else if (excelCellInfo.getCellFormat().equals("SCIENCE")) {
						numberCell = new jxl.write.Number(col, row, ((Number) cellValue).doubleValue(), SCIENCE_FORMAT);

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

			return bos;
		} catch (WriteException ex) {
			ex.printStackTrace();

			return bos;
		} catch (Throwable ex) {
			ex.printStackTrace();

			return bos;
		} finally {
			wwb.write();
			wwb.close();
		}
	}

	public static ByteArrayOutputStream saveAsResultSetToExcelFile(HttpServletRequest request,
			HttpServletResponse response, SaveAsInfo saveAsInfo) throws IOException, WriteException {
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
		WritableFont FONT = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

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
			if ((saveAsInfo.getSourceFileName() != null) && (saveAsInfo.getSourceFileName().length() > 1)) {
				srcIs = Resources.getResourceAsStream(saveAsInfo.getSourceFileName());

				sourcewb = Workbook.getWorkbook(srcIs);
				wwb = Workbook.createWorkbook(bos, sourcewb);
			} else {
				wwb = Workbook.createWorkbook(bos);
			}

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
							&& ((reflect.getFieldType(columnInfo.getFieldName()).toLowerCase().indexOf("string") > -1) || (reflect
									.getFieldType(columnInfo.getFieldName()).indexOf("Long") > -1))) {

						value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
					}

					javaType = reflect.getFieldType(columnInfo.getFieldName());

					if (!value.equals("")) {
						if (columnInfo.getFormat() == null) {
							if ((javaType.toLowerCase().indexOf("date") > -1)
									|| (javaType.toLowerCase().indexOf("time") > -1)) {
								dateCell = new DateTime(i_colummn, i_row + 1, (Date) value, DATE_FORMAT);

								ws.addCell(dateCell);
							} else if ((javaType.toLowerCase().indexOf("int") > -1)
									|| (javaType.toLowerCase().indexOf("long") > -1)
									|| (javaType.toLowerCase().indexOf("short") > -1)
									|| (javaType.toLowerCase().indexOf("byte") > -1)
									|| (javaType.toLowerCase().indexOf("double") > -1)
									|| (javaType.toLowerCase().indexOf("float") > -1)
									|| (javaType.toLowerCase().indexOf("big") > -1)) {

								if (value.toString().indexOf(".") > -1) {
									numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value
											.toString()), FLOAT_FORMAT);
								} else {
									numberCell = new jxl.write.Number(i_colummn, i_row + 1, Double.parseDouble(value
											.toString()), LONG_FORMAT);
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
							numberCell = new jxl.write.Number(i_colummn, i_row + 1,
									Double.parseDouble(value.toString()), LONG_FORMAT);

							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("FLOAT")) {
							numberCell = new jxl.write.Number(i_colummn, i_row + 1,
									Double.parseDouble(value.toString()), FLOAT_FORMAT);

							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("CHINA")) {
							numberCell = new jxl.write.Number(i_colummn, i_row + 1,
									Double.parseDouble(value.toString()), CHINA_FORMAT);

							ws.addCell(numberCell);
						} else if (columnInfo.getFormat().equals("SCIENCE")) {
							numberCell = new jxl.write.Number(i_colummn, i_row + 1,
									Double.parseDouble(value.toString()), SCIENCE_FORMAT);

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
			}

			return bos;
		} catch (WriteException ex) {
			ex.printStackTrace();

			return bos;
		} catch (Throwable ex) {
			ex.printStackTrace();

			return bos;
		} finally {
			wwb.write();
			wwb.close();
		}
	}

	public static ByteArrayOutputStream saveAsResultSetToTxtFile(HttpServletRequest request,
			HttpServletResponse response, SaveAsInfo saveAsInfo) throws IOException, WriteException {
		ColumnInfo columnInfo = null;

		Object value = "";
		StringBuffer sb = new StringBuffer();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
			columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
			sb.append(columnInfo.getTitlecomment());
			if (i < saveAsInfo.getColumnList().size() - 1) {
				sb.append("\t");
			}
		}
		sb.append("\r\n");

		try {
			ResultSet rs = saveAsInfo.getRs();

			while (rs.next()) {
				for (int j = 0; j < saveAsInfo.getColumnList().size(); j++) {
					columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(j);
					value = rs.getObject(columnInfo.getFieldName());
					if (value == null) {
						value = "";
					}

					if ((!saveAsInfo.isShowCode()) && (!value.equals(""))) {
						value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
					}

					sb.append(value);

					if (j < saveAsInfo.getColumnList().size() - 1) {
						sb.append("\t");
					}
				}
				sb.append("\r\n");
			}

			if (sb.length() > 0) {
				bos.write(sb.toString().getBytes());
			}

			return bos;
		} catch (Exception ex) {
			ex = ex;
			ex.printStackTrace();
			return bos;
		} finally {
		}
	}

	public static ByteArrayOutputStream saveAsDomainListToTxtFile(HttpServletRequest request, SaveAsInfo saveAsInfo) {
		ColumnInfo columnInfo = null;
		Object obj = new Object();
		String value = "";
		StringBuffer sb = new StringBuffer();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
			columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
			sb.append(columnInfo.getTitlecomment());
			if (i < saveAsInfo.getColumnList().size() - 1) {
				sb.append("\t");
			}
		}
		sb.append("\r\n");

		try {
			Reflect reflect = new Reflect(saveAsInfo.getDomainClass());

			Map mobj = null;
			Object tobj = null;
			for (int i = 0; i < saveAsInfo.getDomainList().size(); i++) {
				if ((i % 3000 == 0) && (sb.length() > 0)) {
					bos.write(sb.toString().getBytes());
					sb = new StringBuffer();
				}

				obj = saveAsInfo.getDomainList().get(i);
				for (int j = 0; j < saveAsInfo.getColumnList().size(); j++) {
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
						value = reflect.getObjFieldStrValue(obj, columnInfo.getFieldName());
					}

					if (value == null) {
						value = "";
					}

					if ((!saveAsInfo.isShowCode()) && (!value.equals(""))) {
						value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value);
					}

					sb.append(value);

					if (j < saveAsInfo.getColumnList().size() - 1) {
						sb.append("\t");
					}
				}
				sb.append("\r\n");
			}

			if (sb.length() > 0) {
				bos.write(sb.toString().getBytes());
			}

			return bos;
		} catch (Exception ex) {
			ex = ex;
			ex.printStackTrace();
			return bos;
		} finally {
		}
	}

	private String getDomainList2String(HttpServletRequest request, HttpServletResponse response, SaveAsInfo saveAsInfo) {
		ColumnInfo columnInfo = null;
		Object obj = null;
		String value = "";
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
			columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
			sb.append(columnInfo.getTitlecomment());
			if (i < saveAsInfo.getColumnList().size() - 1) {
				sb.append("\t");
			}
		}
		sb.append("\r\n");

		Reflect reflect = new Reflect(saveAsInfo.getDomainClass());

		try {
			Map mobj = null;
			Object tobj = null;
			for (int i = 0; i < saveAsInfo.getDomainList().size(); i++) {
				obj = saveAsInfo.getDomainList().get(i);
				for (int j = 0; j < saveAsInfo.getColumnList().size(); j++) {
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
						value = reflect.getObjFieldStrValue(obj, columnInfo.getFieldName());
					}

					if (value == null) {
						value = "";
					}

					if (!value.equals("")) {
					}

					sb.append(value);

					if (j < saveAsInfo.getColumnList().size() - 1) {
						sb.append("\t");
					}
				}
				sb.append("\r\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

	private static String getCodeDesc(HttpServletRequest request, String codeType, String codeValue) {
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

	public static List getSaveColumnMeta(HttpServletRequest request) throws Exception {
		List columnMetas = getSaveColumnMeta();
		ColumnInfo columnMeta = null;
		if ((columnMetas == null) || (columnMetas.size() == 0)) {
			columnMetas = new ArrayList();
			String colMetaStr = request.getParameter("com.yinhai.list.column.metadata");

			if (colMetaStr != null) {
				StringTokenizer entrys = new StringTokenizer(colMetaStr, "^");

				while (entrys.hasMoreTokens()) {
					StringTokenizer items = new StringTokenizer(entrys.nextToken(), "`");

					columnMeta = new ColumnInfo();
					columnMeta.setColumnName(items.nextToken());
					columnMeta.setFieldName(columnMeta.getColumnName());
					columnMeta.setTitlecomment(items.nextToken());
					columnMetas.add(columnMeta);
				}
			}
		}
		return columnMetas;
	}

	public static List getSaveColumnMeta(HttpServletRequest request, String metaName) throws Exception {
		List columnMetas = getSaveColumnMeta();
		ColumnInfo columnMeta = null;
		if ((columnMetas == null) || (columnMetas.size() == 0)) {
			columnMetas = new ArrayList();
			String colMetaStr = request.getParameter(metaName);

			if (colMetaStr != null) {
				StringTokenizer entrys = new StringTokenizer(colMetaStr, "^");

				while (entrys.hasMoreTokens()) {
					StringTokenizer items = new StringTokenizer(entrys.nextToken(), "`");

					columnMeta = new ColumnInfo();
					columnMeta.setColumnName(items.nextToken());
					columnMeta.setFieldName(columnMeta.getColumnName());
					columnMeta.setTitlecomment(items.nextToken());
					columnMetas.add(columnMeta);
				}
			}
		}
		return columnMetas;
	}

	private static List getSaveColumnMeta() {
		return null;
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
}
