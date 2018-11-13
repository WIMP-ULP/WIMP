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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout Splash = findViewById(R.id.Splash);

        Animation animationSplash = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.transition);
        Splash.startAnimation(animationSplash);

        new Intent(getApplicationContext(),LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        new Handler().postDelayed(this::AutoLogin,2500);
    }
    private PreferenciasLogin LecturaDeTipoLogin(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return new PreferenciasLogin().setTipoSignOut(sharedPreferences.getString("type_sign_out", "default"))
                .setRecordarUsuario(sharedPreferences.getBoolean("remember", true))
                .setTipoSignIn(sharedPreferences.getString("type_sign_in", "default"));}

    private void AutoLogin(){
        String res = String.valueOf(LecturaDeTipoLogin().isRecordarUsuario());
        switch (LecturaDeTipoLogin().getTipoSignIn())
        {
            case "password":
                switch (res) {
                    case "true": {
                        InicioSesion();
                    }
                    break;
                    case "false": {
                        Loguearse();
                    }break;
                }
                break;
            case"facebook":
                InicioSesion();
                break;
            case "google":
                InicioSesion();
                break;
            default:
                Loguearse();
                break;
        }
    }
    private void InicioSesion() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
    private void Loguearse() {
        SplashScreenActivity.this.startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
        SplashScreenActivity.this.finish();
    }
}
