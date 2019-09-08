package com.yinhai.ta3.system.org.dao;

import java.util.Date;
import java.util.List;

import com.yinhai.sysframework.iorg.IPosition;

public interface PositionDao {

	public abstract IPosition getPosition(Long paramLong);

	public abstract List<IPosition> getUserEffectivePosition(Long paramLong, Date paramDate);

	public abstract IPosition getUserMainPosition(Long paramLong);
}
