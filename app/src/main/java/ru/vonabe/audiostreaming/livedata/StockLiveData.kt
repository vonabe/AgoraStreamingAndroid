package ru.vonabe.audiostreaming.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import ru.vonabe.audiostreaming.only.AGApplication

class StockLiveDataJava private constructor() : LiveData<String>() {

    private fun loadData() {
        object : Thread() {
            override fun run() {
                postValue(AGApplication.getLanguage())
            }
        }.start()
    }

    fun postData(value: String) {
        super.postValue(value)
    }

    override fun onActive() {
        super.onActive()
        loadData()
        Log.e("Language", "onActive")
    }

    override fun onInactive() {
        super.onInactive()
        Log.e("Language", "onInactive")
    }

    companion object {
        private lateinit var instance: StockLiveDataJava
        fun getInstance(): StockLiveDataJava {
            instance = if (::instance.isInitialized) instance else StockLiveDataJava()
            return instance
        }
    }

}
