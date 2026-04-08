package com.example.calculoimc.model;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    private Pattern mPattern;
    // Dentro da classe DecimalDigitsInputFilter
    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        // Regex corrigido: permite dígitos antes e depois, e o separador . ou ,
        mPattern = Pattern.compile("[0-9]{0," + digitsBeforeZero + "}+((\\.[0-9]{0," + digitsAfterZero + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String replacement = source.subSequence(start, end).toString();
        String newVal = dest.subSequence(0, dstart).toString() + replacement + dest.subSequence(dend, dest.length()).toString();

        // Troca vírgula por ponto para o Regex validar corretamente
        newVal = newVal.replace(',', '.');

        Matcher matcher = mPattern.matcher(newVal);
        if (!matcher.matches()) return "";
        return null;
    }
}