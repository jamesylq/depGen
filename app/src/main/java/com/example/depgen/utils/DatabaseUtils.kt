package com.example.depgen.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.depgen.ADMIN
import com.example.depgen.Global
import com.example.depgen.LOGGED_OUT
import com.example.depgen.ctxt
import com.example.depgen.json
import com.example.depgen.model.Wrapper
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeParseException


@RequiresApi(Build.VERSION_CODES.O)
fun save() {
    val dbRef = FirebaseDatabase
        .getInstance("https://depgen-a8040-default-rtdb.asia-southeast1.firebasedatabase.app")
        .reference

    dbRef.child("lastUpdate").setValue(LocalDateTime.now().toString())

    val wrapper = Wrapper(Global.profileList, Global.eventList, Global.skillsList, Global.rolesList)
    dbRef.child("save").setValue(json.encodeToString(wrapper))
        .addOnSuccessListener {
            Log.d("FirebaseIO", "Save File $wrapper saved to Firebase!")
        }
        .addOnFailureListener {
            Log.wtf("FirebaseIO", "Saving Failed: ", it)
        }

    saveLocally()
}

fun clear() {
    val dbRef = FirebaseDatabase
        .getInstance("https://depgen-a8040-default-rtdb.asia-southeast1.firebasedatabase.app")
        .reference

    dbRef.child("save").setValue("")
        .addOnSuccessListener {
            Log.d("FirebaseIO", "Firebase Save File cleared!")
        }
        .addOnFailureListener {
            Log.wtf("FirebaseIO", "Clearing Failed: ", it)
        }
}

@RequiresApi(Build.VERSION_CODES.O)
fun saveLocally() {
    Log.d("LocalFileIO", "Initiated Save ${Global.profileList}")
    val saveFile = File(ctxt.filesDir, "save.json")
    val wrapper = Wrapper(Global.profileList, Global.eventList, Global.skillsList, Global.rolesList)
    saveFile.writeText(json.encodeToString(wrapper))

    val timeFile = File(ctxt.filesDir, "lastUpdate.json")
    timeFile.writeText(json.encodeToString(LocalDateTime.now().toString()))
}

@RequiresApi(Build.VERSION_CODES.O)
fun load() {
    val dbRef = FirebaseDatabase
        .getInstance("https://depgen-a8040-default-rtdb.asia-southeast1.firebasedatabase.app")
        .reference

    var lastLocalUpdate = NO_DATE_OBJ
    var lastDBUpdate = NO_DATE_OBJ.plusDays(1L)

    try {
        val file = File(ctxt.filesDir, "lastUpdate.json")
        if (!file.exists()) throw RuntimeException()
        lastLocalUpdate = LocalDateTime.parse(json.decodeFromString<String>(file.readText()))

    } catch (e: DateTimeParseException) {
        Log.w("depGen", "Warning (Local Save Not Found): ", e)
    } catch (_: RuntimeException) { }

    dbRef.child("lastUpdate").get()
        .addOnSuccessListener {
            try {
                lastDBUpdate = LocalDateTime.parse(it.getValue(String::class.java)!!)
                Log.d("FirebaseIO", "Last Database Update: $lastDBUpdate")
            } catch (e: NullPointerException) {
                Log.w("depGen", "Warning (Database Save Time Not Found): ", e)
            }
        }
        .addOnCompleteListener {
            var retrieved = false
            if (!lastLocalUpdate.isBefore(lastDBUpdate)) {
                try {
                    val file = File(ctxt.filesDir, "save.json")
                    if (!file.exists()) {
                        file.createNewFile()
                        throw RuntimeException()
                    }

                    val wrapper: Wrapper = json.decodeFromString(file.readText())

                    Global.eventList.clear()
                    Global.profileList.clear()
                    Global.skillsList.clear()
                    Global.rolesList.clear()
                    Global.eventList.addAll(wrapper.events)
                    Global.profileList.addAll(wrapper.profiles)
                    Global.skillsList.addAll(wrapper.skills)
                    Global.rolesList.addAll(wrapper.roles)
                    LOGGED_OUT = Global.profileList[0]
                    ADMIN = Global.profileList[1]

                    for (i in 1 ..< Global.profileList.size) {
                        for (skill in Global.skillsList) {
                            if (!Global.profileList[i].skills.containsKey(skill)) {
                                Global.profileList[i].skills[skill] = skill.defaultLevel
                            }
                        }
                    }

                    retrieved = true

                } catch (e: RuntimeException) {
                    Log.wtf("depGen", "Critical Error With Local Save File, Falling Back on Firebase: ", e)
                }
            }

            if (!retrieved) {
                Log.d("FirebaseIO", "Local Save File outdated, retrieving Data from Firebase.")
                dbRef.child("save").get()
                    .addOnSuccessListener {
                        try {
                            val data = it.getValue(String::class.java)!!
                            val wrapper: Wrapper = json.decodeFromString(data)

                            Global.eventList.clear()
                            Global.profileList.clear()
                            Global.skillsList.clear()
                            Global.rolesList.clear()
                            Global.eventList.addAll(wrapper.events)
                            Global.profileList.addAll(wrapper.profiles)
                            Global.skillsList.addAll(wrapper.skills)
                            Global.rolesList.addAll(wrapper.roles)
                            LOGGED_OUT = Global.profileList[0]
                            ADMIN = Global.profileList[1]

                            for (i in 1..<Global.profileList.size) {
                                for (skill in Global.skillsList) {
                                    if (!Global.profileList[i].skills.containsKey(skill)) {
                                        Global.profileList[i].skills[skill] = skill.defaultLevel
                                    }
                                }
                            }
                            saveLocally()

                        } catch (_: RuntimeException) {
                            Log.d("depGen", "Save file not found on database. Data has been reset.")
                            Global.profileList.clear()
                            Global.eventList.clear()
                            Global.skillsList.clear()
                            Global.rolesList.clear()
                            Global.profileList.add(LOGGED_OUT)
                            Global.profileList.add(ADMIN)
                        }
                    }
                    .addOnFailureListener {
                        Log.wtf("FirebaseIO", "Loading Failed: ", it)
                    }

            } else {
                Log.d("LocalFileIO", "Operating on Local Save File. (DB: $lastDBUpdate, Local: $lastLocalUpdate)")
            }
        }
}
