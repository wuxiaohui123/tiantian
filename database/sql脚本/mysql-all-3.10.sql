/* 新建时不用删除外键  
ALTER TABLE TAORG DROP FOREIGN KEY FK_REFERENCE_6;
ALTER TABLE TAPOSITION DROP FOREIGN KEY FK_RELATIONSHIP_5;
ALTER TABLE TAPOSITIONAUTHRITY DROP FOREIGN KEY FK_REFERENCE_7;
ALTER TABLE TAPOSITIONAUTHRITY DROP FOREIGN KEY FK_RELATIONSHIP_11;
ALTER TABLE TAUSERPOSITION DROP FOREIGN KEY FK_RELATIONSHIP_10;
ALTER TABLE TAUSERPOSITION DROP FOREIGN KEY FK_RELATIONSHIP_9;
ALTER TABLE TARUNQIANRESOURCE
   DROP FOREIGN KEY FK_YHCIP_RU_REFERENCE_YHCIP_RU;
ALTER TABLE TARUNQIANAD52REFERENCE
   DROP FOREIGN KEY FK_REPORT_INFO;
*/

DROP view IF EXISTS AA10A1;

DROP TABLE IF EXISTS AA10;

DROP TABLE IF EXISTS TACONFIG;

DROP TABLE IF EXISTS TACONFIGSYSPATH;

DROP TABLE IF EXISTS TAORGMG;

DROP TABLE IF EXISTS TAMANAGERMG;

DROP TABLE IF EXISTS TASHAREPOSITION;

DROP TABLE IF EXISTS TAMENU;

DROP TABLE IF EXISTS TAORG;

DROP TABLE IF EXISTS TAORGOPLOG;

DROP TABLE IF EXISTS TAPOSITION;

DROP TABLE IF EXISTS TAPOSITIONAUTHRITY;

DROP TABLE IF EXISTS TAUSER;

DROP TABLE IF EXISTS TAUSERPOSITION;

DROP TABLE IF EXISTS TAACCESSLOG;

DROP TABLE IF EXISTS TAMENUPOSITIONYAB003;

DROP TABLE IF EXISTS TAYAB003SCOPE;

DROP TABLE IF EXISTS SERVERADDRESS;

DROP TABLE IF EXISTS TACOMMONMENU;

DROP TABLE IF EXISTS TACONSOLEMODULE;

DROP TABLE IF EXISTS TACONSOLEMODULELOCATION;

DROP TABLE IF EXISTS TACONSOLEMODULEPRIVILEGE;

DROP TABLE IF EXISTS TAONLINELOG;

DROP TABLE IF EXISTS TALOGINHISTORYLOG;

DROP TABLE IF EXISTS TASERVEREXCEPTIONLOG;


/*==============================================================*/
/* Table: TASERVEREXCEPTIONLOG                                                 */
/*==============================================================*/
CREATE TABLE TASERVEREXCEPTIONLOG
(
   ID                   VARCHAR(32) NOT NULL COMMENT 'id',
   IPADDRESS            VARCHAR(255) COMMENT '服务器ip地址',
   TYPE                 VARCHAR(255) COMMENT '异常类型',
   CONTENT              BLOB COMMENT '异常内容',
   TIME                 DATETIME COMMENT '报错时间',
   SYSPATH 		VARCHAR(20) COMMENT '系统路径',
   CLIENTIP 		VARCHAR(50) COMMENT '客户端ip',
   URL 			VARCHAR(100) COMMENT '访问功能url',
   MENUID 		VARCHAR(8) COMMENT '菜单id',
   MENUNAME 		VARCHAR(30) COMMENT '菜单名称',
   USERAGENT 		VARCHAR(200) COMMENT '客户端环境'

);

ALTER TABLE TASERVEREXCEPTIONLOG COMMENT '系统异常日志表';

ALTER TABLE TASERVEREXCEPTIONLOG ADD PRIMARY KEY (ID);


/*==============================================================*/
/* Table: AA10                                                  */
/*==============================================================*/
CREATE TABLE AA10
(
   AAA100               VARCHAR(20) NOT NULL COMMENT 'AAA100代码类别',
   AAA101               VARCHAR(50) NOT NULL COMMENT 'AAA101类别名称',
   AAA102               VARCHAR(6) NOT NULL COMMENT 'AAA102代码值',
   AAA103               VARCHAR(50) NOT NULL COMMENT 'AAA103代码名称',
   YAB003               VARCHAR(4) NOT NULL COMMENT 'YAB003经办机构',
   AAE120               VARCHAR(6) NOT NULL COMMENT 'AAE120注销标志',
   VER                  NUMERIC(10,0) COMMENT 'VER', PRIMARY KEY (AAA100, AAA102)
);
ALTER TABLE AA10 COMMENT 'AA10代码表';

/*==============================================================*/
/* Table: TACONFIG                                                  */
/*==============================================================*/
CREATE TABLE TACONFIG (
  CONFIGID INT(11) NOT NULL AUTO_INCREMENT COMMENT '配置项ID',
  CONFIGNAME VARCHAR(100) NOT NULL COMMENT '配置项名称',
  CONFIGVALUE VARCHAR(1024) COMMENT '配置项内容',
  CONFIGTYPE VARCHAR(1) COMMENT '是否系统参数',
  CONFIGFLAG VARCHAR(20) NOT NULL COMMENT '系统标识',
  CONFIGDESC VARCHAR(200) COMMENT '参数说明',
  PRIMARY KEY (CONFIGID)
);

ALTER TABLE TACONFIG COMMENT '系统配置表';
/*==============================================================*/
/* Table: TACONFIGSYSPATH                                                */
/*==============================================================*/
CREATE TABLE TACONFIGSYSPATH (
  SERIALID INT(11) NOT NULL AUTO_INCREMENT COMMENT '流水号',
  ID VARCHAR(20) NOT NULL COMMENT '系统ID',
  NAME VARCHAR(50) NOT NULL COMMENT '系统名称',
  URL VARCHAR(100) NOT NULL COMMENT '系统路径前缀',
  PY VARCHAR(20) DEFAULT NULL COMMENT '拼音',
  CURSYSTEM VARCHAR(1) NOT NULL COMMENT '是否为当前系统',
  PRIMARY KEY (SERIALID)
);

