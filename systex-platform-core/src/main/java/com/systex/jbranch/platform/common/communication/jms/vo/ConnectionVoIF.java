package com.systex.jbranch.platform.common.communication.jms.vo;

public interface ConnectionVoIF {

    int getAcknowledgeMode();
    void setAcknowledgeMode(int value);
    
    String getClientID();
    void setClientID(String value);
}
