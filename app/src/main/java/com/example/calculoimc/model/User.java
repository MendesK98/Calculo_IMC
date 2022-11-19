package com.example.calculoimc.model;

import java.text.DecimalFormat;

public class User {
    String nome;
    int idade;
    double altura;
    double peso;
    double imc;

    //Métodos construtores
    public User() {};

    User (double altura, double peso) {
        this.altura = altura;
        this.peso = peso;
    }


    //Método para cálculo do indice de massa corporal
    public void calcIMC (double altura, double peso) {
        this.imc = peso / (altura * altura);
    }

    //Gerando mensangem para o usuário baseado no IMC calculado
    public String imcMessage () {
        String imcDefinicao = "";
        if (this.imc > 0 && this.imc <= 18.5) {
            imcDefinicao += "seu estado atual é de magreza, sinta-se à vontade para comer mais alimentos saudáveis e gostosos.";
        } else if (this.imc > 18.5 && this.imc < 25) {
            imcDefinicao += "seu estado atual é satisfatório, continue praticando exercícios e se alimentando corretamente";
        } else if (this.imc >= 25 && this.imc < 30) {
            imcDefinicao += "seu estado atual é de sobrepeso, recomenda-se a procura de um profissional de saúde/nutrição";
        } else if (this.imc >= 30 && this.imc < 35) {
            imcDefinicao += "seu estado atual é de obesidade grau I, recomenda-se procurar um profissional de saúde/nutrição";
        } else if (this.imc >= 35 && this.imc < 40) {
            imcDefinicao += "seu estado atual é de obesidade grau II, recomenda-se procurar um profissional de saúde/nutrição";
        } else if (this.imc > 40 && this.imc < 80) {
            imcDefinicao += "seu estado atual é obesidade grau III, recomenda-se procurar um profissional de saúde/nutrição";
        } else {
            imcDefinicao = "Valores NÃO inseridos corretamente, reinicie o app e tente novamente!!!";
        }
        return imcDefinicao;
    }


    //Getters and Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getImc() {
        return imc;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }
}