ALTER TABLE TACONFIGSYSPATH COMMENT '系统路径配置表';

/*==============================================================*/
/* Table: TAMENU                                                */
/*==============================================================*/
CREATE TABLE TAMENU
(
   MENUID               INT(10) NOT NULL AUTO_INCREMENT COMMENT '菜单Id',
   PMENUID              INT(10) NOT NULL COMMENT '父菜单id',
   MENUNAME             VARCHAR(60) COMMENT '功能名称',
   URL                  VARCHAR(100) COMMENT '功能地址',
   MENUIDPATH           VARCHAR(1024) COMMENT '菜单id路径',
   MENUNAMEPATH         VARCHAR(1024) COMMENT '菜单名称路径',
   ICONSKIN             VARCHAR(200) COMMENT '选择前图片',
   SELECTIMAGE          VARCHAR(200) COMMENT '选择后图片',
   REPORTID             VARCHAR(50) COMMENT '查询报表的报表id',
   ACCESSTIMEEL         VARCHAR(200) COMMENT '访问限制时间表达式',
   EFFECTIVE            CHAR(1) NOT NULL COMMENT '有效标志',
   SECURITYPOLICY       CHAR(1) NOT NULL COMMENT '安全策略',
   ISDISMULTIPOS        CHAR(1) NOT NULL COMMENT '是否显示多岗',
   QUICKCODE            VARCHAR(20) COMMENT '快捷访问码',
   SORTNO               INT COMMENT '排序号',
   RESOURCETYPE         CHAR(2) NOT NULL COMMENT '权限类型（功能权限\按钮权限\表单只读\表单可编辑\表格列只读\表格列可编辑）',
   MENULEVEL            INT,
   ISLEAF               CHAR(1),
   MENUTYPE             CHAR(1),
   ISCACHE              CHAR(1), 
   SYSPATH                 VARCHAR(20) COMMENT '系统路径标识',
   USEYAB003				CHAR(1) COMMENT '是否启用分中心数据权限',
   TYPEFLAG                 INT COMMENT '类标识',
   ISAUDITE 		VARCHAR(1) DEFAULT 1 COMMENT '是否需要审核',
   CONSOLEMODULE 	CHAR(1) DEFAULT 1 COMMENT '是否为工作台模块',
   PRIMARY KEY (MENUID)
);

ALTER TABLE TAMENU COMMENT '功能菜单';

/*==============================================================*/
/* Table: TAORG                                                 */
/*==============================================================*/
CREATE TABLE TAORG
(
   ORGID                INT(10) NOT NULL AUTO_INCREMENT COMMENT '组织id',
   PORGID               INT(10) COMMENT '组织id',
   ORGNAME              VARCHAR(60) NOT NULL COMMENT '名称',
   COSTOMNO             VARCHAR(10) COMMENT '自定义编码',
   ORGIDPATH            VARCHAR(1024) COMMENT '组织id路径',
   ORGNAMEPATH          VARCHAR(1024) COMMENT '组织名称路径',
   COSTOMNOPATH         VARCHAR(1024) COMMENT '自定义编码路径',
   ORGTYPE              CHAR(2) COMMENT '组织类型（机构（集团、子公司）、部门、组）',
   SORT                 INT COMMENT '排序号',
   YAB003               VARCHAR(6) COMMENT '经办机构',
   DIMENSION            CHAR(2) COMMENT '视图维度',
   CREATEUSER           INT(10) NOT NULL COMMENT '创建人',
   CREATETIME           DATETIME NOT NULL COMMENT '创建时间',
   EFFECTIVE            CHAR(1) NOT NULL,
   ORGLEVEL             INT(10),
   ISLEAF               CHAR(1), 
   ORGMANAGER           INT(10) COMMENT '组织负责人（正职）',
   DESTORY              CHAR(1) COMMENT '是否销毁',
   TYPEFLAG             INT COMMENT '类标识',
   YAB139 		VARCHAR(6) COMMENT '数据区',
   PRIMARY KEY (ORGID)
);

ALTER TABLE TAORG COMMENT '组织视图';

/*==============================================================*/
/* Table: TAORGOPLOG                                            */
/*==============================================================*/
CREATE TABLE TAORGOPLOG
(
   LOGID                INT(10) NOT NULL AUTO_INCREMENT COMMENT '日志id',
   BATCHNO              VARCHAR(10) COMMENT '操作批次号',
   OPTYPE               CHAR(2) COMMENT '操作类型（新增、编辑、删除、授权、回收权限）',
   INFLUENCEBODYTYPE    CHAR(2) COMMENT '影响主体类型（组织、人员、岗位、角色、权限资源）',
   INFLUENCEBODY        VARCHAR(10) COMMENT '影响主体',
   OPBODY               CHAR(2) COMMENT '操作主体类型（人员、组织、岗位、角色、权限资源）',
   OPSUBJEKT            VARCHAR(10) COMMENT '操作主体',
   CHANGCONTENT         VARCHAR(2048) COMMENT '主体变更内容',
   OPTIME               DATETIME NOT NULL COMMENT '经办时间',
   OPUSER               VARCHAR(10) NOT NULL COMMENT '经办人',
   OPPOSITION           VARCHAR(10) NOT NULL COMMENT '经办岗位', 
   ISPERMISSION 	VARCHAR(1) COMMENT '是否有权限',
   PRIMARY KEY (LOGID)
);

ALTER TABLE TAORGOPLOG COMMENT '组织及权限操作日志';

