package com.http.ceas;

import android.util.Log;
import com.http.ceas.core.HttpClient;
import com.http.ceas.core.HttpConnection;
import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpMethod;
import com.http.ceas.core.HttpProperty;
import com.http.ceas.core.HttpStatus;
import com.http.ceas.core.annotation.Headers;
import com.http.ceas.core.annotation.Insert;
import com.http.ceas.core.annotation.Params;
import com.http.ceas.core.annotation.verbs.CONNECT;
import com.http.ceas.core.annotation.verbs.DELETE;
import com.http.ceas.core.annotation.verbs.GET;
import com.http.ceas.core.annotation.verbs.HEAD;
import com.http.ceas.core.annotation.verbs.OPTIONS;
import com.http.ceas.core.annotation.verbs.PATCH;
import com.http.ceas.core.annotation.verbs.POST;
import com.http.ceas.core.annotation.verbs.PUT;
import com.http.ceas.core.annotation.verbs.TRACE;
import com.http.ceas.entity.Authorization;
import com.http.ceas.entity.BodyRequest;
import com.http.ceas.util.ReflectionUtils;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientFactory{

    private final String baseUrl;

    private ClientFactory(String baseUrl){
        this.baseUrl = baseUrl;
    }

    public static ClientFactory newInstance(){
        return new ClientFactory(null);
    }

    public static ClientFactory newInstance(String baseUrl){
        return new ClientFactory(baseUrl);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type){
        if(type == null){
            throw new IllegalArgumentException(type.getSimpleName() + " cannot be null");
        }
        if(!type.isInterface()){
            throw new IllegalArgumentException(type.getSimpleName() + " need interface");
        }
        return (T) Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class[]{type},
            new AssembleClient(baseUrl, type)
        );
    }


    private static class AssembleClient implements InvocationHandler{

        private static final String PATTERN = "\\{\\d+\\}";

        private final static class VerbProperty{
            final String path;
            final int body;

            public VerbProperty(String path, int body){
                this.path = path;
                this.body = body;
            }
        }

        private final static Class[] VERBS = new Class[]{
            DELETE.class, GET.class, HEAD.class, OPTIONS.class,
            PATCH.class, POST.class, PUT.class, CONNECT.class, TRACE.class
        };

        private final HttpClient client;

        public AssembleClient(String baseUrl, Class<?> type){
            this.client = initClient(baseUrl, type);
        }

        private static HttpClient initClient(String baseUrl, Class<?> type){
            HttpStatus[] status = null;
            HttpProperty[] headers = null;
            MediaType[] mediaType = null;
            Integer readTimeOut = null;
            Integer connectTimeOut = null;

            for(Field field : type.getFields()){
                if(!field.isAnnotationPresent(Insert.class)) continue;
                Object object = getObjectFieldStatic(field);
                switch(field.getAnnotation(Insert.class).value()){
                    case BASE_URL:{
                            if(baseUrl != null) break;
                            if(object instanceof String){
                                baseUrl = (String) object;
                            }else{
                                throw new IllegalArgumentException("The " + field.getName() + " must be String");
                            }
                            break;
                        }
                    case EXPECTED_STATUS:{
                            if(object instanceof HttpStatus[]){
                                status = (HttpStatus[]) object;
                            }else if(object instanceof HttpStatus){
                                status = new HttpStatus[]{(HttpStatus)object};
                            }else{
                                throw new IllegalArgumentException("The " + field.getName() + " must be HttpStatus");
                            }
                            break;
                        }
                    case EXPECTED_HEADERS:{
                            if(object instanceof HttpProperty[]){
                                headers = (HttpProperty[])object;
                            }else if(object instanceof HttpProperty){
                                headers = new HttpProperty[]{(HttpProperty)object};
                            }else{
                                throw new IllegalArgumentException("The " + field.getName() + " must be HttpProperty");
                            }
                            break;
                        }
                    case EXPECTED_MEDIA_TYPE:{
                            if(object instanceof MediaType[]){
                                mediaType = (MediaType[])object;
                            }else if(object instanceof MediaType){
                                mediaType = new MediaType[]{(MediaType)object};
                            }else{
                                throw new IllegalArgumentException("The " + field.getName() + " must be MediaType");
                            }
                            break;
                        }
                    case READ_TIMEOUT:{
                            if(object instanceof Integer){
                                readTimeOut = (Integer) object;
                            }else{
                                throw new IllegalArgumentException("The " + field.getName() + " must be Integer");
                            }
                            break;
                        }
                    case CONNECT_TIMEOUT:{
                            if(object instanceof Integer){
                                connectTimeOut = (Integer) object;
                            }else{
                                throw new IllegalArgumentException("The " + field.getName() + " must be Integer");
                            }
                            break;
                        }
                }
            }
            HttpClient client = HttpClient.with(baseUrl);
            if(status != null)
                client.expectedStatus(status);
            if(headers != null)
                client.exceptedHeaders(headers);
            if(mediaType != null)
                client.expectedMediaType(mediaType);
            if(readTimeOut != null)
                client.readTimeOut(readTimeOut);
            if(connectTimeOut != null)
                client.connectTimeOut(connectTimeOut);
            return client;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
            if(isHttpMethod(method)){
                HttpHeaders headers = resolveHeaders(method, args);
                Map<String, String> params = resolveParams(method, args);
                HttpMethod httpMethod = resolveHttpMethod(method);
                String path = resolvePath(method, httpMethod, args);
                BodyRequest body = resolveBody(method, httpMethod, args);

                if(headers != null){
                    client.setHeader(headers);
                }
                if(params != null){
                    client.putQuery(params);
                }
                if(path != null){
                    client.addPath(path);
                }
                if(body != null){
                    client.body(body);
                }
                return client.turnOn(httpMethod);
            }else{
                return method.invoke(proxy, args);
            }
        }

        private static Object getObjectFieldStatic(Field field){
            try{
                return field.get(null);
            }catch(IllegalArgumentException e){
                throw new IllegalArgumentException(e);
            }catch(IllegalAccessException e){
                throw new IllegalArgumentException(e);
            }
        }

        private static boolean isHttpMethod(Method method){
            return ReflectionUtils.containsAnnotation(method, VERBS) && ReflectionUtils.checkReturnType(method, HttpConnection.class);
        }

        private static HttpMethod resolveHttpMethod(Method method){
            return HttpMethod.valueOf(
                ReflectionUtils.getFirstAnnotation(method, VERBS).annotationType().getSimpleName()
            );
        }

        private static String resolvePath(Method method, HttpMethod httpMethod, Object[] args){
            String path = getVerbProperty(method, httpMethod).path;
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(path);
            while(matcher.find()){
                String group = matcher.group();
                Object argument = resolveArgument(group, args);
                if(argument == null){
                    throw new NullPointerException("The path cannot be null");
                }
                path = path.replace(group, (String)argument);
            }
            return path;
        }

        private static BodyRequest resolveBody(Method method, HttpMethod httpMethod, Object[] args){
            int index = getVerbProperty(method, httpMethod).body;
            if(index < 0){
                return null;
            }else if(index >= args.length){
                throw new IllegalArgumentException("The index: " + index + " not found in body");
            }else{
                Object body = args [index];
                if(body instanceof Byte[]){
                    return BodyRequest.create((byte[])body);
                }else if(body instanceof String){
                    return BodyRequest.create((String)body);
                }else if(body instanceof InputStream){
                    return BodyRequest.create((InputStream)body);
                }else if(body instanceof BodyRequest){
                    return (BodyRequest)body;
                }else{
                    throw new IllegalArgumentException("The argument in: " + index + " cannot accept body"); 
                }
            }
        }

        private static HttpHeaders resolveHeaders(Method method, Object[] args){
            Headers headers = ReflectionUtils.getAnnotation(method, Headers.class);
            return (headers == null) ? null : HttpHeaders.builder(decodeValues(headers.value(), args)).create();
        }

        private static Map<String, String> resolveParams(Method method, Object[] args){
            Params params = ReflectionUtils.getAnnotation(method, Params.class);
            return (params == null) ? null : decodeValues(params.value(), args);
        }

        private static Map<String, String> decodeValues(String[] values, Object[] args){
            Map<String, String> linkedMap = new LinkedHashMap<>();
            if(values == null || values.length == 0) return linkedMap;
            for(String keyAndValue : values){
                String[] array = keyAndValue.split(":");
                String key = array [0].trim();
                String value = array [1].trim();
                if(value.matches(PATTERN)){
                    Object argument = resolveArgument(value, array);
                    if(argument instanceof String || argument instanceof Number){
                        value = Objects.toString(argument);
                    }else if(argument instanceof Authorization){
                        value = ((Authorization) argument).get();
                    }else if(argument == null){
                        throw new NullPointerException("The argument in " + value + "cannot be null");
                    }else{
                        throw new IllegalArgumentException("The argument in value: " + value + " can not be resolved");
                    }
                }
                linkedMap.put(key, value);
            }
            return linkedMap;
        }

        private static VerbProperty getVerbProperty(Method method, HttpMethod httpMethod){
            switch(httpMethod){
                case POST:
                    POST post = ReflectionUtils.getAnnotation(method, POST.class);
                    return new VerbProperty(post.value(), post.body());
                case PUT:
                    PUT put = ReflectionUtils.getAnnotation(method, PUT.class);
                    return new VerbProperty(put.value(), put.body());
                case DELETE:
                    DELETE delete = ReflectionUtils.getAnnotation(method, DELETE.class);
                    return new VerbProperty(delete.value(), delete.body());
                case PATCH:
                    PATCH patch = ReflectionUtils.getAnnotation(method, PATCH.class);
                    return new VerbProperty(patch.value(), patch.body());
                case GET:
                    GET get = ReflectionUtils.getAnnotation(method, GET.class);
                    return new VerbProperty(get.value(), -1);
                case HEAD:
                    HEAD head = ReflectionUtils.getAnnotation(method, HEAD.class);
                    return new VerbProperty(head.value(), -1);
                case TRACE:
                    TRACE trace = ReflectionUtils.getAnnotation(method, TRACE.class);
                    return new VerbProperty(trace.value(), -1);
                case CONNECT:
                    CONNECT connect = ReflectionUtils.getAnnotation(method, CONNECT.class);
                    return new VerbProperty(connect.value(), -1);
                case OPTIONS:
                    OPTIONS options = ReflectionUtils.getAnnotation(method, OPTIONS.class);
                    return new VerbProperty(options.value(), options.body());
                default:
                    return new VerbProperty("", -1);
            }
        }


        private static Object resolveArgument(String value, Object[] args){
            int index = Integer.parseInt(value.substring(1, value.length() - 1));
            if(index >= args.length){
                throw new IllegalArgumentException("The index: " + index + " not found in arguments");
            }
            return args [index];
        }

    }

}


