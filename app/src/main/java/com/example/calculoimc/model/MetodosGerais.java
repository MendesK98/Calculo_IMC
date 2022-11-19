package com.example.calculoimc.model;

public class MetodosGerais {

    public Double ConverStringToDouble (String x) {
        x = x.trim();
        if (x.contains(",")) {
            x = x.replace(",", ".");
        }
        Double y = Double.parseDouble(x);
        return y;
    }
}
