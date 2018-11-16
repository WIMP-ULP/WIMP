package actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.whereismypet.whereismypet.R;

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
}