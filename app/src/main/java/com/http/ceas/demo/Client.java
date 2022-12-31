package com.http.ceas.demo;
import com.http.ceas.core.HttpConnection;
import com.http.ceas.core.annotation.Insert;
import com.http.ceas.core.annotation.InsertionType;
import com.http.ceas.core.annotation.verbs.DELETE;
import com.http.ceas.core.annotation.verbs.GET;
import com.http.ceas.core.annotation.verbs.POST;
import com.http.ceas.entity.BodyRequest;

public interface Client{

 
    @Insert(InsertionType.BASE_URL)
    String BASE_URL = "https://6259e1d3cda73d132d1af641.mockapi.io/API/teste/pessoa";

    @GET
    HttpConnection getAll();

    @GET("{0}")
    HttpConnection findById(int id);

    @POST(body = 0)
    HttpConnection create(BodyRequest body);

    @DELETE("delete/{0}")
    HttpConnection delete(int id);

}
