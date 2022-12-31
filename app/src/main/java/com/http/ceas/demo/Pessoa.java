package com.http.ceas.demo;
import java.io.Serializable;

public class Pessoa implements Serializable{
    int id;
    String nome;

    public Pessoa(int id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getNome(){
        return nome;
    }

    @Override
    public String toString(){
        return nome;
    }
    
    
}
