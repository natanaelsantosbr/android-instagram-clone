package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Instagram");
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_sair:
                deslogarUsuario();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try     {
            autenticacao.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
