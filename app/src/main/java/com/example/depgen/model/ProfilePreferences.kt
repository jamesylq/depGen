package com.example.depgen.model

import com.example.depgen.Global
import com.example.depgen.PREFERENCES
import com.example.depgen.utils.savePreferences
import kotlinx.serialization.Serializable

@Serializable
data class ProfilePreferences (
    var preferences: HashMap<String, HashMap<String, String>>
) {
    init {
        for (profile in Global.profileList) {
            if (!preferences.contains(profile.username)) preferences[profile.username] = HashMap()
            for (entry in PREFERENCES) {
                if (!preferences[profile.username]!!.contains(entry.key)) {
                    preferences[profile.username]!![entry.key] = entry.value
                }
            }
        }
    }

    constructor(profilePreferences: ProfilePreferences) : this(profilePreferences.preferences)

    fun getPreference(param: String, profile: Profile = Global.profile): String {
        return preferences[profile.username]!![param]!!
    }

    fun setPreference(param: String, value: String, profile: Profile = Global.profile) {
        preferences[profile.username]!![param] = value
        savePreferences()
    }
}
