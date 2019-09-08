package com.yinhai.synthesis.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;

import com.yinhai.synthesis.service.SynthesisService;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.util.ValidateUtil;

@Namespace("/process/synthesis")
@Action(value = "suggestFrameworkAction")
@SuppressWarnings({ "rawtypes", "unused" })
public class SuggestFrameworkAction extends SynthesisAction {

	private SynthesisService synthesisService = (SynthesisService) getService("synthesisService");

	/**
	 * newRPC处理方式 获取人员信息 String
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAc01String() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab139", dto.getUserInfo().getOrgId());
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('证件类型','证件号码','姓名','人员ID','性别','出生日期','单位名称','医保编号','养老编号','失业编号','参保地')"); // 需要在下拉列表中显示的列
		String sql = new String();
		sql = "suggestFramework.getAc01String"; // 精确查找
		setData("data", baseUtil_newRPC(dto, sb, sql));
		return JSON;
	}

	/**
	 * newRPC处理方式 获取人员信息 List
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAc01List() throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getAc01List", dto);
		setList("dg0", list);
		return JSON;
	}

	/**
	 * 
	 * @param dto
	 *            前台页面传入参数
	 * @param sb
	 *            所要展示下拉列表的列配置
	 * @param sql
	 *            列表所需SQLID
	 * @return
	 * @throws Exception
	 */
	public String baseUtil(ParamDTO dto, StringBuffer sb, String sql) throws Exception {
		dto.put("inputstring", dto.getAsString("jstj"));
		StringBuffer temp = sb;
		try {
			List list = super.getDao().queryForList(sql, dto);
			for (int i = 0; i < list.size(); i++) {
				temp.append(",new Array(");
				temp.append(((Map) list.get(i)).get("outstring").toString());
				temp.append(")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		temp.append(")");
		return temp.toString();
	}

	/**
	 * 
	 * @param dto
	 *            前台页面传入参数
	 * @param sb
	 *            所要展示下拉列表的列配置
	 * @param sql
	 *            列表所需SQLID
	 * @return
	 * @throws Exception
	 */
	public String baseUtil_newRPC(ParamDTO dto, StringBuffer sb, String sql) throws Exception {
		StringBuffer temp = sb;
		try {
			List list = super.getDao().queryForList(sql, dto);
			for (int i = 0; i < list.size(); i++) {
				temp.append(",new Array(");
				temp.append(((Map) list.get(i)).get("outstring").toString());
				temp.append(")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		temp.append(")");
		return temp.toString();
	}

	/**
	 * 单位信息查询newRPC的Rpc需要
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAb01String() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", dto.getUserInfo().getOrgId());
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('单位管理码','单位ID','单位名称','参保所在地','医保编号','养老编号','失业编号')"); // 需要在下拉列表中显示的列
		String inputString = dto.getAsString("inputString");
		String sql = new String();
		if (!ValidateUtil.isEmpty(inputString)) {
			sql = "suggestFramework.getAab001String"; // 精确查询
			setData("data", baseUtil_newRPC(dto, sb, sql));
		}
		return JSON;
	}

	/**
	 * 单位信息查询newRPC的弹出窗口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAb01List() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", dto.getUserInfo().getOrgId());
		List list = getDao().queryForList("suggestFramework.getAab001List", dto);
		System.out.println(list);
		setList("dg1", list);
		return JSON;
	}

	/**
	 * 单位信息查询newRPC的Rpc需要
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAb01String_q() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", dto.getUserInfo().getOrgId());
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('单位管理码','单位ID','单位名称','参保所在地','医保编号','养老编号','失业编号')"); // 需要在下拉列表中显示的列
		String inputString = dto.getAsString("inputString");
		String sql = new String();
		if (!ValidateUtil.isEmpty(inputString)) {
			sql = "suggestFramework.getAab001_qString"; // 精确查询
			setData("data", baseUtil_newRPC(dto, sb, sql));
		}
		return JSON;
	}

	/**
	 * 单位信息查询newRPC的弹出窗口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAb01List_q() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", dto.getUserInfo().getOrgId());
		List list = getDao().queryForList("suggestFramework.getAab001_qList", dto);
		System.out.println(list);
		setList("dg1", list);
		return JSON;
	}

	/**
	 * 税务机构信息newRPC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getSWInfoString() throws Exception {
		ParamDTO dto = getDto();
		StringBuffer sb = new StringBuffer();
		System.out.println(dto);
		String sql = "suggestFramework.getSWInfoString"; // 获取下拉列表数据所对应的SQLID
		sb.append("new Array(new Array('税务机构ID','税务机构名称','税号')"); // 需要在下拉列表中显示的列
		setData("data", baseUtil_newRPC(dto, sb, sql));
		return JSON;
	}

	/**
	 * 税务机构信息newRPC新窗口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getSWInfoList() throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getSWInfoList", dto);
		setList("dg2", list);
		return JSON;
	}

	/**
	 * 信息newRPC新窗口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getBankString() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('开户银行账号ID','银行名称','金融机构编码')");
		String sql = "suggestFramework.getDwBankString"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil_newRPC(getDto(), sb, sql));
		return JSON;
	}

	/**
	 * 信息newRPC新窗口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getBankList() throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getDwBankList", dto);
		setList("dg3", list);
		return JSON;
	}

	/**
	 * 获取基本养老征缴银行账户信息_基金收款、付款登帐用
	 * 
	 * @update 2013/4/15 17:26
	 * @return
	 * @throws Exception
	 */
	/**
	 * 获取基本养老征缴银行账户信息
	 * 
	 * @update 2013/4/15 17:26
	 * @return
	 * @throws Exception
	 */
	public String getBankInjbString() throws Exception {
		ParamDTO dto = getDto();
		String yab139 = getDto().getUserInfo().getOrgId();
		dto.put("yab139", yab139);
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('银行','行业编号','银行名称','帐号','开户名称')");// '账号ID',
		String sql = "suggestFramework.getBankInjbByAsnString"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil(dto, sb, sql));
		return JSON;
	}
	/**
	 * 获取基本养老征缴银行账户信息_基金收款(特权)用 没有套帐类型充入待转金
	 * 
	 * @update 2013/4/15 17:26
	 * @return
	 * @throws Exception
	 */
    public String getBankInjbTqString() throws Exception {
		ParamDTO dto = getDto();
		String yab139 = getDto().getUserInfo().getOrgId();
		dto.put("yab139", yab139);
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('银行','开户名称','帐号','套账类型')");
		String sql = "suggestFramework.getBankInjbTqByAsnString"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil(dto, sb, sql));
		return JSON;
	}
	
	/**
	 * 获取基本养老征缴银行账户信息_基金收款、付款登帐用
	 * 
	 * @update 2013/4/15 17:26
	 * @return
	 * @throws Exception
	 */
	/**
	 * 获取基本养老征缴银行账户信息
	 * 
	 * @update 2013/4/15 17:26
	 * @return
	 * @throws Exception
	 */
	public String getBankInjbList() throws Exception {
		ParamDTO dto = getDto();
		String yab139 = getDto().getUserInfo().getOrgId();
		dto.put("yab139", yab139);
		List list = getDao().queryForList("suggestFramework.getBankInjbByAsnList", dto);
		setList("dg4", list);
		return JSON;
	}
	
	/**
	 * 获取基本养老征缴银行账户信息_基金收款(特权)用 没有套帐类型充入待转金
	 * 
	 * @update 2013/4/15 17:26
	 * @return
	 * @throws Exception
	 */
    public String getBankInjbTqList() throws Exception {
    	ParamDTO dto = getDto();
        String yab139 = getDto().getUserInfo().getOrgId();
        dto.put("yab139", yab139);
        List list = getDao().queryForList("suggestFramework.getBankInjbTqByAsnList", dto);
        setList("dg4", list);
        return JSON;
  	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取单位和人员信息
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:17
	 */
	public String getDSRXXString() throws Exception {
		ParamDTO dto = getDto();
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('当事人类别','当事人ID','当事人名称','当事人外部标识')"); // 需要在下拉列表中显示的列
		String sql = "suggestFramework.getDSRXXString"; // 获取下拉列表数据所对应的SQLID
		dto.put("yab139", dto.getUserInfo().getOrgId());
		setData("data", baseUtil_newRPC(dto, sb, sql));
		return JSON;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取单位和人员信息
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:17
	 */
	public String getDSRXXList() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab139", dto.getUserInfo().getOrgId());
		String sql = "suggestFramework.getDSRXXList"; // 获取下拉列表数据所对应的SQLID
		List list = getDao().queryForList("suggestFramework.getDSRXXList", dto);
		setList("dg5", list);
		return JSON;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取失业代管单位信息 精确查询
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:17
	 */
	public String getAb01a1String() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", dto.getUserInfo().getOrgId());
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('失业代管单位编号','失业代管单位名称')");
		String sql = "suggestFramework.getAb01a1String";
		setData("data", baseUtil_newRPC(dto, sb, sql));
		return JSON;
	}

	/**
	 * 获取失业代管单位信息 模糊查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAb01a1List() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", dto.getUserInfo().getOrgId());
		List list = getDao().queryForList("suggestFramework.getAb01a1List", dto);
		setList("dg8", list);
		return JSON;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取征集单ID
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:17
	 */
	public String getAAZ288String() throws Exception {
		ParamDTO dto = getDto();
		if (ValidateUtil.isEmpty(dto.getAsString("inputString"))) {
			dto.put("inputString", dto.getAsString("jstj"));
		}
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('征集单ID')"); // 需要在下拉列表中显示的列
		String sql = "suggestFramework.getAAZ288String"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil_newRPC(dto, sb, sql));
		return JSON;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取征集单ID
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:17
	 */
	public String getAAZ288List() throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getAAZ288List", dto);
		setList("dg6", list);
		return JSON;
	}

	/**
	 * 查询原参保地信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getYac256String() throws Exception {
		StringBuffer sb = new StringBuffer();
		// suggestframework列表头
		sb.append("new Array(new Array('原参保地区名称','原参保机构行政区划代码 ','原参保机构名称 ','原参保机构联系电话 ','原参保机构地址','原参保机构邮编')");
		// 获取下拉列表数据所对应的SQLID
		String sql = "suggestFramework.getYac256String";
		// 返回页面值
		setData("data", baseUtil_newRPC(getDto(), sb, sql));
		return JSON;
	}

	/**
	 * 查询原参保地信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getYac256List() throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getYac256List", dto);
		setList("dg7", list);
		return JSON;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取个人基本信息 不按经办机构
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:22
	 */
	public String getAac001_c() throws Exception {
		ParamDTO dto = getDto();
		String jstj = dto.getAsString("inputString");
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('证件号码','姓名','人员ID','性别','出生日期','单位名称','医保编号','养老编号','失业编号','参保地')"); // 需要在下拉列表中显示的列
		String sql = new String();
		if (!ValidateUtil.isEmpty(jstj)) {
			/*
			 * 判断前台输入的个人编号或者姓名或者证件号码是否存在~ 如果不存在 则精确查询 如果存在 则模糊查询
			 */
			if (jstj.indexOf("~") < 0) {
				sql = "suggestFramework.getAc01ListByAsn_jq"; // 获取下拉列表数据所对应的SQLID
																// 精确查询
			} else if (jstj.indexOf("~") >= 0) {
				dto.put("jstj", jstj.replace("~", ""));
				sql = "suggestFramework.getAc01ListByAsn"; // 获取下拉列表数据所对应的SQLID
															// 模糊查询
			}
			// 全部为模糊查询
			setData("data", baseUtil_newRPC(dto, sb, sql));
		}
		return JSON;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * @description comm 获取单位基本信息（公共业务 查询使用[没有带上单位与组织状态]）
	 * @version 1.0
	 * @author Haword
	 * @update 2012-12-27 下午03:07:17
	 */
	public String getAab001_q() throws Exception {
		ParamDTO dto = getDto();
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('单位管理码','单位ID','单位名称','参保所在地','医保编号','养老编号','失业编号')"); // 需要在下拉列表中显示的列
		String jstj = dto.getAsString("jstj");
		dto.put("inputString", jstj);
		String sql = new String();
		if (!ValidateUtil.isEmpty(jstj)) {
			/*
			 * 判断前台输入的单位编号或者单位管理码或者单位名称是否存在~ 如果不存在 则模糊查询 如果存在 则精确查询
			 */
			if (jstj.indexOf("~") < 0) {
				sql = "suggestFramework.getAb01ListByAsnQ"; // 获取下拉列表数据所对应的SQLID
															// 模糊查询
			} else if (jstj.indexOf("~") >= 0) {
				dto.put("jstj", jstj.replace("~", ""));
				sql = "suggestFramework.getAb01ListByAsnQ_jq"; // 获取下拉列表数据所对应的SQLID
																// 精确查询
			}
			setData("data", baseUtil(dto, sb, sql));
		}
		return JSON;
	}

	/**
	 * 获取指定险种的个人基本信息 说明: 1.aac001,aac002,aac003 页面输入框的值,不用like查询 2.yab003,aae140
	 * 作为关键过滤条件,必须传入
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getAc01ByAae140ForYL() throws Exception {
		ParamDTO dto = getDto();
		StringBuffer sb = new StringBuffer();
		String jstj = dto.getAsString("jstj");
		dto.put("inputString", jstj);
		// 分中心也作为查询条件
		if (ValidateUtil.isEmpty(jstj)) {
			dto.put("yab139", dto.getUserInfo().getOrgId());
		}
		// suggestframework列表头
		sb.append("new Array(new Array('公民身份号码','姓名','人员ID','性别','老医保号','出生日期','单位名称')");
		// 获取下拉列表数据所对应的SQLID
		String sql = "suggestFramework.getAc01ByAae140ForYL";
		// 返回页面值
		setData("data", baseUtil(dto, sb, sql));
		return JSON;
	}

	/**
	 * 获取组织机构基本信息 
	 * String
	 * @return
	 * @throws Exception
	 * @description  获取组织机构基本信息
	 */
	public String getAaz001String() throws Exception {
		ParamDTO dto = getDto();
		StringBuffer sb = new StringBuffer();
		sb.append("new Array(new Array('机构编码','机构名称')"); // 需要在下拉列表中显示的列
		String sql = "suggestFramework.getAe31StringByAsn"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil_newRPC(dto, sb, sql));
		return JSON;
	}
	
	/**
	 * 获取组织机构基本信息 
	 * List
	 * @return
	 * @throws Exception
	 */
	public String getAaz001List() throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getAe31ListByAsn", dto);// 获取数据
		setList("dg10", list);
		return JSON;
	}
	
	/**
	 * 获取相关险种的银行账户信息(转入)
	 * 
	 * @update 2013/8/19 19:26
	 * @return
	 * @throws Exception
	 */
	public String getBankInByAae140String() throws Exception {
		ParamDTO dto = getDto();
		String inputString = dto.getAsString("inputString");
		if (!ValidateUtil.isNotEmpty(inputString)) {
			String yab139 = getDto().getUserInfo().getOrgId();
			dto.put("yab139", yab139);
			StringBuffer sb = new StringBuffer();
			sb.append("new Array(new Array('银行编号','银行名称','账号','开户名','机构编码')");
			String sql = "suggestFramework.getBankInByAae140String"; // 获取下拉列表数据所对应的SQLID
			setData("data", baseUtil_newRPC(dto, sb, sql));
		}
		return JSON;
	}
	/**
	 * (模糊)获取相关险种的银行账户信息(转入)
	 * @return
	 * @throws Exception
	 */
	public String getBankInByAae140List() throws Exception {
		ParamDTO dto = getDto();
		String yab139 = getDto().getUserInfo().getOrgId();
		dto.put("yab139", yab139);
		List list = getDao().queryForList("suggestFramework.getBankInByAae140List");
		setList("dg11", list);
		return JSON;
	}
	/**
	 * (精确)获取相关险种的银行账户信息（转出）
	 * 
	 * @update 2013/8/19 19:26
	 * @return
	 * @throws Exception
	 */
	public String getBankOutByAae140String () throws Exception {
		ParamDTO dto = getDto();
		String inputString = dto.getAsString("inputString");
		if (!ValidateUtil.isNotEmpty(inputString)) {
			StringBuffer sb = new StringBuffer();
			sb.append("new Array(new Array('银行编号','银行名称','机构编码','银行地址(省)','银行地址(市、县)','账号','开户名')");
			String sql = "suggestFramework.getBankOutByAae140String"; // 获取下拉列表数据所对应的SQLID
			setData("data", baseUtil_newRPC(dto, sb, sql));
		}
		return JSON;
	}
	/**
	 * (模糊)获取相关险种的银行账户信息（转出）
	 */
	public String getBankOutByAae140List () throws Exception {
		ParamDTO dto = getDto();
		List list = getDao().queryForList("suggestFramework.getBankOutByAae140List");
		setList("dg12", list);
		return JSON;
	}
	
	/**
	 * 获取个人基本信息(带经办机构ac60) String
	 * @return
	 * @throws Exception
	 * @description 获取个人基本信息(带经办机构ac60)
	 */
	public String getAac001DYAC60String() throws Exception {
		StringBuffer sb = new StringBuffer();
		getDto().put("yab003", getDto().getUserInfo().getOrgId());
		getDto().put("aae140", getDto().getAsString("aae140"));
		sb.append("new Array(new Array('公民身份号码','姓名','人员ID','性别','年龄','单位名称','出生年月')"); // 需要在下拉列表中显示的列
		String sql = "suggestFramework.getAc01ListByAsnDYAC60String"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil_newRPC(getDto(), sb, sql));
		return JSON;
	}
	
