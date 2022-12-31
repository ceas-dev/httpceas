package com.http.ceas.core;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.http.ceas.MediaType;
import com.http.ceas.callback.HttpCallback;
import com.http.ceas.entity.Authorization;
import com.http.ceas.entity.BodyRequest;
import com.http.ceas.entity.BodyResponse;
import com.http.ceas.entity.Request;
import com.http.ceas.entity.Response;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public final class HttpConnection{

    private SSLSocketFactory sslSocketFactory;
    private HostnameVerifier hostnameVerifier;
    private Interceptor interceptor;
    private final Request request;

    private HttpConnection(Request request){
        this(request, null);
    }

    private HttpConnection(Request request, Interceptor interceptor){
        if(request == null){
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
        this.interceptor = interceptor;
    }

    public static HttpConnection open(Request request){
        return new HttpConnection(request);
    }

    public static HttpConnection open(Request request, Interceptor interceptor){
        return new HttpConnection(request, interceptor);
    }

    public Response execute() throws Exception{
        return startProcess(request);
    }

    public void then(final HttpCallback callback){
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable execution = new Runnable(){
            @Override
            public void run(){
                try{
                    Response response = startProcess(request);
                    handler.post(callback.onResponse(response));
                }catch(final Exception e){
                    handler.post(
                        new Runnable(){
                            @Override
                            public void run(){
                                callback.onFailure(e);
                            }
                        }
                    );
                }
            }
        };
        Executors.newSingleThreadExecutor().execute(execution);
    }

    public void notifyView(View view){
        then(new NotifyView(view));
    }

    public HttpConnection setSslSocketFactory(SSLSocketFactory sslSocketFactory){
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public HttpConnection setHostnameVerifier(HostnameVerifier hostnameVerifier){
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public SSLSocketFactory getSslSocketFactory(){
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier(){
        return hostnameVerifier;
    }

    public HttpConnection intercept(Interceptor interceptor){
        this.interceptor = interceptor;
        return this;
    }

    public interface Interceptor{
        Request onRequest(Request request) throws Exception;
        Response onResponse(Response response) throws Exception;
    }


    private Response startProcess(Request request) throws Exception{
        if(interceptor != null){
            request = interceptor.onRequest(request);
        }

        URL url = new URL(request.url());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        if(http instanceof HttpsURLConnection){
            if(sslSocketFactory == null){
                this.sslSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
            }
            if(hostnameVerifier == null){
                this.hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
            }
            ((HttpsURLConnection)http).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection)http).setHostnameVerifier(hostnameVerifier);
        }

        http.setRequestMethod(request.methodName());
        http.setReadTimeout(request.readTimeOut());
        http.setConnectTimeout(request.connectTimeOut());
        http.setDoInput(request.method() != HttpMethod.HEAD);
        http.setDoOutput(request.hasBody());
        return process(request, http);
    }

    private Response process(Request request, HttpURLConnection http) throws Exception{

        //****Request*****//

        if(request.hasHeaders()){
            HttpHeaders headers = request.headers();
            for(String key : headers.getKeys()){
                http.setRequestProperty(key, headers.get(key));
            }
        }

        if(request.hasAuthorization()){
            Authorization auto = request.authorization();
            http.setRequestProperty(
                HttpProperty.AUTHORIZATION.getProperty(),
                auto.get()
            );
        }

        if(request.hasBody()){
            BodyRequest body = request.body();
            final long size = body.size();
            if(size > 0){
                http.setFixedLengthStreamingMode(size);
                http.setRequestProperty(
                    HttpProperty.CONTENT_LENGTH.getProperty(),
                    String.valueOf(size)
                );
                http.setRequestProperty(
                    HttpProperty.CONTENT_TYPE.getProperty(),
                    body.mediaType.getMimeType()
                );
                body.write(http.getOutputStream());
            }
        }

        //****Response*****//

        HttpStatus status = HttpStatus.valueOf(http.getResponseCode(), http.getResponseMessage());

        HttpHeaders.Builder headersBuilder = HttpHeaders.builder();
        for(String key : http.getHeaderFields().keySet()){
            if(key != null && !key.isEmpty()){
                headersBuilder.put(key, http.getHeaderField(key));
            } 
        }   

        HttpHeaders headers = headersBuilder.create();

        BodyResponse body = BodyResponse.empty();

        if(http.getDoInput()){

            MediaType mediaType = null;
            Long contentLength = null;

            if(headers.hasContentType()){
                String contentType = headers.getContentType();
                mediaType = contentType.isEmpty() ? null : MediaType.create(contentType);
            }

            if(headers.hasContentLength()){
                String content = headers.getContentLength();
                contentLength = content.isEmpty() ? 0 : Long.parseLong(content);
            }

            body = BodyResponse.create(
                status.isSuccess() ? http.getInputStream() : http.getErrorStream(),
                mediaType,
                contentLength
            );
        }

        Response response = Response.builder(http)
            .setBodyResponse(body)
            .setHttpHeaders(headers)
            .setHttpStatus(status)
            .setRequest(request)
            .create();

        if(interceptor != null){
            return interceptor.onResponse(response);
        }
        return response;
    }

    private static class NotifyView implements HttpCallback{
        
        private final View view;

        public NotifyView(View view){
            this.view = view;
        }

        @Override
        public Runnable onResponse(Response response) throws Exception{
            final Object body;
            if(view instanceof TextView){
                body = response.body().toString();
            }else{
                body = response.body().toBitmap();
            }
            return new Runnable(){
                @Override
                public void run(){
                    if(view instanceof ImageView){
                        ((ImageView)view).setImageBitmap((Bitmap)body);
                    }else if(view instanceof TextView){
                        ((TextView)view).setText((String)body);
                    }else{
                        view.setBackground(new BitmapDrawable((Bitmap)body));
                    }
                }
            };
        }

        @Override
        public void onFailure(Exception e){
            Log.e("NOTIFY_VIEW", e.toString());
        }
    }

}
