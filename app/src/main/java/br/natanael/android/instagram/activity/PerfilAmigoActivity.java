package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.helper.UsuarioFirebase;
import br.natanael.android.instagram.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Button buttonAcaoPerfil;

    private CircleImageView imagePerfil;

    private TextView textPublicacoes;
    private TextView textSeguidores;
    private TextView textSeguindo;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference firebaseRef;

    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;





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
        buttonAcaoPerfil.setText("Seguir");

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

        verificaSegueAmigo();


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


                textPublicacoes.setText(String.valueOf(usuario.getPostagens()) );
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
            buttonAcaoPerfil.setText("Seguir");
    }
}
