package com.yinhai.comm.test.service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class MyServiceClient {

	public static void main(String[] args) {
		//首先右键run as 运行com.hsy.server.webServiceApp类，然后再运行这段客户端代码  
        JaxWsProxyFactoryBean jwpfb = new JaxWsProxyFactoryBean();  
        jwpfb.setServiceClass(MyService.class);  
        jwpfb.setAddress("http://localhost:8888/ta3/services/myService");  
        MyService hw = (MyService) jwpfb.create();  
         
        System.out.println(hw.sayHello("123"));  
	}
}
