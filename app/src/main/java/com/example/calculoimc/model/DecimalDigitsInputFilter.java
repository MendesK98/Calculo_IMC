package com.example.calculoimc.model;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    private Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        // Regex que aceita apenas números e ponto, respeitando as casas decimais
        String regex = "^[0-9]{0," + digitsBeforeZero + "}(\\.[0-9]{0," + digitsAfterZero + "})?$";
        mPattern = Pattern.compile(regex);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 1. Convertemos o que está sendo digitado: se for vírgula, vira ponto
        String input = source.subSequence(start, end).toString();
        String replacement = input.replace(',', '.');

        // 2. Simulamos como o texto ficaria no EditText se aceitássemos a alteração
        String textFinal = dest.subSequence(0, dstart).toString() +
                replacement +
                dest.subSequence(dend, dest.length()).toString();

        // 3. Verificamos se o texto final (já com o ponto) obedece ao limite de casas decimais
        Matcher matcher = mPattern.matcher(textFinal);

        if (!matcher.matches()) {
            return ""; // Se estourar o limite de números, bloqueia a digitação
        }

        // 4. AQUI ESTÁ O SEGREDO:
        // Se o usuário digitou uma vírgula e ela é válida como ponto,
        // retornamos "." explicitamente para forçar a troca na tela.
        if (input.equals(",")) {
            return ".";
        }

        // Se o que ele digitou já for ponto ou número, retornamos null
        // (null diz ao Android: "aceite o caractere original")
        return null;
    }
}