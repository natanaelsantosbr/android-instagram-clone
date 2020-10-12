package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.adapter.AdapterGrid;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.helper.UsuarioFirebase;
import br.natanael.android.instagram.model.Postagem;
import br.natanael.android.instagram.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private Button buttonAcaoPerfil;

    private CircleImageView imagePerfil;

    private TextView textPublicacoes;
    private TextView textSeguidores;
    private TextView textSeguindo;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference firebaseRef;
    private DatabaseReference postagensUsuarioRef;

    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;

    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        inicializarComponentes();

        configurarToolbar();

        recuperarUsuario();

        preencherCampos();
    }

    private void preencherCampos() {
        buttonAcaoPerfil.setText("Carregando...");

        if(usuarioSelecionado.getCaminhoFoto() != null)
        {
            Uri url = Uri.parse(usuarioSelecionado.getCaminhoFoto());

            Glide.with(getApplicationContext())
                    .load(url)
                    .into(imagePerfil);
        }
        else
        {
            Glide.with(getApplicationContext())
                    .load(R.drawable.avatar)
                    .into(imagePerfil);
        }


    }

    public void inicializarImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();

        ImageLoader.getInstance().init(configuration);
    }
    private void carregarFotosPostagem() {
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlsFotos = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlsFotos.add((postagem.getCaminhoFoto()));
                }
                textPublicacoes.setText(String.valueOf(urlsFotos.size()));

                //Configurar o adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlsFotos);
                gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void recuperarDadosDoUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                        .child("postagens")
                        .child(usuarioSelecionado.getId());

                verificaSegueAmigo();

                inicializarImageLoader();

                carregarFotosPostagem();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void recuperarUsuario() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();

        //Recuperar dados usuario logado
        recuperarDadosDoUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);

    }

    private void recuperarDadosPerfilAmigo() {
        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);


                //textPublicacoes.setText(String.valueOf(usuario.getPostagens()) );
                textSeguidores.setText(String.valueOf(usuario.getSeguidores()));
                textSeguindo.setText(String.valueOf(usuario.getSeguindo()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        //Habilitar botao voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alterar o icone do botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
    }

    private void inicializarComponentes() {
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        imagePerfil = findViewById(R.id.imagePerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;

    }

    private  void verificaSegueAmigo() {
        DatabaseReference seguidorRef = seguidoresRef
                .child(idUsuarioLogado);

        //Executar apenas uma vez
        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    habilitarBotaoDeSeguir(true);

                }
                else
                {
                    habilitarBotaoDeSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void habilitarBotaoDeSeguir(boolean segueUsuario)
    {
        if(segueUsuario)
            buttonAcaoPerfil.setText("Seguindo");
        else
        {
            buttonAcaoPerfil.setText("Seguir");

            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);

                }
            });

        }
    }

    private void salvarSeguidor(Usuario logado, Usuario amigo) {

        HashMap<String, Object> dadosAmigos = new HashMap<>();
        dadosAmigos.put("nome", amigo.getNome());
        dadosAmigos.put("caminhoFoto", amigo.getCaminhoFoto());


        DatabaseReference seguidorRef = seguidoresRef
                .child(logado.getId())
                .child(amigo.getId());

        seguidorRef.setValue(dadosAmigos);

        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        //Incremetar seguindo do usuario logado
        int seguindo = logado.getSeguindo() + 1;

        DatabaseReference usuarioSeguindo = usuariosRef
                .child(logado.getId());


        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);
        usuarioSeguindo.updateChildren(dadosSeguindo);


        //Incrementar seguidores do usuario logado
        int seguidores = amigo.getSeguidores() + 1;

        DatabaseReference usuarioSeguidores = usuariosRef
                .child(amigo.getId());


        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);
        usuarioSeguidores.updateChildren(dadosSeguidores);
    }


}
