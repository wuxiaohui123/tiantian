ALTER TABLE TARUNQIANRESOURCE
   DROP FOREIGN KEY FK_YHCIP_RU_REFERENCE_YHCIP_RU;
ALTER TABLE TARUNQIANAD52REFERENCE
   DROP FOREIGN KEY FK_REPORT_INFO;

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

insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('RAQTYPE', '报表类型', '0', '参数报表', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('RAQTYPE', '报表类型', '1', '主报表', '9999', '0', 0);
insert into AA10 (AAA100, AAA101, AAA102, AAA103, YAB003, AAE120, VER)
values ('RAQTYPE', '报表类型', '2', '子报表', '9999', '0', 0);
insert into tamenu (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG)
values (25, 2, '润乾报表', '', '1/2/25', '银海软件/系统管理/润乾报表', 'tree-menu', '', '', '', '0', '1', '1', '', 10, '01', 3, '1', '1', '', 'sysmg', '', null);

insert into tamenu (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG)
values (26, 25, '润乾报表菜单管理', 'runqian/queryReportMgAction.do', '1/2/25/26', '银海软件/系统管理/润乾报表/润乾报表菜单管理', 'tree-menu', '', '', '', '0', '1', '1', '', 0, '01', 4, '0', '1', '', 'sysmg', '', null);

insert into tamenu (MENUID, PMENUID, MENUNAME, URL, MENUIDPATH, MENUNAMEPATH, ICONSKIN, SELECTIMAGE, REPORTID, ACCESSTIMEEL, EFFECTIVE, SECURITYPOLICY, ISDISMULTIPOS, QUICKCODE, SORTNO, RESOURCETYPE, MENULEVEL, ISLEAF, MENUTYPE, ISCACHE, SYSPATH, USEYAB003, TYPEFLAG)
values (27, 25, '润乾报表模板管理', 'runqian/runQianReportFileMgAction.do', '1/2/25/27', '银海软件/系统管理/润乾报表/润乾报表模板管理', 'tree-menu', '', '', '', '0', '1', '1', '', 1, '01', 4, '0', '1', '', 'sysmg', '', null);

commit;