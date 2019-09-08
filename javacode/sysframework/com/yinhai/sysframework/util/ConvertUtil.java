package com.yinhai.sysframework.util;

import java.util.Map;

import com.yinhai.sysframework.iorg.IPosition;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@SuppressWarnings("rawtypes")
public class ConvertUtil {

	public static IMenu ObjectToMenu(Object object) {
		if (object instanceof IMenu) {
			return (IMenu) object;
		}
		if (object instanceof Map) {
			return JSonFactory.convertObject((Map) object, Menu.class);
		}
		return null;
	}
	public static IPosition ObjectToPosition(Object object) {
		if (object instanceof IMenu) {
			return (IPosition) object;
		}
		if (object instanceof Map) {
			return JSonFactory.convertObject((Map) object, Position.class);
		}
		return null;
	}
}
