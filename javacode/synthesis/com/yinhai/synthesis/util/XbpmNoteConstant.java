package com.yinhai.synthesis.util;

public interface XbpmNoteConstant{
	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 节点名：开始
	 */
	public static final String N_START = "开始";
	/**
	 * 节点名：结束
	 */
	public static final String N_END = "结束";


    /**
     * 流程名:社保药品目录新增管理
     */
	public static final String P_SBYPMLXZGL="社保药品目录新增管理";
	/**
	 * 流程KEY
	 */
	public static final String P_SBYPMLXZGL_PROCESSKEY="newDrugs";
	/**
	 * 节点名:社保药品目录新增经办
	 */
	public static final String N_SBYPMLXZGL_SBYPMLXZJB="社保药品目录新增经办";

	/**
	 * 节点名:社保药品目录新增审核
	 */
	public static final String N_SBYPMLXZGL_SBYPMLXZSH="社保药品目录新增审核";

	/**
	 * 流程名:社保药品目录修改管理
	 */
    public static final String P_SBYPMLXGGL="社保药品目录修改管理";
    /**
     * 流程KEY
     */
    public static final String P_SBYPMLXGGL_PROCESSKEY="modifyDrugs";
    /**
     * 节点名:社保药品目录修改经办
     */
    public static final String N_SBYPMLXGGL_SBYPMLXGJB="社保药品目录修改经办";
    /**
     * 节点名:社保药品目录修改审核
     */
    public static final String N_SBYPMLXGGL_SBYPMLXGSH="社保药品目录修改审核";

	public static final String P_SBYPMLTYGL="社保药品目录停用管理";
	public static final String P_SBYPMLTYGL_PROCESSKEY="stopDrugs";
	public static final String N_SBYPMLTYGL_SBYPMLTYJB="社保药品目录停用经办";
	public static final String N_SBYPMLTYGL_SBYPMLTYSH="社保药品目录停用审核";

	public static final String P_SBSDMLWHGL ="社保三大目录维护管理";
	public static final String P_SBSDMLWHGL_PROCESSKEY ="drugsMaitain";
	public static final String N_SBSDMLWHGL_SBSDMLWHJB ="社保三大目录维护经办";
	public static final String N_SBSDMLWHGL_SBSDMLWHSH ="社保三大目录维护审核";

    /******************社保诊疗目录管理start***********/
    /**
     * 流程名:社保诊疗目录新增管理
     */
	public static final String P_SBZLMLXZGL="社保诊疗目录新增管理";
    /**
     * 流程KEY
     */
	public static final String P_ZLMLXZGL_PROCESSKEY="社保诊疗目录新增";
    /**
     * 节点名:社保诊疗目录新增经办
     */
	public static final String N_ZLMLXZGL_JB="诊疗项目新增经办";
    /**
     * 流程名:社保诊疗目录修改管理
     */
	public static final String P_SBZLMLXGGL="社保诊疗目录修改管理";
    /**
     * 流程KEY
     */
	public static final String P_ZLMLXGGL_PROCESSKEY="社保诊疗项目修改";
    /**
     * 节点名:社保诊疗目录修改经办
     */
	public static final String N_ZLMLXGGL_JB="诊疗项目修改经办";
    /**
     * 流程名:社保诊疗停用管理
     */
	public static final String P_SBZLMLTYGL="社保诊疗目录停用管理";
    /**
     * 流程KEY
     */
	public static final String P_ZLMLTYGL_PROCESSKEY="社保诊疗项目停用";
    /**
     * 节点名:社保诊疗目录停用经办
     */
	public static final String N_ZLMLTYGL_JB="诊疗项目停用经办";
    /**
     * 节点名:社保诊疗目录审核
     */
	public static final String N_ZLMLGL_SH="诊疗项目审核";
    /**
     * 流程名:特殊疾病用药范围维护管理
     */
	public static final String P_TSJBYYFWWHGL="特殊疾病用药范围维护管理";
    /**
     * 流程KEY
     */
	public static final String P_P_TSJBYYFWWHGL_PROCESSKEY="特殊疾病用药范围维护";
    /**
     * 节点名:特殊疾病用药范围维护经办
     */
	public static final String N_P_TSJBYYFWWHGL_JB="特殊疾病用药范围维护经办";
    /**
     * 节点名:特殊疾病用药范围维护审核
     */
	public static final String N_P_TSJBYYFWWHGL_SH="特殊疾病用药范围维护审核";
    /**
     * 流程名:病种目录新增管理
     */
	public static final String P_BZMLXZGL="病种目录新增管理";
    /**
     * 流程KEY
     */
	public static final String P_BZMLXZGL_PROCESSKEY="病种目录新增";
    /**
     * 节点名:病种目录新增经办
     */
	public static final String N_BZMLXZGL_JB="病种目录新增经办";
    /**
     * 节点名:病种目录新增审核
     */
	public static final String N_BZMLXZGL_SH="病种目录新增审核";
    /**
     * 流程名:病种目录修改管理
     */
	public static final String P_BZMLXGGL="病种目录修改管理";
    /**
     * 流程KEY
     */
	public static final String P_BZMLXGGL_PROCESSKEY="病种目录修改";
    /**
     * 节点名:病种目录修改经办
     */
	public static final String N_BZMLXGGL_JB="病种目录修改经办";
    /**
     * 节点名:病种目录修改审核
     */
	public static final String N_BZMLXGGL_SH="病种目录修改审核";
	 /**
     * 流程名:个人账户返还管理
     */
	public static final String P_GRZHFHGL="个人账户返还管理";
    /**
     * 流程KEY
     */
	public static final String P_GRZHFHGL_PROCESSKEY="个人账户返还";
    /**
     * 节点名:个人账户返还初审
     */
	public static final String N_GRZHFHGL_CS="个人账户返还初审";
    /**
     * 节点名:个人账户返还审核
     */
	public static final String N_GRZHFHGL_SH="个人账户返还审核";

