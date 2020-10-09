package br.natanael.android.instagram.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Result;

import br.natanael.android.instagram.R;
import br.natanael.android.instagram.activity.FiltroActivity;
import br.natanael.android.instagram.helper.Permissao;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {

    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };



    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);


        //Validar permissoes
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);


        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);

        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getActivity().getPackageManager()) != null)
                {
                    startActivityForResult(i, SELECAO_CAMERA );
                }
            }
        });

        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getActivity().getPackageManager()) != null)
                {
                    startActivityForResult(i, SELECAO_GALERIA );
                }

            }
        });



        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK)
        {
            Bitmap imagem = null;

            try {
                //validar tipo de selecao
                switch (requestCode)
                {
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localDaImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localDaImagemSelecionada);
                        break;
                }

                if(imagem != null)
                {
                    //Converter imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Envia imagem escolhida para aplicacao de filtro
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra("fotoEscolhida", dadosImagem);
                    startActivity(i);

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }



    }
}
