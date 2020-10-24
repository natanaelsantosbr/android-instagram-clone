package br.natanael.android.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import br.natanael.android.instagram.R;

public class ComentariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        configurarToolbar();

    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);

        //Habilitar botao voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alterar o icone do botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
    }
}