     /******************社保诊疗目录管理end***********/

	/******************个人参保登记begin***********/
    /**
	 * 流程名：个人参保登记
	 */
	public static final String P_GRCBDJ = "个人参保登记";
	/**
	 * 流程key: registerEmployee
	 */
	public static final String P_GRCBDJ_PROCESSKEY = "registerEmployee";
	/**
	 * 节点名：个人参保登记经办
	 */
	public static final String N_GRCBDJ_JB = "个人参保登记经办";
	/**
	 * 节点名：个人参保登记审核
	 */
	public static final String N_GRCBDJ_SH = "个人参保登记审核";
	/******************个人参保登记end***********/
	/****************人员死亡信息登记start****************/
	/**
	 * 节点名：节点名：个人参保登记审核
	 */
	public static final String P_RYSWXXDJ = "人员死亡信息登记";
	/**
	 * 流程key: registerEmpDeadInfo
	 */
	public static final String P_RYSWXXDJ_PROCESSKEY = "registerEmpDeadInfo";
	/**
	 * 节点名：节点名：个人参保登记审核
	 */
	public static final String N_RYSWXXDJ_SH = "人员死亡信息登记审核";
	/****************人员死亡信息登记end****************/
	/******************个人恢复缴费begin***********/
	/**
	 * 流程名：个人恢复缴费
	 */
	public static final String P_GRHFJF = "个人恢复缴费";
	/**
	 * 流程key: resumePayEmp
	 */
	public static final String P_GRHFJF_PROCESSKEY = "resumePayEmp";
	/**
	 * 节点名：个人恢复缴费经办
	 */
	public static final String N_GRHFJF_JB = "个人恢复缴费经办";
	/**
	 * 节点名：个人恢复缴费审核
	 */
	public static final String N_GRHFJF_SH = "个人恢复缴费审核";
	/******************个人恢复缴费end***********/

	/******************个人终止参保begin***********/
	/**
	 * 流程名：个人终止参保
	 */
	public static final String P_GRZZCB = "个人终止参保";
	/**
	 * 流程key: terminateEmp
	 */
	public static final String P_GRZZCB_PROCESSKEY = "terminateEmp";
	/**
	 * 节点名：个人终止参保经办
	 */
	public static final String N_GRZZCB_JB = "个人终止参保经办";
	/**
	 * 节点名：个人终止参保审核
	 */
	public static final String N_GRZZCB_SH = "个人终止参保审核";
	/******************个人终止参保end***********/

	/******************在职转退休start*************/
	/**
	 * 流程名：在职转退休
	 */
	public static final String P_ZZZTX= "在职转退休";
	/**
	 * 流程key: retireEmp
	 */
	public static final String P_ZZZTX_PROCESSKEY = "retireEmp";
	/**
	 * 节点名：在职转退休经办
	 */
	public static final String N_ZZZTX_JB = "在职转退休经办";
	/**
	 * 节点名：在职转退休审核
	 */
	public static final String N_ZZZTX_SH = "在职转退休审核";
	/******************在职转退休end*************/

	/******************个人工资申报start*************/
	/**
	 * 流程名：个人工资申报
	 */
	public static final String P_GRGZSB = "个人工资申报";
	/**
	 * 流程key: wageReportEmp
	 */
	public static final String P_GRGZSB_PROCESSKEY = "wageReportEmp";
	/**
	 * 节点名：个人工资申报经办
	 */
	public static final String N_GRGZSB_JB = "个人工资申报经办";
	/**
	 * 节点名：个人工资申报审核
	 */
	public static final String N_GRGZSB_SH = "个人工资申报审核";
	/******************个人工资申报end*************/

	/******************个人暂停缴费begin***********/
	/**
	 * 流程名：个人暂停缴费
	 */
	public static final String P_GRZTJF = "个人暂停缴费";
	/**
	 * 流程key: pausePayEmp
	 */
	public static final String P_GRZTJF_PROCESSKEY = "pausePayEmp";
	/**
	 * 节点名：个人暂停缴费经办
	 */
	public static final String N_GRZTJF_JB = "个人暂停缴费经办";
	/**
	 * 节点名：个人暂停缴费审核
	 */
	public static final String N_GRZTJF_SH = "个人暂停缴费审核";
	/******************个人暂停缴费end***********/

	/******************个人信息维护begin***********/
	/**
	 * 流程名：个人信息维护
	 */
	public static final String P_GRXXWH = "个人信息维护";
	/**
	 * 流程key: maintainBIEmployee
	 */
	public static final String P_GRXXWH_PROCESSKEY = "maintainBIEmployee";
	/**
	 * 节点名：个人信息维护经办
	 */
	public static final String N_GRXXWH_JB = "个人信息维护经办";
	/**
	 * 节点名：个人信息维护审核
	 */
	public static final String N_GRXXWH_SH = "个人信息维护审核";
	/******************个人信息维护end***********/

	/******************多号合并end***********/
	/**
	 * 流程名：多号合并
	 */
	public static final String P_DHHB = "多号合并";
	/**
	 * 流程key: accountComb
	 */
	public static final String P_DHHB_PROCESSKEY = "accountComb";
	/**
	 * 节点名：多号合并经办
	 */
	public static final String N_DHHB_JB = "多号合并经办";
	/**
	 * 节点名：多号合并审核
	 */
	public static final String N_DHHB_SH = "多号合并审核";
	/******************多号合并end***********/

	/******************单位参保登记start***********/
	/**
	 * 流程名：单位参保登记
	 */
	public static final String P_DWCBDJ = "单位参保登记";
	/**
	 * 流程key: registerEmployerInfo
	 */
	public static final String P_DWCBDJ_PROCESSKEY = "registerEmployerInfo";
	/**
	 * 节点名：单位参保登记经办
	 */
	public static final String N_DWCBDJ_JB = "单位参保登记经办";
	/**
	 * 节点名：单位参保登记审核
	 */
	public static final String N_DWCBDJ_SH = "单位参保登记审核";

