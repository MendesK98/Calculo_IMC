package com.example.calculoimc.model;

import java.io.Serializable;

public class UserAtributos implements Serializable {
    private int id;
    private String nome;
    private int idade;
    private double altura;
    private double metaIMC;
    private double metaPeso;
    private IMC imc;

    // Construtor padrão inicializando o objeto IMC para evitar NullPointerException
    public UserAtributos() {
        this.imc = new IMC();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getAltura() { return altura; }

    public void setAltura(double altura) {
        this.altura = altura;
        calcularMetaIMC(); // Recalcula se a altura mudar
    }

    public double getMetaPeso() { return metaPeso; }

    public void setMetaPeso(double metaPeso) {
        this.metaPeso = metaPeso;
        calcularMetaIMC(); // Recalcula se o peso da meta mudar
    }

    public double getMetaIMC() { return metaIMC; }
    // Não criamos setMetaIMC pois ele é calculado internamente

    public IMC getImc() { return imc; }
    public void setImc(IMC imc) { this.imc = imc; }

    // Lógica de cálculo automático da Meta de IMC
    private void calcularMetaIMC() {
        if (this.altura > 0 && this.metaPeso > 0) {
            this.metaIMC = this.metaPeso / (this.altura * this.altura);
        } else {
            this.metaIMC = 0;
        }
    }

    @Override
    public String toString() {
        return this.nome + " (Idade: " + this.idade + ")";
    }
}