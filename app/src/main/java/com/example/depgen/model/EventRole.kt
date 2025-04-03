package com.example.depgen.model

import kotlinx.serialization.Serializable

@Serializable
data class EventRole(
    val eventRole: String,
    val color: List<Int> = listOf(0, 0, 0),
    val priority: Int,
    val maxCount: Int = Int.MAX_VALUE,
    val minCount: Int = 0,
    val prerequisites: HashMap<Skill, Int> = hashMapOf()
) : Comparable<EventRole> {
    override fun compareTo(other: EventRole) : Int {
        return this.priority.compareTo(other.priority)
    }
}