	/******************单位参保登记end*************/

	/******************个人补收start*************/
	/**
	 * 流程名：个人补收
	 */
	public static final String P_GRBS = "个人补收";
	/**
	 * 流程key: interrupt
	 */
	public static final String P_GRBS_PROCESSKEY = "interrupt";
	/**
	 * 节点名：个人补收经办
	 */
	public static final String N_GRBS_JB = "个人补收经办";
	/**
	 * 节点名：个人补收审核
	 */
	public static final String N_GRBS_SH = "个人补收审核";
	/******************个人补收end*************/

	/******************个人退收start*************/
	/**
	 * 流程名：个人退收
	 */
	public static final String P_GRTS = "个人退收";
	/**
	 * 流程key: returnContrib
	 */
	public static final String P_GRTS_PROCESSKEY = "returnContrib";
	/**
	 * 节点名：个人退收经办
	 */
	public static final String N_GRTS_JB = "个人退收经办";
	/**
	 * 节点名：个人退收审核
	 */
	public static final String N_GRTS_SH = "个人退收审核";
	/******************个人退收end*************/

	/******************个人补差start*************/
	/**
	 * 流程名：个人补差
	 */
	public static final String P_GRJFJSBS = "个人补差";
	/**
	 * 流程key: adjustPerson
	 */
	public static final String P_GRJFJSBS_PROCESSKEY = "adjustPerson";
	/**
	 * 节点名：个人补差经办
	 */
	public static final String N_GRJFJSBS_JB = "个人补差经办";
	/**
	 * 节点名：个人补差审核
	 */
	public static final String N_GRJFJSBS_SH = "个人补差审核";
	/******************个人退收end*************/


	/******************零星缴费start*************/
	/**
	 * 流程名：零星缴费
	 */
	public static final String P_LXJF = "零星缴费";
	/**
	 * 流程key: severalGathering
	 */
	public static final String P_LXJF_PROCESSKEY = "severalGathering";
	/**
	 * 节点名：零星缴费经办
	 */
	public static final String N_LXJF_JB = "零星缴费经办";
	/**
	 * 节点名：零星缴费审核
	 */
	public static final String N_LXJF_SH = "零星缴费审核";
	/******************零星缴费end*************/

	/******************趸缴费start*************/
	/**
	 * 流程名：趸缴费
	 */
	public static final String P_DJF = "趸缴费";
	/**
	 * 流程key: fixedFee
	 */
	public static final String P_DJF_PROCESSKEY = "fixedFee";
	/**
	 * 节点名：趸缴费经办
	 */
	public static final String N_DJF_JB = "趸缴费经办";
	/**
	 * 节点名：趸缴费审核
	 */
	public static final String N_DJF_SH = "趸缴费审核";
	/******************趸缴费end*************/



	/**
	 * 流程名：单位应收核定
	 */
	public static final String P_DWYSHD = "单位应收核定";
	/**
	 * 流程key: makePlanEmployer
	 */
	public static final String P_DWYSHD_PROCESSKEY = "makePlanEmployer";
	/**
	 * 节点名：单位应收核定经办
	 */
	public static final String N_DWYSHD_JB = "单位应收核定经办";
	/**
	 * 节点名：单位应收核定审核
	 */
	public static final String N_DWYSHD_SH = "单位应收核定审核";

	/**
	 * 流程名：单位批量应收核定
	 */
	public static final String P_DWPLYSHD = "单位批量应收核定";
	/**
	 * 流程key: batchmakeplanemployer
	 */
	public static final String P_DWPLYSHD_PROCESSKEY = "batchmakeplanemployer";
	/**
	 * 节点名：单位批量应收核定经办
	 */
	public static final String N_DWPLYSHD_JB = "单位批量应收核定经办";
	/**
	 * 节点名：单位批量应收核定审核
	 */
	public static final String N_DWPLYSHD_SH = "单位批量应收核定审核";

	/*************单位注销登记start**************/
	/**
	 * 流程名：单位注销登记
	 */
	public static final String P_DWZXDJ = "单位注销登记";
	/**
	 * 流程key: unitCancellRegist
	 */
	public static final String P_DWZXDJ_PROCESSKEY = "unitCancellRegist";
	/**
	 * 节点名：单位注销登记经办
	 */
	public static final String N_DWZXDJ_JB = "单位注销登记经办";
	/**
	 * 节点名：单位注销登记审核
	 */
	public static final String N_DWZXDJ_SH = "单位注销登记审核";
	/*************单位注销登记end**************/

	/*************单位工伤浮动率调整start**************/
	/**
	 * 流程名：单位工伤浮动率调整
	 */
	public static final String P_DWGSTZ = "单位工伤浮动率调整";
	/**
	 * 流程key: mwifreChange
	 */
	public static final String P_DWGSTZ_PROCESSKEY = "mwifreChange";
	/**
	 * 节点名：单位工伤浮动率调整申请
	 */
	public static final String N_DWGSTZ_JB = "单位工伤浮动率调整申请";
	/**
	 * 节点名：单位工伤浮动率调整复核
	 */
	public static final String N_DWGSTZ_SH = "单位工伤浮动率调整复核";
	/*************单位工伤浮动率调整end**************/

	/**
	 * 流程名：单位征集
	 */
	public static final String P_DWZJ = "单位征集";
	/**
	 * 流程key: fundCollection
	 */
	public static final String P_DWZJ_PROCESSKEY = "fundCollection";
	/**
	 * 节点名：单位征集经办
	 */
	public static final String N_DWZJ_JB = "单位征集经办";
	/**
	 * 节点名：单位征集审核
	 */
	public static final String N_DWZJ_SH = "单位征集审核";

