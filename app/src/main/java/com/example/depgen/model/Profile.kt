package com.example.depgen.model

import com.example.depgen.Global
import kotlinx.serialization.Serializable


@Serializable
data class Profile (
    var username: String,
    var password: String,
    var email: String,
    val skills: HashMap<Skill, Int> = hashMapOf()
) {
    fun getIdx(): Int {
        for (i in Global.profileList.indices) {
            if (Global.profileList[i].username == this.username) {
                return i
            }
        }
        return -1
    }
}
