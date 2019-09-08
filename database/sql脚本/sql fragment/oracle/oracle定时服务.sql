
DROP TABLE QRTZ_JOB_MSGS CASCADE CONSTRAINTS;
DROP TABLE YHCIP_ORACLE_JOBS CASCADE CONSTRAINTS;
CREATE TABLE YHCIP_ORACLE_JOBS  (
   JOBID                VARCHAR2(20)                    not null,
   ORACLEJOBID          VARCHAR2(20)                    not null,
   JOBNAME              VARCHAR2(100)                   not null,
   USERID               VARCHAR2(20)                    not null,
   WHAT                 VARCHAR2(200)                   not null,
   STARTTIME            VARCHAR2(200)                   not null,
   ENDTIME              DATE,
   INTERVAL             VARCHAR2(200),
   SUBMITTIME           DATE,
   constraint PK_YHCIP_ORACLE_JOBS primary key (JOBID)
);

COMMENT ON TABLE YHCIP_ORACLE_JOBS IS
'oracle定时';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.JOBID IS
'任务id';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.ORACLEJOBID IS
'oracle的jobid';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.JOBNAME IS
'任务名称';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.USERID IS
'用户id';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.WHAT IS
'执行的过程';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.STARTTIME IS
'开始时间';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.ENDTIME IS
'结束时间';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.INTERVAL IS
'时间间隔';

COMMENT ON COLUMN YHCIP_ORACLE_JOBS.SUBMITTIME IS
'提交时间';
CREATE TABLE QRTZ_JOB_MSGS  (
   ID                   VARCHAR2(50)                    NOT NULL,
   JOB_NAME             VARCHAR2(80)                    NOT NULL,
   JOB_GROUP            VARCHAR2(80)                    NOT NULL,
   USERID               VARCHAR2(20),
   EXECSTARTTIME        VARCHAR2(30),
   EXECENDTIME          VARCHAR2(30),
   ISSUCCESS            VARCHAR2(2),
   SUCCESSMSG           VARCHAR2(1024),
   ERRORMSG             VARCHAR2(1024),
   CONSTRAINT SYS_C0052264 PRIMARY KEY (ID)
);
CREATE OR REPLACE PACKAGE pkg_YHCIP AS
   /*---------------------------------------------------------------------------
   ||  程序包名 ：pkg_YHCIP                                                   
   ||  业务环节 ：YHCIP                                                       
   ||  对象列表 ：私有过程函数                                                
   ||             --------------------------------------------------------------
   ||             公用过程函数                                                
   ||             --------------------------------------------------------------
   ||                                                                         
   ||  其它说明 ：                                                            
   ||  完成日期 ：                                                            
   ||  版本编号 ：Ver 1.0                                                     
   ||  审 查 人 ：×××                      审查日期 ：YYYY-MM-DD           
   ||-------------------------------------------------------------------------*/

   /*-------------------------------------------------------------------------*/
   /* 公用全局数据类型声明                                                    */
   /*-------------------------------------------------------------------------*/

   /*-------------------------------------------------------------------------*/
   /* 公用全局常量声明                                                        */
   /*-------------------------------------------------------------------------*/
   gn_def_OK  CONSTANT VARCHAR2(12) := 'NOERROR'; -- 成功
   gn_def_ERR CONSTANT VARCHAR2(12) := '9999'; -- 系统错误
   
   GN_DEF_YES CONSTANT VARCHAR2(12) := '是'; -- 是
   GN_DEF_NO  CONSTANT VARCHAR2(12) := '否'; -- 否

   gs_def_DatetimeFormat  CONSTANT VARCHAR2(21) := 'YYYY-MM-DD HH24:MI:SS';
   gs_def_DateFormat      CONSTANT VARCHAR2(10) := 'YYYY-MM-DD';
   gs_def_YearMonthFormat CONSTANT VARCHAR2(6) := 'YYYYMM';
   gs_def_YearFormat      CONSTANT VARCHAR2(4) := 'YYYY';
   gs_def_TimeFormat      CONSTANT VARCHAR2(10) := 'HH24:MI:SS';
   gs_def_NumberFormat    CONSTANT VARCHAR2(15) := '999999999999.99';
   gs_def_NOFormat        CONSTANT VARCHAR2(15) := '999999999999999';
   gs_def_NullDate        CONSTANT DATE := TO_DATE('1900-01-01',
                                                   gs_def_DateFormat);

   /*-------------------------------------------------------------------------*/
   /* 公用过程函数声明                                                        */
   /*-------------------------------------------------------------------------*/

   /*模版*/
   PROCEDURE prc_Template (
      prm_AppCode                         OUT      VARCHAR2          ,
      prm_ErrorMsg                        OUT      VARCHAR2          );
   

   PROCEDURE prc_oracleJob (
			      prm_jobid                          IN OUT VARCHAR2      ,--jobid外部传进来的是用于YHCIP_ORACLE_JOBS表的主键，出去的是oracle生成的jobid
			      prm_jobname			                 IN     VARCHAR2      ,--任务名称
			      prm_what                           IN     VARCHAR2      ,--执行过程，需要“;”分号结尾
			      prm_next_date                      IN     VARCHAR2      ,--执行时间
			      prm_interval                       IN     VARCHAR2      ,--间隔循环时间
			      prm_userid                         IN     VARCHAR2      ,--用户id
			      prm_AppCode                        OUT    VARCHAR2      ,
			      prm_ErrorMsg                       OUT    VARCHAR2      );     
   PROCEDURE prc_oraclejobbroken(
			      prm_jobid                          IN     NUMBER        ,--jobid外部传进来的是用于YHCIP_ORACLE_JOBS表的主键，出去的是oracle生成的jobid
			      prm_broken                         IN     VARCHAR2      ,--是否暂停
			      prm_next_date                      IN     VARCHAR2      ,--下次执行时间
			      prm_AppCode                        OUT    VARCHAR2      ,
			      prm_ErrorMsg                       OUT    VARCHAR2      );
   PROCEDURE prc_oraclejobchange (
			      prm_jobid                          IN OUT VARCHAR2      ,--jobid外部传进来的是用于YHCIP_ORACLE_JOBS表的主键，出去的是oracle生成的jobid 
			      prm_jobname                        IN     VARCHAR2      ,--任务名称                                                                 
			      prm_what                           IN     VARCHAR2      ,--执行过程，需要“;”分号结尾                                              
			      prm_next_date                      IN     VARCHAR2      ,--执行时间                                                                 
			      prm_interval                       IN     VARCHAR2      ,--间隔循环时间                                                             
			      prm_userid                         IN     VARCHAR2      ,--用户id                                                                   
			      prm_AppCode                        OUT    VARCHAR2      ,                                                                           
			      prm_ErrorMsg                       OUT    VARCHAR2      )	;		      			                          

