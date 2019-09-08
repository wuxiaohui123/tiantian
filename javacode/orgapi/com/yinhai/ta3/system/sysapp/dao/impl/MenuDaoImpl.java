package com.yinhai.ta3.system.sysapp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.dao.hibernate.BaseDao;
import com.yinhai.sysframework.event.EventSource;
import com.yinhai.sysframework.event.TaEventPublisher;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.ReflectUtil;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.system.org.domain.PermissionInfoVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.PositionAuthrity;
import com.yinhai.ta3.system.sysapp.dao.MenuDao;
import com.yinhai.ta3.system.sysapp.domain.Menu;

@SuppressWarnings("unchecked")
public class MenuDaoImpl extends BaseDao<Menu, Long> implements MenuDao {

	private String getEntityClass(String className) {
		return SysConfig.getSysConfig(className, className);
	}

	protected Class<Menu> getEntityClass() {
		return Menu.class;
	}

	public IMenu getMenu(Long menuid) {
		return (IMenu) super.get(menuid);
	}

	public List<IMenu> getUserPermissionMenus(Long userId, Date now) {
		List<IMenu> list = new ArrayList<IMenu>();
		StringBuilder hql = new StringBuilder();
		hql.append(
				"select distinct a from " + getEntityClass(Menu.class.getName()) + " a,PositionAuthrity d,"
						+ getEntityClass(Position.class.getName()) + " e,UserPosition f ").append("where ").append("f.id.tauser.userid=? ")
				.append("and e.effective=?").append("and (e.validtime is null or e.validtime >=?) ")
				.append("and e.positionid = f.id.taposition.positionid ").append("and a.effective=? ").append("and d.id.tamenu.menuid = a.menuid ")
				.append("and d.usepermission=? ").append("and a.resourcetype=? ").append(" and a.securitypolicy<>?")
				.append("and d.id.taposition.positionid = e.positionid ").append("and (d.effecttime is null or d.effecttime >= ?) ")
				.append(" and (d.auditstate=? or d.auditstate=?)");

		if (!isPortal()) {
			hql.append(" and a.syspath=?");
			hql.append("order by a.menulevel,a.sortno");
			list = super.find(hql.toString(),
					new Object[] { userId, "0", now, "0", "1", "01", "2", now, "0", "2", SysConfig.getSysConfig("curSyspathId", "sysmg") });
		} else {
			hql.append("order by a.menulevel,a.sortno");
			list = super.find(hql.toString(), new Object[] { userId, "0", now, "0", "1", "01", "2", now, "0", "2" });
		}

		list.addAll(getNoSecurityMenus());
		return list;
	}

	public List<IMenu> getEffectiveMenus() {
		if (!isPortal()) {
			return super.find("from " + getEntityClass(Menu.class.getName())
					+ " where effective=? and resourcetype=? and (securitypolicy=? or securitypolicy=?) and syspath=? order by menulevel,sortno",
					new Object[] { "0", "01", "4", "1", SysConfig.getSysConfig("curSyspathId", "sysmg") });
		}
		return super.find("from " + getEntityClass(Menu.class.getName())
				+ " where effective=? and resourcetype=? and (securitypolicy=? or securitypolicy=?)  order by menulevel,sortno", new Object[] { "0",
				"01", "4", "1" });
	}

	public List<IMenu> getEffectiveMenus(Long pmenuid) {
		IMenu menu = (IMenu) get(pmenuid);
		if (menu == null)
			return null;
		if (!isPortal()) {
			return super.find("from " + getEntityClass(Menu.class.getName())
					+ " where effective=? and menuidpath like '?%' and syspath=? order by menulevel,sortno", new Object[] { "0",
					menu.getMenuidpath(), SysConfig.getSysConfig("curSyspathId", "sysmg") });
		}
		return super.find("from " + getEntityClass(Menu.class.getName()) + " where effective=? and menuidpath like '?%' order by menulevel,sortno",
				new Object[] { "0", menu.getMenuidpath() });
	}

