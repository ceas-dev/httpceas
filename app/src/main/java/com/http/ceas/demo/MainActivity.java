package com.http.ceas.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.http.ceas.ClientFactory;
import com.http.ceas.callback.HttpCallback;
import com.http.ceas.core.HttpConnection;
import com.http.ceas.core.HttpURL;
import com.http.ceas.core.annotation.Insert;
import com.http.ceas.core.annotation.InsertionType;
import com.http.ceas.core.annotation.Params;
import com.http.ceas.core.annotation.verbs.GET;
import com.http.ceas.entity.Response;
import com.http.ceas.core.annotation.Headers;


public class MainActivity extends Activity{

    TextView text;
    ImageView img;

    Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       text = findViewById(R.id.activitymainTextView1);
       img = findViewById(R.id.activity_mainImageView);

       /* HttpURL url = HttpURL.create(Teste.baseUrl);
        url.putQuery("@", "teste");
        url.putQuery("key", "master");
        url.putQuery("user", "carlos");
        text.setText(url.toString());*/

       Teste teste = ClientFactory.newInstance().create(Teste.class);
        teste.get("teste", "bola").then(new HttpCallback() {
            @Override
            public Runnable onResponse(final Response response) throws Exception {
                return new Runnable() {
                    @Override
                    public void run() {
                        text.setText(response.request().url());
                    }
                };
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        
        
      
        
    }

    interface Teste{

        @Insert(InsertionType.BASE_URL)
        String baseUrl = "https://google.com/";

        @GET("v2/auth/update/")
        @Params({"@:{0}"})
        @Headers({"RPEASYAPP:{1}"})
        HttpConnection get(String t, String k);
    }







} 
