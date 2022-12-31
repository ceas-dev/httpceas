package com.http.ceas.callback;

import com.http.ceas.entity.Response;

public interface HttpCallback{
    Runnable onResponse(final Response response) throws Exception;
    void onFailure(Exception e);
}
