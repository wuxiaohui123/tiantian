package com.yinhai.sysframework.app.domain;

import java.io.Serializable;
import java.util.Map;

import com.yinhai.sysframework.dto.DTO;

public interface VO extends Serializable {

    String toXMLString(String className);

    String toXML();

    String toJson();

    Map toMap();

    DTO toDTO();

    Key getKey();

    DomainMeta getMetadata();
}
