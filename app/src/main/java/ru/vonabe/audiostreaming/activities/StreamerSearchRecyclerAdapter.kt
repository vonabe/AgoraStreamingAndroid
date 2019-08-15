package ru.vonabe.audiostreaming.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import ru.vonabe.audiostreaming.R
import ru.vonabe.audiostreaming.network.pojo.UserSearch

class StreamerSearchRecyclerAdapter(
    private var userSearch: ArrayList<UserSearch>,
    private val context: Context,
    private val callback: Callback
) :
    RecyclerView.Adapter<UserHolder>() {

    fun addItem(item: UserSearch) {
        userSearch.add(item)
    }

    fun getItems(): ArrayList<UserSearch> {
        return userSearch
    }

    fun setItems(list: List<UserSearch>) {
        this.userSearch.clear()
        this.userSearch.addAll(list)
    }

    fun clearItems() {
        this.userSearch.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.streamer_search_view, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return userSearch.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val userSearch = userSearch[position]
        holder.setImg(userSearch.icon)
        holder.setTitle(userSearch.title)
        holder.setDescription(userSearch.description)
        holder.setRating(userSearch.rate)
        holder.setSubscribe(userSearch.subscribe)

        holder.core?.setOnClickListener {
            callback.click(userSearch)
        }

        holder.btnSubscribe?.setOnClickListener {
            callback.subscribe(userSearch)
        }

    }
}


class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var img: ImageView? = null
    var title: TextView? = null
    var description: TextView? = null
    var rate: AppCompatRatingBar? = null
    var btnSubscribe: Button? = null
    var core: View? = null

    fun setRating(rate: Int) {
        this.rate?.rating = rate.toFloat()
    }

    fun setImg(res: Int) {
        img?.setImageResource(res)
    }

    fun setTitle(res: String) {
        title?.text = res
    }

    fun setDescription(res: String) {
        description?.text = res
    }

    fun setSubscribe(subscribe: Boolean) {
        btnSubscribe?.text = if (subscribe) "Отписаться" else "Подписаться"
    }

    init {
        img = itemView.findViewById<ImageView>(R.id.img)
        title = itemView.findViewById<TextView>(R.id.txtTitle)
        description = itemView.findViewById<TextView>(R.id.txtDescription)
        rate = itemView.findViewById<AppCompatRatingBar>(R.id.ratingBar)
        btnSubscribe = itemView.findViewById<Button>(R.id.btnSub)
        core = itemView
    }

}

interface Callback {
    fun subscribe(user: UserSearch)
    fun click(user: UserSearch)
}