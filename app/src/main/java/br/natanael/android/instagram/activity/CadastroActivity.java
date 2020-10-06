package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //Cadastrar usuario
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoNome.isEmpty())
                {
                    if(!textoEmail.isEmpty())
                    {
                        if(!textoSenha.isEmpty())
                        {

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);

                            cadastrar(usuario);
                        }
                        else
                        {
                            adicionarToast("Preencha a senha!");
                        }
                    }
                    else
                    {
                        adicionarToast("Preencha o email!");
                    }

                }
                else
                {
                    adicionarToast("Preencha o nome!");
                }

            }
        });
    }

    private void cadastrar(final Usuario usuario) {
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(
                        this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {

                                    try {
                                        progressBar.setVisibility(View.GONE);

                                        //Salvar dados no firebase
                                        String idUsuario = task.getResult().getUser().getUid();
                                        usuario.setId(idUsuario);
                                        usuario.salvar();

                                        adicionarToast("Cadastro com sucesso");

                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                    catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }
                                }
                                else
                                {
                                    progressBar.setVisibility(View.GONE);

                                    String erroExcecao = "";

                                    try
                                    {
                                        throw task.getException();
                                    }
                                    catch (FirebaseAuthWeakPasswordException e)
                                    {
                                        erroExcecao = "Digite uma senha mais forte!";
                                    }
                                    catch (FirebaseAuthInvalidCredentialsException e)
                                    {
                                        erroExcecao = "Por favor, digite um e-mail válido";
                                    }
                                    catch (FirebaseAuthUserCollisionException e)
                                    {
                                        erroExcecao = "Esta conta já foi cadastrada";
                                    }
                                    catch (Exception e)
                                    {
                                        erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    adicionarToast("Erro: "  + erroExcecao);
                                }
                            }
                        }
                );
    }

    private void adicionarToast(String mensagem) {
        Toast.makeText(CadastroActivity.this,mensagem, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {
        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);

        botaoCadastrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressCadastro);

        campoNome.requestFocus();
    }
}
