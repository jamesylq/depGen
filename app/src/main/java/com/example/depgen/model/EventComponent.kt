package com.example.depgen.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.depgen.Global
import com.example.depgen.utils.toNaturalDateTime
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@Serializable
data class EventComponent(
    val deployment: HashMap<Profile, ArrayList<EventRole>>,
    val rolesRequired: HashMap<EventRole, Int>,
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

    fun getEvent() : Event? {
        for (event in Global.eventList) {
            for (entry in event.components) {
                if (entry.value.contains(this)) {
                    return event
                }
            }
        }
        Log.w("depGen", "Warning: Mismatched Event!")
        return null
    }

    fun setDeployment(dep: HashMap<EventRole, List<Profile>>) {
        if (deployment.isEmpty()) {
            for (entry in dep) {
                for (member in entry.value) {
                    if (!deployment.contains(member)) deployment[member] = ArrayList()
                    deployment[member]!!.add(entry.key)
                }
            }

        } else {
            Log.w("depGen", "Warning: Deployment Overridden!")
        }
    }
}
