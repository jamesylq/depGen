package com.example.depgen.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.depgen.Global
import com.example.depgen.model.DeploymentProposal
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile

@RequiresApi(Build.VERSION_CODES.O)
class OTDCompleteSearchHelper(private val eventComponent: EventComponent) {
    private val k = eventComponent.rolesRequired.size
    private val roles = eventComponent.rolesRequired.keys.toList()

    private fun propose(ind: Int, curr: HashMap<EventRole, List<Profile>>): Pair<Double, HashMap<EventRole, List<Profile>>> {
        if (ind == k) return Pair(DeploymentProposal(eventComponent, curr).penalty(), curr)

        val n = eventComponent.rolesRequired[roles[ind]]!!
        var ans = Pair(Double.MAX_VALUE, HashMap<EventRole, List<Profile>>())
        for (possibility in Global.profileList.combinations(n)) {
            val new = HashMap(curr)
            new[roles[ind]] = possibility
            val res = propose(ind + 1, new)
            if (ans.first > res.first) ans = res
        }
        return ans
    }

    fun generate() : HashMap<EventRole, List<Profile>> {
        return propose(0, HashMap()).second
    }
}

fun <T> List<T>.combinations(size: Int): List<List<T>> {
    if (size > this.size) return emptyList()

    return if (size == 0) {
        listOf(emptyList())
    } else {
        this.indices.flatMap { index ->
            this.subList(index + 1, this.size)
                .combinations(size - 1)
                .map { listOf(this[index]) + it }
        }
    }
}

