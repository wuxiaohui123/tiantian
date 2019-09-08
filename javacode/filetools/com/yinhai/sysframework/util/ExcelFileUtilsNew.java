package com.yinhai.sysframework.util;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.exception.IllegalInputAppException;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.persistence.ibatis.CountStatementUtil;
import com.yinhai.sysframework.persistence.ibatis.IDao;
import com.yinhai.sysframework.print.SaveAsInfo;
import com.yinhai.sysframework.util.json.JSonFactory;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExcelFileUtilsNew {

	/**
	 * 行高系数
	 */
	private static final Double ROW_HEGIHT_CV = 15.625;
	/**
	 * 列宽系数
	 */
	private static final Double COL_HEGIHT_CV = 35.7;

	public static Workbook chooseWorkbook(InputStream inputStream) {
		BufferedInputStream buf = new BufferedInputStream(inputStream);
		try {
			Object localObject1;
			if (POIFSFileSystem.hasPOIFSHeader(buf)) {
				localObject1 = new HSSFWorkbook(buf);
				return (Workbook) localObject1;
			}
			if (DocumentFactoryHelper.hasOOXMLHeader(buf)) {
				localObject1 = new SXSSFWorkbook(new XSSFWorkbook(OPCPackage.open(buf)));
				return (Workbook) localObject1;
			}
			throw new AppException("非法的Excel表格文件");
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException("非法的Excel表格文件");
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			throw new AppException("非法的Excel表格文件");
		} finally {
		}
	}

	public static void closeWorkbook(Workbook workbook) {
		if (workbook == null)
			return;
		try {
			if ((workbook instanceof XSSFWorkbook)) {
				((XSSFWorkbook) workbook).getPackage().close();
			} else {
				workbook.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// --------------excel导入 BINGEN-----------------------------------//
	
	public static String[] SheetCellsToStringArray(Cell[] cells) {
		String[] resultArray = new String[cells.length];
		for (int i = 0; i < cells.length; i++) {
			resultArray[i] = cells[i].getStringCellValue();
		}
		return resultArray;
	}
	
	public static List<Map<String, String>> getSheetToMapList(SXSSFSheet sheet, Integer model, Map<String, Object> datamap, Integer rowStart,Integer rowEnd) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		rowEnd = rowEnd != null ? rowEnd : sheet.getPhysicalNumberOfRows();
		rowEnd = rowEnd > sheet.getPhysicalNumberOfRows() ? sheet.getPhysicalNumberOfRows() : rowEnd;
		for (int i = rowStart; i < rowEnd; i++) {
			map = new LinkedHashMap<String, String>();
			SXSSFRow sxssfRow = sheet.getRow(i);
			for (String key : datamap.keySet()) {
				int j = Integer.valueOf((String) datamap.get(key)) - 1;
				map.put(key, sxssfRow.getCell(j).getStringCellValue().trim() + "");
			}
			list.add(map);
		}
		return list;
	}
	
	public static List<?> getExcelInputStream2ObjectList(InputStream is, String fieldNames, String domainClassName, boolean hasTitle)
			throws IllegalInputAppException {
		int rowStart = hasTitle ? 1 : 0;
		int rowEnd = 0;
		int cellStart = 0;
		int cellEnd = 0;
		return getExcelInputStream2ObjectListWithParam(is, fieldNames, domainClassName, rowStart, rowEnd, cellStart, cellEnd);
	}

	public static void dealExcelInputStream(InputStream is, int fieldCount, boolean hasTitle, ExcelReadRowHandler excelReadRowHandler)
			throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		Workbook workbook = chooseWorkbook(is);
		Sheet sheet0 = workbook.getSheetAt(0);
		int rowNumber = hasTitle ? 1 : 0;
		int rows = sheet0.getLastRowNum() - sheet0.getFirstRowNum();
		String[] values = new String[fieldCount];
		for (; rowNumber < rows + 1; rowNumber++) {
			for (int i = 0; i < fieldCount; i++) {
				Cell cell = sheet0.getRow(rowNumber).getCell(i);
				values[i] = (cell == null ? null : cell.toString());
			}
			try {
				excelReadRowHandler.handerRow(values);
			} catch (Exception e) {
				inputExp.addException(new AppException("在第" + (rowNumber + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
			}
		}
		closeWorkbook(workbook);
	}

	public static List<Object> getExcelInputStream2ObjectListWithParam(InputStream is, String fieldNames, String domainClassName, int rowStart,
			int rowEnd, int cellStart, int cellEnd) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List<Object> retList = new ArrayList<Object>();
		String[] fields = fieldNames.split(",");
		Workbook workbook = chooseWorkbook(is);
		Sheet sheet0 = workbook.getSheetAt(0);
		int rowNumber = rowStart;
		int rows = sheet0.getLastRowNum() - sheet0.getFirstRowNum() - rowEnd;
		String[] values = new String[fields.length];
		Object domainObject = null;
		for (; rowNumber < rows + 1; rowNumber++) {
			for (int i = cellStart; i < values.length + cellStart; i++) {
				Cell cell = sheet0.getRow(rowNumber).getCell(i);
				values[i] = (cell == null ? null : cell.toString());
			}
			try {
				domainObject = ReflectUtil.generatorObjectFromArray(fields, values, domainClassName);
				retList.add(domainObject);
			} catch (Exception e) {
				e.printStackTrace();
				inputExp.addException(new AppException("在第" + (rowNumber + 1) + "行数据格式不符合要求，请检查！" + e.getMessage()));
			}
		}
		closeWorkbook(workbook);
		return retList;
	}

	public static ByteArrayOutputStream saveAsDomainListToExcelFile(HttpServletRequest request, HttpServletResponse response, SaveAsInfo saveAsInfo)
			throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");

		return null;
	}

	// --------------excel导入 END-----------------------------------//

	// --------------excel导出 BINGEN-----------------------------------//

	public static SXSSFWorkbook getGridDataToWorkbook(HttpServletRequest request, ParamDTO dto, String parameter) {
		JSONArray json2bean = (JSONArray) JSonFactory.json2bean(parameter.toString(), JSONArray.class);
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = workbook.createSheet("sheet1");
		String isHasTableTitle = request.getParameter("Set0");
		String isHasColumnHead = request.getParameter("Set1");
		String isHasCreateInfo = request.getParameter("Set2");
		int startRow = 0;// sheet开始行号
		if ("1".equals(isHasTableTitle)) {// 是否包含表标题
			String title = request.getParameter("tableTitle");// 表格标题
			int colNum = ((JSONArray) json2bean.get(0)).size() - 1;// 列数
			SXSSFRow eTitle = sheet.createRow(0);
			eTitle.setHeightInPoints(35.625f);
			SXSSFCell TitleCell = null;
			XSSFCellStyle cellTStyle = getTableTitleStyle(workbook.getXSSFWorkbook(), TitleCell);
			for (int i = 0; i <= colNum; i++) {
				TitleCell = eTitle.createCell(i);
				TitleCell.setCellStyle(cellTStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colNum));// 合并单元格
			TitleCell = sheet.getRow(0).getCell(0);
			TitleCell.setCellValue(new XSSFRichTextString(title));
			startRow++;
		}
		if ("1".equals(isHasCreateInfo)) {// 是否添加创建信息
			IUser user = dto.getUserInfo();
			String informaction = "informaction// 用户名：" + user.getName() + " 岗位：" + user.getNowPosition().getPositionname();
			informaction += " 创建时间：" + DateUtil.getCurDateTime();
			int colNum = ((JSONArray) json2bean.get(0)).size() - 1;// 列数
			XSSFCellStyle cellInfoTStyle = getCreateInfoStyle(workbook.getXSSFWorkbook());
			SXSSFRow createInfoRow = sheet.createRow(1);
			createInfoRow.setHeightInPoints((short) 16);
			SXSSFCell creInfoCell = null;
			for (int j = 0; j <= colNum; j++) {
				creInfoCell = createInfoRow.createCell(j);
				creInfoCell.setCellStyle(cellInfoTStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, colNum));// 合并单元格
			creInfoCell = sheet.getRow(1).getCell(0);
			creInfoCell.setCellValue(new XSSFRichTextString(informaction));
			startRow++;
		}
		ArrayList<Object> list = null;
		for (int i = 0; i < json2bean.size(); i++) {
			SXSSFRow row = sheet.createRow(i + startRow);
			list = new ArrayList<Object>((JSONArray) json2bean.get(i));
			if ("1".equals(isHasColumnHead) && i == 0) {
				row.setHeightInPoints(30);
				XSSFCellStyle headStyle = getColumnHeadStyle(workbook.getXSSFWorkbook());
				setColumnHeadAndStyle(sheet, list, row, headStyle);
			} else {
				row.setHeightInPoints(25);
				XSSFCellStyle dataStyle = getDataRowStyle(workbook.getXSSFWorkbook());
				setDataRowAndStyle(sheet, list, row, dataStyle);
			}
		}
		return workbook;
	}

	public static XSSFCellStyle getTableTitleStyle(XSSFWorkbook workbook, SXSSFCell TitleCell) {
		// 样式
		XSSFCellStyle cellTStyle = workbook.createCellStyle();
		cellTStyle.setBorderLeft(BorderStyle.THIN);
		cellTStyle.setBorderTop(BorderStyle.THIN);
		cellTStyle.setBorderRight(BorderStyle.THIN);
		cellTStyle.setBorderBottom(BorderStyle.THIN);

		cellTStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
		cellTStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
		// 字体
		XSSFFont cellFont = workbook.createFont();
		cellFont.setFontName("微软雅黑");
		cellFont.setFontHeightInPoints((short) 16);// 16px
		cellFont.setBold(true);// 加粗
		cellTStyle.setFont(cellFont);
		// 设置自动换行
		cellTStyle.setWrapText(false);
		return cellTStyle;

	}

	public static XSSFCellStyle getCreateInfoStyle(XSSFWorkbook workbook) {
		// 样式
		XSSFCellStyle cellInfoTStyle = workbook.createCellStyle();
		cellInfoTStyle.setBorderLeft(BorderStyle.THIN);
		cellInfoTStyle.setBorderTop(BorderStyle.THIN);
		cellInfoTStyle.setBorderRight(BorderStyle.THIN);
		cellInfoTStyle.setBorderBottom(BorderStyle.THIN);
		cellInfoTStyle.setAlignment(HorizontalAlignment.RIGHT);// 水平靠右
		// 字体
		XSSFFont cellInfoFont = workbook.createFont();
		cellInfoFont.setFontName("微软雅黑");
		cellInfoFont.setFontHeightInPoints((short) 10);// 10像素
		cellInfoTStyle.setFont(cellInfoFont);
		return cellInfoTStyle;
	}

	public static XSSFCellStyle getDataRowStyle(XSSFWorkbook workbook) {
		XSSFCellStyle dataStyle = workbook.createCellStyle();
		// 数据边框
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderTop(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);
		dataStyle.setBorderBottom(BorderStyle.THIN);
		// 数据边框颜色
		dataStyle.setLeftBorderColor(new XSSFColor(Color.BLACK));
		dataStyle.setTopBorderColor(new XSSFColor(Color.BLACK));
		dataStyle.setRightBorderColor(new XSSFColor(Color.BLACK));
		dataStyle.setBottomBorderColor(new XSSFColor(Color.BLACK));
		// 字体
		XSSFFont dataFont = workbook.createFont();
		dataFont.setFontName("微软雅黑");
		dataFont.setFontHeightInPoints((short) 11);// 11像素
		dataStyle.setFont(dataFont);
		// 数据对齐方式
		dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直对齐方式
		dataStyle.setAlignment(HorizontalAlignment.CENTER);// 水平对齐方式
		return dataStyle;
	}

	public static void setDataRowAndStyle(SXSSFSheet sheet, ArrayList<Object> list, SXSSFRow row, XSSFCellStyle dataStyle) {
		for (int j = 0; j < list.size(); j++) {
			// 设置列宽
			sheet.setColumnWidth(j, 140 * COL_HEGIHT_CV.intValue());
			SXSSFCell cell = row.createCell(j);
			cell.setCellStyle(dataStyle);
			XSSFRichTextString text = new XSSFRichTextString(list.get(j).toString());
			cell.setCellValue(text);
		}
	}

	public static void setColumnHeadAndStyle(SXSSFSheet sheet, ArrayList<Object> list, SXSSFRow row, XSSFCellStyle headStyle) {
		for (int j = 0; j < list.size(); j++) {
			// 设置列宽
			sheet.setColumnWidth(j, 140 * COL_HEGIHT_CV.intValue());
			SXSSFCell cell = row.createCell(j);
			cell.setCellStyle(headStyle);
			XSSFRichTextString text = new XSSFRichTextString(list.get(j).toString());
			cell.setCellValue(text);
		}
	}

	public static XSSFCellStyle getColumnHeadStyle(XSSFWorkbook workbook) {
		// 表头样式
		XSSFCellStyle Headstyle = workbook.createCellStyle();
		Headstyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直对齐方式
		Headstyle.setAlignment(HorizontalAlignment.CENTER);// 水平对齐方式
		// 表头字体
		XSSFFont HeadFont = workbook.createFont();
		HeadFont.setFontName("微软雅黑");
		HeadFont.setFontHeightInPoints((short) 13);
		HeadFont.setBold(true);

		HeadFont.setColor(new XSSFColor(new Color(255, 255, 255)));
		Headstyle.setFont(HeadFont);
		// 表头边框
		Headstyle.setBorderLeft(BorderStyle.MEDIUM);
		Headstyle.setBorderTop(BorderStyle.MEDIUM);
		Headstyle.setBorderRight(BorderStyle.MEDIUM);
		Headstyle.setBorderBottom(BorderStyle.MEDIUM);
		// 表头边框颜色
		Headstyle.setLeftBorderColor(new XSSFColor(Color.BLACK));
		Headstyle.setTopBorderColor(new XSSFColor(Color.BLACK));
		Headstyle.setRightBorderColor(new XSSFColor(Color.BLACK));
		Headstyle.setBottomBorderColor(new XSSFColor(Color.BLACK));
		// 表头背景颜色
		Headstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);// 纯色填充
		Headstyle.setFillForegroundColor(new XSSFColor(new Color(91, 155, 213)));
		return Headstyle;
	}

	
	public static SXSSFWorkbook getGridAllDataToWorkbook(ParamDTO dto, IDao dao) throws Exception {
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
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = workbook.createSheet("sheet1");
		int startRow = 0;// sheet开始行号
		Integer isHasTableTitle = dto.getAsInteger("Set0");
		Integer isHasColumnHead = dto.getAsInteger("Set1");
		Integer isHasCreateInfo = dto.getAsInteger("Set2");
		if (isHasTableTitle == 1) {// 是否包含表标题
			String title = dto.getAsString("tableTitle");// 表格标题
			int colNum = headName.size() - 1;// 列数
			SXSSFRow eTitle = sheet.createRow(0);
			eTitle.setHeightInPoints(35.625f);
			SXSSFCell TitleCell = null;
			XSSFCellStyle cellTStyle = getTableTitleStyle(workbook.getXSSFWorkbook(), TitleCell);
			for (int i = 0; i <= colNum; i++) {
				TitleCell = eTitle.createCell(i);
				TitleCell.setCellStyle(cellTStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colNum));// 合并单元格
			TitleCell = sheet.getRow(0).getCell(0);
			TitleCell.setCellValue(new XSSFRichTextString(title));
			startRow++;
		}
		if (isHasCreateInfo == 1) {// 是否添加创建信息
			IUser user = dto.getUserInfo();
			String informaction = "informaction// 用户名：" + user.getName() + " 岗位：" + user.getNowPosition().getPositionname();
			informaction += " 创建时间：" + DateUtil.getCurDateTime();
			int colNum = headName.size() - 1;// 列数
			SXSSFRow createInfoRow = sheet.createRow(1);
			createInfoRow.setHeightInPoints((short) 16);
			SXSSFCell creInfoCell = null;
			XSSFCellStyle cellInfoTStyle = getCreateInfoStyle(workbook.getXSSFWorkbook());
			for (int j = 0; j <= colNum; j++) {
				creInfoCell = createInfoRow.createCell(j);
				creInfoCell.setCellStyle(cellInfoTStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, colNum));// 合并单元格
			creInfoCell = sheet.getRow(1).getCell(0);
			creInfoCell.setCellValue(new XSSFRichTextString(informaction));
			startRow++;
		}
		if (isHasColumnHead == 1) {
			SXSSFRow row = sheet.createRow(2);
			row.setHeightInPoints(30);
			XSSFCellStyle headStyle = getColumnHeadStyle(workbook.getXSSFWorkbook());
			setColumnHeadAndStyle(sheet, new ArrayList<Object>(headName), row, headStyle);
			startRow++;
		}
		Class c = Class.forName((String) resultType.get(0));
		Object obj = c.newInstance();
		Map map = new HashMap();
		BaseDomain baseDomain = null;
		String yab003 = "9999";
		String desc = "";
		XSSFCellStyle dataStyle = getDataRowStyle(workbook.getXSSFWorkbook());
		for (int i = 0; i < list.size(); i++) {
			SXSSFRow row = sheet.createRow(i + startRow);
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
				SXSSFCell cell = row.createCell(j);
				cell.setCellStyle(dataStyle);
				XSSFRichTextString text = null;
				if ("".equals(map.get(headId.get(j))) || map.get(headId.get(j)) == null) {
					text = new XSSFRichTextString("");
				} else if (collection.size() > 0) {
					for (int k = 0; k < collection.size(); k++) {
						if (headId.get(j).equals(collection.get(k))) {
							desc = CodeTableLocator.getCodeDesc((String) headId.get(j), map.get(headId.get(j)).toString(), yab003);
							text = new XSSFRichTextString(desc);
							break;
						}
						if (k == collection.size() - 1) {
							text = new XSSFRichTextString(map.get(headId.get(j)).toString());
						}
					}
				} else {
					text = new XSSFRichTextString(map.get(headId.get(j)).toString());
				}
				cell.setCellValue(text);
			}
		}
		return workbook;
	}

	// --------------excel导出 END-----------------------------------//
	public static void main(String[] args) throws Exception {
	}
}