END pkg_YHCIP;
/

show error;

CREATE OR REPLACE PACKAGE BODY pkg_YHCIP AS
   /*---------------------------------------------------------------------------
   ||  程序包名 ：pkg_YHCIP                                                   
   ||  业务环节 ：YHCIP                                                       
   ||  对象列表 ：私有过程函数                                                
   ||             --------------------------------------------------------------
   ||             公用过程函数                                                
   ||             --------------------------------------------------------------
   ||                                                                         
   ||  其它说明 ：                                                            
   ||  完成日期 ：                                                            
   ||  版本编号 ：Ver 1.0                                                     
   ||  审 查 人 ：×××                      审查日期 ：YYYY-MM-DD           
   ||-------------------------------------------------------------------------*/
   /*------------------------------------------------------------------------*/
   /* 私有全局数据类型声明                                                   */
   /*------------------------------------------------------------------------*/

   /*------------------------------------------------------------------------*/
   /* 私有全局常量声明                                                       */
   /*------------------------------------------------------------------------*/
   PRE_ERRCODE  CONSTANT VARCHAR2(12) := 'YHCIP'; -- 本包的错误编号前缀
   NULL_PREFIX  CONSTANT VARCHAR2(1) := ' ';

   /*------------------------------------------------------------------------*/
   /* 私有全局变量声明                                                       */
   /*------------------------------------------------------------------------*/

   /*------------------------------------------------------------------------*/
   /* 私有过程函数声明                                                       */
   /*------------------------------------------------------------------------*/

   /*------------------------------------------------------------------------*/
   /* 公用过程函数描述                                                       */
   /*------------------------------------------------------------------------*/

   /*---------------------------------------------------------------------------
   || 业务环节 ：prc_Template
   || 过程名称 ：
   || 功能描述 ：
   || 使用场合 ：
   || 参数描述 ：标识                  名称             输入输出   数据类型
   ||            ---------------------------------------------------------------
   ||            prm_AppCode        执行代码             输出     VARCHAR2(12)
   ||            prm_ErrorMsg       出错信息             输出     VARCHAR2(128)
   ||
   || 参数说明 ：标识               详细说明
   ||            ---------------------------------------------------------------
   ||
   || 其它说明 ：
   || 作    者 ：
   || 完成日期 ：
   ||---------------------------------------------------------------------------
   ||                                 修改记录
   ||---------------------------------------------------------------------------
   || 修 改 人 ：×××                        修改日期 ：YYYY-MM-DD
   || 修改描述 ：
   ||-------------------------------------------------------------------------*/
   PROCEDURE prc_Template (
      prm_AppCode                        OUT      VARCHAR2          ,
      prm_ErrorMsg                       OUT      VARCHAR2          )
   IS
      /*变量声明*/
      /*游标声明*/
   BEGIN
      /*初始化变量*/
      prm_AppCode  := PRE_ERRCODE || gn_def_ERR;
      prm_ErrorMsg := ''                                ;

      /*成功处理*/
      <<label_OK>>
      /*关闭打开的游标*/
      /*给返回参数赋值*/
      prm_AppCode  := gn_def_OK ;
      prm_ErrorMsg := ''                 ;
      RETURN ;

      /*处理失败*/
      <<label_ERROR>>
      /*关闭打开的游标*/
      /*给返回参数赋值*/
      IF prm_AppCode = gn_def_OK THEN
         prm_AppCode  := PRE_ERRCODE || gn_def_ERR;
      END IF ;
      RETURN ;

   EXCEPTION
      -- WHEN NO_DATA_FOUND THEN
      -- WHEN TOO_MANY_ROWS THEN
      -- WHEN DUP_VAL_ON_INDEX THEN
      WHEN OTHERS THEN
         /*关闭打开的游标*/
         prm_AppCode  := PRE_ERRCODE || gn_def_ERR;
         prm_ErrorMsg := '数据库错误:'|| SQLERRM ;
         RETURN;
   END prc_Template ;



  /*---------------------------------------------------------------------------
  || 业务环节 ：创建一个定时服务
  || 过程名称 ：prc_oracleJob
  || 功能描述 ：创建一个定时服务
  ||
  || 使用场合 ：对需要定时执行的过程
  || 参数描述 ：标识                  名称             输入输出   数据类型
  ||            ---------------------------------------------------------------
  ||		prm_jobid          任务编号             输入/输出     VARCHAR2
  ||		prm_jobname	   任务名称		 输入         VARCHAR2 
  ||		prm_what           需要执行的过程        输入         VARCHAR2
  ||		prm_next_date      执行时间              输入         VARCHAR2 
  ||		prm_interval       间隔循环时间          输入         VARCHAR2 
  ||		prm_userid         用户id                输入         VARCHAR2
  ||            prm_AppCode        执行代码              输出         VARCHAR2(12) 
  ||            prm_ErrorMsg       出错信息              输出         VARCHAR2(128)   
  ||
  || 参数说明 ：标识               详细说明
  ||            ---------------------------------------------------------------
  ||
  || 其它说明 ：
  || 作    者 ：林隆永
  || 完成日期 ：
  ||---------------------------------------------------------------------------
  ||                                 修改记录
  ||---------------------------------------------------------------------------
  || 修 改 人 ：×××                        修改日期 ：YYYY-MM-DD
  || 修改描述 ：
  ||-------------------------------------------------------------------------*/
  PROCEDURE prc_oracleJob (
      prm_jobid                          IN OUT VARCHAR2      ,--jobid外部传进来的是用于YHCIP_ORACLE_JOBS表的主键，出去的是oracle生成的jobid
      prm_jobname			 IN     VARCHAR2      ,--任务名称
      prm_what                           IN     VARCHAR2      ,--执行过程，需要“;”分号结尾
      prm_next_date                      IN     VARCHAR2      ,--执行时间
      prm_interval                       IN     VARCHAR2      ,--间隔循环时间
      prm_userid                         IN     VARCHAR2      ,--用户id
      prm_AppCode                        OUT    VARCHAR2      ,
      prm_ErrorMsg                       OUT    VARCHAR2      )
   IS
      /*变量声明*/
      v_next_date date;
      jobid BINARY_INTEGER;
      sqlStr varchar2(200);
      /*游标声明*/
   BEGIN
      /*给返回参数赋值*/
      prm_AppCode  := gn_def_OK;
      prm_ErrorMsg := '';
      sqlStr := 'select to_date(to_char('||prm_next_date||',''yyyy-MM-dd HH24:mi:ss''),''yyyy-MM-dd HH24:mi:ss'') from dual';
      execute immediate sqlStr into v_next_date;

     IF prm_interval IS NULL THEN
       dbms_job.submit(jobid,prm_what,v_next_date);
     ELSE
        dbms_job.submit(jobid,prm_what,v_next_date,prm_interval);
     END IF;

      INSERT INTO YHCIP_ORACLE_JOBS(
                       JOBID,--代表一个定时任务
      		       JOBNAME,--定时任务的名称
      		       STARTTIME,--开始执行时间
      		       USERID,--执行的用户
                       oraclejobid,
                       what,
                       interval)--oracle的jobid
      		VALUES (
      		       prm_jobid,
      		       prm_jobname,
      		       prm_next_date,
      	               prm_userid,
                       jobid,
                       prm_what,
                       prm_interval);

      prm_jobid := jobid;
      
      INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  USERID,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  SUCCESSMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobname,
                  'DEFAULT',
                  prm_userid,
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '0',
                  'LOG:创建成功');
      commit;
   EXCEPTION
      -- WHEN NO_DATA_FOUND THEN
      -- WHEN TOO_MANY_ROWS THEN
      -- WHEN DUP_VAL_ON_INDEX THEN
      WHEN OTHERS THEN
         /*关闭打开的游标*/
         INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  USERID,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  ERRORMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobname,
                  'DEFAULT',
                  prm_userid,
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '1',
                  'LOG:创建失败');
         commit;          
         prm_AppCode  := PRE_ERRCODE||'0006';
         prm_ErrorMsg := '数据库错误:'|| SQLERRM ;
         RETURN;
   END prc_oracleJob ;
  /*---------------------------------------------------------------------------
  || 业务环节 ：暂停、继续 任务
  || 过程名称 ：prc_oraclejobbroken
  || 功能描述 ：暂停、继续 任务
  ||
  || 使用场合 ：对任务进行暂停、继续任务
  || 参数描述 ：标识                  名称             输入输出   数据类型
  ||            ---------------------------------------------------------------
  ||		prm_jobid          任务编号             输入/输出     VARCHAR2
  ||		prm_broken	   暂停、继续(true,false)输入         VARCHAR2 
  ||		prm_next_date      执行时间              输入         VARCHAR2 
  ||            prm_AppCode        执行代码              输出         VARCHAR2(12) 
  ||            prm_ErrorMsg       出错信息              输出         VARCHAR2(128)   
  ||
  || 参数说明 ：标识               详细说明
  ||            ---------------------------------------------------------------
  ||
  || 其它说明 ：
  || 作    者 ：林隆永
  || 完成日期 ：
  ||---------------------------------------------------------------------------
  ||                                 修改记录
  ||---------------------------------------------------------------------------
  || 修 改 人 ：×××                        修改日期 ：YYYY-MM-DD
  || 修改描述 ：
  ||-------------------------------------------------------------------------*/  
	PROCEDURE prc_oraclejobbroken(
	      prm_jobid                          IN     NUMBER        ,
	      prm_broken                         IN     VARCHAR2      ,
	      prm_next_date                      IN     VARCHAR2      ,
	      prm_AppCode                        OUT    VARCHAR2      ,
	      prm_ErrorMsg                       OUT    VARCHAR2      )
	IS
	      v_next_date date;
	      sqlStr varchar2(200);
	BEGIN
	      /*给返回参数赋值*/
	  prm_AppCode  := gn_def_OK ;--pkg_COMM.gn_def_OK
	  prm_ErrorMsg := '';


	  IF prm_broken = 'false' OR prm_broken = 'FALSE' THEN
	    sqlStr := 'select to_date(to_char('||prm_next_date||',''yyyy-MM-dd HH24:mi:ss''),''yyyy-MM-dd HH24:mi:ss'') from dual';
	    execute immediate sqlStr into v_next_date;
	    DBMS_JOB.broken(prm_jobid,false,v_next_date);
      INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  SUCCESSMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobid,
                  'DEFAULT',
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '0',
                  'LOG:继续执行成功');
	  ELSIF prm_broken = 'true' OR prm_broken = 'TRUE' THEN
	    DBMS_JOB.broken(prm_jobid,true);
       INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  SUCCESSMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobid,
                  'DEFAULT',
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '0',
                  'LOG:暂停成功');
	  END IF;
	  commit;
	  EXCEPTION
	      -- WHEN NO_DATA_FOUND THEN
	      -- WHEN TOO_MANY_ROWS THEN
	      -- WHEN DUP_VAL_ON_INDEX THEN
	      WHEN OTHERS THEN
	         /*关闭打开的游标*/
           INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  ERRORMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobid,
                  'DEFAULT',
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '1',
                  'LOG:暂停或继续执行失败');
           commit;
	         prm_AppCode  := PRE_ERRCODE||'0007';
	         prm_ErrorMsg := '数据库错误:'|| SQLERRM ;
	         RETURN;
	END prc_oraclejobbroken;
  /*---------------------------------------------------------------------------
  || 业务环节 ：更改一个定时服务
  || 过程名称 ：prc_oraclejobchange
  || 功能描述 ：更改一个定时服务
  ||
  || 使用场合 ：更改一个定时服务
  || 参数描述 ：标识                  名称             输入输出   数据类型
  ||            ---------------------------------------------------------------
  ||		prm_jobid          任务编号             输入/输出     VARCHAR2
  ||		prm_jobname	   任务名称		 输入         VARCHAR2 
  ||		prm_what           需要执行的过程        输入         VARCHAR2
  ||		prm_next_date      执行时间              输入         VARCHAR2 
  ||		prm_interval       间隔循环时间          输入         VARCHAR2 
  ||		prm_userid         用户id                输入         VARCHAR2
  ||            prm_AppCode        执行代码              输出         VARCHAR2(12) 
  ||            prm_ErrorMsg       出错信息              输出         VARCHAR2(128)   
  ||
  || 参数说明 ：标识               详细说明
  ||            ---------------------------------------------------------------
  ||
  || 其它说明 ：
  || 作    者 ：林隆永
  || 完成日期 ：
  ||---------------------------------------------------------------------------
  ||                                 修改记录
  ||---------------------------------------------------------------------------
  || 修 改 人 ：×××                        修改日期 ：YYYY-MM-DD
  || 修改描述 ：
  ||-------------------------------------------------------------------------*/	
  PROCEDURE prc_oraclejobchange (
      prm_jobid                          IN OUT VARCHAR2      ,--jobid外部传进来的是用于YHCIP_ORACLE_JOBS表的主键，出去的是oracle生成的jobid
      prm_jobname                        IN     VARCHAR2      ,--任务名称
      prm_what                           IN     VARCHAR2      ,--执行过程，需要“;”分号结尾
      prm_next_date                      IN     VARCHAR2      ,--执行时间
      prm_interval                       IN     VARCHAR2      ,--间隔循环时间
      prm_userid                         IN     VARCHAR2      ,--用户id
      prm_AppCode                        OUT    VARCHAR2      ,
      prm_ErrorMsg                       OUT    VARCHAR2      )
   IS
      /*变量声明*/
      v_next_date date;
      sqlStr varchar2(200);
      /*游标声明*/
   BEGIN
      /*给返回参数赋值*/
      prm_AppCode  := gn_def_OK;--pkg_COMM.gn_def_OK
      prm_ErrorMsg := '';

      sqlStr := 'select to_date(to_char('||prm_next_date||',''yyyy-MM-dd HH24:mi:ss''),''yyyy-MM-dd HH24:mi:ss'') from dual';
      execute immediate sqlStr into v_next_date;

      dbms_job.change(prm_jobid,prm_what,v_next_date,prm_interval);
      UPDATE YHCIP_ORACLE_JOBS
          SET userid     = prm_userid   ,
              starttime  = prm_next_date,
              jobname    = prm_jobname  ,
              what       = prm_what     ,
              interval   = prm_interval
          WHERE
              oraclejobid=prm_jobid;
      
      INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  USERID,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  SUCCESSMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobid,
                  'DEFAULT',
                  prm_userid,
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '0',
                  'LOG:更改成功');              
      commit;
   EXCEPTION
      -- WHEN NO_DATA_FOUND THEN
      -- WHEN TOO_MANY_ROWS THEN
      -- WHEN DUP_VAL_ON_INDEX THEN
      WHEN OTHERS THEN
         /*关闭打开的游标*/
  INSERT INTO QRTZ_JOB_MSGS(
                  ID,
                  JOB_NAME,
                  JOB_GROUP,
                  USERID,
                  EXECSTARTTIME,
                  EXECENDTIME,
                  ISSUCCESS,
                  ERRORMSG)VALUES(
                  SEQ_DEFAULT.NEXTVAL,
                  prm_jobid,
                  'DEFAULT',
                  prm_userid,
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
                  '1',
                  'LOG:更改失败'); 
   commit; 
	 prm_AppCode  := PRE_ERRCODE||'0008';
	 prm_ErrorMsg := '数据库错误:'|| SQLERRM ;
         RETURN;
   END prc_oraclejobchange ;

END pkg_YHCIP;
/
show error;

commit;
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('23','2','oracle定时服务','system/oracleJobAction.do','1/2/20','银海软件/系统管理/oracle定时服务','tree-star',NULL,NULL,NULL,'0','1','1',NULL,'3','01','3','0','1',NULL,'sysmg',0);
