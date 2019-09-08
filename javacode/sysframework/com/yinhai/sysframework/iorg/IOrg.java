package com.yinhai.sysframework.iorg;

import java.io.Serializable;
import java.util.Date;

public interface IOrg extends Serializable {

	 String ORG_TYPE_ORG = "01";
	 String ORG_TYPE_DEPART = "02";
	 String ORG_TYPE_GROUP = "03";
	 String ORG_TYPE_TEAM = "04";
	 String ORG_TYPE_SUBORG = "05";
	 String ORG_LEAF_YES = "0";
	 String ORG_LEAF_NO = "1";
	 Long ORG_ROOT_ID = Long.valueOf(1L);

	 Long getOrgid();

	 String getOrgname();

	 Long getPorgid();

	 String getCostomno();

	 String getOrgtype();

	 String getOrgidpath();

	 String getOrgnamepath();

	 String getCostomnopath();

	 String getDimension();

	 Long getCreateuser();

	 Date getCreatetime();

	 Integer getSort();

	 String getYab003();

	 String getYab139();
}
