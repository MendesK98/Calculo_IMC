package com.example.calculoimc.model;

import java.io.Serializable;

public class UserAtributos implements Serializable {
    private int id;
    private String nome;
    private int idade;
    private IMC imc = new IMC();
    //Métodos construtores
    public UserAtributos() {};

    public UserAtributos(int id) {
        this.id = id;
    }

    public UserAtributos(String nome) {
        this.nome = nome;
    }

   public UserAtributos(int id, String nome, String email) {
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

    public IMC getImc() {
        return imc;
    }

    public void setImc(IMC imc) {
        this.imc = imc;
    }

    // Dentro da classe UserAtributos.java
    @Override
    public String toString() {
        return this.nome + " (Idade: " + this.idade + ")\n" + this.imc.toString();
    }
}
