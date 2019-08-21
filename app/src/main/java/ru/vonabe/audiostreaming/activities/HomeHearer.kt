package ru.vonabe.audiostreaming.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.StreamerActivityTwo
import ru.vonabe.audiostreaming.network.pojo.*
import ru.vonabe.audiostreaming.only.AGApplication

class HomeHearer : AppCompatActivity() {

    private var streamerSearchRecyclerAdapter: StreamerSearchRecyclerAdapter? = null
    private var my_stream_constructor: ConstraintLayout? = null
    private var streamer: Streamers? = null

    private var streamNameEdit: AppCompatEditText? = null
    private var btnStartStream: AppCompatButton? = null
    private var selectCategory: AppCompatSpinner? = null

    companion object {
        val listBroadcast = HashMap<String, Keys>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val window = window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

        setContentView(R.layout.home_hearer)
//        var toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)

        my_stream_constructor = findViewById(R.id.includeMyStream)

        my_stream_constructor?.let { it ->

            var category: ArrayList<BroadcastCategories>? = null
            selectCategory = it.findViewById(R.id.spinnerCategoryStream)
            selectCategory!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
            }
            val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListOf())
            selectCategory!!.adapter = arrayAdapter

            AGApplication.service.getBroadcastCategories().enqueue(object : Callback<List<BroadcastCategories>> {
                override fun onFailure(call: Call<List<BroadcastCategories>>, t: Throwable) {}

                override fun onResponse(
                    call: Call<List<BroadcastCategories>>,
                    response: Response<List<BroadcastCategories>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        category = ArrayList(body)
                        body?.forEach { category ->
                            val catId = category.cat_id
                            val catName = category.cat_name
                            arrayAdapter.add(catName)
                        }
                        arrayAdapter.notifyDataSetChanged()
                        selectCategory!!.setSelection(0)
                    }
                }
            })

            streamNameEdit = it.findViewById(R.id.inputStreamName)
            btnStartStream = it.findViewById(R.id.btnSwitchStream)

            AGApplication.service.getStreamers().enqueue(object : Callback<List<Streamers>> {
                override fun onFailure(call: Call<List<Streamers>>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<List<Streamers>>, response: Response<List<Streamers>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        body?.forEach {
                            if (it.id == AGApplication.getUser().user_id) {
                                streamer = it
                                val list = ArrayList(streamer?.broadcasts)
                                var isStopStream = false
                                var broadcast: Broadcasts? = null

                                list.forEach {
                                    if (it.online == 1) {
                                        streamNameEdit!!.setText(it.name)
                                        streamNameEdit!!.isEnabled = false
                                        selectCategory!!.isEnabled = false
                                        btnStartStream!!.text = "Остановить"
                                        isStopStream = true
                                        broadcast = it
                                    }
                                }

                                btnStartStream!!.setOnClickListener {
                                    if (isStopStream) {

                                        AGApplication.service.stopStream(
                                            bc_id = RequestBody.create(
                                                MediaType.get("multipart/form-data"),
                                                broadcast!!.bc_id.toString()
                                            )
                                        )
                                            .enqueue(object : Callback<Status> {
                                                override fun onFailure(call: Call<Status>, t: Throwable) {
                                                    t.printStackTrace()
                                                }

                                                override fun onResponse(
                                                    call: Call<Status>,
                                                    response: Response<Status>
                                                ) {
                                                    if (response.isSuccessful && response.body() != null) {
                                                        val body = response.body()
                                                        if (body?.status == 1) {
                                                            Toast.makeText(
                                                                this@HomeHearer,
                                                                "Трансляций завершена",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            list.remove(broadcast)
                                                            var bool = false
                                                            list.forEach {
                                                                if(it.online == 1){
                                                                    selectCategory!!.setSelection(if(it.category_id == 1) 0 else 1)
                                                                    streamNameEdit!!.setText(it.name)
                                                                    streamNameEdit!!.isEnabled = false
                                                                    selectCategory!!.isEnabled = false
                                                                    btnStartStream!!.text = "Остановить"
                                                                    isStopStream = true
                                                                    broadcast = it
                                                                    bool = true
                                                                }
                                                            }
                                                            if(!bool){
                                                                streamNameEdit!!.setText("")
                                                                streamNameEdit!!.isEnabled = true
                                                                selectCategory!!.isEnabled = true
                                                                btnStartStream!!.text = "Запустить"
                                                                isStopStream = false
                                                            }

                                                        }
                                                    }
                                                }
                                            })

                                        return@setOnClickListener
                                    }

                                    val streamName = streamNameEdit!!.text.toString()

                                    if (streamName.isNullOrEmpty()) {
                                        streamNameEdit!!.error = "Поле не должно быть пустым"
                                        return@setOnClickListener
                                    }

                                    val currentTime = AGApplication.getCurrentTime()
                                    Log.e(
                                        "HomeHearer",
                                        "Stream name $streamName CurrentTime ${currentTime[0]} ${currentTime[1]}"
                                    )

                                    AGApplication.service.addBroadcast(
                                        name = RequestBody.create(
                                            MediaType.get("multipart/form-data"),
                                            streamName
                                        ),
                                        date = RequestBody.create(
                                            MediaType.get("multipart/form-data"),
                                            AGApplication.getCurrentTime()[0]
                                        ),
                                        time_utc = RequestBody.create(
                                            MediaType.get("multipart/form-data"),
                                            AGApplication.getCurrentTime()[1]
                                        ),
                                        streamer_id = RequestBody.create(
                                            MediaType.get("multipart/form-data"),
                                            AGApplication.getUser().user_id.toString()
                                        ),
                                        category_id = RequestBody.create(
                                            MediaType.get("multipart/form-data"),
                                            category!![selectCategory!!.selectedItemId.toInt()].cat_id
                                        )
                                    ).enqueue(object : Callback<AddBroadcast> {
                                        override fun onFailure(call: Call<AddBroadcast>, t: Throwable) {
                                            t.printStackTrace()
                                        }

                                        override fun onResponse(
                                            call: Call<AddBroadcast>,
                                            response: Response<AddBroadcast>
                                        ) {
                                            if (response.isSuccessful && response.body() != null) {
                                                val body = response.body()
                                                if (body?.bc_id != -1) {
                                                    Toast.makeText(
                                                        this@HomeHearer,
                                                        "Трансляция успешно добавлена ${body?.bc_id}",
                                                        Toast.LENGTH_LONG
                                                    ).show()

                                                    val intent =
                                                        Intent(this@HomeHearer, StreamerActivityTwo::class.java)
                                                    intent.putExtra("title", streamName)
                                                    intent.putExtra("bc_id", body?.bc_id.toString())
                                                    startActivity(intent)
                                                }
                                            }
                                        }
                                    })

                                }

                            }
                        }
                    }
                }
            })

        }

        var spinnerFilter = findViewById<AppCompatSpinner>(R.id.spinnerFilter)

        var titleStreamer = findViewById<TextView>(R.id.titleStreamers)
        var titleTranslation = findViewById<TextView>(R.id.titleTranslation)

        val colorEnable = ContextCompat.getColor(this, R.color.text_enabled)
        val colorDisable = ContextCompat.getColor(this, R.color.text_disabled)

        var recyclerOnlineStreamer = findViewById<RecyclerView>(R.id.recyclerOnlineStreamers)
        var layout = GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false)
        recyclerOnlineStreamer.layoutManager = layout

        val streamerOnlineRecycleAdapter = StreamerOnlineRecycleAdapter(
            arrayListOf(), this
        )

        recyclerOnlineStreamer.adapter = streamerOnlineRecycleAdapter

        AGApplication.service.getBroadcast(options = emptyMap()).enqueue(object : Callback<List<Broadcast>> {
            override fun onFailure(call: Call<List<Broadcast>>, t: Throwable) {
                val message = "Error ${t.message}"
                Toast.makeText(this@HomeHearer, message, Toast.LENGTH_LONG).show()
                Log.e("HomeHearer error", message)
            }

            override fun onResponse(call: Call<List<Broadcast>>, response: Response<List<Broadcast>>) {
//                Toast.makeText(this@HomeHearer, "response size broadcast ${response.body()?.size}", Toast.LENGTH_LONG).show()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    listBroadcast.clear()
                    body?.forEach { it ->
                        if (it?.online == 1) {
                            it?.name?.let { name ->
                                listBroadcast[name] = it.keys
                                val userOnline = UserOnline(R.mipmap.img_avatars, title = name, description = name)
                                streamerOnlineRecycleAdapter.addItem(userOnline)
                            }
                        }
                    }
                    streamerOnlineRecycleAdapter.notifyDataSetChanged()
                }
            }
        })


        var recyclerSearch = findViewById<RecyclerView>(R.id.recyclerSearchStreamers)
        recyclerSearch.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        streamerSearchRecyclerAdapter =
            StreamerSearchRecyclerAdapter(arrayListOf(), this, object : ru.vonabe.audiostreaming.activities.Callback {
                override fun subscribe(user: UserSearch) {}

                override fun click(user: UserSearch) {
                    if (user.title == "Начать стрим") {
                        val intent = Intent(this@HomeHearer, StreamerActivityTwo::class.java)
                        intent.putExtra("title", user.title)
                        startActivity(intent)
                    }
                }
            })
        recyclerSearch.adapter = streamerSearchRecyclerAdapter

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("По имени", "По рейтингу"))
        spinnerFilter.adapter = arrayAdapter
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        streamerSearchRecyclerAdapter!!.setItems(streamerSearchRecyclerAdapter!!.getItems().sortedBy { it.title })
                        streamerSearchRecyclerAdapter!!.notifyDataSetChanged()
                    }
                    1 -> {
                        streamerSearchRecyclerAdapter!!.setItems(streamerSearchRecyclerAdapter!!.getItems().sortedBy { it.rate })
                        streamerSearchRecyclerAdapter!!.notifyDataSetChanged()
                    }
                }

