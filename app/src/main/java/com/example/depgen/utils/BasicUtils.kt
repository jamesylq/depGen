package com.example.depgen.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import com.example.depgen.ADMIN
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.ctxt
import com.example.depgen.json
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import com.example.depgen.model.Wrapper
import com.example.depgen.navController
import java.io.File
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


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

fun findRole(role: String): EventRole? {
    for (eventRole in Global.rolesList) {
        if (eventRole.eventRole == role) {
            return eventRole
        }
    }
    return null
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

        for (i in 1 ..< Global.profileList.size) {
            for (skill in Global.skillsList) {
                if (!Global.profileList[i].skills.containsKey(skill)) {
                    Global.profileList[i].skills[skill] = skill.defaultLevel
                }
            }
        }

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

fun safeNavigate(dest: String) {
    navController.navigate(dest) { popUpTo(dest) }
}

@OptIn(ExperimentalLayoutApi::class)
@Stable
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }

    if (isFocused) {
        val imeIsVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current

        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }

    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) keyboardAppearedSinceLastFocused = false
        }
    }
}
