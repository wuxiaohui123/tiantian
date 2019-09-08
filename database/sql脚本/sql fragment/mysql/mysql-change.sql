﻿/* 3.9.1408111431 */
ALTER TABLE TAORGOPLOG ADD ISPERMISSION VARCHAR(1) COMMENT '是否有权限';

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
  PRIMARY KEY (DIMENSIONID)
);
ALTER TABLE TADATAACCESSDIMENSION COMMENT '维度数据权限表';
INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG) values('23','4','经办机构数据权限管理','org/position/dataAccessDimensionManagementAction.do','1/2/4/23','银海软件/系统管理/组织权限管理/经办机构数据权限管理','tree-database',NULL,NULL,'','0','1','1',NULL,'10','01','4','0','1',NULL,'sysmg',NULL,NULL);
/*==============================================================*/
/* Table: TADATAACCESSDIMENSION                                                  */
/*==============================================================*/
CREATE TABLE T_YAB003 (
  YAB003 VARCHAR(6) NOT NULL COMMENT '经办机构',
  PRIMARY KEY (YAB003)
);
ALTER TABLE T_YAB003 COMMENT '经办机构临时表，用于数据权限';

/*3.9.1408251628*/

ALTER TABLE TAACCESSLOG ADD URL VARCHAR(1024) COMMENT '访问路径';
ALTER TABLE TAACCESSLOG ADD SYSFLAG VARCHAR(50) COMMENT '系统标识';

/* 3.9.1409121038 */

ALTER TABLE TAPOSITION ADD POSITIONCATEGORY VARCHAR(2) COMMENT '岗位类别';
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('POSITIONCATEGORY', '岗位类别', '01', '业务岗', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('POSITIONCATEGORY', '岗位类别', '02', '稽核岗', '9999', '0', 0);

ALTER TABLE TASERVEREXCEPTIONLOG ADD SYSPATH VARCHAR(20) COMMENT '系统路径';

INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG) values('28','2','系统路径管理','sysapp/configSysPathAction.do','1/2/4/28','银海软件/系统路径管理','tree-database',NULL,NULL,'','0','1','1',NULL,'7','01','4','0','1',NULL,'sysmg',NULL,NULL);

ALTER TABLE TADATAACCESSDIMENSION ADD SYSPATH VARCHAR(50) COMMENT '系统标识';

CREATE OR REPLACE VIEW YAB003_V AS
SELECT
       U.USERID AS USERID,
       M.MENUID AS MENUID,
       DAD.DIMENSIONPERMISSIONID AS YAB003,
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
      AND DAD.DIMENSIONTYPE='YAB003'
      AND DAD.ALLACCESS<>0
UNION
SELECT
       U.USERID AS USERID,
       M.MENUID AS MENUID,
       A.AAA102 AS YAB003,
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
      AND DAD.DIMENSIONTYPE='YAB003'
      AND DAD.ALLACCESS=0
      AND A.AAA100='YAB003';
      
/*3.9.1411031447*/
DROP VIEW IF EXISTS YAB003_V;
DROP TABLE IF EXISTS T_YAB003;
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
      
DROP TABLE IF EXISTS TAADMINYAB139SCOPE;
CREATE TABLE TAADMINYAB139SCOPE (
  POSITIONID INT(11) NOT NULL COMMENT '岗位id',
  YAB139 VARCHAR(6) NOT NULL COMMENT '数据区',
  PRIMARY KEY (POSITIONID,YAB139)
);

ALTER TABLE TAADMINYAB139SCOPE COMMENT '管理员数据区管理范围';
INSERT INTO TAADMINYAB139SCOPE(POSITIONID,YAB139) SELECT T.POSITIONID,T.YAB003 FROM TAADMINYAB003SCOPE T;
DROP TABLE IF EXISTS TAADMINYAB003SCOPE;

ALTER TABLE TAORG ADD YAB139 VARCHAR(6) COMMENT '数据区';

ALTER TABLE TAPOSITIONAUTHRITY ADD AUDITEACCESSDATE DATE COMMENT '审核通过时间';
ALTER TABLE TAPOSITIONAUTHRITY ADD AUDITEUSER INT(10) COMMENT '审核人';
ALTER TABLE TAPOSITIONAUTHRITY ADD AUDITSTATE VARCHAR(1) COMMENT '审核状态';
ALTER TABLE TAMENU ADD ISAUDITE VARCHAR(1) DEFAULT 1 COMMENT '是否需要审核';
UPDATE TAMENU SET ISAUDITE=1 WHERE ISAUDITE IS NULL;
UPDATE TAPOSITIONAUTHRITY SET AUDITSTATE=0;

INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '0', '无需审核', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '1', '待审核', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '2', '审核通过', '9999', '0', 0);
INSERT INTO AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER) VALUES ('AUDITSTATE', '审核状态', '3', '审核未通过', '9999', '0', 0);

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

INSERT INTO TAMENU (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE)
VALUES (30, 4, '权限内控', 'org/position/innerControlAction.do', '1/2/4/30', '银海软件/系统管理/组织权限管理/权限内控', 'tree-edit-redo', '', '', '', '0', '1', '1', '', 11, '01', 4, '0', '1', '', 'sysmg', '1', null, '1');

INSERT INTO tamenu (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG, ISAUDITE)
VALUES (29, 4, '经办机构数据区管理', 'org/position/yab003MgAction.do', '1/2/4/29', '银海软件/系统管理/组织权限管理/经办机构数据区管理', 'tree-organisation', '', '', '', '0', '1', '1', '', 10, '01', 4, '0', '1', '', 'sysmg', '1', null, '1');


ALTER TABLE TASERVEREXCEPTIONLOG ADD CLIENTIP VARCHAR(50) COMMENT '客户端ip';
ALTER TABLE TASERVEREXCEPTIONLOG ADD URL VARCHAR(100) COMMENT '访问功能url';
ALTER TABLE TASERVEREXCEPTIONLOG ADD MENUID VARCHAR(8) COMMENT '菜单id';
ALTER TABLE TASERVEREXCEPTIONLOG ADD MENUNAME VARCHAR(30) COMMENT '菜单名称';
ALTER TABLE TASERVEREXCEPTIONLOG ADD USERAGENT VARCHAR(200) COMMENT '客户端环境';

ALTER TABLE TAMENU ADD CONSOLEMODULE CHAR(1) DEFAULT 1 COMMENT '是否为工作台模块';

commit;