	public List<Menu> getAllMenuInfo() {
		if (!isPortal()) {
			return createQuery("from " + getEntityClass(Menu.class.getName()) + " where syspath=?",
					new Object[] { SysConfig.getSysConfig("curSyspathId", "sysmg") }).list();
		}
		return createQuery("from " + getEntityClass(Menu.class.getName()), new Object[0]).list();
	}

	public List<Menu> getChildMenus(Long pMenuid) {
		if (!isPortal()) {
			return createQuery("from " + getEntityClass(Menu.class.getName()) + " where pmenuid=? and syspath=? order by menulevel,sortno",
					new Object[] { pMenuid, SysConfig.getSysConfig("curSyspathId", "sysmg") }).list();
		}
		return createQuery("from " + getEntityClass(Menu.class.getName()) + " where pmenuid=? order by menulevel,sortno", new Object[] { pMenuid })
				.list();
	}

	public Menu createMenu(Menu u) {
		save(u);
		return u;
	}

	public void sortMenu(Long[] menuids) {
		Assert.notNull(menuids, "菜单不能为空");
		for (int i = 0; i < menuids.length; i++) {
			createQuery("update " + getEntityClass(Menu.class.getName()) + " m set m.sortno=? where m.menuid=?",
					new Object[] { Long.valueOf(i), menuids[i] }).executeUpdate();
		}
	}

	public void updateMenu(Menu m) {
		String nowMenuName = m.getMenuname();
		String nowEffective = m.getEffective();
		Menu oldMenu = (Menu) getMenu(m.getMenuid());
		String oldMenuName = oldMenu.getMenuname();
		String oldEffective = oldMenu.getEffective();
		List<Menu> list = createQuery("from " + getEntityClass(Menu.class.getName()) + " m where m.menuid<>? and m.menuidpath like ?||'%'",
				new Object[] { oldMenu.getMenuid(), oldMenu.getMenuidpath() }).list();
		String oldNamepath;
		String newNamepath;
		if ((oldMenuName != null) && (oldMenuName.equals(nowMenuName))) {
			ReflectUtil.copyObjectToObjectNotNull(m, oldMenu);
			update(oldMenu);

		} else {

			oldNamepath = oldMenu.getMenunamepath();
			newNamepath = oldNamepath.substring(0, oldNamepath.indexOf(oldMenu.getMenuname())) + m.getMenuname();

			ReflectUtil.copyObjectToObjectNotNull(m, oldMenu);
			oldMenu.setMenunamepath(newNamepath);
			update(oldMenu);

			for (Menu upMenu : list) {
				upMenu.setMenunamepath(upMenu.getMenunamepath().replaceFirst(oldNamepath, newNamepath));
				update(upMenu);
			}
		}

		if (!nowEffective.equals(oldEffective)) {
			for (Menu upMenu : list) {
				upMenu.setEffective(nowEffective);
				update(upMenu);
			}
		}
	}

	public void unUseMenu(Long menuid) {
		createQuery("update " + getEntityClass(Menu.class.getName()) + " m set m.effective=? and m.menuid=?", new Object[] { "1", menuid });
	}

	public void reUseMenu(Long menuid) {
		createQuery("update " + getEntityClass(Menu.class.getName()) + " m set m.effective=? and m.menuid=?", new Object[] { "0", menuid });
	}

	public void removeMenu(Long menuid) {
		IMenu m = getMenu(menuid);
		List<Menu> plist = getChildMenus(m.getPmenuid());
		if ((!ValidateUtil.isEmpty(plist)) && (plist.size() == 1)) {
			Menu pm = (Menu) getMenu(m.getPmenuid());
			pm.setIsleaf("0");
			super.update(pm);
		}
		String menuidpath = getMenu(menuid).getMenuidpath();
		List<Menu> list = createQuery("from " + getEntityClass(Menu.class.getName()) + " where menuidpath like ?||'/%' or menuidpath = ?",
				new Object[] { menuidpath, menuidpath }).list();
		for (Menu menu : list) {
			createQuery("delete from PositionAuthrity pa where pa.id.tamenu.menuid=?", new Object[] { menu.getMenuid() }).executeUpdate();
			delete(menu);
		}
	}

