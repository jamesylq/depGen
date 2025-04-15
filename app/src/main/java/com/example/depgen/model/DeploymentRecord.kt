package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class DeploymentRecord (
    val roles: ArrayList<EventRole>,
    val component: EventComponent,
    val event: Event
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate() : LocalDate {
        return component.getStart().toLocalDate()
    }
}
