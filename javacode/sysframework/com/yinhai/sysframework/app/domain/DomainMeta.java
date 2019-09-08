package com.yinhai.sysframework.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("all")
public class DomainMeta implements Serializable {

	private String domainName;
	private String tableName;
	private String comment;
	private String namespace;
	private String sqlmapxml;
	private Map fields = new HashMap();

	public DomainMeta() {
	}

	public DomainMeta(String domainName, String tableName, String comment) {
		this.domainName = domainName;
		this.tableName = tableName;
		this.comment = comment;
	}

	public DomainMeta(String domainName, String tableName, String comment, String namespace, String sqlmapxml) {
		this.domainName = domainName;
		this.tableName = tableName;
		this.comment = comment;
		this.namespace = namespace;
		this.sqlmapxml = sqlmapxml;
	}

	public synchronized DomainMeta appendField(FieldMeta field) {
		fields.put(field.getFieldName(), field);
		return this;
	}

	public synchronized DomainMeta appendField(String fieldName, String columnName, String comment, String javaType,
			String dbType, int length, boolean notEmpty, boolean primaryKey, boolean code) {
		fields.put(fieldName, new FieldMeta(fieldName, columnName, comment, javaType, dbType, length, notEmpty,
				primaryKey, code));
		return this;
	}

	public String getDomainName() {
		return domainName;
	}

	public Map getFields() {
		return fields;
	}

	public FieldMeta getField(String fieldName) {
		return (FieldMeta) fields.get(fieldName);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getSqlmapxml() {
		return sqlmapxml;
	}

	public void setSqlmapxml(String sqlmapxml) {
		this.sqlmapxml = sqlmapxml;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public List getFieldsAsList() {
		return new ArrayList(fields.values());
	}

	public void removeField(String fieldName) {
		fields.remove(fieldName);
	}

	public String getTableName() {
		return tableName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setFields(Map fields) {
		this.fields = fields;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append((new StringBuilder()).append("领域对象名:").append(getDomainName()).append("\n").toString()).append((new StringBuilder()).append("对应数据库表名:").append(getTableName()).append("\n").toString()).append((new StringBuilder()).append("中文名:").append(getComment()).append("\n").toString()).append("字段描述如下\n");
		for (int i = 0; i < getFields().size(); i++)
			buffer.append((new StringBuilder()).append(((FieldMeta)getFieldsAsList().get(i)).toString()).append("\n").toString());

		return buffer.toString();
	}

	public static class FieldMeta {
		private String fieldName;

		private String columnName;

		private String comment;

		private String javaType;

		private String dbType;

		private boolean primaryKey;

		private boolean notEmpty;

		private int length;

		private boolean code;

		public FieldMeta() {
		}

		public FieldMeta(String fieldName, String columnName, String comment, String javaType, String dbType,
				int length, boolean notEmpty, boolean primaryKey, boolean code) {
			this.code = code;
			this.columnName = columnName;
			this.comment = comment;
			this.dbType = dbType;
			this.fieldName = fieldName;
			this.javaType = javaType;
			this.length = length;
			this.notEmpty = notEmpty;
			this.primaryKey = primaryKey;
		}

		public boolean isCode() {
			return code;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getComment() {
			return comment;
		}

		public String getDbType() {
			return dbType;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getJavaType() {
			return javaType;
		}

		public int getLength() {
			return length;
		}

		public boolean isNotEmpty() {
			return notEmpty;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public void setCode(boolean code) {
			this.code = code;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public void setDbType(String dbType) {
			this.dbType = dbType;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public void setJavaType(String javaType) {
			this.javaType = javaType;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public void setNotEmpty(boolean notEmpty) {
			this.notEmpty = notEmpty;
		}

		public void setPrimaryKey(boolean primaryKey) {
			this.primaryKey = primaryKey;
		}

		public String toString() {
			return (new ToStringBuilder(this)).append("字段名称:", getFieldName()).append("数据库列名:", getColumnName()).append("中文名:", getComment()).append("java数据类型:", getJavaType()).append("数据库列类型:", getDbType()).append("长度:", getLength()).append("是否主键:", !isPrimaryKey() ? "否" : "是").append("是否非空:", !isNotEmpty() ? "否" : "是").append("是否是代码:", !isCode() ? "否" : "是").append("\n").toString();

		}
	}
}
