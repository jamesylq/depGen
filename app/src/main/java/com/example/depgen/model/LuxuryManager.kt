package com.example.depgen.model

import android.util.Log
import com.example.depgen.Global
import kotlinx.serialization.Serializable

@Serializable
class LuxuryManager {
    init {
        for (profile in Global.profileList) {
            if (luxuries[profile.username] == null) {
                luxuries[profile.username] = ProfileLuxury()
            }
        }
        Log.d("depGen", "LuxuryManager initialised: $luxuries")
    }

    companion object {
        private val luxuries: HashMap<String, ProfileLuxury> = HashMap()

        fun getLuxury(profile: Profile): ProfileLuxury {
            return luxuries[profile.username]!!
        }
    }
}
