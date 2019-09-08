package com.yinhai.sysframework.dao.hibernate.pagenation;

public interface Paginate {

	 int getTotalCount();

	 int getTotalPage();

	 int getPageSize();

	 int getPageNo();

	 boolean isFirstPage();

	 boolean isLastPage();

	 int getNextPage();

	 int getPrePage();
}
