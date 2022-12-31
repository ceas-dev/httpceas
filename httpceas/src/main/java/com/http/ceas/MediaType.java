package com.http.ceas;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import android.webkit.MimeTypeMap;

public class MediaType implements Serializable{

    public static final MediaType ALL;
    public static final MediaType APPLICATION_ATOM_XML;
    public static final MediaType APPLICATION_FORM_URLENCODED;
    public static final MediaType APPLICATION_GRAPHQL;
    public static final MediaType APPLICATION_JSON;
    public static final MediaType APPLICATION_OCTET_STREAM;
    public static final MediaType APPLICATION_PDF;
    public static final MediaType APPLICATION_PROBLEM_XML;
    public static final MediaType APPLICATION_PROTOBUF;
    public static final MediaType APPLICATION_RSS_XML;
    public static final MediaType APPLICATION_NDJSON;
    public static final MediaType APPLICATION_XHTML_XML;
    public static final MediaType APPLICATION_XML;
    public static final MediaType IMAGE_GIF;
    public static final MediaType IMAGE_JPEG;
    public static final MediaType IMAGE_PNG;
    public static final MediaType MULTIPART_FORM_DATA;
    public static final MediaType MULTIPART_MIXED;
    public static final MediaType MULTIPART_RELATED;
    public static final MediaType TEXT_EVENT_STREAM;
    public static final MediaType TEXT_HTML;
    public static final MediaType TEXT_MARKDOWN;
    public static final MediaType TEXT_PLAIN;
    public static final MediaType TEXT_XML;

    static {
        ALL = create("*", "*");
        APPLICATION_ATOM_XML = create("application", "atom+xml");
        APPLICATION_FORM_URLENCODED = create("application", "x-www-form-urlencoded");
        APPLICATION_GRAPHQL = create("application", "graphql+json");
        APPLICATION_JSON = create("application", "json");
        APPLICATION_NDJSON = create("application", "x-ndjson");
        APPLICATION_OCTET_STREAM = create("application", "octet-stream");
        APPLICATION_PDF = create("application", "pdf");
        APPLICATION_PROBLEM_XML = create("application", "problem+xml");
        APPLICATION_PROTOBUF = create("application", "x-protobuf");
        APPLICATION_RSS_XML = create("application", "rss+xml");
        APPLICATION_XHTML_XML = create("application", "xhtml+xml");
        APPLICATION_XML = create("application", "xml");
        IMAGE_GIF = create("image", "gif");
        IMAGE_JPEG = create("image", "jpeg");
        IMAGE_PNG = create("image", "png");
        MULTIPART_FORM_DATA = create("multipart", "form-data");
        MULTIPART_MIXED = create("multipart", "mixed");
        MULTIPART_RELATED = create("multipart", "related");
        TEXT_EVENT_STREAM = create("text", "event-stream");
        TEXT_HTML = create("text", "html");
        TEXT_MARKDOWN = create("text", "markdown");
        TEXT_PLAIN = create("text", "plain");
        TEXT_XML = create("text", "xml");
    }



    private final String mimeType;
    private static final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

    private MediaType(String mimeType){
        if(mimeType == null || mimeType.isEmpty()){
            throw new IllegalArgumentException("mimeType invalid");
        }
        this.mimeType = mimeType;
    }

    public static MediaType create(String mimeType){
        return new MediaType(mimeType);
    }

    public static MediaType create(String mimeType, String subMimeType){
        return create(mimeType + "/" + subMimeType);
    }

    @Override
    public boolean equals(Object obj){
        if(obj != null && obj instanceof MediaType){
            return ((MediaType)obj).toString().equals(mimeType);
        }
        return false;
    }
    
    
    @Override
    public String toString(){
        return mimeType;
    }
    
    public String getMimeType(){
        return mimeType;
    }
    
    public String getSubMimeType(){
        int index = mimeType.lastIndexOf("/");
        return index < 0 ? "" : mimeType.substring(index + 1);
    }

    public static MediaType valueOf(String extension){
        if(!mimeTypeMap.hasExtension(extension)){
            throw new IllegalArgumentException("extension invalid");
        }
        return create(mimeTypeMap.getMimeTypeFromExtension(extension));
    }
    
}
