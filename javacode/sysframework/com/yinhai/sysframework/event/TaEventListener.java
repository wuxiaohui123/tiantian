package com.yinhai.sysframework.event;

import java.util.EventListener;
import java.util.concurrent.Executor;

public interface TaEventListener extends EventListener {

    void handleEvent(TaEvent taEvent);

    String getEventType();

    void setTaskExecutor(Executor executor);

    Executor getTaskExecutor();
}
