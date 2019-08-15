package ru.vonabe.audiostreaming.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.StreamerActivityTwo
import ru.vonabe.audiostreaming.network.pojo.UserOnline

class StreamerOnlineRecycleAdapter(private val list: ArrayList<UserOnline>, val context: Context?) :
    RecyclerView.Adapter<ContactHolder>() {

    fun addItem(item: UserOnline) {
        list.add(item)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val userOnline = list[position]
        holder.setImg(userOnline.icon)
        holder.setTitle(userOnline.title)
        holder.setDescription(userOnline.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.streamer_online_view, parent, false)
        val contactHolder = ContactHolder(view)

        view.setOnClickListener {
            val intent = Intent(context, StreamerActivityTwo::class.java)
            intent.putExtra("title", contactHolder.title?.text)
            context?.startActivity(intent)
        }

        return contactHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var img: ImageView? = null
    var title: TextView? = null
    var description: TextView? = null

    fun setImg(res: Int) {
        img?.setImageResource(res)
    }

    fun setTitle(res: String) {
        title?.text = res
    }

    fun setDescription(res: String) {
        description?.text = res
    }

    init {
        img = itemView.findViewById<ImageView>(R.id.img)
        title = itemView.findViewById<TextView>(R.id.txtTitle)
        description = itemView.findViewById<TextView>(R.id.txtDescription)
    }

}