package com.yinhai.sysframework.log;

import javax.jws.WebService;

import com.yinhai.sysframework.dto.BaseDTO;
import com.yinhai.sysframework.service.WsService;

@WebService
public interface UserLineSessionLogService extends WsService {

	 String SERVICE_KEY = "userLineSessionLogService";

	 void saveOutLineSessionLog(BaseDTO dto);

	 void saveOutLineSessionLogByParam(String sessionid, Long userid, String clientip, String serverip);

	 void saveLoginSessionLogByParam(String sessionid, Long userid, String name, String clientip, String serverip,String resource);

	 void saveLoginSessionLog(BaseDTO dto);
}
