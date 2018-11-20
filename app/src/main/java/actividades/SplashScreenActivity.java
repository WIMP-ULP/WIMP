package actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.whereismypet.whereismypet.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Modelo.PreferenciasLogin;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        RelativeLayout Splash = findViewById(R.id.Splash);
        Animation animationSplash = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.transition);
        Splash.startAnimation(animationSplash);
        new Intent(getApplicationContext(),LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        new Handler().postDelayed(this::AutoLogin,2500);
        KeyHash();
    }
    private PreferenciasLogin LecturaDeTipoLogin(){
        return new PreferenciasLogin().setTipoSignOut(mSharedPreferences.getString("type_sign_out", "default"))
                .setRecordarUsuario(mSharedPreferences.getBoolean("remember", true))
                .setTipoSignIn(mSharedPreferences.getString("type_sign_in", "default"));}


    private void AutoLogin(){
        String as = LecturaDeTipoLogin().getTipoSignIn();
        String sa = LecturaDeTipoLogin().getTipoSignOut();
        if(!LecturaDeTipoLogin().getTipoSignIn().equals("default")){
            if(!LecturaDeTipoLogin().getTipoSignOut().equals("default")){
               Loguearse();
            }
            else{ InicioSesion(); }
        }
       else{ Loguearse();}


    }

    private void InicioSesion() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
    private void Loguearse() {
        startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
        finish();
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
}