	/**********单位信息维护 start ************/

	/**
	 * 流程名：单位信息维护
	 */
	public static final String P_DWXXWH = "单位信息维护登记";
	/**
	 * 流程key: maintainEmployerInfo
	 */
	public static final String P_DWXXWH_PROCESSKEY = "maintainEmployerInfo";
	/**
	 * 节点名：单位信息维护经办
	 */
	public static final String N_DWXXWH_JB = "单位信息维护经办";
	/**
	 * 节点名：单位信息维护审核
	 */
	public static final String N_DWXXWH_SH = "单位信息维护审核";

	/**********单位信息维护   end *************/

	/**********单位暂停缴费 start ************/

	/**
	 * 流程名：单位暂停缴费
	 */
	public static final String P_DWZTJF = "单位暂停缴费";
	/**
	 * 流程key: unitPausePay
	 */
	public static final String P_DWZTJF_PROCESSKEY = "unitPausePay";
	/**
	 * 节点名：单位暂停缴费经办
	 */
	public static final String N_DWZTJF_JB = "单位暂停缴费经办";
	/**
	 * 节点名：单位暂停缴费审核
	 */
	public static final String N_DWZTJF_SH = "单位暂停缴费审核";

	/**********单位暂停缴费   end *************/

	/**********单位恢复缴费 start ************/

	/**
	 * 流程名：单位恢复缴费
	 */
	public static final String P_DWHFJF = "单位恢复缴费";
	/**
	 * 流程key: unitResumePay
	 */
	public static final String P_DWHFJF_PROCESSKEY = "unitResumePay";
	/**
	 * 节点名：单位恢复缴费经办
	 */
	public static final String N_DWHFJF_JB = "单位恢复缴费经办";
	/**
	 * 节点名：单位恢复缴费审核
	 */
	public static final String N_DWHFJF_SH = "单位恢复缴费审核";

	/**********单位恢复缴费   end *************/

	/**********人员转移管理start ************/
	/**
	 * 流程名：参保关系转移申请
	 */
	public static final String P_CBGXZY_JB = "参保关系转移申请";
	/**
	 * 节点名：参保关系转移审核
	 */
	public static final String P_CBGXZY_SH = "参保关系转移审核";
	/**
	 * 流程名：参保关系转出申请
	 */
	public static final String P_CBGXZC_JB = "参保关系转出申请";
	/**
	 * 节点名：参保关系转出审核
	 */
	public static final String P_CBGXZC_SH = "参保关系转出审核";
	/**
	 * 流程名：参保关系转入申请
	 */
	public static final String P_CBGXZR_JB = "参保关系转入申请";
	/**
	 * 节点名：参保关系转入审核
	 */
	public static final String P_CBGXZR_SH = "参保关系转入审核";

	/**
	 * 流程名：参保关系转移申请(系统内)
	 */
	public static final String P_CBGXZY_XTN_JB = "参保关系转移申请(系统内)";
	/**
	 * 节点名：参保关系转移审核(系统内)
	 */
	public static final String P_CBGXZY_XTN_SH = "参保关系转移审核(系统内)";
	/**
	 * 流程名：参保关系转出申请(系统内)
	 */
	public static final String P_CBGXZC_XTN_JB = "参保关系转出申请(系统内)";
	/**
	 * 节点名：参保关系转出审核(系统内)
	 */
	public static final String P_CBGXZC_XTN_SH = "参保关系转出审核(系统内)";
	/**
	 * 流程名：参保关系转入申请(系统内)
	 */
	public static final String P_CBGXZR_XTN_JB = "参保关系转入申请(系统内)";
	/**
	 * 节点名：参保关系转入审核(系统内)
	 */
	public static final String P_CBGXZR_XTN_SH = "参保关系转入审核(系统内)";

	/**********人员转移管理end *************/



	/**********待遇终止 start ************/

	/**
	 * 流程名：养老待遇终止
	 */
	public static final String P_DYZZ = "养老待遇终止";
	/**
	 * 流程key: treatmentTerminate110
	 */
	public static final String P_DYZZ_PROCESSKEY = "treatmentTerminate110";
	/**
	 * 节点名：养老待遇终止经办
	 */
	public static final String N_DYZZ_JB = "养老待遇终止经办";
	/**
	 * 节点名：养老待遇终止审核
	 */
	public static final String N_DYZZ_SH = "养老待遇终止审核";
	/**
	 * 节点名：养老待遇终止审批
	 */
	public static final String N_DYZZ_SP = "养老待遇终止审批";
	/**
	 * 流程名：养老供养人员待遇终止
	 */
	public static final String P_DYZZGY = "养老供养人员待遇终止";
	/**
	 * 流程key: treatmentTerminate110GY
	 */
	public static final String P_DYZZGY_PROCESSKEY = "treatmentTerminate110GY";
	/**
	 * 节点名：养老供养人员待遇终止经办
	 */
	public static final String N_DYZZGY_JB = "养老供养人员待遇终止经办";
	/**
	 * 节点名：养老供养人员待遇终止审核
	 */
	public static final String N_DYZZGY_SH = "养老供养人员待遇终止审核";
	/**
	 * 节点名：养老供养人员待遇终止审批
	 */
	public static final String N_DYZZGY_SP = "养老供养人员待遇终止审批";
	/**
	 * 流程名：失业待遇终止
	 */
	public static final String P_DYZZSY = "失业待遇终止";
	/**
	 * 节点名：
	/**
	 * 流程key: treatmentTerminate210
	 */
	public static final String P_DYZZSY_PROCESSKEY = "treatmentTerminate210";
	/**
	 * 节点名：失业待遇终止经办
	 */
	public static final String N_DYZZSY_JB = "失业待遇终止经办";
	/**
	 * 节点名：失业待遇终止审核
	 */
	public static final String N_DYZZSY_SH = "失业待遇终止审核";
	/**
	 * 节点名：失业待遇终止审批
	 */
	public static final String N_DYZZSY_SP = "失业待遇终止审批";
	/**
	 * 流程名：工伤待遇终止
	 */
	public static final String P_DYZZGS = "工伤待遇终止";
	/**
	 * 节点名：
	/**
	 * 流程key: treatmentTerminate410
	 */
	public static final String P_DYZZGS_PROCESSKEY = "treatmentTerminate410";
	/**
	 * 节点名：工伤待遇终止经办
	 */
	public static final String N_DYZZGS_JB = "工伤待遇终止经办";
	/**
	 * 节点名：工伤待遇终止审核
	 */
	public static final String N_DYZZGS_SH = "工伤待遇终止审核";
	/**
	 * 节点名：工伤待遇终止审批"
	 */
	public static final String N_DYZZGS_SP = "工伤待遇终止审批";
	/**
	 * 流程名：工伤供养亲属人员待遇终止
	 */
	public static final String P_DYZZGSGY = "工伤供养亲属人员待遇终止";
	/**
	 * 节点名：
	/**
	 * 流程key: treatmentTerminate410
	 */
	public static final String P_DYZZGSGY_PROCESSKEY = "treatmentTerminate410GY";
	/**
	 * 节点名：工伤供养亲属人员待遇终止经办
	 */
	public static final String N_DYZZGSGY_JB = "工伤供养亲属人员待遇终止经办";
	/**
	 * 节点名：工伤供养亲属人员待遇终止审核
	 */
	public static final String N_DYZZGSGY_SH = "工伤供养亲属人员待遇终止审核";
	/**
	 * 节点名：工伤供养亲属人员待遇终止审批
	 */
	public static final String N_DYZZGSGY_SP = "工伤供养亲属人员待遇终止审批";

