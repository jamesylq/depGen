package com.example.depgen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.depgen.model.Profile
import com.example.depgen.model.Wrapper
import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.jvm.Throws


fun switchProfile(newProfile: Profile) {
    Global.idx = Global.profileList.indexOf(newProfile)
    Global.profile = newProfile
}

fun colorToList(color: Color): List<Int> {
    return listOf((color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())
}

fun listToColor(lst: List<Int>): Color {
    return Color(lst[0], lst[1], lst[2], 255)
}

fun findProfile(username: String) : Profile {
    for (profile in Global.profileList) {
        if (profile.username == username) {
            return profile
        }
    }
    return LOGGED_OUT
}

@Throws(DateTimeParseException::class)
@RequiresApi(Build.VERSION_CODES.O)
fun toNaturalDateTime(isoString: String): String {
    return LocalDateTime.parse(isoString).format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a"))
}

@Throws(DateTimeParseException::class)
@RequiresApi(Build.VERSION_CODES.O)
fun toHHMMTime(datetime: LocalDateTime): String {
    return datetime.format(DateTimeFormatter.ofPattern("HH:mm"))
}

fun save() {
    Log.d("FileIO", "Initiated Save ${Global.profileList}")
    val file = File(ctxt.filesDir, "save.json")
    val wrapper = Wrapper(Global.profileList, Global.eventList, Global.skillsList, Global.rolesList)

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

        Global.eventList.clear()
        Global.profileList.clear()
        Global.skillsList.clear()
        Global.rolesList.clear()
        Global.eventList.addAll(wrapper.events)
        Global.profileList.addAll(wrapper.profiles)
        Global.skillsList.addAll(wrapper.skills)
        Global.rolesList.addAll(wrapper.roles)
        LOGGED_OUT = Global.profileList[0]
        ADMIN = Global.profileList[1]

    } catch (_: RuntimeException) {
        Log.d("ERROR", "Save file not found, created new save file!")
        Global.profileList.clear()
        Global.eventList.clear()
        Global.skillsList.clear()
        Global.rolesList.clear()
        Global.profileList.add(LOGGED_OUT)
        Global.profileList.add(ADMIN)
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun String.encryptSHA256() : String {
    return MessageDigest.getInstance("SHA-256").digest(toByteArray()).toHexString()
}

fun lazyTime(s: String): String {
    if (s.length < 3 || s.contains(":")) return s
    val l = s.length
    return s.substring(0, l - 2) + ":" + s.substring(l - 2, l)
}

fun isInt(s: String): Boolean {
    return s.isNotBlank() && s.toIntOrNull() != null
}

fun isNotInt(s: String): Boolean {
    return s.isNotBlank() && s.toIntOrNull() == null
}
