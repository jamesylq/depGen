package com.example.depgen.utils

import android.util.Log
import com.example.depgen.Global
import com.example.depgen.MAX_COMPLETE_CHECK_TIME_STATE_COUNT
import com.example.depgen.MAX_COMPLETE_SEARCH_DURATION
import com.example.depgen.exceptions.CompleteSearchInterruptedException
import com.example.depgen.exceptions.CompleteSearchTimeoutException
import com.example.depgen.model.DeploymentProposal
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Random


private var states = 0L
private var executionStart = -1L


class OTDCompleteSearchHelper(
    private val eventComponent: EventComponent
) {
    fun generate() : HashMap<EventRole, List<Profile>> {
        var currBest = Double.MAX_VALUE
        var currBestCurr: HashMap<EventRole, List<Profile>>? = null

        for (i in 1..10000) {
            var pt = 0
            val curr = HashMap<EventRole, List<Profile>>()
            val depList = Global.profileList.subList(2, Global.profileList.size).shuffled()
            for (entry in eventComponent.rolesRequired) {
                curr[entry.key] = ArrayList(depList.subList(pt, pt + entry.value))
                pt += entry.value
            }
            val penalty = DeploymentProposal(eventComponent, curr).penalty()
            if (penalty < currBest) {
                currBest = penalty
                currBestCurr = curr
            }
        }

        return currBestCurr!!
    }
}


class RDCompleteSearchHelper(
    private val dummyComponent: EventComponent,
    private val dates: List<LocalDate>
) {
    private val roles = dummyComponent.rolesRequired.keys.toList()

    fun generate(): ArrayList<EventComponent> {
        executionStart = System.currentTimeMillis()
        while (true) {
            try {
                val potential = attempt(
                    ArrayList(),
                    ArrayList(
                        Global.profileList.subList(2, Global.profileList.size).map {
                            Pair(NO_DATE_OBJ.toLocalDate(), it)
                        }
                    )
                )
                if (potential.isNotEmpty()) {
                    executionStart = -1
                    return potential
                }
            } catch (e: CompleteSearchTimeoutException) {
                Log.wtf("depGen", e.toString())
                break
            }
        }
        executionStart = -1
        return ArrayList()
    }

    private fun attempt(
        current: ArrayList<EventComponent>,
        profileData: ArrayList<Pair<LocalDate, Profile>>
    ): ArrayList<EventComponent> {
        if (++states % MAX_COMPLETE_CHECK_TIME_STATE_COUNT == 0L) {
            if (System.currentTimeMillis() - executionStart > MAX_COMPLETE_SEARCH_DURATION) {
                throw CompleteSearchTimeoutException("Execution Timed Out: $states States Evaluated (${(System.currentTimeMillis() - executionStart)}ms)")
            }
        }

        if (current.size == dates.size) return current
        val curGenerating = dates[current.size]

        val available = profileData.shuffledToDeployOn(
            date = curGenerating,
            filter = { profile, date -> profile.isAvailable(date) }
        )

        for (start in 0 .. minOf(available.size - 1, 4)) {
            val proposal = HashMap<Profile, ArrayList<EventRole>>()

            try {
                val newProfileData = ArrayList(profileData)

                for (role in roles) {
                    val roleAvail = newProfileData.shuffledToDeployOn(
                        date = curGenerating,
                        role = role,
                        filter = { profile, date -> profile.isAvailable(date) }
                    )
                    val rem = ArrayList(roleAvail.map{ it.second })

                    for (i in 0..< dummyComponent.rolesRequired[role]!!) {
                        var found = false
                        for (profile in rem) {
                            if (role.satisfiedBy(profile)) {
                                if (!proposal.containsKey(profile)) proposal[profile] = ArrayList()
                                proposal[profile]!!.add(role)
                                rem.remove(profile)
                                newProfileData.remove(profile)
                                newProfileData.add(Pair(curGenerating, profile))
                                found = true
                                break
                            }
                        }
                        if (!found) throw CompleteSearchInterruptedException("")
                    }
                }

                val new = ArrayList(current)
                new.add(
                    EventComponent(
                        proposal,
                        dummyComponent.rolesRequired,
                        curGenerating.atStartOfDay().toString(),
                        curGenerating.atTime(END_OF_DAY).toString()
                    )
                )
                val potential = attempt(new, newProfileData)
                if (potential.isNotEmpty()) return potential
                throw CompleteSearchInterruptedException("")

            } catch (e: CompleteSearchInterruptedException) {
                continue
            }
        }

        return ArrayList()
    }
}


fun List<Pair<LocalDate, Profile>>.shuffledToDeployOn(
    date: LocalDate? = null,
    role: EventRole? = null,
    filter: ((Profile, LocalDate) -> Boolean)? = null
): List<Pair<LocalDate, Profile>> {

    val penalties = ArrayList(
        this.map{
            Pair(0.5 * Random().nextGaussian(), it)
        }
    )

    if (date != null) {
        for (ind in this.indices) {
            penalties[ind] = Pair(
                penalties[ind].first + DeploymentProposal.overworkedMetric(
                    ChronoUnit.DAYS.between(this[ind].first, date).toDouble()
                ),
                penalties[ind].second
            )
        }
    }

    if (role != null) {
        for (ind in this.indices) {
            penalties[ind] = Pair(
                penalties[ind].first + DeploymentProposal.skillPenalty(this[ind].second, role),
                penalties[ind].second
            )
        }
    }

    penalties.sortBy { it.first }

    if (filter == null) return penalties.map { it.second }
    return penalties.mapNotNull { if (filter(it.second.second, date!!)) it.second else null }
}

fun <A, B> ArrayList<Pair<A, B>>.remove(arg: B): Boolean {
    for (item in this) {
        if (item.second == arg) {
            return this.remove(item)
        }
    }
    return false
}
