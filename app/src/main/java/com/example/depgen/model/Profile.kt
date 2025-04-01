package com.example.depgen.model

import android.util.Log
import com.example.depgen.Global
import com.example.depgen.ctxt
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest


@Serializable
data class Profile (
    var username: String,
    var password: String,
    var email: String
)

var ADMIN = Profile(
    "admin",
    "7c97666f116c8f112e11c4f59f8c92fd5592aad0337e0c1bdeb0497b34c0e210",
    ""
)

var LOGGED_OUT = Profile(
    "Guest",
    "",
    ""
)

val json = Json {
    allowStructuredMapKeys = true
}

fun switchProfile(newProfile: Profile) {
    Global.idx = Global.profileList.indexOf(newProfile)
    Global.profile = newProfile
}

fun findProfile(username: String) : Profile {
    for (profile in Global.profileList) {
        if (profile.username == username) {
            return profile
        }
    }
    return LOGGED_OUT
}

@Serializable
data class Wrapper(
    var profiles: ArrayList<Profile>,
    var events: ArrayList<Event>
)

@OptIn(ExperimentalStdlibApi::class)
fun String.encryptSHA256() : String {
    return MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHexString()
}

fun save() {
    Log.d("FileIO", "Initiated Save ${Global.profileList}")
    val file = File(ctxt.filesDir, "save.json")
    val wrapper = Wrapper(Global.profileList, Global.eventList)

    file.writeText(json.encodeToString(wrapper))
    Log.d("FileIO", json.encodeToString(wrapper))
}

fun clear() {
    val file = File(ctxt.filesDir, "save.json")
    file.writeText("")
}

fun load() {
    try {
        val file = File(ctxt.filesDir, "save.json")
        if (!file.exists()) {
            file.createNewFile()
            throw RuntimeException()
        }

        val wrapper: Wrapper = json.decodeFromString(file.readText())
        Log.d("FileIO", file.readText())

        Global.eventList.addAll(wrapper.events)
        Global.profileList.addAll(wrapper.profiles)
        LOGGED_OUT = Global.profileList[0]
        ADMIN = Global.profileList[1]

    } catch (_: RuntimeException) {
        Log.d("ERROR", "Save file not found, created new save file!")
        Global.profileList.clear()
        Global.profileList.add(LOGGED_OUT)
        Global.profileList.add(ADMIN)
    }
}
