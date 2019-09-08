package com.yinhai.sysframework.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.yinhai.sysframework.exception.SysLevelException;

@SuppressWarnings("all")
public class ReflectUtil {

    private static final Class[] NO_CLASSES = new Class[0];

    private static final Class[] NO_OBJECTS = new Class[0];

    private static final Class[] OBJECT = {Object.class};

    private static final Method OBJECT_EQUALS;

    private static final ReflectUtil instance = new ReflectUtil();

    private static final Map classDefnCache = new HashMap();

    static {
        Method eq;
        try {
            eq = Object.class.getMethod("equals", OBJECT);
        } catch (Exception e) {
            throw new SysLevelException("Could not find Object.equals()", e);
        }
        OBJECT_EQUALS = eq;
    }

    public static ReflectUtil getInstance() {
        return instance;
    }

    public static boolean isPublic(Class clazz, Member member) {
        return (Modifier.isPublic(member.getModifiers())) && (Modifier.isPublic(clazz.getModifiers()));
    }

    public static boolean isAbstractClass(Class clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isInterfaceClass(Class clazz) {
        return Modifier.isInterface(clazz.getModifiers());
    }

    public static boolean overridesEquals(Class clazz) {
        Method equals;
        try {
            equals = clazz.getMethod("equals", OBJECT);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return !OBJECT_EQUALS.equals(equals);
    }

    protected static ClassDef getClassDefn(Class clazz) {
        ClassDef classDefn = (ClassDef) classDefnCache.get(clazz.getName());
        if (classDefn == null) {
            classDefn = new ClassDef(clazz);
            classDefnCache.put(clazz.getName(), classDefn);
        }
        return classDefn;
    }

    protected static ClassDef getClassDefn(String className) {
        ClassDef classDefn = (ClassDef) classDefnCache.get(className);
        if (classDefn == null) {
            Class clazz;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new SysLevelException("Not found class:" + className, e);
            }
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new SysLevelException("Not found class:" + className, e);
            }
            classDefn = getClassDefn(clazz);
        }
        return classDefn;
    }

    public static String getParmTypesDesc(Class[] parmTypes) {
        StringBuffer parmTypesDesc = new StringBuffer("");
        for (int i = 0; (parmTypes != null) && (i < parmTypes.length); i++) {
            parmTypesDesc.append(parmTypes[i].toString() + (i == parmTypes.length - 1 ? "" : ","));
        }
        return "(" + parmTypesDesc + ")";
    }

    public static Class classForName(String name) {
        return getClassDefn(name).getClazz();
    }

    public static Constructor getConstructor(Class clazz, Class[] parmTypes) {
        Constructor constructor = (Constructor) getClassDefn(clazz).getConstructors().get(parmTypes);
        if (constructor == null) {
            throw new SysLevelException("No such Constructor:" + clazz.getName() + getParmTypesDesc(parmTypes));
        }
        return constructor;
    }

    public static Constructor getConstructor(Class clazz) {
        return getConstructor(clazz, NO_CLASSES);
    }

    public static Object newInstance(Class clazz) {
        try {
            getClassDefn(clazz);
            return clazz.newInstance();
        } catch (IllegalAccessException ex) {
            throw new SysLevelException("Can't create instance of " + clazz.getName() + "()");
        } catch (InstantiationException ex) {
            throw new SysLevelException("Can't create instance of " + clazz.getName() + "()");
        }
    }

    public static Object newInstance(String className) {
        return newInstance(classForName(className));
    }

    public static Object newInstance(Class clazz, Class[] parmTypes, Object[] parameterValues) {
        try {
            return getConstructor(clazz, parmTypes).newInstance(parameterValues);
        } catch (IllegalAccessException ex) {
            throw new SysLevelException("Can't create instance of " + clazz.getName() + getParmTypesDesc(parmTypes));
        } catch (InstantiationException ex) {
            throw new SysLevelException("Can't create instance of " + clazz.getName() + getParmTypesDesc(parmTypes));
        } catch (InvocationTargetException e) {
            throw new SysLevelException("Can't create instance of " + clazz.getName() + getParmTypesDesc(parmTypes));
        }
    }

    public static Object newInstance(String className, Class[] parmTypes, Object[] parameterValues) {
        return newInstance(classForName(className), parmTypes, parameterValues);
    }

    public static Field getField(Class clazz, String fieldName) {
        Field field = (Field) getClassDefn(clazz).getFields().get(fieldName);
        if (field == null) {
            throw new SysLevelException("No such field:" + clazz.getName() + "." + fieldName);
        }
        return field;
    }

