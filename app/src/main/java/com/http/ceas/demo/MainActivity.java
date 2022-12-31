package com.http.ceas.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.http.ceas.ClientFactory;


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
        //Response t = null;
        //List<String> list = t.body().toListOf(String.class);
        try{
            client = ClientFactory.newInstance().create(Client.class);
        }catch(Exception e){
            text.setText(e.toString());
        }
    }

    @Override
    public void onBackPressed(){
        
        
        

        
    }







} 