//                Toast.makeText(this@HomeHearer, "Select ${arrayAdapter.getItem(position)}", Toast.LENGTH_SHORT).show()
            }
        }

        var onChangeTitles = View.OnClickListener { view ->
            when (view) {
                titleStreamer -> {
                    titleStreamer.setTextColor(colorEnable)
                    titleTranslation.setTextColor(colorDisable)
                    changeTypeSearch("streamer")
                }
                titleTranslation -> {
                    titleTranslation.setTextColor(colorEnable)
                    titleStreamer.setTextColor(colorDisable)
                    changeTypeSearch("translation")
                }
            }
        }
        titleStreamer.setOnClickListener(onChangeTitles)
        titleTranslation.setOnClickListener(onChangeTitles)

        this.loadStreamers()
    }

    private fun loadStreamers() {
        AGApplication.service.getStreamers().enqueue(object : Callback<List<Streamers>> {
            override fun onFailure(call: Call<List<Streamers>>, t: Throwable) {
                val message = "Error ${t.message}"
                Toast.makeText(this@HomeHearer, message, Toast.LENGTH_LONG).show()
                Log.e("HomeHearer error", message)
            }

            override fun onResponse(call: Call<List<Streamers>>, response: Response<List<Streamers>>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    streamerSearchRecyclerAdapter?.clearItems()
                    body?.forEach {
                        val username = it.username
                        streamerSearchRecyclerAdapter?.addItem(UserSearch(R.mipmap.img_avatars, username, "---", 1))
                    }
                }
                streamerSearchRecyclerAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun changeTypeSearch(type: String) {
        when (type) {
            "streamer" -> {
                my_stream_constructor?.visibility = ConstraintLayout.GONE
                loadStreamers()
            }
            "translation" -> {
                my_stream_constructor?.visibility = ConstraintLayout.VISIBLE
                streamerSearchRecyclerAdapter?.let {
                    streamerSearchRecyclerAdapter?.clearItems()
//                    streamerSearchRecyclerAdapter?.setItems(
//                        arrayListOf(
//                            UserSearch(R.mipmap.img_avatars, "Начать стрим", "Войти к себе на стрим", 0)
//                        )
//                    )
                    streamerSearchRecyclerAdapter?.notifyDataSetChanged()
                }
            }
        }
    }


}