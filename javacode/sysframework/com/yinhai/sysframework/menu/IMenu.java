package com.yinhai.sysframework.menu;

import java.io.Serializable;

public interface IMenu extends Serializable {

	 Long ROOT_ID = Long.valueOf(1L);
	 String DISMULTIPOS_YES = "1";
	 String DISMULTIPOS_NO = "0";
	 String SECURITYPOLICY_DISPLAY_SECURITY = "1";
	 String SECURITYPOLICY_NODISPLAY_SECURITY = "2";
	 String SECURITYPOLICY_NODISPLAY_NOSECURITY = "3";
	 String SECURITYPOLICY_DISPLAY_NOSECURITY = "4";
	 String CACHE_YES = "1";
	 String CACHE_NO = "0";
	 String AUDITE_STATE_YES = "0";
	 String AUDITE_STATE_WATE = "1";
	 String AUDITE_STATE_PASS = "2";
	 String AUDITE_STATE_FAILURE = "3";
	 String TYPE_MENU = "01";
	 String TYPE_BUTTON = "02";
	 String TYPE_FORMREAD = "03";
	 String TYPE_FORMWRITE = "04";
	 String TYPE_GRIDCOLUMNREAD = "05";
	 String TYPE_GRIDCOLUMNEDITOR = "06";
	 String MENUTYPE_COMMON = "0";
	 String MENUTYPE_SYSMG = "1";
	 String MENUTYPE_BUSINESS = "2";
	 String MENU_LEAF_NO = "1";
	 String MENU_LEAF_YES = "0";

	 Long getMenuid();

	 Long getPmenuid();

	 String getMenuname();

	 String getUrl();

	 String getMenuidpath();

	 String getMenunamepath();

	 String getAccesstimeel();

	 String getIconSkin();

	 String getSelectImage();

	 String getReportid();

	 boolean isDismultipos();

	 boolean effective();

	 String getSecuritypolicy();

	 String getQuickcode();

	 Long getSortno();

	 boolean isParent();
}
