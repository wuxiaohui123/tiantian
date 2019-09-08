package org.activiti.engine.impl.cmd;

import java.io.Serializable;
import java.util.Map;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

public class SetUserInfoMapCmd
  implements Command<Object>, Serializable
{
  private static final long serialVersionUID = 1L;
  protected String userId;
  protected String type;
  protected Map<String, String> userinfos;
  
  public SetUserInfoMapCmd() {}
  
  public SetUserInfoMapCmd(String userId, Map<String, String> userinfos)
  {
    this.userId = userId;
    this.type = "userinfo";
    this.userinfos = userinfos;
  }
  
  public Object execute(CommandContext commandContext)
  {
    commandContext.getIdentityInfoEntityManager().setUserInfo(this.userId, this.type, this.userinfos);
    return null;
  }
}
