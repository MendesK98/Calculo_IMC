package com.example.calculoimc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String nome;
    private int id;
    private int idade;
    private double altura;
    private double peso;
    private IMC imc = new IMC();


    //Métodos construtores
    public User() {};

    public User (int id) {
        this.id = id;
    }

    public User (String nome) {
        this.nome = nome;
    }

   public  User (int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
    }

    //Getters and Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public IMC getImc() {
        return imc;
    }

    public void setImc(IMC imc) {
        this.imc = imc;
    }

}
