package com.yinhai.sysframework.codetable.domain;


public class Aa10 extends AppCode {

    private AppCodeId id;

    public Aa10() {
    }

    public Aa10(AppCodeId id, String codeTypeDESC, String codeDESC, String yab003, String validFlag) {
        codeType = id.getCodeType();
        codeValue = id.getCodeValue();
        this.codeTypeDESC = codeTypeDESC;
        this.codeDESC = codeDESC;
        this.yab003 = yab003;
        this.validFlag = validFlag;
    }

    public Aa10(AppCodeId id, String codeTypeDESC, String codeDESC, String yab003, String validFlag, Integer ver) {
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
