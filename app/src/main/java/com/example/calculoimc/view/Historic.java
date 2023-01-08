package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.calculoimc.R;

public class Historic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        TextView userV = (TextView) findViewById(R.id.userView);
        TextView imcList = (TextView) findViewById(R.id.imcList);

        //TODO Mostrar valores do banco de dados nas TextViews acima
        }

}