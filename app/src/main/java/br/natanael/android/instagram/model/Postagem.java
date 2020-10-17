package br.natanael.android.instagram.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.helper.UsuarioFirebase;

public class Postagem implements Serializable {
    private String id;
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;

    public Postagem() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");

        String idPostagem = postagemRef.push().getKey();

        setId(idPostagem);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public boolean salvar(DataSnapshot seguidoresSnapshot) {

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef  = ConfiguracaoFirebase.getFirebase();

        //referencia para postagem
        String combinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/postagens" + combinacaoId, this);


        for ( DataSnapshot seguiredores: seguidoresSnapshot.getChildren())
        {
            String idSeguidor = seguiredores.getKey();

            HashMap<String, Object> dadosSeguidores = new HashMap<>();
            dadosSeguidores.put("fotoPostagem", getCaminhoFoto());
            dadosSeguidores.put("descricao", getDescricao());
            dadosSeguidores.put("id", getId());
            dadosSeguidores.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidores.put("fotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizacoes = "/" + idSeguidor + "/" + getId();

            objeto.put("/feed" + idsAtualizacoes, dadosSeguidores);
        }


        firebaseRef.updateChildren(objeto);

        //postagensRef.setValue(this);
        return true;




    }
}
