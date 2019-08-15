package ru.vonabe.audiostreaming.only;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import com.facebook.drawee.backends.pipeline.Fresco;
import ru.vonabe.audiostreaming.network.RetrofitClientInstance;
import ru.vonabe.audiostreaming.network.pojo.RestApiService;

public class AGApplication extends Application {

    //    private WorkerThread mWorkerThread;
    public static RestApiService service = RetrofitClientInstance.Companion.getInstance().create(RestApiService.class);

    private static AGApplication instance;

    private static Context context;

    public static void saveAuth(LoginAndPassword loginAndPassword) {
        SharedPreferences preferences_login = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences_login.edit();
        edit.putString("login", loginAndPassword.getLogin());
        edit.putString("password", loginAndPassword.getPassword());
        edit.commit();
        edit.apply();
    }

    public static LoginAndPassword getAuth() {
        SharedPreferences preferences_login = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String login = preferences_login.getString("login", null);
        String password = preferences_login.getString("password", null);
        if (login != null && password != null) {
            return new LoginAndPassword(login, password);
        }
        return null;
    }

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
        Fresco.initialize(this);
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
