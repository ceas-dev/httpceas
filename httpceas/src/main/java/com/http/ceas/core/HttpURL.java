package com.http.ceas.core;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpURL {
    private static final String PATH_SEPARATOR = "/";
    private final List<String> paths = new ArrayList<>();
    private final Map<String, String> queries = new LinkedHashMap<>();
    private final Uri uri;

    private HttpURL(String url) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null.");
        }
        if (url.isEmpty()) {
            throw new IllegalArgumentException("url cannot be empty.");
        }
        this.uri = Uri.parse(url).buildUpon().clearQuery().path(null).build();
        updatePathsAndQueries(url);
    }

    public static HttpURL create(String url) {
        return new HttpURL(url);
    }

    public String getProtocol() {
        return toUri().getScheme();
    }

    public HttpURL addPath(String path) {
        if (path != null && !path.isEmpty()) {
            if (path.contains(PATH_SEPARATOR)) {
                addPath(path.split(PATH_SEPARATOR));
            } else {
                paths.add(path);
            }
        }
        return this;
    }

    public HttpURL addPath(String... paths) {
        if (paths != null && paths.length != 0) {
            this.paths.addAll(Arrays.asList(paths));
        }
        return this;
    }

    public HttpURL editPath(int index, String path) {
        if (isSafeIndex(index, countPaths())) {
            paths.set(index, path);
        }
        return this;
    }

    public HttpURL removePath(int index) {
        if (isSafeIndex(index, countPaths())) {
            paths.remove(index);
        }
        return this;
    }

    public HttpURL clearPaths() {
        paths.clear();
        return this;
    }

    public int countPaths() {
        return paths.size();
    }

    public boolean containsPaths() {
        return !paths.isEmpty();
    }

    public boolean containsPath(String path) {
        if (path.contains(PATH_SEPARATOR)) {
            for (String part : path.split(PATH_SEPARATOR)) {
                if (paths.contains(part)) return true;
            }
            return false;
        }
        return paths.contains(path);
    }

    public String getPath(int index) {
        return isSafeIndex(index, countPaths()) ? paths.get(index) : null;
    }

    public String getLastPath() {
        return getPath(paths.size() - 1);
    }

    public List<String> getAllPaths() {
        return paths;
    }

    public HttpURL putQuery(Map<String, String> queries) {
        this.queries.putAll(queries);
        return this;
    }

    public HttpURL putQuery(String key, String value) {
        queries.put(key, value);
        return this;
    }

    public HttpURL removeQuery(String key) {
        queries.remove(key);
        return this;
    }

    public HttpURL clearQuery() {
        queries.clear();
        return this;
    }

    public boolean containsQueries() {
        return !queries.isEmpty();
    }

    public boolean containsQuery(String key) {
        return queries.containsKey(key);
    }

    public boolean containsQueryValue(String value) {
        return queries.containsValue(value);
    }

    public String getQuery(String key) {
        return queries.get(key);
    }

    public int countQuery() {
        return queries.size();
    }

    public Set<String> getAllKeysQuery() {
        return queries.keySet();
    }

    private void updatePathsAndQueries(String url) {
        Uri uri = Uri.parse(url);
        paths.clear();
        queries.clear();
        paths.addAll(uri.getPathSegments());
        if (uri.getEncodedPath().endsWith(PATH_SEPARATOR)) {
            paths.add("");
        }
        for (String key : uri.getQueryParameterNames()) {
            queries.put(key, uri.getQueryParameter(key));
        }
    }

    private Uri toUri() {
        Uri.Builder builder = uri.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        builder.encodedQuery(encodeQueries(queries));
        return builder.build();
    }

    private static String encodeQueries(Map<String, String> queries) {
        StringBuilder encodeQuery = new StringBuilder();
        for (String key : queries.keySet()) {
            encodeQuery.append(String.format("%s=%s&", key, queries.get(key)));
        }
        final int size = encodeQuery.length();
        if (size > 0) {
            return encodeQuery.substring(0, size - 1);
        }
        return encodeQuery.toString();
    }

    private static boolean isSafeIndex(int index, int limit) {
        return index < limit && index >= 0;
    }

    @Override
    public String toString() {
        return toUri().toString();
    }
}
