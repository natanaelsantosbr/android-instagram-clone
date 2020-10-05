package br.natanael.android.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CpuUsageInfo;
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

import java.util.UUID;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private ProgressBar progressBar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarComponentes();
        verificarUsuarioLogado();

        progressBar.setVisibility(View.GONE);
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoEmail.isEmpty())
                {
                    if(!textoSenha.isEmpty())
                    {
                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);

                        validarLogin(usuario);
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
        });

    }

    private void validarLogin(Usuario usuario) {
        progressBar.setVisibility(View.VISIBLE);
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
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
        });
    }

    private void inicializarComponentes() {
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressLogin);
        campoEmail.requestFocus();
        autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    public void abrirCadastro(View view)
    {
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }

    private void adicionarToast(String mensagem) {
        Toast.makeText(LoginActivity.this,mensagem, Toast.LENGTH_SHORT).show();
    }

    public void verificarUsuarioLogado() {
        if(autenticacao.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