/*==============================================================*/
/* Table: TAPOSITION                                            */
/*==============================================================*/
CREATE TABLE TAPOSITION
(
   POSITIONID           INT(10) NOT NULL AUTO_INCREMENT COMMENT '岗位id',
   ORGID                INT(10) NOT NULL COMMENT '组织id',
   POSITIONNAME         VARCHAR(60) NOT NULL COMMENT '岗位名称',
   POSITIONTYPE         CHAR(1) NOT NULL COMMENT '岗位类型（个人专属岗位/公有岗位）',
   CREATEPOSITIONID     INT(10) NOT NULL COMMENT '创建人使用的岗位',
   ORGIDPATH            VARCHAR(1024),
   ORGNAMEPATH          VARCHAR(1024),
   VALIDTIME            DATETIME COMMENT '只真对委派生成的岗位',
   CREATEUSER           INT(10) NOT NULL COMMENT '创建人',
   CREATETIME           DATETIME NOT NULL COMMENT '创建时间',
   EFFECTIVE            CHAR(1) NOT NULL COMMENT '有效标志',
   ISADMIN              CHAR(1) COMMENT '是否为管理员',
   ISSHARE               CHAR(1) COMMENT '是否为共享岗位',
   ISCOPY                  CHAR(1) COMMENT '是否为复制岗位',
   TYPEFLAG                 INT COMMENT '类标识',
   POSITIONCATEGORY 	VARCHAR(2) COMMENT '岗位类别',
   PRIMARY KEY (POSITIONID)
);

ALTER TABLE TAPOSITION COMMENT '岗位';

/*==============================================================*/
/* Table: TAPOSITIONAUTHRITY                                    */
/*==============================================================*/
CREATE TABLE TAPOSITIONAUTHRITY
(
   POSITIONID           INT(10) NOT NULL COMMENT '岗位id',
   MENUID               INT(10) NOT NULL COMMENT '菜单Id',
   USEPERMISSION        CHAR(1) COMMENT '使用权限',
   REPERMISSION         CHAR(1) COMMENT '授权别人使用权限',
   REAUTHRITY           CHAR(1) COMMENT '授权别人授权权限',
   CREATEUSER           INT(10) NOT NULL COMMENT '创建人',
   CREATETIME           DATETIME NOT NULL COMMENT '创建时间',
   EFFECTTIME           DATETIME COMMENT '有效时间', 
   AUDITEACCESSDATE DATE COMMENT '审核通过时间',
   AUDITEUSER INT(10) COMMENT '审核人',
   AUDITSTATE VARCHAR(1) DEFAULT 0 COMMENT '审核状态',
   PRIMARY KEY (POSITIONID, MENUID)
);

ALTER TABLE TAPOSITIONAUTHRITY COMMENT '岗位权限表';

/*==============================================================*/
/* Table: TAUSER                                                */
/*==============================================================*/
CREATE TABLE TAUSER
(
   USERID               INT(10) NOT NULL AUTO_INCREMENT COMMENT '人员id',
   NAME                 VARCHAR(50) NOT NULL COMMENT '姓名',
   SEX                  CHAR(1) COMMENT '性别',
   LOGINID              VARCHAR(20) NOT NULL COMMENT '登陆账号',
   PASSWORD             VARCHAR(50) NOT NULL COMMENT '密码',
   PASSWORDFAULTNUM     INT COMMENT '口令错误次数',
   PWDLASTMODIFYDATE    DATETIME COMMENT '口令最后修改时间',
   ISLOCK               CHAR(1) COMMENT '锁定标志',
   SORT                 INT COMMENT '排序号',
   EFFECTIVE            CHAR(1) NOT NULL COMMENT '有效标志',
   TEL                  VARCHAR(15) COMMENT '联系电话',
   CREATEUSER           INT(10) NOT NULL COMMENT '创建人',
   CREATETIME           DATETIME NOT NULL COMMENT '创建时间', 
   DIRECTORGID          INT(10) NOT NULL COMMENT '直属组织',
   DESTORY				CHAR(1) COMMENT '是否销毁',
   TYPEFLAG          INT(10) COMMENT '类标识',
   PRIMARY KEY (USERID)
);

ALTER TABLE TAUSER COMMENT '人员';

/*==============================================================*/
/* Table: TAUSERPOSITION                                        */
/*==============================================================*/
CREATE TABLE TAUSERPOSITION
(
   USERID               INT(10) NOT NULL COMMENT '人员id',
   POSITIONID           INT(10) NOT NULL COMMENT '岗位id',
   MAINPOSITION         CHAR(1) NOT NULL COMMENT '默认岗位',
   CREATEUSER           INT(10) NOT NULL COMMENT '创建人',
   CREATETIME           DATETIME NOT NULL COMMENT '创建时间', PRIMARY KEY (USERID, POSITIONID)
);
/*==============================================================*/
/* Table: TAORGMG                                               */
/*==============================================================*/
CREATE TABLE TAORGMG
(
   POSITIONID           INT(10) COMMENT '岗位id',
   ORGID                INT(10) COMMENT '组织id'
);
ALTER TABLE TAORGMG COMMENT '部门管理表';
/*==============================================================*/
/* Table: TAMANAGERMG                                               */
/*==============================================================*/
CREATE TABLE TAMANAGERMG
(
   POSITIONID           INT(10) COMMENT '岗位id',
   ORGID                INT(10) COMMENT '组织id'
);
ALTER TABLE TAMANAGERMG COMMENT '组织负责人（副职）管理表';

/*==============================================================*/
/* Table: TASHAREPOSITION                                               */
/*==============================================================*/
CREATE TABLE TASHAREPOSITION
(
   SPOSITIONID           INT(10) COMMENT '源岗位id',
   DPOSITIONID           INT(10) COMMENT '复制后岗位id'
);
ALTER TABLE TASHAREPOSITION COMMENT '共享岗位表';

