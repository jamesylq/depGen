package com.example.depgen.utils

import com.example.depgen.Global
import com.example.depgen.model.DeploymentRecord
import com.example.depgen.model.Profile
import java.time.LocalDate

var deploymentRecordMemo = HashMap<Profile, ArrayList<DeploymentRecord>>()

fun getDeployments(profile: Profile) : ArrayList<DeploymentRecord> {
    if (deploymentRecordMemo.containsKey(profile)) return deploymentRecordMemo[profile]!!
    deploymentRecordMemo[profile] = ArrayList()

    for (event in Global.eventList) {
        for (entry in event.getComponents()) {
            for (component in entry.value) {
                if (component.deployment.containsKey(profile)) {
                    deploymentRecordMemo[profile]!!.add(
                        DeploymentRecord(
                            component.deployment[profile]!!,
                            component,
                            event
                        )
                    )
                }
            }
        }
    }

    deploymentRecordMemo[profile]!!.sortBy { it.getDate() }
    return deploymentRecordMemo[profile]!!
}


fun ArrayList<DeploymentRecord>.getDate(date: LocalDate): ArrayList<DeploymentRecord> {
    return ArrayList(
        this.mapNotNull{
            if (it.getDate() == date) it else null
        }
    )
}
