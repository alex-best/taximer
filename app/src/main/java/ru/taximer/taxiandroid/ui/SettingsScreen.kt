package ru.taximer.taxiandroid.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_applications.root_container
import kotlinx.android.synthetic.main.activity_settings.buisnes
import kotlinx.android.synthetic.main.activity_settings.card
import kotlinx.android.synthetic.main.activity_settings.cash
import kotlinx.android.synthetic.main.activity_settings.child
import kotlinx.android.synthetic.main.activity_settings.comfort
import kotlinx.android.synthetic.main.activity_settings.economy
import kotlinx.android.synthetic.main.activity_settings.miniven
import org.jetbrains.anko.intentFor
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.presenters.SettingsPresenter
import ru.taximer.taxiandroid.presenters.SettingsView

class SettingsActivity : MvpAppCompatActivity(), SettingsView, View.OnClickListener {

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: SettingsPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun provideSettingsPresenter(): SettingsPresenter = SettingsPresenter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initUI()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUI() {
        comfort.setData(R.drawable.ic_car_comfort, R.string.label_settings_comfort_title, R.string.label_settings_comfort_description)
        buisnes.setData(R.drawable.ic_car_business, R.string.label_settings_buisnes_title, R.string.label_settings_buisnes_description)
        economy.setData(R.drawable.ic_car_economy, R.string.label_settings_economy_title, R.string.label_settings_economy_description)
        child.setData(R.drawable.ic_kids_seat, R.string.label_settings_child_title, R.string.label_settings_child_description)
        miniven.setData(R.drawable.ic_car_van, R.string.label_settings_miniven_title, R.string.label_settings_miniven_description)

        economy.setOnClickListener(this)
        comfort.setOnClickListener(this)
        buisnes.setOnClickListener(this)
        child.setOnClickListener(this)
        miniven.setOnClickListener(this)

        card.setOnCheckedChangeListener { _, value -> presenter.setCard(value) }
        cash.setOnCheckedChangeListener { _, value -> presenter.setCash(value) }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setBuisnes() {
        buisnes.setChecked(true)
        economy.setChecked(false)
        comfort.setChecked(false)
        miniven.setChecked(false)
    }

    override fun setComfort() {
        buisnes.setChecked(false)
        economy.setChecked(false)
        comfort.setChecked(true)
        miniven.setChecked(false)
    }

    override fun setEconomy() {
        buisnes.setChecked(false)
        economy.setChecked(true)
        comfort.setChecked(false)
        miniven.setChecked(false)
    }

    override fun setMiniven() {
        buisnes.setChecked(false)
        economy.setChecked(false)
        comfort.setChecked(false)
        miniven.setChecked(true)
    }

    override fun setIsChild(value: Boolean) {
        child.setChecked(value)
    }

    override fun setIsCAsh(value: Boolean) {
        cash.isChecked = value
    }

    override fun setIsCard(value: Boolean) {
        card.isChecked = value
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.economy -> presenter.setEconomy()
            R.id.comfort -> presenter.setComfort()
            R.id.buisnes -> presenter.setBuisnes()
            R.id.miniven -> presenter.setMiniven()
            R.id.child -> presenter.setChild()
        }
    }

    override fun showError(message: String) {
        Log.d("Dto", message)
        val snackbar = Snackbar.make(root_container, message, Snackbar.LENGTH_LONG)

        snackbar.show()
    }

    companion object {
        fun launch(context: Activity) {
            val intent = context.intentFor<SettingsActivity>().apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }
    }
}