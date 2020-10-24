package br.natanael.android.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.helper.UsuarioFirebase;
import br.natanael.android.instagram.model.Comentario;
import br.natanael.android.instagram.model.Usuario;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private String idPostagem;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializa componentes
        editComentario = findViewById(R.id.editComentario);

        //configuracoes iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();


        configurarToolbar();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            idPostagem = bundle.getString("idPostagem");
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void salvarComentario(View view) {
        String textoComentario = editComentario.getText().toString();

        if(textoComentario != null)
        {
            if(!textoComentario.equals(""))
            {
                Comentario comentario = new Comentario();
                comentario.setIdPostagem(idPostagem);
                comentario.setIdUsuario(usuario.getId());
                comentario.setNomeUsuario(usuario.getNome());
                comentario.setCaminhoFoto(usuario.getCaminhoFoto());
                comentario.setComentario(textoComentario);

                if(comentario.salvar())
                {
                    Toast.makeText(this, "Comentário salvo com sucesso", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
                }



            }
            else
                Toast.makeText(this, "Digite um comentário", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Digite um comentário", Toast.LENGTH_SHORT).show();
        }

        editComentario.setText("");

    }
}
