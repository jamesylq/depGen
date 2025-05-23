package com.example.depgen.model

import java.util.Random
import kotlin.math.exp
import kotlin.math.pow


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
        return deploymentByMember.keys.sumOf { it.overworkedness(eventComponent) } +
               deploymentByMember.values.sumOf { multiRoleMetric(it.size) }
    }

    private fun skillIndex() : Double {
        var sum = 0.0
        for (roleEntry in proposal) {
            for (profile in roleEntry.value) {
                sum += skillPenalty(profile, roleEntry.key)
            }
        }
        return sum
    }

    fun penalty() : Double {
        return overworkedIndex() + skillIndex() + 0.5 * Random().nextGaussian()
    }

    companion object {
        fun overworkedMetric(d: Double): Double {
            return 3 * (1.1 / (1.0 + exp(0.6 * (d - 18.0))).coerceIn(0.0, 1.0)
                    + 1 / (d + 0.1) - 5.0 / (1 + exp(-0.1 * (d - 70.0))))
        }

        fun multiRoleMetric(d: Int): Double {
            if (d == 0) return 0.0
            return (d.toDouble().pow(d) - 1)
        }

        fun skillPenalty(profile: Profile, role: EventRole): Double {
            var sum = 0.0
            for (skillEntry in role.prerequisites) {
                for (condition in skillEntry.value) {
                    if (!profile.skills.containsKey(skillEntry.key)) {
                        profile.skills[skillEntry.key] = skillEntry.key.defaultLevel
                    }
                    sum += condition!!.penaltyOf(profile.skills[skillEntry.key]!!)
                }
            }
            return sum
        }
    }
}
