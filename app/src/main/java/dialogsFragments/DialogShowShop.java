package dialogsFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.whereismypet.whereismypet.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import Modelo.Comentario;
import Modelo.Marcadores;
import Modelo.Mascota;
import Modelo.Oferta;
import Modelo.Publicidad;
import Modelo.Tienda;
import actividades.MainActivity;
import adaptadores.AdaptadorPublicidades;
import de.hdodenhof.circleimageview.CircleImageView;
import finalClass.GeneralMethod;
import finalClass.Utils;


@SuppressLint("ValidFragment")
public class DialogShowShop extends DialogFragment implements View.OnClickListener {
    private Tienda mDatosTienda;
    private Button btnPublicar;

    private ArrayList<Oferta> mListaOfertas;
    private RecyclerView recyclerPublicidad;
    private DatabaseReference mDatabase;
    private FirebaseUser mUserFireBase;
    private String ShopFlag;

    public DialogShowShop(Tienda mDatosTienda) {
        this.mDatosTienda = mDatosTienda;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.dialog_marcador_tienda, null);
        btnPublicar = content.findViewById(R.id.btnPublicar);
        mUserFireBase = FirebaseAuth.getInstance().getCurrentUser();
        btnPublicar.setOnClickListener(this);
        if(mUserFireBase.getUid().equals(mDatosTienda.getIdUsuario())) btnPublicar.setVisibility(View.VISIBLE);
        else btnPublicar.setVisibility(View.INVISIBLE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content);
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
            }
            return false;
        });
        CargarDatosTienda(content, (Tienda) mDatosTienda);
        return builder.create();
    }


    private void CargarDatosTienda(View view, Tienda mTienda) {
        final TextView eDireccionTienda = view.findViewById(R.id.eDireccionTienda),
                eNombreTienda = view.findViewById(R.id.eNombreTienda),
                eTelefonoTienda = view.findViewById(R.id.eTelefonoTienda);

        final CircleImageView imgTienda = view.findViewById(R.id.imgFotoTienda);

        eDireccionTienda.setText(mTienda.getDireccion());
        eNombreTienda.setText(mTienda.getNombre());
        eTelefonoTienda.setText(mTienda.getTelefono());
        GeneralMethod.GlideUrl(this.getActivity(), mTienda.getImagen(), imgTienda);

        // CargarPublicidadesTienda(mTienda,view);
    }

    private void CargarPublicidadesTienda(Tienda mTienda, View view) {
        mDatabase.child("Usuarios").child(Objects.requireNonNull(mTienda.getIdMarcador()))
                .child("Marcadores").child("Publicidad").child(mTienda.getIdPublicidad()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("PUBLICIDAD", dataSnapshot.toString());
                Oferta mOferta = dataSnapshot.getValue(Oferta.class);

                mListaOfertas.add(mOferta);
                recyclerPublicidad = view.findViewById(R.id.RecViewPublicidad);
                recyclerPublicidad.setLayoutManager(new LinearLayoutManager(view.getContext()));
                AdaptadorPublicidades adapter = new AdaptadorPublicidades(mListaOfertas, view.getContext());
                recyclerPublicidad.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPublicar: {
                instanciarCargaDeDatosOferta();
            }


        }

    }
    public class DialogPublicar extends DialogFragment implements View.OnClickListener {
        private EditText eTituloOferta;
        private EditText eDescripcionoferta;
        private EditText eprecioOferta;
        private Button eAgregarImagen;
        private Button eAcepto;
        private CircleImageView mFotoItemOFerta;
        private Uri  mUriOfertaPublicidad;
        private String tipoDeFoto = "VACIO";
        private String pathCaptureItemOferta;
        private ProgressDialog progressDialog;
        private StorageReference mStorageReference;
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View content = inflater.inflate(R.layout.dialog_publicar, null);

            eTituloOferta=content.findViewById(R.id.Titulo_publi);
            eDescripcionoferta=content.findViewById(R.id.Descripcion_publi);
            eprecioOferta=content.findViewById(R.id.Precio_publi);
            mFotoItemOFerta = content.findViewById(R.id.mFotoItemOFerta);
            mFotoItemOFerta.setOnClickListener(this);
            eAcepto =content.findViewById(R.id.BtnAceptaryPublicarPubli);
            eAcepto.setOnClickListener(this);
            mStorageReference = FirebaseStorage.getInstance().getReference();

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(content);

            builder.setOnKeyListener((dialog, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            });
            return builder.create();
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mFotoItemOFerta:{
                    mostrarDialogOpciones();
                }break;
                case R.id.BtnAceptaryPublicarPubli:{
                    RegistrarOfertaPublicidad(new Oferta().setDescripcionOferta(eDescripcionoferta.getText().toString())
                    .setTituloOferta(eTituloOferta.getText().toString())
                    .setIdMarcador(mDatosTienda.getIdMarcador())
                    .setIdPublicidad(GeneralMethod.getRandomString())
                            .setPrecio(eprecioOferta.getText().toString()));
                }break;
            }
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case Utils.COD_SELECCIONA: {
                        // URI Camara
                        mUriOfertaPublicidad = Objects.requireNonNull(data).getData();
                        tipoDeFoto = "SELECCIONA";
                        try {
                            mFotoItemOFerta.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), mUriOfertaPublicidad));
                            mUriOfertaPublicidad = GeneralMethod.reducirTamano(mUriOfertaPublicidad,this.getActivity());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }break;
                    case Utils.COD_FOTO: {
                        MediaScannerConnection.scanFile(this.getActivity(), new String[]{pathCaptureItemOferta}, null,(path, uri) -> Log.i("Path", "" + path));
                        mFotoItemOFerta.setImageBitmap(BitmapFactory.decodeFile(pathCaptureItemOferta));
                        mUriOfertaPublicidad = Uri.fromFile(new File(pathCaptureItemOferta));
                        mUriOfertaPublicidad = GeneralMethod.reducirTamano(mUriOfertaPublicidad,this.getActivity());
                        tipoDeFoto = "FOTO";
                    } break;
                }
            } else {
                mFotoItemOFerta.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.tienda));
            }
        }

        private void mostrarDialogOpciones() {
            final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Elige una Opción");
            builder.setItems(opciones, (dialogInterface, i) -> {
                if (opciones[i].equals("Tomar Foto")) {
                    abriCamara();
                } else {
                    if (opciones[i].equals("Elegir de Galeria")) {
                        Intent intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/");
                        startActivityForResult(Intent.createChooser(intent, "Seleccione"), Utils.COD_SELECCIONA);
                    } else {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }

        private void abriCamara() {
            File miFile = new File(Environment.getExternalStorageDirectory(), Utils.DIRECTORIO_IMAGEN);
            boolean isCreada = miFile.exists();

            if (!isCreada) {
                isCreada = miFile.mkdirs();
            }
            if (isCreada) {
                Long consecutivo = System.currentTimeMillis() / 1000;
                String nombre = consecutivo.toString() + ".jpg";

                pathCaptureItemOferta = Environment.getExternalStorageDirectory() + File.separator + Utils.DIRECTORIO_IMAGEN
                        + File.separator + nombre;//indicamos la ruta de almacenamiento

                File fileImagen = new File(pathCaptureItemOferta);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

                ////
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String authorities = getActivity().getPackageName() + ".provider";
                    Uri imageUri = FileProvider.getUriForFile(getActivity(), authorities, fileImagen);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
                }
                startActivityForResult(intent, Utils.COD_FOTO);
            }

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == Utils.MIS_PERMISOS){
                if(GeneralMethod.solicitaPermisosVersionesSuperiores(getActivity())){//el dos representa los 2 permisos
                    //GeneralMethod.showSnackback("Gracias por aceptar los permisos..!",mView,mActivity);
                    mostrarDialogOpciones();
                }
            }
        }
        private void RegistrarOfertaPublicidad(Oferta mItemOferta){
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage("Registrando mascota perdida...");
            progressDialog.show();

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            if(!tipoDeFoto.equals("VACIO")) {
                storageIMG(mItemOferta,mDatabase);

            }
            else{
                mItemOferta.setImgOferta(Utils.mDefaultItemOferta);
                SubirRealtimeDatabase(mItemOferta,mDatabase);
            }



        }
        private void storageIMG(final Oferta mOferta, final DatabaseReference mDatabase){
            final StorageReference mStorageImgPerfilUsuario = mStorageReference.child("Imagenes").child("Pubicidad").child(GeneralMethod.getRandomString());
            mStorageImgPerfilUsuario.putFile(mUriOfertaPublicidad).addOnSuccessListener(taskSnapshot -> mStorageImgPerfilUsuario.getDownloadUrl().addOnSuccessListener(uri -> {
                mOferta.setImgOferta(uri.toString());
                SubirRealtimeDatabase(mOferta,mDatabase);
            })).addOnFailureListener(e -> { });
        }
        private void SubirRealtimeDatabase(final Oferta mOferta, final DatabaseReference mDatabase){
            mDatabase.child("Usuarios").child(Objects.requireNonNull(mUserFireBase.getUid())).child("Publicidad").child(mOferta.getIdMarcador()).setValue(mOferta);
           // CreateMarkers(new LatLng(Double.valueOf(mMascota.getLatitud()),Double.valueOf(mMascota.getLongitud())),map, mMascota);
            FirebaseMessaging.getInstance().subscribeToTopic(mOferta.getIdMarcador()).addOnSuccessListener(aVoid -> { });
            progressDialog.dismiss();
        }



    }

    public void instanciarCargaDeDatosOferta(){
        DialogPublicar dialog = new DialogPublicar();  //Instanciamos la clase con el dialogo
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "DATOSPUBLICAR");// Mostramos el dialogo
    }


}




