package com.http.ceas.callback;

import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpStatus;
import org.json.JSONObject;

public abstract class JsonCallback extends RestCallback{

    @Override
    public final void onResponse(String body, HttpStatus status, HttpHeaders headers) throws Exception{
        onResponse(new JSONObject(body), status, headers);
    }

    public abstract void onResponse(JSONObject body, HttpStatus status, HttpHeaders headers) throws Exception;
}
