package com.example.depgen

import com.example.depgen.Global.idx
import com.example.depgen.Global.profile
import com.example.depgen.Global.profileList
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest


@Serializable
data class Profile (
    var username: String,
    var password: String
)

val ADMIN = Profile(
    "admin",
    "7c97666f116c8f112e11c4f59f8c92fd5592aad0337e0c1bdeb0497b34c0e210"
)

val LOGGED_OUT = Profile(
    "Guest",
    ""
)

fun switchProfile(newProfile: Profile) {
    idx = profileList.indexOf(newProfile)
    profile = newProfile
}

fun findProfile(username: String) : Profile {
    for (profile in profileList) {
        if (profile.username == username) {
            return profile
        }
    }
    return LOGGED_OUT
}

@Serializable
data class Wrapper (
    var global: Global
)

@OptIn(ExperimentalStdlibApi::class)
fun String.encryptSHA256() : String {
    return MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHexString()
}

//fun save() {
//    val file = File(ctxt.filesDir, "save.json")
//    file.writeText(Json.encodeToString(Global))
//}
//
//fun clear() {
//    val file = File(ctxt.filesDir, "save.json")
//    file.writeText("")
//}
//
//fun load() {
//    try {
//        val file = File(ctxt.filesDir, "save.json")
//        if (!file.exists()) throw RuntimeException()
//
//        val global: Global = Json.decodeFromString(file.readText())
//        Global.profile = global.profile
//
//    } catch (_: RuntimeException) { }
//}
