package ru.taximer.taxiandroid.ui.adapters


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedImageView
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.SearchTaxiModel
import ru.taximer.taxiandroid.utils.displayImage

interface OnTaxiHolderListener {
    fun onAppSelect(installUrl: String, openUrl: String, appId: String, taxoparkId: String)
}

class TaxiAdapter(
        val callback: OnTaxiHolderListener
) : RecyclerView.Adapter<TaxiViewHolder>(), OnTaxiHolderListener {
    private val data: ArrayList<SearchTaxiModel> = ArrayList()

    override fun onBindViewHolder(holder: TaxiViewHolder, position: Int) {
        holder.bind(data[position], this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxiViewHolder {
        return TaxiViewHolder.create(LayoutInflater.from(parent.context), parent)
    }

    fun addItem(item: SearchTaxiModel) {
        data.add(item)
        data.distinctBy { it.order }
        val i = data.indexOf(item)
        notifyItemInserted(i)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onAppSelect(installUrl: String, openUrl: String, appId: String, taxoparkId: String) {
        callback.onAppSelect(installUrl, openUrl, appId, taxoparkId)
    }

    override fun getItemCount() = data.size
}


class TaxiViewHolder private constructor(root: View) : RecyclerView.ViewHolder(root) {
    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
                TaxiViewHolder(inflater.inflate(R.layout.application_item, parent, false))
    }

    var appIcon: RoundedImageView = itemView.findViewById<View>(R.id.app_icon) as RoundedImageView
    var appName: TextView = itemView.findViewById<View>(R.id.app_name) as TextView
    var appValue: TextView = itemView.findViewById(R.id.price) as TextView

    var model: SearchTaxiModel? = null
    var listener: OnTaxiHolderListener? = null

    init {
        root.setOnClickListener {
            listener?.onAppSelect(
                    model?.install_link ?: "",
                    model?.application_link ?: "",
                    model?.android_app_id ?: "",
                    model?.taxopark?.id?.toString() ?: ""
            )
        }
    }

    fun bind(model: SearchTaxiModel, listener: OnTaxiHolderListener) {
        this.listener = listener
        this.model = model
        appName.text = this.model?.taxopark?.name
        appIcon.displayImage(this.model?.taxopark?.img_url, R.mipmap.ic_launcher)
        appValue.text = this.model?.taxopark_price
    }
}
