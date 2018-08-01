package app.com.spoonpot.servicies;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

import app.com.spoonpot.R;
import app.com.spoonpot.activity.MainActivity;
import app.com.spoonpot.activity.NotificationActivity;
import app.com.spoonpot.config.AppPreferences;
import app.com.spoonpot.helpers.DataBase;

import static app.com.spoonpot.helpers.DataBase.TABLE_MSG;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private AppPreferences app;
    private DataBase db;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        //Log.d(TAG, "From: " + remoteMessage.getFrom());


        try {
            app = new AppPreferences(getApplicationContext());
            db = new DataBase(getApplicationContext());

            if(remoteMessage.getData()!=null) {

                JSONObject res;
                res = new JSONObject(remoteMessage.getData());

                String title =  res.getString("title");
                String body = res.getString("body");
                String msg = res.getString("msg");
               // String device = res.getString("device");

                String firebaseid= "";
                if(res.getString("firebaseId")!=null)
                {
                    firebaseid=res.getString("firebaseId");
                }
                //
                //Log.d(TAG, "message" + res.getString("title"));
                final NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                final NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_spoon_blanco)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setColor(Color.parseColor("#66ce95"));

                if (title == null) {
                    builder.setContentTitle(getString(R.string.app_name));
                } else {
                    builder.setContentTitle(title);

                }
                builder.setContentText(body);
                // Build stack
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Add parent activity
                stackBuilder.addParentStack(MainActivity.class);

                Intent intent = new Intent(this, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder.addNextIntent(intent);
                // Get PendingIntent from the stack



                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                // Asignar intent y establecer true para notificar como aviso
                builder.setFullScreenIntent(pendingIntent, false);
                // Remove notification when interacting with her
                builder.setAutoCancel(true);
                if (app.getSound().equals("1")) {
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                }
                if (app.getVibrate().equals("1")) {
                    builder.setDefaults(Notification.DEFAULT_VIBRATE);
                }
                if (app.getLight().equals("1")) {
                    builder.setDefaults(Notification.DEFAULT_LIGHTS);

                }
                int count = 0;
                count = new Random().nextInt(5000 - 5 + 1) + 5;
                if(msg.equals("N"))
                {
                    app.setNoti(Integer.parseInt(app.getNoti())+1);
                }else
                {


                    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MSG
                                    + " WHERE firebaseid = ?",
                            new String[] { firebaseid });

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();

                        if(cursor.getInt(cursor.getColumnIndex("contador"))==0)
                        {
                            app.setMensajes(Integer.parseInt(app.getMensajes())+1);

                        }
                        count=cursor.getInt(cursor.getColumnIndex("numeracion"));
                        db.delete(TABLE_MSG, "firebaseid = '"+firebaseid+"'", null);
                        guardar(firebaseid,count);

                        /*count= cursor.getInt(cursor
                                .getColumnIndex("_id"));
                        txtNombre.setText(cursor.getString(cursor
                                .getColumnIndex("nombre")));
                        txtCedula.setText(cursor.getString(cursor
                                .getColumnIndex("cedula")));*/
                    }else
                    {
                        guardar(firebaseid,count);
                        app.setMensajes(Integer.parseInt(app.getMensajes())+1);
                    }
                    cursor.close();
                    cursor = null;


                }
                db.close();

                builder.setPriority(Notification.PRIORITY_MAX);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT)
                {
                    builder.setCategory(Notification.CATEGORY_EVENT);
                }


                mNotificationManager.notify(count, builder.build());


                final int finalCount = count;




                Thread thread = new Thread() {
                    public void run() {
                        Looper.prepare();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do Work
                                handler.removeCallbacks(this);
                                Looper.myLooper().quit();

                                builder.setFullScreenIntent(null,false);
                                mNotificationManager.notify(finalCount,builder.build());

                            }
                        }, 3000);

                        Looper.loop();
                    }
                };
                thread.start();





            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


    }

    public int guardar(String firebaseId,int numeracion)
    {
        db.open();
        ContentValues vals = new ContentValues();
        vals.put("firebaseid", firebaseId);
        vals.put("contador", 1);
        vals.put("numeracion",numeracion);
        Long id =db.insert(TABLE_MSG, vals);
        db.close();
        return id.intValue();
    }
}


