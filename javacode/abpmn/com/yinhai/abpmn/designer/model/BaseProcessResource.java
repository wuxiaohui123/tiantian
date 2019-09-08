package com.yinhai.abpmn.designer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.impl.bpmn.behavior.BoundaryEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.ErrorEventDefinition;
import org.activiti.engine.impl.bpmn.parser.EventSubscriptionDeclaration;
import org.activiti.engine.impl.jobexecutor.TimerDeclarationImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.Lane;
import org.activiti.engine.impl.pvm.process.LaneSet;
import org.activiti.engine.impl.pvm.process.ParticipantProcess;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yinhai.abpmn.core.AbpmnAppServiceImpl;

public class BaseProcessResource extends AbpmnAppServiceImpl {

	private RuntimeService runtimeService = getRuntimeService();

	private RepositoryService repositoryService = getRepositoryService();

	private HistoryService historyService = getHistoryService();

	public ObjectNode getDiagramNode(String processInstanceId, String processDefinitionId) {
		List<String> highLightedFlows = Collections.emptyList();
		List<String> highLightedActivities = Collections.emptyList();

		Map<String, ObjectNode> subProcessInstanceMap = new HashMap();

		ProcessInstance processInstance = null;
		if (processInstanceId != null) {
			processInstance = (ProcessInstance) this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			if (processInstance == null) {
				throw new ActivitiObjectNotFoundException("Process instance could not be found");
			}
			processDefinitionId = processInstance.getProcessDefinitionId();

			List<ProcessInstance> subProcessInstances = this.runtimeService.createProcessInstanceQuery().superProcessInstanceId(processInstanceId)
					.list();
			for (ProcessInstance subProcessInstance : subProcessInstances) {
				String subDefId = subProcessInstance.getProcessDefinitionId();

				String superExecutionId = ((ExecutionEntity) subProcessInstance).getSuperExecutionId();
				ProcessDefinition subDef = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(subDefId);

				ObjectNode processInstanceJSON = new ObjectMapper().createObjectNode();
				processInstanceJSON.put("processInstanceId", subProcessInstance.getId());
				processInstanceJSON.put("superExecutionId", superExecutionId);
				processInstanceJSON.put("processDefinitionId", subDef.getId());
				processInstanceJSON.put("processDefinitionKey", subDef.getKey());
				processInstanceJSON.put("processDefinitionName", subDef.getName());

				subProcessInstanceMap.put(superExecutionId, processInstanceJSON);
			}
		}
		if (processDefinitionId == null) {
			throw new ActivitiObjectNotFoundException("No process definition id provided");
		}
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processDefinitionId);
		if (processDefinition == null) {
			throw new ActivitiException("Process definition " + processDefinitionId + " could not be found");
		}
		ObjectNode responseJSON = new ObjectMapper().createObjectNode();