	public void removeMenu(Long menuid, Long userid) {
		IMenu m = getMenu(menuid);
		List<Menu> plist = getChildMenus(m.getPmenuid());
		if ((!ValidateUtil.isEmpty(plist)) && (plist.size() == 1)) {
			Menu pm = (Menu) getMenu(m.getPmenuid());
			pm.setIsleaf("0");
			super.update(pm);
		}
		String menuidpath = getMenu(menuid).getMenuidpath();
		List<Menu> list = createQuery("from " + getEntityClass(Menu.class.getName()) + " where menuidpath like ?||'/%' or menuidpath = ?",
				new Object[] { menuidpath, menuidpath }).list();

		ITimeService timeService = (ITimeService) ServiceLocator.getService("timeService");
		for (Menu menu : list) {
			List<PositionAuthrity> pas = createQuery("from PositionAuthrity pa where pa.id.tamenu.menuid=?", new Object[] { menu.getMenuid() })
					.list();
			for (PositionAuthrity pa : pas) {
				createQuery("delete from PositionAuthrity pa where pa.id.taposition.positionid=? and pa.id.tamenu.menuid=?",
						new Object[] { pa.getId().getTaposition().getPositionid(), menu.getMenuid() }).executeUpdate();
				TaEventPublisher.publishEvent(new EventSource(new PermissionInfoVO(menu.getMenuid(), pa.getId().getTaposition().getPositionid(),
						userid, timeService.getSysTimestamp(), "0", "6")), "permission_change");
			}

			delete(menu);
		}
	}

	public Set<String> getAllNeedCacheUrl() {
		List<String> list = createQuery("select url from " + getEntityClass(Menu.class.getName()) + " where iscache=?", new Object[] { "1" }).list();
		Set<String> set = new HashSet<String>();
		set.addAll(list);
		return set;
	}

	public List<IMenu> getUserPermissionMenus(Long userId, Date curdate, Long positionId) {
		List<IMenu> list = new ArrayList<IMenu>();
		StringBuilder hql = new StringBuilder();
		hql.append(
				"select distinct a from " + getEntityClass(Menu.class.getName()) + " a,PositionAuthrity d,"
						+ getEntityClass(Position.class.getName()) + " e,UserPosition f ").append("where ").append("f.id.tauser.userid=? ")
				.append(" and f.id.taposition.positionid=?").append("and e.effective=?").append("and (e.validtime is null or e.validtime >=?) ")
				.append("and e.positionid = f.id.taposition.positionid ").append("and a.effective=? ").append("and d.id.tamenu.menuid = a.menuid ")
				.append("and d.usepermission=? ").append("and a.resourcetype=? ").append(" and a.securitypolicy<>?")
				.append(" and d.id.taposition.positionid = e.positionid ").append(" and (d.effecttime is null or d.effecttime >= ?) ")
				.append(" and (d.auditstate=? or d.auditstate=?)");

		if (!isPortal()) {
			hql.append(" and a.syspath=?");
			hql.append("order by a.menulevel,a.sortno");
			list = super.find(
					hql.toString(),
					new Object[] { userId, positionId, "0", curdate, "0", "1", "01", "2", curdate, "0", "2",
							SysConfig.getSysConfig("curSyspathId", "sysmg") });
		} else {
			hql.append("order by a.menulevel,a.sortno");
			list = super.find(hql.toString(), new Object[] { userId, positionId, "0", curdate, "0", "1", "01", "2", curdate, "0", "2" });
		}
		list.addAll(getNoSecurityMenus());
		return list;
	}

