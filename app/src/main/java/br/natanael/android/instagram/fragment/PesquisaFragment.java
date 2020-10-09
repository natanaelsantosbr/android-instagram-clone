package br.natanael.android.instagram.fragment;


import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CpuUsageInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TableRow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.activity.PerfilAmigoActivity;
import br.natanael.android.instagram.adapter.AdapterPesquisa;
import br.natanael.android.instagram.helper.ConfiguracaoFirebase;
import br.natanael.android.instagram.helper.RecyclerItemClickListener;
import br.natanael.android.instagram.helper.UsuarioFirebase;
import br.natanael.android.instagram.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;

    private AdapterPesquisa adapterPesquisa;
    private String idUsuarioLogado;
    

    public PesquisaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa  = view.findViewById(R.id.recyclerPesquisa);

        searchViewPesquisa.setFocusable(true);
        searchViewPesquisa.setIconified(false);
        searchViewPesquisa.requestFocusFromTouch();

        //Configuracao Inicial
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();


        //Configurar RecyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);

        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ) {
        });


        //configura searchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toLowerCase();

                pesquisarUsuarios(textoDigitado);

                return true;
            }
        });



        return  view;
    }

    private void pesquisarUsuarios(String texto) {

        //limpa lista
        listaUsuarios.clear();

        //Pesquisa usuario caso tenha texto na pesquisa

        if(texto.length() >= 3)
        {
            Query query = usuariosRef.orderByChild("nomeMinusculo")
                    .startAt(texto)
                    .endAt(texto + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaUsuarios.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        //verifica se e usuario logado e remove da lista
                        Usuario usuario = ds.getValue(Usuario.class);

                        if(idUsuarioLogado.equals(usuario.getId()))
                            continue;

                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();

                    /*int total = listaUsuarios.size();
                    Log.i("totalUsuarios","total: " + total);*/

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}
