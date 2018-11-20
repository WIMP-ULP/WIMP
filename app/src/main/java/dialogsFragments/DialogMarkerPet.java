package dialogsFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.whereismypet.whereismypet.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import Modelo.CustomInfoWindowAdapter;
import Modelo.Marcadores;
import Modelo.Mascota;
import de.hdodenhof.circleimageview.CircleImageView;
import finalClass.GeneralMethod;
import finalClass.Utils;

import static com.whereismypet.whereismypet.R.drawable.corazon_rojo;

@SuppressLint("ValidFragment")
public class DialogMarkerPet extends DialogFragment implements View.OnClickListener{

    ///MASCOTA
    private boolean RespuestaValidacion = false;
    //Componentes
    private EditText mNombreMascotaMarcador,mDescripcionMascotaMarcador,mTelefonoMascotaMarcador;
    private CircleImageView mFotoMascotaMarcador, mFavorito,mFavoritosRojo;
    private String tipoDeFoto = "VACIO";
    //Firebase
    private FirebaseUser mUserFireBase;
    private StorageReference mStorageReference;

    //Mapa
    private LatLng latLng;
    private GoogleMap map;
    //Imagen
    private String pathCapturePets;
    private Uri mUriMascotaMarcador;

    CardView mGuardarMascota;
    private ProgressDialog progressDialog;
    private final Activity mActivityMarkerPet = this.getActivity();

    public DialogMarkerPet(GoogleMap map, LatLng latLng) {
        this.latLng = latLng;
        this.map = map;
    }
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        @SuppressLint("InflateParams")
        View content = inflater.inflate(R.layout.dialog_pets, null);
        mNombreMascotaMarcador = content.findViewById(R.id.input_nombre);
        mDescripcionMascotaMarcador = content.findViewById(R.id.input_descripcion);
        mTelefonoMascotaMarcador=content.findViewById(R.id.input_telefono);
        mFotoMascotaMarcador = content.findViewById(R.id.imgMascota);
        mFotoMascotaMarcador.setOnClickListener(this);
        mFavorito = content.findViewById(R.id.imgFavoritos);
    //    mFavorito.setOnClickListener(this);
        mFavoritosRojo=content.findViewById(R.id.imgfavoritoRojo);
      //  mFavoritosRojo.setOnClickListener(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mUserFireBase = FirebaseAuth.getInstance().getCurrentUser();

        mGuardarMascota = content.findViewById(R.id.btnGuardarMarcadorMascota);
        mGuardarMascota.setOnClickListener(this);
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

