package com.yinhai.ta3.system.codetable;

import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.domain.AppCodeId;

public class Aa10a1 extends AppCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8175330026644296469L;
	private AppCodeId id;

	public Aa10a1() {
	}

	public Aa10a1(AppCodeId id, String codeTypeDESC, String codeDESC, String yab003, String validFlag) {
		codeType = id.getCodeType();
		codeValue = id.getCodeValue();
		this.codeTypeDESC = codeTypeDESC;
		this.codeDESC = codeDESC;
		this.yab003 = yab003;
		this.validFlag = validFlag;
	}

	public Aa10a1(AppCodeId id, String codeTypeDESC, String codeDESC, String yab003, String validFlag, Integer ver) {
		codeType = id.getCodeType();
		codeValue = id.getCodeValue();
		this.codeTypeDESC = codeTypeDESC;
		this.codeDESC = codeDESC;
		this.yab003 = yab003;
		this.validFlag = validFlag;
		this.ver = ver;
	}

	public AppCodeId getId() {
		return id;
	}

	public void setId(AppCodeId id) {
		this.id = id;
	}

	public String getCodeType() {
		return getId().getCodeType();
	}

	public String getCodeValue() {
		return getId().getCodeValue();
	}
}
