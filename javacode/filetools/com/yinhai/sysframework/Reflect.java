package com.yinhai.sysframework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;

import com.yinhai.sysframework.util.DateUtil;

public class Reflect {

	private Field[] publicFields;
	private Field[] thisFields;
	private Method[] publicMethods;
	private Method[] thisMethods;
	private HashMap publicFieldMap = new HashMap();
	private HashMap thisFieldMap = new HashMap();
	private HashMap publicMethodMap = new HashMap();
	private HashMap thisMethodMap = new HashMap();

	public Reflect() {
	}

	public Reflect(Class clazz) {
		publicFields = clazz.getFields();
		thisFields = clazz.getDeclaredFields();
		publicMethods = clazz.getMethods();
		thisMethods = clazz.getDeclaredMethods();
		for (int i = 0; i < publicFields.length; i++) {
			publicFieldMap.put(publicFields[i].getName(), publicFields[i]);
		}
		for (int i = 0; i < thisFields.length; i++) {
			thisFieldMap.put(thisFields[i].getName(), thisFields[i]);
		}
		for (int i = 0; i < publicMethods.length; i++) {
			publicMethodMap.put(publicMethods[i].getName(), publicMethods[i]);
		}
		for (int i = 0; i < thisMethods.length; i++) {
			thisMethodMap.put(thisMethods[i].getName(), thisMethods[i]);
		}
	}

	public Object getObjFieldValue(Object obj, String fieldName) {
		Field field = (Field) thisFieldMap.get(fieldName);
		if ((field == null) || (obj == null)) {
			return null;
		}
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalAccessException ex) {
			return null;
		} catch (IllegalArgumentException ex) {
		}
		return null;
	}

	public String getObjFieldStrValue(Object obj, String fieldName) {
		Object fieldObj = getObjFieldValue(obj, fieldName);
		String type = getFieldType(fieldName);
		type = type.toLowerCase();
		String str = "";
		if (fieldObj == null) {
			return "";
		}
		if ((type.indexOf("date") > -1) || (type.indexOf("time") > -1)) {
			str = DateUtil.dateToString((Date) fieldObj);
		} else {
			str = fieldObj.toString();
		}

		return str;
	}

	public String getFieldType(String fieldName) {
		Field field = (Field) thisFieldMap.get(fieldName);
		if (field == null) {
			return "";
		}
		String type = field.getType().toString();
		return type;
	}

	public String getFieldScope(String fieldName) {
		Field field = (Field) thisFieldMap.get(fieldName);
		if (field == null) {
			return "";
		}
		int i = field.getModifiers();
		String s = Modifier.toString(i);
		return s;
	}

	public Class getFieldClass(String fieldName) {
		Field field = (Field) thisFieldMap.get(fieldName);
		if (field == null) {
			return null;
		}
		Class clazz = field.getDeclaringClass();
		return clazz;
	}
}
