package ru.vonabe.audiostreaming

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.drawee.generic.RoundingParams
import kotlinx.android.synthetic.main.streamer_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vonabe.audiostreaming.activities.HomeHearer
import ru.vonabe.audiostreaming.network.pojo.Broadcast
import ru.vonabe.audiostreaming.network.pojo.Status
import ru.vonabe.audiostreaming.network.pojo.StatusStartStream
import ru.vonabe.audiostreaming.only.AGApplication

class StreamerActivityTwo : AppCompatActivity() {

    private var agoraInitialize: AgoraInitialize? = null
    private var channelUID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.streamer_main)

        val stringTitle = intent.getStringExtra("title")
        txtTitle.text = stringTitle

        var txtDelay = findViewById<TextView>(R.id.txtDelay)

        val roundingParams = RoundingParams.fromCornersRadius(5f)
        roundingParams.setBorder(ContextCompat.getColor(this, R.color.red), 1.0f)
        roundingParams.roundAsCircle = true
        imageStreamer.hierarchy.roundingParams = roundingParams

        imageStreamer.hierarchy.setPlaceholderImage(R.drawable.michail)

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                txtDelay.text = "Задержка $progress сек"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { agoraInitialize?.setDelay(seekBar.progress) }
            }
        })

        backButton.setOnClickListener {
            onBackPressed()
            finish()
        }

        agoraInitialize = AgoraInitialize.getInstance(this)

        btnPlay.setOnClickListener {

            if (stringTitle == "Начать стрим") {
                AGApplication.service.getBroadcast().enqueue(object : Callback<List<Broadcast>> {
                    override fun onFailure(call: Call<List<Broadcast>>, t: Throwable) {
                        val message = "Error ${t.message}"
                        Toast.makeText(this@StreamerActivityTwo, message, Toast.LENGTH_LONG).show()
                        Log.e("HomeHearer error", message)
                    }

                    override fun onResponse(call: Call<List<Broadcast>>, response: Response<List<Broadcast>>) {
                        if (response.isSuccessful && response.body() != null) {
                            val body = response.body()
                            var arrayList = ArrayList<String>()
                            body?.forEach {
                                it.name?.let { name ->
//                                    if(it.keys.status==0) {
                                        arrayList.add(name)
//                                    }
                                }
                            }

                            val toTypedArray = arrayList.toTypedArray()
                            Log.e("StreamerActivityTwo ", "arrayList $toTypedArray")

                            var builder = AlertDialog.Builder(this@StreamerActivityTwo)
                            builder.setTitle("Выберите трансляцию")
//                            builder.setMessage("Чтобы начать трансляцию выберите канал")
                            builder.setItems(
                                toTypedArray
                            ) { _, which ->
                                //                                    Toast.makeText(this@StreamerActivityTwo, "${toTypedArray[which]}", Toast.LENGTH_LONG).show()
                                val broadcast = body?.get(which)
                                val bcId = broadcast?.bc_id

                                if(broadcast?.online == 1){
                                    Log.e(
                                        "StreamerActivityTwo",
                                        "StartStream ${broadcast.name + ", ${broadcast.bc_id}"}"
                                    )
                                    agoraInitialize?.startStream(
                                        broadcast.keys.name,
                                        broadcast.keys.appid
                                    )
                                    channelUID = broadcast.keys.appid
                                } else {

                                    AGApplication.service.startStream(
                                        bc_id = RequestBody.create(
                                            MediaType.get("multipart/form-data"),
                                            bcId
                                        )
                                    ).enqueue(object : Callback<StatusStartStream> {
                                        override fun onFailure(call: Call<StatusStartStream>, t: Throwable) {
                                            t.printStackTrace()
                                        }

                                        override fun onResponse(
                                            call: Call<StatusStartStream>,
                                            response: Response<StatusStartStream>
                                        ) {
                                            if (response.isSuccessful && response.body() != null) {
                                                val statusStartStream = response.body()
                                                if (statusStartStream?.status == 1) {
                                                    Log.e(
                                                        "StreamerActivityTwo",
                                                        "StartStream ${statusStartStream.name + ", ${statusStartStream.appid}"}"
                                                    )
                                                    agoraInitialize?.startStream(
                                                        statusStartStream.name,
                                                        statusStartStream.appid
                                                    )
                                                    channelUID = statusStartStream.appid
                                                } else {
                                                    Toast.makeText(
                                                        this@StreamerActivityTwo,
                                                        "Не удалось запустить трансляцию код ошибки ${statusStartStream?.status}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    Log.e("StreamerActivityTwo ", "${response.body().toString()}")
                                                }
                                            }
                                        }
                                    })
                                }
                            }

                            builder.setCancelable(true)
//                            builder.setPositiveButton(
//                                "Подключиться",
//                                DialogInterface.OnClickListener { dialog, which -> })
//                            builder.setNegativeButton("Отмена", DialogInterface.OnClickListener { dialog, which -> })

                            val create = builder.create()
                            create.show()

                        } else {
                            Log.e("StreamerActivityTwo ", "body null")
                        }
                    }
                })

//                AGApplication.service.startStream()
//                instance?.startStream()
            } else {
                HomeHearer.listBroadcast[stringTitle]?.let {
                    agoraInitialize?.joinChannel(it.name, it.appid)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraInitialize?.destroy()
        try {
            AGApplication.service.stopStream(
                bc_id = RequestBody.create(MediaType.get("multipart/form-data"), channelUID)
            ).enqueue(object : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}