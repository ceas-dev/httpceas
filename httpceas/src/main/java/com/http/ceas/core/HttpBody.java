package com.http.ceas.core;
import com.http.ceas.MediaType;
import java.io.IOException;
import java.io.InputStream;

public abstract class HttpBody{

    protected final InputStream stream;
    protected final long size;
    protected final MediaType mediaType;

    protected HttpBody(InputStream stream){
        this(stream, null, null);
    }

    protected HttpBody(InputStream stream, MediaType mediaType, Long size){
        if(stream == null){
            throw new IllegalArgumentException("stream cannot be null");
        }
        this.stream = stream;
        this.size = resolveSize(size);
        this.mediaType = resolveMediaType(mediaType);
    }

    public long size(){
        return size;
    }
    
    public MediaType mediaType(){
        return mediaType;
    }

    public InputStream toStream(){
        return stream;
    }

    private final long resolveSize(Long size){
        try{
            return (size == null) ? stream.available() : size;
        }catch(IOException e){
            return 0;
        }
    }

    private final MediaType resolveMediaType(MediaType mediaType){
        return (mediaType == null) ? MediaType.APPLICATION_OCTET_STREAM : mediaType;
    }
}
