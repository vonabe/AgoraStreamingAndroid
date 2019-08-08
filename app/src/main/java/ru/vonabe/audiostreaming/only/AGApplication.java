package ru.vonabe.audiostreaming.only;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import ru.vonabe.audiostreaming.network.RetrofitClientInstance;
import ru.vonabe.audiostreaming.network.pojo.RestApiService;

public class AGApplication extends Application {

    //    private WorkerThread mWorkerThread;
    public static RestApiService service = RetrofitClientInstance.Companion.getInstance().create(RestApiService.class);

    private static AGApplication instance;

    private static Context context;

    public static void saveLanguage(String country) {
        SharedPreferences english_preferences = context.getSharedPreferences("english_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = english_preferences.edit();
        edit.putString("language", country);
        edit.commit();
        edit.apply();
    }

    public static String getLanguage() {
        SharedPreferences english_preferences = context.getSharedPreferences("english_preferences", Context.MODE_PRIVATE);
        String string = english_preferences.getString("language", "rus");
        return string;
    }

    public static AGApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        instance = this;

    }

//    public synchronized void initWorkerThread() {
//        if (mWorkerThread == null) {
//            mWorkerThread = new WorkerThread(getApplicationContext());
//            mWorkerThread.start();
//
//            mWorkerThread.waitForReady();
//        }
//    }
//
//    public synchronized WorkerThread getWorkerThread() {
//        return mWorkerThread;
//    }
//
//    public synchronized void deInitWorkerThread() {
//        mWorkerThread.exit();
//        try {
//            mWorkerThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mWorkerThread = null;
//    }
//
//    public static final CurrentUserSettings mAudioSettings = new CurrentUserSettings();
}
