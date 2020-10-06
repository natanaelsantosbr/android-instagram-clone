package br.natanael.android.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.PropertyResourceBundle;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;


    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao()
    {
        if(referenciaAutenticacao == null)
            referenciaAutenticacao = FirebaseAuth.getInstance();

        return  referenciaAutenticacao;
    }

    public static DatabaseReference getFirebase()
    {
        if(referenciaFirebase == null)
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        return referenciaFirebase;
    }
}
