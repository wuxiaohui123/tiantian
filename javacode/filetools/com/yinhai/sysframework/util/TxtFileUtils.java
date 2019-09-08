package com.yinhai.sysframework.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.write.WriteException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

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
import com.yinhai.sysframework.print.SaveAsInfo;
import com.yinhai.sysframework.util.DESCoderUtil;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.sysframework.util.json.JSonFactory;

public class TxtFileUtils {

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
	public static String getTxtInputStreamToStringLineByCharset(InputStream is,String charset) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName(charset)));

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
	/**
	* 读取文本文件的指定行
	*/
	public static String getTxtInputStreamToStringByLineNum(InputStream is,int lineNum) {
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	     String line="";
	     int i=0;
	     try {
	    	 while(i<lineNum){
				line=reader.readLine();
				i++;
	    	 }
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return line;
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
			String tname = "";
			String tvalue = "";
			for (int kk = 0; kk < elements.size(); kk++) {
				Element tele = (Element) elements.get(kk);
				Map retMap = new HashMap();
				List plist = tele.getChildren();
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

	public static List getTxtInputStreamToObjectList(InputStream is, String fieldNames, String domainClassName,
			boolean hasTitle) throws IllegalInputAppException {
		IllegalInputAppException inputExp = new IllegalInputAppException();
		List retList = new ArrayList();
		String[] fieldName = fieldNames.split(",");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));

		int linenum = 0;
		try {
			String line = br.readLine();

			if ((hasTitle) && (line != null)) {
				line = br.readLine();
			}

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

	public static ByteArrayOutputStream saveAsResultSetToTxtFile(HttpServletRequest request,
			HttpServletResponse response, SaveAsInfo saveAsInfo) throws Exception {
		ColumnInfo columnInfo = null;
		Object value = "";
		StringBuffer sb = new StringBuffer();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		if (saveAsInfo.isViewTitle()) {
			for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
				columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
				sb.append(columnInfo.getTitlecomment());
				if (i < saveAsInfo.getColumnList().size() - 1) {
					sb.append("\t");
				}
			}
			sb.append("\r\n");
		}

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

					if (0 != columnInfo.getWidth()) {
						int count = columnInfo.getWidth() - length(value.toString());
						if (0 < count) {
							value = value.toString() + getBlankString(count);
						}
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
			throw ex;
		} finally {
		}
	}

	private static String getBlankString(int count) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < count; i++) {
			result.append(" ");
		}
		return result.toString();
	}

	public static boolean isLetter(char c) {
		int k = 128;
		return c / k == 0;
	}

	public static int length(String s) {
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	public static FileInputStream saveAsResultSetToTxtFile2(HttpServletRequest request, HttpServletResponse response,
			SaveAsInfo saveAsInfo) throws IOException, WriteException {
		ColumnInfo columnInfo = null;

		Object value = "";
		StringBuffer sb = new StringBuffer();
		File f = new File(System.getProperty("java.io.tmpdir"));
		File ftemp = File.createTempFile("txtexport" + Math.random(), ".txt", f);
		FileOutputStream fos = new FileOutputStream(ftemp);

		if (saveAsInfo.isViewTitle()) {

			for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
				columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
				sb.append(columnInfo.getTitlecomment());
				if (i < saveAsInfo.getColumnList().size() - 1) {
					sb.append("\t");
				}
			}
			sb.append("\r\n");
		}

		try {
			ResultSet rs = saveAsInfo.getRs();

			int i = 1;
			while (rs.next()) {
				i += 1;
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
				if (i % 2000 == 0) {
					fos.write(sb.toString().getBytes());
					fos.flush();
					sb = new StringBuffer();
				}
			}

			if (sb.length() > 0) {
				fos.write(sb.toString().getBytes());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			FileInputStream fis = new FileInputStream(ftemp);
			ftemp.deleteOnExit();
			fos.close();
			return fis;
		}
	}

	public static ByteArrayOutputStream saveAsDomainListToTxtFile(HttpServletRequest request,
			HttpServletResponse response, SaveAsInfo saveAsInfo) throws IOException, WriteException {
		ColumnInfo columnInfo = null;
		Object obj = null;
		Object value = "";
		StringBuffer sb = new StringBuffer();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		if (saveAsInfo.isViewTitle()) {

			for (int i = 0; i < saveAsInfo.getColumnList().size(); i++) {
				columnInfo = (ColumnInfo) saveAsInfo.getColumnList().get(i);
				sb.append(columnInfo.getTitlecomment());
				if (i < saveAsInfo.getColumnList().size() - 1) {
					sb.append("\t");
				}
			}
			sb.append("\r\n");
		}

		try {
			List domainList = saveAsInfo.getDomainList();

			Reflect reflect = new Reflect(saveAsInfo.getDomainClass());

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
						value = reflect.getObjFieldValue(obj, columnInfo.getFieldName());
					}

					if ((!saveAsInfo.isShowCode()) && (value != null) && (!value.equals(""))) {
						value = getCodeDesc(request, columnInfo.getColumnName().toUpperCase(), value.toString());
					}

					if (value == null) {
						value = "";
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
	
	public static StringBuffer getGridDataByStringBuffer(String str,String commas){
	    StringBuffer sb = new StringBuffer();
	    List<Object> list = new ArrayList<Object>();
	    JSONArray json2bean = (JSONArray) JSonFactory.json2bean(str, JSONArray.class);
	    for (int i = 0; i < json2bean.size(); i++) {
			list = json2bean.getJSONArray(i);
			for (int j = 0; j < list.size(); j++) {
				sb.append("\""+list.get(j)+"\"");
				if(j<list.size()-1){
					sb.append(commas);
				}
			}
			if(i<json2bean.size()-1){
				sb.append("\r\n");
			}
		}
	    return sb;
	}
    public static StringBuffer getGridAllDataByStringBuffer(ParamDTO dto,IDao dao) throws Exception {
    	StringBuffer sb = new StringBuffer();
    	String gridHead = dto.getAsString("_gridHead_");
    	String commas = dto.getAsString("field_commas");
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
		String sqlStatementName = new String(DESCoderUtil.decrypt(decryptData,"reYj6fIsWGE="));
		int count =  (int) CountStatementUtil.autoGetTotalCount(sqlStatementName, dto, dao);
		List list = dao.queryForPage(sqlStatementName, dto, 0, count);
		String desc = "";
		String yab003 = "9999";
		Class c = Class.forName((String) resultType.get(0));
		Object obj = c.newInstance();
		BaseDomain baseDomain = null;
		Map map = new HashMap();
		Integer isHasHead = dto.getAsInteger("Set1");
		if(isHasHead==1){
			for (int n = 0; n < headName.size(); n++) {
				sb.append("\""+headName.get(n).toString()+"\"");
				if (n < headName.size() - 1) {
			        sb.append(commas);
				}
			}
		}
		for (int i = 0; i < list.size(); i++) {
			obj = list.get(i);
			if(obj instanceof BaseDomain){
				baseDomain = (BaseDomain) obj;
				map = baseDomain.toMap();
			}
			if(obj instanceof Map){
				map = (Map) obj;
			}
			IUser user = dto.getUserInfo();
			if (user != null) {
				yab003 = user.getYab003();
			}
			
			for (int j = 0; j < headName.size(); j++) {
				String text = "";
				if ("".equals(map.get(headId.get(j)))|| map.get(headId.get(j)) == null) {
					text ="";
				}else if (collection.size() > 0) {
					for (int k = 0; k < collection.size(); k++) {
						if (headId.get(j).equals(collection.get(k))) {
							desc = CodeTableLocator.getCodeDesc((String) headId.get(j),map.get(headId.get(j)).toString(), yab003);
							text = desc;
							break;
						}
						if (k == collection.size() - 1) {
							text = map.get(headId.get(j)).toString();
						}
					}
				}else{
					text = map.get(headId.get(j)).toString();
				}
				sb.append("\""+text+"\"");
				if (j < headName.size() - 1) {
			        sb.append(commas);
				}
			}
			if(i < list.size() -1 ){
				sb.append("\r\n");
			}
		}
    	return sb;
		
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
