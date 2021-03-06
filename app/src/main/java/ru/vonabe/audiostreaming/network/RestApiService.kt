package ru.vonabe.audiostreaming.network.pojo

import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface RestApiService {

    private val token: RequestBody
        get() = RequestBody.create(MediaType.get("multipart/form-data"), "tv3}^P*Jt5HU#kaJxVKAM9Gd")

    @Multipart
    @POST("/api/create_user")
    fun registration(
        @Part("api_key") key_token: RequestBody? = token,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("is_streamer") is_streamer: RequestBody,
        @Part("language") language: RequestBody
    ): Call<Registration>

    @Multipart
    @POST("/api/user_login")
    fun login(
        @Part("api_key") key_token: RequestBody = token,
        @Part("username") email: RequestBody,
        @Part("password") password: RequestBody
    ): Call<User>

    @Multipart
    @POST("/api/get_broadcast_categories")
    fun getBroadcastCategories(@Part("api_key") key: RequestBody = token): Call<List<BroadcastCategories>>

    @Multipart
    @POST("/api/get_broadcasts")
    fun getBroadcast(
        @Part("api_key") key_token: RequestBody = token,
        @Part("category_id") category_id: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("bc_id") bc_id: RequestBody?
    ): Call<List<Broadcast>>

    @Multipart
    @POST("/api/get_broadcasts")
    fun getBroadcast(
        @Part("api_key") key_token: RequestBody = token,
        @PartMap options: Map<String, String>? = emptyMap()
    ): Call<List<Broadcast>>

    @Multipart
    @POST("/api/add_broadcast")
    fun addBroadcast(
        @Part("api_key") key_token: RequestBody = token,
        @Part("name") name: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("time_utc") time_utc: RequestBody?,
        @Part("streamer_id") streamer_id: RequestBody?,
        @Part("category_id") category_id: RequestBody?
    ): Call<AddBroadcast>

    @Multipart
    @POST("/api/get_streamers")
    fun getStreamers(
        @Part("api_key") key_token: RequestBody = token
    ): Call<List<Streamers>>

    @Multipart
    @POST("/api/broadcast_start")
    fun startStream(
        @Part("api_key") key_token: RequestBody = token,
        @Part("bc_id") bc_id: RequestBody?
    ): Call<StatusStartStream>

    @Multipart
    @POST("/api/broadcast_end")
    fun stopStream(
        @Part("api_key") key_token: RequestBody = token,
        @Part("bc_id") bc_id: RequestBody?
    ): Call<Status>


}
