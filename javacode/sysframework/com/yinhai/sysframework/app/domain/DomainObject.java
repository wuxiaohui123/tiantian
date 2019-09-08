package com.yinhai.sysframework.app.domain;

import com.yinhai.sysframework.exception.IllegalInputAppException;

public interface DomainObject extends VO {

	 Key getPK();

	 Key getKey();

	 void setKey(Key key);

	 String getObjId();

	 void setObjId(String str);

	 String getDomainObjectName();

	 Object[] toArray();

	 void validateData() throws IllegalInputAppException;

	 String toTabString();

	 boolean equals(Object object);
}
