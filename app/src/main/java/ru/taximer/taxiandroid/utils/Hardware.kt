package ru.taximer.taxiandroid.utils

import android.content.Context
import android.os.Build
import android.support.v4.view.ViewConfigurationCompat
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import ru.taximer.taxiandroid.R


///////////////////////////////////////////////////////////////////////////
// Processor Architecture
///////////////////////////////////////////////////////////////////////////

val is32bitArch: Boolean by lazy {
    val arch = System.getProperty("os.arch")
    arch.isNullOrEmpty() || !arch.contains("64")
}

val is64bitArch: Boolean by lazy {
    val arch = System.getProperty("os.arch") ?: "64"
    arch.contains("64")
}


///////////////////////////////////////////////////////////////////////////
// Has Software Nav Bar
///////////////////////////////////////////////////////////////////////////

fun hasSoftwareNavBar(context: Context): Boolean {

    if (Build.FINGERPRINT.startsWith("generic"))
        return true

    val hasMenuKey = ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(context))
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

    return !hasMenuKey && !hasBackKey
}

///////////////////////////////////////////////////////////////////////////
// Resources
///////////////////////////////////////////////////////////////////////////

fun getStatusBarHeight(context: Context): Int =
        getIntResource(context, "status_bar_height", "dimen", "android")

fun getActionBarHeight(context: Context): Int {
    val heightAttr = intArrayOf(R.attr.actionBarSize)
    val typedArray = context.theme.obtainStyledAttributes(heightAttr)
    val actionBarHeight = typedArray.getDimensionPixelSize(0, 0)
    typedArray.recycle()
    return actionBarHeight
}

fun getNavigationBarHeight(context: Context): Int =
        getIntResource(context, "navigation_bar_height", "dimen", "android")

fun getIntResource(context: Context, name: String, defType: String, defPackage: String): Int {
    val appContext = context.applicationContext
    var result = 0

    val resourceId = appContext.resources.getIdentifier(name, defType, defPackage)
    if (resourceId > 0)
        result = appContext.resources.getDimensionPixelSize(resourceId)

    return result
}

///////////////////////////////////////////////////////////////////////////
// Keyboard
///////////////////////////////////////////////////////////////////////////

fun showKeyboard(context: Context, view: View) {
    val appContext = context.applicationContext
    view.postDelayed({
        view.requestFocus()
        appContext.inputMethodManager.showSoftInput(view, 0)
    }, 64)
}

fun hideKeyboard(context: Context, view: View) {
    val windowToken = view.windowToken
    val appContext = context.applicationContext
    view.post{
        view.clearFocus()
        appContext.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
