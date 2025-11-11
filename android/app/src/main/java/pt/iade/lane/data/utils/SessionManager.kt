package pt.iade.lane.data.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LaneAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
    }
    fun saveAuth(token: String, userId: Int) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putInt(USER_ID, userId)
        editor.apply()
    }
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    fun fetchUserId(): Int? {
        val id = prefs.getInt(USER_ID, -1)
        return if (id == -1) null else id
    }
    fun clearAuth() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}