package com.http.ceas.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.http.ceas.MediaType;
import com.http.ceas.core.HttpBody;
import com.http.ceas.util.FlowUtils;
import java.io.IOException;
import java.io.InputStream;

public class BodyResponse extends HttpBody{

    private BodyResponse(InputStream stream){
        super(stream);
    }

    private BodyResponse(InputStream stream, MediaType mediaType, Long size){
        super(stream, mediaType, size);
    }

    public static BodyResponse empty(){
        return create(FlowUtils.emptyStream());
    }

    public static BodyResponse create(InputStream stream){
        return new BodyResponse(stream);
    }

    public static BodyResponse create(InputStream stream, MediaType mediaType, Long size){
        return new BodyResponse(stream, mediaType, size);
    }

    @Override
    public String toString(){
        try{
            return FlowUtils.toString(stream);
        }catch(IOException e){
            return null;
        }finally{
            FlowUtils.close(stream);
        }
    }

    public byte[] toBytes() throws IOException{
        return FlowUtils.toBytes(stream);
    }


    public Bitmap toBitmap(){
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        FlowUtils.close(stream);
        return bitmap;
    }

    
    public <T> T toType(Class<T> type){
        return new Gson().fromJson(toString(), type);
    }

    public <T> T toType(TypeToken<T> typeToken){
        return new Gson().fromJson(toString(), typeToken.getType());
    }


}
