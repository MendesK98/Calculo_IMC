package com.example.calculoimc.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.calculoimc.R;
import com.example.calculoimc.model.IMC;
import com.example.calculoimc.model.User;

public class Historic extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        TextView userV = (TextView) findViewById(R.id.userView);
        TextView imcList = (TextView) findViewById(R.id.imcList);

        User user = new User();
        user.setNome("Lucas");

        userV.setText(user.getNome());

        String resList = "";
        for (IMC imcL : user.getImcs()) {
            resList += "\n" + imcL.getIndice();
        }

        imcList.setText(resList);

    }
}