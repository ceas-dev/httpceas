package com.http.ceas.core;

import com.http.ceas.MediaType;
import com.http.ceas.entity.Authorization;
import com.http.ceas.entity.BodyRequest;
import com.http.ceas.entity.Request;
import com.http.ceas.entity.Response;
import com.http.ceas.exception.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpClient{

    private final Request.Builder requestBuilder = new Request.Builder();
    private final HttpURL httpUrl;
    private final List<MediaType> listExpectedTypes = new ArrayList<>();
    private final List<HttpStatus> listExpectedStatus = new ArrayList<>();
    private final List<String> listExpectedHeaders = new ArrayList<>();

    private HttpClient(String url){
        this.httpUrl = HttpURL.create(url);
    }

    public static HttpClient with(String url){
        return new HttpClient(url);
    }
    

    public HttpClient setHeader(HttpHeaders headers){
        this.requestBuilder.header(headers);
        return this;
    }

    public HttpClient addHeader(String key, String value){
        this.requestBuilder.addHeader(key, value);
        return this;
    }

    public HttpClient addHeader(HttpProperty property, String value){
        this.requestBuilder.addHeader(property, value);
        return this;
    }

    public HttpClient putQuery(String key, String value){
        this.httpUrl.putQuery(key, value);
        return this;
    }

    public HttpClient putQuery(Map<String, String> queries){
        this.httpUrl.putQuery(queries);
        return this;
    }

    public HttpClient addPath(String path){
        this.httpUrl.addPath(path);
        return this;
    }

    public HttpClient body(BodyRequest bodyRequest){
        this.requestBuilder.body(bodyRequest);
        return this;
    }

    public HttpClient authorization(Authorization authorization){
        this.requestBuilder.authorization(authorization);
        return this;
    }

    public HttpClient readTimeOut(int readTimeOut){
        if(readTimeOut <= 0) return this;
        this.requestBuilder.readTimeOut(readTimeOut);
        return this;
    }

    public HttpClient connectTimeOut(int connectTimeOut){
        if(connectTimeOut <= 0) return this;
        this.requestBuilder.connectTimeOut(connectTimeOut);
        return this;
    }

    public HttpClient expectedStatus(HttpStatus... status){
        this.listExpectedStatus.addAll(Arrays.asList(status));
        return this;
    }

    public HttpClient expectedStatus(int... statusCodes){
        for(int status : statusCodes){
            this.listExpectedStatus.add(HttpStatus.valueOf(status, null));
        }
        return this;
    }

    public HttpClient expectedMediaType(MediaType... mediaTypes){
        this.listExpectedTypes.addAll(Arrays.asList(mediaTypes));
        return this;
    }

    public HttpClient exceptedHeaders(String... keys){
        this.listExpectedHeaders.addAll(Arrays.asList(keys));
        return this;
    }

    public HttpClient exceptedHeaders(HttpProperty...property){
        for(HttpProperty httpProperty : property){
            this.listExpectedHeaders.add(httpProperty.getProperty());
        }
        return this;
    }

    public HttpConnection get(){
        return turnOn(HttpMethod.GET);
    }

    public HttpConnection head(){
        return turnOn(HttpMethod.HEAD);
    }

    public HttpConnection delete(){
        return turnOn(HttpMethod.DELETE);
    }

    public HttpConnection post(){
        return turnOn(HttpMethod.POST);
    }

    public HttpConnection put(){
        return turnOn(HttpMethod.PUT);
    }

    public HttpConnection patch(){
        return turnOn(HttpMethod.PATCH);
    }

    public HttpConnection trace(){
        return turnOn(HttpMethod.TRACE);
    }

    public HttpConnection options(){
        return turnOn(HttpMethod.OPTIONS);
    }

    public HttpConnection connect(){
        return turnOn(HttpMethod.CONNECT);
    }

    public HttpConnection turnOn(HttpMethod method){
        return connection(requestBuilder.method(method));
    }

    private HttpConnection connection(Request.Builder request){
        requestBuilder.httpUrl(httpUrl);
        return HttpConnection.open(request.create(), new HttpConnection.Interceptor(){
                @Override
                public Request onRequest(Request request) throws Exception{
                    return request;
                }

                @Override
                public Response onResponse(Response response) throws Exception{
                    MediaType mediaType = response.body().mediaType();
                    HttpHeaders headers = response.headers();
                    HttpStatus status = response.status();

                    if(!listExpectedTypes.isEmpty() && !listExpectedTypes.contains(mediaType)){
                        throw new ExpectedException("the media type: " + ((mediaType == null) ? "" : mediaType.getMimeType()) + " not expected");
                    }

                    if(!listExpectedStatus.isEmpty() && !listExpectedStatus.contains(status)){
                        throw new ExpectedException("the http staus: " + status.toString() + " not expected " + listExpectedStatus.get(0).toString());
                    }

                    if(!listExpectedHeaders.isEmpty()){
                        if(headers.containsKeys(listExpectedHeaders.toArray(new String[0]))){
                            throw new ExpectedException("the http headers: " + listExpectedHeaders.toString() + " not expected");
                        }
                    }

                    return response;
                }
            }
        );
    }
}
