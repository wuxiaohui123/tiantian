package com.yinhai.synthesis.util;

import java.math.BigDecimal;
import java.util.Date;

import com.yinhai.sysframework.util.DateUtil;

public interface Constant {

	/**
	 * 项目路径
	 */
	public final static String XMMC = "/yhsi3";
	/**
	 * 值班编号
	 */
	public final static String CTJG_ZBJL_DH = "CTJG-ZBJL-DH";
	public final static String CTJG_ZBJL_SW = "CTJG-ZBJL-SW";
	public final static String CTJG_ZBJL_JB = "CTJG-ZBJL-JB";
	public final static String LD_AAA010_NUM = "0";
	public final static String DH_AAA010_NUM = "1";
	public final static String SW_AAA010_NUM = "2";
	public final static String JB_AAA010_NUM = "3";
	/**
	 * Key
	 */
	public final static String MESSAGE = "message";

	/**
	 * 请求request返回key
	 */
	public final static String QUERY_LIST = "domainList";
	/**
	 * domain对象
	 */
	public final static String DOMAINOBJECT = "domainObject";

	public final static String EXCELFILEPATH = "";

	public final static String EXCELFILENAME = "ExcelFileName";

	/**
	 * 删除报表
	 */
	public final static String DELETEREPORT = "DeleteReport";

	/**
	 * EXCEL导出
	 */
	public final static String EXCELREPORT = "excelReport";

	/**
	 * 保存为EXCEL文件
	 */
	public final static String SAVEASTOEXCEL = "saveAsToExcel";

	/**
	 * 分割符
	 */
	public static final String COMMA = ",";

	/**
	 * 存储过程无错误
	 */
	public static final String PRC_APPCODE_NOERROR = "NOERROR";

	/**
	 * UTC时间
	 */
	public static final Date NULL_DATE = DateUtil.stringToDate("1900-01-01");

	/**
	 * 零
	 */
	public static final BigDecimal ZERO = new BigDecimal("0");

	/** ******助记码类型******************************* */

	/**
	 * 拼音
	 */
	public static final String YAE313_PY = "1";

	/**
	 * 五笔
	 */
	public static final String YAE313_WB = "2";
	
	/*********************************AAA008:建账标志*********************************/
	/**
	 * 建账标志(AAA008):否
	 */
	public static final String AAA008_0_F = "0";
	
	/**
	 * 建账标志(AAA008):是
	 */
	public static final String AAA008_1_S = "1";
	
	/*******************************AAA017:组织有效状态̬*******************************/
	/**
	 * 组织有效状态(AAA017):注销（含吊销、撤消等）
	 */
	public static final String AAA017_0_ZXHDXCXD = "0";
	
	/**
	 * 组织有效状态״(AAA017):正常
	 */
	public static final String AAA017_1_ZC = "1";
	
	/*****************************AAA021:工资水平发布级别*****************************/
	/**
	 * 工资水平发布级别(AAA021):全国
	 */
	public static final String AAA021_0_QG = "0";
	
	/**
	 * 工资水平发布级别(AAA021):省级
	 */
	public static final String AAA021_1_SJ = "1";
	
	/**
	 * 工资水平发布级别(AAA021):地市级
	 */
	public static final String AAA021_2_DSJ = "2";
	
	/**
	 * 工资水平发布级别(AAA021):区县级
	 */
	public static final String AAA021_3_QXJ = "3";
	
	/*********************************AAA026:运算类型*********************************/
	/**
	 * 运算类型(AAA026):定额或定值
	 */
	public static final String AAA026_001_DEHDZ = "001";
	
	/**
	 * 运算类型(AAA026):四则运算取值
	 */
	public static final String AAA026_002_SZYSQZ = "002";
	
	/**
	 * 运算类型(AAA026):JAVA函数调用取值
	 */
	public static final String AAA026_003_HSDYQZ = "003";
	
	/**
	 * 运算类型(AAA026):入参直接调用
	 */
	public static final String AAA026_004_RCZJDY = "004";
	
	/**
	 * 运算类型(AAA026):SQL调用取值
	 */
	public static final String AAA026_005_M = "005";
	
	/**
	 * 运算类型(AAA026):比较运算取值
	 */
	public static final String AAA026_006_BJYSQZ = "006";
	
	/**
	 * 运算类型(AAA026):存储过程调用取值
	 */
	public static final String AAA026_007_CCGCDYQZ = "007";
	
