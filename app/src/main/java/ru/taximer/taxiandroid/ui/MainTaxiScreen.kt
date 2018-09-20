package ru.taximer.taxiandroid.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.location.places.Places
import kotlinx.android.synthetic.main.activity_main_taxi_screen.appVersionView
import kotlinx.android.synthetic.main.activity_main_taxi_screen.drawer_layout
import kotlinx.android.synthetic.main.activity_main_taxi_screen.feedback
import kotlinx.android.synthetic.main.activity_main_taxi_screen.markAppView
import kotlinx.android.synthetic.main.activity_main_taxi_screen.notificationSwitch
import kotlinx.android.synthetic.main.activity_main_taxi_screen.shareAppView
import kotlinx.android.synthetic.main.app_bar_main_taxi_screen.goButton
import kotlinx.android.synthetic.main.app_bar_main_taxi_screen.toolbar
import kotlinx.android.synthetic.main.bottom_sheet.autocompleteAddresses
import kotlinx.android.synthetic.main.bottom_sheet.bottomSheet
import kotlinx.android.synthetic.main.bottom_sheet.coloredContainer
import kotlinx.android.synthetic.main.bottom_sheet.tmpBar
import org.jetbrains.anko.intentFor
import ru.taximer.taxiandroid.BuildConfig
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.presenters.MainTaxiPresenter
import ru.taximer.taxiandroid.presenters.MainTaxiView
import ru.taximer.taxiandroid.presenters.UserSettingsPresenter
import ru.taximer.taxiandroid.presenters.base.BaseLoadingView
import ru.taximer.taxiandroid.ui.adapters.OnPlaceListener
import ru.taximer.taxiandroid.ui.adapters.SearchPlaceAdapter
import ru.taximer.taxiandroid.utils.gms.GoogleApiPartial
import ru.taximer.taxiandroid.utils.gms.GoogleApiPartialActivityCallbacks
import ru.taximer.taxiandroid.utils.gms.GoogleApiPartialCallbacks
import ru.taximer.taxiandroid.utils.hideKeyboard


class MainTaxiScreen :
        MvpAppCompatActivity(),
        MainTaxiView,
        TextWatcher,
        GoogleApiPartialCallbacks,
        GoogleApiPartialActivityCallbacks,
        OnPlaceListener,
        BaseLoadingView,
        View.OnFocusChangeListener{

    @InjectPresenter(type = PresenterType.GLOBAL)
    lateinit var presenter: MainTaxiPresenter

    @ProvidePresenter(type = PresenterType.GLOBAL)
    fun provideMainTaxiPresenter(): MainTaxiPresenter = MainTaxiPresenter()

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var userSettingsPresenter: UserSettingsPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun provideUserSettingsPresenter(): UserSettingsPresenter = UserSettingsPresenter()

    private var adapter: SearchPlaceAdapter? = null
    private lateinit var googleApiPartial: GoogleApiPartial<MainTaxiScreen>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_taxi_screen)
        setSupportActionBar(toolbar)

        googleApiPartial = GoogleApiPartial(
                this,
                this,
                Places.GEO_DATA_API,
                Places.PLACE_DETECTION_API)
        googleApiPartial.restoreState(savedInstanceState)

        googleApiPartial.start()

        val toggle = ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        appVersionView.text = BuildConfig.VERSION_NAME
        adapter = SearchPlaceAdapter(googleApiPartial, this)
        autocompleteAddresses.layoutManager = LinearLayoutManager(this)
        autocompleteAddresses.adapter = adapter
        tmpBar.addTextChangedListener(this)
        goButton.setOnClickListener {
            presenter.onSearch()
        }
        tmpBar.onFocusChangeListener = this
        tmpBar.setOnClickListener { _ ->
            adapter?.getAutocomplete(
                    tmpBar.text.toString(),
                    presenter.isStartEdit,
                    presenter.getSearchLocation()
            )
            openPanel()
        }

        notificationSwitch.setOnCheckedChangeListener { _, value ->
            userSettingsPresenter.changeNotifications(value)
        }
        shareAppView.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=ru.taximer.taxiandroid")
                type = "text/plain"
            }
            startActivity(sendIntent)
        }

        markAppView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ru.taximer.taxiandroid")))
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.setBottomSheetCallback(BottomCallbck())
        setFeedBack()
    }

    override fun onDestroy() {
        googleApiPartial.destroy()
        super.onDestroy()
    }

    override fun onFocusChange(p0: View?, value: Boolean) {
        if (value) {
            openPanel()
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        adapter?.getAutocomplete(
                p0?.toString(),
                presenter.isStartEdit,
                presenter.getSearchLocation()
        )
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    fun openStartSearch() {
        tmpBar.setText("")
        presenter.editStart()
        openPanel()
    }

    fun openEndSearch() {
        tmpBar.setText("")
        presenter.editEnd()
        openPanel()
    }

    fun openPanel() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun hidePanel(isBothLocationContaints: Boolean) {
        tmpBar.setText("")
        tmpBar.hideKeyboard()

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (isBothLocationContaints) {
            bottomSheetBehavior.isHideable = true
            goButton.visibility = View.VISIBLE

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        else {
            bottomSheetBehavior.isHideable = false
            goButton.visibility = View.INVISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onPlaceSelect(place: PlaceLocationModel) {
        presenter.setLocation(place)
        hidePanel(presenter.isBothLocationSelected())
    }

    override fun onBackPressed() = when {
        drawer_layout.isDrawerOpen(GravityCompat.START) ->
            drawer_layout.closeDrawer(GravityCompat.START)
        else -> {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            when {
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                    hidePanel(presenter.isBothLocationSelected())
                else -> super.onBackPressed()
            }
        }
    }

    override fun onGoogleApiClientErrorResolved() {
        googleApiPartial.onErrorResolved()
    }

    override fun setEnd(location: PlaceLocationModel?) {
        //nope
    }

    override fun setStart(location: PlaceLocationModel?) {
        //nope
    }

    override fun setState(isStartEdit: Boolean, isBothLocationContaints: Boolean) {
        hidePanel(presenter.isBothLocationSelected())

        if (isStartEdit) {
            tmpBar.setHint(R.string.label_from)
        }
        else {
            tmpBar.setHint(R.string.label_to)
        }
    }

    override fun startSearch(start: PlaceLocationModel, end: PlaceLocationModel) {
        TaxiActivity.launch(this, start, end)
    }

    override fun hideLoading() {
        //nope
    }

    override fun showError(message: String) {
        //nope
    }

    override fun showLoading() {
        //nope
    }

    private fun setFeedBack() {
        feedback.setOnClickListener {

        }
    }

    companion object {
        fun launch(context: Activity) {
            val intent = context.intentFor<MainTaxiScreen>().apply {
                addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }
    }

    inner class BottomCallbck : BottomSheetBehavior.BottomSheetCallback(){
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when(newState){
                BottomSheetBehavior.STATE_EXPANDED ->{
                    val color = ContextCompat.getColor(
                            applicationContext,
                            if (presenter.isStartEdit) R.color.pink else R.color.colorAccent
                    )
                    coloredContainer.setBackgroundColor(color)
                }
                BottomSheetBehavior.STATE_COLLAPSED ->{
                    coloredContainer.setBackgroundColor(
                            ContextCompat.getColor(applicationContext,
                                    android.R.color.white)
                    )
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            //nope
       }
    }
}
