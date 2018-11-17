package servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.whereismypet.whereismypet.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import actividades.MainActivity;
import finalClass.GeneralMethod;

public class Notificacion extends BroadcastReceiver{
    String body;
    String title;
    String icon;
    public void onReceive(Context context, Intent intent) {
        body= intent.getStringExtra("body");
        title = intent.getStringExtra("title");
        icon = intent.getStringExtra("icon");
       new ManejaNotificacion(context)
               .mostrarNotificacion(body,title,icon);
//noti.mostrarNotificacion();
    }
}

 class ManejaNotificacion{
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    //Attributes
    private long timeOut = -1;

    public ManejaNotificacion(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public void mostrarNotificacion(String body, String title,String icon) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent( context , MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),PendingIntent.FLAG_ONE_SHOT);

        Uri sonido=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icono= getBitmapFromURL(icon);
        Uri ic =  Uri.parse( icon );
        //com.whereismypet.whereismypet FIRMA
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.dialog_notificaciones);
        notificationLayout.setTextViewText(R.id.titleNotificacion, title);
        notificationLayout.setTextViewText(R.id.bodyNotificacion,body);
        if(icon=="DEFAULT")
        {
            notificationLayout.setImageViewResource(R.id.imgNoti, R.drawable.icon_app);
        }else{
            notificationLayout.setImageViewUri(R.id.imgNoti, ic);
        }

        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        mBuilder.setLargeIcon(icono)
        //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                //.setCustomBigContentView(notificationLayoutExpanded)
                .setTimeoutAfter(timeOut)
                .setSound(sonido)
                .setContentIntent(pendingIntent)
                //.setLocalOnly(true)
                .setLights(Color.GREEN, 1000, 1000)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        assert notificationManager != null;
        notificationManager.notify(R.drawable.icon_app, mBuilder.build());



    }
}
