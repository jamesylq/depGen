package com.example.depgen.model

import com.example.depgen.utils.NO_DATE_OBJ
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.TreeMap


@Serializable
data class Event (
    var name: String,
    val components: HashMap<ComponentType, ArrayList<EventComponent>>
) {
    fun getComponents() : TreeMap<ComponentType, ArrayList<EventComponent>> {
        val map = TreeMap<ComponentType, ArrayList<EventComponent>>(compareBy<ComponentType> { it.priority })
        for (entry in components) map[entry.key] = ArrayList(entry.value)
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

    fun hasCompleted(time: LocalDateTime? = null) : Boolean {
        return !getLatest().isAfter(time?: LocalDateTime.now())
    }

    fun getLatest() : LocalDateTime {
        var latest = NO_DATE_OBJ
        for (entry in components) {
            for (component in entry.value) {
                latest = maxOf(latest, component.getEnd())
            }
        }
        return latest
    }

    override fun toString(): String {
        return "${name.uppercase()}\n\n\n${
            getComponents().map {
                it.value.joinToString("\n\n\n") {
                    component -> "${it.key.componentType}\n" + component.toString(true)
                }
            }.joinToString ("\n\n\n")
        }"
    }
}
