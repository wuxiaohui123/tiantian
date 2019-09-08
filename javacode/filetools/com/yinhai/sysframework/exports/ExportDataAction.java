package com.yinhai.sysframework.exports;

import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.util.ExcelFileUtils;
import com.yinhai.sysframework.util.ExcelFileUtilsNew;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.TxtFileUtils;
import com.yinhai.webframework.BaseAction;

public class ExportDataAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1034639758832448308L;

	public final String taCommonDefaultExportData() throws Exception {
		try {
			String eType = request.getParameter("eType");
			String parameter = request.getParameter("_grid_item_export_excel");
			String fileName = request.getParameter("fileName");
			if (StringUtil.isBlank(fileName)) {
				fileName = "export_" + String.valueOf(Math.random()).substring(2, 8);
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}
			response.setContentType("application/octet-stream");
			ServletOutputStream outputStream = response.getOutputStream();
			if ("1".equals(eType)) {// txt
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".txt");
				String commas = request.getParameter("field_commas");
				StringBuffer sb = TxtFileUtils.getGridDataByStringBuffer(parameter.toString(), commas);
				outputStream.write(sb.toString().getBytes());
			} else if ("2".equals(eType)) {// excel(.xls)
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
				HSSFWorkbook workbook = ExcelFileUtils.getGridDataToWorkbook(request, getDto(), parameter);
				workbook.write(outputStream);
			} else {// excel(.xlsx)
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
				SXSSFWorkbook workbook = ExcelFileUtilsNew.getGridDataToWorkbook(request, getDto(), parameter);
				workbook.write(outputStream);
			}
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			throw new AppException("导出数据发送错误！");
		}
		return null;
	}

	public String taCommonAllDataExportData() throws Exception {
		try {
			ParamDTO dto = getDto();
			Integer eType = dto.getAsInteger("eType");
			String fileName = dto.getAsString("fileName");
			if (StringUtil.isBlank(fileName)) {
				fileName = "export_" + String.valueOf(Math.random()).substring(2, 8);
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}
			response.setContentType("application/octet-stream");
			ServletOutputStream outputStream = response.getOutputStream();
			if (eType == 1) {// txt
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".txt");
				StringBuffer sb = TxtFileUtils.getGridAllDataByStringBuffer(dto, getDao());
				outputStream.write(sb.toString().getBytes());
			} else if (eType == 2) {// excel
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
				HSSFWorkbook workbook = ExcelFileUtils.getGridAllDataToWorkbook(dto, getDao());
				workbook.write(outputStream);
			} else {// excel
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
				SXSSFWorkbook workbook = ExcelFileUtilsNew.getGridAllDataToWorkbook(dto, getDao());
				workbook.write(outputStream);
			}
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			throw new AppException("导出数据发送错误！");
		}

		return null;
	}

}
