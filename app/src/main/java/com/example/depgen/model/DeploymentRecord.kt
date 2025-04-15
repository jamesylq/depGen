package com.example.depgen.model

import java.time.LocalDate

data class DeploymentRecord (
    val roles: ArrayList<EventRole>,
    val component: EventComponent,
    val event: Event
) {
    fun getDate() : LocalDate {
        return component.getStart().toLocalDate()
    }
}
