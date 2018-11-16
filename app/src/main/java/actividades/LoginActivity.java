package actividades;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.whereismypet.whereismypet.R;



import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import Modelo.PreferenciasLogin;
import Modelo.Usuario;
import dialogsFragments.DialogRegistry;
import finalClass.GeneralMethod;
import finalClass.Utils;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private EditText mEmailEditTextLogin, mPasswordEditTextLogin;
    private ConstraintLayout mContainerLogin;
    private CheckBox mRecordarUsuarioCheckBox;
    //FIREBASE-----
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUserFireBase;
    //FACEBOOK--
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    //GOOGLE------
    private GoogleSignInClient mGoogleSignInClient;

    //Preferencias
    private SharedPreferences sharedPreferences;
    private PreferenciasLogin mPreferenciasLogin;


    private TextInputLayout correoText;
    private TextInputLayout contraseñaText;
    //----------------------------------------CICLOS DE VIDA DE ACTIVITY-------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        correoText=findViewById(R.id.InputLayoutEmail);
        contraseñaText=findViewById(R.id.textInputLayoutPassword);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Auth-Database FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUserFireBase = mFirebaseAuth.getCurrentUser();
        //Metodos de Login
        LoginGoogle();
        EscuchandoEstadoDeAutenticacion();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mPreferenciasLogin = new PreferenciasLogin();
        //Inicializar las vistas
        final CardView mIniciarCardView = findViewById(R.id.btnIniciarLogin);
        final TextView mRegistroTextView = findViewById(R.id.tvRegistrarseLogin);
        final TextView mOlvidoContrasenaTextView = findViewById(R.id.tvOlvidoContraseñaLogin);
        mRecordarUsuarioCheckBox = findViewById(R.id.RecordarSesion);
        mContainerLogin = findViewById(R.id.ContainerLogin);
        mEmailEditTextLogin = findViewById(R.id.CorreoLogin);
        mPasswordEditTextLogin = findViewById(R.id.PasswordLogin);
        loginButton = findViewById(R.id.login_button);
        mRecordarUsuarioCheckBox.setOnClickListener(this);

        //mRecordarUsuarioCheckBox.setChecked(LecturaDeTipoLogin().isRecordarUsuario());

        //GOOGLE
        SignInButton mSignInButton = findViewById(R.id.sign_in_button);
        callbackManager = CallbackManager.Factory.create();

        //Botones
        mIniciarCardView.setOnClickListener(this);
        mRegistroTextView.setOnClickListener(this);
        mOlvidoContrasenaTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);

        //KeyHash();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //   ValidarLogin();
        // LimpiarEditText();
        /*mEmailEditText.addTextChangedListener(new GeneralMethod.addListenerOnTextChange(this,mEmailEditText,String tipo));
        mPasswordEditText.addTextChangedListener(new GeneralMethod.addListenerOnTextChange(this,mPasswordEditText));*/
    }

    //--------------------------------------ESCUCHADOR DE AUTENTICACION, POR SI CAMBIA DE LOGUEO------------------------------------
    private void EscuchandoEstadoDeAutenticacion() {

        mAuthStateListener = firebaseAuth -> {
            mUserFireBase = firebaseAuth.getCurrentUser();
            if (mUserFireBase != null && LecturaLogin().getTipoSignOut().equals("default")) {
                InicioSesionCorrecto();
            }
            /*else
                GeneralMethod.showSnackback("El correo no se encuentra verificado, por favor verifique el correo",mContainerLogin,this);*/
        };
    }
    //-------------------------------------------PREFERENCIAS------------------------------------------------------------------
    private PreferenciasLogin LecturaLogin(){
        return new PreferenciasLogin().setTipoSignOut(sharedPreferences.getString("type_sign_out", "default"))
                .setRecordarUsuario(sharedPreferences.getBoolean("remember", true))
                .setTipoSignIn(sharedPreferences.getString("type_sign_in", "default"));
    }

    private void GuardarSignIn(final PreferenciasLogin signIn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type_sign_in",signIn.getTipoSignIn());
        editor.putString("type_sign_out",signIn.getTipoSignOut());
        editor.putBoolean("remember",signIn.isRecordarUsuario());
        editor.apply();
    }

    //-------------------------------------------OBTENER HASH DEL PROYECTO PARA IDENTIFICARLO----------------------------------
    private void KeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.whereismypet.whereismypet", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
    }
    //-------------------------------------------METODO DE CAMBIO DE ACTIVITY A LA PRINCIPAL----------------------------------
    public void InicioSesionCorrecto() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
    //-------------------------------------------METODOS QUE VACIO LOS MENSAJES Y DATOS DE LOS COMPONENTES Y OTRO LLAMO A LAS VALIDACIONES CUANDO CAMBIA EL TEXT DEL EDITTEXT----------------------------------
    private void LimpiarEditText() {
        mEmailEditTextLogin.setText("");
        mPasswordEditTextLogin.setText("");
        mEmailEditTextLogin.setError(null);
        mPasswordEditTextLogin.setError(null);
    }

    private void ValidarLogin() {
        mEmailEditTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                GeneralMethod.RegexLogin("correo", LoginActivity.this);
                GeneralMethod.RegexLogin("correovacio", LoginActivity.this);
            }
        });
        mPasswordEditTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                GeneralMethod.RegexLogin("contrasenavacio", LoginActivity.this);
            }
        });
    }
    //---------------------------------------FIREBASE EMAIL---------------------------------------------------
    private void LoginEmailPassword() {
        final String mMsgShowSnackBarVerificado = "El correo no se encuentra verificado, por favor verifique el correo",
                mMsgShowSnackBarEmailPassword = "Usuario o Contraseña incorrecto, por favor vuelva a ingresarlos.!",
                mMsgShowSnackBarCurrentUser = "Cuenta no registrada, registra la cuenta o elija alguna de las opciones";
        final String mEmailStringEditTextLogin = mEmailEditTextLogin.getText().toString().trim(),
                mPasswordStringEditTextLogin = mPasswordEditTextLogin.getText().toString();
        if(mFirebaseAuth != null){
            if (mUserFireBase != null) {
                mUserFireBase.reload().addOnCompleteListener(task -> {
                    if (mUserFireBase.isEmailVerified()) {

                        mFirebaseAuth.signInWithEmailAndPassword(mEmailStringEditTextLogin, mPasswordStringEditTextLogin)
                                .addOnCompleteListener(LoginActivity.this, taskSignInWithEmailAndPassword -> {
                                    if (taskSignInWithEmailAndPassword.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Iniciando", LENGTH_SHORT).show();
                                        InicioSesionCorrecto();

                                    } else
                                        GeneralMethod.showSnackback(mMsgShowSnackBarEmailPassword,mContainerLogin,LoginActivity.this);
                                });
                    } else
                        GeneralMethod.showSnackback(mMsgShowSnackBarVerificado,mContainerLogin,LoginActivity.this);
                });
            } else {
                AuthCredential mAuthCredential = EmailAuthProvider
                        .getCredential(mEmailStringEditTextLogin, mPasswordStringEditTextLogin);
                mFirebaseAuth.signInWithCredential(mAuthCredential)
                        .addOnCompleteListener(task -> InicioSesionCorrecto())
                        .addOnFailureListener(e -> GeneralMethod.showSnackback(mMsgShowSnackBarCurrentUser,mContainerLogin,LoginActivity.this));
            }
        }
    }


    //---------------------------------------GOOGLE------------------------------------------------------------
    private void LoginGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if(acct.getIdToken() != null){
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mFirebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            InicioSesionCorrecto();
                            GeneralMethod.showSnackback("Bienvenido a WIMP?",mContainerLogin,LoginActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            GeneralMethod.showSnackback("Lo sentimos,pero la autentificacion fallo",mContainerLogin,LoginActivity.this);
                        }
                    });
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Utils.RC_SIGN_IN);
    }



    private void revokeAccess() {
        // Firebase sign out
        mFirebaseAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                task -> { });
    }



    //---------------------------------------FACEBOOK----------------------------------------------------------
    private void LoginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Utils.mPermisosNecesariosFacebook);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        InicioSesionCorrecto();
                    } else
                        Toast.makeText(LoginActivity.this, R.string.auth_failed, LENGTH_SHORT).show();

                });
    }

    //---------------------------------------CAPTURO EL RESULTADO DE LA ACTIVIDAD LUEGO DE LOGUEARSE CON LAS REDES------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (mPreferenciasLogin.getTipoSignIn()) {
            case "facebook": {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
            break;
            case "google": {
                if (requestCode == Utils.RC_SIGN_IN) {
                    GoogleSignInResult mGoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = mGoogleSignInResult.getSignInAccount();
                    if(account != null) firebaseAuthWithGoogle(account);
                }
            }
            break;
            default:break;
        }


    }

    //------------------------------------------------METODOS SOBREESCRITOS POR LA IMPLEMENTACION DE LA INTERFACES DE LA CLASE-------------------------------------
    // ESTE METODO ES POR DI FALLA LA CONECCION CON LA API DE GOOGLE, LO SOBREESCRIBO POR LA IMPLEMENTACION EN LA CLASE
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }
    //METODO ONCLICK DE LOS COMPONENTES(BOTONES,TEXTVIEW)
    @Override
    public void onClick(View v) {
        final String mMsgShowSnackBar = "Todos los campos deben estar completos correctamente, por favor verifiquelos";
        switch (v.getId()) {
            case R.id.btnIniciarLogin: {
                //   if (GeneralMethod.RegexLogin("correo", this) && GeneralMethod.RegexLogin("contrasenavacio", this)) {
                mPreferenciasLogin.setTipoSignIn("password").setRecordarUsuario(mRecordarUsuarioCheckBox.isChecked());
                mPreferenciasLogin.setTipoSignOut("default");
                GuardarSignIn(mPreferenciasLogin);
                LoginEmailPassword();
                //}
                //else
                //  GeneralMethod.showSnackback(mMsgShowSnackBar,mContainerLogin,LoginActivity.this);
            }
            break;
            case R.id.tvRegistrarseLogin: {
                instanciarDialogRegistro();
            }
            break;
            case R.id.login_button: {
                mPreferenciasLogin.setTipoSignIn("facebook");
                mPreferenciasLogin.setTipoSignOut("default");
                GuardarSignIn(mPreferenciasLogin);
                LoginFacebook();
            }
            break;
            case R.id.sign_in_button: {
                mPreferenciasLogin.setTipoSignIn("google");
                mPreferenciasLogin.setTipoSignOut("default");
                GuardarSignIn(mPreferenciasLogin);
                signIn();
            }
            break;
            default:
                break;
        }
    }

    //------------------------------------------------------------------------DIALOGO REGISTRO---------------------------------------------------------------

    public void instanciarDialogRegistro() {
        DialogRegistry dialog = new DialogRegistry();  //Instanciamos la clase con el dialogo
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "REGISTRO");// Mostramos el dialogo
    }
}