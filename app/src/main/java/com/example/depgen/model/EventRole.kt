package com.example.depgen.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class EventRole(
    val eventRole: String,
    val color: List<Int> = listOf(0, 0, 0),
    val priority: Int
) : Comparable<EventRole> {
    override fun compareTo(other: EventRole) : Int {
        return this.priority.compareTo(other.priority)
    }
}

fun colorToList(color: Color): List<Int> {
    return listOf((color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())
}

fun listToColor(lst: List<Int>): Color {
    return Color(lst[0], lst[1], lst[2], 255)
}


val OIC = EventRole("Overall in-Charge", colorToList(Color(255, 237, 0, 255)), 1)
val IC = EventRole("In-Charge", colorToList(Color(255, 244, 179, 255)), 0)
val SOUND = EventRole("Sound Engineer", colorToList(Color(0, 115, 255, 255)), 0)
val FOH = EventRole("Front of House", colorToList(Color(61, 148, 253, 255)), 2)
val SFX = EventRole("SFX Engineer", colorToList(Color(104, 171, 255, 255)), 3)
val LIGHTS = EventRole("Lighting Engineer", colorToList(Color(65, 255, 69, 255)), 1)
val BACKSTAGE_IC = EventRole("Backstage in-Charge", colorToList(Color(255, 38, 56, 255)), 1)
val BACKSTAGE = EventRole("Backstage", colorToList(Color(255, 134, 144, 255)), 0)
val LIVESTREAM_IC = EventRole("Livestream in-Charge", colorToList(Color(123, 50, 255, 255)), 1)
val LIVESTREAM = EventRole("Livestream", colorToList(Color(171, 125, 255, 255)), 0)

val ROLES = listOf(OIC, IC, SOUND, FOH, SFX, LIGHTS, BACKSTAGE_IC, BACKSTAGE, LIVESTREAM_IC, LIVESTREAM)
