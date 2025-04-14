package com.example.depgen.model

import com.example.depgen.AT_LEAST
import com.example.depgen.AT_MOST
import kotlinx.serialization.Serializable
import kotlin.math.pow

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

    fun penaltyOf(level: Int): Double {
        return when (type.id) {
            AT_LEAST.id -> {
                val r = (level + 1.0) / (args[0] + 1.0)
                return (if (r <= 1) 100.0 else 1.0) * (r - 1).pow(2)
            }
            AT_MOST.id -> {
                val r = (level + 1.0) / (args[0] + 1.0)
                return (if (r <= 1) 1.0 else 100.0) * (r - 1).pow(2)
            }
            else -> 0.0
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