	public Set<String> getEffectiveUrls(Long userId, Long positionId, Date curdate) {
		List<String> find = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		StringBuilder hql = new StringBuilder();
		hql.append(
				"select distinct '/'||c.url from " + getEntityClass(Menu.class.getName()) + " c,PositionAuthrity d,"
						+ getEntityClass(Position.class.getName()) + " e,UserPosition f ").append("where ").append(" f.id.tauser.userid=?")
				.append(" and f.id.taposition.positionid=?").append(" and e.effective=?").append(" and (e.validtime is null or e.validtime >=?) ")
				.append(" and e.positionid = f.id.taposition.positionid ").append(" and c.menuid = d.id.tamenu.menuid ")
				.append(" and d.id.taposition.positionid = e.positionid ").append(" and d.usepermission=?")
				.append(" and (d.effecttime is null or d.effecttime >= ?) ").append(" and (d.auditstate=? or d.auditstate=?)");

		if (!isPortal()) {
			hql.append(" and c.syspath=?");
			find = find(hql.toString(),
					new Object[] { userId, positionId, "0", curdate, "1", curdate, "0", "2", SysConfig.getSysConfig("curSyspathId", "sysmg") });
		} else {
			find = find(hql.toString(), new Object[] { userId, positionId, "0", curdate, "1", curdate, "0", "2" });
		}
		find.addAll(getNoSecurityUrls());
		set.addAll(find);
		return set;
	}

	public Set<String> getEffectiveUrls(Long userId, Date curdate) {
		List<String> find = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		StringBuilder hql = new StringBuilder();
		hql.append(
				"select distinct '/'||c.url from " + getEntityClass(Menu.class.getName()) + " c,PositionAuthrity d,"
						+ getEntityClass(Position.class.getName()) + " e,UserPosition f ").append("where ").append(" f.id.tauser.userid=?")
				.append(" and e.effective=?").append(" and (e.validtime is null or e.validtime >=?) ")
				.append(" and e.positionid = f.id.taposition.positionid ").append(" and c.menuid = d.id.tamenu.menuid ")
				.append(" and d.id.taposition.positionid = e.positionid ").append(" and d.usepermission=?")
				.append(" and (d.effecttime is null or d.effecttime >= ?) ").append(" and (d.auditstate=? or d.auditstate=?)");

		if (!isPortal()) {
			hql.append(" and c.syspath=?");
			find = find(hql.toString(),
					new Object[] { userId, "0", curdate, "1", curdate, "0", "2", SysConfig.getSysConfig("curSyspathId", "sysmg") });
		} else {
			find = find(hql.toString(), new Object[] { userId, "0", curdate, "1", curdate, "0", "2" });
		}

		find.addAll(getNoSecurityUrls());
		set.addAll(find);
		return set;
	}

	public IMenu getMenuByUrl(String url) {
		List<IMenu> list = find("from " + getEntityClass(Menu.class.getName()) + " m where m.url=?", new Object[] { url });
		if (!ValidateUtil.isEmpty(list)) {
			return (IMenu) list.get(0);
		}
		return null;
	}

	private List<IMenu> getNoSecurityMenus() {
		if (!isPortal()) {
			return super.find("from " + getEntityClass(Menu.class.getName()) + " m where m.effective=? and m.securitypolicy=? and m.syspath=?",
					new Object[] { "0", "4", SysConfig.getSysConfig("curSyspathId", "sysmg") });
		}
		return super
				.find("from " + getEntityClass(Menu.class.getName()) + " m where m.effective=? and m.securitypolicy=?", new Object[] { "0", "4" });
	}

	private List<String> getNoSecurityUrls() {
		if (!isPortal()) {
			return super.find("select distinct '/'||m.url from " + getEntityClass(Menu.class.getName())
					+ " m where m.effective=? and (m.securitypolicy=? or m.securitypolicy=?) and m.syspath=?", new Object[] { "0", "4", "3",
					SysConfig.getSysConfig("curSyspathId", "sysmg") });
		}
		return super.find("select distinct '/'||m.url from " + getEntityClass(Menu.class.getName())
				+ " m where m.effective=? and (m.securitypolicy=? or m.securitypolicy=?)", new Object[] { "0", "4", "3" });
	}

	private boolean isPortal() {
		return SysConfig.getSysconfigToBoolean("isPortal", false);
	}
}
