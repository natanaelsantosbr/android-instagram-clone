package br.natanael.android.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.natanael.android.instagram.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void abrirCadastro(View view)
    {
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }

}
