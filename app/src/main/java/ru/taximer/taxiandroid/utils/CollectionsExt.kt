package ru.taximer.taxiandroid.utils

import android.support.v4.util.SparseArrayCompat
import android.util.LongSparseArray
import android.util.SparseArray
import java.util.Collections
import java.util.HashMap
import android.support.v4.util.LongSparseArray as LongSparseArrayCompat

fun <T> Collection<T>?.isNullOrEmpty() = this == null || isEmpty()
fun <T> Collection<T>?.isNotNullOrEmpty() = !isNullOrEmpty()

fun <T> Collection<T>.disjoint(other: Collection<T>) = Collections.disjoint(this, other)

fun <K, V> MutableMap<K, V>.put(pair: Pair<K, V>) = put(pair.first, pair.second)


///////////////////////////////////////////////////////////////////////////
// Sparse Arrays
///////////////////////////////////////////////////////////////////////////

fun <T> SparseArray<T>.toHashMap(): HashMap<Int, T> {
    val size = size()
    val map = HashMap<Int, T>(size)
    for (i in 0..size - 1) {
        map.put(keyAt(i), valueAt(i))
    }
    return map
}

fun <T> Map<Int, T>.toSparseArray(): SparseArray<T> {
    val array = SparseArray<T>(size)
    for ((key, value) in entries) {
        array.put(key, value)
    }
    return array
}

fun <T> LongSparseArray<T>.toHashMap(): HashMap<Long, T> {
    val size = size()
    val map = HashMap<Long, T>(size)
    for (i in 0..size - 1) {
        map.put(keyAt(i), valueAt(i))
    }
    return map
}

fun <T> Map<Long, T>.toSparseArray(): LongSparseArray<T> {
    val array = LongSparseArray<T>(size)
    for ((key, value) in entries) {
        array.put(key, value)
    }
    return array
}

fun <T> SparseArrayCompat<T>.toHashMap(): HashMap<Int, T> {
    val size = size()
    val map = HashMap<Int, T>(size)
    for (i in 0..size - 1) {
        map.put(keyAt(i), valueAt(i))
    }
    return map
}

fun <T> Map<Int, T>.toSparseArrayCompat(): SparseArrayCompat<T> {
    val array = SparseArrayCompat<T>(size)
    for ((key, value) in entries) {
        array.put(key, value)
    }
    return array
}

fun <T> android.support.v4.util.LongSparseArray<T>.toHashMap(): HashMap<Long, T> {
    val size = size()
    val map = HashMap<Long, T>(size)
    for (i in 0..size - 1) {
        map.put(keyAt(i), valueAt(i))
    }
    return map
}

fun <T> Map<Long, T>.toSparseArrayCompat(): android.support.v4.util.LongSparseArray<T> {
    val array = android.support.v4.util.LongSparseArray<T>(size)
    for ((key, value) in entries) {
        array.put(key, value)
    }
    return array
}

fun SparseArray<*>.containsKey(key: Int): Boolean = indexOfKey(key) > -1
fun SparseArrayCompat<*>.containsKey(key: Int): Boolean = indexOfKey(key) > -1
fun LongSparseArray<*>.containsKey(key: Long): Boolean = indexOfKey(key) > -1
fun android.support.v4.util.LongSparseArray<*>.containsKey(key: Long): Boolean = indexOfKey(key) > -1
