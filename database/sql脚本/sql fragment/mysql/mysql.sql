/*==============================================================*/
/* DBMS name:      MYSQL50                                      */
/* Created on:     2013/10/14 15:39:01                          */
/*==============================================================*/

ALTER TABLE TAORGMG DROP FOREIGN KEY FK_REFERENCE_8;
ALTER TABLE TAORGMG DROP FOREIGN KEY FK_REFERENCE_9;
ALTER TABLE TAORG DROP FOREIGN KEY FK_REFERENCE_6;
ALTER TABLE TAPOSITION DROP FOREIGN KEY FK_RELATIONSHIP_5;
ALTER TABLE TAPOSITIONAUTHRITY DROP FOREIGN KEY FK_REFERENCE_7;
ALTER TABLE TAPOSITIONAUTHRITY DROP FOREIGN KEY FK_RELATIONSHIP_11;
ALTER TABLE TAUSERPOSITION DROP FOREIGN KEY FK_RELATIONSHIP_10;
ALTER TABLE TAUSERPOSITION DROP FOREIGN KEY FK_RELATIONSHIP_9;

DROP view AA10A1;

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

DROP TABLE IF EXISTS TAADMINYAB003SCOPE;

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
   TIME                 DATETIME COMMENT '报错时间'
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
   DESTORY                CHAR(1) COMMENT '是否销毁',
   TYPEFLAG             INT COMMENT '类标识',PRIMARY KEY (ORGID)
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
   OPPOSITION           VARCHAR(10) NOT NULL COMMENT '经办岗位', PRIMARY KEY (LOGID)
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
   EFFECTTIME           DATETIME COMMENT '有效时间', PRIMARY KEY (POSITIONID, MENUID)
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
/* Table: TAADMINYAB003SCOPE                                              */
/*==============================================================*/
CREATE TABLE TAADMINYAB003SCOPE (
  POSITIONID INT(11) NOT NULL COMMENT '岗位id',
  YAB003 VARCHAR(6) NOT NULL COMMENT '分中心',
  PRIMARY KEY (POSITIONID,YAB003)
);

