package com.example.calculoimc.model;

public class IMC {
    private double altura;
    private double peso;
    private double indice;

    public IMC () {}

    public IMC (Double altura, Double peso) {
        this.altura = altura;
        this.peso = peso;
    }

    //Método para cálculo do indice de massa corporal
    public double calcIMC () {
        return this.indice = (this.peso / (this.altura * this.altura));
    }

    //Gerando mensangem para o usuário baseado no IMC calculado
    public String imcMessage () {
        String imcDefinicao = "";
        if (this.indice > 0 && this.indice <= 18.5) {
            imcDefinicao += "seu estado atual é de magreza, sinta-se à vontade para comer mais alimentos saudáveis e gostosos.";
        } else if (this.indice > 18.5 && this.indice < 25) {
            imcDefinicao += "seu estado atual é satisfatório, continue praticando exercícios e se alimentando corretamente";
        } else if (this.indice >= 25 && this.indice < 30) {
            imcDefinicao += "seu estado atual é de sobrepeso, recomenda-se a procura de um profissional de saúde/nutrição";
        } else if (this.indice >= 30 && this.indice < 35) {
            imcDefinicao += "seu estado atual é de obesidade grau I, recomenda-se procurar um profissional de saúde/nutrição";
        } else if (this.indice >= 35 && this.indice < 40) {
            imcDefinicao += "seu estado atual é de obesidade grau II, recomenda-se procurar um profissional de saúde/nutrição";
        } else if (this.indice > 40 && this.indice <= 80) {
            imcDefinicao += "seu estado atual é obesidade grau III, recomenda-se procurar um profissional de saúde/nutrição";
        } else {
            imcDefinicao = "os valores NÃO foram inseridos corretamente, reinicie o app e tente novamente!!!";
        }
        return imcDefinicao;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Double getIndice() {
        return indice;
    }

    public void setIndice(Double indice) {
        this.indice = indice;
    }

}
