package com.yinhai.sysframework.iorg;

import java.io.Serializable;

public interface IPermission extends Serializable {

	 String USEPERMISSION_NO = "0";
	 String USEPERMISSION_YES = "1";
	 String REAUTHRITY_NO = "0";
	 String REAUTHRITY_YES = "1";
	 String REPERMISSION_NO = "0";
	 String REPERMISSION_YES = "1";
	 String USEPERMISSION = "3";
	 String REPERMISSION = "4";
	 String REAUTHORITY = "5";
	 String DELETE_PERMISSION = "6";
	 String EFFECTIVETIME = "7";
	 String RETRIEVE_PERMISSION = "0";
	 String GRANT_PERMISSION = "1";
}
