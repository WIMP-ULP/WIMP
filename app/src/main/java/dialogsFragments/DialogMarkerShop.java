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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.whereismypet.whereismypet.R;


import java.io.File;
import java.io.IOException;
import java.util.Objects;

import Modelo.CustomInfoWindowAdapter;
import Modelo.Marcadores;
import Modelo.Tienda;
import finalClass.GeneralMethod;
import finalClass.Utils;


@SuppressLint("ValidFragment")
public class DialogMarkerShop extends DialogFragment implements View.OnClickListener {
    private LatLng mLatLng;
    private GoogleMap mGoogleMap;

    ///TIENDA
    private EditText mNombreTienda,mDescripcionTienda,mTelefonoTienda,mDireccionTienda;
    private boolean RespuestaValidacion = false;
    //Componentes
    private EditText mNombreTiendaMarcador,mDescripcionTiendaMarcador,mTelefonoTiendaMarcador,mDireccionTiendaMarcador;
    private ImageView mFotoTiendaMarcador;
    //CAMARA

    private Uri mUriTiendaMarcador;
    private String tipoDeFoto = "VACIO";
    //Firebase
    private FirebaseUser mUserFireBase;
    private StorageReference mStorageReference;
    private String pathCaptureShop;

    private ProgressDialog progressDialog;

    public DialogMarkerShop(GoogleMap map, LatLng latLng) {
        this.mLatLng = latLng;
        this.mGoogleMap = map;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        @SuppressLint("InflateParams")
        View content = inflater.inflate(R.layout.dialog_shop, null);
        mNombreTiendaMarcador = content.findViewById(R.id.input_nombreShop);
        mDescripcionTiendaMarcador = content.findViewById(R.id.input_descripcionShop);
        mTelefonoTiendaMarcador = content.findViewById(R.id.input_telefonoShop);
        mDireccionTiendaMarcador = content.findViewById(R.id.input_direccionShop);
        mFotoTiendaMarcador = content.findViewById(R.id.imgMercado);
        mFotoTiendaMarcador.setOnClickListener(this);
        mUserFireBase = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content);

        builder.setPositiveButton("GUARDAR", (dialogInterface, i) -> {
            Marcadores mTienda = new Tienda()
                    .setIdPublicidad("TODAVIA NO TENGO")
                    .setIdUsuario(mUserFireBase.getUid())
                    .setIdMarcador(GeneralMethod.getRandomString())
                    .setDireccion(mDireccionTiendaMarcador.getText().toString())
                    .setLongitud(String.valueOf(mLatLng.longitude))
                    .setLatitud(String.valueOf(mLatLng.latitude))
                    .setNombre(mNombreTiendaMarcador.getText().toString())
                    .setDescripcion(mDescripcionTiendaMarcador.getText().toString())
                    .setTelefono(mTelefonoTiendaMarcador.getText().toString());
            RegistrarMarcadorDeTienda((Tienda)mTienda);
        });
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
            }
            return false;
        });
//ValidarCargaDeTienda(content);
        return builder.create();
    }
    private void CreateMarkers(LatLng latLng,GoogleMap googleMap, Tienda mMarcadorTienda) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(this.getContext().getApplicationContext()), mMarcadorTienda, this.getActivity()));
        }
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(String.valueOf(mMarcadorTienda.getNombre()))
                .snippet(mMarcadorTienda.getDescripcion())
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.shop_markers)));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgMercado:{
                if (GeneralMethod.solicitaPermisosVersionesSuperiores(this.getActivity())) {
                    mostrarDialogOpciones();
                }
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
                    mUriTiendaMarcador = Objects.requireNonNull(data).getData();
                    tipoDeFoto = "SELECCIONA";
                    try {
                        mFotoTiendaMarcador.setImageBitmap(GeneralMethod.getBitmapClip(MediaStore.Images.Media.getBitmap(DialogMarkerShop.this.getActivity().getContentResolver(), mUriTiendaMarcador)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }break;
                case Utils.COD_FOTO: {
                    MediaScannerConnection.scanFile(DialogMarkerShop.this.getActivity(), new String[]{pathCaptureShop}, null,(path, uri) -> Log.i("Path", "" + path));
                    mFotoTiendaMarcador.setImageBitmap(GeneralMethod.getBitmapClip(BitmapFactory.decodeFile(pathCaptureShop)));
                    mUriTiendaMarcador = Uri.fromFile(new File(pathCaptureShop));
                    tipoDeFoto = "FOTO";
                } break;
            }
        } else {
            mFotoTiendaMarcador.setImageBitmap(GeneralMethod.getBitmapClip(BitmapFactory.decodeResource(getResources(),R.drawable.com_facebook_profile_picture_blank_square)));
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

            pathCaptureShop = Environment.getExternalStorageDirectory() + File.separator + Utils.DIRECTORIO_IMAGEN
                    + File.separator + nombre;//indicamos la ruta de almacenamiento

            File fileImagen = new File(pathCaptureShop);

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
            if(GeneralMethod.solicitaPermisosVersionesSuperiores(this.getActivity())){//el dos representa los 2 permisos
                //GeneralMethod.showSnackback("Gracias por aceptar los permisos..!",mView,mActivity);
                mostrarDialogOpciones();
            }
        }

    }


    private void RegistrarMarcadorDeTienda(Tienda mTienda){
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Registrando tienda...");
        progressDialog.show();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if(!tipoDeFoto.equals("VACIO")) {
            storageIMG(mTienda,mDatabase);
        }
        else{
            mTienda.setImagen(Utils.mDefautMarkerShop);
            SubirRealtimeDatabase(mTienda,mDatabase);
        }
    }

    private void SubirRealtimeDatabase(final Tienda mTienda, final DatabaseReference mDatabase){
        mDatabase.child("Usuarios").child(Objects.requireNonNull(mUserFireBase.getUid())).child("Marcadores").child("Shop").child(mTienda.getIdMarcador()).setValue(mTienda);
        CreateMarkers(new LatLng(Double.valueOf(mTienda.getLatitud()),Double.valueOf(mTienda.getLongitud())),mGoogleMap,mTienda);
        progressDialog.dismiss();
    }
    private void storageIMG(final Tienda mTienda, final DatabaseReference mDatabase ){
        final StorageReference mStorageImgPerfilUsuario = mStorageReference.child("Imagenes").child("Marcadores").child("Shop").child(GeneralMethod.getRandomString());
        mStorageImgPerfilUsuario.putFile(mUriTiendaMarcador).addOnSuccessListener(taskSnapshot -> mStorageImgPerfilUsuario.getDownloadUrl().addOnSuccessListener(uri -> {
            mTienda.setImagen(uri.toString());
            SubirRealtimeDatabase(mTienda,mDatabase);
        })).addOnFailureListener(e -> { });

    }


  /*  private Boolean ValidarCargaDeTienda(View view) {


        mNombreTienda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion =     GeneralMethod.RegexCargarMascota("nombre",view);

            }
        });
        mDescripcionTienda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion =   GeneralMethod.RegexCargarMascota("descripcion",view );
            }
        });
        mTelefonoTienda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion =  GeneralMethod.RegexCargarMascota("telefono", view);
            }
        });
        mDireccionTienda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexCargarMascota("direccion", view);
            }
        });
        return RespuestaValidacion;
    }
*/






}
