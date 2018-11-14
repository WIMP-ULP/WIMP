package dialogsFragments;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import Modelo.Usuario;
import finalClass.GeneralMethod;
import finalClass.Utils;

public class DialogRegistry extends DialogFragment implements View.OnClickListener {
    //Componentes Registro
    private EditText mNombreRegistroEditText, mApellidoRegistroEditText, mEmailRegistroEditText, mPassRegistroEditText,
            mPassConfirmRegistroEditText,mEmailConfirmarRegistroEditText;
    private ProgressDialog progressDialog;
    private ConstraintLayout mContrainerRegistro;
    //Validaciones
    private boolean RespuestaValidacion = false;
    //Imagen
    private String tipoDeFoto = "VACIO",pathTomarFoto;
    private Uri mFotoPerfilRegistro;
    private ImageView mImgPerfilDBRegistroImageView;
    // Activity
    private Activity mActivityRegistry;
    //............................................
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabase;
    FirebaseUser mUserFireBase;
    StorageReference mStorageReference;
    //----------------------------------------CICLOS DE VIDA DEL DIALOG-------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.dialog_registro, null);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserFireBase = mFirebaseAuth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mActivityRegistry = this.getActivity();
        //Vistas
        CardView btnRegistro = content.findViewById(R.id.CheckIn);
        mNombreRegistroEditText = content.findViewById(R.id.nombreRegistro);
        mApellidoRegistroEditText = content.findViewById(R.id.apellidoRegistro);
        mEmailRegistroEditText = content.findViewById(R.id.emailRegistro);
        mEmailConfirmarRegistroEditText=content.findViewById(R.id.confirmar_emailRegistro);
        mPassRegistroEditText = content.findViewById(R.id.passRegistro);
        mPassConfirmRegistroEditText = content.findViewById(R.id.confirmpassRegistro);
        mContrainerRegistro = content.findViewById(R.id.ContenedorRegistro);
        mImgPerfilDBRegistroImageView = content.findViewById(R.id.imgPerfilDBRegistro);
        btnRegistro.setOnClickListener(this);
        mImgPerfilDBRegistroImageView.setOnClickListener(this);

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
    public void onResume() {
        super.onResume();
        ValidarRegistro();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Utils.COD_SELECCIONA: {
                    try {
                        mFotoPerfilRegistro = data.getData();
                        mFotoPerfilRegistro = GeneralMethod.reducirTamano(mFotoPerfilRegistro, mActivityRegistry);
                        mImgPerfilDBRegistroImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivityRegistry.getContentResolver(), mFotoPerfilRegistro));
                        tipoDeFoto = "SELECCIONA";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case Utils.COD_FOTO: {
                    MediaScannerConnection.scanFile(mActivityRegistry, new String[]{pathTomarFoto}, null,
                            (path, uri) -> Log.i("Path", "" + path));
                    mImgPerfilDBRegistroImageView.setImageBitmap(BitmapFactory.decodeFile(pathTomarFoto));
                    mFotoPerfilRegistro = Uri.fromFile(new File(Objects.requireNonNull(pathTomarFoto)));
                    mFotoPerfilRegistro = GeneralMethod.reducirTamano(mFotoPerfilRegistro, mActivityRegistry);
                    tipoDeFoto = "FOTO";
                }break;
            }
        }
    }
    // Click de boton registar y imagenview de imagenPerfil
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CheckIn: {
                RegistrarUsuarioEmailPassword();
            }
            break;
            case R.id.imgPerfilDBRegistro: {
                if (GeneralMethod.solicitaPermisosVersionesSuperiores(mActivityRegistry)) {
                    mostrarDialogOpciones(mActivityRegistry);
                }
            }
            break;
        }
    }

    //METODO DE REGISTRO FIREBASE
    private void RegistrarUsuarioEmailPassword() {
        if (ValidarRegistro()) {
            final Usuario mUser = new Usuario()
                    .setEmail(mEmailRegistroEditText.getText().toString().trim())
                    .setContraseña(mPassRegistroEditText.getText().toString());
            progressDialog = new ProgressDialog(mActivityRegistry);
            progressDialog.setMessage("Registrando...");
            progressDialog.show();
            mFirebaseAuth.createUserWithEmailAndPassword(mUser.getEmail(), mUser.getContraseña())
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            final Usuario.UsuarioPublico mUserPublic = new Usuario.UsuarioPublico()
                                    .setNombre(mNombreRegistroEditText.getText().toString())
                                    .setApellido(mApellidoRegistroEditText.getText().toString())
                                    .setIdUsuario(task.getResult().getUser().getUid());



                            if(!tipoDeFoto.equals("VACIO")) {
                                storageIMG(mUserPublic);
                            }
                            else{
                                mUserPublic.setImagen(Utils.mDefaultUser);
                                SubirRealtimeDatabase(mUserPublic);
                            }
                            final FirebaseUser firebaseUser = Objects.requireNonNull(task.getResult()).getUser();
                            firebaseUser.sendEmailVerification();
                            dismiss();
                            //GeneralMethod.showSnackback("Registro exitoso, gracias por registrarse!",mContainerLogin,LoginActivity.this);
                            //poner este mensaje en el activity main porque se autologue de inicio
                        } else {
                            Toast.makeText(mActivityRegistry, "Ocurrio un inconveniente al intentar registrar el email, por favor, vuelva a intentarlo",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            GeneralMethod.showSnackback("Algunos campos se encuentran vacios, por favor verifiquelos",mContrainerRegistro,mActivityRegistry);
        }
    }
    private void SubirRealtimeDatabase(final Usuario.UsuarioPublico mUserPublic){
        mDatabase.child("Usuarios").child(mUserPublic.getIdUsuario()).child("Datos Personales").setValue(mUserPublic);
        progressDialog.dismiss();
    }
    public void storageIMG (final Usuario.UsuarioPublico mUserPublic) {
        final StorageReference mStorageImgPerfilUsuario =  mStorageReference.child("Imagenes").child("Perfil").child(GeneralMethod.getRandomString());
        mStorageImgPerfilUsuario.putFile(mFotoPerfilRegistro).addOnSuccessListener(taskSnapshot -> mStorageImgPerfilUsuario.getDownloadUrl().addOnSuccessListener(uri -> {
            mUserPublic.setImagen(uri.toString());
            SubirRealtimeDatabase(mUserPublic);
        })).addOnFailureListener(e -> { });
    }
    // METODOS COMPROBACION CAMPOS Y GENERANDO NOMBRE IMAGEN
    private boolean ValidarRegistro(){
        mNombreRegistroEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexRegistro("nombre",mContrainerRegistro);
            }
        });
        mApellidoRegistroEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexRegistro("apellido",mContrainerRegistro);
            }
        });
        mEmailRegistroEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexRegistro("email",mContrainerRegistro);
            }
        });
        mEmailConfirmarRegistroEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexRegistro("confirmaremail",mContrainerRegistro);
            }
        });
        mPassRegistroEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexRegistro("password",mContrainerRegistro);
            }
        });
        mPassConfirmRegistroEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                RespuestaValidacion = GeneralMethod.RegexRegistro("confirmacontraseña",mContrainerRegistro);
            }
        });
        return RespuestaValidacion;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.MIS_PERMISOS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                GeneralMethod.showSnackback("Gracias por aceptar los permisos..!",mContrainerRegistro,mActivityRegistry);
                mostrarDialogOpciones(this.getActivity());
            }
        }
    }
    // CAMARA
    public void mostrarDialogOpciones(final Context mActivity) {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
        new AlertDialog.Builder(mActivity)
                .setTitle("Elige una Opción")
                .setItems(opciones, (dialogInterface, i) -> {
            if (opciones[i].equals("Tomar Foto")) {
                abrirCamara(mActivity);
            } else {
                if (opciones[i].equals("Elegir de Galeria")) {
                    Intent intent = new Intent(Intent.ACTION_PICK).setType("image/");
                    if (intent.resolveActivity(mActivity.getPackageManager()) != null)
                        startActivityForResult(Intent.createChooser(intent, "Seleccione"), Utils.COD_SELECCIONA);
                } else {
                    dialogInterface.dismiss();
                }
            }
        }).show();
    }

    private void abrirCamara(Context mActivity) {
        File miFile = new File(Environment.getExternalStorageDirectory(), Utils.DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();
        if (!isCreada) {
            isCreada = miFile.mkdirs();
        }

        if (isCreada) {
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = consecutivo.toString() + ".jpg";

            pathTomarFoto = Environment.getExternalStorageDirectory() + File.separator + Utils.DIRECTORIO_IMAGEN
                    + File.separator + nombre;

            //Imagen
            File fileImagen = new File(pathTomarFoto);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authorities = mActivity.getPackageName() + ".provider";
                Uri imageUri = FileProvider.getUriForFile(mActivity, authorities, fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            startActivityForResult(intent, Utils.COD_FOTO);
        }
    }
}
