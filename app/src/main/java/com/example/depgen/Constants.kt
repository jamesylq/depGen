package com.example.depgen

val TECHRUN = ComponentType("Techrun", 0)
val REHEARSAL = ComponentType("Rehearsal", 1)
val EXECUTION_3 = ComponentType("Tier 3 Event", 2)
val EXECUTION_2 = ComponentType("Tier 2 Event", 3)
val EXECUTION_1 = ComponentType("Tier 1 Event", 4)
val EXECUTION_0 = ComponentType("Tier 0 Event", 5)
val EVENT_TYPES = listOf(TECHRUN, REHEARSAL, EXECUTION_3, EXECUTION_2, EXECUTION_1, EXECUTION_0)

class InvalidEventTypeException(p: String): Exception(p)
