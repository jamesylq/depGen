package com.example.depgen.model

import android.util.Log
import androidx.compose.ui.util.fastJoinToString
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
    fun getStart(): LocalDateTime {
        return LocalDateTime.parse(start)
    }

    fun getEnd(): LocalDateTime {
        return LocalDateTime.parse(end)
    }

    @Throws(DateTimeParseException::class)
    fun getNaturalStart(): String {
        return try { start.toNaturalDateTime() }
        catch (_: DateTimeParseException) { "" }
    }

    @Throws(DateTimeParseException::class)
    fun getNaturalEnd(): String {
        return try { end.toNaturalDateTime() }
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

    private fun toNaturalDeployments() : Map<EventRole, ArrayList<Profile>> {
        val toReturn = HashMap<EventRole, ArrayList<Profile>>()
        for (entry in deployment) {
            for (role in entry.value) {
                if (!toReturn.containsKey(role)) toReturn[role] = ArrayList()
                toReturn[role]!!.add(entry.key)
            }
        }
        return toReturn
    }


    fun toString(displayTime: Boolean = false, preamble: String = ""): String {
        return if (displayTime) """
            ${preamble}Start: ${start.toNaturalDateTime()}
            End: ${end.toNaturalDateTime()}
            
            Deployments
            $this
        """.trimIndent().trimMargin() else """
            ${preamble}Deployment
            $this
        """.trimIndent().trimMargin()
    }

    override fun toString(): String {
        return toNaturalDeployments().map {
            "   •   ${it.key.eventRole}: ${it.value.map{
                profile -> profile.username
            }.fastJoinToString(", ")}"
        }.fastJoinToString("\n")
    }
}
