package com.http.ceas.entity;

import android.util.Base64;

public abstract class Authorization{
    private final String value;

    protected Authorization(String value){
        this.value = value;
    }

    public String get(){
        return value;
    }

    public boolean isBearerToken(){
        return this instanceof BearerToken;
    }

    public boolean isBasic(){
        return this instanceof Basic;
    }

    public static BearerToken withBearerToken(String token){
        return new BearerToken(token);
    }

    public static Basic withBasic(String username, String password){
        return new Basic(username, password);
    }

    //BearerToken
    public static class BearerToken extends Authorization{

        public BearerToken(String token){
            super(resolveToken(token));
        }

        private static String resolveToken(String token){
            final String BEARER = "Bearer";
            if(token == null || token.isEmpty()){
                throw new IllegalArgumentException("token invalid");
            }
            return token.contains(BEARER) ? token : BEARER + " " + token;
        }
    }

    //Basic
    public static class Basic extends Authorization{
        private final String username, password;

        public Basic(String username, String password){
            super(resolveToken(username, password));
            this.username = username;
            this.password = password;
        }

        public String getUsername(){
            return username;
        }

        public String getPassword(){
            return password;
        }

        private static String resolveToken(String username, String password){
            if(username == null || username.isEmpty()){
                throw new IllegalArgumentException("username invalid");
            }
            if(password == null || password.isEmpty()){
                throw new IllegalArgumentException("password invalid");
            }
            return "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
        }
    }
}
