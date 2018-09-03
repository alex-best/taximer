package ru.taximer.taxiandroid.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.disposables.Disposable
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.network.models.PlacePredictionModel
import ru.taximer.taxiandroid.network.models.emptyPrediction
import ru.taximer.taxiandroid.network.usecases.applyDefaultNetSchedulers
import ru.taximer.taxiandroid.ui.MainTaxiScreen
import ru.taximer.taxiandroid.utils.gms.GoogleApiPartial
import ru.taximer.taxiandroid.utils.gms.getPlace
import ru.taximer.taxiandroid.utils.gms.getPredictions

interface OnPlaceHolderListener{
    fun onPlaceSelect(place: PlacePredictionModel)
}

interface OnPlaceListener{
    fun onPlaceSelect(place: PlaceLocationModel)
}
class SearchPlaceAdapter(
        val googleApiPartial: GoogleApiPartial<MainTaxiScreen>,
        val listener: OnPlaceListener)
    : RecyclerView.Adapter<SearchPlaceViewHolder>(), OnPlaceHolderListener {
    private var query: String = ""
    private val data = ArrayList<PlacePredictionModel>()
    private var suggestionSubscription: Disposable? = null
    private var placeSubscription: Disposable? = null

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SearchPlaceViewHolder, position: Int) {
        holder.bind(data[position], this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPlaceViewHolder {
        return SearchPlaceViewHolder.create(LayoutInflater.from(parent.context), parent)
    }

    override fun onPlaceSelect(place: PlacePredictionModel) {
        placeSubscription?.dispose()

        placeSubscription = googleApiPartial.getPlace(place.placeId)
                ?.applyDefaultNetSchedulers()
                ?.subscribe(
                        { listener.onPlaceSelect(PlaceLocationModel(it.get(0), place.address)) },
                        {
                            error { it.toString() }
                        },
                        { })
    }

    @Synchronized
    fun getAutocomplete(constraint: String) {
        if (query == constraint) return
        query = constraint
        if (query == "") {
            data.clear()
            notifyDataSetChanged()
            return
        }

        suggestionSubscription?.dispose()
        suggestionSubscription = googleApiPartial.getPredictions(query, null, null)
                ?.applyDefaultNetSchedulers()
                ?.subscribe(
                        { buffer ->
                            data.clear()
                            val res = buffer.map { PlacePredictionModel(it) }
                            data.addAll(res)
                        },
                        {
                            data.clear()
                            it.printStackTrace()
                        },
                        { notifyDataSetChanged() })
    }

}

class SearchPlaceViewHolder private constructor(root: View) : RecyclerView.ViewHolder(root) {
    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
                SearchPlaceViewHolder(inflater.inflate(R.layout.item_place, parent, false))
    }

    private var placeName: TextView = root.findViewById(R.id.place_name)
    private var placeAddress: TextView = root.findViewById(R.id.place_address)

    var model = emptyPrediction
    var listener: OnPlaceHolderListener? = null

    init {
        root.setOnClickListener { listener?.onPlaceSelect(model) }
    }

    fun bind(model: PlacePredictionModel, listener: OnPlaceHolderListener) {
        this.listener = listener
        this.model = model
        placeName.text = this.model.address
        placeAddress.text = this.model.fullAddress
    }
}