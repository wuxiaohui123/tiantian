package com.yinhai.sysframework.persistence.ibatis;


public interface ICountStatement {

	long autoGetTotalCount(String paramString, Object paramObject, IDao paramIDao);
}
