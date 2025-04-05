package com.example.depgen.model

import com.example.depgen.AT_LEAST
import com.example.depgen.AT_MOST
import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val type: ConditionType,
    val args: List<Int>
) {
    fun check(level: Int) : Boolean {
        return when (type.id) {
            AT_LEAST.id -> { level >= args[0] }
            AT_MOST.id -> { level <= args[0] }
            else -> false
        }
    }

    override fun toString(): String {
        return when (type.id) {
            AT_LEAST.id -> { "at least ${args[0]}" }
            AT_MOST.id -> { "at most ${args[0]}" }
            else -> "ERROR"
        }
    }
}
