package com.irancargocompany.logistics.utils

import android.content.Context
import android.content.SharedPreferences
import com.irancargocompany.logistics.model.User
import com.irancargocompany.logistics.model.UserType

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()
    
    companion object {
        private const val PREF_NAME = "CargoSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_ACCESS_CODE = "accessCode"
        private const val KEY_USER_TYPE = "userType"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_LAST_LOGIN = "lastLogin"
    }
    
    fun saveUserSession(user: User) {
        editor.apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.id)
            putString(KEY_ACCESS_CODE, user.accessCode)
            putString(KEY_USER_TYPE, user.userType.name)
            putString(KEY_USER_NAME, user.name)
            putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            apply()
        }
    }
    
    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val accessCode = prefs.getString(KEY_ACCESS_CODE, null) ?: return null
        val userTypeString = prefs.getString(KEY_USER_TYPE, null) ?: return null
        val userName = prefs.getString(KEY_USER_NAME, null)
        val lastLogin = prefs.getLong(KEY_LAST_LOGIN, 0L)
        
        val userType = try {
            UserType.valueOf(userTypeString)
        } catch (e: IllegalArgumentException) {
            return null
        }
        
        return User(
            id = userId,
            accessCode = accessCode,
            userType = userType,
            name = userName,
            lastLogin = lastLogin,
            isActive = true
        )
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserType(): UserType? {
        val userTypeString = prefs.getString(KEY_USER_TYPE, null) ?: return null
        return try {
            UserType.valueOf(userTypeString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    fun clearSession() {
        editor.clear().apply()
    }
    
    fun updateLastLogin() {
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis()).apply()
    }
}