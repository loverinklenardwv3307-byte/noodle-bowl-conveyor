package com.broth.noodleconveyor.data.local

import android.content.Context
import android.content.SharedPreferences

class NoodlePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    var stamps: Int
        get() = prefs.getInt(KEY_STAMPS, 0)
        set(value) = prefs.edit().putInt(KEY_STAMPS, value).apply()

    var bowlsServed: Int
        get() = prefs.getInt(KEY_BOWLS, 0)
        set(value) = prefs.edit().putInt(KEY_BOWLS, value).apply()

    var bestCombo: Int
        get() = prefs.getInt(KEY_COMBO, 0)
        set(value) = prefs.edit().putInt(KEY_COMBO, value).apply()

    var selectedShop: Int
        get() = prefs.getInt(KEY_SHOP, 1)
        set(value) = prefs.edit().putInt(KEY_SHOP, value).apply()

    var streak: Int
        get() = prefs.getInt(KEY_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_STREAK, value).apply()

    var lastPlayedDay: Long
        get() = prefs.getLong(KEY_DAY, 0L)
        set(value) = prefs.edit().putLong(KEY_DAY, value).apply()

    var firstShiftDone: Boolean
        get() = prefs.getBoolean(KEY_FIRST_SHIFT, false)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_SHIFT, value).apply()

    var paceName: String
        get() = prefs.getString(KEY_PACE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PACE, value).apply()

    var haptics: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var leftHandMode: Boolean
        get() = prefs.getBoolean(KEY_LEFT_HAND, false)
        set(value) = prefs.edit().putBoolean(KEY_LEFT_HAND, value).apply()

    var relaxedMode: Boolean
        get() = prefs.getBoolean(KEY_RELAXED, false)
        set(value) = prefs.edit().putBoolean(KEY_RELAXED, value).apply()

    var ownedDecor: Set<Int>
        get() = (prefs.getStringSet(KEY_DECOR, emptySet()) ?: emptySet())
            .mapNotNull { it.toIntOrNull() }
            .toSet()
        set(value) = prefs.edit()
            .putStringSet(KEY_DECOR, value.map { it.toString() }.toSet())
            .apply()

    fun starsFor(shopId: Int): Int = prefs.getInt(KEY_STARS + shopId, 0)

    fun setStars(shopId: Int, stars: Int) {
        prefs.edit().putInt(KEY_STARS + shopId, stars).apply()
    }

    fun clearAll(shopIds: List<Int>) {
        val editor = prefs.edit()
        editor.remove(KEY_STAMPS)
        editor.remove(KEY_BOWLS)
        editor.remove(KEY_COMBO)
        editor.remove(KEY_SHOP)
        editor.remove(KEY_STREAK)
        editor.remove(KEY_DAY)
        editor.remove(KEY_DECOR)
        shopIds.forEach { editor.remove(KEY_STARS + it) }
        editor.apply()
    }

    private companion object {
        const val STORE = "noodle_bowl_conveyor"
        const val KEY_STAMPS = "stamps"
        const val KEY_BOWLS = "bowls_served"
        const val KEY_COMBO = "best_combo"
        const val KEY_SHOP = "selected_shop"
        const val KEY_STREAK = "streak"
        const val KEY_DAY = "last_played_day"
        const val KEY_FIRST_SHIFT = "first_shift_done"
        const val KEY_PACE = "pace"
        const val KEY_HAPTICS = "haptics"
        const val KEY_LEFT_HAND = "left_hand"
        const val KEY_RELAXED = "relaxed"
        const val KEY_DECOR = "owned_decor"
        const val KEY_STARS = "stars_shop_"
    }
}
