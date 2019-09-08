package com.yinhai.abpmn.app.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;

import com.yinhai.abpmn.core.AbpmnAppAction;
import com.yinhai.sysframework.util.DateUtil;
/**
 * 流程待办任务类
 * @author wuxiaohui
 *
 */
@Namespace("/abpmn")
@Action(value = "todoTaskAction")
public class TodoTaskAction extends AbpmnAppAction {

	private RepositoryService repositoryService = getProcessEngine().getRepositoryService();
	
	private TaskService taskService = getProcessEngine().getTaskService();
	
	public String queryToDoUserTask() throws Exception {
		String userId = getDto().getUserInfo().getUserId();
		ProcessDefinitionQuery processDefinitionQuery= repositoryService.createProcessDefinitionQuery();
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByDueDateNullsLast().desc().list();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> taskMap = null;
		for (Task task : tasks) {
			taskMap = new HashMap<String, Object>();
			taskMap.put("taskId", task.getId());//任务ID
			ProcessDefinition  processDefinition = processDefinitionQuery.processDefinitionId(task.getProcessDefinitionId()).singleResult();
			taskMap.put("taskName", processDefinition.getName()+"-"+task.getName());//任务名称
			taskMap.put("taskTime", DateUtil.datetimeToString(task.getDueDate()));//到期时间
			taskMap.put("taskAssign", getUserInfoByUserId(task.getAssignee()).getName());//任务办理人
			taskMap.put("proInstanceId", task.getProcessInstanceId());//流程实例id
			list.add(taskMap);
		}
		setData("tasks", list);
		return JSON;
	}
}
