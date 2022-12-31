package com.http.ceas.callback;
import com.http.ceas.entity.Response;
import com.http.ceas.core.HttpStatus;
import com.http.ceas.core.HttpHeaders;
import com.http.ceas.entity.Request;

public abstract class InfoCallback implements HttpCallback{

    @Override
    public final Runnable onResponse(final Response response) throws Exception{
        return new Runnable(){
            @Override
            public void run(){
                try{
                    onResponse(response.status(), response.headers(), response.request());
                }catch(Exception e){
                    onFailure(e);
                }finally{
                    response.disconnect();
                }
            }
        };
    }

    public abstract void onResponse(HttpStatus status, HttpHeaders headers, Request request) throws Exception;


}
