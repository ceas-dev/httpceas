package com.http.ceas.entity;

import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpMethod;
import com.http.ceas.core.HttpProperty;
import com.http.ceas.core.HttpURL;

public class Request{

    private final HttpMethod httpMethod;
    private final String url;
    private final HttpHeaders httpHeaders;
    private final BodyRequest bodyRequest;
    private final Authorization authorization;
    private final int readTimeOut;
    private final int connectTimeOut;

    protected Request(Builder builder){
        this.httpMethod = builder.httpMethod;
        this.url = builder.httpUrl.toString();
        this.httpHeaders = builder.httpHeaders.create();
        this.bodyRequest = builder.bodyRequest;
        this.authorization = builder.authorization;
        this.readTimeOut = builder.readTimeOut;
        this.connectTimeOut = builder.connectTimeOut;
    }

    
    public HttpMethod method(){
        return httpMethod;
    }

    public String methodName(){
        return httpMethod.name();
    }

    public String url(){
        return url;
    }

    public HttpHeaders headers(){
        return httpHeaders;
    }

    public BodyRequest body(){
        return bodyRequest;
    }

    public Authorization authorization(){
        return authorization;
    }

    public int readTimeOut(){
        return readTimeOut;
    }

    public int connectTimeOut(){
        return connectTimeOut;
    }

    public boolean hasBody(){
        return bodyRequest != null;
    }

    public boolean hasHeaders(){
        return !httpHeaders.isEmpty();
    }

    public boolean hasAuthorization(){
        return authorization != null;
    }

    public static Builder get(String url){
        return new Builder(HttpMethod.GET, url);
    }

    public static Builder head(String url){
        return new Builder(HttpMethod.HEAD, url);
    }

    public static Builder delete(String url){
        return new Builder(HttpMethod.DELETE, url);
    }

    public static Builder post(String url){
        return new Builder(HttpMethod.POST, url);
    }

    public static Builder put(String url){
        return new Builder(HttpMethod.PUT, url);
    }

    public static Builder patch(String url){
        return new Builder(HttpMethod.PATCH, url);
    }

    public static Builder trace(String url){
        return new Builder(HttpMethod.TRACE, url);
    }

    public static Builder options(String url){
        return new Builder(HttpMethod.OPTIONS, url);
    }

    public static Builder builder(HttpMethod method, String url){
        return new Builder(method, url);
    }


    public static class Builder{

        private HttpMethod httpMethod;
        private HttpURL httpUrl;
        private HttpHeaders.Builder httpHeaders = new HttpHeaders.Builder();
        private BodyRequest bodyRequest;
        private Authorization authorization;
        private int readTimeOut;
        private int connectTimeOut;

        public Builder(HttpMethod httpMethod, HttpURL httpUrl){
            this.httpMethod = httpMethod;
            this.httpUrl = httpUrl;
        }

        public Builder(HttpMethod httpMethod, String url){
            this(httpMethod, HttpURL.create(url));
        }

        public Builder(){}

        public Builder httpUrl(HttpURL httpUrl){
            this.httpUrl = httpUrl;
            return this;
        }

        public Builder method(HttpMethod method){
            this.httpMethod = method;
            return this;
        }

        public Builder header(HttpHeaders header){
            this.httpHeaders = header.newBuilder();
            return this;
        }

        public Builder body(BodyRequest bodyRequest){
            this.bodyRequest = bodyRequest;
            return this;
        }

        public Builder authorization(Authorization authorization){
            this.authorization = authorization;
            return this;
        }

        public Builder addHeader(String key, String value){
            if(httpHeaders != null){
                this.httpHeaders.put(key, value);
            }
            return this;
        }

        public Builder addHeader(HttpProperty httpProperty, String value){
            return addHeader(httpProperty.getProperty(), value);
        }

        public Builder readTimeOut(int readTimeOut){
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder connectTimeOut(int connectTimeOut){
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        public Request create(){
            if(httpUrl == null){
                throw new IllegalArgumentException("url cannot be null");
            }
            if(httpMethod == null){
                throw new IllegalArgumentException("httpMethod cannot be null");
            }
            if(httpMethod.isRequiredBody() && bodyRequest == null){
                throw new IllegalArgumentException("the method " + httpMethod.name() + " need body");
            }
            return new Request(this);
        }
    }

}
