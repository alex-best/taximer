package ru.taximer.taxiandroid.utils

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.StyleRes
import android.support.v4.view.ViewCompat
import android.support.v4.widget.TextViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import ru.taximer.taxiandroid.GlideApp

///////////////////////////////////////////////////////////////////////////
// Layout Params Direct Links
///////////////////////////////////////////////////////////////////////////

const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT


///////////////////////////////////////////////////////////////////////////
// Keyboard manipulations
///////////////////////////////////////////////////////////////////////////

fun View.showKeyboard() = showKeyboard(context, this)
fun View.hideKeyboard() = hideKeyboard(context, this)


///////////////////////////////////////////////////////////////////////////
// Helpers
///////////////////////////////////////////////////////////////////////////

var View.isDisabled: Boolean
    get() = !isEnabled
    set(value) {
        isEnabled = !value
    }


///////////////////////////////////////////////////////////////////////////
// Visibility
///////////////////////////////////////////////////////////////////////////

var View.isVisible: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.GONE
    }

val View.isNotVisible: Boolean
    get() = this.visibility != View.VISIBLE

fun View.setVisible() {
    if (visibility != View.VISIBLE)
        this.visibility = View.VISIBLE
}

var View.isInvisible: Boolean
    get() = this.visibility == View.INVISIBLE
    set(value) {
        this.visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

fun View.setInvisible() {
    if (visibility != View.INVISIBLE)
        this.visibility = View.INVISIBLE
}

var View.isGone: Boolean
    get() = this.visibility == View.GONE
    set(value) {
        this.visibility = if (value) View.GONE else View.VISIBLE
    }

fun View.setGone() {
    if (visibility != View.GONE)
        this.visibility = View.GONE
}

fun View.toggleVisibility() {
    if (isVisible) isInvisible = true
    else isVisible = true
}

fun View.toggleVisibilityGone() {
    if (isVisible) isGone = true
    else isVisible = true
}


///////////////////////////////////////////////////////////////////////////
// Elevation
///////////////////////////////////////////////////////////////////////////

fun View.setElevationCompat(@DimenRes elevationResId: Int) {
    ViewCompat.setElevation(this, resources.getDimension(elevationResId))
}

fun View.setElevationCompat(elevation: Float) {
    ViewCompat.setElevation(this, elevation)
}

fun View.getElevationCompat() = ViewCompat.getElevation(this)


///////////////////////////////////////////////////////////////////////////
// Text Views
///////////////////////////////////////////////////////////////////////////

fun TextView.setTextAppearanceCompat(@StyleRes styleRes: Int) {
    TextViewCompat.setTextAppearance(this, styleRes)
}

fun TextView.setCompoundDrawableLeftCompat(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
}

fun TextView.setCompoundDrawableLeftCompat(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setCompoundDrawableLeftRetainCompat(drawable: Drawable?) {
    val drawables = compoundDrawables
    setCompoundDrawablesWithIntrinsicBounds(drawable, drawables[1], drawables[2], drawables[3])
}

fun TextView.setCompoundDrawableTopCompat(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0)
}

fun TextView.setCompoundDrawableTopCompat(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
}

fun TextView.setCompoundDrawableRightCompat(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0)
}

fun TextView.setCompoundDrawableRightCompat(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.setCompoundDrawableRightRetainCompat(drawable: Drawable?) {
    val drawables = compoundDrawables
    setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3])
}

fun TextView.setCompoundDrawableBottomCompat(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, resId)
}

fun TextView.setCompoundDrawableBottomCompat(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
}


///////////////////////////////////////////////////////////////////////////
// Image Views
///////////////////////////////////////////////////////////////////////////

private const val glideDebug = false

fun ImageView.displayImage(uri: String?,
                           @DrawableRes fallbackImageResId: Int,
                           withBlur: Boolean = false) {

    GlideApp.with(context)
            .load(uri)
            .placeholder(fallbackImageResId)
            .error(fallbackImageResId)
            .dontAnimate()
            .priority(Priority.HIGH)
            .apply { if (withBlur) bitmapTransform(BlurTransformation(context, 10, 2)) }
            .into(this)
}

fun ImageView.displayListImage(uri: String?,
                               @DrawableRes fallbackImageResId: Int,
                               withBlur: Boolean = false) {

    GlideApp.with(context)
            .load(uri)
            .placeholder(fallbackImageResId)
            .error(fallbackImageResId)
            .dontAnimate()
            .priority(Priority.HIGH)
            .apply { if (withBlur) bitmapTransform(BlurTransformation(context, 10, 2)) }
            .into(this)
}

fun ImageView.displayFullImage(uri: String?,
                               @DrawableRes fallbackImageResId: Int,
                               withBlur: Boolean = false) {
    GlideApp
            .with(context)
            .load(uri)
            .placeholder(fallbackImageResId)
            .error(fallbackImageResId)
            .dontAnimate()
            .apply { if (withBlur) bitmapTransform(BlurTransformation(context, 10, 2)) }
            .into(this)
}

///////////////////////////////////////////////////////////////////////////
// Compound Buttons
///////////////////////////////////////////////////////////////////////////

fun CompoundButton.setCheckedSilently(isChecked: Boolean, listener: CompoundButton.OnCheckedChangeListener) {
    setOnCheckedChangeListener(null)
    if (this.isChecked != isChecked) this.isChecked = isChecked
    setOnCheckedChangeListener(listener)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

inline fun <reified  T : Parcelable> createParcel(): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source : Parcel) : T? = createFromParcel(source)
            override fun newArray(size: Int) : Array<out T?> = arrayOfNulls(size)
        }

