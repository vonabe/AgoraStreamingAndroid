package ru.vonabe.audiostreaming.livedata

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LanguageLivecycleV1: LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectListener() {
        Log.e("LanguageLivecycleV1", "connectListener")
        // this method will respond to resume event of our Lifecycle owner (activity/fragment in our case)
        // So let's get location here and provide callback
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {
        Log.e("LanguageLivecycleV1", "disconnectListener")
        // this method will respond to pause event of our Lifecycle owner (activity/fragment in our case)
        // So let's stop receiveing location updates here and remove callback
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) // Optional if you want to cleanup references
    fun cleanUp() {
        // this method will respond to destroy event of our Lifecycle owner (activity/fragment in our case)
        // Clean up code here
    }

}