	/**********待遇终止   end *************/

    /**********养老待遇停发 start ************/
	/**
	 * 流程名：养老待遇停发
	 */
	public static final String P_CZQYZGJBYLDYDF = "养老待遇停发";
	/**
	 * 流程key: intermitTerminate110GY
	 */
	public static final String P_CZQYZGJBDYDFGY_PROCESSKEY = "intermitTerminate110GY";
	/**
	 * 节点名：养老供养亲属停发经办
	 */
	public static final String N_CZQYZGJBDYDFGY_JB = "养老供养亲属停发经办";
	/**
	 * 节点名：养老供养亲属停发审核
	 */
	public static final String N_CZQYZGJBDYDFGY_SH = "养老供养亲属停发审核";
	/**
	 * 节点名：养老供养亲属停发审批
	 */
	public static final String N_CZQYZGJBDYDFGY_SP = "养老供养亲属停发审批";
	/**
	 * 流程key: intermitTerminate110
	 */
	public static final String P_CZQYZGJBDYDF_PROCESSKEY = "intermitTerminate110";
	/**
	 * 节点名：养老待遇停发经办
	 */
	public static final String N_CZQYZGJBDYDF_JB = "养老待遇停发经办";
	/**
	 * 节点名：养老待遇停发审核
	 */
	public static final String N_CZQYZGJBDYDF_SH = "养老待遇停发审核";
	/**
	 * 节点名：养老待遇停发审批
	 */
	public static final String N_CZQYZGJBDYDF_SP = "养老待遇停发审批";

	/**********养老待遇停发   end *************/

	/**********失业待遇停发 start ************/
	/**
	 * 流程名：失业待遇停发
	 */
	public static final String P_SYDYDF = "失业待遇停发";
	/**
	 * 流程key: intermitTerminate210
	 */
	public static final String P_SYDYDF_PROCESSKEY = "intermitTerminate210";
	/**
	 * 节点名：失业待遇停发经办
	 */
	public static final String N_SYDYDF_JB = "失业待遇停发经办";
	/**
	 * 节点名：失业待遇停发审核
	 */
	public static final String N_SYDYDF_SH = "失业待遇停发审核";
	/**
	 * 节点名：失业待遇停发审批
	 */
	public static final String N_SYDYDF_SP = "失业待遇停发审批";

	/**********失业待遇停发   end *************/

    /**********工伤待遇停发 start ************/
	/**
	 * 流程名：工伤待遇停发
	 */
	public static final String P_GSBXDYDF = "工伤待遇停发";
	/**
	 * 流程key: intermitTerminate410
	 */
	public static final String P_GSBXDYDF_PROCESSKEY = "intermitTerminate410";
	/**
	 * 节点名：工伤待遇停发经办
	 */
	public static final String N_GSBXDYDF_JB = "工伤待遇停发经办";
	/**
	 * 节点名：工伤待遇停发审核
	 */
	public static final String N_GSBXDYDF_SH = "工伤待遇停发审核";
	/**
	 * 节点名：工伤待遇停发审批
	 */
	public static final String N_GSBXDYDF_SP = "工伤待遇停发审批";
	/**
	 * 流程key: intermitTerminate410GY
	 */
	public static final String P_GSBXDYDFGY_PROCESSKEY = "intermitTerminate410GY";
	/**
	 * 节点名：工伤待遇停发经办
	 */
	public static final String N_GSBXDYDFGY_JB = "工伤遗属待遇停发经办";
	/**
	 * 节点名：工伤待遇停发审核
	 */
	public static final String N_GSBXDYDFGY_SH = "工伤遗属待遇停发审核";
	/**
	 * 节点名：工伤待遇停发审批
	 */
	public static final String N_GSBXDYDFGY_SP = "工伤遗属待遇停发审批";

	/**********工伤待遇停发   end *************/

