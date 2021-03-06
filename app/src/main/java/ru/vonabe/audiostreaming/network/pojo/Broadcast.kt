package ru.vonabe.audiostreaming.network.pojo

import java.util.*

data class Broadcast(
    val bc_id: String?, val name: String?, val date: Date?, val time_utc: String?,
    val streamer_id: String?, val category_id: String?, val status: String?,
    val credentials_id: String?, val added_by: String?, val added_datetime: String,
    val bc_past: Int, val keys: Keys, val online: Int
)

//"bc_id": "1",
//"name": "Лига чемпионов Реал - Аякс",
//"date": "2019-07-30",
//"time_utc": "18:30:00",
//"streamer_id": "20",
//"category_id": "1",
//"status": "1",
//"credentials_id": null,
//"added_by": "1",
//"added_datetime": "2019-07-30 00:00:00"