package com.yinhai.ta3.system.org.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.BaseDao;
import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.ta3.system.org.dao.PositionDao;
import com.yinhai.ta3.system.org.domain.Position;

@SuppressWarnings("unchecked")
public class PositionDaoImpl extends BaseDao<Position, Long> implements PositionDao {

    protected Class<Position> getEntityClass() {
        return Position.class;
    }

    public IPosition getPosition(Long positionid) {
        return (IPosition) super.createCriteria(Restrictions.eq("positionid", positionid)).setFetchMode("taorg", FetchMode.EAGER)
                .uniqueResult();
    }

    public List<IPosition> getUserEffectivePosition(Long userid, Date now) {
        StringBuilder hql = new StringBuilder();
        hql.append("select p from " + SysConfig.getSysConfig(Position.class.getName(), Position.class.getName()) + " p left join p.tauserpositions up")
                .append(" where up.id.tauser.userid=?").append(" and up.id.taposition.positionid=p.positionid")
                .append(" and (p.validtime is null or p.validtime>=?) and p.effective=?");

        return super.find(hql.toString(), userid, now, "0");
    }

    public IPosition getUserMainPosition(Long userid) {
        String hql = "select b from UserPosition a left join a.id.taposition b where a.id.tauser.userid=? and a.mainposition=? and b.effective=?";
        List<IPosition> list = super.find(hql, userid, "1", "0");
        if (list != null && list.size() > 0)
            return list.get(0);
        return null;
    }
}
