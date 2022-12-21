package com.example.calculoimc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    String nome;
    private int id;
    private int idade;
    private double altura;
    private double peso;
    private IMC imc = new IMC();


    private List<IMC> imcs = new ArrayList<>();

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




    //Salvar IMC na lista
    public void adicionarIMC (IMC x) {
        imcs.add(x);
    }




    //Getters and Setters

}
