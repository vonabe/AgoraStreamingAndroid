package ru.vonabe.audiostreaming.network.pojo

data class UserSearch(val icon: Int, val title: String, val description: String, val rate: Int = 0, val subscribe: Boolean = false)