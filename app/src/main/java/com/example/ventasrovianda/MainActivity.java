package com.example.ventasrovianda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavDeepLinkBuilder;
import androidx.room.Room;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.ventasrovianda.Utils.Models.ModeOfflineModel;
import com.example.ventasrovianda.Utils.SincronizationWorker;
import com.example.ventasrovianda.Utils.ViewModelStore;
import com.example.ventasrovianda.Utils.bd.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            this.appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "rovisapi").build();
            ViewModelStore model = new ViewModelProvider(this).get(ViewModelStore.class);
            ModeOfflineModel modeOfflineModel = new ModeOfflineModel();
            modeOfflineModel.setUsername("Usuario de prueba");
            model.saveStore(modeOfflineModel);
            checkPermission();
            ActivityCompat.requestPermissions(this,new String[]{}, 1);
            createNotificationChannel();
        PeriodicWorkRequest synchronizationRovianda =
                new PeriodicWorkRequest.Builder(SincronizationWorker.class,PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS,PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,TimeUnit.MILLISECONDS)
                        .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.MIN_BACKOFF_MILLIS,TimeUnit.MILLISECONDS)
                        .build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("roviSync", ExistingPeriodicWorkPolicy.KEEP,synchronizationRovianda);


    }
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    void deleteAllSales(){
        executor.execute(new  Runnable() {
            @Override
            public void run() {
                appDatabase.saleDao().deleteAllSales();
                appDatabase.subSalesDao().deleteAllSubSales();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Eliminados");
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkPermission()
    {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Permisos denegados",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Rovianda ventas";
            String description = "Sistema de transferencia en linea";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("rovisapi", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}