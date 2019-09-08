package com.yinhai.sysframework.excelmodelprint;

import java.io.Serializable;

import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

public class ExcelCellFormat implements Serializable {

	public static final WritableFont FONT = new WritableFont(WritableFont.createFont("宋体"), 12, WritableFont.NO_BOLD,
			false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);

	public static String LABEL = "LABEL";
	public static WritableCellFormat LABEL_FORMAT = new WritableCellFormat(FONT);

	private static DateFormat dtf = new DateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat df = new DateFormat("yyyy-MM-dd");

	public static final String DATETIME = "DATETIME";
	public static WritableCellFormat DATETIME_FORMAT = new WritableCellFormat(FONT, dtf);
	public static final String DATE = "DATE";
	public static WritableCellFormat DATE_FORMAT = new WritableCellFormat(FONT, df);

	private static NumberFormat nf_long = new NumberFormat("#");
	private static NumberFormat nf_float = new NumberFormat("0.00;[红色]-0.00");
	private static NumberFormat nf_China = new NumberFormat("￥#,##0.00");
	private static NumberFormat nf_science = new NumberFormat("#,##0.00;[红色]-#,##0.00");

	public static final String LONG = "LONG";
	public static final WritableCellFormat LONG_FORMAT = new WritableCellFormat(FONT, nf_long);
	public static final String FLOAT = "FLOAT";
	public static final WritableCellFormat FLOAT_FORMAT = new WritableCellFormat(FONT, nf_float);

	public static final String CHINA = "CHINA";
	public static WritableCellFormat CHINA_FORMAT = new WritableCellFormat(FONT, nf_China);
	public static final String SCIENCE = "SCIENCE";
	public static WritableCellFormat SCIENCE_FORMAT = new WritableCellFormat(FONT, nf_science);

	public static final String STRING_FORMULA = "STRING_FORMULA";

	public static final String DATE_FORMULA = "DATE_FORMULA";
	public static final String NUMBER_FORMULA = "NUMBER_FORMULA";
	public static String BOOLEAN_FORMULA = "BOOLEAN_FORMULA";
}
