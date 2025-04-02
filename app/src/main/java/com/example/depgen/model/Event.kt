package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.TreeMap
import kotlin.jvm.Throws


@Serializable
data class Event (
    var name: String,
    val components: HashMap<ComponentType, ArrayList<EventComponent>>
) {
    fun getComponents() : TreeMap<ComponentType, ArrayList<EventComponent>> {
        val map = TreeMap<ComponentType, ArrayList<EventComponent>>(compareBy<ComponentType> { it.priority })
        for (entry in components) map[entry.key] = entry.value
        return map
    }

    fun getComponentType(component: EventComponent): ComponentType? {
        for (entry in components) {
            if (component in entry.value) {
                return entry.key
            }
        }
        return null
    }
}

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

@Serializable
data class ComponentType (
    val componentType: String,
    val priority: Int
)

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

fun lazyTime(s: String): String {
    if (s.length < 3 || s.contains(":")) return s
    val l = s.length
    return s.substring(0, l - 2) + ":" + s.substring(l - 2, l)
}

