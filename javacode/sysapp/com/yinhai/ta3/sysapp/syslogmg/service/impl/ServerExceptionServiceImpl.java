package com.yinhai.ta3.sysapp.syslogmg.service.impl;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.log.ServerExceptionService;
import com.yinhai.sysframework.log.Taserverexceptionlog;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.syslogmg.dao.ServerExceptionLogDao;

import java.sql.Timestamp;
import java.util.List;

public class ServerExceptionServiceImpl extends BaseService implements ServerExceptionService {

    private ServerExceptionLogDao serverExceptionDao;

    public ServerExceptionLogDao getServerExceptionDao() {
        return serverExceptionDao;
    }

    public void setServerExceptionDao(ServerExceptionLogDao serverExceptionDao) {
        this.serverExceptionDao = serverExceptionDao;
    }

    public void addServerException(Taserverexceptionlog log) {
        serverExceptionDao.insert(log);
    }

    public List<Taserverexceptionlog> getList(Timestamp begin, Timestamp end) {
        List<Taserverexceptionlog> list = serverExceptionDao.getList(begin, end);
        for (Taserverexceptionlog log : list) {
            byte[] content = log.getContent();

            if (!ValidateUtil.isEmpty(content)) {
                log.setContentStr(new String(content));
            }
        }
        return list;
    }

    public PageBean getPage(String gridId, Timestamp begin, Timestamp end, int start, int limit) {
        return serverExceptionDao.getPage(gridId, begin, end, start, limit);
    }

    public Taserverexceptionlog getTaserverexceptionlog(String id) {
        Taserverexceptionlog log = serverExceptionDao.getTaserverexceptionlog(id);
        byte[] content = log.getContent();
        if (!ValidateUtil.isEmpty(content)) {
            log.setContentStr(new String(content));
        }
        return log;
    }

    public void delete(String id) {
        serverExceptionDao.delete(id);
    }

    public PageBean getPageByCount(String gridId, Timestamp begin, Timestamp end, int start, int limit) {
        return serverExceptionDao.getPageByCount(gridId, begin, end, start, limit);
    }
}
