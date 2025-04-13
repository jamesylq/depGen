package com.example.depgen

import com.example.depgen.model.ComponentType
import com.example.depgen.model.ConditionType
import com.example.depgen.model.Profile
import kotlinx.serialization.json.Json

val TECHRUN = ComponentType("Techrun", 0)
val REHEARSAL = ComponentType("Rehearsal", 1)
val EXECUTION_3 = ComponentType("Tier 3 Event", 2)
val EXECUTION_2 = ComponentType("Tier 2 Event", 3)
val EXECUTION_1 = ComponentType("Tier 1 Event", 4)
val EXECUTION_0 = ComponentType("Tier 0 Event", 5)
val EVENT_TYPES = listOf(TECHRUN, REHEARSAL, EXECUTION_3, EXECUTION_2, EXECUTION_1, EXECUTION_0)

val AT_LEAST = ConditionType(100000, "at least")
val AT_MOST = ConditionType(100001, "at most")

val CONDITION_TYPES = listOf(AT_LEAST.typeName, AT_MOST.typeName)

const val DELTA = 1000L
const val MAX_REPEATING_DEPLOYMENT_DAYS = 366
const val MAX_COMPLETE_SEARCH_DURATION = 60000L
const val MAX_COMPLETE_CHECK_TIME_STATE_COUNT = 1000L

val EMAIL_REGEX: Regex = Regex("^(?!.*\\.\\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")

var ADMIN = Profile(
    "admin",
    "7c97666f116c8f112e11c4f59f8c92fd5592aad0337e0c1bdeb0497b34c0e210",
    ""
)

var LOGGED_OUT = Profile(
    "Guest",
    "",
    ""
)

val json = Json {
    allowStructuredMapKeys = true
}