    /**********养老待遇续发 start ************/
	/**
	 * 流程名：养老待遇续发
	 */
	public static final String P_CZQYZGJBYLDYXF = "养老待遇续发";
	/**
	 * 流程key: resumeTreatment110
	 */
	public static final String P_CZQYZGJBDYXF_PROCESSKEY = "resumeTreatment110";
	/**
	 * 节点名：养老待遇续发经办
	 */
	public static final String N_CZQYZGJBDYXF_JB = "养老待遇续发经办";
	/**
	 * 节点名：养老待遇续发审核
	 */
	public static final String N_CZQYZGJBDYXF_SH = "养老待遇续发审核";
	/**
	 * 节点名：养老待遇续发审批
	 */
	public static final String N_CZQYZGJBDYXF_SP = "养老待遇续发审批";

	/**
	 * 供养亲属
	 */
	public static final String P_CZQYZGJBYLDYXFGY = "养老供养待遇续发";
	/**
	 * 流程key: resumeTreatment110GY
	 */
	public static final String P_CZQYZGJBDYXFGY_PROCESSKEY = "resumeTreatment110GY";
	/**
	 * 节点名：养老待遇续发经办
	 */
	public static final String N_CZQYZGJBDYXFGY_JB = "养老供养待遇续发经办";
	/**
	 * 节点名：养老待遇续发审核
	 */
	public static final String N_CZQYZGJBDYXFGY_SH = "养老供养待遇续发审核";
	/**
	 * 节点名：养老待遇续发审批
	 */
	public static final String N_CZQYZGJBDYXFGY_SP = "养老供养待遇续发审批";

	/**********养老待遇续发   end *************/

    /**********失业待遇续发 start ************/
	/**
	 * 流程名：失业待遇续发
	 */
	public static final String P_SYDYXF = "失业待遇续发";
	/**
	 * 流程key: resumeTreatment210
	 */
	public static final String P_SYDYXF_PROCESSKEY = "resumeTreatment210";
	/**
	 * 节点名：失业待遇续发经办
	 */
	public static final String N_SYDYXF_JB = "失业待遇续发经办";
	/**
	 * 节点名：失业待遇续发审核
	 */
	public static final String N_SYDYXF_SH = "失业待遇续发审核";
	/**
	 * 节点名：失业待遇续发审批
	 */
	public static final String N_SYDYXF_SP = "失业待遇续发审批";

	/**********失业待遇续发   end *************/

	/**********工伤待遇续发 start ************/
	/**
	 * 流程名：工伤待遇续发
	 */
	public static final String P_GSBXDYXF = "工伤待遇续发";
	/**
	 * 流程key: resumeTreatment410
	 */
	public static final String P_GSBXDYXF_PROCESSKEY = "resumeTreatment410";
	/**
	 * 节点名：工伤待遇续发经办
	 */
	public static final String N_GSBXDYXF_JB = "工伤待遇续发经办";
	/**
	 * 节点名：工伤待遇续发审核
	 */
	public static final String N_GSBXDYXF_SH = "工伤待遇续发审核";
	/**
	 * 节点名：工伤待遇续发审批
	 */
	public static final String N_GSBXDYXF_SP = "工伤待遇续发审批";
	/**
	 * 流程名：工伤供养亲属待遇续发
	 */
	public static final String P_GSBXDYXFGY = "工伤供养待遇续发";
	/**
	 * 流程key: resumeTreatment410
	 */
	public static final String P_GSBXDYXFGY_PROCESSKEY = "resumeTreatment410GY";
	/**
	 * 节点名：工伤待遇续发经办
	 */
	public static final String N_GSBXDYXFGY_JB = "工伤供养待遇续发经办";
	/**
	 * 节点名：工伤待遇续发审核
	 */
	public static final String N_GSBXDYXFGY_SH = "工伤供养待遇续发审核";
	/**
	 * 节点名：工伤待遇续发审批
	 */
	public static final String N_GSBXDYXFGY_SP = "工伤供养待遇续发审批";

	/**********工伤待遇续发   end *************/

	/**********在职退保待遇核定start ************/
	/**
	 * 流程名：养老退保待遇审理
	 */
	public static final String P_YLTBDYSL = "养老退保待遇审理";
	/**
	 * 流程key: unitResumePay
	 */
	public static final String P_YLTBDYSL_PROCESSKEY = "treatmentlumpsum";
	/**
	 * 节点名：养老退保待遇审理经办
	 */
	public static final String N_YLTBDYSL_JB = "养老退保待遇审理经办";
	/**
	 * 节点名：养老退保待遇审理审核
	 */
	public static final String N_YLTBDYSL_SH = "养老退保待遇审理审核";
	/**********在职退保待遇核定end ************/


	/********--------- 两定管理START------------**************/

