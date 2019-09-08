package com.yinhai.sysframework.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.yinhai.sysframework.exception.SysLevelException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ClassDef {

	private static Log log = LogFactory.getLog(ClassDef.class);
	private String name;
	private Class<?> clazz;
	private Map<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();
	private Map<Object, Method> methods = new HashMap<Object, Method>();
	private Map<String, PropertyDescriptor> props = new HashMap<String, PropertyDescriptor>();
	private Map<String, Field> fields = new HashMap<String, Field>();

	public ClassDef(Class<?> clazz) {
		name = clazz.getName();
		this.clazz = clazz;
		loadClassDef(clazz);
	}

	private void loadClassDef(Class<?> clazz) {
		log.debug("开始加载类定义：" + clazz.getName());
		Constructor<?>[] constructors = clazz.getConstructors();
		Arrays.stream(constructors).forEach(constructor -> {
			log.debug("缓存构造方法：" + constructor.getName() + ReflectUtil.getParmTypesDesc(constructor.getParameterTypes()));
			this.constructors.put(ReflectUtil.getParmTypesDesc(constructor.getParameterTypes()), constructor);
		});
		Field[] fileds = clazz.getFields();
		Arrays.stream(fileds).forEach(field -> {
			log.debug("缓存成员变量：" + field.getName());
			fields.put(field.getName(), field);
		});
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new SysLevelException(e);
		}
		PropertyDescriptor[] pd = bi.getPropertyDescriptors();
		Arrays.stream(pd).forEach(propertyDescriptor -> {
			log.debug("缓存成员属性：" + propertyDescriptor.getName());
			props.put(propertyDescriptor.getName(), propertyDescriptor);
		});
		Method[] methods = clazz.getMethods();
		Arrays.stream(methods).forEach(method -> {
			log.debug("缓存成员方法：" + method.getName() + ReflectUtil.getParmTypesDesc(method.getParameterTypes()));
			this.methods.put(method.getName() + ReflectUtil.getParmTypesDesc(method.getParameterTypes()), method);
		});
		log.debug("加载类定义：" + clazz.getName() + "成功");
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Map<String, Constructor<?>> getConstructors() {
		return constructors;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public Map<Object, Method> getMethods() {
		return methods;
	}

	public String getName() {
		return name;
	}

	public Map<String, PropertyDescriptor> getProps() {
		return props;
	}
}
