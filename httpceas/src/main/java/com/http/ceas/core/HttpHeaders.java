package com.http.ceas.core;

import com.http.ceas.entity.Authorization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.http.ceas.MediaType;

public class HttpHeaders {

    private final Map<String, String> values = new HashMap<>();

    private HttpHeaders(Builder builder) {
        this.values.putAll(builder.values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Map<String, String> map){
        return new Builder(map);
    }

    public static Builder builder(HttpHeaders httpHeaders) {
        return new Builder(httpHeaders);
    }

    public Builder newBuilder() {
        return builder(this);
    }

    public boolean containsKey(HttpProperty httpProperty) {
        return values.containsKey(httpProperty.getProperty());
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public boolean containsKeys(HttpProperty... properties) {
        int size = properties.length;
        String[] keys = new String[size];
        for (int i = 0; i < size; i++) {
            keys[i] = properties[i].getProperty();
        }
        return containsKeys(keys);
    }

    public boolean containsKeys(String... keys) {
        for (String key : keys) {
            if (values.containsKey(key)) {
                return true;
            }
        }
        return false;
    }


    public boolean containsValue(String value) {
        return values.containsValue(value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public String get(HttpProperty httpProperty) {
        return get(httpProperty.getProperty());
    }

    public String getAuth() {
        return get(HttpProperty.AUTHORIZATION);
    }

    public String getContentType() {
        return get(HttpProperty.CONTENT_TYPE);
    }

    public String getContentLength() {
        return get(HttpProperty.CONTENT_LENGTH);
    }

    public String getAccept() {
        return get(HttpProperty.ACCEPT);
    }

    public boolean hasContentType() {
        return containsKey(HttpProperty.CONTENT_TYPE);
    }

    public boolean hasContentLength() {
        return containsKey(HttpProperty.CONTENT_LENGTH);
    }

    public String[] getKeys() {
        return values.keySet().toArray(new String[0]);
    }

    public Iterator<String> iterator() {
        return values.keySet().iterator();
    }

    public void clear() {
        values.clear();
    }

    public String remove(String key) {
        return values.remove(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : getKeys()) {
            sb.append(key)
                    .append(":")
                    .append(get(key))
                    .append("\n");
        }
        return sb.toString().trim();
    }


    public void receiveAll(HttpHeaders httpHeaders) {
        if (httpHeaders == null) {
            throw new IllegalArgumentException("httpHeaders cannot be null");
        }
        values.putAll(httpHeaders.values);
    }

    public void receiveOnlyNew(HttpHeaders httpHeaders) {
        if (httpHeaders == null) {
            throw new IllegalArgumentException("httpHeaders cannot be null");
        }
        for (String key : httpHeaders.getKeys()) {
            if (!containsKey(key)) {
                values.put(key, httpHeaders.get(key));
            }
        }
    }

    public static class Builder {

        private final Map<String, String> values = new HashMap<>();

        public Builder() {
        }

        public Builder(Map<String, String> map) {
            values.putAll(map);
        }

        public Builder(HttpHeaders httpHeaders) {
            this(httpHeaders.values);
        }

        public Builder put(String key, String header) {
            values.put(key, header);
            return this;
        }

        public Builder put(HttpProperty httpProperty, String header) {
            values.put(httpProperty.getProperty(), header);
            return this;
        }

        public Builder putAuth(Authorization auth) {
            return put(HttpProperty.AUTHORIZATION, auth.get());
        }

        public Builder putContentType(String header) {
            return put(HttpProperty.CONTENT_TYPE, header);
        }

        public Builder putContentLength(String header) {
            return put(HttpProperty.CONTENT_LENGTH, header);
        }

        public Builder putAccept(String header) {
            return put(HttpProperty.ACCEPT, header);
        }

        public HttpHeaders create() {
            return new HttpHeaders(this);
        }
    }
}
