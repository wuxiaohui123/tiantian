package com.yinhai.sysframework.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import com.alibaba.fastjson.JSONArray;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.persistence.ibatis.IDao;
import com.yinhai.sysframework.util.CollectionUtils;
import com.yinhai.sysframework.util.ExcelFileUtils;
import com.yinhai.sysframework.util.StringUtil;
import com.yinhai.sysframework.util.TxtFileUtils;
import com.yinhai.webframework.BaseAction;
@SuppressWarnings({"rawtypes","unchecked","unused","deprecation"})
public class ImportDataAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5487576291579144411L;
	private static final String SQL = "SELECT a.TABLE_NAME,b.COLUMN_NAME FROM user_tables a "
                                     + "LEFT JOIN User_Tab_Columns b ON a.TABLE_NAME = b.TABLE_NAME "
                                     + " ORDER BY a.TABLE_NAME,b.COLUMN_NAME";
	private String fileFileName;
	private String fileContentType;
	private File file;
	/**
	 * 获取当前数据库的表和表的字段
	 * @return
	 */
	public String getCurrentDataBaseTablesAndColumns(){
		List<Object[]> tableList = getHibernateDao().createSqlQuery(SQL, new Object[]{}).list();
    	Map<String,List<String>> m = new HashMap<String,List<String>>();
    	List<String> tables = new ArrayList<String>();
    	for (Object[] object : tableList) {
    		if(!tables.contains(String.valueOf(object[0]))){
    			tables.add(String.valueOf(object[0]));
    		}
			if(m.containsKey(String.valueOf(object[0]))){
			  List<String> colArray = (List<String>)m.get(object[0]);
			  colArray.add(String.valueOf(object[1]));
			  m.put(String.valueOf(object[0]), colArray);
			}else{
			  List<String> colArray = new ArrayList<String>();
			  colArray.add(String.valueOf(object[1]));
			  m.put(String.valueOf(object[0]), colArray); 
			}
		}
    	setData("tables", tables);
    	setData("tableCol", m);
    	return JSON;
    }

    /**上传文件并获取参数
     * @return
     */
    public String upLoadImportDataFile(){
    	String itype = request.getParameter("itypeid");
    	try {
    		String TemPath = saveFileToTemp();
			FileInputStream is = new FileInputStream(file);
			if("1".equals(itype)){//文本文件
				String oneLineStr = TxtFileUtils.getTxtInputStreamToStringByLineNum(is, 1);
				setData("oneline", oneLineStr);
			}else{//excel文件
				WorkbookSettings workbookSettings = new WorkbookSettings();
				workbookSettings.setEncoding("ISO-8859-1");
				Workbook book = Workbook.getWorkbook(is, workbookSettings);
				Sheet[] sheets = book.getSheets();
			    List<Map> list = new ArrayList<Map>();
			    Map<String,Object> map = null;
				for (Sheet sheet : sheets) {
					map = new HashMap<String,Object>();
					map.put("sheetName", sheet.getName());
					map.put("sheetColumns", sheet.getColumns());
					map.put("sheetRows", sheet.getRows());
					Cell[] cells = sheet.getRow(0);
					String[] cellArray = new String[cells.length];
					for (int i = 0; i < cells.length; i++) {
						cellArray[i] = cells[i].getContents();
					}
					map.put("oneRow", cellArray);
					list.add(map);
				}
				setData("sheetlist", list);
			}
			setData("realPath",TemPath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("上传文件失败！");
		}
		return JSON;
	}
    /**
     * 开始导入数据
     * @return
     * @throws AppException
     */
    public String startImportDataFormSubmit() throws AppException{
    	long starTime=System.currentTimeMillis();
    	ParamDTO dto = getDto();
    	String iType = dto.getAsString("iType");//导入文件类型
    	File file = getTempFile(dto.getAsString("realname"));//获取真实的临时文件名称
    	Integer imodel = Integer.valueOf(dto.getAsString("iModel"));
		int headrownum = dto.getAsInteger("fieldname_hs")!=null?dto.getAsInteger("fieldname_hs")-1:0;
    	FileInputStream is;
    	List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
    	String[] fieldArray = {};
    	Map<String,Object> datamap = null;
		try {
			is = new FileInputStream(file);//创建文件输入流
			//Text文本文件
			String separator = dto.getAsString("field_commas");//字段分隔符
			//获取数据开始行
			int rowStart = dto.getAsInteger("frist_datah") !=null ? dto.getAsInteger("frist_datah") : 0;
			rowStart = rowStart > 0 ? rowStart - 1 : 0;
			if("1".equals(iType)){
				//将文本文件装好为一行为单位的字符串数组
				String[] Strlist = TxtFileUtils.getTxtInputStreamToStringLineByCharset(is,dto.getAsString("encode")).split("\\n");
				//获取数据结束行
				int rowEnd = dto.getAsInteger("last_datah")!=null ?dto.getAsInteger("last_datah"):Strlist.length;
				rowEnd = rowEnd > Strlist.length ? Strlist.length : rowEnd;
				//将获取的目标表数据字符串转换为List
				List<Map<String, Object>> targetList = JSONStringToArrayList(dto.getAsString("targetTable")); 
				//获取目标表的真实名称，因文件类型是text文件，故该表格有且只有一条数据
				String targetTableName = String.valueOf(targetList.get(0).get("TargetTb"));//目标表名称
				//获取该目标表是否需要新建数据库表的boolean值，ture为需要新建，false不需要新建
				boolean isCreate = Boolean.valueOf(String.valueOf(targetList.get(0).get("CreateTb")));//获取目标表是否创建新表的布尔值
				//如果是需要新建表
				if(isCreate){
					//根据目标表名称获取创建该数据库表所需要的字段名称、数据类型、数据长度及是否是主键等信息的字符串
					String createStr = dto.getAsString(targetTableName);
					//执行创建数据库表的方法并返回创建成功后的表的字段的list
					List<String> fieldlist = ImprotDataCreateTable(imodel,targetTableName,JSONStringToArrayList(createStr));
					//将字段list转换为字符串数组
					datamap = MappingTargetFieldToSourceField(fieldlist, Strlist[headrownum], separator);
					fieldArray = (String[]) datamap.keySet().toArray(new String[fieldlist.size()]);
				}else{//否则，不是新建表
					//根据目标表名称获取创建该数据库表所需要的字段名称、数据类型、数据长度及是否是主键等信息的字符串 将该字符串转换为list
					List<Map<String, Object>> importList = JSONStringToArrayList(dto.getAsString(targetTableName));
					//获取数据库表字段与文件所在数据列的映射关系，数据库表字段-->文本文件的列号（如：AAA100-->1），。。。。。。
					datamap = MappingDatabaseFieldsToSourceFields(dto,importList,targetTableName,Strlist[headrownum], separator);
					//fieldArray = StringUtil.objectArrayToStringArray((Object[]) datamap.get("filedlist"));
					datamap.remove("filedlist");
				}
				//将Text文本文件内容转换为导入的标准数据集合
				datalist = StringArrayToArrayList(imodel,Strlist, datamap, separator, rowStart, rowEnd);
				executeImportDataByModel(dto, targetTableName, fieldArray, datalist);
			}else{//Excel文件
				//获取数据结束行
				WorkbookSettings workbookSettings = new WorkbookSettings();
				workbookSettings.setEncoding("ISO-8859-1");
				Workbook book = Workbook.getWorkbook(is, workbookSettings);
				List<Map<String, Object>> list = JSONStringToArrayList(dto.getAsString("targetTable"));
				for (Map<String, Object> map : list) {
					boolean isCreate = Boolean.valueOf(String.valueOf(map.get("CreateTb")));
					String tableName = String.valueOf(map.get("TargetTb"));
					Sheet sheet = book.getSheet(tableName);
					//String cellString = StringUtil.StringArrayToString(ExcelFileUtils.SheetCellsToStringArray(sheet.getRow(headrownum)),",");
					if(isCreate){
						List<String> fieldlist = ImprotDataCreateTable(imodel, tableName, JSONStringToArrayList(dto.getAsString(tableName)));
						//将字段list转换为字符串数组
						//datamap = MappingTargetFieldToSourceField(fieldlist, cellString, ",");
						fieldArray = (String[]) datamap.keySet().toArray(new String[fieldlist.size()]);
					}else{
						//根据目标表名称获取创建该数据库表所需要的字段名称、数据类型、数据长度及是否是主键等信息的字符串 将该字符串转换为list
						List<Map<String, Object>> importList = JSONStringToArrayList(dto.getAsString(tableName));
						//获取数据库表字段与文件所在数据列的映射关系，数据库表字段-->文本文件的列号（如：AAA100-->1），。。。。。。
						//datamap = MappingDatabaseFieldsToSourceFields(dto,importList,tableName, cellString, ",");
						//fieldArray = StringUtil.objectArrayToStringArray((Object[]) datamap.get("filedlist"));
						datamap.remove("filedlist");
					}
					datalist = ExcelFileUtils.getSheetToMapList(sheet, imodel, datamap, rowStart, dto.getAsInteger("last_datah"));
					executeImportDataByModel(dto, tableName, fieldArray, datalist);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setMsg("导入数据失败，原因可能是【"+e.getMessage()+"】，请联系维护人员！！", "error");
			throw new AppException(e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		setData("info","{'type':"+iType+",'model':"+imodel+",'size':"+datalist.size()+",'time':"+(endTime-starTime)+"}");
		setMsg("导入数据成功！！", "success");
    	return JSON;
    }
    
    public void executeImportDataByModel(ParamDTO dto,String tableName,String[] field,List<Map<String, String>> list){
    	//String fieldstr = StringUtil.StringArrayToString(field,",");
    	//String paramstr = StringUtil.splitJointStringByNumber("?", ",", field.length);
    	//String whereString = StringUtil.splitJointStringByStringArray(field, " = ", "?", " AND ",false);
    	IDao dao = getDao();
    	String imodel = dto.getAsString("iModel");
     	if("1".equals(imodel)){//添加
    		//String insertSql = "INSERT INTO " + tableName + "(" + fieldstr +") VALUES(" + paramstr + ")";
    		//dao.executeBatchByJDBC(insertSql, list);
    	}else if ("2".equals(imodel)) {//更新
    		String[] updateArray =   StringUtil.split(dto.getAsString(tableName+"updateArray"),",");
        	String[] primaryArray = StringUtil.split(dto.getAsString(tableName+"primaryArray"),",");
        	//String setString = StringUtil.splitJointStringByStringArray(updateArray," = ", "?", ",",true);
        	//String updateWhereString =  StringUtil.splitJointStringByStringArray(primaryArray, " = ", "?", " AND ",false);
    		//String updateSql = "UPDATE " + tableName + " SET " + setString + " WHERE 1 = 1 " + updateWhereString;
    		//dao.executeBatchByJDBC(updateSql, list);
    	}else if ("3".equals(imodel)) {//添加或更新
    		//String deleteSql = "DELETE FROM " + tableName + " WHERE 1 = 1 " + whereString;
    		//String insertSql = "INSERT INTO " + tableName + "(" + fieldstr +") VALUES(" + paramstr + ")";
    		//dao.executeBatchByJDBC(deleteSql, list);
    		//dao.executeBatchByJDBC(insertSql, list);
    	}else if ("4".equals(imodel)) {//删除
    		//String deleteSql = "DELETE FROM " + tableName + " WHERE 1 = 1 " + whereString;
    		//dao.executeBatchByJDBC(deleteSql, list);
    	}else{//复制
    		//String deleteSql = "DELETE FROM " + tableName + " WHERE 1 = 1 " + whereString;
    		//String insertSql = "INSERT INTO " + tableName + "(" + fieldstr +") VALUES(" + paramstr + ")";
    		//dao.executeBatchByJDBC(deleteSql, list);
    		//dao.executeBatchByJDBC(insertSql, list);
    	}
    }
    /**
     * 
     * @param strArray
     * @param fieldArray
     * @param separator
     * @param rowstart
     * @param rowend
     * @return
     */
    private List<Map<String, String>> StringArrayToArrayList(Integer model,String[] strArray,Map<String, Object> datamap,String separator,int rowstart,int rowend){
    	List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    	Map<String,String> map = null;
    	for (int i = rowstart; i < rowend; i++) {
    		map = model==2?new LinkedHashMap<String,String>():new TreeMap<String,String>();
			String[] tempArray = strArray[i].split(separator);	
			for (String key : datamap.keySet()) {
				int j =  Integer.valueOf((String)datamap.get(key))-1;
				map.put(key, tempArray[j].replace("\"", ""));
			}
			list.add(map);
		}
    	return list;
    }
    /**
     * 将JSON字符串转换为List
     * @param jsonString
     * @return
     */
    private List<Map<String,Object>> JSONStringToArrayList(String jsonString){
    	if(StringUtil.isBlank(jsonString)){
    		return null;
    	}else{
    		JSONArray array = JSONArray.parseArray(jsonString);
    		List<Map<String,Object>> list = JSONArray.toJavaObject(array, List.class);
    		return list;
    	}
    }
    /**
     * 创建新的数据库表
     * @param tableName
     * @param list
     */
	private List<String> ImprotDataCreateTable(Integer imodel,String tableName,List<Map<String,Object>> list){
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
		List<String> resultlist = new ArrayList<String>();
		List<String> primarylist = new ArrayList<String>();
    	Connection conn = null; 
    	StringBuffer createSb = new StringBuffer();
		StringBuffer primarySb = new StringBuffer();
		for (Map<String,Object> map : list) {
			if((boolean)map.get("checkbox")){
				String field = String.valueOf(map.get("targetField")).replaceAll(regEx, "").trim();
				createSb.append(field).append(" ").append(String.valueOf(map.get("fieldType")).trim());
				createSb.append("(").append(String.valueOf(map.get("fieldLength")).trim()).append("),");
				resultlist.add(field);
			}
			if((boolean)map.get("primaryKey")){
				String primary = String.valueOf(map.get("targetField")).replaceAll(regEx, "").trim();
				primarySb.append(primary).append(",");
				primarylist.add(primary);
			}
		}
		if(imodel==2&&primarylist!=null&&primarylist.size()>0){
			for (String s : primarylist) {
				resultlist.remove(s);
			}
			resultlist.addAll(resultlist.size(), primarylist);
		}
    	try {  
    	   conn = getHibernateDao().getSessionFactory().getCurrentSession().disconnect();
    	   DatabaseMetaData metaDate = conn.getMetaData();  
    	   ResultSet rs = metaDate.getTables(null, null, tableName, new String[] { "TABLE" });  
    	   if (rs.next()) {  
    	       System.out.println(tableName + "： 表存在! ");  
    	   } else {  
    		   String createStr = createSb.toString().trim();
    		   String primaryStr = primarySb.toString().trim();
    		   if(!StringUtil.isBlank(createStr)){
    			   String create = StringUtil.isBlank(primaryStr)?createStr.substring(0, createStr.length()-1):createStr;
    			   String primary = StringUtil.isBlank(primaryStr)?"":primaryStr.substring(0, primaryStr.length()-1);
    			   String primaryString = StringUtil.isBlank(primary)?"":"CONSTRAINT "+tableName.toUpperCase()+"_PK PRIMARY KEY("+primary+")";
    			   String sql = "CREATE TABLE " + tableName +"(" + create + primaryString + ")";
    			   conn.prepareStatement(sql).execute();
    		   }
    	   } 
        } catch (Exception e) {
    	     e.printStackTrace();  
    	}
    	return resultlist;
    }
	/**
	 * 数据库字段到源字段的映射
	 * @return list
	 */
	private Map<String,Object> MappingDatabaseFieldsToSourceFields(ParamDTO dto,List<Map<String, Object>> importList,String tableName,String headRowString,String regex){
		String[] headrow = headRowString.split(regex);
		Map<String,Object> map = null;
		List<String> filedlist = new ArrayList<String>();
		Integer imodel = Integer.valueOf(dto.getAsString("iModel"));
		if(imodel==2){
			map = new LinkedHashMap<String,Object>();
			String[] updateArray =   StringUtil.split(dto.getAsString(tableName+"updateArray"),",");
	    	String[] primaryArray = StringUtil.split(dto.getAsString(tableName+"primaryArray"),",");
	    	String[] array = ArrayUtils.addAll(updateArray, primaryArray);
	    	for (String s : array) {
				Integer index = getArrayIndexByElement(s, headrow);
				map.put(s, index.toString());
				filedlist.add(s);
			}
	    	map.put("filedlist", headrow);
		}else{
			map = new TreeMap<String,Object>();
			for (Map m : importList) {
				if((boolean)m.get("checkbox")){
					String field = String.valueOf(m.get("targetField"));
					Integer index = getArrayIndexByElement(String.valueOf(m.get("sourceField")), headrow);
					map.put(field, index.toString());
					filedlist.add(field);
				}
			}
			map.put("filedlist", filedlist.toArray());
		}
		return map;
	}
	
	private Map<String,Object> MappingTargetFieldToSourceField(List<String> list,String headRowString,String regex){
		String[] headrow = headRowString.split(regex);
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		for (String s : list) {
			Integer index = getArrayIndexByElement(s, headrow);
			map.put(s, index.toString());
		}
		return map;
	}
	/**
	 * 获取数组包含元素的索引值，从1开始，不包含改元素则返回0
	 * @param element 元素
	 * @param array  数组
	 * @return
	 */
	private int getArrayIndexByElement(String element,String[] array){
		int index = 0;
		if((!StringUtil.isBlank(element))&&array!=null&&array.length>0){
			for (int i = 0; i < array.length; i++) {
				String tmp = array[i].trim().replace("\"", "");
				if(element.equals(tmp)){
					index = i+1;
				}
			}
		}
		return index;
	}
    /**
     * 获取文件
     * @param tempFileName
     * @return
     */
    private File getTempFile(String tempFileName){
    	String path = getSystemTempDir()+"\\"+tempFileName;
    	File f = new File(path);
    	if(f.isFile()){
    		return f;
    	}
    	return null;
    }
    /**
     * 保存为临时文件
     * @return
     * @throws IOException
     */
    private String saveFileToTemp() throws IOException{ 
    	String tempPath = System.currentTimeMillis()+"_"+getFileFileName();
        InputStream in = new FileInputStream(getFile());
        //创建一个文件输出流
        FileOutputStream out = new FileOutputStream(getSystemTempDir() + "\\" +tempPath);
        byte buffer[] = new byte[1024];
        int i = 0;
        while ((i = in.read(buffer)) >0) {
           out.write(buffer, 0, i);
        }
        if(in!=null){
        	in.close();
        }
        if(out!=null){
        	out.close();
        }
        return tempPath;
    }
    /**
     * 删除存在的临时文件
     * @return
     */
    public String deleteTempFiles(){
    	String tempFiles = request.getParameter("files");
    	String tempDir = getSystemTempDir();
    	if(tempFiles!=null){
    		String[] fileList = tempFiles.split(",");
    		for (String str : fileList) {
    			File file = new File(tempDir+"\\"+str);
    			if (file.exists()) {
    				file.delete();
    			}
    		}
    	}
    	return JSON;
    }
    /**
     * 获取临时文件存储目录
     * @return
     */
    private String getSystemTempDir(){
    	//E:\Tomcat\tomcat7\apache-tomcat-7.0.57\temp
    	String tmpdir = System.getProperty("java.io.tmpdir");
    	File f = new File(tmpdir);
    	if(f.isDirectory()){
    		return System.getProperty("java.io.tmpdir");
    	}else{
    		return SysConfig.getSysConfig("tempDir");
    	}
    }
	public String getFileFileName() {
		return fileFileName;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	public String getFileContentType() {
		return fileContentType;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
}
