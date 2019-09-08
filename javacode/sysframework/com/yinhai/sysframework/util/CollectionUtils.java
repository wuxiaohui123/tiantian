package com.yinhai.sysframework.util;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yinhai.sysframework.util.json.JSonFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class CollectionUtils {

    public static Collection<Object> eliminateRepetition(Collection<Object> c1) {
        List<Object> c2 = new ArrayList<Object>();
        List<Object> c3 = new ArrayList<Object>();
        c1.forEach(o -> {
            if (c2.contains(o)) {
                c3.add(o);
            } else
                c2.add(o);
        });
        c1.clear();
        c1.addAll(c2);
        return c3;
    }

    public static Collection<Object> union(Collection<?> c1, Collection<?> c2) {
        Collection<Object> c3 = unionAll(c1, c2);
        eliminateRepetition(c3);
        return c3;
    }

    public static Collection<Object> unionAll(Collection<?> c1, Collection<?> c2) {
        List<Object> c3 = new ArrayList<Object>();
        c3.addAll(c1);
        c3.addAll(c2);
        return c3;
    }

    public static Collection<Object> intersect(Collection<?> c1, Collection<?> c2) {
        Collection<Object> c3 = intersectAll(c1, c2);
        eliminateRepetition(c3);
        return c3;
    }

    public static Collection<Object> intersectAll(Collection<?> c1, Collection<?> c2) {
        List<Object> c3 = new ArrayList<Object>(c1);
        List<Object> c4 = new ArrayList<Object>(c2);
        c4.retainAll(c1);
        c3.retainAll(c2);
        c3.addAll(c4);
        return c3;
    }

    public static Collection<Object> minus(Collection<?> c1, Collection<?> c2) {
        Collection<Object> c3 = minusAll(c1, c2);
        eliminateRepetition(c3);
        return c3;
    }

    public static Collection<Object> minusAll(Collection<?> c1, Collection<?> c2) {
        List<Object> c3 = new ArrayList<Object>();
        c1.forEach(o -> {
            if (!c2.contains(o))
                c3.add(o);
        });
        return c3;
    }

    public static boolean equals(Object o1, Object o2, String[] conditionProperties) {
        if (null == o1 && null != o2 || null != o1 && null == o2)
            return false;
        if (null == o1)
            return true;
        if (null == conditionProperties || 0 == conditionProperties.length) {
            return o1.equals(o2);
        }
        Object v1 = null;
        Object v2 = null;
        for (int i = 0; i < conditionProperties.length; i++) {
            v1 = BeanUtil.getProperty(o1, conditionProperties[i]);
            v2 = BeanUtil.getProperty(o2, conditionProperties[i]);
            if ((null == v1 && null != v2) || (null != v1 && null == v2))
                return false;
            if (null != v1 && !v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, Object> getConditionKeyMap(Object o1, String[] properties) {
        if (null == o1)
            return null;
        Map<String, Object> keyMap = new HashMap<String, Object>();
        if (null == properties || 0 == properties.length) {
            keyMap.put(o1.getClass().getName(), o1);
            return keyMap;
        }
        Arrays.stream(properties).forEach(p -> {
            try {
                keyMap.put(p, BeanUtil.getProperty(o1, p));
            } catch (Exception e) {
                keyMap.put(p, null);
            }
        });
        return keyMap;
    }

    public static List<Object> getConditionKeyList(Object o1, String[] properties) {
        if (null == o1)
            return null;
        List<Object> keyList = new ArrayList<Object>();
        if (null == properties || 0 == properties.length) {
            keyList.add(o1);
            return keyList;
        }
        Arrays.stream(properties).forEach(p -> {
            try {
                keyList.add(BeanUtil.getProperty(o1, p));
            } catch (Exception e) {
                keyList.add(null);
            }
        });
        return keyList;
    }

    public static Collection<Object> eliminateRepetition(Collection<Object> c1, String[] conditionProperties) {
        if (null == conditionProperties || 0 == conditionProperties.length)
            eliminateRepetition(c1);
        Map<Map<?, ?>, Object> m2 = new HashMap<Map<?, ?>, Object>();
        List<Object> c3 = new ArrayList<Object>();
        c1.forEach(o -> {
            Map<?, ?> k = getConditionKeyMap(o, conditionProperties);
            if (m2.containsKey(k)) {
                c3.add(o);
            } else
                m2.put(k, o);
        });
        c1.clear();
        c1.addAll(m2.values());
        return c3;
    }

    public static Collection<Object> union(Collection<?> c1, Collection<?> c2, String[] conditionProperties) {
        if (null == conditionProperties || 0 == conditionProperties.length)
            union(c1, c2);
        Collection<Object> c3 = unionAll(c1, c2);
        eliminateRepetition(c3, conditionProperties);
        return c3;
    }

    public static Collection intersect(Collection<?> c1, Collection<?> c2, String[] conditionProperties) {
        if (null == conditionProperties || 0 == conditionProperties.length) {
            intersect(c1, c2);
        }
        List c4 = new ArrayList();
        c2.forEach(object -> {
            Map key = getConditionKeyMap(object, conditionProperties);
            if (!c4.contains(key)) {
                c4.add(key);
            }
        });
        Map m3 = new HashMap();
        c1.forEach(object -> {
            Map key = getConditionKeyMap(object, conditionProperties);
            if (c4.contains(key)) {
                m3.put(key, object);
            }
        });
        return m3.values();
    }

    public static Collection intersectAll(Collection c1, Collection c2, String[] conditionProperties) {
        if (null == conditionProperties || 0 == conditionProperties.length)
            intersectAll(c1, c2);
        Map m1 = Maps.newHashMap();
        AtomicReference<List> atomicReference = new AtomicReference<>();
        c1.forEach(o1 -> {
            Map key = getConditionKeyMap(o1, conditionProperties);
            atomicReference.set((List) m1.get(key));
            if (null == atomicReference.get()) {
                atomicReference.set(Lists.newArrayList());
                m1.put(key, atomicReference.get());
            }
            atomicReference.get().add(o1);
        });
        Map m2 = Maps.newHashMap();
        List c3 = Lists.newArrayList();
        AtomicReference<Object> atomicObject = new AtomicReference<>();
        c2.forEach(o2 -> {
            Map key2 = getConditionKeyMap(o2, conditionProperties);
            atomicObject.set(m1.get(key2));
            if (null != atomicObject.get()) {
                c3.add(o2);
                m2.put(key2, atomicObject.get());
            }
        });
        m2.values().forEach(object -> c3.addAll((List) object));
        return c3;
    }

    public static Collection minus(Collection c1, Collection c2, String[] conditionProperties) {
        if (null == conditionProperties || 0 == conditionProperties.length)
            minus(c1, c2);
        List c4 = Lists.newArrayList();
        c2.forEach(object -> c4.add(getConditionKeyMap(object, conditionProperties)));
        Map m3 = Maps.newHashMap();
        c1.forEach(object -> {
            Map key1 = getConditionKeyMap(object, conditionProperties);
            if (!c4.contains(key1)) {
                m3.put(key1, object);
            }
        });
        return m3.values();
    }

    public static Collection minusAll(Collection c1, Collection c2, String[] conditionProperties) {
        if (null == conditionProperties || 0 == conditionProperties.length)
            minusAll(c1, c2);
        List c4 = Lists.newArrayList();
        c2.forEach(object -> c4.add(getConditionKeyMap(object, conditionProperties)));
        List c3 = Lists.newArrayList();
        c1.forEach(object -> {
            if (!c4.contains(getConditionKeyMap(object, conditionProperties))) {
                c3.add(object);
            }
        });
        return c3;
    }

    public static Object findValueOfType(Collection coll, Class type) throws IllegalArgumentException {
        AtomicReference<Object> atomicReference = new AtomicReference<>();
        coll.forEach(object -> {
            if (type.isInstance(object)) {
                if (atomicReference.get() != null) {
                    throw new IllegalArgumentException("More than one value of type [" + type.getName() + "] found");
                }
                atomicReference.set(object);
            }
        });
        return atomicReference.get();
    }

    public static Object findValueOfType(Collection coll, Class[] types) throws IllegalArgumentException {
        Object value = null;
        for (Class type : types) {
            value = findValueOfType(coll, type);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static String mapToJson(Map map) {
        StringBuilder sb = new StringBuilder("{");
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        map.forEach((k, v) -> {
            if (atomicBoolean.get()) {
                sb.append(",");
            }
            sb.append("\"").append(k).append("\":");
            boolean noy = (v instanceof Double || v instanceof Long || v instanceof BigDecimal || v instanceof Boolean);
            boolean isString = (v instanceof String || v instanceof StringBuilder || v instanceof StringBuffer || v instanceof Date);
            if (v == null) {
                sb.append("null");
            } else if (isString || noy) {
                String valueTmp = SimpleTypeConvert.convert2String(v, "");
                if (!noy)
                    sb.append("\"");
                sb.append(JSonFactory.toJson(valueTmp));
                if (!noy)
                    sb.append("\"");
            } else {
                sb.append(JSonFactory.bean2json(v));
            }
            atomicBoolean.set(true);
        });

        sb.append("}");
        return sb.toString();
    }
}
