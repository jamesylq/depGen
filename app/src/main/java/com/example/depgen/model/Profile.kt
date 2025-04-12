package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.depgen.Global
import com.example.depgen.utils.DATETIMEFORMATTER
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Serializable
data class Profile (
    var username: String,
    var password: String,
    var email: String,
    val skills: HashMap<Skill, Int> = HashMap(),
    val deployments: ArrayList<String> = ArrayList()
) {
    fun getIdx(): Int {
        for (i in Global.profileList.indices) {
            if (Global.profileList[i].username == this.username) {
                return i
            }
        }
        return -1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDeployment(component: EventComponent) {
        this.deployments.add(component.getStart().toLocalDate().format(DATETIMEFORMATTER))
        this.deployments.sortBy { LocalDate.parse(it, DATETIMEFORMATTER) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeDeployment(component: EventComponent) {
        this.deployments.remove(component.getStart().toLocalDate().format(DATETIMEFORMATTER))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun overworkedness(start: LocalDate? = null, end: LocalDate? = null) : Double {
        var sum = 0.0
        var prev: LocalDate? = null
        for (component in this.getDeploymentList()) {
            if (
                (start == null || component.isAfter(start)) &&
                (end == null || component.isBefore(end))
            ) {
                if (prev != null) {
                    sum += DeploymentProposal.overworkedMetric(
                        ChronoUnit.DAYS.between(prev, component).toDouble()
                    )
                }
                prev = component
            }
        }
        return sum
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun overworkedness(eventComponent: EventComponent): Double {
        return overworkedness(
            eventComponent.getStart().toLocalDate().minusDays(14L),
            eventComponent.getStart().toLocalDate().plusDays(14L)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeploymentList() : ArrayList<LocalDate> {
        return ArrayList(this.deployments.map { LocalDate.parse(it, DATETIMEFORMATTER) }.sorted())
    }
}
