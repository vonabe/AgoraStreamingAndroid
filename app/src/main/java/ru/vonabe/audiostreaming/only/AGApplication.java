package ru.vonabe.audiostreaming.only;

import android.app.Application;
import android.os.StrictMode;
import ru.vonabe.audiostreaming.network.RetrofitClientInstance;
import ru.vonabe.audiostreaming.network.pojo.RestApiService;

public class AGApplication extends Application {

//    private WorkerThread mWorkerThread;
    public static RestApiService service = RetrofitClientInstance.Companion.getInstance().create(RestApiService.class);

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        instance = this;
    }

    private static AGApplication instance;
    public static AGApplication getInstance(){
        return instance;
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