	/********** 定点医疗机构新增  start **********/
	/**
	 * 流程名：定点医疗机构新增
	 */
	public static final String P_DDYLJGXZ = "定点医疗机构新增";
	/**
	 * 流程key:newHospital
	 */
	public static final String P_DDYLJGXZ_PROCESSKEY = "newHospital";
	/**
	 * 节点名:定点医疗机构新增经办
	 */
	public static final String N_DDYLJGXZ_JB = "定点医疗机构新增经办";
	/**
	 * 节点名:定点医疗机构新增审核
	 */
	public static final String N_DDYLJGXZ_SH = "定点医疗机构新增审核";
	/**
	 * 节点名:定点医疗机构新增审核不通过
	 */
	public static final String N_DDYLJGXZ_SHBTG = "定点医疗机构新增审核不通过";
	/********** 定点医疗机构新增  end **********/
	//---------------------------------------------------------------------------
	/*********** 定点医疗机构修改start ***********/
	/**
	 * 流程名:定点医疗机构修改
	 */
	public static final String P_DDYLJGXG = "定点医疗机构修改";
	/**
	 * 流程key:modifyHospital
	 */
	public static final String P_DDYLJGXG_PROCESSKEY = "modifyHospital";
	/**
	 * 节点名:定点医疗机构新增经办
	 */
	public static final String N_DDYLJGXG_JB = "定点医疗机构修改经办";
	/**
	 * 节点名:定点医疗机构新增审核
	 */
	public static final String N_DDYLJGXG_SH = "定点医疗机构修改审核";
	/**
	 * 节点名:定点医疗机构新增审核不通过
	 */
	public static final String N_DDYLJGXG_SHBTG = "定点医疗机构修改审核不通过";
	/*********** 定点医疗机构修改end ***********/
	//-----------------------------------------------------------------------------
	/*********** 个人账户清退 start ***********/
	/**
	 * 流程名:个人账户清退
	 */
	public static final String P_GRZHQT = "个人账户清退";
	/**
	 * 流程key:accountGiveReg
	 */
	public static final String P_GRZHQT_PROCESSKEY = "accountGiveReg";
	/**
	 * 节点名:个人账户清退初审
	 */
	public static final String N_GRZHQT_CS = "个人账户清退初审";
	/**
	 * 节点名:个人账户清退审核
	 */
	public static final String N_GRZHQT_FS = "个人账户清退复审";
	/**
	 * 节点名:个人账户清退审核不通过
	 */
	public static final String N_GRZHQT_SHBTG = "个人账户清退审核不通过";
	/*********** 个人账户清退 end ***********/
	//-------------------------------------------------------------------------------
	/*********** 补录个人账户 start ***********/
	/**
	 * 流程名:补录个人账户
	 */
	public static final String P_BLGRZH = "补录个人账户";
	/**
	 * 流程key:accountAgainReg
	 */
	public static final String P_BLGRZH_PROCESSKEY = "accountAgainReg";
	/**
	 * 节点名:补录个人账户初审
	 */
	public static final String N_BLGRZH_CS = "补录个人账户初审";
	/**
	 * 节点名:补录个人账户复审
	 */
	public static final String N_BLGRZH_FS = "补录个人账户复审";
	/**
	 * 节点名:补录个人账户审核不通过
	 */
	public static final String N_BLGRZH_SHBTG = "补录个人账户审核不通过";
	/*********** 补录个人账户 end ***********/
	/********--------- 两定管理END ------------**************/

	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：待遇人员基本信息维护
	 */
	public static final String P_DYRYJBXXWH = "待遇人员基本信息维护";
	/**
	 * 流程key: BasicInfoMaintenance
	 */
	public static final String P_DYRYJBXXWH_PROCESSKEY = "BasicInfoMaintenance";
	/**
	 * 节点名：信息维护经办
	 */
	public static final String N_DYRYJBXXWH_JB = "待遇人员基本信息维护经办";
	/**
	 * 节点名：信息维护审核
	 */
	public static final String N_DYRYJBXXWH_SH = "待遇人员基本信息维护审核";

	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：待遇人员基本信息维护
	 */
	public static final String P_DYRYJBXXWH_02 = "待遇人员基本信息维护";
	/**
	 * 流程key: BasicInfoMaintenance
	 */
	public static final String P_DYRYJBXXWH_PROCESSKEY_02 = "BasicInfoMaintenance02";
	/**
	 * 节点名：信息维护经办
	 */
	public static final String N_DYRYJBXXWH_JB_02 = "待遇人员基本信息维护经办";
	/**
	 * 节点名：信息维护审核
	 */
	public static final String N_DYRYJBXXWH_SH_02 = "待遇人员基本信息维护审核";
  	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：待遇发放信息维护
	 */
	public static final String P_DYFFXXWH_110 = "养老待遇发放信息维护";
	/**
	 * 流程key: BasicInfoMaintenance
	 */
	public static final String P_DYFFXXWH_PROCESSKEY_110 = "PaymentInfoMaintenance110";
	/**
	 * 节点名：待遇发放信息维护经办
	 */
	public static final String N_DYFFXXWH_JB_110 = "待遇发放信息维护经办";
	/**
	 * 节点名：待遇发放信息维护审核
	 */
	public static final String N_DYFFXXWH_SH_110 = "待遇发放信息维护审核";


  	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：待遇发放信息维护
	 */
	public static final String P_DYFFXXWH_210 = "失业待遇发放信息维护";
	/**
	 * 流程key: BasicInfoMaintenance
	 */
	public static final String P_DYFFXXWH_PROCESSKEY_210 = "PaymentInfoMaintenance210";
	/**
	 * 节点名：待遇发放信息维护经办
	 */
	public static final String N_DYFFXXWH_JB_210 = "待遇发放信息维护经办";
	/**
	 * 节点名：待遇发放信息维护审核
	 */
	public static final String N_DYFFXXWH_SH_210 = "待遇发放信息维护审核";

	  	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：特殊补发退发
	 */
	public static final String P_TSBFTF_110 = "养老特殊补发退发";
	/**
	 * 流程key: SpecialRefund
	 */
	public static final String P_TSBFTF_PROCESSKEY_110 = "SpecialRefund110";
	/**
	 * 节点名：特殊补发退发经办
	 */
	public static final String N_TSBFTF_JB_110 = "特殊补发退发经办";
	/**
	 * 节点名：特殊补发退发审核
	 */
	public static final String N_TSBFTF_SH_110 = "特殊补发退发审核";
	/**
	 * 节点名：特殊补发退发审批
	 */
	public static final String N_TSBFTF_SP_110 = "特殊补发退发审批";
  	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：特殊补发退发
	 */
	public static final String P_TSBFTF_210 = "失业特殊补发退发";
	/**
	 * 流程key: SpecialRefund
	 */
	public static final String P_TSBFTF_PROCESSKEY_210 = "SpecialRefund210";
	/**
	 * 节点名：特殊补发退发经办
	 */
	public static final String N_TSBFTF_JB_210 = "特殊补发退发经办";
	/**
	 * 节点名：特殊补发退发审核
	 */
	public static final String N_TSBFTF_SH_210 = "特殊补发退发审核";
	/**
	 * 节点名：特殊补发退发审批
	 */
	public static final String N_TSBFTF_SP_210 = "特殊补发退发审批";
	  	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：零星调整
	 */
	public static final String P_LXTZ_110 = "养老零星调整";
	/**
	 * 流程key: SporadicAdjust
	 */
	public static final String P_LXTZ_PROCESSKEY_110 = "SporadicAdjust110";
	/**
	 * 节点名：零星调整经办
	 */
	public static final String N_LXTZ_JB_110 = "零星调整经办";
	/**
	 * 节点名：零星调整审核
	 */
	public static final String N_LXTZ_SH_110 = "零星调整审核";
	/**
	 * 节点名：零星调整审批
	 */
	public static final String N_LXTZ_SP_110 = "零星调整审批";


