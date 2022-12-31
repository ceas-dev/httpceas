package com.http.ceas.callback;

import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpStatus;
import com.http.ceas.entity.Response;
import javax.security.auth.callback.Callback;

public abstract class RestCallback implements HttpCallback{

    @Override
    public final Runnable onResponse(final Response response) throws Exception{
        final String body = response.body().toString();
        return new Runnable(){
            @Override
            public void run(){
                try{
                    onResponse(body, response.status(), response.headers());
                }catch(Exception e){
                    onFailure(e);
                }finally{
                    response.disconnect();
                }
            }
        };
    }

    public abstract void onResponse(String body, HttpStatus status, HttpHeaders headers) throws Exception;

}
