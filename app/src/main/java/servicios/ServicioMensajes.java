package servicios;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.whereismypet.whereismypet.R;

import actividades.MainActivity;

public class ServicioMensajes extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
private Notificacion notificacion;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
         if (remoteMessage.getNotification()!=null)
        {

        }
    }
private void Notificacion(String body, String title, String icon)
{
    Intent i = new Intent(MainActivity.NOTIFICACION);
    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
}

}
