package org.activiti.engine.impl;

import java.util.List;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;

public class UserQueryImpl extends AbstractQuery<UserQuery, User> implements UserQuery {
	private static final long serialVersionUID = 1L;
	protected String id;
	protected String name;
	protected String nameLike;
	protected String sex;
	protected String sexLike;
	protected String department;
	protected String departmentLike;
	protected String email;
	protected String emailLike;
	protected String phone;
	protected String phoneLike;
	protected String groupId;
	protected String procDefId;

	public UserQueryImpl() {
	}

	public UserQueryImpl(CommandContext commandContext) {
		super(commandContext);
	}

	public UserQueryImpl(CommandExecutor commandExecutor) {
		super(commandExecutor);
	}

	public UserQuery userId(String id) {
		if (id == null) {
			throw new ActivitiIllegalArgumentException("Provided id is null");
		}
		this.id = id;
		return this;
	}

	public UserQuery userName(String name) {
		if (name == null) {
			throw new ActivitiIllegalArgumentException("Provided name is null");
		}
		this.name = name;
		return this;
	}

	public UserQuery userNameLike(String nameLike) {
		if (nameLike == null) {
			throw new ActivitiIllegalArgumentException("Provided nameLike is null");
		}
		this.nameLike = nameLike;
		return this;
	}

	public UserQuery userSex(String sex) {
		if (sex == null) {
			throw new ActivitiIllegalArgumentException("Provided sex is null");
		}
		this.sex = sex;
		return this;
	}

	public UserQuery userSexLike(String sexLike) {
		if (sexLike == null) {
			throw new ActivitiIllegalArgumentException("Provided sexLike is null");
		}
		this.sexLike = sexLike;
		return this;
	}

	public UserQuery userDepartment(String department) {
		if (department == null) {
			throw new ActivitiIllegalArgumentException("Provided department is null");
		}
		this.department = department;
		return this;
	}

	public UserQuery userDepartmentLike(String departmentLike) {
		if (departmentLike == null) {
			throw new ActivitiIllegalArgumentException("Provided departmentLike is null");
		}
		this.departmentLike = departmentLike;
		return this;
	}

	public UserQuery userEmail(String email) {
		if (email == null) {
			throw new ActivitiIllegalArgumentException("Provided email is null");
		}
		this.email = email;
		return this;
	}

	public UserQuery userEmailLike(String emailLike) {
		if (emailLike == null) {
			throw new ActivitiIllegalArgumentException("Provided emailLike is null");
		}
		this.emailLike = emailLike;
		return this;
	}

	public UserQuery userPhone(String phone) {
		if (phone == null) {
			throw new ActivitiIllegalArgumentException("Provided phone is null");
		}
		this.phone = phone;
		return this;
	}

	public UserQuery userPhoneLike(String phoneLike) {
		if (phoneLike == null) {
			throw new ActivitiIllegalArgumentException("Provided phoneLike is null");
		}
		this.phoneLike = phoneLike;
		return this;
	}

	public UserQuery memberOfGroup(String groupId) {
		if (groupId == null) {
			throw new ActivitiIllegalArgumentException("Provided groupId is null");
		}
		this.groupId = groupId;
		return this;
	}

	public UserQuery potentialStarter(String procDefId) {
		if (procDefId == null) {
			throw new ActivitiIllegalArgumentException("Provided processDefinitionId is null or empty");
		}
		this.procDefId = procDefId;
		return this;
	}

	public UserQuery orderByUserId() {
		return (UserQuery) orderBy(UserQueryProperty.USER_ID);
	}

	public UserQuery orderByUserName() {
		return (UserQuery) orderBy(UserQueryProperty.NAME);
	}

	public UserQuery orderByUserSex() {
		return (UserQuery) orderBy(UserQueryProperty.SEX);
	}

	public UserQuery orderByUserDepartment() {
		return (UserQuery) orderBy(UserQueryProperty.DEPARTMENT);
	}

	public UserQuery orderByUserEmail() {
		return (UserQuery) orderBy(UserQueryProperty.EMAIL);
	}

	public UserQuery orderByUserPhone() {
		return (UserQuery) orderBy(UserQueryProperty.PHONE);
	}

	public long executeCount(CommandContext commandContext) {
		checkQueryOk();
		return commandContext.getUserIdentityManager().findUserCountByQueryCriteria(this);
	}

	public List<User> executeList(CommandContext commandContext, Page page) {
		checkQueryOk();
		return commandContext.getUserIdentityManager().findUserByQueryCriteria(this, page);
	}

	public static long getSerialversionuid() {
		return 1L;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getNameLike() {
		return this.nameLike;
	}

	public String getSex() {
		return this.sex;
	}

	public String getSexLike() {
		return this.sexLike;
	}

	public String getDepartment() {
		return this.department;
	}

	public String getDepartmentLike() {
		return this.departmentLike;
	}

	public String getEmail() {
		return this.email;
	}

	public String getEmailLike() {
		return this.emailLike;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getPhoneLike() {
		return this.phoneLike;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getProcDefId() {
		return this.procDefId;
	}
}
