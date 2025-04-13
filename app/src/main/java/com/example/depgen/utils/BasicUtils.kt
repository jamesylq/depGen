package com.example.depgen.utils

import android.os.Build
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
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import com.example.depgen.navController
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


fun switchProfile(newProfile: Profile) {
    Global.idx = Global.profileList.indexOf(newProfile)
    Global.profile = newProfile
}

fun List<Int>.toColor() : Color{
    return Color(this[0], this[1], this[2], 255)
}

fun Color.toList(): List<Int> {
    return listOf((this.red * 255).toInt(), (this.green * 255).toInt(), (this.blue * 255).toInt())
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
fun String.toNaturalDateTime(): String {
    return LocalDateTime.parse(this).format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a"))
}

@Throws(DateTimeParseException::class)
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toHHMMTime(): String {
    return this.format(DateTimeFormatter.ofPattern("HH:mm"))
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

fun String.isInt(): Boolean {
    return this.isNotBlank() && this.toIntOrNull() != null
}

fun String.isNotInt(): Boolean {
    return this.isNotBlank() && this.toIntOrNull() == null
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
