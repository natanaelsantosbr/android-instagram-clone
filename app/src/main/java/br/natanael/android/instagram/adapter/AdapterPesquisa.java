package br.natanael.android.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.URI;
import java.util.List;
import java.util.PropertyResourceBundle;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {

    private List<Usuario> _listaUsuario;
    private Context _context;

    public AdapterPesquisa(List<Usuario> listaUsuario, Context context) {
        this._listaUsuario = listaUsuario;
        this._context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemDaLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);



        return new MyViewHolder(itemDaLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = _listaUsuario.get(position);

        holder.nome.setText(usuario.getNome());

        if(usuario.getCaminhoFoto() != null)
        {
            Uri url = Uri.parse(usuario.getCaminhoFoto());

            Glide.with(_context)
                    .load(url)
                    .into(holder.foto);
        }
        else
        {
            holder.foto.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return _listaUsuario.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoPesquisa);
            nome = itemView.findViewById(R.id.textNomePesquisa);

        }
    }
}
