package com.yinhai.abpmn.domain;

import java.io.Serializable;

public class ProcessModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 模型id
	 */
	private String id;
	
	/**
	 * 模型资源名称
	 */
	private String name;
	
	/**
	 * 模型版本
	 */
	private String rev;
	
	/**
	 * 部署id
	 */
	private String deploymentid;
	
	/**
	 * 模型流程图
	 */
	private byte[] bytes;
	
	/**
	 * 模型创建人
	 */
	private String generated;
    
	public ProcessModel(){
	}
	
	public ProcessModel(String id, String name, String rev, String deploymentid, byte[] bytes, String generated) {
		super();
		this.id = id;
		this.name = name;
		this.rev = rev;
		this.deploymentid = deploymentid;
		this.bytes = bytes;
		this.generated = generated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getDeploymentid() {
		return deploymentid;
	}

	public void setDeploymentid(String deploymentid) {
		this.deploymentid = deploymentid;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getGenerated() {
		return generated;
	}

	public void setGenerated(String generated) {
		this.generated = generated;
	}
	
	
	
}
