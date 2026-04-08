package com.example.calculoimc.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IMC {
    private double altura;
    private double peso;
    private double indice;
    private double circunferencia;
    private Date data;

    public IMC() {
        this.data = new Date();
    }

    public IMC(double altura, double peso) {
        this.altura = altura;
        this.peso = peso;
        this.data = new Date();
        this.calcIMC();
    }

    // Método para cálculo do índice de massa corporal
    public double calcIMC() {
        if (this.altura > 0) {
            this.indice = (this.peso / (this.altura * this.altura));
        } else {
            this.indice = 0;
        }
        return this.indice;
    }

    public String getDescricaoCategoria() {
        if (indice <= 0) return "Inválido";
        if (indice <= 18.5) return "Magreza";
        if (indice < 25) return "Peso Normal";
        if (indice < 30) return "Sobrepeso";
        if (indice < 35) return "Obesidade Grau I";
        if (indice < 40) return "Obesidade Grau II";
        return "Obesidade Grau III";
    }

    public String imcMessage() {
        if (indice <= 0 || indice > 100) {
            return "Os valores não foram inseridos corretamente. Verifique o peso e a altura!";
        }

        String categoria = getDescricaoCategoria();
        String recomendacao;

        switch (categoria) {
            case "Magreza":
                recomendacao = "sinta-se à vontade para comer mais alimentos saudáveis e gostosos.";
                break;
            case "Peso Normal":
                recomendacao = "continue praticando exercícios e se alimentando corretamente.";
                break;
            default:
                recomendacao = "recomenda-se a procura de um profissional de saúde/nutrição.";
                break;
        }

        return "Seu estado atual é de " + categoria.toLowerCase() + ", " + recomendacao;
    }
    public String getDataFormatada() {
        if (data == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(data);
    }

    @Override
    public String toString() {
        return getDataFormatada() + " | IMC: " + String.format(Locale.US, "%.2f", indice) + "\n" + getDescricaoCategoria();
    }

    // Getters e Setters
    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getIndice() { return indice; }
    public void setIndice(double indice) { this.indice = indice; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public double getCircunferencia() { return circunferencia; }
    public void setCircunferencia(double circunferencia) { this.circunferencia = circunferencia; }
}