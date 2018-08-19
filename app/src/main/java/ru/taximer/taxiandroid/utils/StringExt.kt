package ru.taximer.taxiandroid.utils

import android.util.Patterns

fun CharSequence?.isNotNullOrEmpty() = !isNullOrEmpty()

///////////////////////////////////////////////////////////////////////////
// Validators
///////////////////////////////////////////////////////////////////////////

val CharSequence?.isEmail: Boolean
    get() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

val CharSequence?.isPhone: Boolean
    get() = !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()

val CharSequence.isUrl: Boolean
    get() = !isNullOrEmpty() && Patterns.WEB_URL.matcher(this).matches()

val CharSequence?.isDigitsOnly: Boolean
    get() {
        this ?: return false
        if (isEmpty()) return false

        for (c in this)
            if (!Character.isDigit(c))
                return false

        return true
    }

val CharSequence?.isAlphaOnly: Boolean
    get() {
        this ?: return false
        if (isEmpty()) return false

        for (c in this)
            if (!Character.isLetter(c))
                return false

        return true
    }

///////////////////////////////////////////////////////////////////////////
// Capitalization
///////////////////////////////////////////////////////////////////////////

/** Capitalizes the first character and also **trims** the sequence. */
fun CharSequence?.capitalize(): CharSequence? {
    this ?: return null
    this.trim()
    return CharArray(length, { this[it] })
            .apply { if (length > 0) this[0] = Character.toUpperCase(this[0]) }
            .joinTo(StringBuilder(), "")
}

/** Capitalizes the first character of each word in string and also **trims** the sequence. */
fun CharSequence?.capitalizeWords(): CharSequence? = this?.trim()?.split(" ")?.map(String::capitalize)?.joinTo(StringBuilder(), " ")

/** Capitalizes the first character and also **trims** the sequence. */
fun CharSequence?.capitalizeToString(): String? = if (this == null) null else this.capitalize().toString()

/** Capitalizes the first character of each word in string and also **trims** the sequence. */
fun CharSequence?.capitalizeWordsToString(): String? = if (this == null) null else this.capitalizeWords().toString()
