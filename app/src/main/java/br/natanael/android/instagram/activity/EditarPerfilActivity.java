package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.helper.UsuarioFirebase;
import br.natanael.android.instagram.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        inicializarComponentes();

        preencherCampos();

        salvarAlteracoes();

        alterarFotos();


    }

    private void alterarFotos() {
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            Bitmap imagem  = null;

            try
            {
                switch (requestCode)
                {
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                localImagemSelecionada);
                        break;
                }

                if(imagem != null)
                {
                    imageEditarPerfil.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();


                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child("perfil.jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this, "Erro ao salvar uma imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditarPerfilActivity.this, "Sucesso ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }

    private void salvarAlteracoes() {
        //Salvar alteracoes do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNomePerfil.getText().toString();

                //atualizar nome no perfil
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                //Atualizar nome no banco de dados
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.atualizar();
                Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherCampos() {
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();

        //recuperar dados do usuario
        final FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName());
        editEmailPerfil.setText(usuarioPerfil.getEmail());
    }

    private void inicializarComponentes() {
        configurarToolbar();

        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editEmailPerfil = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false);
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
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
}
