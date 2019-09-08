ALTER TABLE TARUNQIANRESOURCE
   DROP CONSTRAINT FK_YHCIP_RU_REFERENCE_YHCIP_RU;
ALTER TABLE TARUNQIANAD52REFERENCE
   DROP CONSTRAINT FK_REPORT_INFO;

DROP TABLE  TARUNQIANRESOURCE CASCADE CONSTRAINTS;

DROP TABLE TARUNQIANPRINTSETUP CASCADE CONSTRAINTS;

DROP TABLE TARUNQIANAD52REFERENCE CASCADE CONSTRAINTS;
-- Create table
create table TARUNQIANRESOURCE
(
  raqfilename       VARCHAR2(200) not null,
  parentraqfilename VARCHAR2(200),
  raqname           VARCHAR2(200),
  raqtype           VARCHAR2(6),
  raqfile           BLOB,
  uploador          VARCHAR2(19),
  uploadtime        DATE,
  subrow            NUMBER,
  subcell           NUMBER,
  raqdatasource     VARCHAR2(19),
  raqparam          VARCHAR2(500),
  orgid             VARCHAR2(15),
  constraint PK_YHCIP_RUNQIAN_RESOURCE primary key (RAQFILENAME)
);
-- Add comments to the table 
comment on table TARUNQIANRESOURCE
  is '润乾报表模板';
-- Add comments to the columns 
comment on column TARUNQIANRESOURCE.raqfilename
  is '文件名/报表标识（RaqfileName）';
comment on column TARUNQIANRESOURCE.parentraqfilename
  is '父报表标识（ParentRaqfileName）';
comment on column TARUNQIANRESOURCE.raqname
  is '报表名称（RaqName）';
comment on column TARUNQIANRESOURCE.raqtype
  is '报表类型（RaqType）';
comment on column TARUNQIANRESOURCE.raqfile
  is '资源文件（RaqFile）';
comment on column TARUNQIANRESOURCE.uploador
  is '上传人（Uploador）';
comment on column TARUNQIANRESOURCE.uploadtime
  is '上传时间（UploadTime）';
comment on column TARUNQIANRESOURCE.subrow
  is '父报表位置行（SubRow）';
comment on column TARUNQIANRESOURCE.subcell
  is '父报表位置列（SubCell）';
comment on column TARUNQIANRESOURCE.raqdatasource
  is '数据源（RaqDataSource）';
comment on column TARUNQIANRESOURCE.raqparam
  is '报表参数JSON格式Str（RaqParam）';
comment on column TARUNQIANRESOURCE.orgid
  is '部门编号(OrgId)';

alter table TARUNQIANRESOURCE
  add constraint FK_YHCIP_RU_REFERENCE_YHCIP_RU foreign key (PARENTRAQFILENAME)
  references TARUNQIANRESOURCE (RAQFILENAME);

-- Create table
create table TARUNQIANPRINTSETUP
(
  setupid    VARCHAR2(200) not null,
  setupvalue VARCHAR2(400) not null,
  constraint PK_PRINTSETUP primary key (SETUPID)
);
-- Add comments to the table 
comment on table TARUNQIANPRINTSETUP
  is '打印设置信息表';
-- Add comments to the columns 
comment on column TARUNQIANPRINTSETUP.setupid
  is '打印设置编号（SetupId）';
comment on column TARUNQIANPRINTSETUP.setupvalue
  is '打印设置信息（SetupValue）';

-- Create table
create table TARUNQIANAD52REFERENCE
(
  menuid              NUMBER(10),
  raqfilename         VARCHAR2(200),
  limited             NUMBER,
  scaleexp            NUMBER,
  isgroup             VARCHAR2(6),
  needsaveasexcel     VARCHAR2(6),
  needsaveasexcel2007 VARCHAR2(6),
  needsaveaspdf       VARCHAR2(6),
  needsaveasword      VARCHAR2(6),
  needsaveastext      VARCHAR2(6),
  needprint           VARCHAR2(6),
  id                  NUMBER(10) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table TARUNQIANAD52REFERENCE
  is 'YHCIP_RUNQIAN_AD52_REFERENCE润乾报表菜单信息';
-- Add comments to the columns 
comment on column TARUNQIANAD52REFERENCE.menuid
  is '功能编号';
comment on column TARUNQIANAD52REFERENCE.raqfilename
  is '文件名/报表标识（RaqfileName）';
comment on column TARUNQIANAD52REFERENCE.limited
  is '每页显示数（Limited）';
comment on column TARUNQIANAD52REFERENCE.scaleexp
  is 'JSP中缩放比率（ScaleExp）';
comment on column TARUNQIANAD52REFERENCE.isgroup
  is '是否按行分页（IsGroup）';
comment on column TARUNQIANAD52REFERENCE.needsaveasexcel
  is '是否保存为Excel（NeedSaveAsExcel）';
comment on column TARUNQIANAD52REFERENCE.needsaveasexcel2007
  is '是否保存为Excel2007（NeedSaveAsExcel2007）';
comment on column TARUNQIANAD52REFERENCE.needsaveaspdf
  is '是否保存为Pdf（NeedSaveAsPdf）';
comment on column TARUNQIANAD52REFERENCE.needsaveasword
  is '是否保存为Word（NeedSaveAsWord）';
comment on column TARUNQIANAD52REFERENCE.needsaveastext
  is '是否保存为Text（NeedSaveAsText）';
comment on column TARUNQIANAD52REFERENCE.needprint
  is '是否保存为Print（NeedPrint）';
comment on column TARUNQIANAD52REFERENCE.id
  is '主键ID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table TARUNQIANAD52REFERENCE
  add constraint PK_REPORT_INFO primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
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