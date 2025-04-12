package com.example.depgen.model

import kotlinx.serialization.Serializable

@Serializable
data class EventRole(
    var eventRole: String,
    var color: List<Int> = listOf(0, 0, 0),
    var priority: Int,
    val maxCount: Int = Int.MAX_VALUE,
    val minCount: Int = 0,
    var prerequisites: HashMap<Skill, ArrayList<Condition?>> = hashMapOf()
) : Comparable<EventRole> {

    fun satisfiedBy(profile: Profile) : Boolean{
        for (entry in prerequisites) {
            if (!profile.skills.containsKey(entry.key)) {
                profile.skills[entry.key] = entry.key.defaultLevel
            }
            for (condition in entry.value) {
                if (!condition!!.check(profile.skills[entry.key]!!)) {
                    return false
                }
            }
        }
        return true
    }

    override fun compareTo(other: EventRole) : Int {
        return this.priority.compareTo(other.priority)
    }
}
