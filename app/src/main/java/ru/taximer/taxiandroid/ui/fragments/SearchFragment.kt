package ru.taximer.taxiandroid.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.ui.adapters.SearchPlaceAdapter

class SearchFragment: Fragment(), TextWatcher{

    private var adapter: SearchPlaceAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun afterTextChanged(p0: Editable?) {
        adapter?.getAutocomplete(p0?.toString() ?: "")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
}