package ru.taximer.taxiandroid.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

///////////////////////////////////////////////////////////////////////////
// PERMISSIONS
///////////////////////////////////////////////////////////////////////////

val PERMISSIONS_LOCATION: Array<String> by lazy { arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) }
val PERMISSIONS_STORAGE: Array<String> by lazy { arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) }
val PERMISSIONS_CAMERA: Array<String> by lazy { arrayOf(Manifest.permission.CAMERA) }
val PERMISSIONS_MIC: Array<String> by lazy { arrayOf(Manifest.permission.RECORD_AUDIO) }


///////////////////////////////////////////////////////////////////////////
// Verify Permissions Results
///////////////////////////////////////////////////////////////////////////

/** Checks if permissions were granted by user */
fun verifyPermissionsResults(permissions: Array<out String>, grantResults: IntArray): Boolean =
        if (permissions.isEmpty() || grantResults.isEmpty()) false
        else verifyPermissions(grantResults)

private fun verifyPermissions(grantResults: IntArray): Boolean {
    if (grantResults.isEmpty())
        return false
    grantResults.forEach { if (it != PackageManager.PERMISSION_GRANTED) return false }
    return true
}


///////////////////////////////////////////////////////////////////////////
// Are Permissions Granted
///////////////////////////////////////////////////////////////////////////

/** If **false** then should show rationale or request permissions */
fun Activity.arePermissionsGranted(vararg permissions: String): Boolean {
    permissions.forEach { permission ->
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            return false
    }
    return true
}

/** If **false** then should show rationale or request permissions */
fun Fragment.arePermissionsGranted(vararg permissions: String): Boolean =
        activity?.arePermissionsGranted(*permissions) ?: false


///////////////////////////////////////////////////////////////////////////
// Should Show Rationale
///////////////////////////////////////////////////////////////////////////

/** @return **true** if should show rationale, **false** if should request permission */
fun Activity.shouldShowRationale(vararg permissions: String): Boolean {
    var shouldShowRationale = false
    permissions.forEach { permission ->
        shouldShowRationale = shouldShowRationale or ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    }

    return shouldShowRationale
}

fun Fragment.shouldShowRationale(vararg permissions: String): Boolean =
        activity?.shouldShowRationale(*permissions) ?: false


///////////////////////////////////////////////////////////////////////////
// Check For Blocked Permissions
///////////////////////////////////////////////////////////////////////////

/** Returns _true_ if user checked "Don't ask again" checkbox for any of [permissions]. */
fun Activity.isDontAskTriggered(permissions: Array<String>, grantResults: IntArray): Boolean {
    val permissionsLastIndex = permissions.lastIndex
    return (0..permissionsLastIndex).any {
        grantResults[it] == PackageManager.PERMISSION_DENIED && !shouldShowRationale(permissions[it])
    }
}

fun Fragment.isDontAskTriggered(permissions: Array<String>, grantResults: IntArray): Boolean =
        activity?.isDontAskTriggered(permissions, grantResults) ?: false