    private void CreateMarkers(LatLng latLng,GoogleMap googleMap, Mascota mMarcadorMascota) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(this.getContext().getApplicationContext()), mMarcadorMascota, this.getActivity()));
        }
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(String.valueOf(mMarcadorMascota.getIdMarcador()))
                .snippet(mMarcadorMascota.getDescripcion())
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pet_markers)));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgMascota:{
                if (GeneralMethod.solicitaPermisosVersionesSuperiores(this.getActivity())) {
                    mostrarDialogOpciones();
                }
            }break;
            case R.id.imgFavoritos:{

            }break;
            case R.id.imgfavoritoRojo:{

            }
            case R.id.btnGuardarMarcadorMascota:{
                Marcadores mMascota = new Mascota()
                        .setIdMarcador(GeneralMethod.getRandomString())
                        .setIdUsuario(Objects.requireNonNull(mUserFireBase).getUid())
                        .setNombre(mNombreMascotaMarcador.getText().toString())
                        .setDescripcion(mDescripcionMascotaMarcador.getText().toString())
                        .setLatitud(String.valueOf(latLng.latitude))
                        .setLongitud(String.valueOf(latLng.longitude))
                        .setTelefono(mTelefonoMascotaMarcador.getText().toString())
                        .setLongitud(String.valueOf(latLng.longitude))
                        .setDireccion(/*GeneralMethod.ObtenerDireccion(latLng.latitude,latLng.longitude,this.getActivity())*/"DIRECCION PROVISORIA");
                RegistrarMarcadorDeMascota((Mascota) mMascota);

            }
        }
    }

 /*   private void ocultarCorazonRojo() {
        mFavorito.setVisibility(View.INVISIBLE);
        mFavoritosRojo.setVisibility(View.VISIBLE);
    }

    private void mostrarCorazonRojo() {

       // FirebaseMessaging.getInstance().subscribeToTopic()
        mFavorito.setVisibility(View.VISIBLE);
        mFavoritosRojo.setVisibility(View.INVISIBLE);
    }

*/

    private void RegistrarMarcadorDeMascota(Mascota mMascota){
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Registrando mascota perdida...");
        progressDialog.show();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if(!tipoDeFoto.equals("VACIO")) {
            storageIMG(mMascota,mDatabase);

        }
        else{
            mMascota.setImagen(Utils.mDefaultPet);
            SubirRealtimeDatabase(mMascota,mDatabase);
        }
    }

    private void SubirRealtimeDatabase(final Mascota mMascota, final DatabaseReference mDatabase){
        mDatabase.child("Usuarios").child(Objects.requireNonNull(mUserFireBase.getUid())).child("Marcadores").child("Pet").child(mMascota.getIdMarcador()).setValue(mMascota);
        CreateMarkers(new LatLng(Double.valueOf(mMascota.getLatitud()),Double.valueOf(mMascota.getLongitud())),map, mMascota);
        FirebaseMessaging.getInstance().subscribeToTopic(mMascota.getIdMarcador()).addOnSuccessListener(aVoid -> { });
        progressDialog.dismiss();
    }
    private void storageIMG(final Mascota mMascota, final DatabaseReference mDatabase){
        final StorageReference mStorageImgPerfilUsuario = mStorageReference.child("Imagenes").child("Marcadores").child("Pet").child(GeneralMethod.getRandomString());
        mStorageImgPerfilUsuario.putFile(mUriMascotaMarcador).addOnSuccessListener(taskSnapshot -> mStorageImgPerfilUsuario.getDownloadUrl().addOnSuccessListener(uri -> {
            mMascota.setImagen(uri.toString());
            SubirRealtimeDatabase(mMascota,mDatabase);
        })).addOnFailureListener(e -> { });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Utils.COD_SELECCIONA: {
                    // URI Camara
                    mUriMascotaMarcador = Objects.requireNonNull(data).getData();
                    tipoDeFoto = "SELECCIONA";
                    try {
                        mFotoMascotaMarcador.setImageBitmap(MediaStore.Images.Media.getBitmap(DialogMarkerPet.this.getActivity().getContentResolver(), mUriMascotaMarcador));
                        mUriMascotaMarcador = GeneralMethod.reducirTamano(mUriMascotaMarcador,this.getActivity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }break;
                case Utils.COD_FOTO: {
                    MediaScannerConnection.scanFile(DialogMarkerPet.this.getActivity(), new String[]{pathCapturePets}, null,(path, uri) -> Log.i("Path", "" + path));
                    mFotoMascotaMarcador.setImageBitmap(BitmapFactory.decodeFile(pathCapturePets));
                    mUriMascotaMarcador = Uri.fromFile(new File(pathCapturePets));
                    mUriMascotaMarcador = GeneralMethod.reducirTamano(mUriMascotaMarcador,this.getActivity());
                    tipoDeFoto = "FOTO";
                } break;
            }
        } else {
            mFotoMascotaMarcador.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.huella_mascota));
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

            pathCapturePets = Environment.getExternalStorageDirectory() + File.separator + Utils.DIRECTORIO_IMAGEN
                    + File.separator + nombre;//indicamos la ruta de almacenamiento

            File fileImagen = new File(pathCapturePets);

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
            if(GeneralMethod.solicitaPermisosVersionesSuperiores(mActivityMarkerPet)){//el dos representa los 2 permisos
                //GeneralMethod.showSnackback("Gracias por aceptar los permisos..!",mView,mActivity);
                mostrarDialogOpciones();
            }
        }
    }

    private Boolean ValidarCargaDeMascota(View view) {
       /*mNombreMascotaMarcador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion=   GeneralMethod.RegexCargarMascota("nombre",view);
            }
        });
        mDescripcionMascotaMarcador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion=   GeneralMethod.RegexCargarMascota("descripcion", view);
            }
        });
        mTelefonoMascotaMarcador
                .addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion=  GeneralMethod.RegexCargarMascota("telefono", view);
            }
        });*/
        return RespuestaValidacion;

    }
}
