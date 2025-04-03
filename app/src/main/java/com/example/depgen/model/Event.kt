package com.example.depgen.model

import kotlinx.serialization.Serializable
import java.util.TreeMap


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
