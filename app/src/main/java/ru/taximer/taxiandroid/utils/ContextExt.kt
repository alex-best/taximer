package ru.taximer.taxiandroid.utils

import android.app.AlarmManager
import android.app.DownloadManager
import android.app.KeyguardManager
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.PowerManager
import android.os.Vibrator
import android.support.v4.app.NotificationManagerCompat
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import org.jetbrains.anko.configuration
import java.util.Calendar

private const val TABLET_SMALLEST_WIDTH = 600

///////////////////////////////////////////////////////////////////////////
// Hardware
///////////////////////////////////////////////////////////////////////////

val Context.hasSoftwareNavBar: Boolean
    get() = hasSoftwareNavBar(this)

val Context.statusBarHeight: Int
    get() = getStatusBarHeight(this)

val Context.actionBarHeight: Int
    get() = getActionBarHeight(this)

val Context.navBarHeight: Int
    get() = getNavigationBarHeight(this)

val Context.isPhone: Boolean
    get() = configuration.isPhone

val Configuration.isPhone: Boolean
    get() = smallestScreenWidthDp < TABLET_SMALLEST_WIDTH

val Context.isTablet: Boolean
    get() = configuration.isTablet

val Configuration.isTablet: Boolean
    get() = smallestScreenWidthDp >= TABLET_SMALLEST_WIDTH

val Context.isPortrait: Boolean
    get() = configuration.isPortrait

val Configuration.isPortrait: Boolean
    get() = orientation == Configuration.ORIENTATION_PORTRAIT

val Context.isLandscape: Boolean
    get() = configuration.isLandscape

val Configuration.isLandscape: Boolean
    get() = orientation == Configuration.ORIENTATION_LANDSCAPE

val Context.isNetworkAvailable: Boolean
    get() = connectivityManager.activeNetworkInfo.isConnected


///////////////////////////////////////////////////////////////////////////
// Services
///////////////////////////////////////////////////////////////////////////

val Context.layoutInflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.notificationManager: NotificationManagerCompat
    get() = NotificationManagerCompat.from(this)

val Context.vibrator: Vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

val Context.downloadManager: DownloadManager
    get() = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

val Context.clipboardManager: ClipboardManager
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

val Context.powerManager: PowerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager

val Context.keyguardManager: KeyguardManager
    get() = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


fun Context.isFirstInstall(): Int{
    val c = Calendar.getInstance()
    c.roll(Calendar.HOUR, false)
    try {
        val info = packageManager.getPackageInfo(applicationContext.packageName, PackageManager.GET_ACTIVITIES)
        val lt = info.firstInstallTime
        return if (lt != -1L && lt - c.timeInMillis < 0) 1 else 0
    }
    catch (e: PackageManager.NameNotFoundException) {
        return 0
    }

}