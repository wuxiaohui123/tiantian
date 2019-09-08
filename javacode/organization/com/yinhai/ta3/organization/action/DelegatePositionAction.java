package com.yinhai.ta3.organization.action;

import java.util.List;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.organization.service.IDelegatePositionService;
import com.yinhai.ta3.organization.service.IPositionMgService;
import com.yinhai.ta3.system.org.domain.PermissionTreeVO;
import com.yinhai.ta3.system.org.domain.Position;
import com.yinhai.ta3.system.org.domain.UserInfoVO;
import com.yinhai.webframework.BaseAction;

public class DelegatePositionAction extends BaseAction {

	private IDelegatePositionService delegatePositionService = (IDelegatePositionService)super.getService("delegatePositionService");
	   private IPositionMgService positionMgService = (IPositionMgService)super.getService("positionMgService");
	   
	   public String execute() throws Exception {
	     List<Position> delegateeUsers = delegatePositionService.queryDelegateeUsers(getDto().getUserInfo().getNowPosition().getPositionid());
	     setList("deletegatedGrid", delegateeUsers);
	     return "success";
	   }
	   
	   public String deletegatePosition() throws Exception { List<Key> ids = getJsonParamAsList("ids");
	     delegatePositionService.deletegatePosition(ids, getDto());
	     return "tojson";
	   }
	   
	 
	 
	   public String recycleDeletegatePosition()
	     throws Exception
	   {
	     List<Key> positionids = getJsonParamAsList("positionids");
	     delegatePositionService.recycleDeletegatePosition(positionids, getDto().getUserInfo());
	     return "tojson";
	   }
	   
	 
	 
	   public String queryPermissionInfoByPositionid()
	     throws Exception
	   {
	     Long positionid = Long.valueOf(request.getParameter("positionid"));
	     
	     List<PermissionTreeVO> nodes = positionMgService.getUsePermissionTreeByPositionId(positionid);
	     
	     Long curPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
	     List<PermissionTreeVO> curNodes = positionMgService.getRePermissionTreeByPositionId(curPositionid);
	     
	     List<PermissionTreeVO> newNodes = new java.util.ArrayList();
	     PermissionTreeVO newNode = null;
	     for (int i = 0; i < curNodes.size(); i++) {
	       newNode = (PermissionTreeVO)curNodes.get(i);
	       for (PermissionTreeVO node : nodes) {
	         if (newNode.getId().longValue() == node.getId().longValue()) {
	           if (node.getNocheck()) {
	             newNode.setNocheck(true); break;
	           }
	           newNode.setChecked(true);
	           
	           break;
	         }
	       }
	       if (i < 50) {
	         newNode.setOpen(true);
	       }
	       newNodes.add(newNode);
	     }
	     
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	     request.setAttribute("grantTree", JSonFactory.bean2json(newNodes));
	     setData("positionid", positionid);
	     setData("validtime", request.getParameter("validtime"));
	     return "permissionInfo";
	   }
	   
	 
	 
	   public String updateDeletegatePositionPermissions()
	     throws Exception
	   {
	     List<Key> ids = getJsonParamAsList("ids");
	     delegatePositionService.updateDeletegatePositionPermissions(ids, getDto());
	     return "tojson";
	   }
	   
	 
	 
	   public String addDeletegatePosition()
	     throws Exception
	   {
	     Long curPositionid = getDto().getUserInfo().getNowPosition().getPositionid();
	     List<PermissionTreeVO> curNodes = positionMgService.getRePermissionTreeByPositionId(curPositionid);
	     for (int i = 0; i < curNodes.size(); i++) {
	       if (i < 50) {
	         ((PermissionTreeVO)curNodes.get(i)).setOpen(true);
	       }
	     }
	     request.setAttribute("grantTree", JSonFactory.bean2json(curNodes));
	     List<UserInfoVO> users = delegatePositionService.queryScropOrgUsers(getDto().getUserInfo().getNowPosition().getPositionid());
	     setList("personalGrid", users);
	     return "add";
	   }
	   
	 
	 
	   public String queryDeletegatePosition()
	     throws Exception
	   {
	     List<Position> delegateeUsers = delegatePositionService.queryDelegateeUsers(getDto().getUserInfo().getNowPosition().getPositionid());
	     setList("deletegatedGrid", delegateeUsers);
	     return "tojson";
	   }
}
