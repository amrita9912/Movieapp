package com.example.movieapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PreferenceManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCE_NAME = "MyAppPreferences"
        private const val KEY_LAST_VISITED_SCREEN = "last_visited_screen"
        private const val KEY_LAST_VISITED_LIST_POSITION =
            "last_visited_list_position" // Add this key

    }

    fun saveLastVisitedScreen(screen: String) {
        sharedPreferences.edit().putString(KEY_LAST_VISITED_SCREEN, screen).apply()
        Log.d("PreferenceManager", "Last visited screen saved: $screen")
    }

    fun getLastVisitedScreen(): String? {
        return sharedPreferences.getString(KEY_LAST_VISITED_SCREEN, null)
    }


}
