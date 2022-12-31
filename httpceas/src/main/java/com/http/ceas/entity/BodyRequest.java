package com.http.ceas.entity;
import com.http.ceas.core.HttpBody;
import com.http.ceas.util.FlowUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import android.graphics.Bitmap;
import com.google.gson.reflect.TypeToken;

public class BodyRequest extends HttpBody{

    private BodyRequest(InputStream stream){
        super(stream);
    }

    public static <T> BodyRequest create(T object, Class<T> classType){
        return create(new Gson().toJson(object, classType));
    }

    public static BodyRequest create(File file) throws FileNotFoundException{
        return create(new FileInputStream(file));
    }

    public static BodyRequest create(String text){
        return create(text.getBytes(StandardCharsets.UTF_8));
    }

    public static BodyRequest create(String text, Charset charset){
        return (charset == null) ? create(text) : create(text.getBytes(charset));
    }

    public static BodyRequest create(byte[] bytes){
        return create(new ByteArrayInputStream(bytes));
    }

    public static BodyRequest create(InputStream inputStream){
        return new BodyRequest(inputStream);
    }

    public void write(OutputStream outputStream) throws IOException{
        FlowUtils.write(stream, outputStream);
        FlowUtils.close(stream, outputStream);
    }

}
