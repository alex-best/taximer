package ru.taximer.taxiandroid.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_taxi_screen.appVersionView
import kotlinx.android.synthetic.main.activity_main_taxi_screen.drawer_layout
import kotlinx.android.synthetic.main.app_bar_main_taxi_screen.toolbar
import org.jetbrains.anko.intentFor
import ru.taximer.taxiandroid.BuildConfig
import ru.taximer.taxiandroid.R

class MainTaxiScreen : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_taxi_screen)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        appVersionView.text = BuildConfig.VERSION_NAME
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    companion object {
        fun launch(context: Activity) {
            val intent = context.intentFor<MainTaxiScreen>().apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }
    }
}
