package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.temporal.ChronoUnit
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.O)
class DeploymentProposal (
    private val proposals: List<Pair<Event, Map<EventRole, List<Profile>>>>,
) {
    private val deploymentByMember = hashMapOf<Profile, ArrayList<Pair<Pair<EventComponent, Event>, EventRole>>>()

    init {
        for (proposal in proposals) {
            for (eventEntry in proposal.first.getComponents()) {
                for (component in eventEntry.value) {
                    for (deploymentEntry in component.deployment) {
                        if (!deploymentByMember.containsKey(deploymentEntry.key)) {
                            deploymentByMember[deploymentEntry.key] = arrayListOf()
                        }
                        for (role in deploymentEntry.value) {
                            deploymentByMember[deploymentEntry.key]!!.add(
                                Pair(Pair(component, proposal.first), role)
                            )
                        }
                    }
                }
            }
        }
        for (entry in deploymentByMember) {
            entry.value.sortBy {
                it.first.first.getStart()
            }
        }
    }

    private fun overworkedIndex() : Double {
        var sum = 0.0
        for (entry in deploymentByMember) {
            var prev = entry.value[0].first.first.getStart().toLocalDate()
            for (i in 1 ..< entry.value.size) {
                val curr = entry.value[i].first.first.getStart().toLocalDate()
                sum += overworkedMetric(ChronoUnit.DAYS.between(prev, curr).toDouble())
                prev = curr
            }
        }
        return sum
    }

    private fun skillIndex() : Double {
        var sum = 0.0
        for (proposal in proposals) {
            for (eventEntry in proposal.first.getComponents()) {
                 for (component in eventEntry.value) {

                 }
            }
        }
        return sum
    }

    private fun overworkedMetric(d: Double): Double {
        return if (d >= 14) 0.0 else ((d - 14) / 14).pow(4)
    }
}
