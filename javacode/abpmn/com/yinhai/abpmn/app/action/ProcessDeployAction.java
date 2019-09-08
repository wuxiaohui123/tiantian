package com.yinhai.abpmn.app.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.AllowedMethods;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;

import com.yinhai.abpmn.app.service.ProcessDeployService;
import com.yinhai.abpmn.core.AbpmnAppAction;
import com.yinhai.abpmn.domain.ProcessModel;
import com.yinhai.abpmn.util.ProcessEngineConfig;
import com.yinhai.sysframework.util.StringUtil;
/**
 * 流程定义类
 * @author wuxiaohui create by 2017-1-15
 *
 */
@Namespace("/abpmn")
@AllowedMethods({"queryProcessDefinition","downloadProcessModel","deleteProcessDefinition","viewProcessImage"})
@Action(value = "processDeployAction", results = { 
		@Result(name = "success", location = "/abpmn/processManage/processDeploy/processDeploy.jsp") })
public class ProcessDeployAction extends AbpmnAppAction {

	private File theFile;

	private String theFileContentType;

	private ProcessDeployService processDeployService = (ProcessDeployService) super.getService("processDeployService");

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	/**
	 * 查询流程定义
	 * @return
	 * @throws Exception
	 */
	public String queryProcessDefinition() throws Exception {
		List<Map<String, Object>> list = processDeployService.queryProcessDefine(getDto());
		setList("processdefineGrid", list);
		return JSON;
	}

	/**
	 * 删除流程定义
	 * @return
	 * @throws Exception
	 */
	public String deleteProcessDefinition() throws Exception {
		processDeployService.deleteProcDefine(getDto());
		setMsg("删除成功！");
		return JSON;
	}
	/**
	 * 上传流程定义文件
	 * @return
	 * @throws Exception
	 */
	public String saveFileData() throws Exception {
		if (theFileContentType.equals("text/xml")) {
			String filename = getDto().getAsString("filename");
			String uploador = getDto().getUserInfo().getUserId();
			Timestamp uploadTime = processDeployService.getSysTimestamp();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("file", theFile);
			map.put("uploador", uploador);
			map.put("uploadTime", uploadTime);
			filename = filename.substring(filename.lastIndexOf("\\") + 1, filename.lastIndexOf("."));

			map.put("filename", filename);
			InputStream ins = new FileInputStream(theFile.getPath());
			map.put("filecontent", ins);
			String errorMsg = processDeployService.saveFileData(map, getDto());
			if (StringUtil.isNotEmpty(errorMsg)) {
				setSuccess(false);
				setMsg(errorMsg);
			} else {
				String select = getDto().getAsString("openAtOnce");
				if ("on".equals(select)) {
					setSuccess(true);
					setMsg("文件导入成功并已启用！");
				} else {
					setSuccess(true);
					setMsg("文件导入成功！");
				}
			}
			theFile.delete();
		} else {
			setSuccess(false);
			setMsg("请上传以.xml为后缀的xml文件!");
		}
		return "success";
	}

	/**
	 * 下载流程定义文件和流程图
	 * @return
	 * @throws Exception
	 */
	public String downloadProcessModel() throws Exception {
		String resourceName = request.getParameter("filename");
		String deploymentId = request.getParameter("deploymentId");
		response.setContentType("application/octet-stream");
		if (ProcessEngineConfig.getProcesConfig("hasPngResource")) {
			String fileName = resourceName.substring(0, resourceName.indexOf(".")) + ".zip";
			response.addHeader("Content-Disposition", "attachment;filename = " + URLEncoder.encode(fileName, "UTF-8"));
			ZipOutputStream stream = new ZipOutputStream(response.getOutputStream());
			List<ProcessModel> list = processDeployService.getProcessModelResource(deploymentId);
			for (ProcessModel ps : list) {
				stream.putNextEntry(new ZipEntry(ps.getName()));
				stream.write(ps.getBytes());
			}
			stream.flush();
			stream.close();
		} else {
			ServletOutputStream outputStream = response.getOutputStream();
			response.addHeader("Content-Disposition", "attachment;filename = " + URLEncoder.encode(resourceName, "UTF-8"));
			Map<String, String> paramterMap = new HashMap<String, String>();
			paramterMap.put("modelName", resourceName);
			paramterMap.put("deploymentId", deploymentId);
			outputStream.write(processDeployService.getProcessModelResource(paramterMap));
			outputStream.flush();
			outputStream.close();
		}
		return null;
	}

	/**
	 * 查看流程图
	 * @return
	 * @throws Exception
	 */
	public String viewProcessImage() throws Exception {
		String deploymentId = getDto().getAsString("deploymentId");
		String imageBase64Data = "";  
		List<ProcessModel> list = processDeployService.getProcessModelResource(deploymentId);
		for (ProcessModel ps : list) {
			if(ps.getName().endsWith(".png")){
				imageBase64Data = new String(Base64.encodeBase64(ps.getBytes()));
			}
		}
		setData("image", imageBase64Data);
		return JSON;
	}
	
	public File getTheFile() {
		return theFile;
	}

	public void setTheFile(File theFile) {
		this.theFile = theFile;
	}

	public String getTheFileContentType() {
		return theFileContentType;
	}

	public void setTheFileContentType(String theFileContentType) {
		this.theFileContentType = theFileContentType;
	}
}