    public static Object getFieldValue(Object object, Field field) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new SysLevelException("Access failed of field:" + field.getName(), e);
        }
    }

    public static Object getFieldValue(Object object, Class clazz, String fieldName) {
        return getFieldValue(object, getField(clazz, fieldName));
    }

    public static Object getFieldValue(Object object, String fieldName) {
        return getFieldValue(object, object.getClass(), fieldName);
    }

    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new SysLevelException("Access failed of field:" + field.getName(), e);
        }
    }

    public static void setFieldValue(Object object, Class clazz, String fieldName, Object value) {
        setFieldValue(object, getField(clazz, fieldName), value);
    }

    public static void setFieldValue(Object object, String fieldName, Object value) {
        setFieldValue(object, object.getClass(), fieldName, value);
    }

    public static Object getFieldValue(Class clazz, String staticFieldName) {
        return getFieldValue(null, clazz, staticFieldName);
    }

    public static Object getFieldValue(String className, String staticFieldName) {
        return getFieldValue(classForName(className), staticFieldName);
    }

    public static void setFieldValue(Class clazz, String staticFieldName, Object value) {
        setFieldValue(null, clazz, staticFieldName, value);
    }

    public static void setFieldValue(String className, String staticFieldName, Object value) {
        setFieldValue(classForName(className), staticFieldName, value);
    }

    public static Method getMethod(String className, String methodName, Class[] parmTypes) {
        return getMethod(classForName(className), methodName, parmTypes);
    }

    public static Method getMethod(Class clazz, String methodName, Class[] parmTypes) {
        Method method = (Method) getClassDefn(clazz).getMethods().get(methodName + getParmTypesDesc(parmTypes));
        if (method == null) {
            throw new SysLevelException("No Such Method:" + methodName + getParmTypesDesc(parmTypes));
        }
        return method;
    }

    public static Class getMethodReturnType(Class clazz, String methodName, Class[] parmTypes) {
        return getMethod(clazz, methodName, parmTypes).getReturnType();
    }

    public static Class getMethodReturnType(String className, String methodName, Class[] parmTypes) {
        return getMethod(className, methodName, parmTypes).getReturnType();
    }

    public static Object invokeMethod(Object object, Method method, Object[] parameterValues) {
        try {
            return method.invoke(object, parameterValues);
        } catch (IllegalAccessException e) {
            throw new SysLevelException(
                    "Access failed of Method:" + method.getName() + getParmTypesDesc(method.getParameterTypes()), e);
        } catch (InvocationTargetException e) {
            throw new SysLevelException(
                    "Access failed of Method:" + method.getName() + getParmTypesDesc(method.getParameterTypes()), e);
        }
    }

    public static Object invokeMethod(Object object, Class clazz, String methodName, Class[] parmTypes,
                                      Object[] parameterValues) {
        return invokeMethod(object, getMethod(clazz, methodName, parmTypes), parameterValues);
    }

    public static Object invokeMethod(Object object, String methodName, Class[] parmTypes, Object[] parameterValues) {
        return invokeMethod(object, object.getClass(), methodName, parmTypes, parameterValues);
    }

    public static Object invokeMethod(Class clazz, String staticMethodName, Class[] parmTypes,
                                      Object[] parameterValues) {
        return invokeMethod(null, clazz, staticMethodName, parmTypes, parameterValues);
    }

    public static void copyMapToObject(Map map, Object obj, boolean isUpdate) {
        if (map == null || obj == null) {
            return;
        }
        Map props = getPropsFromCache(obj);
        props.forEach((k, v) -> {
            PropertyDescriptor pd = (PropertyDescriptor) v;
            if (!map.containsKey(pd.getName())) {
                return;
            }
            Object value = getObjectByClassType(pd.getName(), pd.getPropertyType(), map.get(pd.getName()));
            if (!isUpdate && pd.getName().equals("id")) {
                return;
            }
            Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                return;
            }
            Class<?> mt = writeMethod.getParameterTypes()[0];
            if (value != null && mt == Timestamp.class && value.getClass() == Date.class) {
                value = new Timestamp(((Date) value).getTime());
            }
            invokeMethod(obj, writeMethod, new Object[]{value});
        });
    }

    public static Object generatorObjectFromArray(String[] fName, Object[] fValue, String domainObjectName) {
        if (fName == null || fValue == null) {
            return null;
        }
        final Object obj = newInstance(domainObjectName);
        Map props = getPropsFromCache(obj);
        props.forEach((k, v) -> {
            PropertyDescriptor pd = (PropertyDescriptor) v;
            Object value = getObjectByClassType(pd.getName(), pd.getPropertyType(), getFieldByName(fName, fValue, pd.getName()));
            if (value == null) {
                return;
            }
            Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                return;
            }
            invokeMethod(obj, writeMethod, new Object[]{value});
        });
        return obj;
    }

    protected static Object getFieldByName(String[] strSource, Object[] strDest, String name) {
        for (int i = 0; i < strSource.length; i++) {
            if (name.equals(strSource[i])) {
                if (i >= strDest.length) {
                    return "";
                }
                return strDest[i];
            }
        }
        return null;
    }

    public static void copyObjectToMap(Object obj, Map map) {
        if (map == null || obj == null) {
            return;
        }
        Map props = getPropsFromCache(obj);
        props.forEach((k, v) -> {
            PropertyDescriptor pd = (PropertyDescriptor) v;
            Method readMethod = pd.getReadMethod();
            if (readMethod == null) {
                return;
            }
            map.put(pd.getName(), invokeMethod(obj, readMethod, NO_CLASSES));
        });
    }

    public static void copyObjectToObject(Object src, Object dest) {
        if (src == null || dest == null)
            throw new NullPointerException();
        Map props = getPropsFromCache(src);
        Map props2 = getPropsFromCache(dest);
        props.forEach((k, v) -> {
            PropertyDescriptor pd = (PropertyDescriptor) v;
            PropertyDescriptor pd2 = (PropertyDescriptor) props2.get(pd.getName());
            if (pd2 == null) {
                return;
            }
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd2.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                return;
            }
            if (!pd2.getPropertyType().equals(pd.getPropertyType()) && !isInheritOrImplement(pd.getPropertyType(), pd2.getPropertyType())) {
                return;
            }
            invokeMethod(dest, writeMethod, new Object[]{invokeMethod(src, readMethod, NO_CLASSES)});
        });
    }

    public static void copyObjectToObjectNotNull(Object src, Object dest) {
        if (src == null || dest == null)
            throw new NullPointerException();
        Map props = getPropsFromCache(src);
        Map props2 = getPropsFromCache(dest);
        props.forEach((k, v) -> {
            PropertyDescriptor pd = (PropertyDescriptor) v;
            PropertyDescriptor pd2 = (PropertyDescriptor) props2.get(pd.getName());
            if (pd2 == null) {
                return;
            }
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd2.getWriteMethod();
            if (readMethod == null || writeMethod == null) {
                return;
            }
            if (!pd2.getPropertyType().equals(pd.getPropertyType()) && !isInheritOrImplement(pd.getPropertyType(), pd2.getPropertyType())) {
                return;
            }
            if (invokeMethod(src, readMethod, NO_CLASSES) == null) {
                return;
            }
            invokeMethod(dest, writeMethod, new Object[]{invokeMethod(src, readMethod, NO_CLASSES)});
        });
    }

    public static boolean hasAccessor(Class clazz, String propName) {
        Map props = getPropsFromCache(clazz);
        PropertyDescriptor pd = (PropertyDescriptor) props.get(propName);
        if (pd == null || pd.getReadMethod() == null) {
            return false;
        }
        return true;
    }

    public static boolean hasAccessor(Object obj, String propName) {
        return hasAccessor(obj.getClass(), propName);
    }

    public static boolean hasMutator(Class clazz, String propName) {
        Map props = getPropsFromCache(clazz);
        PropertyDescriptor pd = (PropertyDescriptor) props.get(propName);
        if (pd == null || pd.getWriteMethod() == null) {
            return false;
        }
        return true;
    }

    public static boolean hasMutator(Object obj, String propName) {
        return hasMutator(obj.getClass(), propName);
    }

    public static Object getPropertyValue(Object obj, String propName) {
        Map props = getPropsFromCache(obj);
        PropertyDescriptor pd = (PropertyDescriptor) props.get(propName);
        if (pd == null)
            return null;
        Method readMethod = pd.getReadMethod();
        if (readMethod == null)
            return null;
        return invokeMethod(obj, readMethod, NO_OBJECTS);
    }

    public static void setPropertyValue(Object obj, String propName, Object value) {
        Map props = getPropsFromCache(obj);
        PropertyDescriptor pd = (PropertyDescriptor) props.get(propName);
        if (pd == null) {
            throw new SysLevelException("No such property:" + propName);
        }
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) {
            throw new SysLevelException("No writemethod of property:" + propName);
        }
        invokeMethod(obj, writeMethod, new Object[]{getObjectByClassType(pd.getName(), pd.getPropertyType(), value)});
    }

    public static Map<String, Object> getPropsFromObject(Object obj, String[] propsName) {
        Map<String, Object> map = Maps.newHashMap();
        Arrays.stream(propsName).forEach(name -> {
            map.put(name, getPropertyValue(obj, name));
        });
        return map;
    }

    protected static boolean hasAccessors(PropertyDescriptor pd) {
        return (pd.getReadMethod() != null) && (pd.getWriteMethod() != null);
    }

    protected static Map getPropsFromCache(Object object) {
        return getPropsFromCache(object.getClass());
    }

    protected static Map getPropsFromCache(Class clazz) {
        return getClassDefn(clazz).getProps();
    }

    public static boolean isInherit(Class c1, Class c2) {
        if (isInterfaceClass(c1)) {
            if (isInterfaceClass(c2)) {
                return isInheritOrImplement(c1, c2);
            }
            return false;
        }
        do {
            if (c1 == c2) {
                return true;
            }
        } while ((c1 = c1.getSuperclass()) != null);
        return false;
    }

    public static boolean isImplement(Class c1, Class c2) {
        do {
            Class[] classes = c1.getInterfaces();
            for (int i = 0; i < classes.length; i++) {
                if (classes[i] == c2) {
                    return true;
                }
            }
        } while ((c1 = c1.getSuperclass()) != null);
        return false;
    }

    public static boolean isInheritOrImplement(Class c1, Class c2) {
        do {
            Class[] classes = c1.getInterfaces();
            if (c1 == c2) {
                return true;
            }
            for (int i = 0; i < classes.length; i++) {
                if (classes[i] == c2) {
                    return true;
                }
            }
        } while ((c1 = c1.getSuperclass()) != null);
        return false;
    }

    protected static Object getObjectByClassType(String fieldName, Class propType, Object value) {
        if (value == null || isInheritOrImplement(value.getClass(), propType)) {
            return value;
        }
        try {
            if (!(value instanceof String))
                return value;
            String s = (String) value;
            if ("".equals(s))
                return null;
            if (propType == Boolean.class || propType == Boolean.TYPE)
                return Boolean.valueOf(s);
            if (propType == Byte.class || propType == Byte.TYPE)
                return Byte.valueOf(s);
            if (propType == Double.class || propType == Double.TYPE)
                return Double.valueOf(s);
            if (propType == Float.class || propType == Float.TYPE)
                return Float.valueOf(s);
            if (propType == Integer.class || propType == Integer.TYPE)
                return Integer.valueOf(s);
            if (propType == Long.class || propType == Long.TYPE)
                return Long.valueOf(s);
            if (propType == Short.class || propType == Short.TYPE)
                return Short.valueOf(s);
            if (propType == Character.class || propType == Character.TYPE)
                return Character.valueOf(s.charAt(0));
            if (propType == java.math.BigDecimal.class)
                return new java.math.BigDecimal(s);
            if (propType == java.math.BigInteger.class)
                return new java.math.BigInteger(s);
            if (propType == Date.class)
                return DateUtil.stringToSqlDate(s);
            if (propType == java.sql.Time.class)
                return DateUtil.stringToSqlTime(s);
            if (propType == Timestamp.class) {
                return DateUtil.stringToSqlTimestamp(s.length() < 11 ? s + " 00:00:00" : s);
            }
        } catch (Exception ex) {
            throw new SysLevelException("字段: " + fieldName + "转换类型：" + propType + "原因：" + ex.getMessage());
        }
        return null;
    }

    public static void setFieldValue(Object target, String fname, Class ftype, Object fvalue) {
        if (target == null || fname == null || "".equals(fname) || (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass()))) {
            return;
        }
        Class clazz = target.getClass();
        try {
            Method method = clazz.getDeclaredMethod("set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1), new Class[]{ftype});
            if (!Modifier.isPublic(method.getModifiers())) {
                method.setAccessible(true);
            }
            method.invoke(target, new Object[]{fvalue});
        } catch (Exception me) {
            try {
                Field field = clazz.getDeclaredField(fname);
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                field.set(target, fvalue);
            } catch (Exception fe) {
            }
        }
    }

    public static Map<String, Object> convertMapKeyToString(Map<Object, Object> map) {
        Map<String, Object> m = Maps.newHashMap();
        if (!map.isEmpty()) {
            map.forEach((k, v) -> m.put(k.toString(), v));
        }
        return m;
    }

    public static Map<String, Object> convertMapValueToObject(Map<String, String> map) {
        Map<String, Object> m = Maps.newHashMap();
        if (!map.isEmpty()) {
            map.forEach((k, v) -> m.put(k, v));
        }
        return m;
    }

    public static Map<String, String> convertMapKeyValueToString(Map<Object, Object> map) {
        Map<String, String> m = Maps.newHashMap();
        if (!map.isEmpty()) {
            map.forEach((k, v) -> m.put(k.toString(), v.toString()));
        }
        return m;
    }
}