	/**
	 * 获取个人基本信息(带经办机构ac60) List
	 * @return
	 * @throws Exception
	 * @description 获取个人基本信息(带经办机构ac60)
	 */
	public String getAac001DYAC60List() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", getDto().getUserInfo().getOrgId());
		List list = getDao().queryForList("suggestFramework.getAc01ListByAsnDYAC60List",dto);
		setList("dg13", list);
		return JSON;
	}
	
	/**
	 * 获取个人基本信息(带经办机构) String
	 * @return
	 * @throws Exception
	 * @description 获取个人基本信息(带经办机构)
	 */
	public String getAac001DYString() throws Exception {
		StringBuffer sb = new StringBuffer();
		getDto().put("yab003", getDto().getUserInfo().getOrgId());
		getDto().put("aae140", getDto().getAsString("aae140"));
		String str = "new Array(new Array('公民身份号码','姓名','人员ID','性别','年龄','单位编号','单位名称')";
		sb.append(str); // 需要在下拉列表中显示的列
		String sql = "suggestFramework.getAc01ListByAsnDYString"; // 获取下拉列表数据所对应的SQLID
		String data = baseUtil_newRPC(getDto(), sb, sql);
		if("410".equals(getDto().getAsString("aae140")) && ValidateUtil.isEmpty(data)){
			// 如果为工伤且未找到对应基本信息则读取是否为建安工程人员
			sb = new StringBuffer();
			sb.append(str);
			sql = "suggestFramework.getAc01ListByAsnGsJaString"; // 获取下拉列表数据所对应的SQLID
			data = baseUtil_newRPC(getDto(), sb, sql);
		}
		setData("data", data);
		return JSON;
	}
	
	/**
	 * 获取个人基本信息(带经办机构) List
	 * @return
	 * @throws Exception
	 * @description 获取个人基本信息(带经办机构)
	 */
	public String getAac001DYList() throws Exception {
		ParamDTO dto = getDto();		
		dto.put("yab003", getDto().getUserInfo().getOrgId());
		dto.put("aae140", getDto().getAsString("aae140"));		
		List list = getDao().queryForList("suggestFramework.getAc01ListByAsnDYList",dto);
		if("410".equals(dto.getAsString("aae140")) && ValidateUtil.isEmpty(list)){
			list = getDao().queryForList("suggestFramework.getAc01ListByAsnGsJaList",dto);
		}
		setList("dg14", list);
		return JSON;
	}
	
	/**
	 * 获取个人基本信息(不带经办机构) String
	 * @return
	 * @throws Exception
	 * @description 获取个人基本信息(不带经办机构)
	 */
	public String getAac001DyAllString() throws Exception {
		StringBuffer sb = new StringBuffer();
		getDto().put("yab003", getDto().getUserInfo().getOrgId());
		getDto().put("aae140", getDto().getAsString("aae140"));
		sb.append("new Array(new Array('公民身份号码','姓名','人员ID','性别','年龄')"); // 需要在下拉列表中显示的列
		String sql = "suggestFramework.getAc01ListByAsnDyAllString"; // 获取下拉列表数据所对应的SQLID
		setData("data", baseUtil_newRPC(getDto(), sb, sql));
		return JSON;
	}
	
	/**
	 * 获取个人基本信息(不带经办机构) String
	 * @return
	 * @throws Exception
	 * @description 获取个人基本信息(不带经办机构)
	 */
	public String getAac001DyAllList() throws Exception {
		ParamDTO dto = getDto();
		dto.put("yab003", getDto().getUserInfo().getOrgId());
		dto.put("aae140", getDto().getAsString("aae140"));
		List list = getDao().queryForList("suggestFramework.getAc01ListByAsnDyAllList",dto);
		setList("dg15", list);
		return JSON;
	}
}
