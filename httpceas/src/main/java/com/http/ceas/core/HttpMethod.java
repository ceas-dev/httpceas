package com.http.ceas.core;

public enum HttpMethod{
    GET,
    POST,
    HEAD,
    OPTIONS,
    PUT,
    PATCH,
    DELETE,
    TRACE,
    CONNECT;
    public boolean isRequiredBody(){
        switch(this){
            case POST:
            case PUT:
            case PATCH:
                return true;
            default:
                return false;
        }
    }
}
