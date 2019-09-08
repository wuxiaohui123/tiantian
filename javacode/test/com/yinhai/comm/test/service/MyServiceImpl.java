package com.yinhai.comm.test.service;

import javax.jws.WebService;


@WebService(endpointInterface="com.yinhai.comm.test.service.MyService")
public class MyServiceImpl implements MyService {

	@Override
	public String sayHello(String text) {
		System.out.println("sayHello方法被调用!");
        return "Hello " + text;
	}

}
