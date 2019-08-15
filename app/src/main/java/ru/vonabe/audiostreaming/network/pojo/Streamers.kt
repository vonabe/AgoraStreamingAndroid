package ru.vonabe.audiostreaming.network.pojo

import java.util.*

data class Streamers(
    val id: Int,
    val username: String,
    val first_name: String,
    val last_name: String,
    val language: String,
    val is_streamer: Int,
    val status: Int,
    val broadcasts: List<Broadcasts>
)

data class Broadcasts(
    val bc_id: Int, val name: String, val date: Date, val time_utc: String, val streamer_id: Int,
    val category_id: Int, val status: Int, val credentials_id: String, val added_by: Int,
    val added_datetime: String, val bc_past: Int, val keys: Keys, val online: Int
)

data class Keys(val key_id: Int, val name: String, val appid: String, val bc_id: Int, val status: Int)