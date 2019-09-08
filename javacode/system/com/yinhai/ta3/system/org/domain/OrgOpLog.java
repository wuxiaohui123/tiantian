package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;
import java.util.Date;

public class OrgOpLog implements Serializable {

	private Long logid;
	private Long batchno;
	private String optype;
	private String influencebodytype;
	private Long influencebody;
	private String opbody;
	private Long opsubjekt;
	private String changcontent;
	private Date optime;
	private Long opuser;
	private Long opposition;
	private String ispermission;

	public OrgOpLog() {
	}

	public OrgOpLog(Long batchno, String optype, String influencebodytype, Long influencebody, String opbody,
			Long opsubjekt, String changcontent, Date optime, Long opuser, Long opposition) {
		this.batchno = batchno;
		this.optype = optype;
		this.influencebodytype = influencebodytype;
		this.influencebody = influencebody;
		this.opbody = opbody;
		this.opsubjekt = opsubjekt;
		this.changcontent = changcontent;
		this.optime = optime;
		this.opuser = opuser;
		this.opposition = opposition;
	}

	public Long getLogid() {
		return logid;
	}

	public void setLogid(Long logid) {
		this.logid = logid;
	}

	public Long getBatchno() {
		return batchno;
	}

	public void setBatchno(Long batchno) {
		this.batchno = batchno;
	}

	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
	}

	public String getInfluencebodytype() {
		return influencebodytype;
	}

	public void setInfluencebodytype(String influencebodytype) {
		this.influencebodytype = influencebodytype;
	}

	public Long getInfluencebody() {
		return influencebody;
	}

	public void setInfluencebody(Long influencebody) {
		this.influencebody = influencebody;
	}

	public String getOpbody() {
		return opbody;
	}

	public void setOpbody(String opbody) {
		this.opbody = opbody;
	}

	public Long getOpsubjekt() {
		return opsubjekt;
	}

	public void setOpsubjekt(Long opsubjekt) {
		this.opsubjekt = opsubjekt;
	}

	public String getChangcontent() {
		return changcontent;
	}

	public void setChangcontent(String changcontent) {
		this.changcontent = changcontent;
	}

	public Date getOptime() {
		return optime;
	}

	public void setOptime(Date optime) {
		this.optime = optime;
	}

	public Long getOpuser() {
		return opuser;
	}

	public void setOpuser(Long opuser) {
		this.opuser = opuser;
	}

	public Long getOpposition() {
		return opposition;
	}

	public void setOpposition(Long opposition) {
		this.opposition = opposition;
	}

	public String getIspermission() {
		return ispermission;
	}

	public void setIspermission(String ispermission) {
		this.ispermission = ispermission;
	}
}