		JsonNode pdrJSON = getProcessDefinitionResponse(processDefinition);
		if (pdrJSON != null) {
			responseJSON.put("processDefinition", pdrJSON);
		}
		if (processInstance != null) {
			ArrayNode activityArray = new ObjectMapper().createArrayNode();
			ArrayNode flowsArray = new ObjectMapper().createArrayNode();

			highLightedActivities = this.runtimeService.getActiveActivityIds(processInstanceId);
			highLightedFlows = getHighLightedFlows(processInstanceId, processDefinition);
			for (String activityName : highLightedActivities) {
				activityArray.add(activityName);
			}
			for (String flow : highLightedFlows) {
				flowsArray.add(flow);
			}
			responseJSON.put("highLightedActivities", activityArray);
			responseJSON.put("highLightedFlows", flowsArray);
		}
		ObjectNode participantProcessJSON;
		if (processDefinition.getParticipantProcess() != null) {
			ParticipantProcess pProc = processDefinition.getParticipantProcess();

			participantProcessJSON = new ObjectMapper().createObjectNode();
			participantProcessJSON.put("id", pProc.getId());
			if (StringUtils.isNotEmpty(pProc.getName())) {
				participantProcessJSON.put("name", pProc.getName());
			} else {
				participantProcessJSON.put("name", "");
			}
			participantProcessJSON.put("x", pProc.getX());
			participantProcessJSON.put("y", pProc.getY());
			participantProcessJSON.put("width", pProc.getWidth());
			participantProcessJSON.put("height", pProc.getHeight());

			responseJSON.put("participantProcess", participantProcessJSON);
		}
		if ((processDefinition.getLaneSets() != null) && (!processDefinition.getLaneSets().isEmpty())) {
			ArrayNode laneSetArray = new ObjectMapper().createArrayNode();
			for (Iterator<LaneSet> laneSetIterator= processDefinition.getLaneSets().iterator(); laneSetIterator.hasNext();) {
				LaneSet laneSet = (LaneSet) laneSetIterator.next();
				ArrayNode laneArray = new ObjectMapper().createArrayNode();
				if ((laneSet.getLanes() != null) && (!laneSet.getLanes().isEmpty())) {
					for (Lane lane : laneSet.getLanes()) {
						ObjectNode laneJSON = new ObjectMapper().createObjectNode();
						laneJSON.put("id", lane.getId());
						if (StringUtils.isNotEmpty(lane.getName())) {
							laneJSON.put("name", lane.getName());
						} else {
							laneJSON.put("name", "");
						}
						laneJSON.put("x", lane.getX());
						laneJSON.put("y", lane.getY());
						laneJSON.put("width", lane.getWidth());
						laneJSON.put("height", lane.getHeight());

						List<String> flowNodeIds = lane.getFlowNodeIds();
						ArrayNode flowNodeIdsArray = new ObjectMapper().createArrayNode();
						for (String flowNodeId : flowNodeIds) {
							flowNodeIdsArray.add(flowNodeId);
						}
						laneJSON.put("flowNodeIds", flowNodeIdsArray);

						laneArray.add(laneJSON);
					}
				}
				ObjectNode laneSetJSON = new ObjectMapper().createObjectNode();
				laneSetJSON.put("id", laneSet.getId());
				if (StringUtils.isNotEmpty(laneSet.getName())) {
					laneSetJSON.put("name", laneSet.getName());
				} else {
					laneSetJSON.put("name", "");
				}
				laneSetJSON.put("lanes", laneArray);

				laneSetArray.add(laneSetJSON);
			}
			if (laneSetArray.size() > 0) {
				responseJSON.put("laneSets", laneSetArray);
			}
		}
		ArrayNode sequenceFlowArray = new ObjectMapper().createArrayNode();
		ArrayNode activityArray = new ObjectMapper().createArrayNode();
		for (ActivityImpl activity : processDefinition.getActivities()) {
			getActivity(processInstanceId, activity, activityArray, sequenceFlowArray, processInstance, highLightedFlows, subProcessInstanceMap);
		}
		responseJSON.put("activities", activityArray);
		responseJSON.put("sequenceFlows", sequenceFlowArray);

