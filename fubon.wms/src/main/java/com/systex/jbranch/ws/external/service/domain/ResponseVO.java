package com.systex.jbranch.ws.external.service.domain;

public class ResponseVO<T> {
    private T body;
    private ErrorVO error;

    public ResponseVO() {
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public ErrorVO getError() {
        return error;
    }

    public void setError(ErrorVO error) {
        this.error = error;
    }
}
