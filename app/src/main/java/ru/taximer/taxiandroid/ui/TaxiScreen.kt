package ru.taximer.taxiandroid.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_applications.applications
import kotlinx.android.synthetic.main.activity_applications.endPlace
import kotlinx.android.synthetic.main.activity_applications.root_container
import kotlinx.android.synthetic.main.activity_applications.startPlace
import org.jetbrains.anko.intentFor
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.network.models.SearchTaxiModel
import ru.taximer.taxiandroid.presenters.GetBestTaxiPresenter
import ru.taximer.taxiandroid.presenters.GetBestTaxiView
import ru.taximer.taxiandroid.presenters.GetTaxiPresenter
import ru.taximer.taxiandroid.presenters.GetTaxiView
import ru.taximer.taxiandroid.presenters.TaxoparkPresenter
import ru.taximer.taxiandroid.presenters.TaxoparkView
import ru.taximer.taxiandroid.ui.adapters.TaxiAdapter

private const val START_PLACE = "start_place"
private const val END_PLACE = "end_place"

class TaxiActivity : MvpAppCompatActivity(), TaxoparkView, GetTaxiView, GetBestTaxiView {

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: TaxoparkPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun provideTaxoparkPresenter(): TaxoparkPresenter = TaxoparkPresenter()

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenterTaxi: GetTaxiPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun provideGetTaxiPresenter(): GetTaxiPresenter = GetTaxiPresenter()

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenterBestTaxi: GetBestTaxiPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun provideGetBestPresenter(): GetBestTaxiPresenter = GetBestTaxiPresenter()

    private var mAdapter: TaxiAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applications)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initList()

    }

    override fun onResume() {
        super.onResume()
        val start = intent.getParcelableExtra(START_PLACE) as PlaceLocationModel
        val end = intent.getParcelableExtra(END_PLACE) as PlaceLocationModel

        presenter.setPoints(start, end)

        startPlace.text = start.address
        endPlace.text = end.address
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.taxi_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_settings -> {
                SettingsActivity.launch(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initList() {
        mAdapter = TaxiAdapter(this)
        applications.layoutManager = LinearLayoutManager(this)
        applications.adapter = mAdapter
    }

    override fun setTaxoparkPack(taxoparl: List<Long>, hash: String) {
        presenterBestTaxi.setHash(hash)
        presenterTaxi.setTaxoparks(taxoparl, hash)
    }

    override fun addBestTaxi(taxi: SearchTaxiModel) {

    }

    override fun addTaxi(taxi: SearchTaxiModel) {
        mAdapter?.addItem(taxi)
    }

    override fun hideLoading() {
        //nope
    }

    override fun showLoading() {
        //nope
    }

    override fun showError(message: String) {
        Log.d("Dto", message)
        val snackbar = Snackbar.make(root_container, message, Snackbar.LENGTH_LONG)

        snackbar.show()
    }

    companion object {
        fun launch(context: Activity, placeStart: PlaceLocationModel, placeEnd: PlaceLocationModel) {
            val intent = context.intentFor<TaxiActivity>(
                    START_PLACE to placeStart,
                    END_PLACE to placeEnd
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }
    }
}
