package com.example.depgen.model

import kotlinx.serialization.Serializable

@Serializable
data class Skill (
    val skill: String,
    val maxLevel: Int,
    val defaultLevel: Int = 0
)
