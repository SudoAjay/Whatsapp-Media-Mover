package com.sudoajay.whatapp_media_mover_to_sdcard.ForegroundService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.sudoajay.whatapp_media_mover_to_sdcard.Background_Task.WorkMangerTaskA;
import com.sudoajay.whatapp_media_mover_to_sdcard.Background_Task.WorkMangerTaskB;
import com.sudoajay.whatapp_media_mover_to_sdcard.Main_Navigation;
import com.sudoajay.whatapp_media_mover_to_sdcard.Notification.NotifyNotification;
import com.sudoajay.whatapp_media_mover_to_sdcard.R;
import com.sudoajay.whatapp_media_mover_to_sdcard.sharedPreferences.TraceBackgroundService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class Foreground extends Service {

    public static final String CHANNEL_ID = "Foreground Service";
    private TraceBackgroundService traceBackgroundService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // create object
        traceBackgroundService = new TraceBackgroundService(getApplicationContext());

        if (Objects.requireNonNull(intent.getStringExtra("com.sudoajay.whatapp_media_mover_to_sdcard.ForegroundService"))
                .equalsIgnoreCase("Start_Foreground")) {

            createNotificationChannel();

            final String url = "https://dontkillmyapp.com/problem";
            Intent knowMoreIntent = new Intent(Intent.ACTION_VIEW);
            knowMoreIntent.setData(Uri.parse(url));

            Intent stopIntent = new Intent(getApplicationContext(), Main_Navigation.class);
            stopIntent.setAction("Stop_Foreground(Setting)");

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setDefaults(Notification.DEFAULT_ALL)
                    // Set required fields, including the small icon, the
                    // notification title, and text.
                    .setContentTitle("Foreground Service")

                    // All fields below this line are optional.

                    // Use a default priority (recognized on devices running Android
                    // 4.1 or later)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    //     .setSound(uri)
                    // Provide a large icon, shown with the notification in the
                    // notification drawer on devices running Android 3.0 or later.

                    // Show a number. This is useful when stacking notifications of
                    // a single type.
                    .setNumber(1)

                    // If this notification relates to a past or upcoming event, you
                    // should set the relevant time information using the setWhen
                    // method below. If this call is omitted, the notification's
                    // timestamp will by set to the time at which it was shown.
                    // TODO: Call setWhen if this notification relates to a past or
                    // upcoming event. The sole argument to this method should be
                    // the notification timestamp in milliseconds.
                    //.setWhen(...)

                    // Set the pending intent to be initiated when the user touches
                    // the notification.
                    .addAction(R.drawable.know_more_icon,
                            this.getString(R.string.why_This_Foreground_Service),
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    knowMoreIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .addAction(R.drawable.stop_icon,
                            this.getString(R.string.stop_Foreground_Service),
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    stopIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))


                    // Show an expanded list of items on devices running Android 4.1
                    // or later.


                    // Example additional actions for this notification. These will
                    // only show on devices running Android 4.1 or later, so you
                    // should ensure that the activity in this notification's
                    // content intent provides access to the same actions in
                    // another way.


                    // Automatically dismiss the notification when it is touched.
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(
                            this,
                            0,
                            new Intent(this, Main_Navigation.class),
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.mipmap.ic_launcher);

            startForeground(1, notification.build());

            // check if date matches then run the process

            // first Process or Task A
            if (DatesMatches(traceBackgroundService.getTaskA()))
                WorkMangerTaskA.getWork(getApplicationContext());

            // Second Process or Task B
            if (DatesMatches(traceBackgroundService.getTaskB()))
                WorkMangerTaskB.getWork(getApplicationContext());

            // Third Process or Task C
            if (DatesMatches(traceBackgroundService.getTaskC())) {
                NotifyNotification notify_notifications = new NotifyNotification(this);
                notify_notifications.notify("Size Of Duplication Data");
            }
        } else if (Objects.requireNonNull(intent.getStringExtra("com.sudoajay.whatapp_media_mover_to_sdcard.ForegroundService"))
                .equalsIgnoreCase("Stop_Foreground")) {
            //your  service end here
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void OnStop() {
        stopSelf();
    }

    private boolean DatesMatches(final String date) {

        try {
            // check if its matches then start the process


            // set The Today Date
            Calendar todayCalender = Calendar.getInstance();
            Date todayDate = todayCalender.getTime();

            // convert to Date
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

            Date curDate = format.parse(date);

            if (todayDate.after(curDate) && todayDate.before(curDate)) return true;
            return false;
        } catch (ParseException e) {
            return false;
        }
    }
}