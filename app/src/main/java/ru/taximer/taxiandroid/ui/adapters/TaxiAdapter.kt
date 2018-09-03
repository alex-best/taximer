package ru.taximer.taxiandroid.ui.adapters


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.SearchTaxiModel
import ru.taximer.taxiandroid.utils.displayImage


class TaxiAdapter(context: Context) : RecyclerView.Adapter<TaxiAdapter.TaxiViewHolder>() {
    private val data: ArrayList<SearchTaxiModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxiViewHolder {
        return TaxiViewHolder(parent.context)
    }

    override fun onBindViewHolder(holder: TaxiViewHolder, position: Int) {
        val taxi = data[position]
        holder.appName.text = taxi.label
        holder.appIcon.displayImage(taxi.taxopark.img_url, R.mipmap.ic_launcher)
    }

    fun addItem(item: SearchTaxiModel){
        data.add(item)
        data.distinctBy { it.order }
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    class TaxiViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var appIcon: ImageView = itemView.findViewById<View>(R.id.app_icon) as ImageView
        var appName: TextView = itemView.findViewById<View>(R.id.app_name) as TextView

        constructor(context: Context) : this(LayoutInflater.from(context).inflate(R.layout.application_item, null)) {}

    }
}
