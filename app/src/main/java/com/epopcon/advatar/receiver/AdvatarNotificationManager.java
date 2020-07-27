package com.epopcon.advatar.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.epopcon.advatar.R;

public class AdvatarNotificationManager extends BroadcastReceiver {

    private static final String TAG = AdvatarNotificationManager.class.getSimpleName();

    private Context context;
    private NotificationManager notificationManager;

    private static AdvatarNotificationManager instance;

    public static synchronized AdvatarNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdvatarNotificationManager(context);
        }
        return instance;
    }

    private AdvatarNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @SuppressLint("WrongConstant")
    public void openNotification(String title, String message, int notificationId, Intent targetIntent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel mChannel = new NotificationChannel("Advatar", "알림", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(context, mChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);

        // 그러나 삼성폰은 일반 아이콘 사용 가능
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer.toLowerCase().contains("samsung")) {
            useWhiteIcon = false;
        }

        int icon = useWhiteIcon ? R.drawable.ic_launcher_foreground : R.drawable.ic_launcher_foreground;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setWhen(System.currentTimeMillis())
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setTicker(title)
                //.setColor(context.getResources().getColor(R.color.common_google_signin_btn_text_light_default)) // 삼성 야간모드인 경우 타이틀이 안보여 제거함.
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH | Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        message = message.replaceAll("\\\\n", "\n");
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.bigText(message).setBigContentTitle(title);
        builder.setStyle(style);

        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);
    }

    public void cancelNotification(int category) {
        try {
            notificationManager.cancel(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}

