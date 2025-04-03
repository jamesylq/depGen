package com.example.depgen.model

import kotlinx.serialization.Serializable

@Serializable
data class ComponentType (
    val componentType: String,
    val priority: Int
)
