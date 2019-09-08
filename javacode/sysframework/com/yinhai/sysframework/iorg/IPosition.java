package com.yinhai.sysframework.iorg;

import java.io.Serializable;
import java.util.Date;

public interface IPosition extends Serializable {

	 Long ADMIN_POSITIONID = Long.valueOf(1L);
	 String POSITION_TYPE_PUBLIC = "1";
	 String POSITION_TYPE_PERSON = "2";
	 String POSITION_TYPE_DELEGATES = "3";
	 String IS_MAIN_POSITION_YES = "1";
	 String IS_MAIN_POSITION_NO = "0";
	 String IS_CHILDRENPOSITION = "0";
	 String IS_CHILDRENORG = "0";
	 String IS_ADMIN_YES = "1";
	 String IS_ADMIN_NO = "0";
	 String IS_SHARE_POSITION_YES = "1";
	 String IS_SHARE_POSITION_NO = "0";
	 String IS_COPY_POSITION_YES = "1";
	 String IS_COPY_POSITION_NO = "0";
	 String POSITION_CATEGORY_BUSINESS = "01";
	 String POSITION_CATEGORY_JIHE = "02";
	 String POSITION_CATEGORY_SHIZHONG = "03";

	 Long getPositionid();

	 Long getOrgid();

	 String getPositionname();

	 String getPositiontype();

	 Long getCreatepositionid();

	 String getOrgidpath();

	 String getOrgnamepath();

	 Date getValidtime();

	 Date getCreatetime();

	 Long getCreateuser();

	 boolean isPerson();

	 boolean isPublicPosition();

	 boolean isDelegatesPosition();
	
}