		return responseJSON;
	}

	private List<String> getHighLightedFlows(String processInstanceId, ProcessDefinitionEntity processDefinition) {
		List<String> highLightedFlows = new ArrayList();

		List<HistoricActivityInstance> historicActivityInstances = ((HistoricActivityInstanceQuery) this.historyService
				.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc()).list();

		List<String> historicActivityInstanceList = new ArrayList();
		for (Iterator localIterator1 = historicActivityInstances.iterator(); localIterator1.hasNext();) {
			HistoricActivityInstance hai = (HistoricActivityInstance) localIterator1.next();
			historicActivityInstanceList.add(hai.getActivityId());
		}
		Object highLightedActivities = this.runtimeService.getActiveActivityIds(processInstanceId);
		historicActivityInstanceList.addAll((Collection) highLightedActivities);
		for (ActivityImpl activity : processDefinition.getActivities()) {
			int index = historicActivityInstanceList.indexOf(activity.getId());
			if ((index >= 0) && (index + 1 < historicActivityInstanceList.size())) {
				List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
				for (PvmTransition pvmTransition : pvmTransitionList) {
					String destinationFlowId = pvmTransition.getDestination().getId();
					if (destinationFlowId.equals(historicActivityInstanceList.get(index + 1))) {
						highLightedFlows.add(pvmTransition.getId());
					}
				}
			}
		}
		return highLightedFlows;
	}

	private void getActivity(String processInstanceId, ActivityImpl activity, ArrayNode activityArray, ArrayNode sequenceFlowArray,
			ProcessInstance processInstance, List<String> highLightedFlows, Map<String, ObjectNode> subProcessInstanceMap) {
		ObjectNode activityJSON = new ObjectMapper().createObjectNode();

		String multiInstance = (String) activity.getProperty("multiInstance");
		if ((multiInstance != null) && (!"sequential".equals(multiInstance))) {
			multiInstance = "parallel";
		}
		ActivityBehavior activityBehavior = activity.getActivityBehavior();

		Boolean collapsed = Boolean.valueOf(activityBehavior instanceof CallActivityBehavior);
		Boolean expanded = (Boolean) activity.getProperty("isExpanded");
		if (expanded != null) {
			collapsed = Boolean.valueOf(!expanded.booleanValue());
		}
		Boolean isInterrupting = null;
		if ((activityBehavior instanceof BoundaryEventActivityBehavior)) {
			isInterrupting = Boolean.valueOf(((BoundaryEventActivityBehavior) activityBehavior).isInterrupting());
		}
		for (Iterator localIterator = activity.getOutgoingTransitions().iterator(); localIterator.hasNext();) {
			PvmTransition sequenceFlow = (PvmTransition) localIterator.next();
			String flowName = (String) sequenceFlow.getProperty("name");
			boolean isHighLighted = highLightedFlows.contains(sequenceFlow.getId());

			boolean isConditional = (sequenceFlow.getProperty("condition") != null)
					&& (!((String) activity.getProperty("type")).toLowerCase().contains("gateway"));

			boolean isDefault = (sequenceFlow.getId().equals(activity.getProperty("default")))
					&& (((String) activity.getProperty("type")).toLowerCase().contains("gateway"));

			List<Integer> waypoints = ((TransitionImpl) sequenceFlow).getWaypoints();
			ArrayNode xPointArray = new ObjectMapper().createArrayNode();
			ArrayNode yPointArray = new ObjectMapper().createArrayNode();
			for (int i = 0; i < waypoints.size(); i += 2) {
				xPointArray.add((Integer) waypoints.get(i));
				yPointArray.add((Integer) waypoints.get(i + 1));
			}
			ObjectNode flowJSON = new ObjectMapper().createObjectNode();
			flowJSON.put("id", sequenceFlow.getId());
			flowJSON.put("name", flowName);
			flowJSON.put("flow", "(" + sequenceFlow.getSource().getId() + ")--" + sequenceFlow.getId() + "-->("
					+ sequenceFlow.getDestination().getId() + ")");
			if (isConditional) {
				flowJSON.put("isConditional", isConditional);
			}
			if (isDefault) {
				flowJSON.put("isDefault", isDefault);
			}
			if (isHighLighted) {
				flowJSON.put("isHighLighted", isHighLighted);
			}
			flowJSON.put("xPointArray", xPointArray);
			flowJSON.put("yPointArray", yPointArray);

			sequenceFlowArray.add(flowJSON);
		}
		
		ArrayNode nestedActivityArray = new ObjectMapper().createArrayNode();
		for (ActivityImpl nestedActivity : activity.getActivities()) {
			nestedActivityArray.add(nestedActivity.getId());
		}
		Map<String, Object> properties = activity.getProperties();
		ObjectNode propertiesJSON = new ObjectMapper().createObjectNode();
		for (String key : properties.keySet()) {
			Object prop = properties.get(key);
			if ((prop instanceof String)) {
				propertiesJSON.put(key, (String) properties.get(key));
			} else if ((prop instanceof Integer)) {
				propertiesJSON.put(key, (Integer) properties.get(key));
			} else if ((prop instanceof Boolean)) {
				propertiesJSON.put(key, (Boolean) properties.get(key));
			} else if ("initial".equals(key)) {
				ActivityImpl act = (ActivityImpl) properties.get(key);
				propertiesJSON.put(key, act.getId());
			} else if ("timerDeclarations".equals(key)) {
				ArrayList<TimerDeclarationImpl> timerDeclarations = (ArrayList) properties.get(key);
				ArrayNode timerDeclarationArray = new ObjectMapper().createArrayNode();
				if (timerDeclarations != null) {
					for (TimerDeclarationImpl timerDeclaration : timerDeclarations) {
						ObjectNode timerDeclarationJSON = new ObjectMapper().createObjectNode();

						timerDeclarationJSON.put("isExclusive", timerDeclaration.isExclusive());
						if (timerDeclaration.getRepeat() != null) {
							timerDeclarationJSON.put("repeat", timerDeclaration.getRepeat());
						}
						timerDeclarationJSON.put("retries", String.valueOf(timerDeclaration.getRetries()));
						timerDeclarationJSON.put("type", timerDeclaration.getJobHandlerType());
						timerDeclarationJSON.put("configuration", timerDeclaration.getJobHandlerConfiguration());

						timerDeclarationArray.add(timerDeclarationJSON);
					}
				}
				if (timerDeclarationArray.size() > 0) {
					propertiesJSON.put(key, timerDeclarationArray);
				}
			} else if ("eventDefinitions".equals(key)) {
				ArrayList<EventSubscriptionDeclaration> eventDefinitions = (ArrayList) properties.get(key);
				ArrayNode eventDefinitionsArray = new ObjectMapper().createArrayNode();
				if (eventDefinitions != null) {
					for (EventSubscriptionDeclaration eventDefinition : eventDefinitions) {
						ObjectNode eventDefinitionJSON = new ObjectMapper().createObjectNode();
						if (eventDefinition.getActivityId() != null) {
							eventDefinitionJSON.put("activityId", eventDefinition.getActivityId());
						}
						eventDefinitionJSON.put("eventName", eventDefinition.getEventName());
						eventDefinitionJSON.put("eventType", eventDefinition.getEventType());
						eventDefinitionJSON.put("isAsync", eventDefinition.isAsync());
						eventDefinitionJSON.put("isStartEvent", eventDefinition.isStartEvent());
						eventDefinitionsArray.add(eventDefinitionJSON);
					}
				}
				if (eventDefinitionsArray.size() > 0) {
					propertiesJSON.put(key, eventDefinitionsArray);
				}
			} else if ("errorEventDefinitions".equals(key)) {
				ArrayList<ErrorEventDefinition> errorEventDefinitions = (ArrayList) properties.get(key);
				ArrayNode errorEventDefinitionsArray = new ObjectMapper().createArrayNode();
				if (errorEventDefinitions != null) {
					for (ErrorEventDefinition errorEventDefinition : errorEventDefinitions) {
						ObjectNode errorEventDefinitionJSON = new ObjectMapper().createObjectNode();
						if (errorEventDefinition.getErrorCode() != null) {
							errorEventDefinitionJSON.put("errorCode", errorEventDefinition.getErrorCode());
						} else {
							errorEventDefinitionJSON.putNull("errorCode");
						}
						errorEventDefinitionJSON.put("handlerActivityId", errorEventDefinition.getHandlerActivityId());

						errorEventDefinitionsArray.add(errorEventDefinitionJSON);
					}
				}
				if (errorEventDefinitionsArray.size() > 0) {
					propertiesJSON.put(key, errorEventDefinitionsArray);
				}
			}
		}
		if ("callActivity".equals(properties.get("type"))) {
			CallActivityBehavior callActivityBehavior = null;
			if ((activityBehavior instanceof CallActivityBehavior)) {
				callActivityBehavior = (CallActivityBehavior) activityBehavior;
			}
			if (callActivityBehavior != null) {
				propertiesJSON.put("processDefinitonKey", callActivityBehavior.getProcessDefinitonKey());

				ArrayNode processInstanceArray = new ObjectMapper().createArrayNode();
				if (processInstance != null) {
					List<Execution> executionList = this.runtimeService.createExecutionQuery().processInstanceId(processInstanceId)
							.activityId(activity.getId()).list();
					if (!executionList.isEmpty()) {
						for (Execution execution : executionList) {
							ObjectNode processInstanceJSON = (ObjectNode) subProcessInstanceMap.get(execution.getId());
							processInstanceArray.add(processInstanceJSON);
						}
					}
				}
				if ((processInstanceArray.size() == 0) && (StringUtils.isNotEmpty(callActivityBehavior.getProcessDefinitonKey()))) {
					ProcessDefinition lastProcessDefinition = (ProcessDefinition) this.repositoryService.createProcessDefinitionQuery()
							.processDefinitionKey(callActivityBehavior.getProcessDefinitonKey()).latestVersion().singleResult();
					if (lastProcessDefinition != null) {
						ObjectNode processInstanceJSON = new ObjectMapper().createObjectNode();
						processInstanceJSON.put("processDefinitionId", lastProcessDefinition.getId());
						processInstanceJSON.put("processDefinitionKey", lastProcessDefinition.getKey());
						processInstanceJSON.put("processDefinitionName", lastProcessDefinition.getName());
						processInstanceArray.add(processInstanceJSON);
					}
				}
				if (processInstanceArray.size() > 0) {
					propertiesJSON.put("processDefinitons", processInstanceArray);
				}
			}
		}
		activityJSON.put("activityId", activity.getId());
		activityJSON.put("properties", propertiesJSON);
		if (multiInstance != null) {
			activityJSON.put("multiInstance", multiInstance);
		}
		if (collapsed.booleanValue()) {
			activityJSON.put("collapsed", collapsed);
		}
		if (nestedActivityArray.size() > 0) {
			activityJSON.put("nestedActivities", nestedActivityArray);
		}
		if (isInterrupting != null) {
			activityJSON.put("isInterrupting", isInterrupting);
		}
		activityJSON.put("x", activity.getX());
		activityJSON.put("y", activity.getY());
		activityJSON.put("width", activity.getWidth());
		activityJSON.put("height", activity.getHeight());

		activityArray.add(activityJSON);
		for (ActivityImpl nestedActivity : activity.getActivities()) {
			getActivity(processInstanceId, nestedActivity, activityArray, sequenceFlowArray, processInstance, highLightedFlows, subProcessInstanceMap);
		}
	}

	private JsonNode getProcessDefinitionResponse(ProcessDefinitionEntity processDefinition) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode pdrJSON = mapper.createObjectNode();
		pdrJSON.put("id", processDefinition.getId());
		pdrJSON.put("name", processDefinition.getName());
		pdrJSON.put("key", processDefinition.getKey());
		pdrJSON.put("version", processDefinition.getVersion());
		pdrJSON.put("deploymentId", processDefinition.getDeploymentId());
		pdrJSON.put("isGraphicNotationDefined", isGraphicNotationDefined(processDefinition));
		return pdrJSON;
	}

	private boolean isGraphicNotationDefined(ProcessDefinitionEntity processDefinition) {
		return ((ProcessDefinitionEntity) this.repositoryService.getProcessDefinition(processDefinition.getId())).isGraphicalNotationDefined();
	}
}
