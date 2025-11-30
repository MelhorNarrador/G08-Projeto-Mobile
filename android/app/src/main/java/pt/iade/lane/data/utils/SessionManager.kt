package pt.iade.lane.data.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LaneAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_USERNAME = "user_username"
        const val USER_EMAIL = "user_email"
        const val USER_PROFILE_IMAGE = "user_profile_image"
        const val USER_BIO = "user_bio"
        const val USER_JOINED_EVENTS = "user_joined_events"
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

    fun saveUserProfile(
        name: String,
        username: String,
        email: String
    ) {
        prefs.edit()
            .putString(USER_NAME, name)
            .putString(USER_USERNAME, username)
            .putString(USER_EMAIL, email)
            .apply()
    }

    fun fetchUserName(): String? = prefs.getString(USER_NAME, null)
    fun fetchUserUsername(): String? = prefs.getString(USER_USERNAME, null)
    fun fetchUserEmail(): String? = prefs.getString(USER_EMAIL, null)

    fun saveUserProfileImage(base64: String?) {
        prefs.edit()
            .putString(USER_PROFILE_IMAGE, base64)
            .apply()
    }

    fun fetchUserProfileImage(): String? =
        prefs.getString(USER_PROFILE_IMAGE, null)

    fun saveUserBio(bio: String) {
        prefs.edit()
            .putString(USER_BIO, bio)
            .apply()
    }

    fun fetchUserBio(): String? =
        prefs.getString(USER_BIO, null)

    fun addJoinedEvent(eventId: Int) {
        val current = prefs.getStringSet(USER_JOINED_EVENTS, emptySet())?.toMutableSet()
            ?: mutableSetOf()
        current.add(eventId.toString())
        prefs.edit()
            .putStringSet(USER_JOINED_EVENTS, current)
            .apply()
    }

    fun fetchJoinedEvents(): Set<Int> {
        val set = prefs.getStringSet(USER_JOINED_EVENTS, emptySet()) ?: emptySet()
        return set.mapNotNull { it.toIntOrNull() }.toSet()
    }
}
