package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.calculoimc.R;
import com.example.calculoimc.model.Usuarios;

public class Historic extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        TextView userV = (TextView) findViewById(R.id.userView);
        TextView imcList = (TextView) findViewById(R.id.imcList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Usuarios.users.stream().forEach(c -> userV.setText(
                    userV.getText().toString() + "\n" + c.getNome()));
        }


    }
}