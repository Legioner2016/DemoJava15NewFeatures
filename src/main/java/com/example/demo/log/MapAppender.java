package com.example.demo.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class MapAppender extends AppenderBase<ILoggingEvent> {

    public static ConcurrentMap<String, ILoggingEvent> eventMap 
      = new ConcurrentHashMap<>();

    @Override
    protected void append(ILoggingEvent event) {
        eventMap.put(String.valueOf(System.currentTimeMillis()), event);
    }
    
    public Map<String, ILoggingEvent> getEventMap() {
        return eventMap;
    }
}