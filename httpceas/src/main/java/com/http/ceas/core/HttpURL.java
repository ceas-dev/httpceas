package com.http.ceas.core;
import android.net.Uri;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class HttpURL{

    private static final String PATH_SEPARATOR = "/";

    private final List<String> paths = new ArrayList<>();
    private final Map<String, String> queries = new LinkedHashMap<>();
    private final Uri.Builder baseUri;

    private HttpURL(String url){
        Uri uri = Uri.parse(url);
        paths.addAll(uri.getPathSegments());
        for(String key : uri.getQueryParameterNames()){
            queries.put(key, uri.getQueryParameter(key));
        }
        baseUri = uri.buildUpon().clearQuery().path(null);
    }

    public static HttpURL create(String url){
        if(url == null){
            throw new IllegalArgumentException("url cannot be null");
        }
        if(url.isEmpty()){
            throw new IllegalArgumentException("url cannot be empty");
        }
        return new HttpURL(url);
    }
    
    public String getProtocol(){
        return toUri().getScheme();
    }
    

    public HttpURL addPath(String path){
        if(path == null || path.isEmpty()) return this;
        if(path.contains(PATH_SEPARATOR)){
            for(String pathSepare : path.split(PATH_SEPARATOR)){
                paths.add(pathSepare);
            }
        }else{
            paths.add(path);
        }
        return this;
    }

    public HttpURL addPath(String... paths){
        if(paths == null || paths.length == 0) return this;
        for(String path : paths){
            this.paths.add(path);
        }
        return this;
    }

    public HttpURL editPath(int index, String path){
        if(isSafeIndex(index)) paths.set(index, path);
        return this;
    }

    public HttpURL removePath(int index){
        if(isSafeIndex(index)) paths.remove(index);
        return this;
    }

    public HttpURL clearPaths(){
        paths.clear();
        return this;
    }

    public int countPaths(){
        return paths.size();
    }

    public boolean containsPaths(){
        return !paths.isEmpty();
    }

    public boolean containsPath(String path){
        if(path.contains(PATH_SEPARATOR)){
            for(String pathSepare : path.split(PATH_SEPARATOR)){
                if(paths.contains(pathSepare)) return true;
            }
            return false;
        }
        return paths.contains(path);
    }

    public String getPath(int index){
        return isSafeIndex(index) ? paths.get(index) : null;
    }

    public String getLastPath(){
        return getPath(paths.size() - 1);
    }

    public List<String> getAllPaths(){
        return paths;
    }

    public HttpURL putQuery(Map<String, String> queries){
        this.queries.putAll(queries);
        return this;
    }
    public HttpURL putQuery(String key, String value){
        queries.put(key, value);
        return this;
    }

    public HttpURL removeQuery(String key){
        queries.remove(key);
        return this;
    }

    public HttpURL clearQuery(){
        queries.clear();
        return this;
    }

    public boolean containsQueries(){
        return !queries.isEmpty();
    }

    public boolean containsQuery(String key){
        return queries.containsKey(key);
    }

    public boolean containsQueryValue(String value){
        return queries.containsValue(value);
    }

    public String getQuery(String key){
        return queries.get(key);
    }

    public int countQuery(){
        return queries.size();
    }

    public Set<String> getAllKeysQuery(){
        return queries.keySet();
    }

    @Override
    public String toString(){
        updateBaseUri();
        return baseUri.toString();
    }

    public Uri toUri(){
        updateBaseUri();
        return baseUri.build();
    }

    private void updateBaseUri(){
        baseUri.clearQuery().path(null);
        for(String path : paths){
            baseUri.appendPath(path);
        }
        for(String key : queries.keySet()){
            baseUri.appendQueryParameter(key, queries.get(key));
        }
    }

    private boolean isSafeIndex(int index){
        return index < countPaths() && index >= 0;
    }
}
