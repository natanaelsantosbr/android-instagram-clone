package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;


import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import br.natanael.android.instagram.R;

public class FiltroActivity extends AppCompatActivity {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);

        configurarToolbar();

        //Recupera a imagem escolhida pelo Usuario
        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");

            if(dadosImagem != null)
            {
                imagem = BitmapFactory.decodeByteArray(dadosImagem,0,dadosImagem.length);
                imageFotoEscolhida.setImageBitmap(imagem);

                imagemFiltro = imagem.copy(imagem.getConfig(),true);
                Filter filter = FilterPack.getAmazonFilter(getApplicationContext());
                imageFotoEscolhida.setImageBitmap(filter.processFilter((imagemFiltro)));
            }
        }
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        //Habilitar botao voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alterar o icone do botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.ic_salvar_postagem:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}