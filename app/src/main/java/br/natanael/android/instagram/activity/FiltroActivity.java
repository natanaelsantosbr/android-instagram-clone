package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;


import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.adapter.AdapterMiniaturas;
import br.natanael.android.instagram.helper.RecyclerItemClickListener;

public class FiltroActivity extends AppCompatActivity {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;

    private List<ThumbnailItem> listaFiltros;

    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //configuracoes inciais
        listaFiltros = new ArrayList<>();

        //Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);

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

                //configurar recylcerView
                adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerFiltros.setLayoutManager(layoutManager);
                recyclerFiltros.setAdapter(adapterMiniaturas);


                //Adicionar evento de clieque no recyclerview
                recyclerFiltros.addOnItemTouchListener(new RecyclerItemClickListener(
                        getApplicationContext(), recyclerFiltros, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ThumbnailItem item = listaFiltros.get(position);

                        imagemFiltro = imagem.copy(imagem.getConfig(),true);
                        Filter filter = item.filter;
                        imageFotoEscolhida.setImageBitmap(filter.processFilter((imagemFiltro)));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                ));


                //Recuperar filtros
                recuperarFiltros();



            }
        }
    }

    private void recuperarFiltros() {

        //Limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //Configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        //Lista todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());

        for (Filter filtro: filtros)
        {
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter  = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged();


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
