package com.example.depgen.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.O)
class DeploymentProposal (
    private val eventComponent: EventComponent,
    private val proposal: Map<EventRole, List<Profile>>
) {
    private val deploymentByMember = hashMapOf<Profile, ArrayList<EventRole>>()

    init {
        for (entry in proposal) {
            for (member in entry.value) {
                if (!deploymentByMember.containsKey(member)) deploymentByMember[member] = ArrayList()
                deploymentByMember[member]!!.add(entry.key)
            }
        }
    }

    private fun overworkedIndex() : Double {
        return deploymentByMember.keys.toList().sumOf { it.overworkedness(eventComponent) } +
               deploymentByMember.values.toList().sumOf { multiRoleMetric(it.size) }
    }

    private fun skillIndex() : Double {
        var sum = 0.0
        for (roleEntry in proposal) {
            for (skillEntry in roleEntry.key.prerequisites) {
                for (condition in skillEntry.value) {
                    sum += deploymentByMember.keys.toList().sumOf {
                        if (!it.skills.containsKey(skillEntry.key)) {
                            it.skills[skillEntry.key] = skillEntry.key.defaultLevel
                        }
                        return@sumOf condition!!.penaltyOf(it.skills[skillEntry.key]!!)
                    }
                }
            }
        }
        return sum
    }

    fun penalty() : Double {
        return overworkedIndex() + skillIndex()
    }

    companion object {
        fun overworkedMetric(d: Double): Double {
            return if (d >= 14) 0.0 else ((14 - d) / 14).pow(4)
        }

        fun multiRoleMetric(d: Int): Double {
            if (d == 0) return 0.0
            return (d.toDouble().pow(d) - 1) / 10
        }
    }
}
