package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
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

    @RequiresApi(Build.VERSION_CODES.O)
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
