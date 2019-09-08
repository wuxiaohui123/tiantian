package com.yinhai.sysframework.util;

import com.google.common.collect.Lists;
import com.yinhai.sysframework.menu.IMenu;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MenuTreeNode {

    private String id;
    private String pId;
    private String menuName;
    private String url;
    private String img;
    private String isShow;
    private Long nSeq;
    private List<MenuTreeNode> childNode;
    private MenuTreeNode parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public Long getnSeq() {
        return nSeq;
    }

    public void setnSeq(Long nSeq) {
        this.nSeq = nSeq;
    }

    public List<MenuTreeNode> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<MenuTreeNode> childNode) {
        this.childNode = childNode;
        if (null != childNode) {
            childNode.forEach(ch -> ch.setParent(this));
        }
    }

    public MenuTreeNode getParent() {
        return parent;
    }

    public void setParent(MenuTreeNode parent) {
        this.parent = parent;
    }

    public MenuTreeNode(String id, String pId, String menuName, String url, String img, String isShow, Long nSeq) {
        this.id = id;
        this.pId = pId;
        this.menuName = menuName;
        this.url = url;
        this.img = img;
        this.isShow = isShow;
        this.nSeq = nSeq;
    }

    @SuppressWarnings("rawtypes")
    public static MenuTreeNode createTree(List list) {
        if (ValidateUtil.isEmpty(list)) {
            return null;
        }
        IMenu menu = ConvertUtil.ObjectToMenu(list.get(0));
        if (menu == null) {
            return null;
        }
        String isShow = ("3".equals(menu.getSecuritypolicy()) || "2".equals(menu.getSecuritypolicy())) ? "hide" : "show";
        AtomicReference<MenuTreeNode> atomicReference = new AtomicReference<>();
        atomicReference.set(new MenuTreeNode(menu.getMenuid().toString(), menu.getPmenuid().toString(), menu.getMenuname(), menu.getUrl(), menu.getIconSkin(), isShow, menu.getSortno()));
        list.forEach(object -> {
            IMenu iMenu = ConvertUtil.ObjectToMenu(object);
            if (iMenu == null) {
                return;
            }
            String show = ("3".equals(iMenu.getSecuritypolicy()) || "2".equals(iMenu.getSecuritypolicy())) ? "hide" : "show";
            MenuTreeNode node = new MenuTreeNode(iMenu.getMenuid().toString(), iMenu.getPmenuid().toString(), iMenu.getMenuname(), iMenu.getUrl(),
                    iMenu.getIconSkin(), show, iMenu.getSortno());
            atomicReference.get().insertNode(node);
            atomicReference.set(atomicReference.get().getRoot());
        });
        return atomicReference.get();
    }


    private MenuTreeNode addChildNode(MenuTreeNode node) {
        if (null == childNode) {
            childNode = Lists.newArrayList();
        }

        if (null == node.getMenuName()) {
            setChildNode(node.getChildNode());
            node.setParent(this);
        } else {
            childNode.add(node);
            node.setParent(this);
        }
        return this;
    }

    public MenuTreeNode getRoot() {
        MenuTreeNode t = this;
        while (null != t.getParent()) {
            t = t.getParent();
        }
        return t;
    }

    public boolean insertNode(MenuTreeNode node) {
        MenuTreeNode root = getRoot();
        if (root.getId().equals(node.getpId())) {
            root.addChildNode(node);
            node.setParent(root);
            return true;
        }
        if (null != root.getpId() && root.getpId().equals(node.getId())) {
            node.addChildNode(root);
            root.setParent(node);
            return true;
        }
        if (getId().equals(node.getpId())) {
            addChildNode(node);
            node.setParent(this);
            return true;
        }
        if (null != getpId() && getpId().equals(node.getId())) {
            node.addChildNode(this);
            setParent(node);
            return true;
        }
        if (null == childNode)
            childNode = Lists.newArrayList();
        for (MenuTreeNode menuTreeNode : childNode) {
            if (menuTreeNode.insertNode(node)) {
                return true;
            }
        }
        return false;
    }
}
