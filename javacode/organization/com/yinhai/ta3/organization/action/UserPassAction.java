package com.yinhai.ta3.organization.action;

import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.sysframework.util.WebUtil;
import com.yinhai.ta3.organization.service.IUserPassChangeService;
import com.yinhai.ta3.system.org.domain.User;
import com.yinhai.webframework.BaseAction;

public class UserPassAction extends BaseAction {

	private IUserPassChangeService userPassChangeService = (IUserPassChangeService) super.getService("userPassChangeService");

	public String changePasswordWidthCurrent() throws Exception {
		if (ValidateUtil.isEmpty(getDto().getAsString("oldPass"))) {
			return JSON;
		}
		if (!"1".equals(request.getParameter("indexChangePass"))) {
			String captcha = (String) request.getSession().getAttribute("simpleCaptchaPassChange");
			if ((captcha != null) && (!captcha.equals(getDto().getAsString("checkCodePass")))) {
				setMsg("验证码错误", "error");
				return JSON;
			}
		}
		String loginid = getDto().getAsString("loginId");
		IUser user = null;
		if (ValidateUtil.isEmpty(loginid)) {
			user = WebUtil.getUserInfo(request);
		} else {
			user = userPassChangeService.getUser(loginid);
		}

		if (user == null) {
			setMsg("查无此人，请检查登录号是否正确", "error");
			return JSON;
		}
		loginid = user.getLoginid();
		if (ValidateUtil.isEmpty(user)) {
			setMsg("不存在此用户", "error");
			return JSON;
		}
		if (!userPassChangeService.encodePassword(getDto().getAsString("oldPass"), loginid).equals(((User) user).getPassword())) {
			setMsg("原始密码错误", "error");
			return JSON;
		}
		userPassChangeService.resetPassword(user.getUserid(), getDto().getAsString("newPass"));
		User user1 = (User) user;
		user1.setPassword(userPassChangeService.encodePassword(getDto().getAsString("newPassword"), loginid));
		getDto().setUserInfo(user1);
		setMsg("修改密码成功！", "success");
		return JSON;
	}
}
