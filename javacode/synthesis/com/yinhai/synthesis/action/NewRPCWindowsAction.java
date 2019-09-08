package com.yinhai.synthesis.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.synthesis.util.Constant;

@Namespace("/newRPCWindows")
@Action(value = "newRPCWindowsAction", results = {  @Result(name = "newRPCWindow01", location = Constant.XMMC + "/newRPCWindows/newRPCWindows01.jsp"), 
													@Result(name = "newRPCWindow02", location = Constant.XMMC + "/newRPCWindows/newRPCWindows02.jsp"), 
													@Result(name = "newRPCWindow02_1", location = Constant.XMMC + "/newRPCWindows/newRPCWindows02_1.jsp"), 
													@Result(name = "newRPCWindow03", location = Constant.XMMC + "/newRPCWindows/newRPCWindows03.jsp"),
													@Result(name = "newRPCWindow04", location = Constant.XMMC + "/newRPCWindows/newRPCWindows04.jsp"), 
													@Result(name = "newRPCWindow05", location = Constant.XMMC + "/newRPCWindows/newRPCWindows05.jsp"), 
													@Result(name = "newRPCWindow06", location = Constant.XMMC + "/newRPCWindows/newRPCWindows06.jsp"), 
													@Result(name = "newRPCWindow07", location = Constant.XMMC + "/newRPCWindows/newRPCWindows07.jsp"),
													@Result(name = "newRPCWindow08", location = Constant.XMMC + "/newRPCWindows/newRPCWindows08.jsp"), 
													@Result(name = "newRPCWindow09", location = Constant.XMMC + "/newRPCWindows/newRPCWindows09.jsp"),
													@Result(name = "newRPCWindow10", location = Constant.XMMC + "/newRPCWindows/newRPCWindows10.jsp"),
													@Result(name = "newRPCWindow11", location = Constant.XMMC + "/newRPCWindows/newRPCWindows11.jsp"),
													@Result(name = "newRPCWindow12", location = Constant.XMMC + "/newRPCWindows/newRPCWindows12.jsp"),
													@Result(name = "newRPCWindow13", location = Constant.XMMC + "/newRPCWindows/newRPCWindows13.jsp"),
													@Result(name = "newRPCWindow14", location = Constant.XMMC + "/newRPCWindows/newRPCWindows14.jsp"),
													@Result(name = "newRPCWindow15", location = Constant.XMMC + "/newRPCWindows/newRPCWindows15.jsp")
												})
public class NewRPCWindowsAction extends SynthesisAction {

	/**
	 * 人员RPC
	 * 
	 * @return
	 */
	public String showRPCWindow01() {
		setFocus("aac001");
		return "newRPCWindow01";
	}

	/**
	 * 单位RPC带组织状态
	 * 
	 * @return
	 */
	public String showRPCWindow02() {
		setFocus("aab001");
		return "newRPCWindow02";
	}

	/**
	 * 单位RPC不带组织状态
	 * 
	 * @return
	 */
	public String showRPCWindow02_1() {
		setFocus("aab001");
		return "newRPCWindow02_1";
	}

	/**
	 * 税务机构RPC
	 * 
	 * @return
	 */
	public String showRPCWindow03() {
		setData("aab301", getDto().getAsString("aab301"));
		setFocus("aaz066");
		return "newRPCWindow03";
	}

	/**
	 * 单位银行信息RPC
	 * 
	 * @return
	 */
	public String showRPCWindow04() {
		setData("aae140", getDto().getAsString("aae140"));
		setFocus("aaz065");
		return "newRPCWindow04";
	}

	/**
	 * 社保机构银行信息RPC
	 * 
	 * @return
	 */
	public String showRPCWindow05() {
		setData("aae140", getDto().getAsString("aae140"));
		setFocus("aaz003");
		return "newRPCWindow05";
	}

	/**
	 * 单位人员信息RPC
	 * 
	 * @return
	 */
	public String showRPCWindow06() {
		setFocus("aaz010");
		return "newRPCWindow06";
	}

	/**
	 * 征集通知ID RPC
	 * 
	 * @return
	 */
	public String showRPCWindow07() {
		setFocus("aaz288");
		return "newRPCWindow07";
	}

	/**
	 * 查询失业代管单位RPC
	 * 
	 * @return
	 */
	public String showRPCWindow09() {
		setFocus("aab001");
		return "newRPCWindow09";
	}

	/**
	 * 原参保地RPC
	 * 
	 * @return
	 */
	public String showRPCWindow08() {
		setFocus("yac250");
		return "newRPCWindow08";
	}
	
	/**
	 * 原参保地RPC
	 * 
	 * @return
	 */
	public String showRPCWindow10() {
		setFocus("yac250");
		return "newRPCWindow10";
	}
	
	/**
	 * 转入地银行信息
	 */
	public String showRPCWindow11(){
		setFocus("aae008_r");
		return "newRPCWindow11";
	}
	
	/**
	 * 转入地银行信息
	 */
	public String showRPCWindow12(){
		setFocus("aae008");
		return "newRPCWindow12";
	}
	
	/**
	 * 个人基本信息带经办机构险种
	 */
	public String showRPCWindow13(){
		setFocus("aac001");
		return "newRPCWindow13";
	}
	
	/**
	 * 个人基本信息不带经办机构险种
	 */
	public String showRPCWindow14(){
		setFocus("aac003");
		return "newRPCWindow14";
	}
	/**
	 * 个人基本信息不带经办机构险种
	 */
	public String showRPCWindow15(){
		setFocus("aac001");
		return "newRPCWindow15";
	}
}