/*==============================================================*/
/* Table: TAACCESSLOG                                              */
/*==============================================================*/
CREATE TABLE TAACCESSLOG (
  LOGID INT(15) NOT NULL AUTO_INCREMENT COMMENT '日志id',
  USERID INT(10) NOT NULL COMMENT '用户id',
  POSITIONID INT(10) NOT NULL COMMENT '岗位id',
  PERMISSIONID INT(10) NOT NULL COMMENT '功能id',
  ISPERMISSION CHAR(1) NOT NULL COMMENT '是否有权限',
  ACCESSTIME DATETIME NOT NULL COMMENT '访问时间',
  URL VARCHAR(1024) COMMENT '访问路径',
  SYSFLAG VARCHAR(50) COMMENT '系统标识',
  PRIMARY KEY (LOGID)
);
ALTER TABLE TAACCESSLOG COMMENT '功能日志表';

/*==============================================================*/
/* Table: TAMENUPOSITIONYAB003                                              */
/*==============================================================*/
CREATE TABLE TAMENUPOSITIONYAB003 (
  MENUID INT(11) NOT NULL COMMENT '菜单id',
  POSITIONID INT(11) NOT NULL COMMENT '岗位id',
  YAB003 VARCHAR(6) NOT NULL COMMENT '分中心',
  PRIMARY KEY (MENUID,POSITIONID,YAB003)
);

ALTER TABLE TAMENUPOSITIONYAB003 COMMENT '功能数据权限';


/*==============================================================*/
/* Table: TAYAB003SCOPE                                              */
/*==============================================================*/
CREATE TABLE TAYAB003SCOPE (
  YAB003 VARCHAR(6) NOT NULL COMMENT '分中心',
  YAB139 VARCHAR(6) NOT NULL COMMENT '分中心数据权限',
  PRIMARY KEY (YAB003,YAB139)
) ;

ALTER TABLE TAYAB003SCOPE COMMENT '分中心数据权限范围表';

/*==============================================================*/
/* Table: SERVERADDRESS                                               */
/*==============================================================*/
CREATE TABLE SERVERADDRESS (
	ADDRESS VARCHAR (200) NOT NULL COMMENT '应用地址',
	CANUSE VARCHAR (1) COMMENT '是否启用',
	PRIMARY KEY (ADDRESS)
);
ALTER TABLE SERVERADDRESS COMMENT '集群各个server的应用地址';

/*==============================================================*/
/* Table: TACOMMONMENU                                               */
/*==============================================================*/
CREATE TABLE TACOMMONMENU (
  USERID INT(11) NOT NULL COMMENT '用户id',
  MENUID INT(11) NOT NULL COMMENT '菜单id',
  PRIMARY KEY (USERID,MENUID)
) ;

ALTER TABLE TACOMMONMENU COMMENT '常用菜单';

/*==============================================================*/
/* Table: TACONSOLEMODULE                                               */
/*==============================================================*/
CREATE TABLE TACONSOLEMODULE
(
  MODULE_ID      INT(10) not null AUTO_INCREMENT COMMENT '模块编号',
  MODULE_NAME    VARCHAR(100) not null COMMENT '模块名称',
  MODULE_URL     VARCHAR(200) not null COMMENT '模块链接',
  MODULE_STA     VARCHAR(1) default 1 not null COMMENT '模块有效标识',
  MODULE_DEFAULT VARCHAR(1) default 1 COMMENT '是否默认显示',
  MODULE_HEIGHT  VARCHAR(10) default 200 COMMENT '模块默认高度',PRIMARY KEY (MODULE_ID)
);
ALTER TABLE TACONSOLEMODULE COMMENT 'ECADMIN工作台自定义组件';
  
/*==============================================================*/
/* Table: TACONSOLEMODULELOCATION                                               */
/*==============================================================*/
CREATE TABLE TACONSOLEMODULELOCATION
(
  MARK       VARCHAR(20) NOT NULL COMMENT '页面标识',
  POSITIONID INT(10) NOT NULL COMMENT '岗位ID',
  LOCATION   VARCHAR(1000) NOT NULL COMMENT '位置信息数据'
);
ALTER TABLE TACONSOLEMODULELOCATION COMMENT 'ECADMIN工作台自定义组件位置信息';
  
/*==============================================================*/
/* Table: TACONSOLEMODULEPRIVILEGE                                               */
/*==============================================================*/
CREATE TABLE TACONSOLEMODULEPRIVILEGE
(
  POSITIONID INT(10) NOT NULL COMMENT '角色编号',
  MODULEID   INT(10) NOT NULL COMMENT '模块编号',PRIMARY KEY (POSITIONID, MODULEID)
);
 ALTER TABLE TACONSOLEMODULEPRIVILEGE COMMENT 'CRM工作台自定义组件权限信息';
 
 /*==============================================================*/
/* Table: TAONLINELOG                                               */
/*==============================================================*/
 CREATE TABLE TAONLINELOG (
  LOGID int(15) NOT NULL AUTO_INCREMENT,
  USERID int(10) DEFAULT NULL,
  LOGINTIME timestamp NULL DEFAULT NULL,
  curresource VARCHAR(1000),
  clientip   VARCHAR(200) not null,
  sessionid  VARCHAR(200) not null,
  serverip   VARCHAR(200),
  syspath    VARCHAR(50),
  PRIMARY KEY (LOGID)
);

 /*==============================================================*/
