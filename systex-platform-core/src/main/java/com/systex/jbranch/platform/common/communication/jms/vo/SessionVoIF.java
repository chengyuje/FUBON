package com.systex.jbranch.platform.common.communication.jms.vo;

public interface SessionVoIF {

    String getMessageSelector();
    void setMessageSelector(String value);
    
    boolean getNoLocal();
    void setNoLocal(boolean value);
    
    String getDurableSubscriptionName();
    void setDurableSubscriptionName(String value);
}
