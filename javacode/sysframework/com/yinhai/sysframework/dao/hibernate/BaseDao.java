package com.yinhai.sysframework.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.util.Assert;

import com.yinhai.sysframework.util.ReflectUtil;

public abstract class BaseDao<T, ID extends Serializable> extends SimpleDao {

    protected Serializable saveT(T t) {
        return getSession().save(t);
    }

    protected void updateT(T t) {
        getSession().update(t);
    }

    protected T get(ID id) {
        return (T) get(id, false);
    }

    protected Object get(ID id, boolean lock) {
        Object entity;
        if (lock) {
            entity = getSession().get(getEntityClass(), id, LockOptions.UPGRADE);
        } else {
            entity = getSession().get(getEntityClass(), id);
        }
        return entity;
    }

    protected List<T> findByProperty(String property, Object value) {
        Assert.hasText(property, "");
        return createCriteria(Restrictions.eq(property, value)).list();
    }

    protected T findUniqueByProperty(String property, Object value) {
        Assert.hasText(property, "");
        Assert.notNull(value, "");
        return (T) createCriteria(Restrictions.eq(property, value)).uniqueResult();
    }

    protected int countByProperty(String property, Object value) {
        Assert.hasText(property, "");
        Assert.notNull(value, "");
        return ((Number) createCriteria(Restrictions.eq(property, value)).setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    protected List findByCriteria(Criterion... criterion) {
        return createCriteria(criterion).list();
    }

    public Object updateByUpdater(Updater<T> updater) {
        ClassMetadata cm = sessionFactory.getClassMetadata(getEntityClass());
        T bean = updater.getBean();
        Object po = getSession().get(getEntityClass(), cm.getIdentifier(bean));
        updaterCopyToPersistentObject(updater, po, cm);
        return po;
    }

    private void updaterCopyToPersistentObject(Updater<T> updater, Object po, ClassMetadata cm) {
        String[] propNames = cm.getPropertyNames();
        String identifierName = cm.getIdentifierPropertyName();
        T bean = updater.getBean();

        for (String propName : propNames) {
            if (!propName.equals(identifierName)) {
                try {
                    Object value = ReflectUtil.getFieldValue(bean, propName);
                    if (updater.isUpdate(propName, value)) {
                        cm.setPropertyValue(po, propName, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("copy property to persistent object failed: '" + propName + "'", e);
                }
            }
        }
    }

    protected Criteria createCriteria(Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(getEntityClass());
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    protected abstract Class<T> getEntityClass();
}
