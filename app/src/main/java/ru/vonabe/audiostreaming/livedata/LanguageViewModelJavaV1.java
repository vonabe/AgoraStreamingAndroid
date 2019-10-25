package ru.vonabe.audiostreaming.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.vonabe.audiostreaming.only.AGApplication;

public class LanguageViewModelJava extends ViewModel {

//    private StockManager stockManager;

    private MutableLiveData<String> data;

    public MutableLiveData<String> getData() {
        if (data == null) {
            data = new MutableLiveData<String>();
            loadData();
        }
        return data;
    }

    private void loadData() {
        new Thread(){
            @Override
            public void run() {
                data.postValue(AGApplication.getLanguage());
            }
        }.start();
    }

}
