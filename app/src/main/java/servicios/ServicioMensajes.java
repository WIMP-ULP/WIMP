package servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.whereismypet.whereismypet.R;

import actividades.MainActivity;

public class ServicioMensajes extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
         if (remoteMessage.getNotification()!=null)
        {
            String body=remoteMessage.getNotification().getBody();
            String title=remoteMessage.getNotification().getTitle();
            String icon = remoteMessage.getNotification().getIcon();
            Intent i = new Intent(MainActivity.NOTIFICACION);
            i.putExtra("body",body);
            i.putExtra("title", title);
            if(icon!=null)
            i.putExtra("icon",icon);
            else
                i.putExtra("icon","DEFAULT");

            LocalBroadcastManager.getInstance(this).sendBroadcast(i);

        }
    }




}