  	/*命名规则
	 *流程名：P_流程名拼音首字母
	 *节点名：N_流程名拼音首字母_节点名拼音首字母
	*/
	/**
	 * 流程名：零星调整
	 */
	public static final String P_LXTZ_210 = "失业零星调整";
	/**
	 * 流程key: SporadicAdjust
	 */
	public static final String P_LXTZ_PROCESSKEY_210 = "SporadicAdjust210";
	/**
	 * 节点名：零星调整经办
	 */
	public static final String N_LXTZ_JB_210 = "零星调整经办";
	/**
	 * 节点名：零星调整审核
	 */
	public static final String N_LXTZ_SH_210 = "零星调整审核";
	/**
	 * 节点名：零星调整审批
	 */
	public static final String N_LXTZ_SP_210 = "零星调整审批";
    /**********养老零星支付 start ************/
    /**
    * 流程名：养老零星支付
	*/
    public static final String P_LXZF = "养老零星支付";
	/**
	* 流程key: backCheckPayment110
	*/
	public static final String P_LXZF_PROCESSKEY = "backCheckPayment110";
	/**
	 * 节点名：养老零星支付经办
	 */
	public static final String N_LXZF_JB = "养老零星支付经办";
	/**
	 * 节点名：养老零星支付审核
	 */
	public static final String N_LXZF_SH = "养老零星支付审核";
	/**********养老零星支付   end *************/

  	/*命名规则
     *流程名：P_流程名拼音首字母
     *节点名：N_流程名拼音首字母_节点名拼音首字母
    */
	/**********失业待遇核定start ************/
	/**
	 * 生育补贴流程key: birthAllowance
	 */
	public static final String P_SYBT_PROCESSKEY = "birthAllowance";
	/**
	 * 节点名：生育补贴经办
	 */
	public static final String N_SYBT_JB = "生育补贴经办";
	/**
	 * 节点名：生育补贴审核
	 */
	public static final String N_SYBT_SH = "生育补贴审核";
	/**
	 * 节点名：生育补贴审批
	 */
	public static final String N_SYBT_SP = "生育补贴审批";
	/**
	 * 流程key: 职业培训费
	 */
	public static final String P_ZYPXF_PROCESSKEY = "training";
	/**
	 * 节点名：职业培训费经办
	 */
	public static final String N_ZYPXF_JB = "职业培训费经办";
	/**
	 * 节点名：职业培训费审核
	 */
	public static final String N_ZYPXF_SH = "职业培训费审核";
	/**
	 * 节点名：职业培训费审批
	 */
	public static final String N_ZYPXF_SP = "职业培训费审批";
	/**********失业待遇核定end  ************/
	/**
	 * 流程key: 职业介绍费
	 */
	public static final String P_ZYJSF_PROCESSKEY = "introduction";
	/**
	 * 节点名：职业介绍费经办
	 */
	public static final String N_ZYJSF_JB = "职业介绍费经办";
	/**
	 * 节点名：职业介绍费审核
	 */
	public static final String N_ZYJSF_SH = "职业介绍费审核";
	/**
	 * 节点名：职业介绍费审批
	 */
	public static final String N_ZYJSF_SP = "职业介绍费审批";
	/**
	 * 流程key: 失业待遇重算
	 */
	public static final String P_SYDYCS_PROCESSKEY = "treatmentRerun";
	/**
	 * 节点名：失业待遇重算经办
	 */
	public static final String N_SYDYCS_JB = "失业待遇重算经办";
	/**
	 * 节点名：失业待遇重算审核
	 */
	public static final String N_SYDYCS_SH = "失业待遇重算审核";
	/**
	 * 节点名：失业待遇重算审批
	 */
	public static final String N_SYDYCS_SP = "失业待遇重算审批";
	/**
	 * 流程key: 定期转一次性领取经办
	 */
	public static final String P_DQZYCX_PROCESSKEY = "turnToLumpsum";
	/**
	 * 节点名：定期转一次性领取经办
	 */
	public static final String N_DQZYCX_JB = "定期转一次性领取经办";
	/**
	 * 节点名：定期转一次性领取审核
	 */
	public static final String N_DQZYCX_SH = "定期转一次性领取审核";
	/**
	 * 节点名：定期转一次性领取审批
	 */
	public static final String N_DQZYCX_SP = "定期转一次性领取审批";
	/**
	 * 流程key: 失业人员新增
	 */
	public static final String P_SYRYXZ_PROCESSKEY = "uneTreatmentCalculation";
	/**
	 * 节点名：失业人员新增经办
	 */
	public static final String N_SYRYXZ_JB = "失业人员新增经办";
	/**
	 * 节点名：失业人员新增审核
	 */
	public static final String N_SYRYXZ_SH = "失业人员新增审核";
	/**
	 * 节点名：失业人员新增审批
	 */
	public static final String N_SYRYXZ_SP = "失业人员新增审批";

	/**********失业待遇核定end  ************/
}