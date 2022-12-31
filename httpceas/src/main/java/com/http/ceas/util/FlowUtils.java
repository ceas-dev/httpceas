package com.http.ceas.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class FlowUtils{

    public static final int BUFFER_SIZE = 4096;

    public static InputStream emptyStream(){
        return new ByteArrayInputStream(new byte[0]);
    }

    public static byte[] toBytes(InputStream inputStream) throws IOException{
        if(inputStream == null){
            throw new IllegalArgumentException("inputStream cannot be null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(inputStream, baos);
        byte[] bytes = baos.toByteArray();
        close(baos);
        return bytes;
    }

    public static String toString(InputStream inputStream) throws IOException{
        return new String(toBytes(inputStream), StandardCharsets.UTF_8);
    }

    public static void write(InputStream inputStream, OutputStream outputStream) throws IOException{
        if(inputStream == null){
            throw new IllegalArgumentException("inputStream cannot be null");
        }
        if(outputStream == null){
            throw new IllegalArgumentException("outputStream cannot be null");
        }
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        int readByte;
        final byte[] BUFFER = new byte[BUFFER_SIZE];
        while((readByte = bis.read(BUFFER)) >= 0){
            bos.write(BUFFER, 0, readByte);
        }
        bos.flush();
    }

    public static void close(Closeable... closeables){
        if(closeables == null) return;
        try{
            for(Closeable closeable : closeables){
                if(closeable != null) closeable.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
