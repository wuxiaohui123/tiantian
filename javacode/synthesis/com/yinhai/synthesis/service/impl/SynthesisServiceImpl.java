package com.yinhai.synthesis.service.impl;

import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.yinhai.synthesis.service.SynthesisService;
import com.yinhai.synthesis.util.Constant;
import com.yinhai.sysframework.codetable.CodeLevelCacheService;
import com.yinhai.sysframework.codetable.domain.AppLevelCode;
import com.yinhai.sysframework.dto.BaseDTO;
import com.yinhai.sysframework.dto.DTO;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.dto.PrcDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.DateUtil;
import com.yinhai.sysframework.util.ValidateUtil;

/**
 * @标题 SynthesisServiceImpl
 * @说明 业务系统基础ServiceImpl
 * @使用 业务系统所有ServiceImpl均继承此ServiceImpl或继承继承此ServiceImpl的ServiceImpl
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SynthesisServiceImpl extends BaseService implements SynthesisService {
	private CodeLevelCacheService codeLevelCacheService = (CodeLevelCacheService)ServiceLocator.getService("codelevelCacheService");
    public String getSequence(String arg0, String arg1) {
	Map map = new HashMap();
	map.put("dbname", arg0);
	map.put("tbname", arg1);
	Object o = dao.queryForObject("synthesis.getStringSequence", map);
	if (ValidateUtil.isEmpty(o)) {
	    throw new AppException("在数据库["+arg0.toUpperCase()+"]中，表[" + arg1.toUpperCase() + "]不存在");
	}
	return (String) o;
    }

    public Map SetLogMap(ParamDTO dto, String aaz002, String aaz010, String aaa832, String aaa831, String yae049, String aaa121, String action, String aae419) {
	Map m = new HashMap();
	String ip = "";
	String name = "";
	try {
	    InetAddress addr = InetAddress.getLocalHost();
	    ip = addr.getHostAddress();// 获得本机IP
	    name = addr.getHostName();// 获得本机名称
	} catch (Exception e) {
	    ip = "127.0.0.1";
	    name = "unkonwn";
	}
	m.put("aae417", dto.getAsString("aae417")); // 申请业务日志id
	m.put("yae248", dto.getAsString("yae248")); // 数据来源
	m.put("aaa795", name); // 主机名字
	m.put("aaa796", ip); // 主机ip
	m.put("aab034", dto.getUserInfo().getOrgId()); // 必传项:经办机构
	m.put("aaz010", aaz010); // 必传项:当事人ID
	m.put("aaa832", aaa832); // 必传项：是否可回退 1:是 0:否
	m.put("aaa831", aaa831); // 必传项：是否使用触发器 1:是 0:否
	m.put("aae013", dto.getAsString("aae013")); // 必传项:备注
	m.put("aaz002", aaz002); // 必传项：公用回退流水号
	m.put("yae049", yae049);// 必传项：ad52中菜单编号
	m.put("yaz001", dto.getAsString("businessID")); // 必传项：流程ID
	m.put("aaa121", aaa121);// 必传项：业务类型编码
	m.put("action", action);// 必传项：动作（0,初审、1通过、2不通过、3打回）
	m.put("aae419", aae419);// 必传项：当前复核级次(初审：0，一级审批(审核)：1，二级审批(审批)：2)
	return m;
    }

    /**
     * 
     * @功能描述：ExecutePrc所需参数
     * @param in
     *            必须是Action中GetDto()所得到
     * @param bizMap
     *            业务过程所需参数
     * @param logMap
     *            公用回退所需参数
     * @param pkgName
     *            业务过程所在包名
     * @param prcName
     *            业务过程名
     * @param savelog
     *            是否写日志 0 否 1 是
     * @return
     * @description
     * @version 1.0
     * @author Haword
     * @update 2013-1-18 上午11:24:29
     */
    public ParamDTO SetDoParam(ParamDTO in, Map bizMap, Map logMap, String pkgName, String prcName, String savelog) {
	// 新加业务编码与业务日志ID，用于在审核界面获取上次审核记录
	in.put("aaa121", logMap.get("aaa121"));
	in.put("aaz002", logMap.get("aaz002"));

	in.put("prcArg", bizMap); // 必传项:存错过程所需传入参数，out类型的参数不用传
	in.put("pkg", pkgName); // 必传项:存错过程所在包名
	in.put("prc", prcName); // 必传项:存错过程名字
	in.put("logArg", logMap);
	in.put("savelog", savelog);
	return in;
    }

    /**
     * 
     * @项目名称：YHSI3
     * 
     * @参数??InputDTO
     * @功能描述：拼接XML，调用存储过??
     * @返回值：OutputDTO
     * @创建人：chenzc 创建时间2011-5-6 下午17:10:32
     * @修改人：tianqi 修改时间2012-10-28 下午17:10:32 修改备注
     * @version
     * 
     */
    public ParamDTO ExecutePrc(ParamDTO in) {
	/*
	 * 变量
	 */

	ParamDTO out = new ParamDTO();
	Map pMap, oMap;
	List listArg;
	String inputXml, outXml;
	String lXml, pXml, listXml;
	String pkg, prc, savelog;

	/*
	 * 正文
	 */

	pMap = new HashMap();
	oMap = new HashMap();
	PrcDTO pd = new PrcDTO();

	pMap = (Map) in.get("prcArg");
	listArg = (List) in.get("listArg");
	prc = in.getAsString("prc");
	pkg = in.getAsString("pkg");
	/*
	 * if (pkg == null||pkg.length()<1) { throw new
	 * AppException("DoService.ExecutePrc执行时发现InputDTO参数中不存在“pkg??); } if
	 * (prc == null ||prc.length()<1) { throw new
	 * AppException("DoService.ExecutePrc执行时发现InputDTO参数中不存在“prc??); }
	 */
	savelog = in.getAsString("savelog");

	if (savelog == null || savelog.length() < 1) {
	    savelog = "1";
	}
	/*
	 * ****ACTION里需要传入的aab034 社会保险经办机构编码aaz010 当事人IDaaa832 是否可回??ae013 备注
	 * ****SERVICE添加的yae049 功能编号 *aae011 经办??*yab003 经办人经办机??*aaa795 主机??*
	 * aaa796 主机IP地址 *appcode *errormsg *
	 */

	if (savelog.equalsIgnoreCase("1")) {
	    Map lMap = new HashMap();
	    lMap = (Map) in.get("logArg");
	    if (lMap == null) {
		throw new AppException("DoService.ExecutePrc执行时发现InputDTO参数中不存在logArg");
	    }
	    lMap.put("yab003", in.getUserInfo().getOrgId());
	    lMap.put("aae011", in.getUserInfo().getUserId());
	    lMap.put("yae049", lMap.get("yae049")); // 功能编号
	    lMap.put("aje080", lMap.get("aje080"));// aaz257
	    lMap.put("ylc001", lMap.get("ylc001"));
	    lMap.put("ymc001", lMap.get("aaz238"));
	    lMap.put("aaa795", "");
	    lXml = XMLbymap(lMap);
	    if (lXml != null) {
		lXml = "<log " + lXml + "/>";
	    }
	} else {
	    lXml = " ";
	}

	if (pMap == null) {
	    pMap = new HashMap();
	}
	pMap.put("appcode", "");

	if (listArg != null && listArg.size() > 0) {
	    Map lMap = new HashMap();
	    AppException app1 = new AppException("DoService.ExecutePrc执行时发现InputDTO参数中不存在logArg");
	    for (int i = 0; i < listArg.size(); i++) {
		prc = ((Map) listArg.get(i)).get("prc").toString();
		pkg = ((Map) listArg.get(i)).get("pkg").toString();
		savelog = ((Map) listArg.get(i)).get("savelog").toString();

		listXml = XMLbymap((Map) listArg.get(i));
		pXml = XMLbymap(pMap);

		if (pXml != null && listXml != null) {
		    pXml = "<prc " + pXml + " " + listXml + "/>";
		} else if (pXml == null && listXml != null) {
		    pXml = "<prc " + listXml + "/>";
		} else {
		    pXml = "<prc " + pXml + "/>";
		}

		if (savelog == null || savelog.length() < 1) {
		    savelog = "1";
		}
		if (savelog.equalsIgnoreCase("1")) {
		    lMap = (Map) in.get("logArg");
		    if (lMap == null) {
			throw app1;
		    }
		    lMap.put("yab003", "449900");
		    lMap.put("aae011", in.getUserInfo().getUserId());
		    lMap.put("yae049", lMap.get("yae049"));
		    lMap.put("aaa795", "");
		    lMap.put("appcode", "");
		    lMap.put("errormsg", "");
		    lXml = XMLbymap(lMap);
		    if (lXml != null) {
			lXml = "<log " + lXml + "/>";
		    }
		} else {
		    lXml = " ";
		}

		// 循环执行存储过程
		inputXml = "<arg package='" + pkg + "' procedure='" + prc + "' savelog='" + savelog + "'>";
		inputXml += lXml;
		inputXml += pXml;
		inputXml += " </arg>";
		pd.put("inputxml", inputXml);
		// 参数过多或??长度过大
		if (inputXml.length() > 4000) {
		    throw app1;
		} else {
		    dao.callPrc("synthesis.prc_do", pd);
		}
		outXml = pd.getAsString("outxml");
		oMap = Mapbyxml(outXml);
		out.put("outMap", oMap);
		out.put("AppCode", pd.get("AppCode"));
		out.put("ErrorMsg", pd.get("ErrorMsg"));
	    }
	} else {
	    // 执行存储过程
	    pXml = XMLbymap(pMap);
	    if (pXml != null) {
		pXml = "<prc " + pXml + "/>";
	    }
	    inputXml = "<arg package='" + pkg + "' procedure='" + prc + "' savelog='" + savelog + "'>";
	    inputXml += lXml;
	    inputXml += pXml;
	    inputXml += " </arg>";
	    pd.put("inputxml", inputXml);
	    // 参数过多或长度过大
	    if (inputXml.length() > 4000) {
		throw new AppException("DoService.ExecutePrc执行时发现参数过多或值长度过??");
	    } else {
		dao.callPrc("synthesis.prc_do", pd);
	    }
	    outXml = pd.getAsString("outxml");
	    oMap = Mapbyxml(outXml);
	    out.put("outMap", oMap);
	    out.put("AppCode", pd.get("AppCode"));
	    out.put("ErrorMsg", pd.get("ErrorMsg"));
	}
	return out;
    }

    /*
     * 通过map构造成xml节点 mdx 2009.03.09
     */
    public static final String XMLbymap(Map map1) {
	StringBuffer s_XML;
	String s_val;
	s_XML = new StringBuffer("");
	Iterator iter = map1.entrySet().iterator();

	while (iter.hasNext()) {
	    Map.Entry entry = (Map.Entry) iter.next();
	    Object key = entry.getKey();
	    Object val = entry.getValue();
	    if (!key.toString().equalsIgnoreCase("jsessionid")) {
		s_XML.append((String) key);
		s_XML.append("='");

		if (val != null) {
		    // 类型转换
		    if (val instanceof Boolean) {
			Boolean bl = (Boolean) val;
			// boolean 转换为字符串 true或false
			if (bl) {
			    s_val = "true";
			} else {
			    s_val = "false";
			}
		    } else if (val instanceof Integer) {
			// 转换integer
			s_val = val.toString();
		    } else {
			// s_val = (String) val;
			s_val = val.toString();
		    }
		} else {
		    s_val = "";
		}
		s_val = s_val.trim();
		// 转换NULL为空
		if (s_val.equalsIgnoreCase("null")) {
		    s_val = "";
		}
		s_val = s_val.replace("&", "&amp;");
		s_val = s_val.replace("'", "&apos;");
		s_val = s_val.replace("<", "&lt;");
		s_val = s_val.replace(">", "&gt;");
		s_XML.append(s_val);
		s_XML.append("' ");
	    }
	}
	return s_XML.toString();
    }

    /*
     * 通过str构成map节点 mdx 2009.03.09
     */
    public static final Map Mapbyxml(String s_xml) {
	SAXBuilder builder = new SAXBuilder();
	Reader rdargxml = new StringReader(s_xml);
	Document docxml = null;
	Map map = new HashMap();
	try {
	    docxml = builder.build(rdargxml);
	    Element rootxml = docxml.getRootElement();
	    List lstArg = rootxml.getChildren();
	    Element el = null;
	    for (Iterator iter = lstArg.iterator(); iter.hasNext();) {
		el = (Element) iter.next();
		map.put(el.getName(), el.getTextTrim());
	    }

	} catch (Exception ex3) {
	    return null;
	}
	return map;
    }

    // ITimeService已定义但未实现,在此实现
    public Timestamp getSysTimestamp() {
	return (Timestamp) dao.queryForObject("synthesis.getSysTimestamp");
    }

	public List getSelectInputLevelDataByList(String aaa100, String aaa102,String yab003) throws AppException {
		List list = new ArrayList();
		DTO dto = new BaseDTO();
		dto.put("aaa100", aaa100);
		dto.put("aaa102", aaa102);
		dto.put("yab003", yab003);
		List list_query = getDao().queryForList("synthesis.getAa11tree", dto);
		if (ValidateUtil.isNotEmpty(list_query) && list_query.size() > 0) {
			Map map = new HashMap();
			for (int i = 0; i < list_query.size(); i++) {
				map = new HashMap<String, Object>();
				map.put("codevalue", ((Map) list_query.get(i)).get("id"));// 代码值
				map.put("codedesc", ((Map) list_query.get(i)).get("name"));// 代码名称
				map.put("levelvalue",((Map) list_query.get(i)).get("levelvalue"));// 级次
				map.put("leaf", ((Map) list_query.get(i)).get("leaf")); // 末级标志
				list.add(map);
			}
		}
		return list;
	}
	
	public List getSelectInputLevelDataListByCache(String aaa100, String aaa102,String yab003) throws AppException {
		List list = new ArrayList();
		List list_query = codeLevelCacheService.getCodeListCache(aaa100, yab003).getList();
		if (ValidateUtil.isNotEmpty(list_query) && list_query.size() > 0) {
			Map map = new HashMap();
			for (int i = 0; i < list_query.size(); i++) {
				map = new HashMap<String, Object>();
				map.put("codevalue", ((AppLevelCode) list_query.get(i)).getId());// 代码值
				map.put("codedesc", ((AppLevelCode) list_query.get(i)).getName());// 代码名称
				map.put("levelvalue",((AppLevelCode) list_query.get(i)).getLevelvalue());// 级次
				map.put("leaf", ((AppLevelCode) list_query.get(i)).getLeaf()); // 末级标志
				list.add(map);
			}
		}
		return list;
	}

    public Integer getLastMonths(Integer aae002, int i) throws AppException {
	DTO dto = new BaseDTO();
	dto.put("aae002", aae002);
	dto.put("i", -i);
	return new Integer(getDao().queryForObject("synthesis.getAdd_Months", dto).toString());
    }

    public Integer getLastMonths(Integer aae002) throws AppException {
	return getLastMonths(aae002, 1);
    }

    public String getSysdate(String format) {
	return DateUtil.datetimeToString(new Date(getSysTimestamp().getTime()), format);
    }

    public String getTipsMessage(String aaz002) throws AppException {
	String message = "";
	try {
	    DTO dto = new BaseDTO();
	    dto.put("aaz002", aaz002);

	    List list = getDao().queryForList("synthesis.getAe02a1Info", dto);

	    if (ValidateUtil.isEmpty(list) || list.size() < 1) {
		message = "业务办理成功，但通过通用方法查询提示信息失败，未查询到事件表！";
	    } else if (list.size() > 1) {
		message = "业务办理成功，但通过通用方法查询提示信息失败，查询到" + list.size() + "条记录！";
	    } else {
		Map map = (Map) list.get(0);
		message = "您操作的【" + getCodeDesc("AAA121", map.get("aaa121").toString(), null) + "】业务，【" + getCodeDesc("YAA017", map.get("yaa017").toString(), null) + "】办理完成，复核标志为【" + getCodeDesc("AAE016", map.get("aae016").toString(), null) + "】";
		if (Constant.AAE016_0_WFH.equals(map.get("aae016").toString())) {
		    message = message + "，请继续办理";
		}
		message = message + "！";
	    }
	} catch (Exception e) {
	    message = "您操作的业务办理完成，但通过公用方法获取提示信息出错！" + e.getMessage();
	}
	return message;
    }

    public int delete(String arg0, Object arg1, String arg2, int arg3) { // Checked
	// 公用子方法
	int i = getDao().delete(arg0, arg1);
	if (arg2.isEmpty()) {
	    throw new AppException("运算类型为空,请传入[ != ]或[ > ]或[ < ].");
	}
	if ("!=".equals(arg2)) {
	    if (i != arg3) {
		throw new AppException("实际删除记录数[ " + i + " ]不等于预期删除记录数[ " + arg3 + " ].");
	    }
	} else if ("<".equals(arg2)) {
	    if (i < arg3) {
		throw new AppException("实际删除记录数[ " + i + " ]小于预期删除记录数[ " + arg3 + " ].");
	    }
	} else if (">".equals(arg2)) {
	    if (i > arg3) {
		throw new AppException("实际删除记录数[ " + i + " ]大于预期删除记录数[ " + arg3 + " ].");
	    }
	} else {
	    throw new AppException("运算类型传入不正确,请传入[ != ]或[ > ]或[ < ].");
	}
	return i;
    }

    public int delete(String arg0, Object arg1, String arg2) { // Checked 公用子方法
	return delete(arg0, arg1, arg2, 500);
    }

    public int delete(String arg0, Object arg1) { // Checked 公用子方法
	return delete(arg0, arg1, ">");
    }

    /**
     * 四险调用
     * 
     * @param dto
     *            必传项：一般由Action传递获得,包含aae417申请业务日志ID,yae248数据来源,aae013备注
     * @param aaz002
     *            必传项: 业务日志ID 用于区别判断首次经办获取业务日志为空的情况
     * @param aaz010
     *            必传项: 当事人ID
     * @param aaa121
     *            必传项：业务类型编码
     * @param action
     *            必传项：动作（0,初审、1通过、2不通过、3打回）
     * @param aae419
     *            必传项：当前复核级次(初审：0,一级审批(审核)：1,二级审批(审批)：2)
     */
    public Map SetLogMap(ParamDTO dto, String aaz002, String aaz010, String aaa121, String action, String aae419) {
	Map m = new HashMap();
	String ip = "";
	String name = "";
	try {
	    InetAddress addr = InetAddress.getLocalHost();
	    ip = addr.getHostAddress();// 获得本机IP
	    name = addr.getHostName();// 获得本机名称
	} catch (Exception e) {
	    ip = "127.0.0.1";
	    name = "unkonwn";
	}

	m.put("aae417", dto.getAsString("aae417")); // 申请业务日志id
	m.put("yae248", dto.getAsString("yae248")); // 数据来源
	m.put("aaa795", name); // 主机名字
	m.put("aaa796", ip); // 主机ip
	m.put("aab034", dto.getUserInfo().getOrgId()); // 必传项:经办机构
	m.put("aaz010", aaz010); // 必传项:当事人ID
	m.put("aaa832", 1); // 必传项：是否可回退 1:是 0:否
	m.put("aaa831", 1); // 必传项：是否使用触发器 1:是 0:否
	m.put("aae013", dto.getAsString("aae013")); // 必传项:备注
	m.put("aaz002", aaz002); // 必传项：公用回退流水号
	// m.put("yae049", getMenuId(dto.getAsString("workbenchUrl"),
	// dto.getRequest()));// 必传项：ad52中菜单编号
	m.put("yaz001", dto.getAsString("businessID")); // 必传项：流程ID
	m.put("aaa121", aaa121);// 必传项：业务类型编码
	m.put("action", action);// 必传项：动作（0,初审、1通过、2不通过、3打回）
	m.put("aae419", aae419);// 必传项：当前复核级次(初审：0,一级审批(审核)：1,二级审批(审批)：2)
	return m;
    }

    public List getStringArray(String arg0) {
	List list = new ArrayList();
	if (ValidateUtil.isNotEmpty(arg0)) {
	    String[] str1 = arg0.split(",");
	    for (int i = 0; i < str1.length; i++) {
		list.add(str1[i]);
	    }
	}
	return list;
    }

    /**
     * 前台进行数据变更且要使用公用回退,必须先执行此方法
     * 
     * @param aaa831
     *            （是否使用触发器1:是,0:否）
     * @param aaa832
     *            （是否可回退1:是,0:否）
     * @throws AppException
     */
    public void insertTemp_Aa83(String aaa831, String aaa832) throws AppException {
	Map map = new HashMap();
	map.put("aaa831", aaa831);
	map.put("aaa832", aaa832);
	getDao().insert("synthesis.insertTemp_Aa83", map);// Checked
    }

    public Map toSaveVerity(ParamDTO in) throws AppException {
	// 当事人ID规则：优先当事人ID，其次个人ID，最后单位ID
	String aaz010 = in.getAsString("aaz010");
	if (ValidateUtil.isEmpty(aaz010)) {
	    aaz010 = in.getAsString("aac001");
	}
	if (ValidateUtil.isEmpty(aaz010)) {
	    aaz010 = in.getAsString("aab001");
	}
	if (ValidateUtil.isEmpty(aaz010)) {
	    throw new AppException("没有获取当事人ID,请检查！");
	}
	String aae011 = in.getUserInfo().getUserId();
	String yab003 = in.getUserInfo().getOrgId();
	String aaz002 = in.getAsString("aaz002");
	String action = in.getAsString("action");
	String aae419 = in.getAsString("fhjc");
	String aaa121 = in.getAsString("aaa121");

	// 是否使用触发器 1:是 0:否
	String aaa831 = "1";
	// 是否写日志 0 否 1 是
	String savelog = "1";

	Map prcMap = new HashMap();
	prcMap.put("aaz002", aaz002);
	prcMap.put("aae011", aae011);
	prcMap.put("yab003", yab003);

	Map logMap = SetLogMap(in, aaz002, aaz010, Constant.AAA832_1_S, aaa831, null, aaa121, action, aae419);

	Map n = new HashMap();
	n.put("aaa121", aaa121);
	n.put("aae419", aae419);
	List list = getDao().queryForList("publicBusiness.getPackage", n);
	if (ValidateUtil.isNotEmpty(list) && list.size() == 1) {
	    n = (Map) list.get(0);
	} else {
	    throw new AppException("通过业务类型和复核级次查询存储过程出错。");
	}

	SetDoParam(in, prcMap, logMap, n.get("yae071").toString(), n.get("yae070").toString(), savelog);

	// 调用存储过程
	ExecutePrc(in);

	Map map = new HashMap();
	map.put("aaz002", aaz002);
	return map;
    }

    public Date getSysDate() {
	   return (Date) dao.queryForObject("synthesis.getSysDate", null);
    }
}
