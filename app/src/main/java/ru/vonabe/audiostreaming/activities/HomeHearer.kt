package ru.vonabe.audiostreaming.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.network.pojo.UserOnline

class HomeHearer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home_hearer)
//        var toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)

        var recyclerOnlineStreamer = findViewById<RecyclerView>(R.id.recyclerOnlineStreamers)
        var layout = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        recyclerOnlineStreamer.layoutManager = layout

        recyclerOnlineStreamer.adapter = StreamerOnlineRecycleAdapter(
            arrayOf(
                UserOnline(R.mipmap.img_avatars, "История графических стилей", "История графических стилей"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 2", "История графических стилей 2"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 3", "История графических стилей 3"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 4", "История графических стилей 4"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 5", "История графических стилей 5"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 6", "История графических стилей 6"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 7", "История графических стилей 7"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 8", "История графических стилей 8"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 9", "История графических стилей 9"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 10", "История графических стилей 10"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 11", "История графических стилей 11"),
                UserOnline(R.mipmap.img_avatars, "История графических стилей 12", "История графических стилей 12")
            ), this
        )

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.toolbar_menu, menu)
//
//        if (menu is MenuBuilder) {
//            menu.setOptionalIconsVisible(true)
//        }
//
//        return true
//    }

}