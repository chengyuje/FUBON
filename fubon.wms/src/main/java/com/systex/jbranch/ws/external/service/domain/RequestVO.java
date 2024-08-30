package com.systex.jbranch.ws.external.service.domain;

public class RequestVO<T> {
    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
