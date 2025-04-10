package com.example.depgen

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.depgen.model.ComponentType
import com.example.depgen.model.ConditionType
import com.example.depgen.model.Profile
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
const val NO_DATE = "0001-01-01T00:00:00"

@RequiresApi(Build.VERSION_CODES.O)
val NO_DATE_OBJ: LocalDateTime = LocalDateTime.parse(NO_DATE)

//val OIC = EventRole("Overall in-Charge", colorToList(Color(255, 237, 0, 255)), 1, maxCount = 1)
//val IC = EventRole("In-Charge", colorToList(Color(255, 244, 179, 255)), 0, minCount = 1)
//val SOUND = EventRole("Sound Engineer", colorToList(Color(0, 115, 255, 255)), 0)
//val FOH = EventRole("Front of House", colorToList(Color(61, 148, 253, 255)), 2)
//val SFX = EventRole("SFX Engineer", colorToList(Color(104, 171, 255, 255)), 3)
//val LIGHTS = EventRole("Lighting Engineer", colorToList(Color(65, 255, 69, 255)), 1)
//val BACKSTAGE_IC = EventRole("Backstage in-Charge", colorToList(Color(255, 38, 56, 255)), 1)
//val BACKSTAGE = EventRole("Backstage", colorToList(Color(255, 134, 144, 255)), 0)
//val LIVESTREAM_IC = EventRole("Livestream in-Charge", colorToList(Color(123, 50, 255, 255)), 1)
//val LIVESTREAM = EventRole("Livestream", colorToList(Color(171, 125, 255, 255)), 0)
//
//val ROLES = listOf(OIC, IC, SOUND, FOH, SFX, LIGHTS, BACKSTAGE_IC, BACKSTAGE, LIVESTREAM_IC, LIVESTREAM)
//val SORTED_ROLES = ROLES.sorted()

val EMAIL_REGEX: Regex = Regex("^(?!.*\\.\\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")

@RequiresApi(Build.VERSION_CODES.O)
val DATETIMEFORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
