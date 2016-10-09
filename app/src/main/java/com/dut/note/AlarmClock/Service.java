package com.dut.note.AlarmClock;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.lib.SharedObject;
import com.dut.note.ui.ViewNote;

/**
 * Created by VoTung246 on 19/10/2015.
 */
public class Service extends IntentService {
    // Notification hien thi mot thong bao
    // Sets an ID for the notification
    private static final  int NOTIFICATION_ID = 1;
    private static final String TAG = "NOTE ALARM";
    private NotificationManager alarmNotificationManager;
    Note note;

    public Service(){
        super("Service");
    }

    public void onHandIntent(Intent intent){
        //dong thong bao nay se hien khi chung ta keo man hinh xuong.

    }
    private void sendNotification(String msg){
        Log.d("Service","Preparing to send notification:"+msg);
        // Gets an instance of the NotificationManager service
        alarmNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(ViewNote.class in this case)
        SharedObject.getInstance().set(ViewNote.SHARED_KEY_NOTE,note);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,
                new Intent(this,ViewNote.class),0);
        /**
         * NOI DUNG THONG BAO SAU KHI VUOT MAN HINH XUONG
         * .setContentTitle(TAG); // in ra thong bao NOTE ALARM
         * .setContentText(msg); //in ra noi dung thong bao la tieu de cua NOTE khi ta vuot man hinh xuong
         * .setSmallIcon(); // in ra Icon cua san pham
         */
        NotificationCompat.Builder alarmNotificationBuider = new NotificationCompat.Builder(this)
                .setContentTitle(TAG)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setOngoing(true).setSmallIcon(R.drawable.ic_cast_light);
        alarmNotificationBuider.setContentIntent(contentIntent);
        // Builds the notification and issues it.
        alarmNotificationManager.notify(NOTIFICATION_ID, alarmNotificationBuider.build());
        Log.i("Service","Notification sent...");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //sendNotification("You have a new notification! Click to view!");
    }
}
