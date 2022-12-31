package com.http.ceas.notifiers;
import android.widget.ImageView;
import android.widget.TextView;
import com.http.ceas.callback.HttpCallback;
import com.http.ceas.entity.BodyResponse;
import com.http.ceas.entity.Response;

public abstract class Notifier<Type>{
    
    protected final Type type;
    
    protected Notifier(Type type){
        this.type = type;
    }

    public static NotifyText in(TextView textView){
        return new NotifyText(textView);
    }
    
    public static NotifyImage in(ImageView imageView){
        return new NotifyImage(imageView);
    }
    
    protected static abstract class BodyParse<Body> implements HttpCallback{

        protected abstract Body onParse(BodyResponse body) throws Exception;

        protected abstract void onParseComplete(Body body);

        @Override
        public final Runnable onResponse(Response response) throws Exception{
            final Body body = onParse(response.body());
            return new Runnable(){
                @Override
                public void run(){
                    onParseComplete(body);
                }
            };
        }
    }
}
