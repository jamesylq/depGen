package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.depgen.toNaturalDateTime
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.jvm.Throws

@Serializable
data class EventComponent(
    val deployment: HashMap<Profile, ArrayList<EventRole>>,
    var start: String,
    var end: String
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStart(): LocalDateTime {
        return LocalDateTime.parse(start)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEnd(): LocalDateTime {
        return LocalDateTime.parse(end)
    }

    @Throws(DateTimeParseException::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNaturalStart(): String {
        return try { toNaturalDateTime(start) }
        catch (_: DateTimeParseException) { "" }
    }

    @Throws(DateTimeParseException::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNaturalEnd(): String {
        return try { toNaturalDateTime(end) }
        catch (_: DateTimeParseException) { "" }
    }
}