	/********************************AAA028:当事人类别********************************/
	/**
	 * 当事人类别(AAA028):组织
	 */
	public static final String AAA028_1_ZZ = "1";
	
	/**
	 * 当事人类别AAA028):人员
	 */
	public static final String AAA028_2_RY = "2";
	
	/**
	 * 当事人类别(AAA028):家庭
	 */
	public static final String AAA028_3_JT = "3";
	
	/******************************AAA033:当事人角色类型******************************/
	/**
	 * 当事人角色类型(AAA033):参保单位
	 */
	public static final String AAA033_010_CBDW = "010";
	
	/**
	 * 当事人角色类型(AAA033):代收机构
	 */
	public static final String AAA033_020_DSJG = "020";
	
	/**
	 * 当事人角色类型(AAA033):代发机构
	 */
	public static final String AAA033_030_DFJG = "030";
	
	/**
	 * 当事人角色类型(AAA033):定点医疗机构
	 */
	public static final String AAA033_040_DDYLJG = "040";
	
	/**
	 * 当事人角色类型(AAA033):职业介绍机构
	 */
	public static final String AAA033_050_ZYJSJG = "050";
	
	/**
	 * 当事人角色类型(AAA033):职业培训机构
	 */
	public static final String AAA033_060_ZYPXJG = "060";
	
	/**
	 * 当事人角色类型(AAA033):辅助器具配置机构
	 */
	public static final String AAA033_070_FZQJPZJG = "070";
	
	/**
	 * 当事人角色类型(AAA033):工伤定点康复机构
	 */
	public static final String AAA033_080_GSDDKFJG = "080";
	
	/**
	 * 当事人角色类型(AAA033):社保机构
	 */
	public static final String AAA033_090_SBJG = "090";
	
	/*******************************AAA052:定额征缴标志־*******************************/
	/**
	 * 定额征缴标志(AAA052):非定额
	 */
	public static final String AAA052_0_FDE = "0";
	
	/**
	 * 定额征缴标志(AAA052):定额
	 */
	public static final String AAA052_1_DE = "1";
	
	/*******************************AAA053:动态分账标志־*******************************/
	/**
	 * 动态分账标志(AAA053):否
	 */
	public static final String AAA053_0_F = "0";
	
	/**
	 * 动态分账标志(AAA053):是
	 */
	public static final String AAA053_1_S = "1";
	
	/*****************************AAA122:业务处理标志־*****************************/
	/**
	 * 业务处理标志(AAA122):未处理
	 */
	public static final String AAA122_0_WCL = "0";
	
	/**
	 * 业务处理标志(AAA122):已处理
	 */
	public static final String AAA122_1_YCL = "1";
	
	/*********************************AAE016:复核标志*********************************/
	/**
	 * 复核标志(AAE016):未复核
	 */
	public static final String AAE016_0_WFH = "0";
	
	/**
	 * 复核标志(AAE016):复核通过
	 */
	public static final String AAE016_1_FHTG = "1";
	
	/**
	 * 复核标志(AAE016):复核未通过
	 */
	public static final String AAE016_2_FHWTG = "2";
	
	/********************************AAA832:是否可回退********************************/
	/**
	 * 是否可回退(AAA832):否
	 */
	public static final String AAA832_0_F = "0";
	
	/**
	 * 是否可回退(AAA832):是
	 */
	public static final String AAA832_1_S = "1";
	
	/*******************************AAA018:业务审核总级次*******************************/
	/**
	 * 业务审核总级次（AAA018）:不审核
	 */
	public static final String AAA018_0_BSH = "0";
	/**
	 * 业务审核总级次（AAA018）:一级审核
	 */
	public static final String AAA018_1_YJSH = "1";
	/**
	 * 业务审核总级次（AAA018）:二级审核
	 */
	public static final String AAA018_2_EJSH = "2";
	/**
	 * 业务审核总级次（AAA018）:三级审核
	 */
	public static final String AAA018_3_SJSH = "3";
	
	
	/*******************************AAA121:业务类型*******************************/
	/**
	 * 业务类型（AAA121）:突发事件登记
	 */
	public static final String AAA121_A10010_TFSJDJ = "A10010";

	/**
	 * 业务类型（AAA121）:信息发布登记
	 */
	public static final String AAA121_A10012_XXFBDJ = "A10012";
	
	

	
	public static final String AAA121_A10015_ZBXXDJ = "A10015";


	
}
