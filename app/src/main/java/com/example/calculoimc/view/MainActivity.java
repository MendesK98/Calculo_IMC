package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.calculoimc.R;
import com.example.calculoimc.view.ReceberDados;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calcular = (Button) findViewById(R.id.bCalcular);

        Intent i = new Intent(this, ReceberDados.class);
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(i);
                finish();
            }
        });

    }
}