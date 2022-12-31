package com.http.ceas.entity;

import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpMethod;
import com.http.ceas.core.HttpProperty;
import com.http.ceas.core.HttpStatus;
import java.net.HttpURLConnection;
import com.google.gson.Gson;
import java.io.IOException;

public class Response{

    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final BodyResponse bodyResponse;
    private final Request request;
    private final HttpURLConnection connection;

    private Response(Builder builder){
        this.httpStatus = builder.httpStatus;
        this.httpHeaders = builder.httpHeaders;
        this.bodyResponse = builder.bodyResponse;
        this.request = builder.request;
        this.connection = builder.connection;
    }

    public static Builder builder(HttpURLConnection connection){
        return new Builder(connection);
    }

    public HttpStatus status(){
        return httpStatus;
    }

    public HttpHeaders headers(){
        return httpHeaders;
    }

    public BodyResponse body(){
        return bodyResponse;
    }
    

    public Request request(){
        return request;
    }

    public void disconnect(){
        if(connection != null)
            connection.disconnect();
    }

    public boolean isSuccessful(){
        return status().isSuccess();
    }
    

    public static class Builder{

        private HttpStatus httpStatus;
        private HttpHeaders httpHeaders;
        private BodyResponse bodyResponse;
        private Request request;
        private final HttpURLConnection connection;

        public Builder(HttpURLConnection connection){
            this.connection = connection;
        }


        public Builder setHttpStatus(HttpStatus httpStatus){
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder setHttpHeaders(HttpHeaders httpHeaders){
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder setBodyResponse(BodyResponse bodyResponse){
            this.bodyResponse = bodyResponse;
            return this;
        }

        public Builder setRequest(Request request){
            this.request = request;
            return this;
        }

        public Response create(){
            return new Response(this);
        }

    }

}
