package com.yinhai.abpmn.designer.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yinhai.abpmn.core.AbpmnAppAction;

@Namespace("/abpmn")
@AllowedMethods({"saveProcessModel"})
@Action(value="modelSaveJsonAction")
public class ModelSaveJsonAction extends AbpmnAppAction implements ModelDataJsonConstants {

	private ProcessEngine processEngine = getProcessEngine();
	
	private RepositoryService repositoryService = processEngine.getRepositoryService();

	private ObjectMapper objectMapper = new ObjectMapper();

	
	public String saveProcessModel() throws ActivitiException {	
		try {
			Model model = repositoryService.getModel(request.getParameter(MODEL_ID));

			ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

			modelJson.put("name", (String) request.getParameter("name"));
			modelJson.put("description", (String) request.getParameter("description"));
			model.setMetaInfo(modelJson.toString());
			model.setName((String) request.getParameter("name"));

			repositoryService.saveModel(model);

			repositoryService.addModelEditorSource(model.getId(), ((String) request.getParameter("json_xml")).getBytes("utf-8"));

			InputStream svgStream = new ByteArrayInputStream(((String) request.getParameter("svg_xml")).getBytes("utf-8"));
			
			TranscoderInput input = new TranscoderInput(svgStream);

			PNGTranscoder transcoder = new PNGTranscoder();

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(outStream);

			transcoder.transcode(input, output);
			byte[] result = outStream.toByteArray();
			repositoryService.addModelEditorSourceExtra(model.getId(), result);
			outStream.close();
		} catch (Exception e) {
			throw new ActivitiException("Error saving model", e);
		}
		return JSON;
	}
}
