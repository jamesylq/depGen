package com.example.depgen.model

import com.example.depgen.Global
import com.example.depgen.exceptions.PasswordValidationException
import com.example.depgen.utils.STANDARD_FORMATTER
import com.example.depgen.utils.encryptSHA256
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Serializable
data class Profile (
    var username: String,
    var password: String,
    var email: String,
    val skills: HashMap<Skill, Int> = HashMap(),
    var unavailable: HashSet<String> = HashSet(),
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

    fun isAvailable(date: LocalDate) : Boolean {
        return !unavailable.contains(date.toString())
    }

    fun addDeployment(component: EventComponent) {
        this.deployments.add(component.getStart().toLocalDate().format(STANDARD_FORMATTER))
        this.deployments.sortBy { LocalDate.parse(it, STANDARD_FORMATTER) }
    }

    fun removeDeployment(component: EventComponent) {
        this.deployments.remove(component.getStart().toLocalDate().format(STANDARD_FORMATTER))
    }

    fun overworkedness(start: LocalDate, end: LocalDate) : Double {
        var sum = 0.0
        var prev: LocalDate? = null
        for (component in this.getDeploymentList(start)) {
            if (component.isAfter(start) && component.isBefore(end)) {
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

    fun overworkedness(eventComponent: EventComponent): Double {
        return overworkedness(
            eventComponent.getStart().toLocalDate().minusDays(30L),
            eventComponent.getStart().toLocalDate().plusDays(30L)
        )
    }

    
    fun getDeploymentList(start: LocalDate? = null) : ArrayList<LocalDate> {
        val lst = ArrayList(this.deployments.map { LocalDate.parse(it, STANDARD_FORMATTER) })
        if (start != null) lst.add(start)
        return ArrayList(lst.sorted())
    }

    @Throws(PasswordValidationException::class)
    fun validate(pwd: String) : Boolean {
        if (this.password == pwd.encryptSHA256()) return true
        throw PasswordValidationException("")
    }
}
