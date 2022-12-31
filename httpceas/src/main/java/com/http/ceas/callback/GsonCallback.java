package com.http.ceas.callback;

import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpStatus;
import com.http.ceas.entity.Response;
import javax.security.auth.callback.Callback;
import com.google.gson.reflect.TypeToken;

public abstract class GsonCallback<T> implements HttpCallback{

    private final TypeToken<T> typeToken;

    public GsonCallback(TypeToken<T> typeToken){
        this.typeToken = typeToken;
    }

    @Override
    public final Runnable onResponse(final Response response) throws Exception{
        final T body = response.body().toType(typeToken);

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

    public abstract void onResponse(T body, HttpStatus status, HttpHeaders headers) throws Exception;

}
