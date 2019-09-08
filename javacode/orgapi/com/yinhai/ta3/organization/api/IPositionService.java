package com.yinhai.ta3.organization.api;

import java.util.Date;

import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.system.org.domain.Position;

public interface IPositionService {

	public abstract PageBean queryPositions(Position paramPosition, Long paramLong1, Long paramLong2, boolean paramBoolean, int paramInt1,
			int paramInt2);

	public abstract Position createPosition(Position paramPosition, Long paramLong);

	public abstract boolean updatePosition(Position paramPosition, Long paramLong);

	public abstract boolean unUsePosition(Long paramLong1, Long paramLong2, Date paramDate);

	public abstract boolean reUsePosition(Long paramLong1, Long paramLong2, Date paramDate);
}
