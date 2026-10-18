package com.example.opticloudtheempire.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("opticloud_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "JWT_TOKEN"
        // Caché en memoria RAM para acceso instantáneo (0ms)
        private var memoryToken: String? = null
    }

    fun saveToken(token: String) {
        memoryToken = token
        prefs.edit().putString(TOKEN_KEY, token).commit()
    }

    fun getToken(): String? {
        if (memoryToken != null) {
            return memoryToken
        }
        memoryToken = prefs.getString(TOKEN_KEY, null)
        return memoryToken
    }

    fun clearToken() {
        memoryToken = null
        prefs.edit().remove(TOKEN_KEY).commit()
    }
}