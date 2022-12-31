package com.http.ceas.notifiers;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Bitmap;
import com.http.ceas.entity.BodyResponse;
import android.graphics.drawable.Drawable;

public class NotifyImage extends Notifier<ImageView>{

    public NotifyImage(ImageView imageView){
        super(imageView);
    }

    public BodyParse setImage(){
        return setImageBitmap(null);
    }

    public BodyParse setImageBitmap(final Bitmap defaultValue){
        return new RenderBitmap(){
            @Override
            public void onFailure(Exception e){
                type.setImageBitmap(defaultValue);
            }
        };
    }
    
    public BodyParse setImageDrawable(final Drawable defaultValue){
        return new RenderBitmap(){
            @Override
            public void onFailure(Exception e){
                type.setImageDrawable(defaultValue);
            }
        };
    }
    
    public BodyParse setImageResource(final int defaultValue){
        return new RenderBitmap(){
            @Override
            public void onFailure(Exception e){
                type.setImageResource(defaultValue);
            }
        };
    }
    

    public abstract class RenderBitmap extends BodyParse<Bitmap>{
        @Override
        protected final Bitmap onParse(BodyResponse body) throws Exception{
            return body.toBitmap();
        }
        @Override
        protected final void onParseComplete(Bitmap body){
            type.setImageBitmap(body);
        }
    }

}
