package com.http.ceas.notifiers;
import android.widget.TextView;
import com.http.ceas.entity.BodyResponse;

public class NotifyText extends Notifier<TextView>{
    
    public NotifyText(TextView textView){
        super(textView);
    }

    public BodyParse setText(){
        return setText(null);
    }
    
    public BodyParse setText(final String defaultValue){
        return new BodyParse<String>(){
            @Override
            public void onFailure(Exception e){
                type.setText(defaultValue);
            }
            @Override
            protected String onParse(BodyResponse body) throws Exception{
                return body.toString();
            }
            @Override
            protected void onParseComplete(String body){
                type.setText(body);
            }
        };
    }

}
