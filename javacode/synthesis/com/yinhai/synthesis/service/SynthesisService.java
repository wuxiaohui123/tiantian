package com.yinhai.synthesis.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.service.Service;

/**
 * @标题 SynthesisService
 * @说明 业务系统基础Service
 * @使用 业务系统所有Service均继承此Service或继承继承此Service的Service
 * 
 */
@SuppressWarnings({ "rawtypes" })
public interface SynthesisService extends Service {

    /**
     * 查询树形下拉列表所需数据,返回list
     * 
     * @param aaa100
     *            （collection名称）
     * @param aaa102
     *            （collection值,使用like过滤,拼接方式aaa102||'%'）
     * @return List
     * @throws AppException
     */
    public List getSelectInputLevelDataByList(String aaa100, String aaa102, String yab003) throws AppException;
    
    
    /**
     * 查询树形下拉列表所需数据,返回list
     * 
     * @param aaa100
     *            （collection名称）
     * @param aaa102
     *            （collection值,使用like过滤,拼接方式aaa102||'%'）
     * @return List
     * @throws AppException
     */
    public List getSelectInputLevelDataListByCache(String aaa100, String aaa102, String yab003) throws AppException;

    /**
     * 获取期号上一期
     * 
     * @param aae002
     * @return Integer
     * @throws AppException
     */
    public Integer getLastMonths(Integer aae002) throws AppException;

    /**
     * 
     * 获取指定格式的日期字符串
     * 
     * @param format
     *            （'YYYY-MM-DD
     *            HH24:MI:SS','YYYY-MM-DD','YYYYMM','YYYY','HH24:MI:SS'）
     * @return String
     */
    public String getSysdate(String format);

    /**
     * 查询标准格式的提示信息
     * 
     * @param aaz002
     *            （业务日志ID）
     * @return String
     * @throws AppException
     */
    public String getTipsMessage(String aaz002) throws AppException;

    /**
     * delete方法
     * 
     * @param arg0
     *            （SQL语句）
     * @param arg1
     *            （参数）
     * @param arg2
     *            （运算方法有[!=、>、 <]三类,为空时使用[>]）
     * @param arg3
     *            （操作记录数,为空时使用500）
     * @return int
     */
    public int delete(String arg0, Object arg1, String arg2, int arg3);

    /**
     * delete方法,操作记录数为500
     * 
     * @param arg0
     *            （SQL语句）
     * @param arg1
     *            （参数）
     * @param arg2
     *            （运算方法有[!=、>、 <]三类,为空时使用[>]）
     * @return int
     */
    public int delete(String arg0, Object arg1, String arg2);

    /**
     * delete方法,运算方法为>,操作记录数为500
     * 
     * @param arg0
     *            （SQL语句）
     * @param arg1
     *            （参数）
     * @return int
     */
    public int delete(String arg0, Object arg1);

    /**
     * 前台进行数据变更且要使用公用回退,必须先执行此方法
     * 
     * @param aaa831
     *            （是否使用触发器1:是,0:否）
     * @param aaa832
     *            （是否可回退1:是,0:否）
     * @throws AppException
     */
    public void insertTemp_Aa83(String aaa831, String aaa832) throws AppException;

    /**
     * 
     * 获取序列
     * 
     * @param arg0
     *            （前缀）
     * @param arg1
     *            （编码）
     * @return String
     */
    public String getSequence(String arg0, String arg1);

    /**
     * 将符合oracle中in运算的字符串转换为list
     * 
     * @param arg
     *            （举例：'110','210','310'）
     * @return List
     * @throws AppException
     */
    public List getStringArray(String arg);
    
}
