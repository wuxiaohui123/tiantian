package com.yinhai.sysframework.time;

import java.sql.Timestamp;
import java.util.Date;

import javax.jws.WebService;

@WebService
public interface ITimeService {

    String SERVICEKEY = "timeService";

    Timestamp getSysTimestamp();

    String getSysStrTimestamp();

    Date getSysDate();

    String getSysStrDate();
}