ALTER TABLE TAADMINYAB003SCOPE COMMENT '管理员分中心管理范围';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS TALOCALCACHEVERSION;
/*==============================================================*/
/* Table: TALOCALCACHEVERSION                                   */
/*==============================================================*/
CREATE TABLE TALOCALCACHEVERSION
(
   VERSION		INT(11) NOT NULL COMMENT '版本号',
   CODETYPE		VARCHAR(20) COMMENT '改动的type'
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE TALOCALCACHEVERSION COMMENT '本地缓存码表版本号';
 
insert into TALOCALCACHEVERSION(VERSION, CODETYPE)
values (1,'');


ALTER TABLE AA10 COMMENT 'AA10代码表';
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
commit;

INSERT INTO TACONFIGSYSPATH (SERIALID, ID, NAME, URL, PY, CURSYSTEM) values('1','sysmg','系统管理','http://127.0.0.1:8080/ta3/','xtgl','0');
commit;
/*
-- Query: SELECT * FROM ta4.tamenu
LIMIT 0, 1000

-- Date: 2013-10-14 15:44
*/
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('1','0','银海软件',NULL,'1','银海软件','tree-apply',NULL,NULL,NULL,'0','1','0',NULL,'0','01','1','1','0',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('2','1','系统管理',NULL,'1/2','银海软件/系统管理','tree-apply',NULL,NULL,NULL,'0','1','0',NULL,'0','01','2','1','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('3','2','菜单管理','sysapp/menuMgAction.do','1/2/3','银海软件/系统管理/菜单管理','tree-setting',NULL,NULL,NULL,'0','1','0',NULL,'0','01','3','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('18','2','系统参数管理','sysapp/configMgAction.do','1/2/18','银海软件/系统管理/系统参数管理','tree-preferences-system',NULL,NULL,NULL,'0','1','0',NULL,'3','01','3','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('4','2','组织权限管理',NULL,'1/2/4','银海软件/系统管理/组织权限管理','tree-view-refresh',NULL,NULL,NULL,'0','1','1',NULL,'1','01','3','1','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('5','4','用户功能权限管理','org/position/personalPositionMgAction.do','1/2/4/5','银海软件/系统管理/组织权限管理/用户功能权限管理','tree-user',NULL,NULL,NULL,'0','1','1',NULL,'3','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('6','4','岗位及权限管理','org/position/positionMgAction.do','1/2/4/6','银海软件/系统管理/组织权限管理/岗位及权限管理','tree-users',NULL,NULL,NULL,'0','1','1',NULL,'4','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('7','4','管理员管理','org/position/adminMgAction.do','1/2/4/7','银海软件/系统管理/组织权限管理/管理员管理','tree-userm',NULL,NULL,NULL,'0','1','1',NULL,'2','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('9','4','用户管理','org/userMgAction.do','1/2/4/9','银海软件/系统管理/组织权限管理/用户管理','tree-group',NULL,NULL,NULL,'0','1','1',NULL,'1','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('10','4','组织管理','org/orgMgAction.do','1/2/4/10','银海软件/系统管理/组织权限管理/组织管理','tree-organisation',NULL,NULL,NULL,'0','1','1',NULL,'0','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('11','4','用户岗位设置','org/position/positionSettingMgAction.do','1/2/4/11','银海软件/系统管理/组织权限管理/用户岗位设置','tree-preferences-system',NULL,NULL,NULL,'0','1','1',NULL,'5','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('12','4','功能权限代理','org/position/delegatePositionAction.do','1/2/4/12','银海软件/系统管理/组织权限管理/功能权限代理','tree-mail-send-receive',NULL,NULL,NULL,'0','1','1',NULL,'6','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('13','4','相似权限授权','org/position/similarAuthorityAction.do','1/2/4/13','银海软件/系统管理/组织权限管理/相似权限授权','tree-view-refresh',NULL,NULL,NULL,'0','1','1',NULL,'7','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('14','4','分中心数据权限范围管理','org/position/yab003ScopeMgAction.do','1/2/4/14','银海软件/系统管理/组织权限管理/分中心数据权限范围管理','tree-text-editor',NULL,NULL,NULL,'0','1','1',NULL,'8','01','4','0','1',NULL,'sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('15','2','工作台管理','','1/2/15','银海软件/系统管理/工作台管理','tree-destop','','','','0','1','1','','2','01','3','1','1','','sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('16','15','工作台模块管理','sysapp/moduleMainAction.do','1/2/15/16','银海软件/系统管理/工作台管理/工作台模块管理','tree-menu','','','','0','1','1','','0','01','4','0','1','','sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('17','15','模块授权管理','sysapp/moduleMainAction!toGrant.do','1/2/15/17','银海软件/系统管理/工作台管理/模块授权管理','tree-star','','','','0','1','1','','1','01','4','0','1','','sysmg',0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('19','2', '代码表查看', 'sysapp/appCodeMainAction.do', '1/2/19', '银海软件/系统管理/代码表查看', 'tree-system-search', NULL, NULL, NULL, '0', '1', '1', NULL, 3, '01', 3, '0', '1', NULL, 'sysmg', 0);
INSERT INTO TAMENU (MENUID,PMENUID,MENUNAME,URL,MENUIDPATH,MENUNAMEPATH,ICONSKIN,SELECTIMAGE,REPORTID,ACCESSTIMEEL,EFFECTIVE,SECURITYPOLICY,ISDISMULTIPOS,QUICKCODE,SORTNO,RESOURCETYPE,MENULEVEL,ISLEAF,MENUTYPE,ISCACHE,SYSPATH,TYPEFLAG) values('20','2', '集群server地址配置', 'sysapp/serverAddressAction.do', '1/2/20', '银海软件/系统管理/集群server地址配置', 'btn-setting', NULL, NULL, NULL, '0', '1', '1', NULL, 4, '01', 3, '0', '1', NULL, 'sysmg', 0);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG) values ('21', '4', '批量导入组织人员', 'org/upload/uploadOrgUserAction.do', '1/2/4/21', '银海软件/系统管理/组织权限管理/批量导入组织人员', 'tree-edit-redo', '', '', '', '0', '1', '1', '', 9, '01', 4, '0', '1', '', 'sysmg', '', 0);
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG) values ('22', '2', '系统Exception日志', 'sysapp/serverExeceptionLogAction.do', '1/2/22', '银海软件/系统管理/系统Exception日志', 'tree-document-new', NULL, NULL, NULL, '0', '1', '1', NULL, 6, '01', 3, '0', '1', NULL, 'sysmg', NULL, NULL);
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
