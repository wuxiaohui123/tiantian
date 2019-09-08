package com.yinhai.sysframework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.yinhai.sysframework.domain.ExcelPrintDomain;

public class ExcelTemplatePrintUtil {

	public static HSSFWorkbook excelSinglePrint2WorkbookByFileTemplate(String templateFileName, Map beanParams)
			throws Exception {
		XLSTransformer transformer = new XLSTransformer();
		InputStream is = new BufferedInputStream(new FileInputStream(templateFileName));

		HSSFWorkbook resultWorkbook = transformer.transformXLS(is, beanParams);
		return resultWorkbook;
	}

	public static ByteArrayOutputStream excelSinglePrint2OutputStreamByFileTemplate(String templateFileName,
			Map beanParams) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XLSTransformer transformer = new XLSTransformer();
		InputStream is = new BufferedInputStream(new FileInputStream(templateFileName));

		HSSFWorkbook resultWorkbook = transformer.transformXLS(is, beanParams);
		OutputStream os = new BufferedOutputStream(bos);
		resultWorkbook.write(os);
		is.close();
		os.flush();
		return bos;
	}

	public static ByteArrayOutputStream excelMultiplePagePrint2OutputStreamByFileTemplate(String templateFileName,
			List objects, Map beanParams, int maxLinePerPage) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = new BufferedInputStream(new FileInputStream(templateFileName));

		HSSFWorkbook resultWorkbook = excelMultiplePagePrint(is, objects, beanParams, maxLinePerPage);

		OutputStream os = new BufferedOutputStream(bos);
		resultWorkbook.write(os);
		is.close();
		os.flush();
		return bos;
	}

	public static HSSFWorkbook excelMultiplePagePrint2WorkbookByFileTemplate(String templateFileName, List objects,
			Map beanParams, int maxLinePerPage) throws FileNotFoundException {
		InputStream is = new BufferedInputStream(new FileInputStream(templateFileName));

		return excelMultiplePagePrint(is, objects, beanParams, maxLinePerPage);
	}

	public static HSSFWorkbook excelMultiplePagePrint(InputStream is, List objects, Map beanParams, int maxLinePerPage) {
		List prints = new ArrayList();
		List sheetNames = new ArrayList();
		if ((objects != null) && (objects.size() > 0)) {
			int count = 0;
			if (objects.size() % maxLinePerPage > 0) {
				count = objects.size() / maxLinePerPage + 1;
			} else {
				count = objects.size() / maxLinePerPage;
			}
			ExcelPrintDomain print = null;
			List subList = null;
			for (int yy = 0; yy < count; yy++) {
				print = new ExcelPrintDomain();
				if (yy == 0) {
					print.setIsBeginPage(1);
				}
				if (yy == count - 1) {
					subList = objects.subList(yy * maxLinePerPage, objects.size());

					print.setDomains(subList);
					print.setIsEndPage(1);
				} else {
					subList = objects.subList(yy * maxLinePerPage, (yy + 1) * maxLinePerPage);

					print.setDomains(subList);
				}
				print.setPageNo(yy + 1);
				prints.add(print);
				sheetNames.add("第" + (yy + 1) + "页");
			}
		}

		XLSTransformer transformer = new XLSTransformer();
		HSSFWorkbook workbook = transformer.transformMultipleSheetsList(is, prints, sheetNames, "print", beanParams, 0);

		return workbook;
	}

	public static ByteArrayOutputStream excelMultiplePagePrint2OutputStream(String srcFilePath, List objects,
			Map beanParams, int maxLinePerPage, int startPageNo, ServletContext context) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		return bos;
	}

	private static HSSFWorkbook excelMultiplePagePrint(InputStream is, List objects, Map beanParams,
			int maxLinePerPage, int startPageNo, ServletContext context) {
		List prints = new ArrayList();
		List sheetNames = new ArrayList();
		if ((objects != null) && (objects.size() > 0)) {
			int count = 0;
			if (objects.size() % maxLinePerPage > 0) {
				count = objects.size() / maxLinePerPage + 1;
			} else {
				count = objects.size() / maxLinePerPage;
			}
			ExcelPrintDomain print = null;
			List subList = null;
			for (int yy = 0; yy < count; yy++) {
				print = new ExcelPrintDomain(context);
				if (yy == 0) {
					print.setIsBeginPage(1);
				}
				if (yy == count - 1) {
					subList = objects.subList(yy * maxLinePerPage, objects.size());

					print.setDomains(subList);
					print.setIsEndPage(1);
				} else {
					subList = objects.subList(yy * maxLinePerPage, (yy + 1) * maxLinePerPage);

					print.setDomains(subList);
				}
				print.setPageNo(startPageNo + yy + 1);
				prints.add(print);
				sheetNames.add("第" + (yy + 1) + "页");
			}
		}

		XLSTransformer transformer = new XLSTransformer();
		HSSFWorkbook workbook = transformer.transformMultipleSheetsList(is, prints, sheetNames, "print", beanParams, 0);

		return workbook;
	}
}
