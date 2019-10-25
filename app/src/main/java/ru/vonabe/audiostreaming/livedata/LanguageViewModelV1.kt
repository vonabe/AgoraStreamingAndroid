package ru.vonabe.audiostreaming.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LanguageViewModel : ViewModel() {

    val language: MutableLiveData<String> by lazy { MutableLiveData<String>() }

}