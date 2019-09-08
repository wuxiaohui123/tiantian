package com.yinhai.sysframework.dao.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;


public class Finder {

	private StringBuilder hqlBuilder;
	private List<String> params;
	private List<Object> values;
	private List<Type> types;
	private List<String> paramsList;
	private List<Collection<Object>> valuesList;
	private List<Type> typesList;
	private List<String> paramsArray;
	private List<Object[]> valuesArray;
	private List<Type> typesArray;

	protected Finder() {
		hqlBuilder = new StringBuilder();
	}

	protected Finder(String hql) {
		hqlBuilder = new StringBuilder(hql);
	}

	public static Finder create() {
		return new Finder();
	}

	public static Finder create(String hql) {
		return new Finder(hql);
	}

	public Finder append(String hql) {
		hqlBuilder.append(hql);
		return this;
	}

	public String getOrigHql() {
		return hqlBuilder.toString();
	}

	public String getRowCountHql() {
		String hql = hqlBuilder.toString();

		int fromIndex = hql.toLowerCase().indexOf("from");
		String projectionHql = hql.substring(0, fromIndex);

		hql = hql.substring(fromIndex);
		String rowCountHql = hql.replace("fetch", "");

		int index = rowCountHql.indexOf("order");
		if (index > 0) {
			rowCountHql = rowCountHql.substring(0, index);
		}
		return wrapProjection(projectionHql) + rowCountHql;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	public Finder setParam(String param, Object value) {
		return setParam(param, value, null);
	}

	public Finder setParam(String param, Object value, Type type) {
		getParams().add(param);
		getValues().add(value);
		getTypes().add(type);
		return this;
	}

	public Finder setParams(Map<String, Object> paramMap) {
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			setParam((String) entry.getKey(), entry.getValue());
		}
		return this;
	}

	public Finder setParamList(String name, Collection<Object> vals, Type type) {
		getParamsList().add(name);
		getValuesList().add(vals);
		getTypesList().add(type);
		return this;
	}

	public Finder setParamList(String name, Collection<Object> vals) {
		return setParamList(name, vals, null);
	}

	public Finder setParamList(String name, Object[] vals,Type type) {
		getParamsArray().add(name);
		getValuesArray().add(vals);
		getTypesArray().add(type);
		return this;
	}

	public Finder setParamList(String name, Object[] vals) {
		return setParamList(name, vals, null);
	}

	public Query setParamsToQuery(Query query) {
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				if (types.get(i) == null) {
					query.setParameter((String) params.get(i), values.get(i));
				} else {
					query.setParameter((String) params.get(i), values.get(i), (Type) types.get(i));
				}
			}
		}

		if (paramsList != null) {
			for (int i = 0; i < paramsList.size(); i++) {
				if (typesList.get(i) == null) {
					query.setParameterList((String) paramsList.get(i), (Collection) valuesList.get(i));
				} else {
					query.setParameterList((String) paramsList.get(i), (Collection) valuesList.get(i),
							(Type) typesList.get(i));
				}
			}
		}

		if (paramsArray != null) {
			for (int i = 0; i < paramsArray.size(); i++) {
				if (typesArray.get(i) == null) {
					query.setParameterList((String) paramsArray.get(i), (Object[]) valuesArray.get(i));
				} else {
					query.setParameterList((String) paramsArray.get(i), (Object[]) valuesArray.get(i),
							(Type) typesArray.get(i));
				}
			}
		}

		return query;
	}

	public Query createQuery(Session s) {
		Query query = setParamsToQuery(s.createQuery(getOrigHql()));
		if (getFirstResult() > 0) {
			query.setFirstResult(getFirstResult());
		}
		if (getMaxResults() > 0) {
			query.setMaxResults(getMaxResults());
		}
		if (isCacheable()) {
			query.setCacheable(true);
		}
		return query;
	}

	private String wrapProjection(String projection) {
		if (projection.indexOf("select") == -1) {
			return "select count(*) ";
		}
		return projection.replace("select", "select count(") + ") ";
	}

	private List<String> getParams() {
		if (params == null) {
			params = new ArrayList<String>();
		}
		return params;
	}

	private List<Object> getValues() {
		if (values == null) {
			values = new ArrayList<Object>();
		}
		return values;
	}

	private List<Type> getTypes() {
		if (types == null) {
			types = new ArrayList<Type>();
		}
		return types;
	}

	private List<String> getParamsList() {
		if (paramsList == null) {
			paramsList = new ArrayList<String>();
		}
		return paramsList;
	}

	private List<Collection<Object>> getValuesList() {
		if (valuesList == null) {
			valuesList = new ArrayList<Collection<Object>>();
		}
		return valuesList;
	}

	private List<Type> getTypesList() {
		if (typesList == null) {
			typesList = new ArrayList<Type>();
		}
		return typesList;
	}

	private List<String> getParamsArray() {
		if (paramsArray == null) {
			paramsArray = new ArrayList<String>();
		}
		return paramsArray;
	}

	private List<Object[]> getValuesArray() {
		if (valuesArray == null) {
			valuesArray = new ArrayList<Object[]>();
		}
		return valuesArray;
	}

	private List<Type> getTypesArray() {
		if (typesArray == null) {
			typesArray = new ArrayList<Type>();
		}
		return typesArray;
	}

	private int firstResult = 0;

	private int maxResults = 0;

	private boolean cacheable = false;
	public static final String ROW_COUNT = "select count(*) ";
	public static final String FROM = "from";
	public static final String DISTINCT = "distinct";
	public static final String HQL_FETCH = "fetch";
	public static final String ORDER_BY = "order";
}