/* Table: TALOGINHISTORYLOG                                               */
/*==============================================================*/
CREATE TABLE TALOGINHISTORYLOG (
  LOGID int(15) NOT NULL AUTO_INCREMENT,
  USERID int(10) DEFAULT NULL,
  LOGINTIME timestamp NULL DEFAULT NULL,
  LOGOUTTIME timestamp NULL DEFAULT NULL,
  clientip   VARCHAR(200) not null,
  sessionid  VARCHAR(200) not null,
  serverip   VARCHAR(200),
  syspath    VARCHAR(50),
  PRIMARY KEY (LOGID)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS seq;
CREATE TABLE seq (
  name varchar(20) NOT NULL DEFAULT '',
  val bigint(20) unsigned NOT NULL,
  PRIMARY KEY (name)
);

DROP TABLE IF EXISTS TALOCALCACHEVERSION;
/*==============================================================*/
/* Table: TALOCALCACHEVERSION                                   */
/*==============================================================*/
CREATE TABLE TALOCALCACHEVERSION
(
   VERSION		INT(11) NOT NULL COMMENT '版本号',
   CODETYPE		VARCHAR(20) COMMENT '改动的type'
);

ALTER TABLE TALOCALCACHEVERSION COMMENT '本地缓存码表版本号';
 
insert into TALOCALCACHEVERSION(VERSION, CODETYPE)
values (1,'');

DROP TABLE IF EXISTS TADATAACCESSDIMENSION;
/*==============================================================*/
/* Table: TADATAACCESSDIMENSION                                                  */
/*==============================================================*/
CREATE TABLE TADATAACCESSDIMENSION (
  DIMENSIONID INT(11) NOT NULL AUTO_INCREMENT COMMENT '维度ID',
  POSITIONID INT(11) NOT NULL COMMENT '岗位ID',
  MENUID INT(11) NOT NULL COMMENT '菜单ID',
  DIMENSIONTYPE VARCHAR(20) NOT NULL COMMENT '维度类型',
  DIMENSIONPERMISSIONID VARCHAR(20) DEFAULT NULL COMMENT '维度权限ID',
  ALLACCESS VARCHAR(1) DEFAULT NULL COMMENT '是否具有该维度所有权限',
  SYSPATH VARCHAR(50) COMMENT '系统标识',
  PRIMARY KEY (DIMENSIONID)
);
ALTER TABLE TADATAACCESSDIMENSION COMMENT '维度数据权限表';

DROP TABLE IF EXISTS TAADMINYAB139SCOPE;
CREATE TABLE TAADMINYAB139SCOPE (
  POSITIONID INT(11) NOT NULL COMMENT '岗位id',
  YAB139 VARCHAR(6) NOT NULL COMMENT '数据区',
  PRIMARY KEY (POSITIONID,YAB139)
);
ALTER TABLE TAADMINYAB139SCOPE COMMENT '管理员数据区管理范围';

DROP TABLE IF EXISTS TAYAB139MG;
CREATE TABLE TAYAB139MG
(
  YAB003 VARCHAR(6) NOT NULL COMMENT '经办机构',
  YAB139 VARCHAR(20) NOT NULL COMMENT '数据区'
);
ALTER TABLE TAYAB139MG COMMENT '经办机构管理数据区范围';
  
DROP TABLE IF EXISTS TAYAB003LEVELMG;
CREATE TABLE TAYAB003LEVELMG
(
  PYAB003 VARCHAR(6) NOT NULL COMMENT '父经办机构',
  YAB003  VARCHAR(6)  NOT NULL COMMENT '经办机构'
);
ALTER TABLE TAYAB003LEVELMG COMMENT '经办机构层级关系管理';


DROP TABLE IF EXISTS  TARUNQIANRESOURCE ;

DROP TABLE IF EXISTS TARUNQIANPRINTSETUP ;

DROP TABLE IF EXISTS TARUNQIANAD52REFERENCE ;

create table TARUNQIANRESOURCE
(
  raqfilename       VARCHAR(200) not null COMMENT '文件名/报表标识（RaqfileName）',
  parentraqfilename VARCHAR(200) COMMENT '父报表标识（ParentRaqfileName）',
  raqname           VARCHAR(200) COMMENT '报表名称（RaqName）',
  raqtype           VARCHAR(6) COMMENT '报表类型（RaqType）',
  raqfile           BLOB COMMENT '资源文件（RaqFile）',
  uploador          VARCHAR(19) COMMENT '上传人（Uploador）',
  uploadtime        DATE COMMENT '上传时间（UploadTime）',
  subrow            INT COMMENT '父报表位置行（SubRow）',
  subcell           INT COMMENT '父报表位置列（SubCell）',
  raqdatasource     VARCHAR(19) COMMENT '数据源（RaqDataSource）',
  raqparam          VARCHAR(500) COMMENT '报表参数JSON格式Str（RaqParam）',
  orgid             VARCHAR(15) COMMENT '部门编号(OrgId)',
  primary key (RAQFILENAME)
);
ALTER TABLE TARUNQIANRESOURCE COMMENT '润乾报表模板';


alter table TARUNQIANRESOURCE
  add constraint FK_YHCIP_RU_REFERENCE_YHCIP_RU foreign key (PARENTRAQFILENAME)
  references TARUNQIANRESOURCE (RAQFILENAME);


create table TARUNQIANPRINTSETUP
(
  setupid    VARCHAR(200) not null COMMENT '打印设置编号（SetupId）' ,
  setupvalue VARCHAR(400) not null COMMENT  '打印设置信息（SetupValue）',
  primary key (SETUPID)
);
ALTER TABLE TARUNQIANRESOURCE COMMENT '打印设置信息表';

create table TARUNQIANAD52REFERENCE
(
  id                  INT(10) not null COMMENT '主键ID',
  menuid              INT(10) COMMENT '功能编号',
  raqfilename         VARCHAR(200) COMMENT '文件名/报表标识（RaqfileName）',
  limited               INT COMMENT '每页显示数（Limited）',
  scaleexp            INT COMMENT 'JSP中缩放比率（ScaleExp）',
  isgroup             VARCHAR(6) COMMENT '是否按行分页（IsGroup）',
  needsaveasexcel     VARCHAR(6) COMMENT '是否保存为Excel（NeedSaveAsExcel）',
  needsaveasexcel2007 VARCHAR(6) COMMENT '是否保存为Excel2007（NeedSaveAsExcel2007）',
  needsaveaspdf       VARCHAR(6) COMMENT '是否保存为Pdf（NeedSaveAsPdf）',
  needsaveasword      VARCHAR(6) COMMENT '是否保存为Word（NeedSaveAsWord）',
  needsaveastext      VARCHAR(6) COMMENT '是否保存为Text（NeedSaveAsText）',
  needprint           VARCHAR(6) COMMENT '是否保存为Print（NeedPrint）',
  primary key (ID)
);
ALTER TABLE TARUNQIANRESOURCE COMMENT 'YHCIP_RUNQIAN_AD52_REFERENCE润乾报表菜单信息';
alter table TARUNQIANAD52REFERENCE
  add constraint FK_REPORT_INFO foreign key (RAQFILENAME)
  references TARUNQIANRESOURCE (RAQFILENAME);


INSERT INTO seq (name,val) VALUES  ('SEQ_DEFAULT',1000000000);

DROP FUNCTION IF EXISTS seq;

DELIMITER $$

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE FUNCTION  seq(seq_name varchar(20)) RETURNS bigint(20)
BEGIN
 UPDATE seq SET val = LAST_INSERT_ID(val+1) WHERE name = seq_name;
 RETURN LAST_INSERT_ID();
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;

insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('MENUTYPE', '菜单类型', '0', '通用菜单', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('MENUTYPE', '菜单类型', '1', '系统管理菜单', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('MENUTYPE', '菜单类型', '2', '业务功能菜单', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('ORGTYPE', '组织类型', '04', '组', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POSITIONTYPE', '岗位类型', '1', '公有岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POSITIONTYPE', '岗位类型', '2', '个人岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POSITIONTYPE', '岗位类型', '3', '委派岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('SEX', '性别', '1', '男', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('SEX', '性别', '2', '女', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('SEX', '性别', '0', '无', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('ORGTYPE', '组织类型', '01', '机构', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('ORGTYPE', '组织类型', '02', '部门', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('EFFECTIVE', '有效标志', '0', '有效', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('EFFECTIVE', '有效标志', '1', '无效', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('YESORNO', '是否', '0', '是', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('YESORNO', '是否', '1', '否', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POLICY', '安全策略', '1', '要认证要显示', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POLICY', '安全策略', '2', '要认证不显示', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POLICY', '安全策略', '3', '不认证不显示', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('POLICY', '安全策略', '4', '不认证要显示', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '01', '新增组织', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '02', '编辑组织', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '03', '禁用组织', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '04', '新增人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '05', '编辑人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '06', '禁用人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '07', '密码重置', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '08', '赋予岗位给人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '09', '设置主岗位给人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '10', '新增岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '11', '编辑岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '12', '禁用岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '13', '赋予岗位使用权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '14', '回收岗位使用权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '15', '启用岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '16', '启用人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '17', '启用组织', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '18', '删除组织', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '19', '赋予岗位授权别人使用权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '20', '回收岗位授权别人使用权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '21', '赋予岗位授权别人授权权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '22', '回收岗位授权别人授权权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '23', '删除管理员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '24', '新增管理员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '25', '删除岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPTYPE', '操作类型', '26', '删除人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPOBJTYPE', '操作对象类型', '01', '组织', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPOBJTYPE', '操作对象类型', '02', '人员', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPOBJTYPE', '操作对象类型', '03', '岗位', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('OPOBJTYPE', '操作对象类型', '04', '权限', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('YAB003', '分中心', '9999', '9999', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('POSITIONCATEGORY', '岗位类别', '01', '业务岗', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('POSITIONCATEGORY', '岗位类别', '02', '稽核岗', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '0', '无需审核', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '1', '待审核', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '2', '审核通过', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '3', '审核未通过', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('RAQTYPE', '报表类型', '0', '参数报表', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('RAQTYPE', '报表类型', '1', '主报表', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('RAQTYPE', '报表类型', '2', '子报表', '9999', '0', 0);

commit;

INSERT INTO TACONFIGSYSPATH (SERIALID, ID, NAME, URL, PY, CURSYSTEM) values('1','sysmg','系统管理','http://127.0.0.1:8080/ta3/','xtgl','0');
commit;
/*
-- Query: SELECT * FROM ta4.tamenu
LIMIT 0, 1000

-- Date: 2013-10-14 15:44
*/
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,USEYAB003,ISAUDITE,TYPEFLAG) 
VALUES (1,0,'银海软件',NULL,'1','银海软件','tree-organisation',NULL,NULL,NULL,'0','1','0',NULL,'0','01','1','1','0',NULL,'sysmg',1,1,0);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (2, 1, '权限管理', '', '1/2', '银海软件/权限管理', 'tree-organisation', '', '', '', '0', '1', '1', '', 0, '01', 2, '1', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (3, 1, '开发管理', '', '1/3', '银海软件/开发管理', 'tree-bug', '', '', '', '0', '1', '1', '', 1, '01', 2, '1', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (4, 1, '系统管理', '', '1/4', '银海软件/系统管理', 'tree-apply', '', '', '', '0', '1', '0', '', 2, '01', 2, '1', '1', '', 'sysmg', '', 0, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (5, 1, '工作台示例widget', '', '1/5', '银海软件/工作台示例widget', 'tree-duty', '', '', '', '0', '2', '1', '', 3, '01', 2, '1', '2', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (6, 2, '组织人员管理', 'org/orgUserMgAction.do', '1/2/6', '银海软件/权限管理/组织人员管理', 'tree-bookmark-new', '', '', '', '0', '1', '1', '', 0, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (7, 2, '管理员权限管理', 'org/admin/adminUserMgAction.do', '1/2/7', '银海软件/权限管理/管理员权限管理', 'tree-groups', '', '', '', '0', '1', '1', '', 1, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (8, 2, '经办用户权限管理', 'org/position/positionUserMgAction.do', '1/2/8', '银海软件/权限管理/经办用户权限管理', 'tree-userm', '', '', '', '0', '1', '1', '', 2, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (9, 2, '相似权限授权', 'org/position/similarAuthorityAction.do', '1/2/9', '银海软件/权限管理/相似权限授权', 'tree-car', '', '', '', '0', '1', '1', '', 3, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (10, 2, '功能权限代理', 'org/position/delegatePositionAction.do', '1/2/10', '银海软件/权限管理/功能权限代理', 'tree-media-floppy', '', '', '', '0', '1', '1', '', 4, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (11, 3, '菜单管理', 'sysapp/menuMgAction.do', '1/3/11', '银海软件/开发管理/菜单管理', 'tree-image', '', '', '', '0', '1', '1', '', 0, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (12, 3, '码表管理', 'sysapp/appCodeMainAction.do', '1/3/12', '银海软件/开发管理/码表管理', 'tree-database', '', '', '', '0', '1', '1', '', 1, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (13, 3, '集群server地址配置', 'sysapp/serverAddressAction.do', '1/3/13', '银海软件/开发管理/集群server地址配置', 'tree-folder-saved-search', '', '', '', '0', '1', '1', '', 2, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (14, 3, '批量导入组织人员', 'org/upload/uploadOrgUserAction.do', '1/3/14', '银海软件/开发管理/批量导入组织人员', 'tree-document-properties', '', '', '', '0', '1', '1', '', 3, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (15, 4, 'oracle定时服务', 'system/oracleJobAction.do', '1/4/15', '银海软件/系统管理/oracle定时服务', 'tree-office-spreadsheet', '', '', '', '0', '1', '1', '', 0, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (16, 4, '润乾报表管理', '', '1/4/16', '银海软件/系统管理/润乾报表管理', 'tree-menu', '', '', '', '0', '1', '1', '', 1, '01', 3, '1', '1', '', 'sysmg', '', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (17, 4, '系统路径管理', 'sysapp/configSysPathAction.do', '1/4/17', '银海软件/系统管理/系统路径管理', 'tree-plugin', '', '', '', '0', '1', '1', '', 2, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (18, 4, '系统异常日志', 'sysapp/serverExeceptionLogAction.do', '1/4/18', '银海软件/系统管理/系统异常日志', 'tree-document-new', '', '', '', '0', '1', '1', '', 3, '01', 3, '0', '1', '', 'sysmg', '', 0, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (19, 16, '润乾报表菜单管理', 'runqian/queryReportMgAction.do', '1/2/16/19', '银海软件/系统管理/润乾报表管理/润乾报表菜单管理', 'tree-menu', '', '', '', '0', '1', '1', '', 1, '01', 4, '0', '1', '', 'sysmg', '', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (20, 16, '润乾报表模板管理', 'runqian/runQianReportFileMgAction.do', '1/2/16/20', '银海软件/系统管理/润乾报表管理/润乾报表模板管理', 'tree-menu', '', '', '', '0', '1', '1', '', 0, '01', 4, '0', '1', '', 'sysmg', '', null, '1', '1');
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, ISAUDITE, CONSOLEMODULE, TYPEFLAG)
VALUES (21, 5, '待办任务', 'sysapp/console/daiban.html', '1/5/21', '银海软件/工作台示例/待办任务', 'tree-phone', '', '', '', '0', '3', '1', '', 0, '01', 3, '0', '2', '', 'sysmg', '1', '1', '0', null);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, ISAUDITE, CONSOLEMODULE, TYPEFLAG)
VALUES (22, 5, '系统', 'sysapp/console/system.html', '1/5/22', '银海软件/工作台示例/系统', 'tree-apply', '', '', '', '0', '3', '1', '', 1, '01', 3, '0', '2', '', 'sysmg', '1', '1', '0', null);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, ISAUDITE, CONSOLEMODULE, TYPEFLAG)
VALUES (23, 5, '通知', 'sysapp/console/tongzhi.html', '1/5/23', '银海软件/工作台示例/通知', 'tree-letter', '', '', '', '0', '3', '1', '', 2, '01', 3, '0', '2', '', 'sysmg', '1', '1', '0', null);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, ISAUDITE, CONSOLEMODULE, TYPEFLAG)
VALUES (24, 5, '协同管理', 'sysapp/console/xietong.html', '1/5/24', '银海软件/工作台示例/协同管理', 'tree-emblem-system', '', '', '', '0', '3', '1', '', 3, '01', 3, '0', '2', '', 'sysmg', '1', '1', '0', null);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, ISAUDITE, CONSOLEMODULE, TYPEFLAG)
VALUES (25, 5, '工作量统计', 'sysapp/console/yewuliang.html', '1/5/25', '银海软件/工作台示例/工作量统计', 'tree-destop', '', '', '', '0', '3', '1', '', 4, '01', 3, '0', '2', '', 'sysmg', '1', '1', '0', null);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, ISAUDITE, CONSOLEMODULE, TYPEFLAG)
VALUES (26, 5, '仪表盘', 'sysapp/console/yibiaopan.html', '1/5/26', '银海软件/工作台示例/仪表盘', 'tree-destop', '', '', '', '0', '3', '1', '', 5, '01', 3, '0', '2', '', 'sysmg', '1', '1', '0', null);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE, CONSOLEMODULE)
VALUES (27, 2, '经办机构配置', 'org/agencies/agenciesMgAction.do', '1/2/27', '银海软件/权限管理/经办机构配置', 'tree-bug', '', '', '', '0', '1', '1', '', 5, '01', 3, '0', '1', '', 'sysmg', '1', null, '1', '1');

INSERT INTO TACONSOLEMODULELOCATION (MARK, POSITIONID, LOCATION)
VALUES ('index', 1, '{"columnsWidth":"0.370242214532872:0.3044982698961938:0.32525951557093424","columnsData":[{"id":"21","index":0,"height":229,"columnindex":2},{"id":"22","index":1,"height":229,"columnindex":0},{"id":"23","index":0,"height":229,"columnindex":1},{"id":"24","index":2,"height":229,"columnindex":0},{"id":"25","index":1,"height":229,"columnindex":2},{"id":"26","index":2,"height":229,"columnindex":1}]}');

commit;
/*
-- Query: SELECT * FROM ta4.taorg
LIMIT 0, 1000

-- Date: 2013-10-14 15:49
*/
INSERT INTO TAORG (ORGID,PORGID,ORGNAME,COSTOMNO,ORGIDPATH,ORGNAMEPATH,COSTOMNOPATH,ORGTYPE,SORT,YAB003,DIMENSION,CREATEUSER,CREATETIME,EFFECTIVE,ORGLEVEL,ISLEAF,TYPEFLAG) VALUES (1,1,'银海软件','1','1','银海软件','0','01',0,'9999',NULL,0,now(),'0',0,'0',0);
commit;
/*
-- Query: SELECT * FROM ta4.TAUSER
LIMIT 0, 1000
-- Date: 2013-10-14 15:52
*/
INSERT INTO TAUSER (USERID,NAME,SEX,LOGINID,PASSWORD,PASSWORDFAULTNUM,PWDLASTMODIFYDATE,ISLOCK,SORT,EFFECTIVE,TEL,CREATEUSER,CREATETIME,DIRECTORGID,TYPEFLAG) VALUES (1,'超级管理员','1','developer','oi89/VUCsAnxdEQN90B/qA==',0,now(),'0',0,'0','0',1,now(),1,0);
commit;
/*
-- Query: SELECT * FROM ta4.taposition
LIMIT 0, 1000

-- Date: 2013-10-14 15:51
*/
INSERT INTO TAPOSITION (POSITIONID,ORGID,POSITIONNAME,POSITIONTYPE,CREATEPOSITIONID,ORGIDPATH,ORGNAMEPATH,VALIDTIME,CREATEUSER,CREATETIME,EFFECTIVE,ISADMIN,TYPEFLAG) VALUES (1,1,'超级管理员','1',1,'1','银海软件',NULL,1,now(),'0','1',0);
commit;
/*
-- Query: SELECT * FROM ta4.TAUSERposition
LIMIT 0, 1000

-- Date: 2013-10-14 15:52
*/
INSERT INTO TAUSERPOSITION (USERID,POSITIONID,MAINPOSITION,CREATEUSER,CREATETIME) VALUES (1,1,'1',1,now());

INSERT INTO TAYAB003LEVELMG (PYAB003, YAB003) VALUES ('0', '9999');

UPDATE TAMENU SET ISAUDITE=1 WHERE ISAUDITE IS NULL;
UPDATE TAPOSITIONAUTHRITY SET AUDITSTATE=0;
commit;


ALTER TABLE TAUSERPOSITION COMMENT '人员与岗位关系表';

ALTER TABLE TAORGMG ADD CONSTRAINT FK_REFERENCE_8 FOREIGN KEY (ORGID)
      REFERENCES TAORG (ORGID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAORGMG ADD CONSTRAINT FK_REFERENCE_9 FOREIGN KEY (POSITIONID)
      REFERENCES TAPOSITION (POSITIONID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAORG ADD CONSTRAINT FK_REFERENCE_6 FOREIGN KEY (PORGID)
      REFERENCES TAORG (ORGID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAPOSITION ADD CONSTRAINT FK_RELATIONSHIP_5 FOREIGN KEY (ORGID)
      REFERENCES TAORG (ORGID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAPOSITIONAUTHRITY ADD CONSTRAINT FK_REFERENCE_7 FOREIGN KEY (MENUID)
      REFERENCES TAMENU (MENUID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAPOSITIONAUTHRITY ADD CONSTRAINT FK_RELATIONSHIP_11 FOREIGN KEY (POSITIONID)
      REFERENCES TAPOSITION (POSITIONID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAUSERPOSITION ADD CONSTRAINT FK_RELATIONSHIP_10 FOREIGN KEY (POSITIONID)
      REFERENCES TAPOSITION (POSITIONID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TAUSERPOSITION ADD CONSTRAINT FK_RELATIONSHIP_9 FOREIGN KEY (USERID)
      REFERENCES TAUSER (USERID) ON DELETE RESTRICT ON UPDATE RESTRICT;


CREATE VIEW AA10A1 AS
    select 
        AA10.AAA100 AS AAA100,
        AA10.AAA101 AS AAA101,
        AA10.AAA102 AS AAA102,
        AA10.AAA103 AS AAA103,
        AA10.YAB003 AS YAB003,
        AA10.AAE120 AS AAE120,
        1 AS VER
    from
        AA10;

CREATE OR REPLACE VIEW V_YAB139 AS
SELECT
       U.USERID AS USERID,
       M.MENUID AS MENUID,
       DAD.DIMENSIONPERMISSIONID AS YAB139,
       DAD.SYSPATH AS SYSPATH
FROM
       TAMENU M ,
       TADATAACCESSDIMENSION DAD,
       TAUSER U,
       TAPOSITION P,
       TAUSERPOSITION UP
WHERE 1=1
      AND U.USERID=UP.USERID
      AND (U.DESTORY IS NULL OR U.DESTORY=0)
      AND U.EFFECTIVE=0
      AND UP.POSITIONID=P.POSITIONID
      AND P.EFFECTIVE=0
      AND (P.VALIDTIME IS NULL OR P.VALIDTIME>=now())
      AND P.POSITIONID=DAD.POSITIONID
      AND DAD.MENUID=M.MENUID
      AND DAD.DIMENSIONTYPE='YAB139'
      AND DAD.ALLACCESS<>0
UNION
SELECT
       U.USERID AS USERID,
       M.MENUID AS MENUID,
       A.AAA102 AS YAB139,
       DAD.SYSPATH AS SYSPATH
FROM
       TAMENU M ,
       TADATAACCESSDIMENSION DAD,
       TAUSER U,
       TAPOSITION P,
       TAUSERPOSITION UP,
       AA10A1 A
WHERE 1=1
      AND U.USERID=UP.USERID
      AND (U.DESTORY IS NULL OR U.DESTORY=0)
      AND U.EFFECTIVE=0
      AND UP.POSITIONID=P.POSITIONID
      AND P.EFFECTIVE=0
      AND (P.VALIDTIME IS NULL OR P.VALIDTIME>=now())
      AND P.POSITIONID=DAD.POSITIONID
      AND DAD.MENUID=M.MENUID
      AND DAD.DIMENSIONTYPE='YAB139'
      AND DAD.ALLACCESS=0
      AND A.AAA100='YAB139';


commit;

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;


CREATE TABLE QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);


commit;

