package com.yinhai.ta3.organization.service;

import com.yinhai.sysframework.iorg.IOrg;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface IOrgOpLogService {

	public static final String SERVICEKEY = "orgOpLogService";
	public static final String OPTYPE_CREATE_ORG = "01";
	public static final String OPTYPE_UPDATE_ORG = "02";
	public static final String OPTYPE_UNUSE_ORG = "03";
	public static final String OPTYPE_CREATE_USER = "04";
	public static final String OPTYPE_UPDATE_USER = "05";
	public static final String OPTYPE_UNUSE_USER = "06";
	public static final String OPTYPE_PASSWORD_RESET = "07";
	public static final String OPTYPE_ATTACH_POSITION_TO_USER = "08";
	public static final String OPTYPE_SET_MAINPOSITION_TO_USER = "09";
	public static final String OPTYPE_CREATE_POSITION = "10";
	public static final String OPTYPE_EDIT_POSITION = "11";
	public static final String OPTYPE_UNUSE_POSITION = "12";
	public static final String OPTYPE_GRANTTO_POSITION_USEPERMISSION = "13";
	public static final String OPTYPE_RETRIVE_POSITION_USEPERMISSION = "14";
	public static final String OPTYPE_USE_POSITION = "15";
	public static final String OPTYPE_USE_USER = "16";
	public static final String OPTYPE_USE_ORG = "17";
	public static final String OPTYPE_DELETE_ORG = "18";
	public static final String OPTYPE_GRANTTO_POSITION_REPERMISSION = "19";
	public static final String OPTYPE_RETRIVE_POSITION_REPERMISSION = "20";
	public static final String OPTYPE_GRANTTO_POSITION_REAUTHRITY = "21";
	public static final String OPTYPE_RETRIVE_POSITION_REAUTHRITY = "22";
	public static final String OPTYPE_REMOVE_ADMINISTRATOR = "23";
	public static final String OPTYPE_ADD_ADMINISTRATOR = "24";
	public static final String OPTYPE_DELETE_POSITION = "25";
	public static final String OPTYPE_DELETE_USER = "26";
	public static final String OPOBJECTTYPE_ORG = "01";
	public static final String OPOBJECTTYPE_USER = "02";
	public static final String OPOBJECTTYPE_POSITION = "03";
	public static final String OPOBJECTTYPE_PERMISSTION = "04";

	public abstract Long logOrgOp(Long paramLong, IUser paramIUser, String paramString1, IOrg paramIOrg,
			String paramString2);

	public abstract Long logUserOp(Long paramLong, IUser paramIUser1, String paramString1, IUser paramIUser2,
			String paramString2);

	public abstract Long logPositionOp(Long paramLong, IUser paramIUser, String paramString1, IPosition paramIPosition,
			String paramString2);

	public abstract Long logPermisstionOp(Long paramLong, IUser paramIUser, String paramString, Menu paramMenu,
			IPosition paramIPosition);
}
